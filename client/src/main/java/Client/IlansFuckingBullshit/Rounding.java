package Client.IlansFuckingBullshit;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Rounding extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Button roundButton = new Button();

        roundButton.setStyle(
                "-fx-background-radius: 5em; " +
                        "-fx-min-width: 30px; " +
                        "-fx-min-height: 30px; " +
                        "-fx-max-width: 30px; " +
                        "-fx-max-height: 30px;"
        );

        StackPane layout = new StackPane(
                roundButton
        );
        layout.setPadding(new Insets(10));
        stage.setScene(new Scene(layout));

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}