package co.uk.ghco.trade.service;

import co.uk.ghco.trade.model.ActionType;
import co.uk.ghco.trade.model.CashAmount;
import co.uk.ghco.trade.model.Trade;
import co.uk.ghco.trade.repository.TradeDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeProcessingServiceImplSmallTest {
    private TradeProcessingService testObj;
    @Mock
    private TradeDao tradeDaoMock;

    @BeforeEach
    void setUp() {
        testObj = new TradeProcessingServiceImpl(tradeDaoMock);
    }

    @Test
    void processTrade_tradeWithNewActionType_callAddTradeDaoSuccess() {
        CashAmount price = new CashAmount(Currency.getInstance("GBP"), "1892.8224430636");
        Trade trade = createTrade("123", price, ActionType.NEW, LocalDateTime.now(), LocalDate.now());

        testObj.processTrade(trade);

        verify(tradeDaoMock).addTrade(any());
        verify(tradeDaoMock, never()).findTradeById(any());
    }

    @Test
    void processTrade_tradeWithAmendActionTypeAndIncreaseForTradePrice_getTradeAndAmendExistingTrade() {
        CashAmount existingPrice = new CashAmount(Currency.getInstance("GBP"), "1892.8224430636");
        CashAmount amendedPrice = new CashAmount(Currency.getInstance("GBP"), "1992.8224430636");
        CashAmount increasePriceDifference = new CashAmount(Currency.getInstance("GBP"), "100.00");
        LocalDateTime tradeTime = LocalDateTime.now();
        LocalDate valueDate = LocalDate.now();
        Trade existingTrade = createTrade("123", existingPrice, ActionType.NEW, tradeTime, valueDate);
        Trade amendedTrade = createTrade("123", amendedPrice, ActionType.AMEND, tradeTime, valueDate);
        Trade expectedUpdatedTrade = createTrade("123", increasePriceDifference, ActionType.AMEND, tradeTime, valueDate);
        when(tradeDaoMock.findTradeById("123")).thenReturn(Optional.of(existingTrade));

        testObj.processTrade(amendedTrade);

        InOrder inOrder = inOrder(tradeDaoMock);
        inOrder.verify(tradeDaoMock, times(1)).findTradeById("123");
        inOrder.verify(tradeDaoMock, times(1)).updateTrade(expectedUpdatedTrade);
        inOrder.verify(tradeDaoMock, never()).addTrade(any());
    }

    @Test
    void processTrade_tradeWithAmendActionTypeAndDecreaseForTradePrice_getTradeAndAmendExistingTrade() {
        CashAmount existingPrice = new CashAmount(Currency.getInstance("GBP"), "1892.8224430636");
        CashAmount amendedPrice = new CashAmount(Currency.getInstance("GBP"), "1792.8224430636");
        CashAmount decreasePriceDifference = new CashAmount(Currency.getInstance("GBP"), "-100.00");
        LocalDateTime tradeTime = LocalDateTime.now();
        LocalDate valueDate = LocalDate.now();
        Trade existingTrade = createTrade("123", existingPrice, ActionType.NEW, tradeTime, valueDate);
        Trade amendedTrade = createTrade("123", amendedPrice, ActionType.AMEND, tradeTime, valueDate);
        Trade updatedTrade = createTrade("123", decreasePriceDifference, ActionType.AMEND, tradeTime, valueDate);
        when(tradeDaoMock.findTradeById("123")).thenReturn(Optional.of(existingTrade));

        testObj.processTrade(amendedTrade);

        InOrder inOrder = inOrder(tradeDaoMock);
        inOrder.verify(tradeDaoMock, times(1)).findTradeById("123");
        inOrder.verify(tradeDaoMock, times(1)).updateTrade(updatedTrade);
        inOrder.verify(tradeDaoMock, never()).addTrade(any());
    }

    @Test
    void processTrade_tradeWithAmendActionTypeAndExistingTradeIsNotPresent_doesNotAmendTrade() {
        CashAmount amendedPrice = new CashAmount(Currency.getInstance("GBP"), "1792.8224430636");
        Trade amendedTrade = createTrade("123", amendedPrice, ActionType.AMEND, LocalDateTime.now(), LocalDate.now());
        when(tradeDaoMock.findTradeById("123")).thenReturn(Optional.empty());

        testObj.processTrade(amendedTrade);

        InOrder inOrder = inOrder(tradeDaoMock);
        inOrder.verify(tradeDaoMock, times(1)).findTradeById("123");
        inOrder.verify(tradeDaoMock, never()).updateTrade(any());
        inOrder.verify(tradeDaoMock, never()).addTrade(any());
    }

    @Test
    void processTrade_tradeWithCancelActionTypeAndExistingTradeIsPresent_removeTrade() {
        CashAmount price = new CashAmount(Currency.getInstance("GBP"), "1892.8224430636");
        Trade existingTrade = createTrade("123", price, ActionType.AMEND, LocalDateTime.now(), LocalDate.now());
        Trade cancelTrade = createTrade("123", price, ActionType.CANCEL, LocalDateTime.now(), LocalDate.now());
        when(tradeDaoMock.findTradeById("123")).thenReturn(Optional.of(existingTrade));

        testObj.processTrade(cancelTrade);

        verify(tradeDaoMock, never()).addTrade(any());
        verify(tradeDaoMock, never()).updateTrade(any());
        verify(tradeDaoMock, times(1)).findTradeById("123");
        verify(tradeDaoMock, times(1)).removeTrade(cancelTrade);
    }

    @Test
    void processTrade_tradeWithCancelActionTypeAndExistingTradeIsNotPresent_doesNotRemoveTrade() {
        CashAmount price = new CashAmount(Currency.getInstance("GBP"), "1892.8224430636");
        Trade cancelTrade = createTrade("123", price, ActionType.CANCEL, LocalDateTime.now(), LocalDate.now());
        when(tradeDaoMock.findTradeById("123")).thenReturn(Optional.empty());

        testObj.processTrade(cancelTrade);

        verify(tradeDaoMock, never()).addTrade(any());
        verify(tradeDaoMock, never()).updateTrade(any());
        verify(tradeDaoMock, times(1)).findTradeById("123");
        verify(tradeDaoMock, never()).removeTrade(cancelTrade);
    }

    private Trade createTrade(String tradeId, CashAmount price, ActionType actionType, LocalDateTime tradeTime, LocalDate valueDate) {
        return new Trade(tradeId, "bbg", price, 12, "port",
                actionType, "accc", "strat", "123", tradeTime, valueDate);
    }
}