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
    this.direction = row == 6 ? 1 : -1 ;
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

  public void promote(){
    int choice = -1 ;
    while(choice == -1){
      String[] options = {"Rook", "Knight", "Queen", "Bishop"};   
      choice = JOptionPane.showOptionDialog( null, "Choose a piece:", "Piece Selection", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

      switch(choice){
        case 0:
          pieces.add(board.state[row][col] = new RookPiece(this.color,row,col,this.board,pieces)) ;
          pieces.remove(this) ;
          break;
        case 1:
          pieces.add(board.state[row][col] = new KnightPiece(this.color,row,col,this.board,pieces)) ;
          pieces.remove(this) ;
          break;
        case 2:
          pieces.add(board.state[row][col] = new QueenPiece(this.color,row,col,this.board,pieces)) ;
          pieces.remove(this) ;
          break;
        case 3:
          pieces.add(board.state[row][col] = new BishopPiece(this.color,row,col,this.board,pieces)) ;
          pieces.remove(this) ;
          break;
      }
    }

    Game.promotionFlag = false ;
  }

  public boolean canMove(int row, int col, ArrayList<ChessPiece> pieces, boolean r) {
    if(!super.canMove(row, col, pieces, r)) return false;
    int rowdiff = this.row - row;
    int coldiff = this.col - col;
    // direction = color ? 1 : -1;
    ChessPiece piece;
    if(row + direction < 0 || row+direction > 7 ) return false ;

    if ((piece = board.state[row + direction][col]) != null && Math.abs(coldiff) == 1 && piece.path.contains("/pawn")) {
      PawnPiece pawnPiece = (PawnPiece) piece;
      if (pawnPiece.color != this.color && Game.moveNumber - pawnPiece.lastMoveNumber == 1
          && pawnPiece.enPassant == true && this.row == (color?3:4)) {
        board.remove(row + direction, col);
        return true;
      }
    }

    if (rowdiff == direction && coldiff == 0) {
      if (isBlocked(this.row - direction, col))
        return false;
      // if (firstmove == true)
      //   firstmove = false;
      if (enPassant)
        enPassant = false;
      if (row == (color?0:7)){
        //DO: promote pawn        
      }
      if(row == (this.direction == 1 ? 0 : 7) ){
        Game.promotionFlag = true ;
      }
      return true;
    }

    if (rowdiff == 2 * direction && firstMove == true && coldiff == 0) {
      // DO: check blocked or not
      if (isBlocked(this.row - direction, col) || isBlocked(this.row - 2 * direction, col))
        return false;
      if (!enPassant && firstMove)
        enPassant = true;
      // firstMove = false;
      return true;
    }

    if (rowdiff == direction && Math.abs(coldiff) == 1) {
      // DO: if opponent piece available return true else return false
      if(board.state[row][col] != null && board.state[row][col].color != this.color){
        if(row == (this.direction == 1 ? 0 : 7) ){
          Game.promotionFlag = true ;
        }
        return true ;
      } 
      // if (row == (color?0:7)){
      //   //DO: promote pawn        
      // }
      // if (firstMove == true)
      //   firstMove = false;
      if (enPassant)
        enPassant = false;
      return false;
    }

    return false;
  }
}

