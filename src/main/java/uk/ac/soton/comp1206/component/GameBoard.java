package uk.ac.soton.comp1206.component;

import java.util.ArrayList;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.BlockClickedListener;
import uk.ac.soton.comp1206.game.Grid;

/**
 * A GameBoard is a visual component to represent the visual GameBoard. It extends a GridPane to
 * hold a grid of GameBlocks.
 * <p>
 * The GameBoard can hold an internal grid of it's own, for example, for displaying an upcoming
 * block. It also be linked to an external grid, for the main game board.
 * <p>
 * The GameBoard is only a visual representation and should not contain game logic or model logic in
 * it, which should take place in the Grid.
 */
public class GameBoard extends GridPane {

  private static final Logger logger = LogManager.getLogger(GameBoard.class);

  /**
   * boolean variable used for display grids to not be clickable
   */
  protected boolean isPlayable;

  /**
   * Number of columns in the board
   */
  private final int cols;

  /**
   * Number of rows in the board
   */
  private final int rows;

  /**
   * The visual width of the board - has to be specified due to being a Canvas
   */
  private final double width;

  /**
   * The visual height of the board - has to be specified due to being a Canvas
   */
  private final double height;

  /**
   * The grid this GameBoard represents
   */
  final Grid grid;

  /**
   * The blocks inside the grid
   */
  GameBlock[][] blocks;

  /**
   * The listener to call when a specific block is clicked
   */
  private BlockClickedListener blockClickedListener;

  /**
   * Determines whether to add the circle indicator to the PieceBoard
   */
  private final boolean current;


  /**
   * Create a new GameBoard, based off the given grid, with a visual width and height.
   *
   * @param grid     linked grid
   * @param width    the visual width
   * @param height   the visual height
   * @param playable whether to add the mouse events or not
   * @param current  whether to add the circle indicator or not
   */
  public GameBoard(Grid grid, double width, double height, boolean playable, boolean current) {
    this.cols = grid.getCols();
    this.rows = grid.getRows();
    this.width = width;
    this.height = height;
    this.grid = grid;
    this.current = current;

    //Build the GameBoard
    isPlayable = playable;
    build();
  }

  /**
   * Method to be accessed by GameBlock to whether to add interactivity or not
   * @return boolean
   */
  public boolean isCurrent() {
    return current;
  }

  /**
   * Get a specific block from the GameBoard, specified by it's row and column
   *
   * @param x column
   * @param y row
   * @return game block at the given column and row
   */
  public GameBlock getBlock(int x, int y) {
    return blocks[x][y];
  }

  /**
   * Build the GameBoard by creating a block at every x and y column and row
   */
  protected void build() {
    //logger.info("Building grid: {} x {}",cols,rows);

    setMaxWidth(width);
    setMaxHeight(height);

    setGridLinesVisible(true);

    blocks = new GameBlock[cols][rows];

    for (var y = 0; y < rows; y++) {
      for (var x = 0; x < cols; x++) {
        createBlock(x, y);
      }
    }
  }

  /**
   * Create a block at the given x and y position in the GameBoard
   *
   * @param x column
   * @param y row
   */
  protected void createBlock(int x, int y) {
    var blockWidth = width / cols;
    var blockHeight = height / rows;

    //Create a new GameBlock UI component
    GameBlock block = new GameBlock(this, x, y, blockWidth, blockHeight);

    //Add to the GridPane
    add(block, x, y);

    //Add to our block directory
    blocks[x][y] = block;

    //Link the GameBlock component to the corresponding value in the Grid
    block.bind(grid.getGridProperty(x, y));

    if (isPlayable) {
      //Add a mouse click handler to the block to trigger GameBoard blockClicked method
      block.setOnMouseClicked((e) -> blockClicked(e, block));

      block.setOnMouseEntered(event -> block.hover());

      block.setOnMouseExited(event -> block.paint());
    }
  }

  /**
   * Set the listener to handle an event when a block is clicked
   *
   * @param listener listener to add
   */
  public void setOnBlockClick(BlockClickedListener listener) {
    this.blockClickedListener = listener;
  }

  /**
   * Triggered when a block is clicked. Call the attached listener.
   *
   * @param event mouse event
   * @param block block clicked on
   */
  private void blockClicked(MouseEvent event, GameBlock block) {
    logger.info("Block clicked: {}", block);

    if (blockClickedListener != null) {
      blockClickedListener.blockClicked(block);
    }
  }

  /**
   * Iterates every GameBlock to apply the fadeOut animation to
   *
   * @param coordinates the coordinates of the GameBlock
   */
  public void fadeOut(ArrayList<GameBlockCoordinate> coordinates) {
    logger.info("Faded gameBlocks");
    for (var coordinate : coordinates) {
      var block = getBlock(coordinate.getX(), coordinate.getY());
      block.fadeOut(block);
    }
  }

}
