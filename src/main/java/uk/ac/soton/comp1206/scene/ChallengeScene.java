package uk.ac.soton.comp1206.scene;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.Utility.Multimedia;
import uk.ac.soton.comp1206.Utility.TextReader;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GameInterface;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the
 * game.
 */
public class ChallengeScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(MenuScene.class);

  /**
   * To get access to the GameBoard
   */
  protected GameBoard board;

  /**
   * PieceBoard holding the current piece
   */
  protected PieceBoard currentPieceBoard;

  /**
   * PieceBoard holding the following piece
   */
  protected PieceBoard followingPieceBoard;

  /**
   * StackPane to switch screens, stacks the pause and gameOver screen on top
   */
  protected StackPane challengePane;

  /**
   * Screen that appears when you pause the game
   */
  private BorderPane pausedGame;

  /**
   * Screen that appears when you lose the game
   */
  protected BorderPane gameOver;

  /**
   * Holding the Game Object currently used
   */
  protected Game game;

  /**
   * String to update the highScore
   */
  private String currentHighScore;

  /**
   * Text that displays the highScore
   */
  private Text highScoreHolder;

  /**
   * Block that the keyboard aim is currently on
   */
  protected GameBlock block;

  /**
   * X coordinate where the aim is currently on
   */
  protected int x = 2;

  /**
   * Y coordinate where the aim is currently on
   */
  protected int y = 2;

  /**
   * Create a new Single Player challenge scene
   *
   * @param gameWindow the Game Window
   */
  public ChallengeScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Challenge Scene");
  }

  /**
   * Initialise the scene and start the game
   */
  @Override
  public void initialise() {
    logger.info("Initialising Challenge");
    Multimedia.playMusic("FIFTY FIFTY - Cupid.mp3");

    pausedGame = setupPauseScreen();
    gameOver = setupGameOverScreen();

    setupKeyBinds();

    game.start();
  }

  /**
   * Build the Challenge window
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    setupGame();

    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    challengePane = new StackPane();
    challengePane.setMaxWidth(gameWindow.getWidth());
    challengePane.setMaxHeight(gameWindow.getHeight());
    challengePane.getStyleClass().add("challenge-background");
    root.getChildren().add(challengePane);

    var mainPane = new BorderPane();
    challengePane.getChildren().add(mainPane);

    board = new GameBoard(game.getGrid(), gameWindow.getWidth() / 2, gameWindow.getWidth() / 2,
        true, false);
    mainPane.setCenter(board);

    //Handle block on gameboard grid being clicked
    board.setOnBlockClick(this::blockClicked);

    //Add the PieceBoards
    var boards = new VBox();
    mainPane.setRight(boards);

    currentPieceBoard = new PieceBoard(120, 120, true);
    VBox current = makePieceBoard(currentPieceBoard, new Text("Current"));

    followingPieceBoard = new PieceBoard(100, 100, false);
    VBox following = makePieceBoard(followingPieceBoard, new Text("Next"));

    boards.getChildren().addAll(current, following);
    boards.setAlignment(Pos.CENTER);
    boards.setSpacing(25);
    boards.setPrefWidth(gameWindow.getWidth() / 4);
    boards.setTranslateX(-25);

    //Handling events for next pieces
    game.setNextPieceListener((piece1, piece2) -> {
      currentPieceBoard.setBoard(piece1);
      followingPieceBoard.setBoard(piece2);
    });

    //Taking in coordinate blocks to animate GameBoard
    game.setOnLineCleared(coordinates -> {
      board.fadeOut(coordinates);
      shakeBoard(board, 10);
    });

    var gaming = new GameInterface();
    //Link UI elements (No need for bidirectional as user does NOT interact with score elements)
    gaming.lifeProperty().bind(Game.lifeProperty());
    gaming.scoreProperty().bind(Game.scoreProperty());
    gaming.levelProperty().bind(Game.levelProperty());
    gaming.multiplierProperty().bind(Game.multiplierProperty());

    //Setting up timer and High Score
    VBox top = new VBox();
    Text highScore = getHighScore();
    top.setAlignment(Pos.TOP_CENTER);
    top.getChildren().addAll(setupProgressBar(), highScore, gaming);
    mainPane.setTop(top);

  }

  /**
   * Handle when a block is clicked
   *
   * @param gameBlock the Game Block that was clocked
   */
  protected void blockClicked(GameBlock gameBlock) {
    shakeBoard(board, 1);
    game.blockClicked(gameBlock);
  }

  /**
   * Setup the game object and model
   */
  public void setupGame() {
    logger.info("Starting a new challenge");

    //Start new game
    game = new Game(5, 5);
    logger.info("Game object: " + game);
  }

  /**
   * Makes the PieceBoard component
   *
   * @param pieceBoard pieceBoard object
   * @param text       current or next
   * @return VBox
   */
  protected VBox makePieceBoard(PieceBoard pieceBoard, Text text) {
    VBox vBox = new VBox();
    text.getStyleClass().add("pieces");
    vBox.getChildren().addAll(pieceBoard, text);
    vBox.setAlignment(Pos.CENTER);
    vBox.setSpacing(15);
    return vBox;
  }

  /**
   * Method to set up the timer UI at the top
   *
   * @return The constructed ProgressBar
   */
  protected ProgressBar setupProgressBar() {
    var timerBar = new ProgressBar(1);
    timerBar.setMaxWidth(gameWindow.getWidth());

    game.setOnGameLoop((time) -> {
      if (game.getScore() > Integer.parseInt(currentHighScore)) {
        currentHighScore = String.valueOf(game.getScore());
        highScoreHolder.setText("High Score: " + currentHighScore);
      }
      //Setting timer colours
      if (time <= 3 && time > 0) {
        timerBar.getStyleClass().remove("progress-bar");
        timerBar.getStyleClass().remove("progress-bar-warning");
        timerBar.getStyleClass().add("progress-bar-urgent");
      } else if (time <= 5 && time > 3) {
        timerBar.getStyleClass().remove("progress-bar-urgent");
        timerBar.getStyleClass().remove("progress-bar");
        timerBar.getStyleClass().add("progress-bar-warning");
      } else if ((time <= 20 && time > 5)) {
        timerBar.getStyleClass().remove("progress-bar-urgent");
        timerBar.getStyleClass().remove("progress-bar-warning");
        timerBar.getStyleClass().add("progress-bar");
      }
      Timeline timeline = new Timeline();
      double numFrames = 50;
      double fromValue = timerBar.getProgress();
      double toValue = time / 10;
      double step = (fromValue - toValue) / numFrames;
      for (int i = 0; i < numFrames; i++) {
        double progress = fromValue - i * step;
        KeyFrame keyFrame = new KeyFrame(Duration.millis((200) * (double) i / numFrames),
            event -> Platform.runLater(() -> timerBar.setProgress(progress)));
        timeline.getKeyFrames().add(keyFrame);
      }
      timeline.play();
    });

    return timerBar;
  }

  /**
   * Setting up the pause screen
   *
   * @return the pause screen
   */
  private BorderPane setupPauseScreen() {
    pausedGame = new BorderPane();
    pausedGame.getStyleClass().add("stopped-game");
    var pauseIcon = new ImageView(
        new Image(getClass().getResource("/images/Pause.png").toExternalForm()));
    pauseIcon.setPreserveRatio(true);
    pauseIcon.setTranslateY(30);
    pauseIcon.setFitWidth(90);
    pauseIcon.setFitHeight(90);
    pausedGame.setTop(pauseIcon);

    //Set of buttons for pause menu
    var pauseButtons = new VBox();
    pauseButtons.getChildren().add(makeButton("Resume"));
    pauseButtons.getChildren().add(makeButton("Retry"));
    pauseButtons.getChildren().add(makeButton("Quit"));

    pausedGame.setCenter(pauseButtons);
    pauseButtons.setTranslateY(120);
    pauseButtons.setSpacing(20);
    pauseButtons.setAlignment(Pos.BASELINE_CENTER);

    return pausedGame;
  }

  /**
   * Setting up the Game Over screen
   *
   * @return the Game Over screen
   */
  protected BorderPane setupGameOverScreen() {
    gameOver = new BorderPane();
    gameOver.setMaxWidth(gameWindow.getWidth() * 1.6);

    gameOver.getStyleClass().add("stopped-game");
    Text title = new Text("GAME OVER");
    title.getStyleClass().add("bigTitle");
    gameOver.setTop(title);
    title.setTranslateX(135);
    title.setTranslateY(140);

    var gameOverButtons = new HBox();
    gameOver.setBottom(gameOverButtons);

    gameOverButtons.getChildren().add(makeButton("Retry"));
    gameOverButtons.getChildren().add(makeButton("Scores"));
    gameOverButtons.getChildren().add(makeButton("Quit"));
    gameOverButtons.setSpacing(20);
    gameOverButtons.setTranslateY(-80);
    gameOverButtons.setAlignment(Pos.BASELINE_CENTER);

    //event for game over
    game.setOnGameOver(() -> {
      logger.info("Making game over screen: " + gameOver);
      challengePane.getChildren().add(gameOver);
      shakeScreen(gameOver, 2);
      Multimedia.playMusic("GameOver.mp3");
    });

    return gameOver;
  }

  /**
   * Factory method to make buttons of similar functionality
   *
   * @param text what type of button it is
   * @return The newly made button
   */
  private Button makeButton(String text) {
    Button button = new Button(text);
    button.getStyleClass().add("menuItem");
    button.setOnMouseEntered(event -> button.getStyleClass().add("menuItem:hover"));
    if ("Resume".equals(text)) {
      button.setOnMouseClicked(event -> {
        game.resumeGame();
        challengePane.getChildren().remove(pausedGame);
      });
    } else if ("Retry".equals(text)) {
      button.setOnMouseClicked(event -> {
        gameWindow.startChallenge();
      });
    } else if ("Quit".equals(text)) {
      button.setOnMouseClicked(event -> gameWindow.startMenu());
    } else if ("Scores".equals(text)) {
      button.setOnMouseClicked(event -> gameWindow.startScores());
    }
    return button;
  }

  /**
   * Reads the first lien of the score file
   *
   * @return the local highScore
   */
  public Text getHighScore() {
    //Return first line of ScoreBoard
    TextReader reader = new TextReader("src/scores.txt");
    //Retrieving the score file, or creating NEW score file
    String[] highScorer = reader.getLine().split(":");
    currentHighScore = highScorer[1];
    highScoreHolder = new Text("High Score: " + currentHighScore);
    highScoreHolder.getStyleClass().add("highScore");
    return highScoreHolder;
  }

  /**
   * Accessor method to pass the game state to ScoresScene
   *
   * @return the current game object
   */
  public Game getGame() {
    return game;
  }

  /**
   * Method to update the UI where the keyboard aim is
   *
   * @param x new X coordinate
   * @param y new Y coordinate
   */
  void aim(int x, int y) {
    if (block == null) {
      block = board.getBlock(x, y);
    }
    block.paint();
    block = board.getBlock(x, y);
    block.hover();
  }

  /**
   * KeyBinds for the controls and to pause game
   */
  void setupKeyBinds() {
    scene.setOnKeyPressed(event -> {
      KeyCode keyCode = event.getCode();
      if (!challengePane.getChildren().contains(pausedGame) && !challengePane.getChildren()
          .contains(gameOver)) {
        switch (keyCode) {
          case R, SPACE -> {
            game.swapCurrentPiece();
            animatePieceBoard(followingPieceBoard);
            animatePieceBoard(currentPieceBoard);
            Multimedia.playAudio("BlockStump.mp3");
          }
          case Q -> { //rotate left
            game.rotateCurrentPiece();
            animatePieceBoard(currentPieceBoard);
            Multimedia.playAudio("rotatePiece.mp3");
          }
          case E -> { //rotate right
            for (var i = 0; i < 3; i++) {
              game.rotateCurrentPiece();
            }
            animatePieceBoard(currentPieceBoard);
            Multimedia.playAudio("rotatePiece.mp3");
          }
          case W -> {
            if (y > 0) {
              y--;
              aim(x, y);
            }
          }
          case A -> {
            if (x > 0) {
              x--;
              aim(x, y);
            }
          }
          case S -> {
            if (y < 4) {
              y++;
              aim(x, y);
            }
          }
          case D -> {
            if (x < 4) {
              x++;
              aim(x, y);
            }
          }
          case ENTER -> blockClicked(block);
          case ESCAPE -> {
            game.pauseGame();
            challengePane.getChildren().add(pausedGame);
          }
        }
      } else if ((keyCode == KeyCode.ESCAPE && !challengePane.getChildren().contains(gameOver))
          && challengePane.getChildren().contains(pausedGame)) {
        game.resumeGame();
        challengePane.getChildren().remove(pausedGame);
      }
    });
  }

  //animation methods

  /**
   * Animates the PieceBoard being swapped
   *
   * @param pieceBoard the PieceBoard in the game
   */
  void animatePieceBoard(PieceBoard pieceBoard) {
    //logger.info("Animating Pieceboard");
    Timeline timeline = new Timeline(
        new KeyFrame(Duration.ZERO, new KeyValue(pieceBoard.scaleXProperty(), 1),
            new KeyValue(pieceBoard.scaleYProperty(), 1)),
        new KeyFrame(Duration.millis(100), new KeyValue(pieceBoard.scaleXProperty(), 1.05),
            new KeyValue(pieceBoard.scaleYProperty(), 1.05)),
        new KeyFrame(Duration.ZERO, new KeyValue(pieceBoard.scaleXProperty(), 1),
            new KeyValue(pieceBoard.scaleYProperty(), 1)),
        new KeyFrame(Duration.millis(250))
    );
    timeline.setCycleCount(1);
    timeline.play();
  }

  /**
   * Animates a shake effect for the GameBoard when pieces are being played on it
   *
   * @param board the GameBoard object
   * @param shake the intensity of the shake
   */
  void shakeBoard(GameBoard board, int shake) {
    logger.info("Animating GameBoard shake");
    Timeline timeline = new Timeline(
        new KeyFrame(Duration.seconds(0),
            new KeyValue(board.translateXProperty(), 0)),
        new KeyFrame(Duration.millis(10),
            new KeyValue(board.translateXProperty(), -shake)),
        new KeyFrame(Duration.millis(20),
            new KeyValue(board.translateXProperty(), shake)),
        new KeyFrame(Duration.millis(30),
            new KeyValue(board.translateXProperty(), -shake)),
        new KeyFrame(Duration.millis(40),
            new KeyValue(board.translateXProperty(), shake)),
        new KeyFrame(Duration.millis(50),
            new KeyValue(board.translateXProperty(), -shake)),
        new KeyFrame(Duration.millis(60),
            new KeyValue(board.translateXProperty(), shake)),
        new KeyFrame(Duration.millis(70),
            new KeyValue(board.translateXProperty(), -shake)),
        new KeyFrame(Duration.millis(80),
            new KeyValue(board.translateXProperty(), shake)),
        new KeyFrame(Duration.millis(90),
            new KeyValue(board.translateXProperty(), -shake)),
        new KeyFrame(Duration.millis(200),
            new KeyValue(board.translateXProperty(), 0))
    );
    timeline.setCycleCount(1);
    timeline.play();
  }

  /**
   * Animates a shake effect when the GameOver screen sppears
   *
   * @param board the GameOver screen
   * @param shake intensity of shake
   */
  void shakeScreen(BorderPane board, int shake) {
    logger.info("Screen shake");
    Timeline timeline = new Timeline(
        new KeyFrame(Duration.seconds(0),
            new KeyValue(board.translateXProperty(), 0)),
        new KeyFrame(Duration.millis(10),
            new KeyValue(board.translateXProperty(), -shake)),
        new KeyFrame(Duration.millis(20),
            new KeyValue(board.translateXProperty(), shake)),
        new KeyFrame(Duration.millis(30),
            new KeyValue(board.translateXProperty(), -shake)),
        new KeyFrame(Duration.millis(40),
            new KeyValue(board.translateXProperty(), shake)),
        new KeyFrame(Duration.millis(50),
            new KeyValue(board.translateXProperty(), -shake)),
        new KeyFrame(Duration.millis(60),
            new KeyValue(board.translateXProperty(), shake)),
        new KeyFrame(Duration.millis(70),
            new KeyValue(board.translateXProperty(), -shake)),
        new KeyFrame(Duration.millis(80),
            new KeyValue(board.translateXProperty(), shake)),
        new KeyFrame(Duration.millis(90),
            new KeyValue(board.translateXProperty(), -shake)),
        new KeyFrame(Duration.millis(200),
            new KeyValue(board.translateXProperty(), 0))
    );
    timeline.setCycleCount(1);
    timeline.play();
  }

}
