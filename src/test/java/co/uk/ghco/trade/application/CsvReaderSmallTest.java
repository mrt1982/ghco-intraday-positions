package co.uk.ghco.trade.application;

import co.uk.ghco.trade.service.TradeProcessingService;
import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CsvReaderSmallTest {
    @Mock
    private TradeProcessingService tradeProcessingServiceMock;

    @Test
    void loadTradesFromCsvFile_testCsvFile_success() throws IOException, CsvException {
        CsvReader testObj = new CsvReader(tradeProcessingServiceMock);
        testObj.loadTradesFromCsvFile("/test-sample-trades.csv");
        verify(tradeProcessingServiceMock, times(2)).processTrade(any());
    }
}