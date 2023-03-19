package link.yauritux.adapter.out.inmemory;

import link.yauritux.domain.entity.CustomerAccount;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author yauritux@gmail.com
 * @version 1.0
 */
class InMemCustomerAccountRepositoryTest {

    private InMemCustomerAccountRepository repository;

    private final CustomerAccount customerAccount = new CustomerAccount("Yauri");

    @BeforeEach
    void setUp() {
        repository = new InMemCustomerAccountRepository();
        customerAccount.setBalance(BigDecimal.valueOf(500));
    }

    @Test
    void save_newCustomer_willCreateNewRecord() {
        repository.save(customerAccount);
        var foundRecord = repository.findCustomerByName(customerAccount.getName());
        assertTrue(foundRecord.isPresent());
        assertEquals(BigDecimal.valueOf(500), foundRecord.get().getBalance());
    }

    @AfterEach
    void tearDown() {
        repository = null;
    }
}
