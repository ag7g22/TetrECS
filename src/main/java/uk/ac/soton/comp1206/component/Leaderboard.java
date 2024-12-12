package uk.ac.soton.comp1206.component;

import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Leaderboard class is a component that currently displays the current player's status in the
 * game. Where they are on in terms of score, rank and whether they're dead or not.
 */
public class Leaderboard extends ScoresList {

  private static final Logger logger = LogManager.getLogger(Leaderboard.class);

  /**
   * Create a new leader component with the gameWindow's size
   *
   * @param gameWindow access to it's sizing
   */
  public Leaderboard(GameWindow gameWindow) {
    logger.info("Creating LeaderBoard component");

    //Set default settings
    setPrefSize(gameWindow.getWidth() * 0.2, gameWindow.getHeight() * 0.6);
    getStyleClass().add("scoreList");
    setSpacing(5);
    setAlignment(Pos.TOP_CENTER);

    //Update score list when score array list is updated
    scores.addListener((ListChangeListener<? super Pair<String, Integer>>) (c) -> updateList());
  }

  /**
   * Updates the list of the users and their scores, the appearance of their names changes based on
   * their status in game
   */
  void updateList() {
    //Remove previous children
    getChildren().clear();

    int rank = 1;
    //Loop over every score
    for (var score : scores) {

      HBox userScore = new HBox();
      userScore.getStyleClass().add("scoreItem");
      userScore.setAlignment(Pos.CENTER_LEFT);

      //Associate with a given name and points
      var rawName = score.getKey();
      var name = new Text(rank + ". " + score.getKey() + ": ");
      var points = new Text(String.valueOf(score.getValue()));

      //Theire appearance whether they're first, dead or active in game
      if (rank == 1) {
        name.getStyleClass().add("onlineTopScoreItem");
        points.getStyleClass().add("onlineTopScoreItem");
      } else if (rawName.contains("DEAD")) {
        rawName = rawName.replace("DEAD", "");
        name = new Text(rank + ". " + rawName + ": ");
        name.getStyleClass().add("onlineDeadPlayer");
        points.getStyleClass().add("onlineDeadPlayer");
      } else {
        name.getStyleClass().add("onlineScoreItem");
        points.getStyleClass().add("onlineScoreItem");
      }

      userScore.getChildren().addAll(name, points);
      getChildren().add(userScore);
      rank++;
    }
  }
}
