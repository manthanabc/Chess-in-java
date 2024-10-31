package com.chess.mainwindow.game.chesspieces ;

import java.util.ArrayList ;
import javax.swing.* ;
import java.awt.* ;
import com.chess.mainwindow.game.board.*  ;

public abstract class ChessPiece {

  public Image image;
  public int x, y;
  public boolean color;
  public int row, col;
  public String path;
  public String pathColor;
  public int lastMoveNumber = 0;
  public Board board;
  public ArrayList<int[]> possibleMoves = null ; 
  public ArrayList<ChessPiece> pieces ;

  public ChessPiece(boolean color, int row, int col, Board board, ArrayList<ChessPiece> pieces) {
    this.board = board;
    this.pieces = pieces ;
    this.col = col;
    this.row = row;
    this.color = color;
    if (color) {
      pathColor = "white";
    } else {
      pathColor = "black";
    }
    x = getX(col);
    y = getY(row);
  }

  public void storePossibleMoves(){
    this.possibleMoves = new ArrayList<int[]>() ;
    for(int i = 0 ; i < Board.MAX_ROW ; i++){
      for(int j = 0 ; j < Board.MAX_COL ; j++){
        System.out.println(i + "  "+ j) ;
        if(this.canMove(i, j, pieces, true)){
          possibleMoves.add(new int[]{i,j}) ;
        }
      }
    }
  }

  public boolean isPosition(int y, int x) {
    int row = y;// / Board.SQUARE_SIZE;
    int col = x;// / Board.SQUARE_SIZE;
    if (row == this.row && col == this.col) {
      return true;
    }
    return false;
  }

  public void drag(int y, int x) {
    this.x = x - Board.SQUARE_SIZE / 2;
    this.y = y - Board.SQUARE_SIZE / 2;
  }

  abstract public boolean moveRules(int row, int col) ;

  public boolean canMove(int row, int col, ArrayList<ChessPiece> pieces, boolean r) {

    KingPiece k = null;
    for(ChessPiece p : pieces ) {
      if(p.path.contains("king") && p.color == this.color) {
        k = (KingPiece) p;
      }
    }

    if(k != null && r) {
      int cur_row = this.row;
      int cur_col = this.col;
      ChessPiece g = this.board.state[row][col];
      this.update(row, col);
      this.board.state[cur_row][cur_col]= null;
      pieces.remove(g);
      this.board.state[row][col] = this;
      if(k.inCheck(pieces)) {
        if(g != null){
          pieces.add(g);
        }
        this.update(cur_row, cur_col);
        this.board.state[cur_row][cur_col]= this;
        this.board.state[row][col] = g;
        return false;
      }
      if(g != null){
        pieces.add(g);
      }
      this.board.state[cur_row][cur_col]= this;
      this.board.state[row][col] = g;
      this.update(cur_row, cur_col);
    }
    return true;
  }

  public void originalPosition() {
    this.x = getX(col);
    this.y = getY(row);
  }

  public boolean update(int newRow, int newCol) {
    this.col = newCol;
    this.row = newRow;
    this.x = getX(col);
    this.y = getY(row);
    return true;
  }

  public int getX(int col) {
    return col * Board.SQUARE_SIZE;
  }

  public int getY(int row) {
    return row * Board.SQUARE_SIZE;
  }

  public void draw(Graphics2D g2d) {
    g2d.drawImage(image, x, y, null);
  }

  public int getCol(int x) {
    if (x < 0 || x > 8 * Board.SQUARE_SIZE)
      return this.col;
    return x / Board.SQUARE_SIZE;
  }

  public int getRow(int y) {
    if (y < 0 || y > 8 * Board.SQUARE_SIZE)
      return this.row;
    return y / Board.SQUARE_SIZE;
  }
}

