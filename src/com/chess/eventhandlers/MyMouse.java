package com.chess.eventhandlers ;

import java.awt.event.* ;

public class MyMouse extends MouseAdapter {

  public boolean pressed;
  public int x;
  public int y;
  public int pressedX;
  public int pressedY;

  public void mouseDragged(MouseEvent e) {
    this.x = e.getX();
    this.y = e.getY();
  }

  public void mousePressed(MouseEvent e) {
    if (e.getButton() == 1) {
      this.pressed = true;
      this.pressedX = e.getX();
      this.pressedY = e.getY();
    }
  }

  public void mouseReleased(MouseEvent e) {
    if (e.getButton() == 1) {
      this.x = e.getX();
      this.y = e.getY();
      this.pressed = false;
    }
  }
}
