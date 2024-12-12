package uk.ac.soton.comp1206.ui;

import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class AirportScene extends StackPane {

  public AirportScene (Window window) {

    setMaxSize(window.getWidth(),window.getHeight());

    var airportPane = new BorderPane();

    var title = new Text("Airports");
    var buttons = new VBox();

    Button addAirport = new Button("Add Airport");
    Button selectAirport = new Button("Select Airport");
    Button back = new Button("Back");
    back.setOnAction(e -> window.changeScene(window.menu));

    buttons.getChildren().add(addAirport);
    buttons.getChildren().add(selectAirport);
    buttons.getChildren().add(back);

    airportPane.setTop(title);
    airportPane.setCenter(buttons);

    getChildren().add(airportPane);

  }

}
