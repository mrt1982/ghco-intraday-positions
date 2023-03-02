package co.uk.ghco.trade.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class TradeBook {
    private final List<TradeBookEntry> tradeBookEntries;
}
