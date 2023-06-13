package Client;

import Client.Controllers.MainWindowController;
import Client.Events.GeneralEvent;
import Client.Events.MessageEvent;
import Entities.Communication.Message;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SimpleChatClient extends Application {

    private static Scene scene;
    private static Stage clientStage;
    private static FXMLLoader fxmlLoader;
    private static SimpleClient client;

    private static MainWindowController mainWindowController;

    public static final double version = 1.7;

    public static void setClient(SimpleClient simpleClient) {
        client = simpleClient;
    }


    @Override
    public void start(Stage stage) throws IOException {
        try {
            clientStage = stage;
            EventBus.getDefault().register(this);
            scene = new Scene(loadFXML("PreLogIn"));
            stage.setScene(scene);
            stage.setTitle("High School Test System - Version " + version);
            Image image = new Image("file:/src/main/resources/Images/HSTSLogo.png");
            stage.getIcons().add(image);
            // To preform cleanup tasks in controllers
            stage.setOnCloseRequest(event -> {
                EventBus.getDefault().post(new GeneralEvent(new Message(0, "Exit")));
            });
            stage.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        Message message = new Message(1, "Client Closed");
        System.out.println("Client stop");
        message.setData(SimpleClient.getUser());
        client.sendToServer(message);
        EventBus.getDefault().unregister(this);
        super.stop();
        Platform.exit();
        System.exit(0);
    }

    public static void setRoot(String fxml) throws IOException {
        System.out.println("setRoot");
        mainWindowController.LoadSceneToMainWindow(fxml);
    }

    public static void NewSetRoot(String fxml) throws IOException {
        Platform.runLater(() -> {
            try {
                scene.setRoot(loadFXML(fxml));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    public static Parent loadFXML(String fxml) throws IOException {
        System.out.println("loadFXML: " + fxml);
        List<String> possiblePaths = new ArrayList<>();
        possiblePaths.add("IlansFuckingBullshitResources/");
        possiblePaths.add("MainViews/");
        possiblePaths.add("StaffViews/");
        possiblePaths.add("PrincipalViews/");
        possiblePaths.add("TeacherViews/");
        possiblePaths.add("StudentViews/");
        possiblePaths.add("Sidebars/");
        possiblePaths.add("SubViews/");
        possiblePaths.add("");

        // recursively search for the fxml file url in the resource folder:
// https://stackoverflow.com/questions/20389255/what-is-the-difference-between-getclass-getclassloader-getresource-and-getcl

        for(String path1 : possiblePaths){
            for(String path2 : possiblePaths) {
                for (String path3 : possiblePaths) {
                    try {
                        return FXMLLoader.load(Objects.requireNonNull(SimpleChatClient.class.getClassLoader().getResource("FXMLs/" + path1 +  path2 +  path3 + fxml + ".fxml")));
                    }
                    catch (Exception e){
                        continue;
                    }
                }
            }
        }
        return null;
        //return FXMLLoader.load(Objects.requireNonNull(SimpleChatClient.class.getClassLoader().getResource("FXMLs/" + fxml + ".fxml")));
    }




    @Subscribe
    public void onMessageEvent(MessageEvent message) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION,
                    String.format("Message:\nId: %d\nData: %s\nTimestamp: %s\n",
                            message.getMessage().getId(),
                            message.getMessage().getMessage(),
                            message.getMessage().getTimeStamp().format(dtf))
            );
            alert.setTitle("new message");
            alert.setHeaderText("New Message:");
            alert.show();
        });
    }



    public static void main(String[] args) {
        try {
            launch();
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, "The server could not be connected, please make sure it iks running or contact your admin", "Server Error", JOptionPane.WARNING_MESSAGE);
            e.printStackTrace();
        }
    }

    public static Scene getScene() {
        return scene;
    }

    public static void setScene(Scene scene) {
        SimpleChatClient.scene = scene;
    }

    public static Stage getClientStage() {
        return clientStage;
    }

    public static void setMainWindowController(MainWindowController newMainWindowController) {
        mainWindowController = newMainWindowController;
    }

    public static MainWindowController getMainWindowController() {
        return mainWindowController;
    }
}