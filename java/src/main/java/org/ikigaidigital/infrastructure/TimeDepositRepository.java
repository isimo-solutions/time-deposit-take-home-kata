package org.ikigaidigital.infrastructure;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.ikigaidigital.TimeDepositDetails;
import org.springframework.stereotype.Repository;

@Repository
class TimeDepositRepository {
    @PersistenceContext
    private EntityManager entityManager;

    List<TimeDepositDetails> findAll() {
        return entityManager.createQuery("""
                select d from TimeDepositEntity d
                left join fetch d.withdrawals
                order by d.id
                """, TimeDepositEntity.class)
                .getResultList().stream()
                .map(TimeDepositEntity::toDetails)
                .toList();
    }

    List<TimeDepositEntity> lockAll() {
        return entityManager.createQuery("select d from TimeDepositEntity d order by d.id", TimeDepositEntity.class)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultList();
    }
}
