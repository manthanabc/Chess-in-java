//oop project Aftab Naik, Atharva Nalat

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Main {

  public static void main(String[] args) {
    JFrame chess = new JFrame("Chess");
    MainPanel boardPanel = new MainPanel();
    boardPanel.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, new Color(0, 0, 0)));
    chess.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    chess.setResizable(false);
    chess.getContentPane().add(boardPanel);
    chess.pack();
    chess.setLocationRelativeTo(null);
    chess.setVisible(true);
    boardPanel.launch();
  }

}

class MainPanel extends JPanel implements Runnable {

  public static final int WIDTH = Board.SQUARE_SIZE * Board.MAX_COL;
  public static final int HEIGHT = Board.SQUARE_SIZE * Board.MAX_ROW;
  private final int TARGET_FPS = 100;
  private final int FRAME_TIME = 1000 / TARGET_FPS;
  private boolean running;
  private Thread gameThread;
  public Board board;
  public ArrayList<ChessPiece> pieces = new ArrayList<>();
  public MyMouse mouse = new MyMouse();
  public ChessPiece activePiece;
  public boolean check;
  public boolean turn = true;
  public static int moveNumber = 0;

  public MainPanel() {
    setPreferredSize((new Dimension(WIDTH, HEIGHT)));
    setBackground(Color.WHITE);
    addMouseListener(mouse);
    addMouseMotionListener(mouse);
    board = new Board();
  }

  public synchronized void launch() {
    if (running)
      return;

    running = true;
    gameThread = new Thread(this);
    gameThread.start();
  }

  public void init() {
    setPieces();
  }

  public void drawPieces(Graphics2D g2d) {
    synchronized (pieces) {
      for (ChessPiece piece : pieces) {
        piece.draw(g2d);
      }
      if (activePiece != null)
        activePiece.draw(g2d);
    }
  }

  public void setPieces() {
    synchronized (pieces) {
      boolean color = false;
      for (int i = 0; i < Board.MAX_COL; i++) {
        pieces.add(new PawnPiece(color, 1, i));
      }
      pieces.add(new KingPiece(color, 0, 4));
      pieces.add(new QueenPiece(color, 0, 3));
      pieces.add(new RookPiece(color, 0, 0));
      pieces.add(new RookPiece(color, 0, 7));
      pieces.add(new KnightPiece(color, 0, 1));
      pieces.add(new KnightPiece(color, 0, 6));
      pieces.add(new BishopPiece(color, 0, 2));
      pieces.add(new BishopPiece(color, 0, 5));
      color = true;
      for (int i = 0; i < Board.MAX_COL; i++) {
        pieces.add(new PawnPiece(color, 6, i));
      }
      pieces.add(new KingPiece(color, 7, 4));
      pieces.add(new QueenPiece(color, 7, 3));
      pieces.add(new RookPiece(color, 7, 0));
      pieces.add(new RookPiece(color, 7, 7));
      pieces.add(new KnightPiece(color, 7, 1));
      pieces.add(new KnightPiece(color, 7, 6));
      pieces.add(new BishopPiece(color, 7, 2));
      pieces.add(new BishopPiece(color, 7, 5));
    }

  }

