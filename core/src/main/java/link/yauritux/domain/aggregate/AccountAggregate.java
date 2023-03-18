package link.yauritux.domain.aggregate;

import link.yauritux.domain.entity.CustomerAccount;
import link.yauritux.domain.entity.CustomerDebt;
import link.yauritux.port.api.CustomerAccountServicePort;
import link.yauritux.port.spi.CustomerAccountRepositoryPort;
import link.yauritux.port.spi.CustomerDebtRepositoryPort;
import link.yauritux.sharedkernel.exception.DomainException;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

/**
 * @author yauritux@gmail.com
 * @version 1.0
 */
@RequiredArgsConstructor
public class AccountAggregate implements CustomerAccountServicePort {

    private final CustomerAccountRepositoryPort accountRepositoryPort;
    private final CustomerDebtRepositoryPort debtRepositoryPort;

    private CustomerAccount currentAccount;

    @Override
    public void login(final String name) {
        this.currentAccount = accountRepositoryPort.findCustomerByName(name)
                .orElse(new CustomerAccount(name));
        accountRepositoryPort.save(this.currentAccount);
    }

    @Override
    public void logout() {
        this.currentAccount = null;
    }

    @Override
    public void deposit(final BigDecimal depositAmount) {
        if (this.currentAccount == null) {
            throw new DomainException("Deposit failed! Please login first!!");
        }
        this.currentAccount.setBalance(this.currentAccount.getBalance().add(depositAmount));
        accountRepositoryPort.save(this.currentAccount);
    }

    @Override
    public BigDecimal transfer(String targetName, BigDecimal transferAmount) {
        if (this.currentAccount == null) {
            throw new DomainException("Please login first!");
        }
        var targetAccount = accountRepositoryPort.findCustomerByName(targetName);
        if (targetAccount.isEmpty()) {
            throw new DomainException("Target account does not exist!");
        }

        var destinationAccount = targetAccount.get();

        if (currentAccount.getBalance().compareTo(transferAmount) >= 0) {
            currentAccount.setBalance(currentAccount.getBalance().subtract(transferAmount));
            destinationAccount.setBalance(destinationAccount.getBalance().add(transferAmount));
            accountRepositoryPort.save(currentAccount);
            accountRepositoryPort.save(destinationAccount);
            return BigDecimal.ZERO;
        }

        var owedAmount = transferAmount.subtract(currentAccount.getBalance());
        currentAccount.setBalance(BigDecimal.ZERO);
        accountRepositoryPort.save(currentAccount);
        destinationAccount.setBalance(destinationAccount.getBalance().add(transferAmount));
        accountRepositoryPort.save(destinationAccount);
        debtRepositoryPort.save(new CustomerDebt(currentAccount.getName(), targetAccount.get().getName(), owedAmount));
        return owedAmount;
    }

    @Override
    public CustomerAccount getCurrentAccount() {
        return this.currentAccount;
    }
}
