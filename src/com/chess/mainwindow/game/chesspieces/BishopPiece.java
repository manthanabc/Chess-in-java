package com.chess.mainwindow.game.chesspieces ;

import java.util.ArrayList ;
import javax.swing.* ;
import com.chess.mainwindow.game.board.* ;

public class BishopPiece extends ChessPiece {

  public BishopPiece(boolean color, int row, int col, Board board, ArrayList<ChessPiece> pieces) {
    super(color, row, col, board, pieces);
    this.path = "./../assets/pieces/" + pathColor + "/bishop" + ".png";
    this.image = new ImageIcon(path).getImage();
  }

  public boolean isBlocked(int row, int col) {
    int rowdiff = this.row - row;
    int coldiff = this.col - col;
    int checkrow;
    int checkcol;

    if (rowdiff > 0 && coldiff < 0) { // WORKS:
      checkcol = this.col + 1;
      checkrow = this.row - 1;
      for (; checkrow > row; checkrow--, checkcol++) {
        if (board.state[checkrow][checkcol] != null) {
          return true;
        }
      }
    }

    else if (rowdiff > 0 && coldiff > 0) { // WORKS:
      checkcol = this.col - 1;
      checkrow = this.row - 1;
      for (; checkrow > row; checkrow--, checkcol--) {
        if (board.state[checkrow][checkcol] != null) {
          return true;
        }
      }
    }

    else if (rowdiff < 0 && coldiff < 0) { // WORKS:
      checkcol = this.col + 1;
      checkrow = this.row + 1;
      for (; checkrow < row; checkrow++, checkcol++) {
        if (board.state[checkrow][checkcol] != null) {
          return true;
        }
      }
    }

    else if (rowdiff < 0 && coldiff > 0) {
      checkcol = this.col - 1;
      checkrow = this.row + 1;
      for (; checkrow < row; checkrow++, checkcol--) {
        if (board.state[checkrow][checkcol] != null) {
          return true;
        }
      }
    }

    return false;
  }

  public boolean moveRules(int row, int col){
    int rowdiff = Math.abs(this.row - row);
    int coldiff = Math.abs(this.col - col);
    if (rowdiff == 0 && coldiff == 0)
      return false;
    if (!(rowdiff == coldiff))
      return false;
    return true ;
  }

  public boolean canMove(int row, int col, ArrayList<ChessPiece> pieces, boolean r) {
    if(!super.canMove(row, col, pieces, r)) return false;
    if(!moveRules(row, col)) 
      return false ;
    if (isBlocked(row, col)) // DO: change implementation
      return false;
    return true;
  }
}

