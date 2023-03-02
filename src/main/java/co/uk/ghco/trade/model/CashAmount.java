package co.uk.ghco.trade.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

@EqualsAndHashCode
@Getter
public class CashAmount {
    private final Currency currency;
    private final BigDecimal amount;

    public CashAmount(Currency currency, String cashAmountValue) {
        this.currency = currency;
        this.amount = new BigDecimal(cashAmountValue).setScale(2, RoundingMode.HALF_UP);
    }

    public CashAmount updateCashAmount(BigDecimal addedAmount) {
        BigDecimal updatedCashAmount = amount.add(addedAmount);
        return new CashAmount(this.currency, updatedCashAmount.toString());
    }
}
