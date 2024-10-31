package com.chess.mainwindow.game.board ;

import java.util.ArrayList ;
import javax.swing.* ;
import java.awt.* ;
import com.chess.mainwindow.game.chesspieces.* ;

public class Board {

  public static final int SQUARE_SIZE = 120;
  public static final int MAX_COL = 8;
  public static final int MAX_ROW = 8;
  private final Color DARK = new Color(118, 150, 86);
  private final Color LIGHT = new Color(238, 238, 210);
  public ChessPiece state[][] = new ChessPiece[8][8];
  public ArrayList<ChessPiece> pieces;
  public boolean blackCheck = false;
  public boolean whiteCheck = true;
  public int activeBlockCol;
  public int activeBlockRow;
  public boolean showPromotionWindow = false;

  public Board(ArrayList<ChessPiece> pieces) {
    this.pieces = pieces;
  }

  public void setActiveBlock(int y, int x) {
    if (getRow(y) < 0 || getRow(y) > 7 || getCol(x) < 0 || getCol(x) > 7)
      return;
    activeBlockCol = getCol(x);
    activeBlockRow = getRow(y);
  }

  public void updateActiveBlock(int y, int x) {
    if (getRow(y) < 0 || getRow(y) > 7 || getCol(x) < 0 || getCol(x) > 7)
      return;
    // pieces.remove(state[activeBlockRow][activeBlockCol]);
    state[getRow(y)][getCol(x)] = state[activeBlockRow][activeBlockCol];
    state[activeBlockRow][activeBlockCol] = null;
  }

  public ChessPiece pieceAtPosition(int y, int x) {
    if (getRow(y) < 0 || getRow(y) > 7 || getCol(x) < 0 || getCol(x) > 7)
      return null;
    return state[getRow(y)][getCol(x)];
  }

  public ChessPiece friendlyPieceAtPosition(int y, int x, boolean friendlyPieceColor) {
    if (getRow(y) < 0 || getRow(y) > 7 || getCol(x) < 0 || getCol(x) > 7)
      return null;
    ChessPiece piece = state[getRow(y)][getCol(x)];
    if (piece == null)
      return null;
    if (piece.color != friendlyPieceColor)
      return null;
    return state[getRow(y)][getCol(x)];
  }

  public ChessPiece enemyPieceAtPosition(int y, int x, boolean friendlyPieceColor) {
    if (getRow(y) < 0 || getRow(y) > 7 || getCol(x) < 0 || getCol(x) > 7)
      return null;
    ChessPiece piece = state[getRow(y)][getCol(x)];
    if (piece == null)
      return null;
    if (piece.color == friendlyPieceColor) {
      return null;
    }
    return state[getRow(y)][getCol(x)];
  }

  public int getCol(int x) {
    return x / Board.SQUARE_SIZE;
  }

  public int getRow(int y) {
    return y / Board.SQUARE_SIZE;
  }

  public void remove(int row, int col) {
    pieces.remove(state[row][col]);
    state[row][col] = null;
  }

  public void draw(Graphics2D g2d) {

    for (int row = 0; row < Board.MAX_ROW; row++) {
      for (int col = 0; col < Board.MAX_COL; col++) {
        if ((row + col) % 2 == 0) {
          g2d.setColor(LIGHT);
        } else {
          g2d.setColor(DARK);
        }
        g2d.fillRect(row * Board.SQUARE_SIZE, col * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
      }
    }
  }

  // public void drawPromotionWindow(int row, int col, Graphics2D g2d){
  //   int y = row * Board.SQUARE_SIZE + Board.SQUARE_SIZE/2 ;
  //   int x = col * Board.SQUARE_SIZE + Board.SQUARE_SIZE/2 ;
  //   g2d.setColor(Color.CYAN) ;
  //   g2d.fillRect(x, y, Board.SQUARE_SIZE/2,  2*Board.SQUARE_SIZE) ;    
  // }
}

