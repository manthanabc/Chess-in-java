package com.chess.subwindow ;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.chess.mainwindow.game.board.* ;

import javax.swing.* ;

public class ChatPanel extends JPanel implements Runnable{
  
  JTextArea incoming;
  JTextField outgoing;
  BufferedReader reader;
  PrintWriter writer;
  Socket sock;
  public ChatPanel(){
    setPreferredSize(new Dimension(350, Board.SQUARE_SIZE * Board.MAX_ROW));
    setBackground(Color.WHITE);
  }

  public void launchClient(){
    Thread thread = new Thread(this) ;
    thread.start() ;
  }

  private void setUpNetworking() {
      try {
          sock = new Socket("127.0.0.1", 5000);
          InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
          reader = new BufferedReader(streamReader);
          writer = new PrintWriter(sock.getOutputStream());
          System.out.println("networking established");
      } catch (Exception ex) {
          ex.printStackTrace();
      }
  }


    public class SendButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            try {
                writer.println(outgoing.getText());
                writer.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            outgoing.setText("");
            outgoing.requestFocus();
        }
    }

    public class IncomingReader implements Runnable {
        public void run() {
           String message;
            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println("read " + message);
                    incoming.append(message + "\n");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

  public void run(){
    
        incoming = new JTextArea(15, 15);
        incoming.setLineWrap(true);
        incoming.setWrapStyleWord(true);
        incoming.setEditable(false);

        JScrollPane qScroller = new JScrollPane(incoming);
        qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        outgoing = new JTextField(20);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new SendButtonListener());

        this.add(qScroller);
        this.add(outgoing);
        this.add(sendButton);

        setUpNetworking();

        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();
  }
}
