package co.uk.ghco.domain.model;

import java.util.*;
import java.util.stream.*;

public class Sales {
    private String region;
    private String product;
    private double amount;

    public Sales(String region, String product, double amount) {
        this.region = region;
        this.product = product;
        this.amount = amount;
    }

    public String getRegion() {
        return region;
    }

    public String getProduct() {
        return product;
    }

    public double getAmount() {
        return amount;
    }

    public static void main(String[] args) {
        List<Sales> sales = Arrays.asList(
                new Sales("North", "Product A", 100.0),
                new Sales("South", "Product B", 200.0),
                new Sales("North", "Product B", 300.0),
                new Sales("South", "Product A", 400.0),
                new Sales("East", "Product A", 500.0),
                new Sales("West", "Product B", 600.0)
        );

        Map<String, Map<String, Double>> result = sales.stream()
                .reduce(new HashMap<>(),
                        (map, sale) -> {
                            map.computeIfAbsent(sale.getRegion(), key -> new HashMap<>())
                                    .merge(sale.getProduct(), sale.getAmount(), Double::sum);
                            return map;
                        },
                        (map1, map2) -> {
                            map2.forEach((region, productMap) -> {
                                map1.merge(region, productMap, (map1Value, map2Value) -> {
                                    map2Value.forEach((product, amount) ->
                                            map1Value.merge(product, amount, Double::sum)
                                    );
                                    return map1Value;
                                });
                            });
                            return map1;
                        });

        System.out.println(result);
    }
}

