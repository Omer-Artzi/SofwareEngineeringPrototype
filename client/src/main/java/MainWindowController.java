import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

public class MainWindowController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private AnchorPane sidePane;

    @FXML
    void initialize() throws IOException {
        InitializationAsserts();

        Parent sideBarParent = SimpleChatClient.loadFXML("/Sidebar");
        sidePane.getChildren().clear();
        sidePane.getChildren().add(sideBarParent);

    }

    void InitializationAsserts() {
        assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert sidePane != null : "fx:id=\"sidePane\" was not injected: check your FXML file 'MainWindow.fxml'.";
    }
}



