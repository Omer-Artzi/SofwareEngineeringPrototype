package Client.Controllers.MainViews.StaffViews.PrincipalViews;

import Client.Controllers.MainViews.SaveBeforeExit;
import Client.Events.NotificationEvent;
import Client.SimpleChatClient;
import Client.SimpleClient;
import Entities.Users.Principal;
import Entities.Users.Teacher;
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
import java.time.LocalTime;
import java.util.ResourceBundle;

public class PrincipalMainScreenController extends SaveBeforeExit {

    @FXML
    private Button seeCurrentTestsBT;
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private Label MainMessageLabel;
/*
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
*/
    @FXML
    void initialize() throws IOException {
        EventBus.getDefault().register(this);
        assert MainMessageLabel != null : "fx:id=\"MainMessageLabel\" was not injected: check your FXML file 'PrincipalMainScreen.fxml'.";
        Principal principal= (Principal) SimpleClient.getClient().getUser();

        LocalTime currentTime=LocalTime.now();
        LocalTime startTime = LocalTime.of(12, 0); // 12:00:00
        LocalTime endTime = LocalTime.of(16, 0);
        boolean isBetween = !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
        if (isBetween) {
            MainMessageLabel.setText("Good Afternoon "+principal.getFullName());
        } else {
            if(currentTime.isBefore(startTime)){
                MainMessageLabel.setText("Good Morning "+principal.getFullName());
            }
            else
            {
                MainMessageLabel.setText("Good Evening "+principal.getFullName());
            }
        }
    }

    public void seeCurrentTests(ActionEvent actionEvent) {
    }
}
