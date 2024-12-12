package uk.ac.soton.comp1206.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.Utility.Multimedia;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.GameOverListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.NextPieceListener;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to
 * manipulate the game state and to handle actions made by the player should take place inside this
 * class.
 */
public class Game {

  private static final Logger logger = LogManager.getLogger(Game.class);

  /**
   * Stores the current playing piece
   */
  protected GamePiece currentPiece;

  /**
   * Stores the following piece after currentPiece
   */
  protected GamePiece followingPiece;

  /**
   * The value the timer counts down from
   */
  protected static final int startingTime = 10;

  /**
   * The value the lives starts from
   */
  protected static final int startingLives = 3;

  /**
   * Local variable to store currently used timer to start and stop it
   */
  protected Timer timer;

  /**
   * Local variable to store the time delay between calling gameLoop
   */
  protected int timeDelay;

  /**
   * Flag to maintain consistent timer countdown, between gameLoop and playing a piece
   */
  protected boolean isTimeRestarted = false;

  /**
   * Listener to update UI of the pieces on the PieceBoards
   */
  protected NextPieceListener pieceListener;

  /**
   * Listener to update the UI of the timer
   */
  protected GameLoopListener loopListener;

  /**
   * Listener to call the fadeOut method after collecting the cleared blocks
   */
  protected LineClearedListener lineListener;

  /**
   * Listener to update the screen to the gameOver screen after losing a game
   */
  protected GameOverListener gameOverListener;

  /**
   * Represents the score on the UI
   */
  protected static final SimpleIntegerProperty score = new SimpleIntegerProperty(0);

  /**
   * Represents the level on the UI
   */
  protected static final SimpleIntegerProperty level = new SimpleIntegerProperty(0);

  /**
   * Represents the lives on the UI
   */
  protected static final SimpleIntegerProperty lives = new SimpleIntegerProperty(startingLives);

  /**
   * Represents the multiplier on the UI
   */
  protected static final SimpleIntegerProperty multiplier = new SimpleIntegerProperty(1);

  /**
   * Represents the current time on timer
   */
  protected static final SimpleDoubleProperty timeLeft = new SimpleDoubleProperty(startingTime);

  /**
   * Number of rows
   */
  protected final int rows;

  /**
   * Number of columns
   */
  protected final int cols;

  /**
   * The grid model linked to the game
   */
  protected final Grid grid;

  /**
   * Create a new game with the specified rows and columns. Creates a corresponding grid model.
   *
   * @param cols number of columns
   * @param rows number of rows
   */
  public Game(int cols, int rows) {
    this.cols = cols;
    this.rows = rows;

    //Create a new grid model to represent the game state
    this.grid = new Grid(cols, rows);
  }

  /**
   * Start the game
   */
  public void start() {
    logger.info("Starting game");
    initialiseGame();
  }

  /**
   * Initialise a new game and set up anything that needs to be done at the start
   */
  public void initialiseGame() {
    logger.info("Initialising game");

    //Setup Timer and the task calling gameLoop
    timer = new Timer();
    score.set(0);
    level.set(0);
    lives.set(startingLives);
    timeLeft.set(startingTime);
    timeDelay = 1200;
    logger.info("Scheduling Timer in initialisation: " + timer);
    timer.schedule(getTask(), timeDelay, getTimerDelay());

    //Spawn pieces
    currentPiece = spawnPiece();
    followingPiece = spawnPiece();
    pieceListener.nextPiece(currentPiece, followingPiece);
  }

  /**
   * Gets a new instance of the TimerTask to be scheduled by a new Timer Object
   * @return TimerTask looping gameLoop
   */
  protected TimerTask getTask() {
    return new TimerTask() {
      @Override
      public void run() {
        gameLoop();
      }
    };
  }

  /**
   * Handle what should happen when a particular block is clicked
   *
   * @param gameBlock the block that was clicked
   */
  public void blockClicked(GameBlock gameBlock) {
    //Get the position of this block
    int x = gameBlock.getX();
    int y = gameBlock.getY();

    //Get block values BEFORE and AFTER attempting to play piece
    int previousValue = grid.get(x, y);
    grid.playPiece(currentPiece, x, y);
    int newValue = grid.get(x, y);

    //New grid is checked if there is a line, call in next piece
    if (previousValue != newValue) {
      Multimedia.playAudio("PlaceBlock.mp3");
      afterPiece();
      nextPiece();
      restartTime();
    } else {
      Multimedia.playAudio("BlockStump.mp3");
    }

  }

