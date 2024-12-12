package uk.ac.soton.comp1206.ui;

import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This UI component stores all the game states such as lives, multiplier and level in the game
 */
public class GameInterface extends HBox {

  private static final Logger logger = LogManager.getLogger(GameInterface.class);

  /**
   * UI for score
   */
  private final Text score = new Text();

  /**
   * UI for level
   */
  private final Text level = new Text();

  /**
   * UI for lives
   */
  private final Text lives = new Text();

  /**
   * UI for multiplier
   */
  private final Text multiplier = new Text();

  /**
   * Create new interface with all game elements
   */
  public GameInterface() {
    //set properties
    setPrefWidth(200);
    setSpacing(20);
    setPadding(new Insets(10, 10, 10, 10));
    setAlignment(Pos.BASELINE_CENTER);

    build();
  }

  /**
   * Build the Score Interface
   */
  protected void build() {
    logger.info("Building Score Interface");

    //Adding Components
    newBox(new Text("LIVES: "), lives);
    newBox(new Text("SCORE: "), score);
    newBox(new Text("LEVEL: "), level);
    newBox(new Text("MULTIPLIER: "), multiplier);

  }

  /**
   * Creates a new element with added styleClasses
   *
   * @param text type of element
   * @param UI   UI
   */
  private void newBox(Text text, Text UI) {
    //Making a Hbox for the scores, lives etc
    var newBox = new HBox();
    text.getStyleClass().add("score");
    UI.getStyleClass().add("score");
    newBox.getChildren().addAll(text, UI);
    getChildren().add(newBox);
  }

  public StringProperty scoreProperty() {
    return score.textProperty();
  }

  public StringProperty levelProperty() {
    return level.textProperty();
  }

  public StringProperty lifeProperty() {
    return lives.textProperty();
  }

  public StringProperty multiplierProperty() {
    return multiplier.textProperty();
  }
}
