//oop project Aftab Naik, Atharva Nalat

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Main {

  public static void main(String[] args) {
    JFrame chat = new JFrame("chat");
    JFrame chess = new JFrame("Chess");
    MainPanel boardPanel = new MainPanel();
    boardPanel.setLayout(null);
    boardPanel.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, new Color(0, 0, 0)));
    chess.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    chat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    chess.setResizable(false);
    chat.setResizable(false);
    chess.getContentPane().add(boardPanel);
    chess.pack();
    chess.setLocationRelativeTo(null);
    chess.setVisible(true);
    boardPanel.launchClient();
  }

}

class ChatPanel extends JPanel {

}

class MainPanel extends JPanel {

  public static final int WIDTH = Board.SQUARE_SIZE * Board.MAX_COL;
  public static final int HEIGHT = Board.SQUARE_SIZE * Board.MAX_ROW;
  public MyMouse mouse;
  public Game game;

  public MainPanel() {
    mouse = new MyMouse();
    game = new Game(this, mouse);
    setPreferredSize((new Dimension(WIDTH, HEIGHT)));
    setBackground(Color.WHITE);
    addMouseListener(mouse);
    addMouseMotionListener(mouse);
  }

  public void launchClient() {
    game.launch();
    // chat.launch();
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    game.render(g2d);
  }
}

class Game implements Runnable {
  private final int TARGET_FPS = 100;
  private final int FRAME_TIME = 1000 / TARGET_FPS;
  private boolean running;
  private Thread gameThread;
  public Board board;
  public ArrayList<ChessPiece> pieces = new ArrayList<>();
  public MyMouse mouse;
  public ChessPiece activePiece;
  public boolean check;
  public boolean turn = true;
  public static int moveNumber = 0;
  public MainPanel boardPanel;

