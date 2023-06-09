package Client.Controllers.MainViews.StaffViews.PrincipalViews;

import Client.Controllers.MainViews.SaveBeforeExit;
import Client.Events.NotificationEvent;
import Client.SimpleChatClient;
import Client.SimpleClient;
import Entities.Users.Principal;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PrincipalMainScreenController extends SaveBeforeExit {

    @FXML
    private Button extraTimeListButton;
    @FXML
    private Button seeCurrentTestsBT;
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;

    @FXML
    private Label MainMessageLabel;

    @Subscribe
    public void update(NotificationEvent event) throws IOException {
        Platform.runLater(()-> {
            try {
                System.out.println("In pricipal MainScreen");
                Principal user=((Principal)(SimpleClient.getClient().getUser()));
                //if(event.IsFound(user)) {
                    //event.show();
                    //JOptionPane.showMessageDialog(null, "HI", "ExtraTimerRequest", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
}

    @FXML
    void initialize() {
        EventBus.getDefault().register(this);
        assert MainMessageLabel != null : "fx:id=\"MainMessageLabel\" was not injected: check your FXML file 'PrincipalMainScreen.fxml'.";

    }

    @FXML
    void extraTimeList(ActionEvent event) throws IOException {
        System.out.println("In Clicking on extra time list button");
        SimpleChatClient.NewSetRoot("ListExtraTimeRequest");
    }

    public void seeCurrentTests(ActionEvent actionEvent) {
    }
}
