package co.uk.ghco.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EqualsAndHashCode
@Getter
public class AggregatedCashPositions {
    private final Map<String, Double> perBbgCodeCashPosition;
    private final Map<String, Double> perPortfolioCashPosition;
    private final Map<String, Double> perStrategyCashPosition;
    private final Map<String, Double> perUserCashPosition;

    public AggregatedCashPositions() {
        perBbgCodeCashPosition = new ConcurrentHashMap<>();
        perPortfolioCashPosition = new ConcurrentHashMap<>();
        perStrategyCashPosition = new ConcurrentHashMap<>();
        perUserCashPosition = new ConcurrentHashMap<>();
    }

    public void addPerBbgCodeCashPosition(String bbgCode, Double cashPosition) {
        perBbgCodeCashPosition.merge(bbgCode, cashPosition, Double::sum);
    }

    public void addPerPortfolioCashPosition(String portfolio, Double cashPosition) {
        perPortfolioCashPosition.merge(portfolio, cashPosition, Double::sum);
    }

    public void addPerStrategyCashPosition(String strategy, Double cashPosition) {
        perStrategyCashPosition.merge(strategy, cashPosition, Double::sum);
    }

    public void addPerUserCashPosition(String user, Double cashPosition) {
        perUserCashPosition.merge(user, cashPosition, Double::sum);
    }
}
