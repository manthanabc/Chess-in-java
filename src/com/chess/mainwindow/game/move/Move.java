package com.chess.mainwindow.game.move ;

import java.io.Serializable; 

public class Move implements Serializable{
  int[] prevPosition = null;
  int[] currPosition = null;

  public Move(){
    
  }

  public void setPrevPosition(int row, int col){
    this.prevPosition = new int[]{row,col} ;
  }

  public void currPosition(int row, int col){
    this.currPosition = new int[]{row, col} ;
  }

  public int[] getPrevPosition(){
    return this.prevPosition ;
  }

  public int[] getcurrPosition(){
    return this.currPosition ;
  }
}
