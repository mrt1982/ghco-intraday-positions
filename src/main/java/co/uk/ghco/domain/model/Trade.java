package co.uk.ghco.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class Trade {
    private final String bbgCode;
    private final String portfolio;
    private final String strategy;
    private final String user;
    private final String tradeId;
    private final Double price;
    private final int volume;
}
