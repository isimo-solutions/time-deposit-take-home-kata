package org.ikigaidigital.infrastructure;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.ikigaidigital.TimeDepositDetails;

@Entity
@Table(name = "withdrawals")
public class WithdrawalEntity {
    @Id
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "timeDepositId", nullable = false)
    private TimeDepositEntity timeDeposit;

    @Column(nullable = false, precision = 38, scale = 2)
    private BigDecimal amount;

    @Column(name = "\"DATE\"", nullable = false)
    private LocalDate date;

    protected WithdrawalEntity() {
    }

    WithdrawalEntity(int id, TimeDepositEntity timeDeposit, BigDecimal amount, LocalDate date) {
        this.id = id;
        this.timeDeposit = timeDeposit;
        this.amount = amount;
        this.date = date;
    }

    TimeDepositDetails.Withdrawal toDetails() {
        return new TimeDepositDetails.Withdrawal(id, amount, date);
    }
}
