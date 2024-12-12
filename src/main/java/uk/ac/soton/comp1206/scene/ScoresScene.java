package uk.ac.soton.comp1206.scene;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.Utility.Multimedia;
import uk.ac.soton.comp1206.Utility.TextReader;
import uk.ac.soton.comp1206.component.ScoresList;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The ScoresScene class is the scene where you see the local and online ScoreBoard after playing a
 * game.
 */
public class ScoresScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(MenuScene.class);

  /**
   * HBox holding the online and offline scoreLists
   */
  private HBox scoreBoards;

  /**
   * HBox holding the prompt to enter a new name
   */
  private HBox prompt;

  /**
   * Pane holding the contents of the ScoresScene
   */
  private BorderPane scoresPane;

  /**
   * Menu of buttons to retry the game or quit to menu
   */
  private HBox buttons;

  /**
   * Stores the final game state after playing a game
   */
  private Game game;

  /**
   * To get access to the scores.txt file
   */
  private final File file;

  /**
   * Stores current line being read from the file to to write to
   */
  protected String line;

  /**
   * ArrayList of scores to put into the Observable lists
   */
  private ArrayList<Pair<String, Integer>> scores;

  /**
   * Observable List for the local scores
   */
  private final ObservableList<Pair<String, Integer>> scoreList = FXCollections.observableArrayList();

  /**
   * ArrayList of local scores read from file
   */
  private final SimpleListProperty<Pair<String, Integer>> localScores = new SimpleListProperty<>(
      scoreList);

  /**
   * Observable list for online scores
   */
  private final ObservableList<Pair<String, Integer>> onlineScoreList = FXCollections.observableArrayList();

  /**
   * ArrayList of online scores received from communicator
   */
  private final SimpleListProperty<Pair<String, Integer>> remoteScores = new SimpleListProperty<>(
      onlineScoreList);

  /**
   * Create a new scene, passing in the GameWindow the scene will be displayed in
   *
   * @param gameWindow the game window
   */

  public ScoresScene(GameWindow gameWindow) {
    super(gameWindow);

    //Retrieving game data and score file
    gameWindow.setGameListener((gameData -> {
      game = gameData;
      logger.info("Game object: " + game);
    }));

    file = gameWindow.getFile();
  }

  @Override
  public void initialise() {
    logger.info("Making Scores scene");

  }

  /**
   * Compares the user's score to previous score list from file
   *
   * @return whether the user exceeded at least the lowest scorer to ask for prompt
   */
  private boolean getScores() {
    var currentScore = game.getScore();
    loadScores();
    //Find the lowest scorer in the list
    var lowestScorer = scores.get(scores.size() - 1);
    for (var score : scores) {
      if (lowestScorer.getValue() > score.getValue()) {
        lowestScorer = score;
      }
    }
    //If new scorer is higher than replace in the list
    if (currentScore > lowestScorer.getValue()) {
      scores.remove(lowestScorer);
      return true;
    }
    return false;
  }

  /**
   * Building the ScoreScene
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    scoresPane = new BorderPane();
    scoresPane.setMaxWidth(gameWindow.getWidth());
    scoresPane.setMaxHeight(gameWindow.getHeight());
    scoresPane.getStyleClass().add("scores-background");
    root.getChildren().add(scoresPane);

    HBox hBox = new HBox();
    Text title = new Text("SCORES");
    title.getStyleClass().add("title");
    hBox.getChildren().add(title);
    scoresPane.setTop(hBox);
    hBox.setAlignment(Pos.CENTER);

    //Setting up prompts and scoreboards
    scoreBoards = new HBox();
    scoreBoards.setSpacing(20);
    var localBoard = setupScoreBoard("Local Scores");
    var onlineBoard = setupScoreBoard("Online Scores");

    scoreBoards.getChildren().add(localBoard);
    scoreBoards.getChildren().add(onlineBoard);
    scoreBoards.setAlignment(Pos.CENTER);
    scoreBoards.setTranslateY(-10);

    prompt = setupPromptScreen();
    buttons = setupButtons();

    //Switching between prompt and scoreboards
    if (getScores()) {
      //Reordering updated List and rewriting into the file
      scoresPane.setCenter(prompt);
    } else {
      //Score board UI component
      showScores();
    }
  }

  /**
   * Making Buttons for the ScoresScene
   *
   * @return the Type of button
   */
  private HBox setupButtons() {
    buttons = new HBox();
    buttons.getChildren().add(makeButton("Retry"));
    buttons.getChildren().add(makeButton("Quit"));
    buttons.setSpacing(20);
    buttons.setTranslateY(-20);
    buttons.setAlignment(Pos.BASELINE_CENTER);
    return buttons;
  }

  /**
   * Building the Name prompt screen
   *
   * @return HBox
   */
  public HBox setupPromptScreen() {
    var screen = new HBox();
    screen.getStyleClass().add("myScore");

    TextField text = new TextField();
    text.getStyleClass().add("scoreItem");
    text.setPromptText("Enter name");
    screen.getChildren().add(text);

    screen.setTranslateY(-20);
    screen.setAlignment(Pos.CENTER);

    //If we press enter, set the name and update the list
    text.setOnKeyPressed((e) -> {
      if (e.getCode() != KeyCode.ENTER) {
        return;
      }
      scores.add(new Pair<>(text.getText(), game.getScore()));
      scores.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
      scoresPane.getChildren().remove(prompt);
      showScores();
    });

    return screen;
  }

  /**
   * Factory method to create either online or offline ScoreBoard
   *
   * @param type type of board
   * @return he newly made board
   */
  public VBox setupScoreBoard(String type) {
    var board = new VBox();
    board.setPadding(new Insets(4, 4, 4, 4));
    Text title = new Text(type);
    title.getStyleClass().add("score");

    ScoresList scoreBox = new ScoresList();
    board.getChildren().addAll(title, scoreBox);
    board.setAlignment(Pos.CENTER);

    switch (type) {
      case "Local Scores" -> scoreBox.scoreProperty().bind(localScores);
      case "Online Scores" -> scoreBox.scoreProperty().bind(remoteScores);
    }

    return board;
  }

  /**
   * Display the UI for all ScoreBoards
   */
  public void showScores() {
    Multimedia.playAudio("HighScore.mp3");
    localScores.addAll(scores);
    scoresPane.setCenter(scoreBoards);
    scoresPane.setBottom(buttons);
    writeScores();
  }

  /**
   * Loading the scores from the file
   */
  public void loadScores() {
    TextReader fileReader = new TextReader(file.getPath());
    scores = new ArrayList<>();
    while (fileReader.fileIsReady()) {
      line = fileReader.getLine();
      String[] split = line.split(":");
      var name = split[0];
      var points = Integer.valueOf(split[1]);
      Pair<String, Integer> score = new Pair<>(name, points);
      scores.add(score);
    }
  }

  /**
   * Overwriting the scores.txt file with new updated scores
   */
  public void writeScores() {
    try {
      FileWriter fileWriter = new FileWriter(file, false);
      for (var score : scores) {
        var name = score.getKey();
        var points = String.valueOf(score.getValue());
        fileWriter.write(name + ":" + points + "\n");
        //logger.info("Writing updated score: " + name + ":" + points);
      }
      fileWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Factory method to make different types of buttons
   *
   * @param text type of button
   * @return Button
   */
  private Button makeButton(String text) {
    Button button = new Button(text);
    button.getStyleClass().add("menuItem");
    button.setOnMouseEntered(event -> button.getStyleClass().add("menuItem:hover"));
    switch (text) {
      case "Retry" -> button.setOnMouseClicked(event -> gameWindow.startChallenge());
      case "Quit" -> button.setOnMouseClicked(event -> gameWindow.startMenu());
    }
    return button;
  }
}
