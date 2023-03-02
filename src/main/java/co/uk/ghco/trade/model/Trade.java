package co.uk.ghco.trade.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Trade {
    private final String tradeId;
    private final String bbgCode;
    private final CashAmount price;
    private final Integer volume;
    private final String portfolio;
    private final ActionType actionType;
    private final String accountId;
    private final String strategy;
    private final String userId;
    private final LocalDateTime tradeTime;
    private final LocalDate valueDate;

    public Trade(String tradeId, String bbgCode, CashAmount price, Integer volume, String portfolio, ActionType actionType, String accountId, String strategy, String userId, LocalDateTime tradeTime, LocalDate valueDate) {
        this.tradeId = tradeId;
        this.bbgCode = bbgCode;
        this.price = price;
        this.volume = volume;
        this.portfolio = portfolio;
        this.actionType = actionType;
        this.accountId = accountId;
        this.strategy = strategy;
        this.userId = userId;
        this.tradeTime = tradeTime;
        this.valueDate = valueDate;
    }

    public CashAmount calculateCashPosition() {
        BigDecimal cashPositionValue = price.getAmount().multiply(BigDecimal.valueOf(volume));
        return new CashAmount(price.getCurrency(), cashPositionValue.toString());
    }

    public String getTradeId() {return this.tradeId;}

    public String getBbgCode() {return this.bbgCode;}

    public CashAmount getPrice() {return this.price;}

    public Integer getVolume() {return this.volume;}

    public String getPortfolio() {return this.portfolio;}

    public ActionType getActionType() {return this.actionType;}

    public String getAccountId() {return this.accountId;}

    public String getStrategy() {return this.strategy;}

    public String getUserId() {return this.userId;}

    public LocalDateTime getTradeTime() {return this.tradeTime;}

    public LocalDate getValueDate() {return this.valueDate;}

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Trade)) return false;
        final Trade other = (Trade) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$tradeId = this.getTradeId();
        final Object other$tradeId = other.getTradeId();
        if (!Objects.equals(this$tradeId, other$tradeId)) return false;
        final Object this$bbgCode = this.getBbgCode();
        final Object other$bbgCode = other.getBbgCode();
        if (!Objects.equals(this$bbgCode, other$bbgCode)) return false;
        final Object this$price = this.getPrice();
        final Object other$price = other.getPrice();
        if (!Objects.equals(this$price, other$price)) return false;
        final Object this$volume = this.getVolume();
        final Object other$volume = other.getVolume();
        if (!Objects.equals(this$volume, other$volume)) return false;
        final Object this$portfolio = this.getPortfolio();
        final Object other$portfolio = other.getPortfolio();
        if (!Objects.equals(this$portfolio, other$portfolio)) return false;
        final Object this$actionType = this.getActionType();
        final Object other$actionType = other.getActionType();
        if (!Objects.equals(this$actionType, other$actionType)) return false;
        final Object this$accountId = this.getAccountId();
        final Object other$accountId = other.getAccountId();
        if (!Objects.equals(this$accountId, other$accountId)) return false;
        final Object this$strategy = this.getStrategy();
        final Object other$strategy = other.getStrategy();
        if (!Objects.equals(this$strategy, other$strategy)) return false;
        final Object this$userId = this.getUserId();
        final Object other$userId = other.getUserId();
        if (!Objects.equals(this$userId, other$userId)) return false;
        final Object this$tradeTime = this.getTradeTime();
        final Object other$tradeTime = other.getTradeTime();
        if (!Objects.equals(this$tradeTime, other$tradeTime)) return false;
        final Object this$valueDate = this.getValueDate();
        final Object other$valueDate = other.getValueDate();
        if (!Objects.equals(this$valueDate, other$valueDate)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {return other instanceof Trade;}

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $tradeId = this.getTradeId();
        result = result * PRIME + ($tradeId == null ? 43 : $tradeId.hashCode());
        final Object $bbgCode = this.getBbgCode();
        result = result * PRIME + ($bbgCode == null ? 43 : $bbgCode.hashCode());
        final Object $price = this.getPrice();
        result = result * PRIME + ($price == null ? 43 : $price.hashCode());
        final Object $volume = this.getVolume();
        result = result * PRIME + ($volume == null ? 43 : $volume.hashCode());
        final Object $portfolio = this.getPortfolio();
        result = result * PRIME + ($portfolio == null ? 43 : $portfolio.hashCode());
        final Object $actionType = this.getActionType();
        result = result * PRIME + ($actionType == null ? 43 : $actionType.hashCode());
        final Object $accountId = this.getAccountId();
        result = result * PRIME + ($accountId == null ? 43 : $accountId.hashCode());
        final Object $strategy = this.getStrategy();
        result = result * PRIME + ($strategy == null ? 43 : $strategy.hashCode());
        final Object $userId = this.getUserId();
        result = result * PRIME + ($userId == null ? 43 : $userId.hashCode());
        final Object $tradeTime = this.getTradeTime();
        result = result * PRIME + ($tradeTime == null ? 43 : $tradeTime.hashCode());
        final Object $valueDate = this.getValueDate();
        result = result * PRIME + ($valueDate == null ? 43 : $valueDate.hashCode());
        return result;
    }

    public String toString() {return "Trade(tradeId=" + this.getTradeId() + ", bbgCode=" + this.getBbgCode() + ", price=" + this.getPrice() + ", volume=" + this.getVolume() + ", portfolio=" + this.getPortfolio() + ", actionType=" + this.getActionType() + ", accountId=" + this.getAccountId() + ", strategy=" + this.getStrategy() + ", userId=" + this.getUserId() + ", tradeTime=" + this.getTradeTime() + ", valueDate=" + this.getValueDate() + ")";}

    public TradeBuilder toBuilder() {return new TradeBuilder().tradeId(this.tradeId).bbgCode(this.bbgCode).price(this.price).volume(this.volume).portfolio(this.portfolio).actionType(this.actionType).accountId(this.accountId).strategy(this.strategy).userId(this.userId).tradeTime(this.tradeTime).valueDate(this.valueDate);}

    public static class TradeBuilder {
        private String tradeId;
        private String bbgCode;
        private CashAmount price;
        private Integer volume;
        private String portfolio;
        private ActionType actionType;
        private String accountId;
        private String strategy;
        private String userId;
        private LocalDateTime tradeTime;
        private LocalDate valueDate;

        TradeBuilder() {}

        public TradeBuilder tradeId(String tradeId) {
            this.tradeId = tradeId;
            return this;
        }

        public TradeBuilder bbgCode(String bbgCode) {
            this.bbgCode = bbgCode;
            return this;
        }

        public TradeBuilder price(CashAmount price) {
            this.price = price;
            return this;
        }

        public TradeBuilder volume(Integer volume) {
            this.volume = volume;
            return this;
        }

        public TradeBuilder portfolio(String portfolio) {
            this.portfolio = portfolio;
            return this;
        }

        public TradeBuilder actionType(ActionType actionType) {
            this.actionType = actionType;
            return this;
        }

        public TradeBuilder accountId(String accountId) {
            this.accountId = accountId;
            return this;
        }

        public TradeBuilder strategy(String strategy) {
            this.strategy = strategy;
            return this;
        }

        public TradeBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public TradeBuilder tradeTime(LocalDateTime tradeTime) {
            this.tradeTime = tradeTime;
            return this;
        }

        public TradeBuilder valueDate(LocalDate valueDate) {
            this.valueDate = valueDate;
            return this;
        }

        public Trade build() {
            return new Trade(tradeId, bbgCode, price, volume, portfolio, actionType, accountId, strategy, userId, tradeTime, valueDate);
        }

        public String toString() {return "Trade.TradeBuilder(tradeId=" + this.tradeId + ", bbgCode=" + this.bbgCode + ", price=" + this.price + ", volume=" + this.volume + ", portfolio=" + this.portfolio + ", actionType=" + this.actionType + ", accountId=" + this.accountId + ", strategy=" + this.strategy + ", userId=" + this.userId + ", tradeTime=" + this.tradeTime + ", valueDate=" + this.valueDate + ")";}
    }
}
