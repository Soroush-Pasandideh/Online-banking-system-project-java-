import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

class ServerHandler extends Thread {
    Socket socketForClient;
    private BufferedReader in;
    private PrintWriter out;
    int num;

    public ServerHandler(Socket socketForClient, int num) {
        this.socketForClient = socketForClient;
        this.num = num;
        try {
            in = new BufferedReader(
                    new InputStreamReader(
                            socketForClient.getInputStream()));
            out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    socketForClient.getOutputStream())), true);
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String signOrLog = in.readLine();
            String identificationCode;
            String chosenAccountId = "";
            if (signOrLog.equals("1")) {

                identificationCode = signUp();

            } else { //  if(signOrLog.equals("2"))

                identificationCode = logIn();

            }
            ///////// entered
            String addOrChoose = in.readLine();
            if (signOrLog.equals("1")) {

                chosenAccountId = continueSignUp(identificationCode);

            }
            if (signOrLog.equals("2")) {

                chosenAccountId = continuelogIn(identificationCode, addOrChoose);

            }

            boolean moreOrders = true;
            while (moreOrders) {
                switch (in.readLine()) {
                    case "1":
                        out.println(Server.clientsMap.get(identificationCode).getAccountHashMap().get(chosenAccountId).getBalance());
                        break;
                    case "2":
                        String amount = in.readLine();
                        if (Server.clientsMap.get(identificationCode).getAccountHashMap().get(chosenAccountId).getBalance() < Integer.parseInt(amount))
                            out.println("0");
                        else {
                            Server.clientsMap.get(identificationCode).getAccountHashMap().get(chosenAccountId).withdraw(Integer.parseInt(amount));
                            out.println("1");
                        }
                        Server.updateFile();
                        break;
                    case "3":
                        Server.clientsMap.get(identificationCode).getAccountHashMap().get(chosenAccountId).deposit(Integer.parseInt(in.readLine()));
                        out.println("deposit is done!");
                        Server.updateFile();
                        break;
                    case "4":
                        ArrayList<TransAction> transActionHistory = Server.clientsMap.get(identificationCode).getAccountHashMap().get(chosenAccountId).getTransActionHistory();
                        out.println(transActionHistory.size());
                        for (TransAction transAction : transActionHistory) {
                            out.println(transAction.getType());
                            out.println(transAction.getAmount());
                            if (transAction.getType().equals("pay bill")) {
                                out.println("1");
                                out.println(transAction.getBillingID());
                                out.println(transAction.getPaymentCode());
                            } else out.println("2");
                        }
                        break;
                    case "5":
                        String billAmount = in.readLine();
                        if (Server.clientsMap.get(identificationCode).getAccountHashMap().get(chosenAccountId).getBalance() < Integer.parseInt(billAmount))
                            out.println("0");
                        else {
                            out.println("1");
                            Server.clientsMap.get(identificationCode).getAccountHashMap().get(chosenAccountId).payBill(Integer.parseInt(billAmount), in.readLine(), in.readLine());
                        }
                        Server.updateFile();
                        break;
                    case "01":
                        in.readLine();// baraye in ke bug nade
                        chosenAccountId = chooseAccount(identificationCode);
                        Server.updateFile();
                        break;
                    case "02":
                        in.readLine();// baraye in ke bug nade
                        chosenAccountId = addAccount(identificationCode);
                        Server.updateFile();
                        break;
                    case "03":
                        sortAccountsByUsage(identificationCode);
                        break;
                    case "04":
                        out.println(Server.clientsMap.get(identificationCode).getAccountHashMap().get(chosenAccountId).getAccountId());
                        String answer = in.readLine();
                        if (answer.equals("1")) {  //yes
                            HashMap<String, Account> accountHashMap = Server.clientsMap.get(identificationCode).getAccountHashMap();
                            out.println(accountHashMap.size()-1);
                            for (Map.Entry<String, Account> entry : accountHashMap.entrySet()) {
                                if (!(entry.getKey().equals(chosenAccountId))) {
                                    out.println(entry.getValue().getAccountId());
                                    out.println(entry.getValue().getUsage());
                                }
                            }
                            String chooseOrAdd=in.readLine();
                            if (chooseOrAdd.equals("1")) {
                                String destinationAccount = in.readLine();
                                int balanceToTransfer = Server.clientsMap.get(identificationCode).getAccountHashMap().get(chosenAccountId).getBalance();
                                Server.clientsMap.get(identificationCode).getAccountHashMap().get(destinationAccount).deposit(balanceToTransfer);
                                Server.clientsMap.get(identificationCode).getAccountHashMap().remove(chosenAccountId);
                                chosenAccountId=destinationAccount;
                                out.println("1");
                            }
                            else { // 2
                                String chosenAccountId2=addAccount(identificationCode);
                                int balanceToTransfer = Server.clientsMap.get(identificationCode).getAccountHashMap().get(chosenAccountId).getBalance();
                                Server.clientsMap.get(identificationCode).getAccountHashMap().get(chosenAccountId2).deposit(balanceToTransfer);
                                Server.clientsMap.get(identificationCode).getAccountHashMap().remove(chosenAccountId);
                                chosenAccountId=chosenAccountId2;
                                out.println("2");
                            }
                        } else out.println("2");    //no

                        break;
                    case "6":
                        moreOrders = false;
                        Server.updateFile();
                        break;
                }
            }

            printClients();

        } catch (
                IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socketForClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String signUp() {
        String identificationCode = "";
        boolean isIdentificationCodeNew;
        try {
            //// check if the ID is used or new
            do {
                isIdentificationCodeNew = true;
                identificationCode = in.readLine();
                for (Map.Entry<String, Client> entry : Server.clientsMap.entrySet()) {
                    if (entry.getKey().equals(identificationCode)) {
                        isIdentificationCodeNew = false;
                        break;
                    }
                }
                if (isIdentificationCodeNew) {
                    out.println("t");
                } else
                    out.println("f");
            } while (!isIdentificationCodeNew);
            String password = in.readLine();
            Server.addClientToMap(identificationCode, new Client(identificationCode, password));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return identificationCode;
    }

    public String logIn() {
        String identificationCode = "";
        try {
            boolean isPasswordCorrect = false;
            do {
                identificationCode = in.readLine();
                String password = in.readLine();
                boolean correctName = false;
                for (Map.Entry<String, Client> entry : Server.clientsMap.entrySet()) {
                    if (identificationCode.equals(entry.getKey())) {
                        correctName = true;
                        if (entry.getValue().getPassword().equals(password)) {
                            out.println("t");
                            isPasswordCorrect = true;
                            break;
                        } else {
                            out.println("f");
                            break;
                        }
                    }
                }
                if (!correctName)
                    out.println("incorrect identificationCode !");
            } while (!isPasswordCorrect);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return identificationCode;
    }

    public String continueSignUp(String identificationCode) {
        String chosenAccountId = "";
        try {
            chosenAccountId = in.readLine();
            Server.clientsMap.get(identificationCode).addNewAccount(chosenAccountId, in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chosenAccountId;
    }

    public String continuelogIn(String identificationCode, String addOrChoose) {
        String chosenAccountId = "";
        if (addOrChoose.equals("1")) {

            chosenAccountId = addAccount(identificationCode);

        }
        if (addOrChoose.equals("2")) {

            chosenAccountId = chooseAccount(identificationCode);

        }
        return chosenAccountId;
    }

    public String addAccount(String identificationCode) {
        String chosenAccountId = "";
        try {
            chosenAccountId = in.readLine();
            Server.clientsMap.get(identificationCode).addNewAccount(chosenAccountId, in.readLine());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return chosenAccountId;
    }

    public String chooseAccount(String identificationCode) {
        String chosenAccountId = "";
        try {
            HashMap<String, Account> accountHashMap = Server.clientsMap.get(identificationCode).getAccountHashMap();
            out.println(accountHashMap.size());
            for (Map.Entry<String, Account> entry : Server.clientsMap.get(identificationCode).getAccountHashMap().entrySet()) {
                out.println(entry.getKey());
            }
            chosenAccountId = in.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return chosenAccountId;
    }

    public void sortAccountsByUsage(String identificationCode) {
        HashMap<String, Account> accountHashMap = Server.clientsMap.get(identificationCode).getAccountHashMap();
        out.println(accountHashMap.size());
        for (Map.Entry<String, Account> entry : accountHashMap.entrySet()) {
            out.println(entry.getValue().getAccountId());
            out.println(entry.getValue().getUsage());
        }
//        HashMap<Integer, Account> accountsToSort = new HashMap<>();
//        for (Map.Entry<String, Account> entry : accountHashMap.entrySet()) {
//            accountsToSort.put(entry.getValue().getUsage(), entry.getValue());
//        }
//        TreeMap<Integer, Account> sorted = new TreeMap<>();
//        sorted.putAll(accountsToSort);
//        out.println(sorted.size());
//        for (Map.Entry<Integer, Account> entry : sorted.entrySet()) {
//            out.println(entry.getValue().getAccountId());
//            out.println(entry.getKey());
//        }
    }

    public void printClients() {
        for (Map.Entry<String, Client> entry : Server.clientsMap.entrySet()) {
            System.out.println(entry.getKey());
        }
    }
}



















