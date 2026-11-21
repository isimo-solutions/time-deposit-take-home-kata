package org.ikigaidigital;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.function.IntPredicate;

public class TimeDepositCalculator {
    private static final int GRACE_PERIOD_DAYS = 30;
    private static final int MONTHS_PER_YEAR = 12;
    private static final int DAYS_IN_YEAR = 365;
    private static final int PREMIUM_WAIT_DAYS = 45;

    private static final Map<String, InterestPlan> INTEREST_PLANS = Map.of(
            "basic", new InterestPlan(0.01, days -> true),
            // The domain stores elapsed days. Student earns interest through day 365.
            "student", new InterestPlan(0.03, days -> days <= DAYS_IN_YEAR),
            "premium", new InterestPlan(0.05, days -> days > PREMIUM_WAIT_DAYS));

    /**
     * Assumes a month is one of 12 equal interest periods in a year. Each call applies
     * one period of interest to the current balance using the annual rate divided by 12.
     * Elapsed days determine eligibility. An eligible deposit receives the full month's
     * interest, including on its first eligible day.
     */
    public void updateBalance(List<TimeDeposit> deposits) {
        for (TimeDeposit deposit : deposits) {
            double interest = monthlyInterest(deposit);
            double roundedInterest = new BigDecimal(interest).setScale(2, RoundingMode.HALF_UP).doubleValue();
            deposit.setBalance(deposit.getBalance() + roundedInterest);
        }
    }

    private double monthlyInterest(TimeDeposit deposit) {
        int days = deposit.getDays();
        if (days <= GRACE_PERIOD_DAYS) {
            return 0;
        }

        InterestPlan plan = INTEREST_PLANS.get(deposit.getPlanType());
        if (plan == null || !plan.isEligible().test(days)) {
            return 0;
        }

        return deposit.getBalance() * plan.annualRate() / MONTHS_PER_YEAR;
    }

    private record InterestPlan(double annualRate, IntPredicate isEligible) {
    }
}
