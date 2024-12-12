package uk.ac.soton.comp1206.ui;

import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class MenuScene extends StackPane {

  public MenuScene (Window window) {

    setMaxSize(window.getWidth(),window.getHeight());
    var menuPane = new BorderPane();

    var title = new Text("Runway Declaration Tool");
    var buttons = new VBox();

    Button airport = new Button("Airport");
    airport.setOnAction(e -> window.changeScene(window.airports));

    Button quit = new Button("Quit");

    buttons.getChildren().add(airport);
    buttons.getChildren().add(quit);

    menuPane.setTop(title);
    menuPane.setCenter(buttons);

    getChildren().add(menuPane);

  }

}
