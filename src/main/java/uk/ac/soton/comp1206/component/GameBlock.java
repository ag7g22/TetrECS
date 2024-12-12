package uk.ac.soton.comp1206.component;

import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Visual User Interface component representing a single block in the grid.
 * <p>
 * Extends Canvas and is responsible for drawing itself.
 * <p>
 * Displays an empty square (when the value is 0) or a coloured square depending on value.
 * <p>
 * The GameBlock value should be bound to a corresponding block in the Grid model.
 */
public class GameBlock extends Canvas {

  private static final Logger logger = LogManager.getLogger(GameBlock.class);

  /**
   * AnimationTimer accessed to stop animation when fadeOut effect is finished
   */
  private AnimationTimer animationTimer;

  /**
   * Access to the GameBoard's methods to get the block object at specified position
   */
  private final GameBoard gameBoard;

  /**
   * The set of colours for different pieces
   */
  public static final Color[] COLOURS = {
      Color.TRANSPARENT,
      Color.DEEPPINK,
      Color.RED,
      Color.ORANGE,
      Color.YELLOW,
      Color.YELLOWGREEN,
      Color.LIME,
      Color.GREEN,
      Color.DARKGREEN,
      Color.DARKTURQUOISE,
      Color.DEEPSKYBLUE,
      Color.AQUA,
      Color.AQUAMARINE,
      Color.BLUE,
      Color.MEDIUMPURPLE,
      Color.PURPLE
  };

  /**
   * Constant width of block to be referenced
   */
  private final double width;

  /**
   * Constant height of block to be referenced
   */
  private final double height;

  /**
   * Value storing the inner square's placement inside the tile
   */
  private final double squareFitSize;

  /**
   * Width AND height for the fill for inner square
   */
  private final double squareSize;

  /**
   * Value storing the indicator circle's placement inside the tile
   */
  private final double circleSize;

  /**
   * Width AND height for the stroke of the indicator circle
   */
  private final double circleFitSize;

  /**
   * The column this block exists as in the grid
   */
  private final int x;

  /**
   * The row this block exists as in the grid
   */
  private final int y;

  /**
   * The value of this block (0 = empty, otherwise specifies the colour to render as)
   */
  private final IntegerProperty value = new SimpleIntegerProperty(0);

  /**
   * Create a new single Game Block
   *
   * @param gameBoard the board this block belongs to
   * @param x         the column the block exists in
   * @param y         the row the block exists in
   * @param width     the width of the canvas to render
   * @param height    the height of the canvas to render
   */
  public GameBlock(GameBoard gameBoard, int x, int y, double width, double height) {
    this.gameBoard = gameBoard;
    this.width = width;
    this.height = height;
    this.x = x;
    this.y = y;

    //A canvas needs a fixed width and height
    setWidth(width);
    setHeight(height);

    //Add Square sizes
    squareSize = width * 0.9;
    squareFitSize = (width - squareSize) / 2;
    circleSize = width * 0.3;
    circleFitSize = (width - circleSize) / 2;

    //Do an initial paint
    paint();

    //When the value property is updated, call the internal updateValue method
    value.addListener(this::updateValue);
  }

  /**
   * When the value of this block is updated,
   *
   * @param observable what was updated
   * @param oldValue   the old value
   * @param newValue   the new value
   */
  private void updateValue(ObservableValue<? extends Number> observable, Number oldValue,
      Number newValue) {
    paint();
  }

  /**
   * Handle painting of the block canvas
   */
  public void paint() {
    //If the block is empty, paint as empty
    if (value.get() == 0) {
      paintEmpty();
    } else {
      //If the block is not empty, paint with the colour represented by the value
      paintColor(COLOURS[value.get()]);
    }
  }

