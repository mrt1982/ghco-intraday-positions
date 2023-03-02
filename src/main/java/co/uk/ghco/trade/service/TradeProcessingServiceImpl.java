package co.uk.ghco.trade.service;

import co.uk.ghco.trade.model.CashAmount;
import co.uk.ghco.trade.model.Trade;
import co.uk.ghco.trade.model.TradeBook;
import co.uk.ghco.trade.repository.TradeDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class TradeProcessingServiceImpl implements TradeProcessingService {
    private final TradeDao tradeDao;

    @Override
    public void processTrade(Trade trade) {
        switch (trade.getActionType()) {
            case NEW -> processNewTrade(trade);
            case AMEND -> processAmendTrade(trade);
            case CANCEL -> processCancelTrade(trade);
            default -> {
            }
        }
    }

    @Override
    public TradeBook getTradeBook() {
        return tradeDao.getTradeBookGroupings();
    }

    private void processCancelTrade(Trade cancelTrade) {
        if (tradeDao.findTradeById(cancelTrade.getTradeId()).isPresent()) {
            tradeDao.removeTrade(cancelTrade);
            log.info("SUCCESS Cancel Trade. TradeId={}", cancelTrade.getTradeId());
        } else {
            log.info("SKIPPED Cancel Trade. TradeId={} was already cancelled", cancelTrade.getTradeId());
        }
    }

    private void processNewTrade(Trade trade) {
        tradeDao.addTrade(trade);
        log.info("SUCCESS Add Trade. TradeId={}", trade.getTradeId());
    }

    private void processAmendTrade(Trade amendedTrade) {
        Optional<Trade> existingTradeOpt = tradeDao.findTradeById(amendedTrade.getTradeId());
        if (existingTradeOpt.isPresent()) {
            BigDecimal priceDifference = amendedTrade.getPrice().getAmount().subtract(existingTradeOpt.get().getPrice().getAmount());
            Trade updatedTrade = amendedTrade.toBuilder().price(new CashAmount(amendedTrade.getPrice().getCurrency(), priceDifference.toString())).build();
            tradeDao.updateTrade(updatedTrade);
            log.info("SUCCESS Amend Trade. TradeId={}. Went from existing price={} to amended price={}",
                    existingTradeOpt.get().getTradeId(),
                    existingTradeOpt.get().getPrice().getAmount(),
                    amendedTrade.getPrice().getAmount());
        } else {
            log.info("SKIPPED Amend Trade. Cannot find trade with TradeId={}", amendedTrade.getTradeId());
        }
    }
}
