package com.chess.mainwindow.game.chesspieces ;

import java.util.ArrayList ;
import javax.swing.* ;
import com.chess.mainwindow.game.board.* ;

public class KnightPiece extends ChessPiece {

  public KnightPiece(boolean color, int row, int col, Board board, ArrayList<ChessPiece> pieces) {
    super(color, row, col, board, pieces);
    this.path = "./../assets/pieces/" + pathColor + "/knight" + ".png";
    this.image = new ImageIcon(path).getImage();
  }

  public boolean moveRules(int row, int col){
    int rowdiff = Math.abs(this.row - row);
    int coldiff = Math.abs(this.col - col);
    if (rowdiff == 0 || coldiff == 0)
      return false;
    return (rowdiff + coldiff == 3);
  }

  public boolean canMove(int row, int col, ArrayList<ChessPiece> pieces, boolean r) {
    if(!super.canMove(row, col, pieces, r)) return false;
    if(!moveRules(row, col))
      return false ;
    return true ;
  }
}

