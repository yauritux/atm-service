package link.yauritux.domain.entity;

import link.yauritux.sharedkernel.exception.DomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author yauritux@gmail.com
 * @version 1.0
 */
class DebtAccountTest {

    @Test
    void createNewDebtAccountWithNullDebtorName_shouldFailWithMessage() {
        Exception exception = assertThrows(DomainException.class,
                () -> new DebtAccount(null, "Igneel", BigDecimal.TEN));
        assertEquals("debtor name is required!", exception.getMessage());
    }

    @Test
    void createNewDebtAccountWithEmptyDebtorName_shouldFailWithMessage() {
        Exception exception = assertThrows(DomainException.class,
                () -> new DebtAccount(" ", "Igneel", BigDecimal.TEN));
        assertEquals("debtor name is required!", exception.getMessage());
    }

    @Test
    void createNewDebtAccountWithNullCreditorName_shouldFailWithMessage() {
        Exception exception = assertThrows(DomainException.class,
                () -> new DebtAccount("Natsu", null, BigDecimal.TEN));
        assertEquals("creditor name is required!", exception.getMessage());
    }

    @Test
    void createNewDebtAccountWithEmptyCreditorName_shouldFailWithMessage() {
        Exception exception = assertThrows(DomainException.class,
                () -> new DebtAccount("Natsu", " ", BigDecimal.TEN));
        assertEquals("creditor name is required!", exception.getMessage());
    }

    @Test
    void createNewDebtAccountWithAmountLessThanZero_shouldFailWithMessage() {
        Exception exception = assertThrows(DomainException.class,
                () -> new DebtAccount("Natsu", "Igneel", BigDecimal.valueOf(-500_000)));
        assertEquals("owed amount should not be less than or equal to zero!", exception.getMessage());
    }

    @Test
    void createNewDebtAccountWithZeroAmount_shouldFailWithMessage() {
        Exception exception = assertThrows(DomainException.class,
                () -> new DebtAccount("Natsu", "Igneel", BigDecimal.ZERO));
        assertEquals("owed amount should not be less than or equal to zero!", exception.getMessage());
    }

    @Test
    void createNewDebtAccount_withConstructor() {
        var newDebtAccount = new DebtAccount("Natsu", "Igneel", BigDecimal.TEN);
        assertEquals("Natsu", newDebtAccount.getDebtorAccountName());
        assertEquals("Igneel", newDebtAccount.getCreditorAccountName());
        assertTrue(newDebtAccount.getAmount().compareTo(BigDecimal.TEN) == 0);
    }

    @Test
    void createNewDebtAccount_withSetter() {
        var newDebtAccount = new DebtAccount();
        newDebtAccount.setDebtorAccountName("Natsu");
        newDebtAccount.setCreditorAccountName("Igneel");
        newDebtAccount.setAmount(BigDecimal.TEN);
        assertEquals("Natsu", newDebtAccount.getDebtorAccountName());
        assertEquals("Igneel", newDebtAccount.getCreditorAccountName());
        assertTrue(newDebtAccount.getAmount().compareTo(BigDecimal.TEN) == 0);
    }

    @Test
    void twoIdenticalDebtAccounts_shouldGiveEqualsTrue() {
        var debtAccount1 = new DebtAccount("Natsu", "Igneel", BigDecimal.TEN);
        var debtAccount2 = new DebtAccount("Natsu", "Igneel", BigDecimal.TEN);
        assertTrue(debtAccount1.equals(debtAccount2));
    }

    @Test
    void twoDebtAccountsWithDifferentAmount_shouldGiveEqualsFalse() {
        var debtAccount1 = new DebtAccount("Natsu", "Igneel", BigDecimal.TEN);
        var debtAccount2 = new DebtAccount("Natsu", "Igneel", BigDecimal.valueOf(100_000));
        assertFalse(debtAccount1.equals(debtAccount2));
    }
}
