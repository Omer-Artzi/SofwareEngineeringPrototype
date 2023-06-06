import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import Entities.Principal;
import Events.NotificationEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PrincipalMainScreenController {

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
        Principal user=((Principal)(SimpleClient.getClient().getUser()));
        if(event.IsFound(user))
            event.show();
    }
    @FXML
    void initialize() {
        EventBus.getDefault().register(this);
        assert MainMessageLabel != null : "fx:id=\"MainMessageLabel\" was not injected: check your FXML file 'PrincipleMainScreen.fxml'.";

    }

    public void seeCurrentTests(ActionEvent actionEvent) {
    }
}
