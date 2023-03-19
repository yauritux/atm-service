package link.yauritux.adapter.in.cli;

import link.yauritux.adapter.out.inmemory.InMemCustomerAccountRepository;
import link.yauritux.adapter.out.inmemory.InMemDebtAccountRepository;
import link.yauritux.domain.aggregate.AccountAggregate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that represent all possible CLI commands invoked by our user.
 *
 * @author yauritux@gmail.com
 * @version 1.0
 */
public class UserCliCommand {

    private final AccountAggregate accountAggregate =
            new AccountAggregate(new InMemCustomerAccountRepository(), new InMemDebtAccountRepository());

    public static final UserCliCommand INSTANCE = new UserCliCommand();

    /**
     * user login command
     */
    public List<String> login(String name) {
        List<String> cmdResponse = new ArrayList<>();
        try {
            var currentBalance = accountAggregate.login(name);
            cmdResponse.add(String.format("Hello, %s!%n", name));
            cmdResponse.add(String.format("Your balance is $%s%n", currentBalance));
        } catch (Exception e) {
            cmdResponse.add(e.getMessage());
        }
        return cmdResponse;
    }

    public List<String> logout() {
        List<String> cmdResponse = new ArrayList<>();
        if (accountAggregate.getCurrentAccount() == null) {
            cmdResponse.add("You've logged-out already!%n");
            return cmdResponse;
        }
        var currentLoggedInName = accountAggregate.getCurrentAccount().getName();
        accountAggregate.logout();
        cmdResponse.add(String.format("Goodbye, %s!%n", currentLoggedInName));
        return cmdResponse;
    }

    public List<String> deposit(BigDecimal amount) {
        List<String> cmdResponse = new ArrayList<>();
        try {
            var response = accountAggregate.deposit(amount);
            response.getTransferList().forEach(t ->
                    cmdResponse.add(String.format(
                            "Transferred $%s to %s%n", t.getTransferAmount(), t.getTargetAccount().getName())));
            cmdResponse.add(String.format("Your balance is $%s%n", response.getCustomerAccount().getBalance()));
            response.getDebtAccounts().forEach(da ->
                    cmdResponse.add(String.format("Owed $%s to %s%n", da.getAmount(), da.getCreditorAccountName())));
        } catch (Exception e) {
            cmdResponse.add(e.getMessage());
        }
        return cmdResponse;
    }

    public List<String> transfer(String targetAccountName, BigDecimal transferAmount) {
        List<String> cmdResponse = new ArrayList<>();
        try {
            var response = accountAggregate.transfer(targetAccountName, transferAmount);
            response.getTransferList().forEach(t -> {
                if (t.getTargetAccount().getName().equalsIgnoreCase(targetAccountName)) {
                    cmdResponse.add(String.format("Transferred $%s to %s%n", t.getTransferAmount(), targetAccountName));
                }
            });
            cmdResponse.add(String.format("your balance is $%s%n", accountAggregate.getCurrentAccount().getBalance()));
            response.getDebtAccounts().forEach(debtAccount ->
                    cmdResponse.add(String.format(
                            "Owed $%s to %s%n", debtAccount.getAmount(), debtAccount.getCreditorAccountName())));
        } catch (Exception e) {
            cmdResponse.add(e.getMessage());
        }
        return cmdResponse;
    }

    private UserCliCommand() {}
}
