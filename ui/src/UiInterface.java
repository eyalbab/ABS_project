public interface UiInterface {

    void Run();
    /*
    Executes program, show ui and interact with user
     */

    void showMenu();
    /*
    show interactive menu to the user with to following options
     */

    void loadFile();
    /*
    asks the user for XML file path (it may contain spaces such as "Program files" and loads
    the XML file to the system, following checks must be made:
    1)file finished with ".xml"
    2)something about category... need to check it again
    3)there's no loan with not existing customer
    4)loan payment divides okay(no remainder) with number of payments
    if there's exception don't crash return relevant info
    in a successful load system info will be charged and user will get "load succeeded" and yaz will restart to 1
     */

    void showLoansInfo();

    /*
    ****ONLY AFTER LOADING A FILE****
    show the following info about each loan in the system: loan identifier (name I think),
    loan owner name, category, loan sum, loan amount of time, loan interest, rate of payments
    status
    if status is pending: list of lenders names and investment, current sum, and amount left for active
    if status is active: all as pending and yaz it became active, and for each payment:
    yaz of payment, amount of loan fund, loan interest, and sum of both 
    */
    void showCustomerInfo();

    void deposit();

    void withdraw();

    void loanAssignment();

    void continueTimeline();

    void saveToDatFile();

    void loadFromDatFile();
}
