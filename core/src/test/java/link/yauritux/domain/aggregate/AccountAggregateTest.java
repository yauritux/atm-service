package link.yauritux.domain.aggregate;

import link.yauritux.domain.entity.CustomerAccount;
import link.yauritux.domain.entity.CustomerDebt;
import link.yauritux.port.spi.CustomerAccountRepositoryPort;
import link.yauritux.port.spi.CustomerDebtRepositoryPort;
import link.yauritux.sharedkernel.exception.DomainException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
    private CustomerDebtRepositoryPort debtRepositoryPort;

    private final CustomerAccount registeredCustomer =
            new CustomerAccount("Yauri Attamimi", BigDecimal.valueOf(10_000_000));

    private final CustomerAccount targetedCustomerAccount =
            new CustomerAccount("Ichigo Kurosaki", BigDecimal.valueOf(500_000));

    @BeforeEach
    void setUp() {
        sut = new AccountAggregate(accountRepositoryPort, debtRepositoryPort);
    }

    @Test
    void loginWithEmptyCustomerAccountName() {
        when(accountRepositoryPort.findCustomerByName(anyString())).thenReturn(Optional.empty());
        verify(accountRepositoryPort, never()).save(any());
        Exception exception = assertThrows(DomainException.class, () -> sut.login(" "));
        assertEquals("Customer account name cannot be empty!", exception.getMessage());
    }

    @Test
    void loginWithNonExistingAccount() {
        when(accountRepositoryPort.findCustomerByName("Uzumaki Naruto")).thenReturn(Optional.empty());
        var currentBalance = sut.login("Uzumaki Naruto");
        assertEquals(BigDecimal.ZERO, sut.getCurrentAccount().getBalance());
        assertEquals(BigDecimal.ZERO, currentBalance);
        verify(accountRepositoryPort, atLeastOnce()).save(any(CustomerAccount.class));
    }

    @Test
    void loginWithExistingAccount() {
        when(accountRepositoryPort.findCustomerByName("Yauri Attamimi")).thenReturn(Optional.of(registeredCustomer));
        var currentBalance = sut.login("Yauri Attamimi");
        assertEquals("Yauri Attamimi", sut.getCurrentAccount().getName());
        assertEquals(BigDecimal.valueOf(10_000_000), sut.getCurrentAccount().getBalance());
        assertEquals(BigDecimal.valueOf(10_000_000), currentBalance);
        verify(accountRepositoryPort, atLeastOnce()).save(any(CustomerAccount.class));
    }

    @Test
    void deposit10DollarToARegisteredAccount() {
        when(accountRepositoryPort.findCustomerByName("Yauri Attamimi")).thenReturn(Optional.of(registeredCustomer));
        sut.login("Yauri Attamimi");
        var lastBalance = sut.deposit(BigDecimal.TEN);
        assertEquals(BigDecimal.valueOf(10_000_010), registeredCustomer.getBalance());
        assertEquals(registeredCustomer.getBalance(), lastBalance);
        verify(accountRepositoryPort, atLeastOnce()).save(any(CustomerAccount.class));
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
    void transferWithoutLogin() {
        Exception exception = assertThrows(DomainException.class,
                () -> sut.transfer("Ichigo Kurosaki", BigDecimal.valueOf(50)));
        assertEquals("Please login first!", exception.getMessage());
    }

    @Test
    void transferToUnregisteredTargetAccount() {
        sut.login("Yauri Attamimi");
        when(accountRepositoryPort.findCustomerByName("Natsu Dragneel")).thenReturn(Optional.empty());
        Exception exception = assertThrows(DomainException.class,
                () -> sut.transfer("Natsu Dragneel", BigDecimal.valueOf(100_000)));
        assertEquals("Target account does not exist!", exception.getMessage());
    }

    @Test
    void transferToRegisteredAccountWithSufficientBalance() {
        when(accountRepositoryPort.findCustomerByName("Yauri Attamimi")).thenReturn(Optional.of(registeredCustomer));
        sut.login("Yauri Attamimi");
        when(accountRepositoryPort.findCustomerByName("Ichigo Kurosaki"))
                .thenReturn(Optional.of(targetedCustomerAccount));
        var transferAmount = BigDecimal.valueOf(500_000);
        var owedAmount = sut.transfer(targetedCustomerAccount.getName(), transferAmount);
        assertEquals(BigDecimal.ZERO, owedAmount);
        assertEquals(BigDecimal.valueOf(1_000_000), targetedCustomerAccount.getBalance());
        assertEquals(BigDecimal.valueOf(9_500_000), sut.getCurrentAccount().getBalance());

        verify(accountRepositoryPort, atLeastOnce()).save(targetedCustomerAccount);
        verify(accountRepositoryPort, atLeastOnce()).save(sut.getCurrentAccount());
        verify(debtRepositoryPort, never()).save(any());
    }

    @Test
    void transferToRegisteredAccountWithInsufficientBalance() {
        when(accountRepositoryPort.findCustomerByName("Yauri Attamimi")).thenReturn(Optional.of(registeredCustomer));
        sut.login("Yauri Attamimi");
        when(accountRepositoryPort.findCustomerByName("Ichigo Kurosaki"))
                .thenReturn(Optional.of(targetedCustomerAccount));
        var transferAmount = BigDecimal.valueOf(15_000_000);
        var owedAmount = sut.transfer(targetedCustomerAccount.getName(), transferAmount);
        assertEquals(BigDecimal.valueOf(5_000_000), owedAmount);
        assertEquals(BigDecimal.valueOf(15_500_000), targetedCustomerAccount.getBalance());
        assertEquals(BigDecimal.ZERO, sut.getCurrentAccount().getBalance());

        verify(accountRepositoryPort, atLeastOnce()).save(targetedCustomerAccount);
        verify(accountRepositoryPort, atLeastOnce()).save(sut.getCurrentAccount());
        verify(debtRepositoryPort, atLeastOnce()).save(any(CustomerDebt.class));
    }

    @AfterEach
    void tearDown() {
        sut = null;
    }
}
