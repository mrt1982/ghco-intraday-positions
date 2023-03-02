package co.uk.ghco.trade.infrastructure.persistence;

import co.uk.ghco.trade.model.CashAmount;
import co.uk.ghco.trade.model.Trade;
import co.uk.ghco.trade.model.TradeBook;
import co.uk.ghco.trade.model.TradeBookEntry;
import co.uk.ghco.trade.repository.TradeDao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TradeDaoInMemoryImpl implements TradeDao {
    private final Map<String, Map<String, Map<String, Map<String, CashAmount>>>> positionAggregates = new HashMap<>();
    private final Map<String, Trade> processedTrades = new LinkedHashMap<>();

    @Override
    public void addTrade(Trade trade) {
        processTradeCashPosition(trade);
        processedTrades.put(trade.getTradeId(), trade);
    }

    @Override
    public Optional<Trade> findTradeById(String tradeId) {
        return Optional.ofNullable(processedTrades.get(tradeId));
    }

    @Override
    public void updateTrade(Trade trade) {
        processTradeCashPosition(trade);
        processedTrades.put(trade.getTradeId(), trade);
    }

    @Override
    public void removeTrade(Trade trade) {
        removeTradeCashPosition(trade);
        processedTrades.remove(trade.getTradeId());
    }

    @Override
    public TradeBook getTradeBookGroupings() {
        List<TradeBookEntry> tradeBookEntries = new ArrayList<>();
        for (Map.Entry<String, Map<String, Map<String, Map<String, CashAmount>>>> perBbgCode : positionAggregates.entrySet()) {
            String bbgCode = perBbgCode.getKey();
            for (Map.Entry<String, Map<String, Map<String, CashAmount>>> perPortfolio : perBbgCode.getValue().entrySet()) {
                String portfolio = perPortfolio.getKey();
                for (Map.Entry<String, Map<String, CashAmount>> perStrategy : perPortfolio.getValue().entrySet()) {
                    String strategy = perStrategy.getKey();
                    for (Map.Entry<String, CashAmount> perUser : perStrategy.getValue().entrySet()) {
                        String user = perUser.getKey();
                        CashAmount pnlPosition = perUser.getValue();
                        TradeBookEntry tradeBookEntry = createTradeBookEntry(bbgCode, portfolio, strategy, user, pnlPosition);
                        tradeBookEntries.add(tradeBookEntry);
                    }
                }
            }
        }
        return new TradeBook(tradeBookEntries);
    }

    private void processTradeCashPosition(Trade trade) {
        CashAmount cashPosition = trade.calculateCashPosition();
        updatePositionAggregates(trade.getBbgCode(), trade.getPortfolio(), trade.getStrategy(), trade.getUserId(), cashPosition);
    }

    private void removeTradeCashPosition(Trade trade) {
        BigDecimal negateCashPosition = trade.calculateCashPosition().getAmount().negate();
        CashAmount cashAmount = new CashAmount(trade.calculateCashPosition().getCurrency(), negateCashPosition.toString());
        updatePositionAggregates(trade.getBbgCode(), trade.getPortfolio(), trade.getStrategy(), trade.getUserId(), cashAmount);
    }

    private void updatePositionAggregates(String bbgCode, String portfolio, String strategy, String user, CashAmount cashPosition) {
        positionAggregates.merge(bbgCode, new HashMap<>(), (m1, m2) -> m1);
        positionAggregates.get(bbgCode).merge(portfolio, new HashMap<>(), (m1, m2) -> m1);
        positionAggregates.get(bbgCode).get(portfolio).merge(strategy, new HashMap<>(), (m1, m2) -> m1);
        positionAggregates.get(bbgCode).get(portfolio).get(strategy).merge(user, cashPosition, (soFarCashPosition, newCashPosition) -> soFarCashPosition.updateCashAmount(newCashPosition.getAmount()));
    }

    private TradeBookEntry createTradeBookEntry(String bbgCode, String portfolio, String strategy, String user, CashAmount pnlPosition) {
        return new TradeBookEntry(bbgCode, portfolio, strategy, user, pnlPosition);
    }
}
