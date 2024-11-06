import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.io.*;

public class Server{
  public ServerSocket serverSocket = null ;
  public Queue<Socket> waitingClients = new LinkedList<>() ;

  public static void main(String[] args){
    Server server = new Server() ;
    try{
      server.serverSocket = new ServerSocket(4240) ;
    }catch(Exception e){
      e.printStackTrace();
    }
    while(true){
      Socket clientSocket = null ;      
      try{
        clientSocket = server.serverSocket.accept() ; 
        clientSocket.setKeepAlive(true); 
        server.waitingClients.add(clientSocket) ;
      }catch(Exception e){
        e.printStackTrace(); 
      }
      System.out.println("connection established :" + clientSocket) ;
      if(server.waitingClients.size() > 1) {
        System.out.println("paired clients") ;
        new Thread(new GameServer(server.waitingClients.remove(), server.waitingClients.remove() )).start();
      }
    }
  }
}


class GameServer implements Runnable{
  Socket whiteClient = null ;
  Socket blackClient = null ;

  public GameServer(Socket whiteClient, Socket blackClient){
    this.whiteClient = whiteClient ;
    this.blackClient = blackClient ;

  }   

  public void run(){
    System.out.println("THREADFIRED");
    init() ;
  }

public class Move implements Serializable {
    private int x;
    private int y;

    public Move(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Move{" +
               "x=" + x +
               ", y=" + y +
               '}';
    }
}

public void init(){
    System.out.println("TELLING COLORS");

    PrintWriter writerblack;
    PrintWriter writerwhite ;
          
    try {
      writerblack = new PrintWriter(blackClient.getOutputStream(), true);
      writerblack.println("U black");
      writerwhite = new PrintWriter(whiteClient.getOutputStream(), true);
      writerwhite.println("U white");
    } catch ( Exception e) {
      e.printStackTrace();
    }

    ObjectInputStream whiteinputStream;
    ObjectOutputStream whiteoutputStream;
        
    ObjectInputStream blackinputStream;
    ObjectOutputStream blackoutputStream;
        
    try {
      blackinputStream = new ObjectInputStream(blackClient.getInputStream());
      blackoutputStream = new ObjectOutputStream(blackClient.getOutputStream());
      whiteinputStream = new ObjectInputStream(whiteClient.getInputStream());
      whiteoutputStream = new ObjectOutputStream(whiteClient.getOutputStream());

      Object receivedObject; 
      while(true) {
        while ((receivedObject = whiteinputStream.readObject()) != null) {
            if (receivedObject instanceof Move) {
                Move move = (Move) receivedObject;
                System.out.println("Received move: " + move);

                blackoutputStream.writeObject(move);
                blackoutputStream.flush();
                break;
            }
        }
        while ((receivedObject = blackinputStream.readObject()) != null) {
            if (receivedObject instanceof Move) {
                Move move = (Move) receivedObject;
                System.out.println("Received move: " + move);

                whiteoutputStream.writeObject(move);
                whiteoutputStream.flush();
                break;
            }
        }
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
} 
