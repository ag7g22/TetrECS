package uk.ac.soton.comp1206.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;

/**
 * The PieceBoard class is a specifically made to display playing pieces on a board, to be used by
 * the current board AND following board.
 */
public class PieceBoard extends GameBoard {

  private static final Logger logger = LogManager.getLogger(PieceBoard.class);

  /**
   * Create new PieceBoard to display a playing piece
   *
   * @param width   width of the PieceBoard
   * @param height  height of the PieceBoard
   * @param current boolean whether to add centre indicator to the board
   */
  public PieceBoard(double width, double height, boolean current) {
    super(new Grid(3, 3), width, height, false, current);
  }

  /**
   * Setting the board display to the piece
   *
   * @param piece the GamePiece being displayed
   */
  public void setBoard(GamePiece piece) {
    //resetting board display
    for (int x = 0; x < grid.getRows(); x++) {
      for (int y = 0; y < grid.getCols(); y++) {
        grid.set(x, y, 0);
      }
    }
    //setting grid display
    int gridX = 0;
    int gridY = 0;
    for (var rows : piece.getBlocks()) {
      for (var block : rows) {
        if (block > 0 && grid.get(gridX, gridY) == 0) {
          grid.set(gridX, gridY, piece.getValue());
        }
        gridX++;
      }
      gridY++;
      gridX = 0;
    }
  }
}
