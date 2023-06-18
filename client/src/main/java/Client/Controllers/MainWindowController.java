package Client.Controllers;

import Client.SimpleChatClient;
import Client.SimpleClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

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
    private Label dateLabel;
    @FXML
    private ImageView LogoImage;
    double LogoImage_x;
    double LogoImage_y;

    private Timer timer;
    private SimpleDateFormat formatter;

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
        //mainPane.getChildren().clear();
        mainPane.getChildren().add(mainWindowParent);

        timer = new Timer();
        formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    ZoneId zoneId = ZoneId.systemDefault();
                    ZonedDateTime zonedDateTime = LocalDateTime.now().atZone(zoneId);
                    dateLabel.setText(formatter.format(Date.from(zonedDateTime.toInstant())));
                });


            }
        }, 0, 1000);

        LogoImage_x = LogoImage.getX();
        LogoImage_y = LogoImage.getY();

        dateLabel.getLayoutX();
        dateLabel.getLayoutY();

    }


    private void InitializationAsserts() {
        assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert sidePane != null : "fx:id=\"sidePane\" was not injected: check your FXML file 'MainWindow.fxml'.";
    }

    @FXML
    public void LoadSceneToMainWindow(String sceneName) throws IOException {
        /*Platform.runLater(() -> {
            Parent mainWindowParent;

            System.out.println("Client.Main window loading scene: " + sceneName);

            try {
                mainWindowParent = SimpleChatClient.loadFXML(sceneName);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            loadedScenes.put(sceneName, mainWindowParent);


            mainPane.getChildren().clear();
            mainPane.getChildren().add(mainWindowParent);
        });*/

        Parent mainWindowParent;

        System.out.println("Client.Main window loading scene: " + sceneName);

        try {
            mainWindowParent = SimpleChatClient.loadFXML(sceneName);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadedScenes.put(sceneName, mainWindowParent);

        if(Platform.isFxApplicationThread()){
            mainPane.getChildren().clear();
            mainPane.getChildren().add(mainWindowParent);
        }
        else{
            Platform.runLater(() -> {
                mainPane.getChildren().clear();
                mainPane.getChildren().add(mainWindowParent);
            });
        }


        if(sceneName.contains("MainScreen"))
        {
            mainPane.getChildren().add(LogoImage);
            mainPane.getChildren().add(dateLabel);
        }

    }
}



