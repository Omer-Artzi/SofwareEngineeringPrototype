package Client.Controllers;

import Client.SimpleChatClient;
import Client.SimpleClient;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.ResourceBundle;

public class MainWindowController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private AnchorPane sidePane;

    private final Hashtable<String, Parent> loadedScenes = new Hashtable<String, Parent>();

    @FXML
    private void initialize() throws IOException {
        InitializationAsserts();

        SimpleChatClient.setMainWindowController(this);

        SimpleChatClient.getScene().getWindow().setHeight(768);
        SimpleChatClient.getScene().getWindow().setWidth(1024);

        // Load the sidebar
        Parent sideBarParent = null;
        String userType = SimpleClient.getUser().getClass().getSimpleName();
        //System.out.println("User type: " + userType);
        String sideBarName = userType + "Sidebar";
        // load correct sidebar according to user type
        sideBarParent = SimpleChatClient.loadFXML(sideBarName);
        //sideBarParent = Client.SimpleChatClient.loadFXML("TeacherSidebar");
        sidePane.getChildren().clear();
        sidePane.getChildren().add(sideBarParent);

        // Load the main window
        Parent mainWindowParent = null;
        String mainScreenName = userType + "MainScreen";
        // load correct window according to user type
        mainWindowParent = SimpleChatClient.loadFXML(mainScreenName);
        //mainWindowParent = Client.SimpleChatClient.loadFXML("ViewQuestions");
        mainPane.getChildren().clear();
        mainPane.getChildren().add(mainWindowParent);

    }

    private void InitializationAsserts() {
        assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert sidePane != null : "fx:id=\"sidePane\" was not injected: check your FXML file 'MainWindow.fxml'.";
    }

    @FXML
    public void LoadSceneToMainWindow(String sceneName) throws IOException {
        Parent mainWindowParent;

        System.out.println("Client.Main window loading scene: " + sceneName);
        // scene reuse code, currently creates problems
        /*if (loadedScenes.containsKey(sceneName)) {
            mainWindowParent = loadedScenes.get(sceneName);
        }
        else {
            mainWindowParent = Client.SimpleChatClient.loadFXML(sceneName);
            loadedScenes.put(sceneName, mainWindowParent);
        }*/


        mainWindowParent = SimpleChatClient.loadFXML(sceneName);
        loadedScenes.put(sceneName, mainWindowParent);


        mainPane.getChildren().clear();
        mainPane.getChildren().add(mainWindowParent);
    }
}


