package com.chess.mainwindow.game.chesspieces ;

import java.util.ArrayList ;
import javax.swing.* ;
import com.chess.mainwindow.game.board.* ; 
import com.chess.mainwindow.game.chesspieces.* ;

public class RookPiece extends ChessPiece {

  public RookPiece(boolean color, int row, int col, Board board, ArrayList<ChessPiece> pieces) {
    super(color, row, col, board, pieces);
    this.path = "./../assets/pieces/" + pathColor + "/rook" + ".png";
    this.image = new ImageIcon(path).getImage();
  }

  public boolean isBlocked(int row, int col) {
    int rowdiff = this.row - row;
    int coldiff = this.col - col;
    int checkrow;
    int checkcol;

    if (coldiff == 0 && rowdiff > 0) {
      checkrow = this.row - 1;
      checkcol = this.col;
      for (; checkrow > row; checkrow--) {
        if (board.state[checkrow][checkcol] != null) {
          return true;
        }
      }
    }

    else if (coldiff == 0 && rowdiff < 0) {
      checkrow = this.row + 1;
      checkcol = this.col;
      for (; checkrow < row; checkrow++) {
        if (board.state[checkrow][checkcol] != null) {
          return true;
        }
      }
    }

    else if (rowdiff == 0 && coldiff > 0) {
      checkrow = this.row;
      checkcol = this.col - 1;
      for (; checkcol > col; checkcol--) {
        if (board.state[checkrow][checkcol] != null) {
          return true;
        }
      }
    }

    else if (rowdiff == 0 && coldiff < 0) {
      checkrow = this.row;
      checkcol = this.col + 1;
      for (; checkcol < col; checkcol++) {
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
    if (!(rowdiff > 0 != coldiff > 0))
      return false;
    return true ;
  }

  public boolean canMove(int row, int col, ArrayList<ChessPiece> pieces, boolean r) {
    if(!super.canMove(row, col, pieces, r)) return false;
    if(!moveRules(row, col))
      return false  ;
    if (isBlocked(row, col))
      return false;
    return true;
  }
}
