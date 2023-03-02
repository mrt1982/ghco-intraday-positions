package co.uk.ghco.trade.repository;

import co.uk.ghco.trade.model.Trade;
import co.uk.ghco.trade.model.TradeBook;

import java.util.Optional;

public interface TradeDao {
    void addTrade(Trade trade);

    Optional<Trade> findTradeById(String tradeId);

    void updateTrade(Trade trade);

    void removeTrade(Trade trade);

    TradeBook getTradeBookGroupings();
}