  /**
   * Listener is activated when piece is played
   *
   * @param listener NextPieceListener
   */
  public void setNextPieceListener(NextPieceListener listener) {
    this.pieceListener = listener;
  }

  /**
   * Activated every time gameLoop is called
   *
   * @param listener GameLoopListener
   */
  public void setOnGameLoop(GameLoopListener listener) {
    this.loopListener = listener;
  }

  /**
   * Activated when line is cleared
   *
   * @param listener LineClearedListener
   */
  public void setOnLineCleared(LineClearedListener listener) {
    this.lineListener = listener;
  }

  /**
   * Activated when game is finished
   *
   * @param listener GameOverListener
   */
  public void setOnGameOver(GameOverListener listener) {
    this.gameOverListener = listener;
  }

  /**
   * Get the grid model inside this game representing the game state of the board
   *
   * @return game grid model
   */
  public Grid getGrid() {
    return grid;
  }

  /**
   * Get the number of columns in this game
   *
   * @return number of columns
   */
  public int getCols() {
    return cols;
  }

  /**
   * Get the number of rows in this game
   *
   * @return number of rows
   */
  public int getRows() {
    return rows;
  }

  /**
   * Creates a new random GamePiece to be played in the game
   *
   * @return the newly generated GamePiece
   */
  public GamePiece spawnPiece() {
    Random rand = new Random();
    GamePiece newPiece = GamePiece.createPiece(rand.nextInt(15), rand.nextInt(4));
    logger.info("created Piece: " + newPiece);
    return newPiece;
  }

  /**
   * Passes the next piece to currentPiece and queues the next piece in followingPiece
   */
  public void nextPiece() {
    currentPiece = followingPiece;
    followingPiece = spawnPiece();
    pieceListener.nextPiece(currentPiece, followingPiece);
  }

  /**
   * Rotates the piece and uses the listener to update the UI
   */
  public void rotateCurrentPiece() {
    currentPiece.rotate();
    pieceListener.nextPiece(currentPiece, followingPiece);
  }

  /**
   * Swaps the pieces between current and following, listeners updates the UI
   */
  public void swapCurrentPiece() {
    //temporary variable to store the current piece
    var swappedPiece = currentPiece;
    currentPiece = followingPiece;
    followingPiece = swappedPiece;
    pieceListener.nextPiece(currentPiece, followingPiece);
  }

  /**
   * Iterates each block per row and column to count complete lines and cleared blocks Also collects
   * all the coordinates of the blocks to be passed to the fadeOut method.
   */
  public void afterPiece() {
    var clearedBlocks = new HashSet<IntegerProperty>();
    var coordinates = new ArrayList<GameBlockCoordinate>();
    GameBlockCoordinate coord;

    // variables needed for the score method
    int clearedLines = 0;
    var blockNum = 0;

    //Checking vertical lines
    for (var x = 0; x <= getCols(); x++) {
      int count = 0;
      for (var y = 0; y <= getRows(); y++) {
        if (grid.get(x, y) > 0) {
          count++;
        }
      }
      if (count == getCols()) {
        logger.info("Found a full vertical line!");
        for (var y = 0; y <= getRows() - 1; y++) {
          clearedBlocks.add(grid.getGridProperty(x, y));
          coord = new GameBlockCoordinate(x, y);
          coordinates.add(coord);
        }
        Multimedia.playAudio("LineCleared.mp3");
        clearedLines++;
      }
    }
    //Checking Horizontal Lines
    for (var y = 0; y <= getRows(); y++) {
      int count = 0;
      for (var x = 0; x <= getCols(); x++) {
        if (grid.get(x, y) > 0) {
          count++;
        }
      }
      if (count == getRows()) {
        logger.info("Found a full horizontal line!");
        for (var x = 0; x <= getCols() - 1; x++) {
          clearedBlocks.add(grid.getGridProperty(x, y));
          coord = new GameBlockCoordinate(x, y);
          coordinates.add(coord);
        }
        Multimedia.playAudio("LineCleared.mp3");
        clearedLines++;
      }
    }
    lineListener.lineCleared(coordinates);
    for (var blocks : clearedBlocks) {
      blocks.set(0);
      blockNum++;
    }
    logger.info("Number of cleared lines: " + clearedLines);
    logger.info("Number of cleared blocks: " + blockNum);
    score(clearedLines, blockNum);
  }

