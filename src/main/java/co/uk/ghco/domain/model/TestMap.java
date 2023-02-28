package co.uk.ghco.domain.model;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
public class TestMap {
    private final List<Trade> trades = List.of(
            new Trade("BRK.A US Equity", "portfolio5", "Strategy6", "User5", "f033fcaf0f164f99886a6bd6249a3b6a", 1892.25, 223),
            new Trade("GOOG US Equity", "portfolio1", "Strategy1", "User2", "7002f15e8f234a5ca278b21157d15372", 1894.25, 225),
            new Trade("BC94 JPY Equity", "portfolio2", "Strategy6", "User4", "7002f15e8f234a5ca278b21157d15372", 184.25, 223),
            new Trade("BC94 JPY Equity", "portfolio2", "Strategy3", "User2", "0b875bfe83644e2882e9a2c528ddc529", 181.25, 22),
            new Trade("BC94 JPY Equity", "portfolio2", "Strategy3", "User2", "0b875bfe83644e2882e9a2c528ddc523", 179.25, 25)
//            new Trade("BRK.A US Equity", "portfolio5", "Strategy6", "User5", "f033fcaf0f164f99886a6bd6249a3b6b", 1892.25, 223),
//            new Trade("GOOG US Equity", "portfolio1", "Strategy1", "User2", "7002f15e8f234a5ca278b21157d15373", 1894.25, 225),
//            new Trade("BC94 JPY Equity", "portfolio2", "Strategy2", "User2", "7002f15e8f234a5ca278b21157d15373", 184.25, 223),
//            new Trade("BC94 JPY Equity", "portfolio2", "Strategy2", "User2", "0b875bfe83644e2882e9a2c528ddc530", 181.25, 22),
//            new Trade("BC94 JPY Equity", "portfolio2", "Strategy3", "User4", "0b875bfe83644e2882e9a2c528ddc524", 179.25, 25),
//            new Trade("BRK.A US Equity", "portfolio5", "Strategy6", "User5", "f033fcaf0f164f99886a6bd6249a3b6c", 1892.25, 223),
//            new Trade("GOOG US Equity", "portfolio1", "Strategy1", "User2", "7002f15e8f234a5ca278b21157d153721", 1894.25, 225),
//            new Trade("BC94 JPY Equity", "portfolio2", "Strategy4", "User4", "7002f15e8f234a5ca278b21157d153725", 184.25, 223),
//            new Trade("BC94 JPY Equity", "portfolio2", "Strategy3", "User5", "0b875bfe83644e2882e9a2c528ddc531", 181.25, 22),
//            new Trade("BC94 JPY Equity", "portfolio2", "Strategy3", "User2", "0b875bfe83644e2882e9a2c528ddc528", 179.25, 25),
//            new Trade("BRK.A US Equity", "portfolio5", "Strategy6", "User5", "f033fcaf0f164f99886a6bd6249a3b6c", 1892.25, 223),
//            new Trade("GOOG US Equity", "portfolio1", "Strategy1", "User2", "7002f15e8f234a5ca278b21157d15378", 1894.25, 225),
//            new Trade("BC94 JPY Equity", "portfolio2", "Strategy2", "User3", "7002f15e8f234a5ca278b21157d15377", 184.25, 223),
//            new Trade("BC94 JPY Equity", "portfolio2", "Strategy3", "User3", "0b875bfe83644e2882e9a2c528ddc5265", 181.25, 22),
//            new Trade("BC94 JPY Equity", "portfolio2", "Strategy2", "User2", "0b875bfe83644e2882e9a2c528ddc52334556", 179.25, 25)
    );

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(TestMap.class.getSimpleName())
                .forks(1)
                .warmupIterations(2)
                .build();
        new Runner(opt).run();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void executeAggregatedCashPositionsParallelizedStream() {
        Map<String, Map<String, Map<String, Map<String, Double>>>> positionAggregates = new HashMap<>();
        for (Trade trade: trades) {
            String bbgCode = trade.getBbgCode();
            String portfolio = trade.getPortfolio();
            String strategy = trade.getStrategy();
            String user = trade.getUser();
            Double cashPosition = trade.getPrice() * trade.getVolume();
            positionAggregates.merge(bbgCode, new HashMap<>(), (m1, m2) -> m1); // merge the first level key
            positionAggregates.get(bbgCode).merge(portfolio, new HashMap<>(), (m1, m2) -> m1); // merge the second level key
            positionAggregates.get(bbgCode).get(portfolio).merge(strategy, new HashMap<>(), (m1, m2) -> m1); // merge the third level key
            positionAggregates.get(bbgCode).get(portfolio).get(strategy).merge(user, cashPosition, Double::sum);
//            Map<String, Map<String, Map<String, Double>>> bbgCodeNestedMap = positionAggregates.getOrDefault(trade.getBbgCode(), new HashMap<>());
//            processPortfolioPosition(trade, bbgCodeNestedMap);
//            positionAggregates.put(trade.getBbgCode(), bbgCodeNestedMap);
        }
//        AggregatedCashPositions aggregatedCashPositions = transformCashPositionsByBbgCode(positionAggregates);
        StringBuilder pnlCashAggregationDisplay = new StringBuilder();
        String header = String.format("%-20s %-12s %-12s %-12s %-15s", "BBGCode", "Portfolio", "Strategy", "User", "pnl");
        pnlCashAggregationDisplay.append(header).append("\n");
        pnlCashAggregationDisplay.append("=".repeat(header.length())).append("\n");

        for (Map.Entry<String, Map<String, Map<String, Map<String, Double>>>> perBbgCode : positionAggregates.entrySet()) {
            String bbgCode = perBbgCode.getKey();

            for (Map.Entry<String, Map<String, Map<String, Double>>> perPortfolio : perBbgCode.getValue().entrySet()) {
                String portfolio = perPortfolio.getKey();

                for (Map.Entry<String, Map<String, Double>> perStrategy : perPortfolio.getValue().entrySet()) {
                    String strategy = perStrategy.getKey();

                    for (Map.Entry<String, Double> perUser : perStrategy.getValue().entrySet()) {
                        String user = perUser.getKey();
                        Double pnlPosition = perUser.getValue();
                        pnlCashAggregationDisplay.append(String.format("%-20s %-12s %-12s %-12s $%.2f", bbgCode, portfolio, strategy, user, pnlPosition)).append("\n");
                    }
                }
            }
        }
        System.out.println(pnlCashAggregationDisplay);
//        System.out.println(positionAggregates);
//        System.out.println(aggregatedCashPositions.getPerBbgCodeCashPosition());
//        System.out.println(aggregatedCashPositions.getPerPortfolioCashPosition());
//        System.out.println(aggregatedCashPositions.getPerStrategyCashPosition());
//        System.out.println(aggregatedCashPositions.getPerUserCashPosition());
    }

