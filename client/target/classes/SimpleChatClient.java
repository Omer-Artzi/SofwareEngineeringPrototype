import Entities.Message;
import Events.MessageEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;


public class SimpleChatClient extends Application {

    private static Scene scene;
    private static Stage clientStage;
    private SimpleClient client;

    public static final double version = 1.7;



    @Override
    public void start(Stage stage) throws IOException {
        try {
            clientStage = stage;
            EventBus.getDefault().register(this);
            scene = new Scene(loadFXML("PreLogIn"), 399, 217);
            stage.setScene(scene);
            stage.setTitle("High School Test System Prototype - Version " + version);
            stage.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SimpleChatClient.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    @Override
	public void stop() throws Exception {
            Message message = new Message(1, "Client Closed");
            client.sendToServer(message);
            EventBus.getDefault().unregister(this);
            super.stop();
            System.exit(0);
	}


    @Subscribe
    public void onMessageEvent(MessageEvent message) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION,
                    String.format("Entities.Message:\nId: %d\nData: %s\nTimestamp: %s\n",
                            message.getMessage().getId(),
                            message.getMessage().getMessage(),
                            message.getMessage().getTimeStamp().format(dtf))
            );
            alert.setTitle("new message");
            alert.setHeaderText("New Entities.Message:");
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
}