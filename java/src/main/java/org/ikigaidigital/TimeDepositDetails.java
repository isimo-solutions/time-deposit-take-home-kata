package org.ikigaidigital;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record TimeDepositDetails(int id, String planType, BigDecimal balance, int days, List<Withdrawal> withdrawals) {
    public record Withdrawal(int id, BigDecimal amount, LocalDate date) {
    }
}
