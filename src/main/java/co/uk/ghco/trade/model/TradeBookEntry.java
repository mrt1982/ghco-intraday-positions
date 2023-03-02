package co.uk.ghco.trade.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class TradeBookEntry {
    private final String bbgCode;
    private final String portfolio;
    private final String strategy;
    private final String user;
    private final CashAmount pnlPosition;
}
