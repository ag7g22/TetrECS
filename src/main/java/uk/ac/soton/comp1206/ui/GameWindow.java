package uk.ac.soton.comp1206.ui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.event.GameListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.scene.BaseScene;
import uk.ac.soton.comp1206.scene.ChallengeScene;
import uk.ac.soton.comp1206.scene.InstructionsScene;
import uk.ac.soton.comp1206.scene.MenuScene;
import uk.ac.soton.comp1206.scene.ScoresScene;

/**
 * The GameWindow is the single window for the game where everything takes place. To move between
 * screens in the game, we simply change the scene.
 * <p>
 * The GameWindow has methods to launch each of the different parts of the game by switching scenes.
 * You can add more methods here to add more screens to the game.
 */
public class GameWindow {

  private static final Logger logger = LogManager.getLogger(GameWindow.class);

  private final int width;
  private final int height;

  private final Stage stage;

  private BaseScene currentScene;
  private Scene scene;
  private GameListener gameListener;
  private File scoreFile;

  /**
   * Create a new GameWindow attached to the given stage with the specified width and height
   *
   * @param stage  stage
   * @param width  width
   * @param height height
   */
  public GameWindow(Stage stage, int width, int height) {
    this.width = width;
    this.height = height;

    this.stage = stage;

    //Setup window
    setupStage();

    //Setup resources
    setupResources();

    //Setup default scene
    setupDefaultScene();

    //Go to menu
    startMenu();
  }

  /**
   * Setup the font and any other resources we need
   */
  private void setupResources() {
    logger.info("Loading resources");

    //We need to load fonts here due to the Font loader bug with spaces in URLs in the CSS files
    Font.loadFont(getClass().getResourceAsStream("/style/Minecraft-Regular.ttf"), 32);
    Font.loadFont(getClass().getResourceAsStream("/style/Minecraft-Bold.ttf"), 32);
    Font.loadFont(getClass().getResourceAsStream("/style/Minecraft-Italic.ttf"), 32);
    Font.loadFont(getClass().getResourceAsStream("/style/Minecraft-BoldItalic.ttf"), 32);

    //Create score file in advance before playing
    scoreFile = new File("src/scores.txt");
    if (!scoreFile.exists()) {
      writeDefaultScores(scoreFile);
    }
  }

  /**
   * Writes in the default score if there is no existing scores.txt file
   *
   * @param file file name
   */
  public void writeDefaultScores(File file) {
    try {
      FileWriter fileWriter = new FileWriter(file, true);
      for (int i = 0; i < 10; i++) {
        fileWriter.write("Guest:0" + "\n");
        //logger.info("Writing default score");
      }
      fileWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns file object
   *
   * @return file
   */
  public File getFile() {
    return scoreFile;
  }

  /**
   * Set up gameListener object
   *
   * @param gameListener listener
   */
  public void setGameListener(GameListener gameListener) {
    this.gameListener = gameListener;
  }

  /**
   * Display the main menu
   */
  public void startMenu() {
    loadScene(new MenuScene(this));
  }

  /**
   * Display the single player challenge
   */
  public void startChallenge() {
    loadScene(new ChallengeScene(this));
  }

  /**
   * Display the Instructions scene
   */
  public void startInstructions() {
    loadScene(new InstructionsScene(this));
  }

  /**
   * Display the Scores scene
   */
  public void startScores() {
    loadScene(new ScoresScene(this));
  }

  /**
   * Setup the default settings for the stage itself (the window), such as the title and minimum
   * width and height.
   */
  public void setupStage() {
    stage.setTitle("TetrECS");
    stage.setMinWidth(width);
    stage.setMinHeight(height + 20);
    stage.setOnCloseRequest(ev -> App.getInstance().shutdown());
  }

  /**
   * Load a given scene which extends BaseScene and switch over.
   *
   * @param newScene new scene to load
   */
  public void loadScene(BaseScene newScene) {
    //Cleanup remains of the previous scene
    cleanup();
    passData(newScene);
    setMusic(newScene);

    //Create the new scene and set it up
    newScene.build();
    currentScene = newScene;
    scene = newScene.setScene();
    stage.setScene(scene);

    //Initialise the scene when ready
    Platform.runLater(() -> currentScene.initialise());
  }

  /**
   * Setup the default scene (an empty black scene) when no scene is loaded
   */
  public void setupDefaultScene() {
    this.scene = new Scene(new Pane(), width, height, Color.BLACK);
    stage.setScene(this.scene);
  }

  /**
   * When switching scenes, perform any cleanup needed, such as removing previous listeners
   */
  public void cleanup() {
    logger.info("Clearing up previous scene");

  }

  /**
   * Data passing for specific scenes
   *
   * @param newScene scene
   */
  public void passData(BaseScene newScene) {
    //Pass Game Object when it switches from Challenge to Scores scene
    if (currentScene instanceof ChallengeScene && newScene instanceof ScoresScene) {
      Game game = ((ChallengeScene) currentScene).getGame();
      gameListener.onGameCreated(game);
      //Pass nickname when it switchs from lobby to multiplayer scene
    }
  }

  /**
   * Specific music set ups
   *
   * @param newScene scene
   */
  public void setMusic(BaseScene newScene) {
    //Menu music plays continuously during menu,instruction and lobby scene
    if (newScene instanceof MenuScene && currentScene instanceof InstructionsScene) {
      ((MenuScene) newScene).toggleMusicPlaying();
    }
  }

  /**
   * Get the current scene being displayed
   *
   * @return scene
   */
  public Scene getScene() {
    return scene;
  }

  /**
   * Get the width of the Game Window
   *
   * @return width
   */
  public int getWidth() {
    return this.width;
  }

  /**
   * Get the height of the Game Window
   *
   * @return height
   */
  public int getHeight() {
    return this.height;
  }

}
