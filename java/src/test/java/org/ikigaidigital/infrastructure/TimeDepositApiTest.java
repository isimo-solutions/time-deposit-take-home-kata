package org.ikigaidigital.infrastructure;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class TimeDepositApiTest {
    @Container
    private static final OracleContainer ORACLE = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart");

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", ORACLE::getJdbcUrl);
        registry.add("spring.datasource.username", ORACLE::getUsername);
        registry.add("spring.datasource.password", ORACLE::getPassword);
    }

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TransactionTemplate transactions;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void clearDatabase() {
        transactions.executeWithoutResult(status -> {
            entityManager.createQuery("delete from WithdrawalEntity").executeUpdate();
            entityManager.createQuery("delete from TimeDepositEntity").executeUpdate();
        });
    }

    @Test
    void returnsEmptyListAndAcceptsUpdateOnEmptyDatabase() throws Exception {
        mvc.perform(post("/time-deposits/balance-updates"))
                .andExpect(status().isNoContent());
        mvc.perform(get("/time-deposits"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void updatesStoredBalancesAndReturnsDepositsWithTheirWithdrawals() throws Exception {
        transactions.executeWithoutResult(status -> {
            BigDecimal openingBalance = new BigDecimal("1200.00");
            var basic = new TimeDepositEntity(1, "basic", openingBalance, 31);
            var student = new TimeDepositEntity(3, "student", openingBalance, 365);
            List.of(basic, new TimeDepositEntity(2, "premium", openingBalance, 45),
                    student, new TimeDepositEntity(4, "student", openingBalance, 366),
                    new TimeDepositEntity(5, "premium", openingBalance, 46),
                    new TimeDepositEntity(6, "basic", openingBalance, 30))
                    .forEach(entityManager::persist);
            entityManager.persist(new WithdrawalEntity(10, basic, new BigDecimal("25.25"),
                    LocalDate.of(2026, 1, 2)));
            entityManager.persist(new WithdrawalEntity(11, basic, new BigDecimal("12.50"),
                    LocalDate.of(2026, 2, 3)));
            entityManager.persist(new WithdrawalEntity(12, student, new BigDecimal("5.00"),
                    LocalDate.of(2026, 3, 4)));
        });

        mvc.perform(post("/time-deposits/balance-updates"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        List<BigDecimal> balances = entityManager.createQuery(
                "select d.balance from TimeDepositEntity d order by d.id", BigDecimal.class).getResultList();
        assertThat(balances)
                .usingElementComparator(BigDecimal::compareTo)
                .containsExactly(new BigDecimal("1201.00"), new BigDecimal("1200.00"),
                        new BigDecimal("1203.00"), new BigDecimal("1200.00"),
                        new BigDecimal("1205.00"), new BigDecimal("1200.00"));

        mvc.perform(get("/time-deposits"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                          {"id":1,"planType":"basic","balance":1201,"days":31,"withdrawals":[
                            {"id":10,"amount":25.25,"date":"2026-01-02"},
                            {"id":11,"amount":12.50,"date":"2026-02-03"}
                          ]},
                          {"id":2,"planType":"premium","balance":1200,"days":45,"withdrawals":[]},
                          {"id":3,"planType":"student","balance":1203,"days":365,"withdrawals":[
                            {"id":12,"amount":5,"date":"2026-03-04"}
                          ]},
                          {"id":4,"planType":"student","balance":1200,"days":366,"withdrawals":[]},
                          {"id":5,"planType":"premium","balance":1205,"days":46,"withdrawals":[]},
                          {"id":6,"planType":"basic","balance":1200,"days":30,"withdrawals":[]}
                        ]
                        """, JsonCompareMode.STRICT));
    }
}
