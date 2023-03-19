package link.yauritux.adapter.out.inmemory;

import link.yauritux.domain.entity.CustomerAccount;
import link.yauritux.domain.entity.DebtAccount;
import link.yauritux.exception.ApplicationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author yauritux@gmail.com
 * @version 1.0
 */
class InMemDebtAccountRepositoryTest {

    private InMemDebtAccountRepository repository;

    private final CustomerAccount debtorAccount = new CustomerAccount("Ken");

    @BeforeEach
    void setUp() {
        this.repository = new InMemDebtAccountRepository();
    }

    @Test
    void save_NoDebtAccountIsProvided_shouldFailWithMessage() {
        Exception exception = assertThrows(ApplicationException.class, () -> repository.save(null));
        assertEquals("debt account is missing!", exception.getMessage());
    }

    @Test
    void save_DebtAccountDebtorNameIsNull_shouldFailWithMessage() {
        var newDebtAccount = new DebtAccount();
        Exception exception = assertThrows(ApplicationException.class, () -> repository.save(newDebtAccount));
        assertEquals("debtor name is missing!", exception.getMessage());
    }

    @Test
    void save_DebtAccountDebtorNameIsEmpty_shouldFailWithMessage() {
        var newDebtAccount = new DebtAccount();
        newDebtAccount.setDebtorAccountName(" ");
        Exception exception = assertThrows(ApplicationException.class, () -> repository.save(newDebtAccount));
        assertEquals("debtor name is missing!", exception.getMessage());
    }

    @Test
    void save_DebtAccountCreditorNameIsNull_shouldFailWithMessage() {
        var newDebtAccount = new DebtAccount();
        newDebtAccount.setDebtorAccountName("Jiraiya");
        Exception exception = assertThrows(ApplicationException.class, () -> repository.save(newDebtAccount));
        assertEquals("creditor name is missing!", exception.getMessage());
    }

    @Test
    void save_DebtAccountCreditorNameIsEmpty_shouldFailWithMessage() {
        var newDebtAccount = new DebtAccount();
        newDebtAccount.setDebtorAccountName("Jiraiya");
        newDebtAccount.setCreditorAccountName(" ");
        Exception exception = assertThrows(ApplicationException.class, () -> repository.save(newDebtAccount));
        assertEquals("creditor name is missing!", exception.getMessage());
    }

    @Test
    void save_NewRecord() {
        var newDebtAccount = new DebtAccount(debtorAccount.getName(), "Yauri", BigDecimal.valueOf(100));
        repository.save(newDebtAccount);
        assertEquals(1, repository.findByDebtorAccount(debtorAccount.getName()).size());
    }

    @Test
    void save_ExistingDebtorRecord_willUpdateDebtListAccount() {
        var firstDebtAccount = new DebtAccount(debtorAccount.getName(), "Yauri", BigDecimal.TEN);
        repository.save(firstDebtAccount);
        var secondDebtAccount = new DebtAccount(debtorAccount.getName(), "Kenji", BigDecimal.TEN);
        repository.save(secondDebtAccount);
        List<DebtAccount> records = repository.findByDebtorAccount(debtorAccount.getName());
        assertEquals(2, records.size());
    }

    @Test
    void save_oweTheSamePerson_willIncrementTheOwedAmount() {
        var firstDebtAccount = new DebtAccount(debtorAccount.getName(), "Yauri", BigDecimal.TEN);
        repository.save(firstDebtAccount);
        var secondDebtAccount = new DebtAccount(debtorAccount.getName(), "Yauri", BigDecimal.valueOf(15));
        repository.save(secondDebtAccount);
        List<DebtAccount> records = repository.findByDebtorAccount(debtorAccount.getName());
        assertEquals(BigDecimal.valueOf(25), records.get(0).getAmount());
    }

    @Test
    void remove() {
        var debtAccount = new DebtAccount(debtorAccount.getName(), "Yauri", BigDecimal.TEN);
        repository.save(debtAccount);
        var secondDebtAccount = new DebtAccount(debtorAccount.getName(), "Kenji", BigDecimal.valueOf(3.5));
        repository.save(secondDebtAccount);
        assertEquals(2, repository.findByDebtorAccount(debtorAccount.getName()).size());
        repository.remove(debtAccount);
        assertEquals(1, repository.findByDebtorAccount(debtorAccount.getName()).size());
    }

    @AfterEach
    void tearDown() {
        this.repository = null;
    }
}
