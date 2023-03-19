package link.yauritux;

import link.yauritux.adapter.out.InMemCustomerAccountRepository;
import link.yauritux.adapter.out.InMemCustomerDebtRepository;
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
                new AccountAggregate(new InMemCustomerAccountRepository(), new InMemCustomerDebtRepository());

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
                var lastBalance = accountAggregate.deposit(depositAmount);
                System.out.printf("Your balance is $%s\n", lastBalance);
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
