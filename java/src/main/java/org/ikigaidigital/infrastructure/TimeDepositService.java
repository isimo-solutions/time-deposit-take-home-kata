package org.ikigaidigital.infrastructure;

import java.math.BigDecimal;
import java.util.List;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.TimeDepositCalculator;
import org.ikigaidigital.TimeDepositDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class TimeDepositService {
    private final TimeDepositRepository repository;
    private final TimeDepositCalculator calculator = new TimeDepositCalculator();

    TimeDepositService(TimeDepositRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TimeDepositDetails> findAll() {
        return repository.findAll();
    }

    @Transactional
    public void updateBalances() {
        // The caller controls when to post interest. Each request counts as a new monthly posting,
        // so repeated requests can compound interest on the same day.
        List<TimeDepositEntity> storedDeposits = repository.lockAll();
        List<TimeDeposit> deposits = storedDeposits.stream().map(TimeDepositEntity::toDomain).toList();
        calculator.updateBalance(deposits);
        for (int index = 0; index < deposits.size(); index++) {
            storedDeposits.get(index).setBalance(BigDecimal.valueOf(deposits.get(index).getBalance()));
        }
    }
}
