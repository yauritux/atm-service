package link.yauritux;

import link.yauritux.adapter.out.inmemory.InMemCustomerAccountRepository;
import link.yauritux.adapter.out.inmemory.InMemDebtAccountRepository;
import link.yauritux.domain.aggregate.AccountAggregate;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * @author yauritux@gmail.com
 * @version 1.0
 */
public class AppRunner {

    public static void main(String[] args) {

        AccountAggregate accountAggregate =
                new AccountAggregate(new InMemCustomerAccountRepository(), new InMemDebtAccountRepository());

        Scanner input = new Scanner(System.in);

        while (true) {
            System.out.print("$ ");
            String line = input.nextLine();

            if (line.equalsIgnoreCase("exit")) {
                break;
            }

            var commands = line.split(" ");

            if (commands[0].equalsIgnoreCase("login")) {
                var accountName = commands[1];
                var currentBalance = accountAggregate.login(commands[1]);
                System.out.printf("Hello, %s!\n", commands[1]);
                System.out.printf("Your balance is $%s\n", currentBalance);
            }

            if (commands[0].equalsIgnoreCase("deposit")) {
                var depositAmount = new BigDecimal(commands[1]);
                var response = accountAggregate.deposit(depositAmount);
                response.getTransferList().forEach(t -> {
                    System.out.printf("Transferred $%s to %s\n", t.getTransferAmount(), t.getTargetAccount().getName());
                });
                System.out.printf("Your balance is $%s\n", response.getCustomerAccount().getBalance());
                response.getDebtAccounts().forEach(da -> {
                    System.out.printf("Owed $%s to %s\n", da.getAmount(), da.getCreditorAccountName());
                });
            }

            if (commands[0].equalsIgnoreCase("logout")) {
                if (accountAggregate.getCurrentAccount() == null) {
                    System.out.println("You've logged-out already!\n");
                    continue;
                }
                var currentLoggedInName = accountAggregate.getCurrentAccount().getName();
                accountAggregate.logout();
                System.out.printf("Goodbye, %s!\n", currentLoggedInName);
            }

            if (commands[0].equalsIgnoreCase("transfer")) {
                var targetAccount = commands[1];
                var transferAmount = new BigDecimal(commands[2]);
                var owedAccount = accountAggregate.transfer(targetAccount, transferAmount);
                System.out.printf("Transferred $%s to %s\n", transferAmount, targetAccount);
                System.out.printf("your balance is $%s\n", accountAggregate.getCurrentAccount().getBalance());
                if (owedAccount.compareTo(BigDecimal.ZERO) > 0) {
                    System.out.printf("Owed $%s to %s\n", owedAccount, targetAccount);
                }
            }
        }
    }
}
