package com.chess.main ;

import javax.swing.* ;
import java.awt.*;
import com.chess.mainwindow.* ;
import com.chess.subwindow.* ;

public class Main {

  public static void main(String[] args) {
    JFrame chat = new JFrame("chat");
    JFrame chess = new JFrame("Chess");
    MainPanel boardPanel = new MainPanel();
    boardPanel.setLayout(null);
    boardPanel.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, new Color(0, 0, 0)));
    boardPanel.launchClient();
    chess.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    chat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    chess.setResizable(false);
    chat.setResizable(false);
    chess.getContentPane().add(boardPanel);
    chess.pack();
    chess.setLocationRelativeTo(null);
    chess.setVisible(true);
  }

}
