package com.chess.main ;

import java.io.*;
import javax.swing.* ;
import java.awt.*;
import java.net.Socket;

import com.chess.mainwindow.* ;
import com.chess.mainwindow.game.board.* ;
import com.chess.subwindow.* ;


public class Main {

  public static final String host = "127.0.0.1" ;
  public static final int port = 4240 ;

  public static void main(String[] args) {

    // JFrame signIn = new JFrame("sign in") ;
    JFrame subWindow = new JFrame("subWindow");
    JFrame chess = new JFrame("Chess");
    MainPanel boardPanel = new MainPanel();
    ChatPanel chatPanel = new ChatPanel();
    frameSetUp(chess, boardPanel);
    frameSetUp(subWindow, chatPanel);
    boardPanel.launchClient();
    chatPanel.launchClient();
    chess.setLocationRelativeTo(null);
    Point point = chess.getLocation() ;
    subWindow.setLocation(point.x + 20 + Board.SQUARE_SIZE * Board.MAX_ROW, point.y) ;
    chess.setVisible(true);
    subWindow.setVisible(true);
  }

  public static void frameSetUp(JFrame frame, JPanel panel){
    panel.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, new Color(0, 0, 0)));
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.getContentPane().add(panel);
    frame.pack();
    frame.setVisible(true);
  }
}


