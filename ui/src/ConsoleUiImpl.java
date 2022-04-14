import customer.Customer;
import exception.AbsException;
import loan.Loan;
import system.MySystem;
import utils.ABSUtils;

import javax.xml.bind.JAXBException;
import java.util.List;
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
        mySystem = new MySystem();
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
                "8) Exit program.%n" +
                "9) Save to DAT file.%n" +
                "10) Load from DAT file.%n");
        System.out.println(menu);
        System.out.println("Enter number between 1-10:");
        handleUserMenuInput();
    }

    private void handleUserMenuInput() {
        Scanner userInput = new Scanner(System.in);
        int menuPick = ABSUtils.tryParseIntAndValidateRange(userInput.nextLine(), 1, 10);
        while (menuPick > 10 || menuPick < 1) {
            System.out.println("invalid argument, please enter number between 1-10");
            menuPick = ABSUtils.tryParseIntAndValidateRange(userInput.nextLine(), 1, 10);
        }
        switch (menuPick) {
            case 1:
                loadFile();
                break;

            case 2:
                if (mySystem.getLoaded()) {
                    showLoansInfo();
                } else {
                    System.out.println("Load A file first");
                }
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
                    withdraw();
                } else {
                    System.out.println("Load A file first");
                }
                break;

            case 6:
                if (mySystem.getLoaded()) {
                    loanAssignment();
                } else {
                    System.out.println("Load A file first");
                }
                break;

            case 7:
                if (mySystem.getLoaded()) {
                    continueTimeline();
                } else {
                    System.out.println("Load A file first");
                }
                break;
            case 8:
                exit(0);
                break;
            case 9:
                if (mySystem.getLoaded()) {
                    saveToDatFile();
                } else {
                    System.out.println("Load A file first");
                }
                break;
            case 10:
                loadFromDatFile();
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
        System.out.println("Please enter customer index you wish to deposit to:");
        Scanner userInput = new Scanner(System.in);
        String userInputName = userInput.nextLine();
        System.out.println("Enter amount to Deposit:");
        String userInputSum = userInput.nextLine();
        System.out.println(mySystem.handleDeposit(userInputName, userInputSum));

    }

    public void withdraw() {
        System.out.println(mySystem.getAllCustomers().showAllCustomersNameByOrder());
        System.out.println("Please enter customer index you wish to withdraw from:");
        Scanner userInput = new Scanner(System.in);
        String userInputName = userInput.nextLine();
        System.out.println("Enter amount to withdraw:");
        String userInputSum = userInput.nextLine();
        System.out.println(mySystem.handleWithdraw(userInputName, userInputSum));
    }

    @Override
    public void loanAssignment() {
        System.out.println(mySystem.getAllCustomers().showAllCustomersNameByOrder());
        System.out.println("Please enter customer index you wish to assign loan:");
        Scanner userInput = new Scanner(System.in);
        String customerIndex = userInput.nextLine();
        System.out.println("Enter sum your willing to invest");
        String sumToInvest = userInput.nextLine();
        System.out.println(mySystem.getCategories() + "Choose categories by their index, seperated with comma (e.g 1,2,3) or press Enter for all");
        String categoriesPick = userInput.nextLine();
        System.out.println("Enter minimum interest percentage you wish to profit(e.g 27) or press Enter for any: ");
        String minInterest = userInput.nextLine();
        System.out.println("Enter minimum loan total yaz time: ");
        String minYazTime = userInput.nextLine();
        try {
            List<Loan> optionalLoans = mySystem.showSuggestedLoans(customerIndex, sumToInvest, categoriesPick, minInterest, minYazTime);
            if (optionalLoans.isEmpty()) {
                System.out.println("There are no matching loans in system");
                return;
            }
            System.out.println("The relevant loans in the system are:\n");
            int i = 1;
            for (Loan loan : optionalLoans) {
                System.out.print(i++ + ". ");
                System.out.println(Customer.loanInfoForCustomer(loan));
            }
            System.out.println("Please pick the wanted loan by pressing the loan index");
            String pickedLoansString = userInput.nextLine();
            List<Loan> pickedLoans = mySystem.handleLoansPick(pickedLoansString, optionalLoans);
            if (pickedLoans.isEmpty()) {
                System.out.println("Invalid loans pick");
                return;
            }
            Customer assignTo = mySystem.getAllCustomers().getAllCustomers().get(Integer.parseInt(customerIndex) - 1);
            mySystem.assignLoans(assignTo, pickedLoans, Integer.parseInt(sumToInvest));
            System.out.println("Loans has assigned successfully");
        } catch (AbsException e) {
            System.out.println(e.getErrorMsg());
        }
    }

    @Override
    public void continueTimeline() {
        System.out.print("Previous yaz is: " + mySystem.getYaz());
        mySystem.continueTimeline();
        System.out.println(", Current yaz is: " + mySystem.getYaz());
    }

    @Override
    public void saveToDatFile() {
        System.out.println("Enter new dat file path: ");
        Scanner userInput = new Scanner(System.in);
        String askedPath = userInput.nextLine();
        System.out.println(mySystem.saveToDatFile(askedPath));
    }

    @Override
    public void loadFromDatFile() {
        System.out.println("Enter dat file path to load: ");
        Scanner userInput = new Scanner(System.in);
        String askedPath = userInput.nextLine();
        MySystem createdSys = mySystem.loadFromDatFile(askedPath);
        if (createdSys == null) {
            System.out.println("Load was NOT successful");
        } else {
            mySystem = createdSys;
            System.out.println("Load was successful");
        }
    }
}
