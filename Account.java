import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Account implements Serializable {
    int balance;
    int usage;
    String accountId;
    String password;
    private static Lock lock = new ReentrantLock();
    ArrayList<TransAction> transActions = new ArrayList<>();

    public Account(String accountId, String password) {
        this.accountId = accountId;
        this.password = password;
        balance = 5;
        usage = 0;
    }

    public void deposit(int amount) {
        lock.lock();
        try {
            if (amount != 0) {
                balance += amount;
                transActions.add(new TransAction(amount + "", "deposit"));
                usage++;
            }
        }finally {
            lock.unlock(); // Release the lock
        }
    }

    public void withdraw(int amount) {
        lock.lock();
        try {
            if (amount != 0) {
                balance -= amount;
                transActions.add(new TransAction(-1 * (amount) + "", "withDraw"));
                usage++;
            }
        }finally {
            lock.unlock(); // Release the lock
        }
    }

    public void payBill(int amount, String billingID, String paymentCode) {
        if (amount != 0) {
            balance -= amount;
            transActions.add(new TransAction(-1 * (amount) + "", "pay bill", billingID, paymentCode));
            usage++;
        }
    }

    public ArrayList<TransAction> getTransActionHistory() {
        return transActions;
    }

    public int getBalance() {
        return balance;
    }

    public String getPassword() {
        return password;
    }

    public int getUsage() {
        return usage;
    }

    public String getAccountId() {
        return accountId;
    }
}
