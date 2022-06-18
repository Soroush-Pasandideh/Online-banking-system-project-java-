import java.io.*;
import java.net.Socket;
import java.util.Scanner;

class ClientHandler extends Thread implements Serializable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    Client currentClient;
    Scanner sc;

    public ClientHandler() {
        try {
            sc = new Scanner(System.in);
            clientSocket = new Socket("127.0.0.1", 4545);
            System.out.println("connected");
            in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    clientSocket.getOutputStream())), true);
            start();
            System.out.println("started");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {

            System.out.println("*** well come ***\n");
            System.out.println("1)sign up\n" +
                    "2)login");
            accountStatus(signOrLog());

        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int signOrLog() {

        int clientStatus;
        do {
            clientStatus = sc.nextInt();
            if (clientStatus != 1 && clientStatus != 2)
                System.out.println("invalid order! 1 or 2 ?");
        } while (clientStatus != 1 && clientStatus != 2);

        if (clientStatus == 1) {
            signUp();
        }
        if (clientStatus == 2) {
            logIn();
        }
        return clientStatus;
    }

    public void signUp() {
        try {
            out.println("1");
            String isIdentificationCodeNew;
            do {
                System.out.println("identification code: (10 digits)");
                String identificationCode;
                identificationCode = checkIdentificationCode();
                out.println(identificationCode);
                isIdentificationCodeNew = in.readLine();
                if (isIdentificationCodeNew.equals("f"))
                    System.out.println("this ID is used before! choose another.");
            } while (isIdentificationCodeNew.equals("f"));
            System.out.println("password: (at least 8 characters) ");
            String password;
            password = checkPassword();
            out.println(password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logIn() {
        out.println("2");
        try {
            do {
                System.out.println("identification code: (10 digits) ");
                String identificationCode;
                identificationCode = checkIdentificationCode();
                System.out.println("password: (at least 8 characters)");
                String password;
                password = checkPassword();
                out.println(identificationCode);
                out.println(password);
                String result = in.readLine();
                if (result.equals("t")) {
                    System.out.println("correct password");
                    break;
                }
                if (result.equals("f")) {
                    System.out.println("wrong password !");

                } else {
                    System.out.println(result);

                }
            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void accountStatus(int clientStatus) {

        if (clientStatus == 1) {

            continueSignUp();

        }
        if (clientStatus == 2) {

            continueLogIn();

        }
    }

    public void continueSignUp() {
        int addOrChoose;
        System.out.println("\n1)add account");

        do {
            addOrChoose = sc.nextInt();
            if (addOrChoose != 1)
                System.out.println("now you can just enter 1!");
        } while (addOrChoose != 1);

        out.println(addOrChoose);
        System.out.println("\naccount id: (16 digits)");
        String accountId;
        accountId = checkAccountId();
        System.out.println("account password: (4 digits)");
        String accountPassword;
        accountPassword = checkAccountPassword();
        out.println(accountId);
        out.println(accountPassword);

        menu();
    }

    public void continueLogIn() {
        int addOrChoose;

        System.out.println("1)add account\n" +
                "2)choose account");

        do {
            addOrChoose = sc.nextInt();
            if (addOrChoose != 1 && addOrChoose != 2)
                System.out.println("invalid order! 1 or 2?");
        } while (addOrChoose != 1 && addOrChoose != 2);

        if (addOrChoose == 1) {

            addAccount();

        }
        if (addOrChoose == 2) {

            chooseAccount();

        }
        menu();
    }

    public void addAccount() {

        out.println("1");
        System.out.println("account id: (16 digits)");
        String accountId;
        accountId = checkAccountId();
        System.out.println("account password: (4 digits)");
        String accountPassword;
        accountPassword = checkAccountPassword();
        out.println(accountId);
        out.println(accountPassword);

    }

    public void chooseAccount() {
        try {
            out.println("2");
            String accountsNum = in.readLine();
            String[] accountIdArr = new String[Integer.parseInt(accountsNum)];
            String accountIdInMap;
            for (int i = 0; i < Integer.parseInt(accountsNum); i++) {
                accountIdInMap = in.readLine();
                System.out.println((i + 1) + ") " + accountIdInMap);
                accountIdArr[i] = accountIdInMap;
            }
            int chosenAccount;
            chosenAccount = checkChosenAccount(accountsNum);

            out.println(accountIdArr[chosenAccount - 1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void menu() {
        int amount;
        String order;
        boolean wannaRequest = true;
        while (wannaRequest) {

            showMenu();

            order = sc.next();
            amount = 0;
            switch (order) {
                case "1":
                    out.println("1");
                    try {
                        System.out.println("your balance is: " + in.readLine());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case "2":
                    out.println("2");
                    System.out.println("enter the amount: ");
                    amount = sc.nextInt();
                    out.println(amount);
                    try {
                        String result = in.readLine();
                        if (result.equals("1"))
                            System.out.println("withdraw is done!");
                        else
                            System.out.println("not enough balance!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case "3":
                    out.println("3");
                    System.out.println("enter the amount: ");
                    amount = sc.nextInt();
                    out.println(amount);
                    try {
                        System.out.println(in.readLine());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case "4":
                    out.println("4");
                    System.out.println("transaction History: ");
                    try {
                        int transActionHistorySize = Integer.parseInt(in.readLine());
                        for (int i = 0; i < transActionHistorySize; i++) {
                            String type = in.readLine();
                            String thisAmount = in.readLine();
                            System.out.println(type + ": " + thisAmount);
                            if (in.readLine().equals("1")) {
                                String billingID = in.readLine();
                                String paymentCode = in.readLine();
                                System.out.println("   billing ID: " + billingID);
                                System.out.println("   paymentCode: " + paymentCode);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case "5":
                    out.println("5");
                    System.out.println("enter the billing ID:");//شناسه قبض
                    String billingID = sc.next();
                    System.out.println("enter the payment code:");//شناسه پرداخت
                    String paymentCode = sc.next();
                    System.out.println("enter the bill amount:");
                    amount = sc.nextInt();
                    out.println(amount);

                    try {
                        String result = in.readLine();
                        if (result.equals("1")) {
                            out.println(billingID);
                            out.println(paymentCode);
                            System.out.println("paying bill is done!");
                        } else
                            System.out.println("not enough balance!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case "01":
                    out.println("01");
                    chooseAccount();
                    break;

                case "02":
                    out.println("02");
                    addAccount();
                    break;

                case "03":
                    out.println("03");
                    try {
                        int accountsNumber = Integer.parseInt(in.readLine());
                        System.out.println(accountsNumber);
                        for (int i = 0; i < accountsNumber; i++) {
                            String accountId = in.readLine();
                            String accountUsage = in.readLine();
                            System.out.println(i + 1 + ") ID: " + accountId + "    usage: " + accountUsage);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "04":
                    out.println("04");
                    try {
                        String accountId = in.readLine();
                        System.out.println("Are you sure wanna DELETE " + accountId + " account ?\n"
                                + "1)yes\n"
                                + "2)no\n");
                        String answer = sc.next();
                        if (answer.equals("1")) {
                            out.println("1");   //yes
                            System.out.println("for transferring the balance make an account or choose an account:");

                            int accountsNumber = Integer.parseInt(in.readLine());
                            System.out.println(accountsNumber);
                            for (int i = 0; i < accountsNumber; i++) {
                                String thisAccountId = in.readLine();
                                String thisAccountUsage = in.readLine();
                                System.out.println((i + 1) + "- ID: " + thisAccountId + "    usage: " + thisAccountUsage);
                            }
                            System.out.println("1)choose account");
                            System.out.println("2)add account");
                            if (sc.next().equals("1")) {
                                out.println("1");
                                System.out.println("enter the destination account ID:");
                                String destinationAccount=sc.next();
                                out.println(destinationAccount);
                            }
                            else { // if 2
                                out.println("2");
                                addAccount();
                            }
                        }
                        else // if 2 == no
                            out.println("2"); //no
                        if (in.readLine().equals("1")) {
                            System.out.println("balance transferred successfully!");
                            System.out.println("account " + accountId + " deleted successfully!");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "6":
                    out.println("6");
                    wannaRequest = false;
                    break;

                default:
                    System.out.println("invalid request");
            }
        }
    }

    public boolean isDigit(String num) {
        for (int i = 0; i < num.length(); i++) {
            if (!Character.isDigit(num.charAt(i)))
                return false;
        }
        return true;
    }

    public String checkIdentificationCode() {
        String identificationCode;
        do {
            do {
                identificationCode = sc.next();
                if (identificationCode.length() != 10)
                    System.out.println("it should be 10 digits! try again:");

            } while (identificationCode.length() != 10);

            if (!isDigit(identificationCode))
                System.out.println("it just can be a number! enter another:");
        } while (!isDigit(identificationCode));
        return identificationCode;
    }

    public String checkPassword() {
        String password;
        do {
            password = sc.next();
            if (password.length() < 8) {
                System.out.println("too short! enter another:");
            }
        } while (password.length() < 8);
        return password;
    }

    public String checkAccountId() {
        String accountId;
        do {
            do {
                accountId = sc.next();
                if (accountId.length() != 16)
                    System.out.println("it should be 16 digits! try again:");

            } while (accountId.length() != 16);
            if (!isDigit(accountId))
                System.out.println("it just can be a number! enter another:");
        } while (!isDigit(accountId));
        return accountId;
    }


    public String checkAccountPassword() {
        String accountPassword;
        do {
            do {
                accountPassword = sc.next();
                if (accountPassword.length() != 4)
                    System.out.println("it should be 4 digits! try again:");

            } while (accountPassword.length() != 4);

            if (!isDigit(accountPassword))
                System.out.println("it just can be a number! enter another:");
        } while (!isDigit(accountPassword));
        return accountPassword;
    }


    public int checkChosenAccount(String accountsNum) {
        int chosenAccount;
        do {
            chosenAccount = sc.nextInt();
            if (chosenAccount < 1 || chosenAccount > Integer.parseInt(accountsNum))
                System.out.println("invalid order! try again:");

        } while (chosenAccount < 1 || chosenAccount > Integer.parseInt(accountsNum));
        return chosenAccount;
    }


    public void showMenu() {
        System.out.println("------------------------------");
        System.out.println("what do you want to do:\n\n"
                + "1)check balance           01)switch account\n"
                + "2)withdraw                02)add new account\n"
                + "3)deposit                 03)useful accounts\n"
                + "4)transaction history     04)delete account\n"
                + "5)pay bill\n"
                + "6)exit\n");
    }
}

