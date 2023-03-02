package co.uk.ghco.trade.application;

import co.uk.ghco.trade.infrastructure.persistence.TradeDaoInMemoryImpl;
import co.uk.ghco.trade.model.ActionType;
import co.uk.ghco.trade.model.CashAmount;
import co.uk.ghco.trade.model.Trade;
import co.uk.ghco.trade.model.TradeBook;
import co.uk.ghco.trade.model.TradeBookEntry;
import co.uk.ghco.trade.repository.TradeDao;
import co.uk.ghco.trade.service.TradeProcessingService;
import co.uk.ghco.trade.service.TradeProcessingServiceImpl;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Currency;
import java.util.Scanner;

@Slf4j
public class TradeLauncher {
    public static void main(String[] args) throws IOException, CsvException {
        TradeDao tradeDao = new TradeDaoInMemoryImpl();
        TradeProcessingService tradeProcessingService = new TradeProcessingServiceImpl(tradeDao);
        CsvReader csvReader = new CsvReader(tradeProcessingService);
        LocalDateTime start = LocalDateTime.now();
        log.info("Start loading of trades. start={}", start);
        csvReader.loadTradesFromCsvFile("/sample_trades.csv");
        LocalDateTime end = LocalDateTime.now();
        long elapsedSeconds = ChronoUnit.MILLIS.between(start, end);
        log.info("Finish loading of trades. Finish ={} elapsed time in milliseconds={}", end, elapsedSeconds);
        boolean exitProgram = false;
        Scanner console = new Scanner(System.in);
        while (!exitProgram) {
            exitProgram = options(console, tradeProcessingService);
        }

    }

    private static boolean options(Scanner console, TradeProcessingService tradeProcessingService) {
        System.out.println("Do you want to exit? please enter 1");
        System.out.println("Do you want to process a trade? please enter 2");
        System.out.println("Do you want to view the trade book? please enter 3");
        int optionChoice = console.nextInt();
        if (optionChoice != 1) {
            if (optionChoice == 2) {
                processTrade(tradeProcessingService);
            }
            if (optionChoice == 3) {
                displayTradeBook(tradeProcessingService);
            }
        }
        return optionChoice == 1;
    }

    private static void displayTradeBook(TradeProcessingService tradeProcessingService) {
        StringBuilder pnlCashAggregationDisplay = new StringBuilder();
        pnlCashAggregationDisplay.append("#### Trade Book ####").append("\n");
        String header = String.format("%-20s %-12s %-12s %-12s %-15s %-8s", "BBGCode", "Portfolio", "Strategy", "User", "pnl", "Currency");
        pnlCashAggregationDisplay.append(header).append("\n");
        pnlCashAggregationDisplay.append("=".repeat(header.length())).append("\n");
        TradeBook tradeBook = tradeProcessingService.getTradeBook();
        for (TradeBookEntry tradeBookEntry : tradeBook.getTradeBookEntries()) {
            pnlCashAggregationDisplay.append(String.format("%-20s %-12s %-12s %-12s %.2f %-8s",
                    tradeBookEntry.getBbgCode(), tradeBookEntry.getPortfolio(), tradeBookEntry.getStrategy(),
                    tradeBookEntry.getUser(), tradeBookEntry.getPnlPosition().getAmount(),
                    tradeBookEntry.getPnlPosition().getCurrency().getCurrencyCode())).append("\n");
        }
        System.out.println(pnlCashAggregationDisplay);
    }

    private static void processTrade(TradeProcessingService tradeProcessingService) {
        Scanner console = new Scanner(System.in);
        System.out.println("Please enter the tradeId ");
        String tradeId = console.nextLine();
        System.out.println("TradeId = " + tradeId);
        System.out.println("Please enter the bbgCode ");
        String bbgCode = console.nextLine();
        System.out.println("bbgCode = " + bbgCode);
        System.out.println("Please enter the currencyIsoCode ");
        Currency currency = Currency.getInstance(console.nextLine());
        System.out.println("Currency = " + currency.getCurrencyCode());
        System.out.println("Please enter the price ");
        String price = console.nextLine();
        System.out.println("Price = " + price);
        System.out.println("Please enter the volume ");
        int volume = console.nextInt();
        System.out.println("Volume = " + volume);
        System.out.println("Please enter the portfolio ");
        String portfolio = console.nextLine();
        System.out.println("portfolio = " + portfolio);
        System.out.println("Please enter the userId ");
        String userId = console.nextLine();
        System.out.println("userId = " + userId);
        System.out.println("Please enter the actionType. Supported action types are NEW, AMEND, CANCEL ");
        ActionType actionType = ActionType.valueOf(console.nextLine());
        System.out.println("actionType = " + actionType.name());
        System.out.println("Please enter the accountId ");
        String accountId = console.nextLine();
        System.out.println("accountId = " + accountId);
        System.out.println("Please enter the strategy ");
        String strategy = console.nextLine();
        System.out.println("strategy = " + strategy);
        Trade trade = new Trade(tradeId, bbgCode, new CashAmount(currency, price), volume, portfolio, actionType, accountId, strategy, userId, LocalDateTime.now(), LocalDate.now());
        tradeProcessingService.processTrade(trade);
    }

}

