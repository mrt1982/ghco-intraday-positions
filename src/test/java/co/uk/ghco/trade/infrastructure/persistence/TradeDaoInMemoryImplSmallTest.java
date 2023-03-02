package co.uk.ghco.trade.infrastructure.persistence;

import co.uk.ghco.trade.model.ActionType;
import co.uk.ghco.trade.model.CashAmount;
import co.uk.ghco.trade.model.Trade;
import co.uk.ghco.trade.model.TradeBook;
import co.uk.ghco.trade.model.TradeBookEntry;
import co.uk.ghco.trade.repository.TradeDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsEmptyCollection.empty;

class TradeDaoInMemoryImplSmallTest {
    public static final String STRATEGY = "strat";
    public static final int TRADE_VOLUME = 12;
    public static final String ACCOUNT_ID = "accc";
    private TradeDao testObj;

    @BeforeEach
    void setUp() {
        testObj = new TradeDaoInMemoryImpl();
    }

    @Test
    void addTrade_trade_success() {
        CashAmount price = new CashAmount(Currency.getInstance("GBP"), "1892.8224430636");
        Trade actualTrade = createTrade("123", "bbg", price, "port1", "user1", ActionType.NEW, LocalDateTime.now(), LocalDate.now());

        testObj.addTrade(actualTrade);

        Optional<Trade> expectedTradeOpt = testObj.findTradeById(actualTrade.getTradeId());
        assertThat(true, is(expectedTradeOpt.isPresent()));
        assertThat(actualTrade, is(equalTo(expectedTradeOpt.get())));
    }

    @Test
    void findTradeById_tradeDoesNotExist_empty() {
        Optional<Trade> expectedTradeOpt = testObj.findTradeById("123");
        assertThat(true, is(expectedTradeOpt.isEmpty()));
    }

    @Test
    void updateTrade_updateExistingTrade_returnUpdatedTrade() {
        CashAmount price = new CashAmount(Currency.getInstance("GBP"), "1892.8224430636");
        Trade trade = createTrade("123", "bbg", price, "port1", "user1", ActionType.NEW, LocalDateTime.now(), LocalDate.now());

        testObj.addTrade(trade);
        Trade expectedUpdatedTrade = trade.toBuilder().price(new CashAmount(Currency.getInstance("GBP"), "1895.8224430636")).build();
        testObj.updateTrade(expectedUpdatedTrade);

        Optional<Trade> actualUpdatedTradeOpt = testObj.findTradeById("123");
        assertThat(actualUpdatedTradeOpt.get(), is(equalTo(expectedUpdatedTrade)));
    }

    @Test
    void removeTrade_removeExistingTrade_returnEmpty() {
        CashAmount price = new CashAmount(Currency.getInstance("GBP"), "1892.8224430636");
        Trade deletedTrade = createTrade("123", "bbg", price, "port1", "user1", ActionType.NEW, LocalDateTime.now(), LocalDate.now());

        testObj.addTrade(deletedTrade);
        testObj.removeTrade(deletedTrade);

        Optional<Trade> actualDeletedTradeOpt = testObj.findTradeById("123");
        assertThat(true, is(actualDeletedTradeOpt.isEmpty()));
    }

    @Test
    void getTradeBookGroupings_addMultipleNewTrades_aggregatedCashPositionsUpdated() {
        CashAmount tradePrice1 = new CashAmount(Currency.getInstance("GBP"), "1892.8224450636");
        CashAmount tradePrice2 = new CashAmount(Currency.getInstance("GBP"), "192.8224450636");
        CashAmount tradePrice3 = new CashAmount(Currency.getInstance("GBP"), "18992.8224630636");
        Trade trade1 = createTrade("123", "bbg", tradePrice1, "port1", "user1", ActionType.NEW, LocalDateTime.now(), LocalDate.now());
        Trade trade2 = createTrade("1234", "cbg", tradePrice2, "port2", "user1", ActionType.NEW, LocalDateTime.now(), LocalDate.now());
        Trade trade3 = createTrade("1235", "bbg", tradePrice3, "port1", "user1", ActionType.NEW, LocalDateTime.now(), LocalDate.now());

        testObj.addTrade(trade1);
        testObj.addTrade(trade2);
        testObj.addTrade(trade3);
        TradeBook tradeBook = testObj.getTradeBookGroupings();

        List<TradeBookEntry> tradeBookEntries = verifyAndReturnTradeBookEntries(tradeBook, 2);
        validateTradeBookEntry(tradeBookEntries.get(0), "bbg", "port1", STRATEGY, "user1", new CashAmount(Currency.getInstance("GBP"), "250627.68"));
        validateTradeBookEntry(tradeBookEntries.get(1), "cbg", "port2", STRATEGY, "user1", new CashAmount(Currency.getInstance("GBP"), "2313.84"));
    }

