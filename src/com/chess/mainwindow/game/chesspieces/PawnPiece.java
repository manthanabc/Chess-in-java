package com.chess.mainwindow.game.chesspieces ;

import java.util.ArrayList ;
import javax.swing.* ;
import com.chess.mainwindow.game.* ;
import com.chess.mainwindow.game.board.* ;
import com.chess.mainwindow.game.chesspieces.* ;



public class PawnPiece extends ChessPiece {
  public boolean firstMove = true;
  public boolean enPassant = false;
  public int direction;

  public PawnPiece(boolean color, int row, int col, Board board, ArrayList<ChessPiece> pieces) {
    super(color, row, col, board, pieces);
    this.path = "./../assets/pieces/" + pathColor + "/pawn" + ".png";
    this.image = new ImageIcon(path).getImage();
  }

  public boolean isBlocked(int row, int col) {
    int coldiff = this.col - col;
    if (coldiff == 0) {
      if (board.state[row][col] != null)
        return true;
    } else {
      if (board.state[row][col] != null) {
        if (board.state[row][col].color != this.color)
          return true;
      }
    }
    return false;
  }

  public boolean moveRules(int row, int col){
    return true ;
  }

  public boolean canMove(int row, int col, ArrayList<ChessPiece> pieces, boolean r) {
    if(!super.canMove(row, col, pieces, r)) return false;
    int rowdiff = this.row - row;
    int coldiff = this.col - col;
    direction = color ? 1 : -1;
    ChessPiece piece;
    if(row + direction < 0 || row+direction > 7 ) return false ;
    System.out.println("NOT REQCHED 47");

    if ((piece = board.state[row + direction][col]) != null && Math.abs(coldiff) == 1 && piece.path.contains("/pawn")) {
      PawnPiece pawnPiece = (PawnPiece) piece;
      if (pawnPiece.color != this.color && Game.moveNumber - pawnPiece.lastMoveNumber == 1
          && pawnPiece.enPassant == true && this.row == (color?3:4)) {
        board.remove(row + direction, col);
        return true;
      }
    }
    System.out.println("NOT REQCHED 56");

    if (rowdiff == direction && coldiff == 0) {
      if (isBlocked(this.row - direction, col))
        return false;
      if (firstMove == true)
        firstMove = false;
      else if (enPassant)
        enPassant = false;
      if (row == (color?0:7)){
        //DO: promote pawn        
      }
      return true;
    }

    if (rowdiff == 2 * direction && firstMove == true && coldiff == 0) {
      // DO: check blocked or not
      if (isBlocked(this.row - direction, col) || isBlocked(this.row - 2 * direction, col))
        return false;
      System.out.println("NOT REQCHED 74");
      if (!enPassant && firstMove)
        enPassant = true;
      firstMove = false;
      return true;
    }

    if (rowdiff == direction && Math.abs(coldiff) == 1) {
      // DO: if opponent piece available return true else return false
      if(board.state[row][col] != null && board.state[row][col].color != this.color) return true ;
      if (row == (color?0:7)){
        //DO: promote pawn        
      }
      if (firstMove == true)
        firstMove = false;
      else if (enPassant)
        enPassant = false;
      return false;
    }

    return false;
  }
}

