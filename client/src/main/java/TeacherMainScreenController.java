import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import Entities.Teacher;
import Events.PrincipalApproveEvent;
import Events.PrincipalDecisionEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;

public class TeacherMainScreenController extends SaveBeforeExit {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label MainMessageLabel;

    @FXML
    void initialize() {

        EventBus.getDefault().register(this);

        assert MainMessageLabel != null : "fx:id=\"MainMessageLabel\" was not injected: check your FXML file 'TeacherMainScreen.fxml'.";

    }

}