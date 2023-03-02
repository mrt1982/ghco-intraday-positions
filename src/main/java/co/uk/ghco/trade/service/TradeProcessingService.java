package co.uk.ghco.trade.service;

import co.uk.ghco.trade.model.Trade;
import co.uk.ghco.trade.model.TradeBook;

public interface TradeProcessingService {
    void processTrade(Trade trade);

    TradeBook getTradeBook();
}
