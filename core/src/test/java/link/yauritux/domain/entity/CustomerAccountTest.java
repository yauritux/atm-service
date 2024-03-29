package link.yauritux.domain.entity;

import link.yauritux.sharedkernel.exception.DomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author yauritux@gmail.com
 * @version 1.0
 */
class CustomerAccountTest {

    @Test
    void newCustomerAccountNameIsNull() {
        assertThrows(DomainException.class, () -> new CustomerAccount(null));
    }

    @Test
    void newCustomerAccountNameIsEmpty() {
        assertThrows(DomainException.class, () -> new CustomerAccount(" "));
    }

    @Test
    void newCustomerAccountWithNoDeposit() {
        var newAccount = new CustomerAccount("Uzumaki Naruto");
        assertEquals("Uzumaki Naruto", newAccount.getName());
        assertEquals(BigDecimal.ZERO, newAccount.getBalance());
    }

    @Test
    void newCustomerAccountWithSomeDepositAmount() {
        var newAccount = new CustomerAccount("Yauri Attamimi", BigDecimal.valueOf(10_000_000));
        assertEquals("Yauri Attamimi", newAccount.getName());
        assertNotEquals(BigDecimal.ZERO, newAccount.getBalance());
    }

    @Test
    void twoIdenticalCustomersShouldReturnEqualsTrue() {
        var firstAccount = new CustomerAccount("Yauri Attamimi", BigDecimal.ZERO);
        var secondAccount = new CustomerAccount("Yauri Attamimi");
        assertEquals(firstAccount, secondAccount);
    }

    @Test
    void twoIdenticalCustomersShouldHaveSameHashCode() {
        var firstAccount = new CustomerAccount("Yauri Attamimi");
        var secondAccount = new CustomerAccount("Yauri Attamimi");
        assertEquals(firstAccount.hashCode(), secondAccount.hashCode());
    }

    @Test
    void twoCustomerAccountsWithDifferentBalanceYetSameNameShouldHaveSameHashCode() {
        var firstAccount = new CustomerAccount("Yauri Attamimi");
        firstAccount.setBalance(BigDecimal.ZERO);
        var secondAccount = new CustomerAccount("Yauri Attamimi", BigDecimal.TEN);
        assertEquals(firstAccount.hashCode(), secondAccount.hashCode());
        assertNotEquals(firstAccount.getBalance(), secondAccount.getBalance());
    }
}
