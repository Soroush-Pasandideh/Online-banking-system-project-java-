import java.io.Serializable;
import java.util.HashMap;

public class Client implements Serializable {
    HashMap<String, Account> accountHashMap = new HashMap<>();
    String identificationCode;
    String password;

    public Client(String identificationCode,String password) {
        this.identificationCode=identificationCode;
        this.password=password;

    }

    public static void main(String[] args) {
        new ClientHandler();
//        Thread.sleep(100);
    }

    public String getIdentificationCode() {
        return identificationCode;
    }

    public String getPassword() {
        return password;
    }

    public HashMap<String, Account> getAccountHashMap() {
        return accountHashMap;
    }

    public void addNewAccount(String accountId, String accountPassword) {
        accountHashMap.put(accountId, new Account(accountId, accountPassword));
    }
}