  /**
   * Paint this canvas empty
   */
  private void paintEmpty() {
    var gc = getGraphicsContext2D();

    //Clear
    gc.clearRect(0, 0, width, height);

    //Fill Colour
    Color gray = Color.GREY.deriveColor(0, 1, 0.4, 1);
    gc.setFill(gray);
    gc.fillRect(squareFitSize, squareFitSize, squareSize, squareSize);
    getDarkerOutline(gray, 0.7);

    //Border
    gc.setLineWidth(3);
    gc.setStroke(Color.BLACK);
    gc.strokeRect(0, 0, width, height);
  }

  /**
   * Paint this canvas with the given colour
   *
   * @param colour the colour to paint
   */
  private void paintColor(Paint colour) {
    var gc = getGraphicsContext2D();

    //Clear
    gc.clearRect(0, 0, width, height);

    //Fill Colour
    gc.setFill(colour);
    gc.fillRect(squareFitSize, squareFitSize, squareSize, squareSize);
    getDarkerOutline(colour, 0.5);

    //Border
    gc.setLineWidth(3);
    gc.setStroke(Color.BLACK);
    gc.strokeRect(0, 0, width, height);
  }

  /**
   * Paint the block's darker outline to show a "tile look" on the block
   *
   * @param colour     The colour of the block
   * @param brightness How dark the outline is
   */
  public void getDarkerOutline(Paint colour, double brightness) {
    var gc = getGraphicsContext2D();
    Color darker = ((Color) colour).deriveColor(0, 1, brightness, 1);
    gc.setStroke(darker);
    gc.setLineWidth(7);
    gc.strokeRect(squareFitSize, squareFitSize, squareSize, squareSize);

    if (gameBoard.isCurrent()) {
      if (getX() == 1 && getY() == 1) {
        gc.setStroke(darker.deriveColor(0, 1, 0.2, 1));
        gc.setLineWidth(2);
        gc.strokeRoundRect(circleFitSize, circleFitSize, circleSize, circleSize, 100, 100);
      }
    }
  }

  /**
   * Method called to highlight a block on the GameBoard where the mouse/keyboard aim is
   */
  public void hover() {
    var gc = getGraphicsContext2D();
    gc.setFill(Color.GHOSTWHITE.deriveColor(0, 1, 0.7, 0.25));
    gc.fillRect(0, 0, width, height);
  }

  /**
   * Method to animate the blocks fading out when a line is cleared
   *
   * @param block To access the block's colour value for animation
   */
  public void fadeOut(GameBlock block) {
    //Collect the block's colour value to be passed into the animation
    var gc = getGraphicsContext2D();
    var start = COLOURS[block.getValue()];
    var end = Color.GREY.deriveColor(0, 1, 0.4, 1);
    logger.info(start + "->" + end);
    animationTimer = new AnimationTimer() {
      final long startTime = System.nanoTime();
      final double duration = 300;

      @Override
      public void handle(long now) {
        long elapsedMillis = (now - startTime) / 1_000_000;
        double progress = (elapsedMillis / duration);
        if (progress < 1) {
          Color current = (Color) Interpolator.EASE_IN.interpolate(start, end, progress);
          logger.info(current);
          gc.setFill(current);
          gc.fillRect(squareFitSize, squareFitSize, squareSize, squareSize);
          getDarkerOutline(current, 0.6);
          logger.info(progress);
        } else {
          paint();
          animationTimer.stop();
        }
      }
    };
    animationTimer.start();
  }

  /**
   * Get the column of this block
   *
   * @return column number
   */
  public int getX() {
    return x;
  }

  /**
   * Get the row of this block
   *
   * @return row number
   */
  public int getY() {
    return y;
  }

  /**
   * Get the current value held by this block, representing it's colour
   *
   * @return value
   */
  public int getValue() {
    return this.value.get();
  }

  /**
   * Bind the value of this block to another property. Used to link the visual block to a
   * corresponding block in the Grid.
   *
   * @param input property to bind the value to
   */
  public void bind(ObservableValue<? extends Number> input) {
    value.bind(input);
  }

}
