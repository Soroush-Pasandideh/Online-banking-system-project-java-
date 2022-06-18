
import com.sun.javafx.runtime.async.AbstractRemoteResource;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
    static File clientsFile;
    static FileOutputStream fOut;
    static FileInputStream fIn;
    static ObjectOutputStream oOut;
    static ObjectInputStream oIn;
    static int count = 1;
    static HashMap<String, Client> clientsMap = new HashMap<>();

    public static void main(String[] args) {
        try {
            clientsFile = new File("clients");
            fOut = new FileOutputStream(clientsFile);
            fIn = new FileInputStream(clientsFile);
            oOut = new ObjectOutputStream(fOut);
            oIn = new ObjectInputStream(fIn);
            ServerSocket serverSocket = new ServerSocket(4545);
            System.out.println("server created");
            updateFile();
            clientsMap = (HashMap<String, Client>) oIn.readObject();
            while (true) {
                Socket socketForClient = serverSocket.accept();
                System.out.println("client connected");
                new ServerHandler(socketForClient, count++);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                fOut.close();
                fIn.close();
                oOut.close();
                oIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addClientToMap(String name, Client client) {
        clientsMap.put(name, client);
        updateFile();
    }

    public static void updateFile() {
        try {
            // bayad file khali she ha
            oOut.writeObject(clientsMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
