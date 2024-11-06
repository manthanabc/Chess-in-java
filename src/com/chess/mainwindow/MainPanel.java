package com.chess.mainwindow ;

import javax.swing.* ;
import java.awt.* ;
import com.chess.eventhandlers.* ;
import com.chess.mainwindow.game.* ;
import com.chess.mainwindow.game.board.* ;

import java.io.*;
import java.net.*;

private class Move implements Serializable {
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

public class MainPanel extends JPanel {

  public static final int WIDTH = Board.SQUARE_SIZE * Board.MAX_COL;
  public static final int HEIGHT = Board.SQUARE_SIZE * Board.MAX_ROW;
  public MyMouse mouse;
  public Game game;
  public static boolean is_black;
  // public Promotion promotion ;
  public static final String host = "127.0.0.1" ;
  public static final int port = 4240 ;

  public MainPanel() {
    // this.add(promotion = new Promotion()) ;
    mouse = new MyMouse();
    game = new Game(this, mouse);
    setPreferredSize((new Dimension(WIDTH, HEIGHT)));
    setBackground(Color.WHITE);
    addMouseListener(mouse);
    addMouseMotionListener(mouse);
  }

  public void launchClient() {

    // chat.launch();
    Socket socket = null ;
    while(socket == null || !socket.isConnected()){
      System.out.println("waiting for connection") ;
      try{
        socket = new Socket(host, port);
      }catch(Exception e){
        e.printStackTrace() ;
      }
      try{
        Thread.sleep(100);
      }catch(Exception e){
        e.printStackTrace();
      }
    }

    try {
      System.out.println("connection established") ;
      System.out.println("Waiting for other player") ;
      try {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String message;
        while ((message = reader.readLine()) != null) {
          if(message.contains("black")) {
            System.out.println("im blackkk");
            MainPanel.is_black = true;
            System.out.println("TYA CREATE SRREAM");
            break;
          }
          break;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

      if(true) {
        System.out.println("TRYING TO SEND");
        // ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

        Move move = new Move(0, 2);
        System.out.println("Move will sent: " + move);

        outputStream.writeObject(move);
        outputStream.flush();

        System.out.println("Move sent: " + move);
      }
          
    } catch(Exception e) {
      e.printStackTrace();
    }

    game.launch();
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    game.render(g2d);
  }
}
