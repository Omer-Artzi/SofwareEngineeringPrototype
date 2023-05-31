import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StudentMainScreenController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label MainMessageLabel;

    @FXML
    void initialize() {
        assert MainMessageLabel != null : "fx:id=\"MainMessageLabel\" was not injected: check your FXML file 'StudentMainScreen.fxml'.";

    }

}
