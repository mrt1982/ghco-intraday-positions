package co.uk.ghco.trade.application;

import co.uk.ghco.trade.model.ActionType;
import co.uk.ghco.trade.model.CashAmount;
import co.uk.ghco.trade.model.Trade;
import co.uk.ghco.trade.service.TradeProcessingService;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CsvReader {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final TradeProcessingService tradeProcessingService;

    public void loadTradesFromCsvFile(String fileName) throws IOException, CsvException {
        InputStream inputStream = TradeLauncher.class.getResourceAsStream(fileName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            CSVReaderBuilder csvReaderBuilder = new CSVReaderBuilder(reader)
                    .withSkipLines(1);
            CSVReader csvReader = csvReaderBuilder.build();
            List<String[]> lines = csvReader.readAll();
            for (String[] line : lines) {
                String tradeId = line[0];
                String bbgCode = line[1];
                String currencyIsoCode = line[2];
                String price = line[4];
                int volume = Integer.parseInt(line[5]);
                String portfolio = line[6];
                String user = line[10];
                ActionType actionType = ActionType.valueOf(line[7]);
                String accountId = line[8];
                String strategy = line[9];
                LocalDateTime tradeTime = LocalDateTime.parse(line[11], DATE_TIME_FORMATTER);
                LocalDate valueDate = LocalDate.parse(line[12], DATE_FORMATTER);
                Trade trade = new Trade(tradeId, bbgCode, new CashAmount(Currency.getInstance(currencyIsoCode), price),
                        volume, portfolio, actionType, accountId, strategy, user, tradeTime, valueDate);
                tradeProcessingService.processTrade(trade);
            }
        }
    }
}