    @Test
    void getTradeBookGroupings_addMultipleNewTradesAndRemoveTrade_aggregatedCashPositionsUpdated() {
        CashAmount tradePrice1 = new CashAmount(Currency.getInstance("GBP"), "1892.8224450636");
        CashAmount tradePrice2 = new CashAmount(Currency.getInstance("GBP"), "192.8224450636");
        CashAmount tradePrice3 = new CashAmount(Currency.getInstance("GBP"), "18992.8224630636");
        Trade trade1 = createTrade("123", "bbg", tradePrice1, "port1", "user1", ActionType.NEW, LocalDateTime.now(), LocalDate.now());
        Trade trade2 = createTrade("1234", "cbg", tradePrice2, "port2", "user1", ActionType.NEW, LocalDateTime.now(), LocalDate.now());
        Trade trade3 = createTrade("1235", "bbg", tradePrice3, "port1", "user1", ActionType.NEW, LocalDateTime.now(), LocalDate.now());
        Trade cancelTrade3 = createTrade("1235", "bbg", tradePrice3, "port1", "user1", ActionType.CANCEL, LocalDateTime.now(), LocalDate.now());

        testObj.addTrade(trade1);
        testObj.addTrade(trade2);
        testObj.addTrade(trade3);
        TradeBook beforeRemoveTradeBook = testObj.getTradeBookGroupings();

        List<TradeBookEntry> beforeRemoveTradeBookEntries = verifyAndReturnTradeBookEntries(beforeRemoveTradeBook, 2);
        validateTradeBookEntry(beforeRemoveTradeBookEntries.get(0), "bbg", "port1", STRATEGY, "user1", new CashAmount(Currency.getInstance("GBP"), "250627.68"));
        validateTradeBookEntry(beforeRemoveTradeBookEntries.get(1), "cbg", "port2", STRATEGY, "user1", new CashAmount(Currency.getInstance("GBP"), "2313.84"));

        testObj.removeTrade(cancelTrade3);
        TradeBook afterRemoveTradeBook = testObj.getTradeBookGroupings();

        List<TradeBookEntry> afterRemoveTradeBookEntries = verifyAndReturnTradeBookEntries(afterRemoveTradeBook, 2);
        validateTradeBookEntry(afterRemoveTradeBookEntries.get(0), "bbg", "port1", STRATEGY, "user1", new CashAmount(Currency.getInstance("GBP"), "22713.84"));
        validateTradeBookEntry(afterRemoveTradeBookEntries.get(1), "cbg", "port2", STRATEGY, "user1", new CashAmount(Currency.getInstance("GBP"), "2313.84"));
    }

    @Test
    void getTradeBookGroupings_addMultipleNewTradesAndAmendATradeWithIncreaseAmount_aggregatedCashPositionsUpdated() {
        CashAmount tradePrice1 = new CashAmount(Currency.getInstance("GBP"), "1892.8224450636");
        CashAmount tradePrice2 = new CashAmount(Currency.getInstance("GBP"), "192.8224450636");
        CashAmount tradePrice3 = new CashAmount(Currency.getInstance("GBP"), "18992.8224630636");
        CashAmount amendTradePrice3 = new CashAmount(Currency.getInstance("GBP"), "999.90224630636");
        CashAmount increaseAmendCashAmount = new CashAmount(Currency.getInstance("GBP"), "262626.48");
        Trade trade1 = createTrade("123", "bbg", tradePrice1, "port1", "user1", ActionType.NEW, LocalDateTime.now(), LocalDate.now());
        Trade trade2 = createTrade("1234", "cbg", tradePrice2, "port2", "user1", ActionType.NEW, LocalDateTime.now(), LocalDate.now());
        Trade trade3 = createTrade("1235", "bbg", tradePrice3, "port1", "user1", ActionType.NEW, LocalDateTime.now(), LocalDate.now());
        Trade amendTrade3 = createTrade("1235", "bbg", amendTradePrice3, "port1", "user1", ActionType.AMEND, LocalDateTime.now(), LocalDate.now());

        testObj.addTrade(trade1);
        testObj.addTrade(trade2);
        testObj.addTrade(trade3);
        TradeBook beforeAmendTradeBook = testObj.getTradeBookGroupings();

        List<TradeBookEntry> beforeAmendTradeBookEntries = verifyAndReturnTradeBookEntries(beforeAmendTradeBook, 2);
        validateTradeBookEntry(beforeAmendTradeBookEntries.get(0), "bbg", "port1", STRATEGY, "user1", new CashAmount(Currency.getInstance("GBP"), "250627.68"));
        validateTradeBookEntry(beforeAmendTradeBookEntries.get(1), "cbg", "port2", STRATEGY, "user1", new CashAmount(Currency.getInstance("GBP"), "2313.84"));

        testObj.updateTrade(amendTrade3);
        TradeBook afterAmendTradeBook = testObj.getTradeBookGroupings();

        List<TradeBookEntry> afterAmendTradeBookEntries = verifyAndReturnTradeBookEntries(afterAmendTradeBook, 2);
        validateTradeBookEntry(afterAmendTradeBookEntries.get(0), "bbg", "port1", STRATEGY, "user1", increaseAmendCashAmount);
        validateTradeBookEntry(afterAmendTradeBookEntries.get(1), "cbg", "port2", STRATEGY, "user1", new CashAmount(Currency.getInstance("GBP"), "2313.84"));
    }

