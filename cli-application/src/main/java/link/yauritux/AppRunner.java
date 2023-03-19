package link.yauritux;

import link.yauritux.adapter.in.cli.UserCliCommand;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * @author yauritux@gmail.com
 * @version 1.0
 */
public class AppRunner {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        while (true) {
            System.out.print("$ ");
            String line = input.nextLine();

            if (line.equalsIgnoreCase("exit")) {
                break;
            }

            var commands = line.split(" ");

            if (commands[0].equalsIgnoreCase("login")) {
                UserCliCommand.INSTANCE.login(commands.length == 2 ? commands[1] : null).forEach(System.out::println);
            } else if (commands[0].equalsIgnoreCase("deposit")) {
                try {
                    var depositAmount = commands.length == 2 ? new BigDecimal(commands[1]) : null;
                    UserCliCommand.INSTANCE.deposit(depositAmount).forEach(System.out::println);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid deposit amount. Please provide number!");
                    System.err.println("E.g. : deposit 100");
                    continue;
                }
            } else if (commands[0].equalsIgnoreCase("transfer")) {
                if (commands.length != 3) {
                    System.err.println("Usage: transfer [target] [amount]");
                    System.err.println("E.g. : transfer Bob 50");
                    continue;
                }
                try {
                    var transferAmount = new BigDecimal(commands[2]);
                    UserCliCommand.INSTANCE.transfer(commands[1], transferAmount).forEach(System.out::println);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid transfer amount. Please provide number!");
                    System.err.println("E.g. : transfer Bob 50");
                }
            } else if (commands[0].equalsIgnoreCase("logout")) {
                UserCliCommand.INSTANCE.logout().forEach(System.out::println);
            } else {
                System.err.println("Unrecognized command! Please use one of the following:");
                System.err.println("login [name] - Log in as this customer or creates the customer if not exist");
                System.err.println("deposit [amount] - Deposits this amount to the logged in customer");
                System.err.println("withdraw [amount] - Withdraws this amount from the logged in customer");
                System.err.println("transfer [target] [amount] - Transfers this amount from the logged in customer to the target customer");
                System.err.println("logout - Logs out of the current customer");
            }
        }
    }
}
