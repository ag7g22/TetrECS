package uk.ac.soton.comp1206.scene;

import java.util.ArrayList;
import java.util.Random;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The InstructionScene class holds the scene that shows the instructions to the game. It is
 * organised in pages to show the rules, controls and pieces
 */
public class InstructionsScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(InstructionsScene.class);

  /**
   * StackPane to change which page the instructionScene is on
   */
  private StackPane instructions;

  /**
   * VBox of controls of the game
   */
  private VBox controls;

  /**
   * ArrayList of pages in the InstructionScene to call them or remove them
   */
  private final ArrayList<BorderPane> pages = new ArrayList<>();

  /**
   * Pane holding the current page
   */
  private BorderPane currentPane;

  /**
   * Pointer variable to indicate what page it is on
   */
  private int pagePointer;

  /**
   * Arraylist of pieces to be displayed
   */
  private final ArrayList<GamePiece> pieces;

  /**
   * Create a new scene, passing in the GameWindow the scene will be displayed in
   *
   * @param gameWindow the game window
   */
  public InstructionsScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Instructions Scene");

    //Adding ALL pieces into the arraylist
    pieces = new ArrayList<>();
    for (int i = 0; i < 15; i++) {
      GamePiece piece = GamePiece.createPiece(i);
      pieces.add(piece);
    }

  }

  /**
   * Initialise the Keybinds navigating the pages
   */
  @Override
  public void initialise() {
    setupKeyBinds();
  }

  /**
   * Bulilding the InstructionsScene
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());
    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    instructions = new StackPane();
    instructions.setMaxSize(gameWindow.getWidth(), gameWindow.getHeight());
    instructions.getStyleClass().add("instructions");
    root.getChildren().add(instructions);

    setupPages();

    currentPane = pages.get(pagePointer);
    instructions.getChildren().add(currentPane);

  }

  /**
   * Creates all instances of the 3 pages
   */
  public void setupPages() {
    //creating pages
    pages.add(createPage("0"));
    pages.add(createPage("1"));
    pages.add(createPage("2"));
    pagePointer = 0;

  }

  /**
   * Updates pointer and display page that is left
   */
  protected void turnPageLeft() {
    if (pagePointer > 0) {
      instructions.getChildren().remove(currentPane);
      pagePointer--;
      currentPane = pages.get(pagePointer);
      instructions.getChildren().add(currentPane);
    }
  }

  /**
   * Updates point and display page that is right
   */
  protected void turnPageRight() {
    if (pagePointer < 2) {
      instructions.getChildren().remove(currentPane);
      pagePointer++;
      currentPane = pages.get(pagePointer);
      instructions.getChildren().add(currentPane);
    }
  }

  /**
   * Writes the line with the newly used Minecraft Font
   *
   * @param string text to be displayed
   */
  public void writeControl(String string) {
    Text text = new Text(string);
    text.getStyleClass().add("instruction-content");
    controls.getChildren().add(text);
  }

  /**
   * Factory method to which page to make
   *
   * @param number page number
   * @return the page from given number
   */
  public BorderPane createPage(String number) {
    var page = new BorderPane();
    page.setPrefSize(gameWindow.getWidth() * 0.75, gameWindow.getHeight());

    HBox pageButtons = new HBox();

    pageButtons.getChildren().add(makeButton("<-"));
    pageButtons.getChildren().add(makeButton("->"));
    pageButtons.setSpacing(500);
    pageButtons.setAlignment(Pos.BOTTOM_CENTER);
    page.setBottom(pageButtons);

    var pageTitle = new Text();
    pageTitle.getStyleClass().add("instruction-page");
    pageTitle.setTranslateX(30);
    pageTitle.setTranslateY(10);

    switch (number) {
      case "0" -> {
        pageTitle.setText(number + ": Rules");

        var rules = new VBox();
        page.setCenter(rules);
        rules.setAlignment(Pos.CENTER);
        rules.setSpacing(50);

        rules.getChildren().add(makeRule(1));
        rules.getChildren().add(makeRule(2));

      }
      case "1" -> {
        pageTitle.setText(number + ": Controls");

        controls = new VBox();
        controls.setSpacing(30);
        writeControl("RIGHTCLICK/ENTER: Place piece on board");
        writeControl("SPACE/R: Swap the upcoming tiles");
        writeControl("Q: Rotate piece to the left");
        writeControl("E: Rotate piece to the right");
        writeControl("WASD/ARROW keys: to move around board");
        page.setCenter(controls);
        controls.setAlignment(Pos.CENTER);

      }
      case "2" -> {
        pageTitle.setText(number + ": Pieces");

        var allPieces = new GridPane();
        allPieces.setVgap(30);
        allPieces.setHgap(30);

        var count = 0;
        for (var y = 0; y < 5; y++) {
          for (var x = 0; x < 3; x++) {
            PieceBoard pieceBoard = new PieceBoard(gameWindow.getWidth() / 10,
                gameWindow.getWidth() / 10, false);
            pieceBoard.setBoard(pieces.get(count));
            count++;
            allPieces.add(pieceBoard, y, x);
          }
        }
        page.setCenter(allPieces);
        allPieces.setAlignment(Pos.CENTER);

      }
    }
    page.setTop(pageTitle);

    return page;
  }

  /**
   * The method that writes the different rules to the game, for the GameBoard and PieceBoard
   *
   * @param num
   * @return
   */
  private HBox makeRule(int num) {
    HBox rule = new HBox();
    switch (num) {
      case 1 -> {
        VBox lines = new VBox();
        lines.setAlignment(Pos.CENTER);
        lines.setSpacing(5);

        Text line1 = writeLine("Create complete rows/columns to");
        Text line2 = writeLine("clear them. The more cleared at the");
        Text line3 = writeLine("same time, the more points you earn!");
        Text line4 = writeLine("The bonus will multiply as you clear");
        Text line5 = writeLine("more in a row!");
        lines.getChildren().addAll(line1, line2, line3, line4, line5);

        var grid = new Grid(5, 5);
        var board = new GameBoard(grid, 150, 150, false, false);
        Random rand = new Random();
        for (int x = 0; x < 5; x++) {
          grid.set(x, 0, rand.nextInt(1, 15));
        }
        for (int y = 1; y < 5; y++) {
          grid.set(4, y, rand.nextInt(1, 15));
        }

        rule.getChildren().addAll(lines, board);
        rule.setSpacing(60);
        rule.setTranslateX(120);
      }
      case 2 -> {
        var currentGrid = new PieceBoard(90, 90, true);
        Text line1 = writeLine("This grid holds");
        Text line2 = writeLine("the current piece!");
        Text line3 = writeLine("Indicator shows where");
        Text line4 = writeLine("the piece is placed.");
        var nextGrid = new PieceBoard(80, 80, false);
        Text line5 = writeLine("This smaller grid");
        Text line6 = writeLine("holds the next piece!");
        Text line7 = writeLine("You can swap and");
        Text line8 = writeLine("rotate both grids!");

        VBox cGrid = new VBox(currentGrid, line1, line2, line3, line4);
        cGrid.setAlignment(Pos.CENTER);
        VBox nGrid = new VBox(nextGrid, line5, line6, line7, line8);
        nGrid.setAlignment(Pos.CENTER);

        rule.getChildren().addAll(cGrid, nGrid);
        rule.setAlignment(Pos.BASELINE_CENTER);
        rule.setSpacing(50);
      }
    }
    return rule;
  }

  /**
   * Method writing the line to make them to the appropriate font
   *
   * @param line line being displayed
   * @return Text object
   */
  private Text writeLine(String line) {
    Text text = new Text(line);
    text.getStyleClass().add("instruction-content");
    return text;
  }

  /**
   * Factory method for buttons of similar functionality
   *
   * @param Text Left or Right button navigating the pages
   * @return the Button
   */
  private Button makeButton(String Text) {
    Button button = new Button(Text);
    button.getStyleClass().add("instruction-arrow");
    button.setOnMouseEntered(event -> button.getStyleClass().add("instruction-arrow:hover"));
    if (Text.equals("->")) {
      button.setOnMouseClicked(event -> turnPageRight());
    } else if (Text.equals("<-")) {
      button.setOnMouseClicked(event -> turnPageLeft());
    }
    return button;
  }

  /**
   * KeyBinds the escape the instructions scene
   */
  private void setupKeyBinds() {
    scene.setOnKeyPressed(event -> {
      KeyCode keyCode = event.getCode();
      switch (keyCode) {
        case ESCAPE -> gameWindow.startMenu();
        case LEFT, A -> turnPageLeft();
        case RIGHT, D -> turnPageRight();
      }
    });
  }

}
