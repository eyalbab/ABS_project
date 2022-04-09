import utils.ABSUtils;

import java.util.Scanner;

public class ConsoleUiImpl implements UiInterface {

    private System mySystem;
    public static void main(String[] args) {
        UiInterface ui = new ConsoleUiImpl();
        ui.Run();

    }

    @Override
    public void Run() {
        showMenu();
        handleUserMenuInput();
    }

    @Override
    public void showMenu() {
        String menu =String.format("1) Load XML file.%n" +
                "2) Show loans information.%n" +
                "3) Show customer information.%n" +
                "4) Deposit to account.%n" +
                "5) Withdraw from account.%n" +
                "6) Loan assignment.%n" +
                "7) Continue timeline%n" +
                "8) Exit program.%n");
        System.out.println(menu);
        System.out.println("Enter number between 1-8:");

    }

    private void handleUserMenuInput() {
        Scanner userInput = new Scanner(System.in);
        int menuPick = ABSUtils.tryParseIntAndValidateRange(userInput.nextLine(), 1,8);
        while (menuPick > 8 || menuPick < 1) {
            System.out.println("invalid argument, please enter number between 1-8");
            menuPick = ABSUtils.tryParseIntAndValidateRange(userInput.nextLine(), 1,8);
        }
        switch (menuPick) {
            case 1:
                //load xml file
                System.out.println("chose 1");
                break;

            case 2:
                //show loans info
                System.out.println("chose 2");
                break;

            case 3:
                // show customer
                System.out.println("chose 3");
                break;

            case 4:
                // deposit
                System.out.println("chose 4");
                break;

            case 5:
                //withdraw
                System.out.println("chose 5");
                break;

            case 6:
                //loan assignment
                System.out.println("chose 6");
                break;
            case 7:
                // continue timeline
                System.out.println("chose 7");
                break;
            case 8:
                //exit
                System.out.println("chose 8");
                break;

        }
    }

    @Override
    public void loadFile() {

    }

    @Override
    public void showLoansInfo() {

    }

    @Override
    public void showCustomerInfo() {

    }

    @Override
    public void deposit() {

    }
}