    private AggregatedCashPositions transformCashPositionsByBbgCode(Map<String, Map<String, Map<String, Map<String, Map<String, Double>>>>> positionAggregates) {
        AggregatedCashPositions aggregatedCashPositions = new AggregatedCashPositions();
        positionAggregates.forEach((bbgCodeKey, perBbgCodePortfolioValue) ->
                aggregatedCashPositions.addPerBbgCodeCashPosition(bbgCodeKey, calculateCashPositionsByPortfolio(perBbgCodePortfolioValue, aggregatedCashPositions)));
        return aggregatedCashPositions;
    }

    private Double calculateCashPositionsByPortfolio(Map<String, Map<String, Map<String, Map<String, Double>>>> perPortfolioCashPositions, AggregatedCashPositions aggregatedCashPositions) {
        Double totalSumPortfolioCashPositions = perPortfolioCashPositions.entrySet()
                .parallelStream()
                .reduce(0.00,
                        (totalPortfolioCashPosition, portfolioTradePosition) -> {
                            totalPortfolioCashPosition += calculateCashPositionByStrategy(portfolioTradePosition.getValue(), aggregatedCashPositions);
                            aggregatedCashPositions.addPerPortfolioCashPosition(portfolioTradePosition.getKey(), totalPortfolioCashPosition);
                            return totalPortfolioCashPosition;
                        },
                        Double::sum
                );
        return totalSumPortfolioCashPositions;
    }

    private Double calculateCashPositionByStrategy(Map<String, Map<String, Map<String, Double>>> strategyCashPositions, AggregatedCashPositions aggregatedCashPositions) {
        Double totalSumStrategyCashPositions = strategyCashPositions.entrySet()
                .parallelStream()
                .reduce(0.00,
                        (totalStrategyCashPosition, strategyTradePosition) -> {
                            totalStrategyCashPosition += calculateCashPositionByUser(strategyTradePosition.getValue(), aggregatedCashPositions);
                            aggregatedCashPositions.addPerStrategyCashPosition(strategyTradePosition.getKey(), totalStrategyCashPosition);
                            return totalStrategyCashPosition;
                        },
                        Double::sum
                );
        return totalSumStrategyCashPositions;
    }

    private Double calculateCashPositionByUser(Map<String, Map<String, Double>> userCashPositions, AggregatedCashPositions aggregatedCashPositions) {
        Double totalSumUserCashPositions = userCashPositions.entrySet()
                .parallelStream()
                .reduce(0.00,
                        (totalUserCashPosition, userTradePosition) -> {
                            totalUserCashPosition += userTradePosition.getValue().entrySet()
                                    .parallelStream().reduce(
                                        0.00,
                                            (totalTradeCashPosition, tradeCashPosition) -> {
                                                totalTradeCashPosition += tradeCashPosition.getValue();
                                                return totalTradeCashPosition;
                                            },
                                            Double::sum
                                    );
                            aggregatedCashPositions.addPerUserCashPosition(userTradePosition.getKey(), totalUserCashPosition);
                            return totalUserCashPosition;
                        },
                        Double::sum
                );
        return totalSumUserCashPositions;
    }

    private void processPortfolioPosition(Trade trade, Map<String, Map<String, Map<String, Double>>> bbgCodeNestedMap) {
        Map<String, Map<String, Double>> portfolioCodeNestedMap = bbgCodeNestedMap.getOrDefault(trade.getPortfolio(), new HashMap<>());
        processStrategyPosition(trade, portfolioCodeNestedMap);
        bbgCodeNestedMap.put(trade.getPortfolio(), portfolioCodeNestedMap);
    }

    private void processStrategyPosition(Trade trade, Map<String, Map<String, Double>> portfolioCodeNestedMap) {
        Map<String, Double> strategyNestedMap = portfolioCodeNestedMap.getOrDefault(trade.getStrategy(), new HashMap<>());
        processUserPosition(trade, strategyNestedMap);
        portfolioCodeNestedMap.put(trade.getStrategy(), strategyNestedMap);
    }

    private void processUserPosition(Trade trade, Map<String, Double> strategyNestedMap) {
        strategyNestedMap.put(trade.getUser(), trade.getPrice() * trade.getVolume());
    }
}
