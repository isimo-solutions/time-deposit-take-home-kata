package org.ikigaidigital;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeDepositCalculatorTest {
    @ParameterizedTest
    @CsvSource({
            "basic,   30,    1200.00,    1200.00",
            "basic,   31,    1200.00,    1201.00",
            "student, 30,    1200.00,    1200.00",
            "student, 31,    1200.00,    1203.00",
            "student, 365,   1200.00,    1203.00",
            "student, 366,   1200.00,    1200.00",
            "premium, 30,    1200.00,    1200.00",
            "premium, 45,    1200.00,    1200.00",
            "premium, 46,    1200.00,    1205.00",
            "basic,   45, 1234567.00, 1235595.81",
            "basic,   31,       6.00,       6.01",
            "unknown, 46,    1200.00,    1200.00"
    })
    void appliesMonthlyInterestWithinThePlanWindow(String planType, int days, double openingBalance,
                                                  double expectedBalance) {
        TimeDeposit deposit = new TimeDeposit(1, planType, openingBalance, days);

        new TimeDepositCalculator().updateBalance(List.of(deposit));

        assertThat(deposit.getBalance()).isEqualTo(expectedBalance);
    }
}
