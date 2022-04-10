import exception.AbsException;
import system.MySystem;
import utils.ABSUtils;

import javax.xml.bind.JAXBException;
import java.util.Scanner;

import static java.lang.System.exit;

public class ConsoleUiImpl implements UiInterface {

    private MySystem mySystem;

    public static void main(String[] args) {
        UiInterface ui = new ConsoleUiImpl();
        ui.Run();

    }

    @Override
    public void Run() {
        showMenu();
    }

    @Override
    public void showMenu() {
        String menu = String.format("1) Load XML file.%n" +
                "2) Show loans information.%n" +
                "3) Show customer information.%n" +
                "4) Deposit to account.%n" +
                "5) Withdraw from account.%n" +
                "6) Loan assignment.%n" +
                "7) Continue timeline%n" +
                "8) Exit program.%n");
        System.out.println(menu);
        System.out.println("Enter number between 1-8:");
        handleUserMenuInput();
    }

    private void handleUserMenuInput() {
        Scanner userInput = new Scanner(System.in);
        int menuPick = ABSUtils.tryParseIntAndValidateRange(userInput.nextLine(), 1, 8);
        while (menuPick > 8 || menuPick < 1) {
            System.out.println("invalid argument, please enter number between 1-8");
            menuPick = ABSUtils.tryParseIntAndValidateRange(userInput.nextLine(), 1, 8);
        }
        switch (menuPick) {
            case 1:
                loadFile();
                break;

            case 2:
                //show loans info
                System.out.println("chose 2");
                break;

            case 3:
                if (mySystem.getLoaded()) {
                    showCustomerInfo();
                } else {
                    System.out.println("Load A file first");
                }
                break;

            case 4:
                if (mySystem.getLoaded()) {
                    deposit();
                } else {
                    System.out.println("Load A file first");
                }
                break;

            case 5:
                if (mySystem.getLoaded()) {
                    //withdraw
                } else {
                    System.out.println("Load A file first");
                }
                break;

            case 6:
                if (mySystem.getLoaded()) {
                    //Loan Assign
                } else {
                    System.out.println("Load A file first");
                }
                break;

            case 7:
                if (mySystem.getLoaded()) {
                    //Continue timelien
                } else {
                    System.out.println("Load A file first");
                }
                break;
            case 8:
                exit(0);
                break;

        }
        showMenu();
    }

    @Override
    public void loadFile() {
        System.out.println("Please enter XML file path:");
        Scanner userInput = new Scanner(System.in);
        try {
            mySystem = MySystem.LoadFile(userInput.nextLine());
            System.out.println("Successfully loaded a file!");
        } catch (JAXBException e) {
            System.out.println("something wrong with your file");
        } catch (AbsException e) {
            System.out.println(e.getErrorMsg());
        }
        showMenu();
    }

    @Override
    public void showLoansInfo() {
        System.out.println(mySystem.getLoans());
    }

    @Override
    public void showCustomerInfo() {
        System.out.println(mySystem.getAllCustomers());
    }

    @Override
    public void deposit() {
        System.out.println(mySystem.getAllCustomers().showAllCustomersNameByOrder());
        System.out.println("Please enter customer name you wish to deposit to:");
        Scanner userInput = new Scanner(System.in);
        String userInputName = userInput.nextLine();
        System.out.println("Enter amount to Deposit");
        String userInputSum = userInput.nextLine();
        System.out.println(mySystem.handleDeposit(userInputName, userInputSum));

    }
}
