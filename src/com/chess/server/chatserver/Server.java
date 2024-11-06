import java.io.*;
import java.net.*;
import java.util.*;

public class Server{
  public ServerSocket serverSocket = null;
  public Queue<Socket> waitingClients = new LinkedList<>();

  public static void main(String[] args) {
    Server server = new Server();
    try {
      server.serverSocket = new ServerSocket(5000);
    } catch (Exception e) {
      e.printStackTrace();
    }
    while (true) {
      Socket clientSocket = null;
      try {
        clientSocket = server.serverSocket.accept();
        clientSocket.setKeepAlive(true);
        server.waitingClients.add(clientSocket);
      } catch (Exception e) {
        e.printStackTrace();
      }
      System.out.println("connection established :" + clientSocket);
      if (server.waitingClients.size() > 1) {
        System.out.println("paired clients");
        new GameServer(server.waitingClients.remove(), server.waitingClients.remove());
      }
    }
  }
}

class GameServer{
  Socket whiteClient = null;
  Socket blackClient = null;
  PrintWriter whiteWriter = null;
  PrintWriter blackWriter = null;

  public GameServer(Socket whiteClient, Socket blackClient) {
    this.whiteClient = whiteClient;
    this.blackClient = blackClient;

    try {
      whiteWriter = new PrintWriter(whiteClient.getOutputStream());
      blackWriter = new PrintWriter(blackClient.getOutputStream());
    } catch (Exception e) {
      e.printStackTrace();
    }

    Thread whiteListener = new Thread(new ClientHandler(whiteClient));
    Thread blackListener = new Thread(new ClientHandler(blackClient));
    whiteListener.start();
    blackListener.start();

  }

  class ClientHandler implements Runnable {
    BufferedReader reader;
    Socket sock;

    public ClientHandler(Socket clientSocket) {
      try {
        sock = clientSocket;
        InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
        reader = new BufferedReader(isReader);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }

    public void run() {
      String message;
      try {
        while ((message = reader.readLine()) != null) {
          System.out.println("read " + message);
          tellEveryone(message);
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }
  public void tellEveryone(String message){
        blackWriter.println(message);
        blackWriter.flush();
        whiteWriter.println(message);
        whiteWriter.flush();
  }
}