    @Test
    void getTradeBookGroupings_addMultipleNewTradesAndAmendATradeWithDecreaseAmount_aggregatedCashPositionsUpdated() {
        CashAmount tradePrice1 = new CashAmount(Currency.getInstance("GBP"), "1892.8224450636");
        CashAmount tradePrice2 = new CashAmount(Currency.getInstance("GBP"), "192.8224450636");
        CashAmount tradePrice3 = new CashAmount(Currency.getInstance("GBP"), "18992.8224630636");
        CashAmount amendTradePrice3 = new CashAmount(Currency.getInstance("GBP"), "-999.90224630636");
        CashAmount decreaseAmendCashAmount = new CashAmount(Currency.getInstance("GBP"), "238628.88");
        Trade trade1 = createTrade("123", "bbg", tradePrice1, "port1", "user1", ActionType.NEW, LocalDateTime.now(), LocalDate.now());
        Trade trade2 = createTrade("1234", "cbg", tradePrice2, "port2", "user1", ActionType.NEW, LocalDateTime.now(), LocalDate.now());
        Trade trade3 = createTrade("1235", "bbg", tradePrice3, "port1", "user1", ActionType.NEW, LocalDateTime.now(), LocalDate.now());
        Trade amendTrade3 = createTrade("1235", "bbg", amendTradePrice3, "port1", "user1", ActionType.AMEND, LocalDateTime.now(), LocalDate.now());

        testObj.addTrade(trade1);
        testObj.addTrade(trade2);
        testObj.addTrade(trade3);
        TradeBook beforeAmendTradeBook = testObj.getTradeBookGroupings();

        List<TradeBookEntry> beforeAmendTradeBookEntries = verifyAndReturnTradeBookEntries(beforeAmendTradeBook, 2);
        validateTradeBookEntry(beforeAmendTradeBookEntries.get(0), "bbg", "port1", STRATEGY, "user1", new CashAmount(Currency.getInstance("GBP"), "250627.68"));
        validateTradeBookEntry(beforeAmendTradeBookEntries.get(1), "cbg", "port2", STRATEGY, "user1", new CashAmount(Currency.getInstance("GBP"), "2313.84"));

        testObj.updateTrade(amendTrade3);
        TradeBook afterAmendTradeBook = testObj.getTradeBookGroupings();

        List<TradeBookEntry> afterAmendTradeBookEntries = verifyAndReturnTradeBookEntries(afterAmendTradeBook, 2);
        validateTradeBookEntry(afterAmendTradeBookEntries.get(0), "bbg", "port1", STRATEGY, "user1", decreaseAmendCashAmount);
        validateTradeBookEntry(afterAmendTradeBookEntries.get(1), "cbg", "port2", STRATEGY, "user1", new CashAmount(Currency.getInstance("GBP"), "2313.84"));
    }

    private List<TradeBookEntry> verifyAndReturnTradeBookEntries(TradeBook tradeBook, int expectedSize) {
        List<TradeBookEntry> tradeBookEntries = tradeBook.getTradeBookEntries();
        assertThat(tradeBookEntries, not(empty()));
        assertThat(expectedSize, is(equalTo(tradeBookEntries.size())));
        sortTradeBookEntriesByBbgCode(tradeBookEntries);
        return tradeBookEntries;
    }

    private void validateTradeBookEntry(TradeBookEntry tradeBookEntry, String bbgCode, String portofolio, String strategy, String user, CashAmount pnlPosition) {
        assertThat(tradeBookEntry, allOf(
                hasProperty("bbgCode", is(equalTo(bbgCode))),
                hasProperty("portfolio", is(equalTo(portofolio))),
                hasProperty("strategy", is(equalTo(strategy))),
                hasProperty("user", is(equalTo(user))),
                hasProperty("pnlPosition", is(equalTo(pnlPosition)))));
    }

    private void sortTradeBookEntriesByBbgCode(List<TradeBookEntry> tradeBookEntries) {
        Collections.sort(tradeBookEntries, Comparator.comparing(TradeBookEntry::getBbgCode));
    }

    private Trade createTrade(String tradeId, String bbgCode, CashAmount price, String portofolio, String user, ActionType actionType, LocalDateTime tradeTime, LocalDate valueDate) {
        return new Trade(tradeId, bbgCode, price, TRADE_VOLUME, portofolio,
                actionType, ACCOUNT_ID, STRATEGY, user, tradeTime, valueDate);
    }
}