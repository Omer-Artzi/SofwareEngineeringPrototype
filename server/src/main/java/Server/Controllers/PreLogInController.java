package Server.Controllers;

import Server.Events.ServerConnectionEvent;
import Server.Events.TerminationEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

public class PreLogInController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button EnterButton;

    @FXML
    private TextField PortTF;
    @FXML
    private Label loadingLabel;


    @FXML
    void initialize() {
        EventBus.getDefault().register(this);
        assert EnterButton != null : "fx:id=\"EnterButton\" was not injected: check your FXML file 'PreLogIn.fxml'.";
        assert PortTF != null : "fx:id=\"PortTF\" was not injected: check your FXML file 'PreLogIn.fxml'.";

    }
    @FXML
    public void onEnter()
    {
        if(PortTF.getText() != null && !PortTF.getText().isBlank())
        {
            try {
                int port = Integer.parseInt(PortTF.getText());
                        if(port >= 0 && port<= 65535) {
                            Platform.runLater(() -> PortTF.setText("Loading..."));
                            System.out.println("Setting up server at " + port);
                            ServerConnectionEvent event = new ServerConnectionEvent(port);
                            EventBus.getDefault().post(event);
                        }
                        else
                        {
                            PortTF.setText("Port can only be 0-65,535");
                        }
            }
            catch (NumberFormatException e)
            {
                JOptionPane.showMessageDialog(null,"Invalid input","Input Error",JOptionPane.WARNING_MESSAGE);
            }

        }
        else
        {
            Platform.runLater(() -> PortTF.setText("Loading..."));
            System.out.println("Setting up server at 3000");
            ServerConnectionEvent event = new ServerConnectionEvent(3000);
            EventBus.getDefault().post(event);
        }



    }
    @Subscribe
    public  void none(TerminationEvent event)
    {
        System.out.println("Termination event received");
        System.exit(0);
    }

}
