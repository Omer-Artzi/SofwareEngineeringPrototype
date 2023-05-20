package il.cshaifasweng.OCSFMediatorExample.server;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;


public class SimpleChatServer extends Application
{
    private final double version = 1.3;
    public void start(Stage stage) throws IOException {
        try {
            SimpleServer server = new SimpleServer(3000);
            server.listen();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        FXMLLoader fxmlLoader = new FXMLLoader(SimpleChatServer.class.getResource("Primary.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 785, 390);
        stage.setScene(scene);
        stage.setTitle("Server Management Tool - Version " + version);
        // Ensure the default "Close Button" terminates the server.
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                EventBus.getDefault().post(new TerminationEvent());
            }
        });
        stage.show();
    }
    public static void main( String[] args )
    {launch();}
}
