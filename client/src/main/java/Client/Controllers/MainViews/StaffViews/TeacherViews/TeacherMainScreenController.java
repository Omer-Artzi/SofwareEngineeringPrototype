package Client.Controllers.MainViews.StaffViews.TeacherViews;

import Client.Controllers.MainViews.SaveBeforeExit;
import Client.SimpleClient;
import Entities.Users.Teacher;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.URL;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class TeacherMainScreenController extends SaveBeforeExit {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label MainMessageLabel;

    @FXML
    void initialize() throws IOException {
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        Teacher teacher= (Teacher)SimpleClient.getClient().getUser();

        LocalTime currentTime=LocalTime.now();
        LocalTime startTime = LocalTime.of(12, 0); // 12:00:00
        LocalTime endTime = LocalTime.of(16, 0);
        boolean isBetween = !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
        if (isBetween) {
            MainMessageLabel.setText("Good Afternoon "+teacher.getFullName());
        } else {
            if(currentTime.isBefore(startTime)){
                MainMessageLabel.setText("Good Morning "+teacher.getFullName());
            }
            else
            {
                MainMessageLabel.setText("Good Evening "+teacher.getFullName());
            }
        }
        //MainMessageLabel.setFont();
        assert MainMessageLabel != null : "fx:id=\"MainMessageLabel\" was not injected: check your FXML file 'TeacherMainScreen.fxml'.";

    }

}