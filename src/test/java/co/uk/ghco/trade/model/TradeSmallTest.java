package co.uk.ghco.trade.model;

import org.junit.jupiter.api.Test;

import java.util.Currency;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class TradeSmallTest {

    @Test
    void calculateCashPosition_multiplyVolumeByPrice_success() {
        Trade testObj = new Trade(null, null, new CashAmount(Currency.getInstance("GBP"), "1892.8224430636"),
                223698, "port", ActionType.NEW, "123", "stra", "user", null, null);
        CashAmount expectedCashPosition = new CashAmount(Currency.getInstance("GBP"), "423420048.36");

        assertThat(testObj.calculateCashPosition(), is(equalTo(expectedCashPosition)));
    }
}