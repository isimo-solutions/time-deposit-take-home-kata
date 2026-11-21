package org.ikigaidigital.infrastructure;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.TimeDepositDetails;

@Entity
@Table(name = "timeDeposits")
public class TimeDepositEntity {
    @Id
    private int id;

    @Column(nullable = false)
    private String planType;

    @Column(nullable = false)
    private int days;

    @Column(nullable = false, precision = 38, scale = 2)
    private BigDecimal balance;

    @OneToMany(mappedBy = "timeDeposit")
    @OrderBy("id ASC")
    private List<WithdrawalEntity> withdrawals = new ArrayList<>();

    protected TimeDepositEntity() {
    }

    TimeDepositEntity(int id, String planType, BigDecimal balance, int days) {
        this.id = id;
        this.planType = planType;
        this.balance = balance;
        this.days = days;
    }

    TimeDeposit toDomain() {
        return new TimeDeposit(id, planType, balance.doubleValue(), days);
    }

    TimeDepositDetails toDetails() {
        return new TimeDepositDetails(id, planType, balance, days,
                withdrawals.stream().map(WithdrawalEntity::toDetails).toList());
    }

    void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
