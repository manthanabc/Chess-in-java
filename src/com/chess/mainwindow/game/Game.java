package com.chess.mainwindow.game ;

import java.util.ArrayList ;
import javax.swing.* ;
import java.awt.* ;
import java.util.concurrent.locks.*;
import com.chess.mainwindow.game.chesspieces.* ;
import com.chess.mainwindow.game.board.* ;
import com.chess.mainwindow.* ;
import com.chess.eventhandlers.* ;

public class Game implements Runnable {
  private final int TARGET_FPS = 100;
  private final int FRAME_TIME = 1000 / TARGET_FPS;
  private boolean running;
  private Thread gameThread;
  public Board board;
  public ArrayList<ChessPiece> pieces = new ArrayList<>();
  public MyMouse mouse;
  public ChessPiece activePiece;
  public boolean check;
  public boolean turn = true;
  public static int moveNumber = 0;
  public MainPanel boardPanel;
  public KingPiece whiteKing ;
  public KingPiece blackKing ;
  public Lock lock = new ReentrantLock() ;

  public Game(MainPanel boardPanel, MyMouse mouse) {
    this.mouse = mouse;
    board = new Board(pieces);
    this.boardPanel = boardPanel;
  }

  public synchronized void launch() {
    if (running)
      return;

    running = true;
    gameThread = new Thread(this);
    gameThread.start();
  }

  public void init() {
    lock.lock();
    try{
      setPieces();
    }catch(Exception e){
      e.printStackTrace();
    }
    lock.unlock();
  }

  public void setPieces() {
    boolean color;
    ChessPiece p;
      color = false; // set white pieces
      for (int i = 0; i < Board.MAX_COL; i++) { // set white pawns
        p = new PawnPiece(color, 1, i, board, pieces);
        pieces.add(p);
        board.state[1][i] = p;
      }
      pieces.add(p = new KingPiece(color, 0, 4, board, pieces));
      whiteKing = (KingPiece)p ;
      board.state[0][4] = p;
      pieces.add(p = new QueenPiece(color, 0, 3, board, pieces));
      board.state[0][3] = p;
      pieces.add(p = new RookPiece(color, 0, 0, board, pieces));
      board.state[0][0] = p;
      pieces.add(p = new RookPiece(color, 0, 7, board, pieces));
      board.state[0][7] = p;
      pieces.add(p = new KnightPiece(color, 0, 1, board, pieces));
      board.state[0][1] = p;
      pieces.add(p = new KnightPiece(color, 0, 6, board, pieces));
      board.state[0][6] = p;
      pieces.add(p = new BishopPiece(color, 0, 2, board, pieces));
      board.state[0][2] = p;
      pieces.add(p = new BishopPiece(color, 0, 5, board, pieces));
      board.state[0][5] = p;
      color = true; // set black pieces
      for (int i = 0; i < Board.MAX_COL; i++) { // set black pawns
        p = new PawnPiece(color, 6, i, board, pieces);
        pieces.add(p);
        board.state[6][i] = p;
      }
      pieces.add(p = new KingPiece(color, 7, 4, board, pieces));
      blackKing = (KingPiece)p ;
      board.state[7][4] = p;
      pieces.add(p = new QueenPiece(color, 7, 3, board, pieces));
      board.state[7][3] = p;
      pieces.add(p = new RookPiece(color, 7, 0, board, pieces));
      board.state[7][0] = p;
      pieces.add(p = new RookPiece(color, 7, 7, board, pieces));
      board.state[7][7] = p;
      pieces.add(p = new KnightPiece(color, 7, 1, board, pieces));
      board.state[7][1] = p;
      pieces.add(p = new KnightPiece(color, 7, 6, board, pieces));
      board.state[7][6] = p;
      pieces.add(p = new BishopPiece(color, 7, 2, board, pieces));
      board.state[7][2] = p;
      pieces.add(p = new BishopPiece(color, 7, 5, board, pieces));
      board.state[7][5] = p;
      try{
        // Thread.sleep(1000) ;
      }catch(Exception e){
        e.printStackTrace();
      }
      // System.out.println("aaj omellete nahi banauga 1") ;
      for(ChessPiece pi : pieces){
        pi.storePossibleMoves();
      }
      // System.out.println("aaj omellete nahi banauga ") ;
  }

  public void update() {
    synchronized (pieces) {
      if (mouse.pressed && !check) {
        ChessPiece piece = board.pieceAtPosition(mouse.pressedY, mouse.pressedX);
        if (piece != null && piece.color == turn) {
          activePiece = piece;
          board.setActiveBlock(mouse.pressedY, mouse.pressedX);
          mouse.x = mouse.pressedX;
          mouse.y = mouse.pressedY;
          check = true;
        }
      }
      if (!mouse.pressed && check) {
        if (board.friendlyPieceAtPosition(mouse.y, mouse.x, turn) == null
            && activePiece.canMove(activePiece.getRow(mouse.y), activePiece.getCol(mouse.x), pieces, true)) {
          activePiece.update(activePiece.getRow(mouse.y), activePiece.getCol(mouse.x));
          pieces.remove(board.enemyPieceAtPosition(mouse.y, mouse.x, turn));
          board.updateActiveBlock(mouse.y, mouse.x);
          turn = !turn;
          activePiece.lastMoveNumber = Game.moveNumber;
          Game.moveNumber++;
          System.out.println("hello brother how are you?");
          if(blackKing.inCheck(pieces) && blackKing.checkMate(pieces)){
            System.out.println("white wins") ;
          }
          else if(whiteKing.inCheck(pieces) && whiteKing.checkMate(pieces)){
            System.out.println("black wins") ;
          }
        } else {
          activePiece.originalPosition();
        }
        check = false;
        activePiece = null;
      }

      if (activePiece != null) {
        activePiece.drag(mouse.y, mouse.x);
      }
    }
  }

  public void render(Graphics2D g2d) {
    lock.lock();
    board.draw(g2d);
    if(board.showPromotionWindow){
      // board.drawPromotionWindow(g2d);
    }
    // boardPanel.promotion.drawPromotionWindow(g2d);
    drawPieces(g2d);
    lock.unlock();
  }

  public void drawPieces(Graphics2D g2d) {
    // System.out.println("aaj omellete nahi banauga 2") ;
    // lock.lock() ;
    // System.out.println("aaj omellete nahi banauga 3") ;
      // System.out.println("hello there");
      // System.out.println("there hello blahy blah") ;
      for (ChessPiece piece : pieces) {
        piece.draw(g2d);
      }
      if (activePiece != null)
        activePiece.draw(g2d);
   
    // lock.unlock() ;
  }

  public void run() {
    init();
    long startTime;
    long renderTime;
    while (running) {
      startTime = System.currentTimeMillis();
      // update
      update();
      
      // render
      boardPanel.repaint();
      renderTime = System.currentTimeMillis() - startTime;

      if (renderTime < FRAME_TIME) {
        try {
          Thread.sleep(FRAME_TIME - renderTime);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

      }
    }
  }
}