  public synchronized void stop() {
    if (!running)
      return;

    running = false;
    try {
      gameThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public boolean pieceAtPosition(int y, int x) {
    synchronized (pieces) {
      for (ChessPiece piece : pieces) {
        if (piece.isPosition(piece.getRow(y), piece.getCol(x)))
          return true;
      }
      return false;
    }
  }

  public boolean friendlyPieceAtPosition(int y, int x) {
    synchronized (pieces) {
      for (ChessPiece piece : pieces) {
        if (piece.isPosition(piece.getRow(y), piece.getCol(x)) && piece.color == turn) {
          activePiece.originalPosition();
          return true;
        }
      }
      return false;
    }
  }

  public ChessPiece enemyPieceAtPosition(int y, int x) {
    synchronized (pieces) {
      for (ChessPiece piece : pieces) {
        if (piece.isPosition(piece.getRow(y), piece.getCol(x)) && piece.color != turn) {
          return piece;
        }
      }
      return null;
    }
  }

  public void update() {
    repaint();
    synchronized (pieces) {
      if (mouse.pressed && !check) {
        for (ChessPiece piece : pieces) {
          if (piece.isPosition(piece.getRow(mouse.pressedY), piece.getCol(mouse.pressedX)) && piece.color == turn) {
            activePiece = piece;
            mouse.x = mouse.pressedX;
            mouse.y = mouse.pressedY;
            check = true;
          }
        }
      }
      if (!mouse.pressed && check) {
        if (!friendlyPieceAtPosition(mouse.y, mouse.x))
          if (activePiece.canMove(activePiece.getRow(mouse.y), activePiece.getCol(mouse.x), pieces)) {
            activePiece.update(activePiece.getRow(mouse.y), activePiece.getCol(mouse.x));
            pieces.remove(enemyPieceAtPosition(mouse.y, mouse.x));
            turn = !turn;
            activePiece.lastMoveNumber = MainPanel.moveNumber;
            MainPanel.moveNumber++;
          } else {
            activePiece.originalPosition();
          }
        check = false;
        activePiece = null;
      }

      if (activePiece != null) {
        activePiece.drag(mouse.y, mouse.x);
      }
    }
  }

  public void run() {
    init();
    long startTime;
    long renderTime;

    while (running) {
      startTime = System.currentTimeMillis();
      // update
      update();
      // render
      renderTime = System.currentTimeMillis() - startTime;

      if (renderTime < FRAME_TIME) {
        try {
          Thread.sleep(FRAME_TIME - renderTime);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

      }
    }
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    board.draw(g2d);
    synchronized (pieces) {
      drawPieces(g2d);
    }
  }
}

class Board {

  public static final int SQUARE_SIZE = 120;
  public static final int MAX_COL = 8;
  public static final int MAX_ROW = 8;
  private final Color DARK = new Color(118, 150, 86);
  private final Color LIGHT = new Color(238, 238, 210);

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
}

abstract class ChessPiece {

  public Image image;
  public int x, y;
  public boolean color;
  public int row, col;
  public String path;
  public String pathColor;
  public int lastMoveNumber = 0;

  public ChessPiece(boolean color, int row, int col) {
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

  public boolean canMove(int row, int col, ArrayList<ChessPiece> pieces) {
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

class KingPiece extends ChessPiece {

  public KingPiece(boolean color, int row, int col) {
    super(color, row, col);
    this.path = "./../assets/pieces/" + pathColor + "/king" + ".png";
    this.image = new ImageIcon(path).getImage();
  }

  public boolean inCheck() {
    return false;
  }

  public boolean canCastle(int row, int col, ArrayList<ChessPiece> pieces){
    int coldiff = this.col - col;
    if (this.lastMoveNumber == 0 && this.row == (color ? 7 : 0) && Math.abs(coldiff) == 2) {
      if (coldiff > 0) {
        for (ChessPiece piece : pieces){
          if (piece.isPosition(this.row, 1) || piece.isPosition(this.row, 2) || piece.isPosition(this.row, 3))
            return false;
        }
        for (ChessPiece piece : pieces) {
          if (!piece.color == this.color || !piece.path.contains("/rook.png"))
            continue;
          if (piece.lastMoveNumber == 0 && piece.col == 0) {
            piece.update(this.row, 3);
            return true;
          }
        }
      } else {
        for(ChessPiece piece : pieces){
          if (piece.isPosition(this.row, 5) || piece.isPosition(this.row, 6))
            return false;
        }
        for (ChessPiece piece : pieces) {
          if (!piece.color == this.color || !piece.path.contains("/rook.png"))
            continue;
          if (piece.lastMoveNumber == 0 && piece.col == 7) {
            piece.update(this.row, 5);
            return true;
          }
        }
      }
    }
    return false ;
  }

  public boolean canMove(int row, int col, ArrayList<ChessPiece> pieces) {
    super.canMove(row, col, pieces);

    int rowdiff = this.row - row;
    int coldiff = this.col - col;
    if(canCastle(row, col, pieces)) return true ;
    if (!((Math.abs(rowdiff) | Math.abs(coldiff)) == 1))
      return false;
    return true;
  }
}

class QueenPiece extends ChessPiece {

  public QueenPiece(boolean color, int row, int col) {
    super(color, row, col);
    this.path = "./../assets/pieces/" + pathColor + "/queen" + ".png";
    this.image = new ImageIcon(path).getImage();
  }

  public boolean isBlocked(int row, int col, ArrayList<ChessPiece> pieces) {
    int rowdiff = this.row - row;
    int coldiff = this.col - col;
    for (ChessPiece piece : pieces) {
      if (piece == this)
        continue;
      int rdiff = this.row - piece.row;
      int cdiff = this.col - piece.col;
      if (coldiff == 0) {
        if (piece.col == col) {
          if (rowdiff > 0 && piece.row < this.row) {
            if (row < piece.row)
              return true;
          } else if (rowdiff < 0 && piece.row > this.row) {
            if (row > piece.row)
              return true;
          }
        }
      } else if (rowdiff == 0) {
        if (piece.row == row) {
          if (coldiff > 0 && piece.col < this.col) {
            if (col < piece.col)
              return true;
          } else if (coldiff < 0 && piece.col > this.col) {
            if (col > piece.col)
              return true;
          }
        }
      }
      if (!(Math.abs(rdiff) == Math.abs(cdiff)))
        continue;
      if ((rowdiff > 0 && coldiff < 0) && (rdiff > 0 && cdiff < 0)) { // works
        // the firstQuadrant
        if (piece.row - row > 0 && piece.col - col < 0)
          return true;
      }

      if ((rowdiff > 0 && coldiff > 0) && (rdiff > 0 && cdiff > 0)) { // works
        // the secondquadrant
        if (piece.row - row > 0 && piece.col - col > 0)
          return true;
      }

      if ((rowdiff < 0 && coldiff > 0) && (rdiff < 0 && cdiff > 0)) { // works
        // the thirdQuadrant
        if (piece.row - row < 0 && piece.col - col > 0)
          return true;
      }

      if ((rowdiff < 0 && coldiff < 0) && (rdiff < 0 && cdiff < 0)) { // works
        // the fourthquadrant
        if (piece.row - row < 0 && piece.col - col < 0)
          return true;
      }
    }
    return false;
  }

  public boolean canMove(int row, int col, ArrayList<ChessPiece> pieces) {
    super.canMove(row, col, pieces);
    int rowdiff = Math.abs(this.row - row);
    int coldiff = Math.abs(this.col - col);
    if (rowdiff == 0 && coldiff == 0)
      return false;
    if (!((rowdiff > 0 != coldiff > 0) || (rowdiff == coldiff)))
      return false;
    if (isBlocked(row, col, pieces))
      return false;
    return true;
  }
}

class RookPiece extends ChessPiece {

  public RookPiece(boolean color, int row, int col) {
    super(color, row, col);
    this.path = "./../assets/pieces/" + pathColor + "/rook" + ".png";
    this.image = new ImageIcon(path).getImage();
  }

  public boolean isBlocked(int row, int col, ArrayList<ChessPiece> pieces) {
    int rowdiff = this.row - row;
    int coldiff = this.col - col;
    for (ChessPiece piece : pieces) {
      if (piece == this)
        continue;
      if (coldiff == 0) {
        if (piece.col == col) {
          if (rowdiff > 0 && piece.row < this.row) {
            if (row < piece.row)
              return true;
          } else if (rowdiff < 0 && piece.row > this.row) {
            if (row > piece.row)
              return true;
          }
        }
      } else if (rowdiff == 0) {
        if (piece.row == row) {
          if (coldiff > 0 && piece.col < this.col) {
            if (col < piece.col)
              return true;
          } else if (coldiff < 0 && piece.col > this.col) {
            if (col > piece.col)
              return true;
          }
        }
      }
    }
    return false;
  }

  public boolean canMove(int row, int col, ArrayList<ChessPiece> pieces) {
    super.canMove(row, col, pieces);
    int rowdiff = Math.abs(this.row - row);
    int coldiff = Math.abs(this.col - col);
    // System.out.println("this is weird" + isBlocked(row, col, pieces));
    if (!(rowdiff > 0 != coldiff > 0))
      return false;
    if (isBlocked(row, col, pieces))
      return false;
    return true;
  }
}

class BishopPiece extends ChessPiece {

  public BishopPiece(boolean color, int row, int col) {
    super(color, row, col);
    this.path = "./../assets/pieces/" + pathColor + "/bishop" + ".png";
    this.image = new ImageIcon(path).getImage();
  }

  public boolean isBlocked(int row, int col, ArrayList<ChessPiece> pieces) {
    int rowdiff = this.row - row;
    int coldiff = this.col - col;
    for (ChessPiece piece : pieces) {
      if (this == piece)
        continue;
      int rdiff = this.row - piece.row;
      int cdiff = this.col - piece.col;
      if (!(Math.abs(rdiff) == Math.abs(cdiff)))
        continue;
      if ((rowdiff > 0 && coldiff < 0) && (rdiff > 0 && cdiff < 0)) { // works
        // the firstQuadrant
        if (piece.row - row > 0 && piece.col - col < 0)
          return true;
      }

      if ((rowdiff > 0 && coldiff > 0) && (rdiff > 0 && cdiff > 0)) { // works
        // the secondquadrant
        if (piece.row - row > 0 && piece.col - col > 0)
          return true;
      }

      if ((rowdiff < 0 && coldiff > 0) && (rdiff < 0 && cdiff > 0)) { // works
        // the thirdQuadrant
        if (piece.row - row < 0 && piece.col - col > 0)
          return true;
      }

      if ((rowdiff < 0 && coldiff < 0) && (rdiff < 0 && cdiff < 0)) { // works
        // the fourthquadrant
        if (piece.row - row < 0 && piece.col - col < 0)
          return true;
      }
    }
    return false;
  }

  public boolean canMove(int row, int col, ArrayList<ChessPiece> pieces) {
    super.canMove(row, col, pieces);
    int rowdiff = Math.abs(this.row - row);
    int coldiff = Math.abs(this.col - col);
    if (rowdiff == 0 && coldiff == 0)
      return false;
    if (!(rowdiff == coldiff))
      return false;
    if (isBlocked(row, col, pieces))
      return false;
    return true;
  }
}

class KnightPiece extends ChessPiece {

  public KnightPiece(boolean color, int row, int col) {
    super(color, row, col);
    this.path = "./../assets/pieces/" + pathColor + "/knight" + ".png";
    this.image = new ImageIcon(path).getImage();
  }

  public boolean canMove(int row, int col, ArrayList<ChessPiece> pieces) {
    super.canMove(row, col, pieces);
    int rowdiff = Math.abs(this.row - row);
    int coldiff = Math.abs(this.col - col);
    if (rowdiff == 0 || coldiff == 0)
      return false;
    return (rowdiff + coldiff == 3);
  }
}

class PawnPiece extends ChessPiece {
  public boolean firstMove = true;
  public boolean enPassant = false;
  public int direction;

  public PawnPiece(boolean color, int row, int col) {
    super(color, row, col);
    this.path = "./../assets/pieces/" + pathColor + "/pawn" + ".png";
    this.image = new ImageIcon(path).getImage();
  }

  public boolean isBlocked(int row, int col, ArrayList<ChessPiece> pieces) {
    int coldiff = this.col - col;

    if (coldiff == 0) {
      for (ChessPiece piece : pieces) {
        if (this == piece)
          continue;
        if (piece.isPosition(row, col))
          return true;
      }
    } else {
      for (ChessPiece piece : pieces) {
        if (this == piece || piece.color == this.color)
          continue;
        if (piece.isPosition(row, col))
          return true;
      }
    }
    return false;
  }

  public boolean canMove(int row, int col, ArrayList<ChessPiece> pieces) {
    super.canMove(row, col, pieces);
    int rowdiff = this.row - row;
    int coldiff = this.col - col;
    direction = color ? 1 : -1;

    if (this.row == (this.color ? 3 : 4) && Math.abs(coldiff) == 1) {
      for (ChessPiece piece : pieces) {
        if (piece.color == this.color || !piece.path.contains("/pawn.png"))
          continue;
        PawnPiece pawnPiece = (PawnPiece) piece;
        if (pawnPiece.isPosition(this.row, col) && pawnPiece.enPassant == true
            && MainPanel.moveNumber - pawnPiece.lastMoveNumber == 1) {
          pieces.remove(piece);
          return true;
        }
      }
    }

    if (rowdiff == direction && coldiff == 0) {
      if (isBlocked(this.row - direction, col, pieces))
        return false;
      if (firstMove == true)
        firstMove = false;
      else if (enPassant)
        enPassant = false;
      return true;
    }

    if (rowdiff == 2 * direction && firstMove == true && coldiff == 0) {
      // DO: check blocked or not
      if (isBlocked(this.row - direction, col, pieces) || isBlocked(this.row - 2 * direction, col, pieces))
        return false;
      if (!enPassant && firstMove)
        enPassant = true;
      firstMove = false;
      return true;
    }

    if (rowdiff == direction && Math.abs(coldiff) == 1) {
      // DO: if opponent piece available return true else return false
      if (isBlocked(this.row - direction, col, pieces))
        return true;
      if (firstMove == true)
        firstMove = false;
      else if (enPassant)
        enPassant = false;
    }

    return false;
  }
}

class MyMouse extends MouseAdapter {

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
