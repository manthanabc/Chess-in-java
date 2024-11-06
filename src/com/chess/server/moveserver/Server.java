import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class Server{
  public ServerSocket serverSocket = null ;
  public Queue<Socket> waitingClients = new LinkedList<>() ;

  public static void main(String[] args){
    Server server = new Server() ;
    try{
      server.serverSocket = new ServerSocket(4242) ;
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
      if(server.waitingClients.size() > 1){
        System.out.println("paired clients") ;
        new Thread(new GameServer(server.waitingClients.remove(), server.waitingClients.remove() )).start(); ;
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
    init() ;
  }

  public void init(){
    
  }

} 
