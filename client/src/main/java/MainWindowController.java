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
    private void initialize() throws IOException {
        InitializationAsserts();

        SimpleChatClient.setMainWindowController(this);

        // Load the sidebar
        Parent sideBarParent = SimpleChatClient.loadFXML("/Sidebar");
        sidePane.getChildren().clear();
        sidePane.getChildren().add(sideBarParent);

        // Load the main window
        Parent mainWindowParent = SimpleChatClient.loadFXML("/ViewQuestionsWindow");
        mainPane.getChildren().clear();
        mainPane.getChildren().add(mainWindowParent);

    }

    private void InitializationAsserts() {
        assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert sidePane != null : "fx:id=\"sidePane\" was not injected: check your FXML file 'MainWindow.fxml'.";
    }

    @FXML
    public void LoadSceneToMainWindow(String sceneName) throws IOException {
        Parent mainWindowParent = SimpleChatClient.loadFXML(sceneName);
        mainPane.getChildren().clear();
        mainPane.getChildren().add(mainWindowParent);
    }
}