  public Game(MainPanel boardPanel, MyMouse mouse) {
    this.mouse = mouse;
    board = new Board(pieces);
    this.boardPanel = boardPanel;
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

  public void setPieces() {
    boolean color;
    ChessPiece p;
    synchronized (pieces) {
      color = false; // set white pieces
      for (int i = 0; i < Board.MAX_COL; i++) { // set white pawns
        p = new PawnPiece(color, 1, i, board);
        pieces.add(p);
        board.state[1][i] = p;
      }
      pieces.add(p = new KingPiece(color, 0, 4, board));
      board.state[0][4] = p;
      pieces.add(p = new QueenPiece(color, 0, 3, board));
      board.state[0][3] = p;
      pieces.add(p = new RookPiece(color, 0, 0, board));
      board.state[0][0] = p;
      pieces.add(p = new RookPiece(color, 0, 7, board));
      board.state[0][7] = p;
      pieces.add(p = new KnightPiece(color, 0, 1, board));
      board.state[0][1] = p;
      pieces.add(p = new KnightPiece(color, 0, 6, board));
      board.state[0][6] = p;
      pieces.add(p = new BishopPiece(color, 0, 2, board));
      board.state[0][2] = p;
      pieces.add(p = new BishopPiece(color, 0, 5, board));
      board.state[0][5] = p;
      color = true; // set black pieces
      for (int i = 0; i < Board.MAX_COL; i++) { // set black pawns
        p = new PawnPiece(color, 6, i, board);
        pieces.add(p);
        board.state[6][i] = p;
      }
      pieces.add(p = new KingPiece(color, 7, 4, board));
      board.state[7][4] = p;
      pieces.add(p = new QueenPiece(color, 7, 3, board));
      board.state[7][3] = p;
      pieces.add(p = new RookPiece(color, 7, 0, board));
      board.state[7][0] = p;
      pieces.add(p = new RookPiece(color, 7, 7, board));
      board.state[7][7] = p;
      pieces.add(p = new KnightPiece(color, 7, 1, board));
      board.state[7][1] = p;
      pieces.add(p = new KnightPiece(color, 7, 6, board));
      board.state[7][6] = p;
      pieces.add(p = new BishopPiece(color, 7, 2, board));
      board.state[7][2] = p;
      pieces.add(p = new BishopPiece(color, 7, 5, board));
      board.state[7][5] = p;
    }

  }

  public void update() {
    synchronized (pieces) {
      if (mouse.pressed && !check) {
        ChessPiece piece = board.pieceAtPosition(mouse.pressedY, mouse.pressedX);
        if (piece != null && piece.color == turn) {
          activePiece = piece;
          board.setActiveBlock(mouse.pressedY, mouse.pressedX);
          mouse.x = mouse.pressedX;
          mouse.y = mouse.pressedY;
          check = true;
        }
      }
      if (!mouse.pressed && check) {
        if (board.friendlyPieceAtPosition(mouse.y, mouse.x, turn) == null
            && activePiece.canMove(activePiece.getRow(mouse.y), activePiece.getCol(mouse.x), pieces, true)) {
          activePiece.update(activePiece.getRow(mouse.y), activePiece.getCol(mouse.x));
          pieces.remove(board.enemyPieceAtPosition(mouse.y, mouse.x, turn));
          board.updateActiveBlock(mouse.y, mouse.x);
          turn = !turn;
          activePiece.lastMoveNumber = Game.moveNumber;
          Game.moveNumber++;
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

  public void render(Graphics2D g2d) {
    board.draw(g2d);
    if(board.showPromotionWindow){
      // board.drawPromotionWindow(g2d);
    }
    drawPieces(g2d);
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

  public void run() {
    init();
    long startTime;
    long renderTime;

    while (running) {
      startTime = System.currentTimeMillis();
      // update
      update();
      // render
      boardPanel.repaint();
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

}

class Promotion extends JPanel {

  public boolean showPromotionWindow = false;
  public int row, col ;

  public void drawPromotionWindow(Graphics2D g2d){
    if (row == 0){
      int y = 0 ;

    }
    else if (row == 7){
      
    }
  }
}

class Board {

  public static final int SQUARE_SIZE = 120;
  public static final int MAX_COL = 8;
  public static final int MAX_ROW = 8;
  private final Color DARK = new Color(118, 150, 86);
  private final Color LIGHT = new Color(238, 238, 210);
  public ChessPiece state[][] = new ChessPiece[8][8];
  public ArrayList<ChessPiece> pieces;
  public boolean blackCheck = false;
  public boolean whiteCheck = true;
  public int activeBlockCol;
  public int activeBlockRow;
  public boolean showPromotionWindow = false;

  public Board(ArrayList<ChessPiece> pieces) {
    this.pieces = pieces;
  }

  public void setActiveBlock(int y, int x) {
    if (getRow(y) < 0 || getRow(y) > 7 || getCol(x) < 0 || getCol(x) > 7)
      return;
    activeBlockCol = getCol(x);
    activeBlockRow = getRow(y);
  }

  public void updateActiveBlock(int y, int x) {
    if (getRow(y) < 0 || getRow(y) > 7 || getCol(x) < 0 || getCol(x) > 7)
      return;
    // pieces.remove(state[activeBlockRow][activeBlockCol]);
    state[getRow(y)][getCol(x)] = state[activeBlockRow][activeBlockCol];
    state[activeBlockRow][activeBlockCol] = null;
  }

  public ChessPiece pieceAtPosition(int y, int x) {
    if (getRow(y) < 0 || getRow(y) > 7 || getCol(x) < 0 || getCol(x) > 7)
      return null;
    return state[getRow(y)][getCol(x)];
  }

  public ChessPiece friendlyPieceAtPosition(int y, int x, boolean friendlyPieceColor) {
    if (getRow(y) < 0 || getRow(y) > 7 || getCol(x) < 0 || getCol(x) > 7)
      return null;
    ChessPiece piece = state[getRow(y)][getCol(x)];
    if (piece == null)
      return null;
    if (piece.color != friendlyPieceColor)
      return null;
    return state[getRow(y)][getCol(x)];
  }

  public ChessPiece enemyPieceAtPosition(int y, int x, boolean friendlyPieceColor) {
    if (getRow(y) < 0 || getRow(y) > 7 || getCol(x) < 0 || getCol(x) > 7)
      return null;
    ChessPiece piece = state[getRow(y)][getCol(x)];
    if (piece == null)
      return null;
    if (piece.color == friendlyPieceColor) {
      return null;
    }
    return state[getRow(y)][getCol(x)];
  }

  public int getCol(int x) {
    return x / Board.SQUARE_SIZE;
  }

  public int getRow(int y) {
    return y / Board.SQUARE_SIZE;
  }

  public void remove(int row, int col) {
    pieces.remove(state[row][col]);
    state[row][col] = null;
  }

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

  // public void drawPromotionWindow(int row, int col, Graphics2D g2d){
  //   int y = row * Board.SQUARE_SIZE + Board.SQUARE_SIZE/2 ;
  //   int x = col * Board.SQUARE_SIZE + Board.SQUARE_SIZE/2 ;
  //   g2d.setColor(Color.CYAN) ;
  //   g2d.fillRect(x, y, Board.SQUARE_SIZE/2,  2*Board.SQUARE_SIZE) ;    
  // }
}

abstract class ChessPiece {

  public Image image;
  public int x, y;
  public boolean color;
  public int row, col;
  public String path;
  public String pathColor;
  public int lastMoveNumber = 0;
  public Board board;

  public ChessPiece(boolean color, int row, int col, Board board) {
    this.board = board;
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
      this.update(row, col);
      this.board.state[cur_row][cur_col]= null;
      ChessPiece g = this.board.state[row][col];
      pieces.remove(g);
      this.board.state[row][col] = this;
      if(k.inCheck(pieces)) {
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

class KingPiece extends ChessPiece {

  public KingPiece(boolean color, int row, int col, Board board) {
    super(color, row, col, board);
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

  public boolean canMove(int row, int col, ArrayList<ChessPiece> pieces, boolean r) {
    if(!super.canMove(row, col, pieces, r)) return false;

    int rowdiff = this.row - row;
    int coldiff = this.col - col;
    if (canCastle(row, col))
      return true;
    if (!((Math.abs(rowdiff) | Math.abs(coldiff)) == 1))
      return false;
    return true;
  }
}

class QueenPiece extends ChessPiece {

  public QueenPiece(boolean color, int row, int col, Board board) {
    super(color, row, col, board);
    this.path = "./../assets/pieces/" + pathColor + "/queen" + ".png";
    this.image = new ImageIcon(path).getImage();
  }

  public boolean isBlocked(int row, int col) {
    int rowdiff = this.row - row;
    int coldiff = this.col - col;
    int checkrow;
    int checkcol;

    if (rowdiff > 0 && coldiff < 0) {
      checkcol = this.col + 1;
      checkrow = this.row - 1;
      for (; checkrow > row; checkrow--, checkcol++) {
        if (board.state[checkrow][checkcol] != null) {
          return true;
        }
      }
    }

    else if (rowdiff > 0 && coldiff > 0) {
      checkcol = this.col - 1;
      checkrow = this.row - 1;
      for (; checkrow > row; checkrow--, checkcol--) {
        if (board.state[checkrow][checkcol] != null) {
          return true;
        }
      }
    }

    else if (rowdiff < 0 && coldiff < 0) {
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

    else if (coldiff == 0 && rowdiff > 0) {
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

  public boolean canMove(int row, int col, ArrayList<ChessPiece> pieces, boolean r) {
    if(!super.canMove(row, col, pieces, r)) return false;
    int rowdiff = Math.abs(this.row - row);
    int coldiff = Math.abs(this.col - col);
    if (rowdiff == 0 && coldiff == 0)
      return false;
    if (!((rowdiff > 0 != coldiff > 0) || (rowdiff == coldiff)))
      return false;
    if (isBlocked(row, col))
      return false;
    return true;
  }
}

class RookPiece extends ChessPiece {

  public RookPiece(boolean color, int row, int col, Board board) {
    super(color, row, col, board);
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

  public boolean canMove(int row, int col, ArrayList<ChessPiece> pieces, boolean r) {
    if(!super.canMove(row, col, pieces, r)) return false;
    int rowdiff = Math.abs(this.row - row);
    int coldiff = Math.abs(this.col - col);
    if (!(rowdiff > 0 != coldiff > 0))
      return false;
    if (isBlocked(row, col))
      return false;
    return true;
  }
}

class BishopPiece extends ChessPiece {

  public BishopPiece(boolean color, int row, int col, Board board) {
    super(color, row, col, board);
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

  public boolean canMove(int row, int col, ArrayList<ChessPiece> pieces, boolean r) {
    if(!super.canMove(row, col, pieces, r)) return false;
    int rowdiff = Math.abs(this.row - row);
    int coldiff = Math.abs(this.col - col);
    if (rowdiff == 0 && coldiff == 0)
      return false;
    if (!(rowdiff == coldiff))
      return false;
    if (isBlocked(row, col)) // DO: change implementation
      return false;
    return true;
  }
}

class KnightPiece extends ChessPiece {

  public KnightPiece(boolean color, int row, int col, Board board) {
    super(color, row, col, board);
    this.path = "./../assets/pieces/" + pathColor + "/knight" + ".png";
    this.image = new ImageIcon(path).getImage();
  }

  public boolean canMove(int row, int col, ArrayList<ChessPiece> pieces, boolean r) {
    if(!super.canMove(row, col, pieces, r)) return false;
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

  public PawnPiece(boolean color, int row, int col, Board board) {
    super(color, row, col, board);
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

  public boolean canMove(int row, int col, ArrayList<ChessPiece> pieces, boolean r) {
    if(!super.canMove(row, col, pieces, r)) return false;
    int rowdiff = this.row - row;
    int coldiff = this.col - col;
    direction = color ? 1 : -1;
    ChessPiece piece;

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
      if (firstMove == true)
        firstMove = false;
      else if (enPassant)
        enPassant = false;
      return true;
    }

    if (rowdiff == 2 * direction && firstMove == true && coldiff == 0) {
      // DO: check blocked or not
      if (isBlocked(this.row - direction, col) || isBlocked(this.row - 2 * direction, col))
        return false;
      if (!enPassant && firstMove)
        enPassant = true;
      firstMove = false;
      return true;
    }

    if (rowdiff == direction && Math.abs(coldiff) == 1) {
      // DO: if opponent piece available return true else return false
      if(board.state[row][col] != null && board.state[row][col].color != this.color) return true ;
      if (firstMove == true)
        firstMove = false;
      else if (enPassant)
        enPassant = false;
      return false;
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
