package link.yauritux.domain.aggregate;

import link.yauritux.domain.entity.CustomerAccount;
import link.yauritux.domain.entity.DebtAccount;
import link.yauritux.domain.valueobject.TransactionResponse;
import link.yauritux.domain.valueobject.TransferDto;
import link.yauritux.port.in.CustomerAccountServicePort;
import link.yauritux.port.out.CustomerAccountRepositoryPort;
import link.yauritux.port.out.DebtAccountRepositoryPort;
import link.yauritux.sharedkernel.exception.DomainException;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author yauritux@gmail.com
 * @version 1.0
 */
@RequiredArgsConstructor
public class AccountAggregate implements CustomerAccountServicePort {

    private final CustomerAccountRepositoryPort accountRepositoryPort;
    private final DebtAccountRepositoryPort debtRepositoryPort;

    private CustomerAccount currentAccount;

    @Override
    public BigDecimal login(final String name) {
        this.currentAccount = accountRepositoryPort.findCustomerByName(name)
                .orElse(new CustomerAccount(name));
        accountRepositoryPort.save(this.currentAccount);
        return this.currentAccount.getBalance();
    }

    @Override
    public void logout() {
        this.currentAccount = null;
    }

    @Override
    public TransactionResponse deposit(final BigDecimal depositAmount) {
        if (this.currentAccount == null) {
            throw new DomainException("Deposit failed! Please login first!!");
        }
        var depositResponse = new TransactionResponse();
        depositResponse.setCustomerAccount(this.currentAccount);
        List<DebtAccount> debtRecords = debtRepositoryPort.findByDebtorAccount(this.currentAccount.getName());
        if (debtRecords == null || debtRecords.isEmpty()) {
            this.currentAccount.setBalance(this.currentAccount.getBalance().add(depositAmount));
            accountRepositoryPort.save(this.currentAccount);
        } else {
            for (DebtAccount da : debtRecords) {
                var debtAmount = da.getAmount();
                if (debtAmount.compareTo(depositAmount) >= 0) { // debt (owed) amount is greater than current deposit amount
                    // update debtor's balance
                    this.currentAccount.setBalance(BigDecimal.ZERO);
                    accountRepositoryPort.save(this.currentAccount);
                    depositResponse.setCustomerAccount(this.currentAccount); // update debtor's account balance

                    // update creditor's balance
                    var creditorAccount =
                            accountRepositoryPort.findCustomerByName(da.getCreditorAccountName());
                    if (creditorAccount.isPresent()) {
                        creditorAccount.get().setBalance(creditorAccount.get().getBalance().add(depositAmount));
                        accountRepositoryPort.save(creditorAccount.get());
                        depositResponse.addTransfer(new TransferDto(creditorAccount.get(), debtAmount.subtract(depositAmount)));
                    }

                    // calculate and update the remaining debts (owed amount)
                    var remainedDebtAmount = debtAmount.subtract(depositAmount);
                    da.setAmount(remainedDebtAmount);
                    if (da.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                        debtRepositoryPort.remove(da);
                    } else {
                        debtRepositoryPort.save(da);
                    }
                    break;
                } else { // deposit amount is greater than current debt (owed) amount
                    var remainedDeposit = depositAmount.subtract(debtAmount);
                    this.currentAccount.setBalance(this.currentAccount.getBalance().add(remainedDeposit));
                    accountRepositoryPort.save(this.currentAccount);
                    da.setAmount(BigDecimal.ZERO);
                    debtRepositoryPort.remove(da);
                }
            }
        }
        if (debtRecords != null && !debtRecords.isEmpty()) {
            depositResponse.setDebtAccounts(debtRecords);
        }
        return depositResponse;
    }

    @Override
    public TransactionResponse transfer(String targetName, BigDecimal transferAmount) {
        if (this.currentAccount == null) {
            throw new DomainException("Please login first!");
        }
        var targetAccount = accountRepositoryPort.findCustomerByName(targetName);
        if (targetAccount.isEmpty()) {
            throw new DomainException("Target account does not exist!");
        }

        var transferredResponse = new TransactionResponse();
        transferredResponse.setCustomerAccount(this.currentAccount);

        List<DebtAccount> debtRecords = debtRepositoryPort.findByDebtorAccount(this.currentAccount.getName());
        if (debtRecords != null && !debtRecords.isEmpty()) {
            transferredResponse.setDebtAccounts(debtRecords);
        }

        var destinationAccount = targetAccount.get();

        if (currentAccount.getBalance().compareTo(transferAmount) >= 0) { // current balance is greater than transfer amount (balance sufficient)
            currentAccount.setBalance(currentAccount.getBalance().subtract(transferAmount));
            destinationAccount.setBalance(destinationAccount.getBalance().add(transferAmount));
            accountRepositoryPort.save(currentAccount);
            accountRepositoryPort.save(destinationAccount);

            // setup response
            transferredResponse.setCustomerAccount(currentAccount);
            transferredResponse.setTransferList(List.of(new TransferDto(destinationAccount, transferAmount)));

            return transferredResponse;
        }

        // balance is not sufficient (i.e., current balance is less than transfer amount)
        var realTransferAmount = currentAccount.getBalance();
        var owedAmount = transferAmount.subtract(currentAccount.getBalance());
        currentAccount.setBalance(BigDecimal.ZERO);
        accountRepositoryPort.save(currentAccount);

        // setup debt account
        var newDebtAccount = new DebtAccount(currentAccount.getName(), destinationAccount.getName(), owedAmount);

        // setup response
        transferredResponse.setCustomerAccount(currentAccount);
        transferredResponse.setTransferList(List.of(new TransferDto(destinationAccount, realTransferAmount)));
        transferredResponse.setDebtAccounts(List.of(newDebtAccount));

        destinationAccount.setBalance(destinationAccount.getBalance().add(transferAmount));
        accountRepositoryPort.save(destinationAccount);
        debtRepositoryPort.save(newDebtAccount);
        return transferredResponse;
    }

    @Override
    public CustomerAccount getCurrentAccount() {
        return this.currentAccount;
    }
}
