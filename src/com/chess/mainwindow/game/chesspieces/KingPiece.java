package com.chess.mainwindow.game.chesspieces ;

import java.util.ArrayList ;
import javax.swing.* ;
import com.chess.mainwindow.game.board.* ;

public class KingPiece extends ChessPiece {

  public KingPiece(boolean color, int row, int col, Board board, ArrayList<ChessPiece> pieces) {
    super(color, row, col, board, pieces);
    this.path = "./../assets/pieces/" + pathColor + "/king" + ".png";
    this.image = new ImageIcon(path).getImage();
  }

  public boolean inCheck(ArrayList<ChessPiece> pieces) {
    for (ChessPiece piece : pieces) {
      if (piece.color == this.color)
        continue;
      if (piece.canMove(this.row, this.col, pieces, false))
        return true;
    }
    return false;
  }

  public boolean checkMate(ArrayList<ChessPiece> pieces){
    for(ChessPiece p : pieces){
      if(p.color != this.color) continue ;
      p.storePossibleMoves();
      if(p.possibleMoves.isEmpty()){
        return false ;
      }
      for() {
        
      }
    }
    return true ;
    
  }

  public boolean canCastle(int row, int col) {
    int coldiff = this.col - col;

    if (this.lastMoveNumber == 0 && this.row == (color ? 7 : 0) && Math.abs(coldiff) == 2) {
      if (coldiff > 0) {
        if (board.state[this.row][1] != null || board.state[this.row][2] != null || board.state[this.row][3] != null) {
          return false;
        }
        if (board.state[this.row][0] != null && board.state[this.row][0].path.contains("/rook")
            && board.state[this.row][0].lastMoveNumber == 0) {
          board.state[this.row][0].update(this.row, 3);
          return true;
        }
      } else {
        if (board.state[this.row][5] != null || board.state[this.row][6] != null) {
          return false;
        }
        if (board.state[this.row][7] != null && board.state[this.row][7].path.contains("/rook")
            && board.state[this.row][7].lastMoveNumber == 0) {
          board.state[this.row][7].update(this.row, 5);
          return true;
        }
      }
    }
    return false;
  }

  public boolean moveRules(int row, int col){
    int rowdiff = this.row - row;
    int coldiff = this.col - col;
    if (!((Math.abs(rowdiff) | Math.abs(coldiff)) == 1))
      return false;
    return true ;
  }

  public boolean canMove(int row, int col, ArrayList<ChessPiece> pieces, boolean r) {
    if(!super.canMove(row, col, pieces, r)) return false;
    if(!moveRules(row, col)) 
      return false ;
    if (canCastle(row, col))
      return true;
    return true;
  }
}
