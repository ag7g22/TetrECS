package uk.ac.soton.comp1206.scene;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.Utility.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(MenuScene.class);
  private boolean keepMusicPlaying = false;

  /**
   * Create a new menu scene
   *
   * @param gameWindow the Game Window this will be displayed in
   */
  public MenuScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Menu Scene");
  }

  /**
   * Build the menu layout
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    var menuPane = new StackPane();
    menuPane.setMaxWidth(gameWindow.getWidth());
    menuPane.setMaxHeight(gameWindow.getHeight());
    menuPane.getStyleClass().add("menu-background");
    root.getChildren().add(menuPane);

    var mainPane = new BorderPane();
    menuPane.getChildren().add(mainPane);

    //updated title
    var title = new Text("TetrECS");
    title.getStyleClass().add("title");
    mainPane.setTop(title);
    title.setTranslateX(205);
    title.setTranslateY(135);
    animateTitle(title);

    //Box holding the buttons
    var buttons = new VBox();
    mainPane.setCenter(buttons);
    buttons.setTranslateY(130);
    buttons.setSpacing(5);
    buttons.setAlignment(Pos.BASELINE_CENTER);

    //Play, Instruction and Exit Button
    buttons.getChildren().add(makeButton("Play"));
    buttons.getChildren().add(makeButton("Instructions"));
    buttons.getChildren().add(makeButton("Exit"));

  }

  private Button makeButton(String text) {
    Button button = new Button(text);
    button.getStyleClass().add("menuItem");
    button.setOnMouseEntered(event -> button.getStyleClass().add("menuItem:hover"));
    switch (text) {
      case "Play" -> button.setOnAction(this::startGame);
      case "Instructions" -> button.setOnAction(this::showInstructions);
      case "Exit" -> button.setOnMouseClicked(event -> shutdown());
    }
    return button;
  }

  /**
   * Shutting down the application
   */
  public void shutdown() {
    logger.info("Shutting down");
    System.exit(0);
  }

  /**
   * Initialise the menu
   */
  @Override
  public void initialise() {
    if (!keepMusicPlaying) {
      Multimedia.playMusic("MenuTetris.mp3");
      keepMusicPlaying = !keepMusicPlaying;
    }
  }

  /**
   * Flag methods to keep music playing
   */
  public void toggleMusicPlaying() {
    keepMusicPlaying = !keepMusicPlaying;
  }

  /**
   * Handle when the Start Game button is pressed
   *
   * @param event event
   */
  private void startGame(ActionEvent event) {
    gameWindow.startChallenge();
  }

  /**
   * Handle when the Instruction Button is pressed
   *
   * @param event event
   */
  private void showInstructions(ActionEvent event) {
    gameWindow.startInstructions();
  }

  /**
   * Title "Beat" animation to be in sync with the music
   *
   * @param text the Title of the game
   */
  public void animateTitle(Text text) {
    logger.info("Animating title");
    Timeline timeline = new Timeline(
        new KeyFrame(Duration.ZERO, new KeyValue(text.scaleXProperty(), 1),
            new KeyValue(text.scaleYProperty(), 1)),
        new KeyFrame(Duration.millis(100), new KeyValue(text.scaleXProperty(), 1.05),
            new KeyValue(text.scaleYProperty(), 1.05)),
        new KeyFrame(Duration.ZERO, new KeyValue(text.scaleXProperty(), 1),
            new KeyValue(text.scaleYProperty(), 1)),
        new KeyFrame(Duration.millis(400))
    );
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
  }
}
