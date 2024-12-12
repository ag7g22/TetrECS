package uk.ac.soton.comp1206.component;

import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;

/**
 * The ScoresList class display the offline and online scores, after being read from a file
 */
public class ScoresList extends VBox {

  /**
   * Arraylist of name score pairs that are currently in the Leaderboards
   */
  public final SimpleListProperty<Pair<String, Integer>> scores = new SimpleListProperty<>();

  /**
   * Create ScoresList component to hold scores in
   */
  public ScoresList() {

    //Set default settings
    getStyleClass().add("scoreList");
    setSpacing(5);
    setPadding(new Insets(10, 10, 10, 10));
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
      var name = new Text(rank + ". " + score.getKey() + ": ");
      var points = new Text(String.valueOf(score.getValue()));

      if (rank == 1) {
        name.getStyleClass().add("topScoreItem");
        points.getStyleClass().add("topScoreItem");
      } else {
        name.getStyleClass().add("scoreItem");
        points.getStyleClass().add("scoreItem");
      }
      userScore.getChildren().addAll(name, points);
      getChildren().add(userScore);
      rank++;

    }
  }

  //accessor methods

  /**
   * UI property of the score List
   * @return scores arrayList
   */
  public ListProperty<Pair<String, Integer>> scoreProperty() {
    return scores;
  }

}