  /**
   * Updates the score property, restarts timer and lowers timeDelay if reached a new level
   *
   * @param clearedLines Used for score calculation and determines whether to update score or not
   * @param blockNum     Used for score calculation
   */
  public void score(int clearedLines, int blockNum) {
    if (clearedLines != 0) {
      //updating score THEN THE multiplier if at least one line is cleared
      score.set(score.get() + (clearedLines * blockNum * 10 * multiplier.get()));
      multiplier.set(multiplier.get() + 1);

      //calculation for updating to higher level
      if (score.get() >= (level.get() + 1) * 1000) {
        level.set(level.get() + 1);
        Multimedia.playAudio("level.wav");
        getTimerDelay();
        timer.cancel();
        timer = new Timer();
        timer.schedule(getTask(), timeDelay, timeDelay);
      }

    } else if (blockNum < 1 && multiplier.get() > 1) {
      //If there are no cleared lines multiplier resets
      multiplier.set(1);
      logger.info("Multiplier reset!");
    }
  }

  //Timer methods
  /**
   * Calculates a new time delay value when leveling up
   * @return new timeDelay value for gameLoop to speed up
   */
  public int getTimerDelay() {
    //So the pausing method doesn't affect the change in timeDelay
    if (timeDelay > 250) {
      timeDelay = timeDelay - (50 * level.get());
      if (timeDelay < 250) {
        timeDelay = 250;
      }
    }
    return timeDelay;
  }

  /**
   * Timer goes down every iteration of timerTask, able to stop game if no lives are left
   */
  public void gameLoop() {
    logger.info(timeLeft.get());
    if (timeLeft.get() > 0) {
      timeLeft.set(timeLeft.get() - 1);
    } else {
      if (lives.get() > 0) {
        lives.set(lives.get() - 1);
        Multimedia.playAudio("lifelose.wav");
        multiplier.set(0);
        nextPiece();
        restartTime();
      } else {
        stopGame();
        Multimedia.playAudio("GameOverSFX.mp3");
      }
    }
    if (!isTimeRestarted) {
      loopListener.timerListener(timeLeft.get());
    }
  }

  /**
   * When time has restarted a listener is activated to update the timer UI to restart
   */
  void restartTime() {
    //To sync up update of timeBar
    timeLeft.set(startingTime);
    isTimeRestarted = true;
    loopListener.timerListener(timeLeft.get());
    isTimeRestarted = !isTimeRestarted;
  }

  /**
   * Stops all the game elements running after losing the game Listener activates to then display
   * the gameOver screen in challenge scene
   */
  public void stopGame() {
    logger.info("Game is stopping");
    Platform.runLater(() -> {
      timer.cancel();
      gameOverListener.gameFinished();
    });
  }

  /**
   * Temporarily stops the game elements running to pause the game
   */
  public void pauseGame() {
    Multimedia.pauseMusic();
    Multimedia.playAudio("Pause.mp3");
    timer.cancel();
  }

  /**
   * Creates a new timer object to resume the game and timer
   */
  public void resumeGame() {
    Multimedia.pauseMusic();
    //logger.info("Scheduling Timer in resume: " + timer);
    timer = new Timer();
    timer.schedule(getTask(), timeDelay, timeDelay);
  }

  //accessor methods

  /**
   * Accessor to get the score from final game state
   * @return score property value
   */
  public Integer getScore() {
    return score.get();
  }

  public static StringBinding scoreProperty() {
    return score.asString();
  }

  public static StringBinding levelProperty() {
    return level.asString();
  }

  public static StringBinding lifeProperty() {
    return lives.asString();
  }

  public static StringBinding multiplierProperty() {
    return multiplier.asString();
  }
}
