package Server;

import Server.Events.ServerConnectionEvent;
import Server.Events.TerminationEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.hibernate.SessionFactory;

import java.io.IOException;


public class SimpleChatServer extends Application
{
    private Stage serverStage;
    private final double version = 1.7;

    @Subscribe
    public void connect(ServerConnectionEvent event)
    {
        if(event != null)
        {
            try {
                System.out.println("Trying to connect to server at port " + event.getPort() + "...");
                SimpleServer server = new SimpleServer(event.getPort());
                server.listen();
                FXMLLoader fxmlLoader = new FXMLLoader(SimpleChatServer.class.getResource("Primary.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 785, 390);
                serverStage.setScene(scene);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
    public void start(Stage stage) throws IOException {
        EventBus.getDefault().register(this);
        FXMLLoader fxmlLoader = new FXMLLoader(SimpleChatServer.class.getResource("PreLogIn.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 785, 390);
        stage.setScene(scene);
        stage.setTitle("Server Management Tool - Version " + version + " IP: " + SimpleServer.getIP() + ", Port: " + SimpleServer.getLocalPort());
        // Ensure the default "Close Button" terminates the server.
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                try {
                    EventBus.getDefault().post(new TerminationEvent());
                    SessionFactory sessionFactory = SimpleServer.getSessionFactory(null);
                    if (sessionFactory != null && !sessionFactory.isClosed()){
                        SimpleServer.session.close();
                        sessionFactory.close();
                        System.out.println("Closed Session Factory");
                    }

                }
                    catch (Exception e){
                    e.printStackTrace();
                }
                Platform.exit();
            }
        });
        serverStage = stage;
        stage.show();
    }

    public static void main( String[] args )
    {launch();}

}
