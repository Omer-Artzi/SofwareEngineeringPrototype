import java.io.IOException;
import java.net.URL;
import java.security.Principal;
import java.util.ResourceBundle;

import Entities.Student;
import Entities.Teacher;
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
        //System.out.println("Liad and Ilan in MainWindow");
        SimpleChatClient.setMainWindowController(this);

        SimpleChatClient.getScene().getWindow().setHeight(768);
        SimpleChatClient.getScene().getWindow().setWidth(1024);

        // Load the sidebar
        Parent sideBarParent = null;
        String userType = SimpleClient.getUser().getClass().getSimpleName();
        //String userType="Principle";
       // System.out.println("User type: " + userType);
        String sideBarName = userType + "Sidebar";
        // load correct sidebar according to user type
        sideBarParent = SimpleChatClient.loadFXML(sideBarName);
        //sideBarParent = SimpleChatClient.loadFXML("TeacherSidebar");
        sidePane.getChildren().clear();
        sidePane.getChildren().add(sideBarParent);

        // Load the main window
        Parent mainWindowParent = null;
        String mainScreenName = userType + "MainScreen";
        // load correct window according to user type
        mainWindowParent = SimpleChatClient.loadFXML(mainScreenName);
        //mainWindowParent = SimpleChatClient.loadFXML("ViewQuestions");
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



