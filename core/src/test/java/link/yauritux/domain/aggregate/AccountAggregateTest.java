package link.yauritux.domain.aggregate;

import link.yauritux.domain.entity.CustomerAccount;
import link.yauritux.domain.entity.DebtAccount;
import link.yauritux.port.out.CustomerAccountRepositoryPort;
import link.yauritux.port.out.DebtAccountRepositoryPort;
import link.yauritux.sharedkernel.exception.DomainException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author yauritux@gmail.com
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class AccountAggregateTest {

    private AccountAggregate sut;

    @Mock
    private CustomerAccountRepositoryPort accountRepositoryPort;

    @Mock
    private DebtAccountRepositoryPort debtRepositoryPort;

    private final CustomerAccount registeredCustomer =
            new CustomerAccount("Yauri Attamimi", BigDecimal.valueOf(10_000_000));

    private final CustomerAccount targetedCustomerAccount =
            new CustomerAccount("Ichigo Kurosaki", BigDecimal.valueOf(500_000));

    @BeforeEach
    void setUp() {
        sut = new AccountAggregate(accountRepositoryPort, debtRepositoryPort);
    }

    @Test
    void login_withEmptyCustomerAccountName_shouldFailTheLoginAttempt() {
        when(accountRepositoryPort.findCustomerByName(anyString())).thenReturn(Optional.empty());
        verify(accountRepositoryPort, never()).save(any());
        Exception exception = assertThrows(DomainException.class, () -> sut.login(" "));
        assertEquals("Customer account name cannot be empty!", exception.getMessage());
    }

    @Test
    void login_withNonExistingAccount_willCreateANewAccount() {
        when(accountRepositoryPort.findCustomerByName("Uzumaki Naruto")).thenReturn(Optional.empty());
        var currentBalance = sut.login("Uzumaki Naruto");
        assertEquals(BigDecimal.ZERO, sut.getCurrentAccount().getBalance());
        assertEquals(BigDecimal.ZERO, currentBalance);
        verify(accountRepositoryPort, atLeastOnce()).save(any(CustomerAccount.class));
    }

    @Test
    void login_withExistingAccount_willReturnTheAccount() {
        when(accountRepositoryPort.findCustomerByName("Yauri Attamimi")).thenReturn(Optional.of(registeredCustomer));
        var currentBalance = sut.login("Yauri Attamimi");
        assertEquals("Yauri Attamimi", sut.getCurrentAccount().getName());
        assertEquals(BigDecimal.valueOf(10_000_000), sut.getCurrentAccount().getBalance());
        assertEquals(BigDecimal.valueOf(10_000_000), currentBalance);
        verify(accountRepositoryPort, atLeastOnce()).save(any(CustomerAccount.class));
    }

    @Test
    void deposit_withoutLogin_shouldAskToLoginFirst() {
        Exception exception = assertThrows(DomainException.class, () -> sut.deposit(BigDecimal.TEN));
        assertEquals("Deposit failed! Please login first!!", exception.getMessage());
    }

    @Test
    void deposit_10DollarToARegisteredAccount_willAddDepositAmountToTheRegisteredAccount() {
        when(accountRepositoryPort.findCustomerByName("Yauri Attamimi")).thenReturn(Optional.of(registeredCustomer));
        sut.login("Yauri Attamimi");
        var response = sut.deposit(BigDecimal.TEN);
        assertEquals(BigDecimal.valueOf(10_000_010), registeredCustomer.getBalance());
        assertEquals(registeredCustomer, response.getCustomerAccount());
        assertTrue(response.getTransferList().isEmpty());
        verify(accountRepositoryPort, atLeastOnce()).save(any(CustomerAccount.class));
    }

    @Test
    void deposit_10DollarWhileOwed30DollarToSomeone_shouldBeAutoDebitAndUpdateRemainingDebt() {
        var sourceAccount = new CustomerAccount("Naruto");
        var targetAccount = new CustomerAccount("Sakura");
        when(accountRepositoryPort.findCustomerByName("Naruto")).thenReturn(Optional.of(sourceAccount));
        var debtAccount = new DebtAccount(sourceAccount.getName(), targetAccount.getName(), BigDecimal.valueOf(30));
        when(debtRepositoryPort.findByDebtorAccount(sourceAccount.getName())).thenReturn(List.of(debtAccount));
        when(accountRepositoryPort.findCustomerByName(debtAccount.getCreditorAccountName())).thenReturn(Optional.of(targetAccount));
        when(accountRepositoryPort.findCustomerByName(sourceAccount.getName())).thenReturn(Optional.of(sourceAccount));
        sut.login(sourceAccount.getName());
        var response = sut.deposit(BigDecimal.TEN);
        assertEquals(sourceAccount, response.getCustomerAccount());
        assertFalse(response.getTransferList().isEmpty());
        verify(accountRepositoryPort, atLeastOnce()).save(sourceAccount);
        verify(accountRepositoryPort, atLeastOnce()).save(targetAccount);
        verify(debtRepositoryPort, never()).remove(any(DebtAccount.class));
    }

    @Test
    void deposit_50DollarWhileOwed50DollarToSomeone_shouldBeAutoDebitAndRemoveTheDebt() {
        var sourceAccount = new CustomerAccount("Sakura");
        var targetAccount = new CustomerAccount("Yauri");
        when(accountRepositoryPort.findCustomerByName("Sakura")).thenReturn(Optional.of(sourceAccount));
        var debtAccount = new DebtAccount(sourceAccount.getName(), targetAccount.getName(), BigDecimal.valueOf(50));
        when(debtRepositoryPort.findByDebtorAccount(sourceAccount.getName())).thenReturn(List.of(debtAccount));
        when(accountRepositoryPort.findCustomerByName(debtAccount.getCreditorAccountName())).thenReturn(Optional.of(targetAccount));
        when(accountRepositoryPort.findCustomerByName(sourceAccount.getName())).thenReturn(Optional.of(sourceAccount));
        sut.login(sourceAccount.getName());
        var response = sut.deposit(BigDecimal.valueOf(50));
        assertEquals(sourceAccount, response.getCustomerAccount());
        assertFalse(response.getTransferList().isEmpty());
        verify(accountRepositoryPort, atLeastOnce()).save(sourceAccount);
        verify(accountRepositoryPort, atLeastOnce()).save(targetAccount);
        verify(debtRepositoryPort, atLeastOnce()).remove(any(DebtAccount.class));
        verify(debtRepositoryPort, never()).save(any(DebtAccount.class));
    }

    @Test
    void deposit_50DollarWhileOwed20DollarToSomeone_shouldBeAutoDebitAndRemoveTheDebt() {
        var sourceAccount = new CustomerAccount("Kenpachi");
        var targetAccount = new CustomerAccount("Yauri");
        when(accountRepositoryPort.findCustomerByName("Kenpachi")).thenReturn(Optional.of(sourceAccount));
        var debtAccount = new DebtAccount(sourceAccount.getName(), targetAccount.getName(), BigDecimal.valueOf(20));
        when(debtRepositoryPort.findByDebtorAccount(sourceAccount.getName())).thenReturn(List.of(debtAccount));
        when(accountRepositoryPort.findCustomerByName(debtAccount.getCreditorAccountName())).thenReturn(Optional.of(targetAccount));
        when(accountRepositoryPort.findCustomerByName(sourceAccount.getName())).thenReturn(Optional.of(sourceAccount));
        sut.login(sourceAccount.getName());
        var response = sut.deposit(BigDecimal.valueOf(50));
        assertEquals(sourceAccount, response.getCustomerAccount());
        assertFalse(response.getTransferList().isEmpty());
        verify(accountRepositoryPort, atLeastOnce()).save(sourceAccount);
        verify(accountRepositoryPort, atLeastOnce()).save(targetAccount);
        verify(debtRepositoryPort, atLeastOnce()).remove(any(DebtAccount.class));
        verify(debtRepositoryPort, never()).save(any(DebtAccount.class));
    }

    @Test
    void logout() {
        when(accountRepositoryPort.findCustomerByName("Yauri Attamimi")).thenReturn(Optional.of(registeredCustomer));
        sut.login("Yauri Attamimi");
        assertNotNull(sut.getCurrentAccount());
        sut.logout();
        assertNull(sut.getCurrentAccount());
    }

    @Test
    void transfer_withoutLogin_shouldAskToLoginFirst() {
        Exception exception = assertThrows(DomainException.class,
                () -> sut.transfer("Ichigo Kurosaki", BigDecimal.valueOf(50)));
        assertEquals("Please login first!", exception.getMessage());
    }

    @Test
    void transfer_toUnregisteredTargetAccount_shouldFailWithMessage() {
        sut.login("Yauri Attamimi");
        when(accountRepositoryPort.findCustomerByName("Natsu Dragneel")).thenReturn(Optional.empty());
        Exception exception = assertThrows(DomainException.class,
                () -> sut.transfer("Natsu Dragneel", BigDecimal.valueOf(100_000)));
        assertEquals("Target account does not exist!", exception.getMessage());
    }

    @Test
    void transfer_toRegisteredAccountWithSufficientBalance_willAddTransferAmountToTargetRegisteredAccount() {
        when(accountRepositoryPort.findCustomerByName("Yauri Attamimi")).thenReturn(Optional.of(registeredCustomer));
        sut.login("Yauri Attamimi");
        when(accountRepositoryPort.findCustomerByName("Ichigo Kurosaki"))
                .thenReturn(Optional.of(targetedCustomerAccount));
        var transferAmount = BigDecimal.valueOf(500_000);
        var response = sut.transfer(targetedCustomerAccount.getName(), transferAmount);
        assertTrue(response.getDebtAccounts().isEmpty());
        assertEquals(BigDecimal.valueOf(1_000_000), targetedCustomerAccount.getBalance());
        assertEquals(BigDecimal.valueOf(9_500_000), sut.getCurrentAccount().getBalance());
        assertEquals(sut.getCurrentAccount(), response.getCustomerAccount());
        assertFalse(response.getTransferList().isEmpty());
        assertEquals(transferAmount, response.getTransferList().get(0).getTransferAmount());
        assertTrue(response.getDebtAccounts().isEmpty());

        verify(accountRepositoryPort, atLeastOnce()).save(targetedCustomerAccount);
        verify(accountRepositoryPort, atLeastOnce()).save(sut.getCurrentAccount());
        verify(debtRepositoryPort, never()).save(any(DebtAccount.class));
    }

    @Test
    void transfer_toRegisteredAccountWithInsufficientBalance_willCreateDebtAccount() {
        var initialSourceBalance = registeredCustomer.getBalance();
        when(accountRepositoryPort.findCustomerByName("Yauri Attamimi")).thenReturn(Optional.of(registeredCustomer));
        sut.login("Yauri Attamimi");
        when(accountRepositoryPort.findCustomerByName("Ichigo Kurosaki"))
                .thenReturn(Optional.of(targetedCustomerAccount));
        var transferAmount = BigDecimal.valueOf(15_000_000);
        var response = sut.transfer(targetedCustomerAccount.getName(), transferAmount);
        assertFalse(response.getDebtAccounts().isEmpty());
        assertEquals(BigDecimal.valueOf(5_000_000), response.getDebtAccounts().get(0).getAmount());
        assertEquals(BigDecimal.valueOf(10_500_000), targetedCustomerAccount.getBalance());
        assertEquals(BigDecimal.ZERO, sut.getCurrentAccount().getBalance());
        assertEquals(sut.getCurrentAccount(), response.getCustomerAccount());
        assertEquals(initialSourceBalance, response.getTransferList().get(0).getTransferAmount());

        verify(accountRepositoryPort, atLeastOnce()).save(targetedCustomerAccount);
        verify(accountRepositoryPort, atLeastOnce()).save(sut.getCurrentAccount());
        verify(debtRepositoryPort, atLeastOnce()).save(any(DebtAccount.class));
    }

    @Test
    void withdraw_withoutLogin_shouldFailWithMessage() {
        Exception exception = assertThrows(DomainException.class, () -> sut.withdraw(BigDecimal.valueOf(500)));
        assertEquals("Please login first!", exception.getMessage());
    }

    @Test
    void withdraw_insufficientBalance_shouldFailWithMessage() {
        when(accountRepositoryPort.findCustomerByName("Yauri Attamimi")).thenReturn(Optional.of(registeredCustomer));
        sut.login("Yauri Attamimi");
        Exception exception = assertThrows(DomainException.class, () -> sut.withdraw(BigDecimal.valueOf(25_000_000)));
        assertEquals(String.format("Insufficient balance. Your current balance is $%s%n",
                registeredCustomer.getBalance()), exception.getMessage());
    }

    @Test
    void withdraw() {
        when(accountRepositoryPort.findCustomerByName("Yauri Attamimi")).thenReturn(Optional.of(registeredCustomer));
        sut.login("Yauri Attamimi");
        var response = sut.withdraw(BigDecimal.valueOf(300_000));
        assertEquals(registeredCustomer, response.getCustomerAccount());
        assertEquals(BigDecimal.valueOf(9_700_000), response.getCustomerAccount().getBalance());
        verify(accountRepositoryPort, atLeastOnce()).save(registeredCustomer);
    }

    @AfterEach
    void tearDown() {
        sut = null;
    }
}
