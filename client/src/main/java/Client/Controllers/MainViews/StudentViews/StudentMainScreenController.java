package Client.Controllers.MainViews.StudentViews;

import Client.Controllers.MainViews.SaveBeforeExit;
import Client.SimpleClient;
import Entities.Users.Student;
import Entities.Users.Teacher;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class StudentMainScreenController extends SaveBeforeExit {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label MainMessageLabel;

    @FXML
    void initialize() throws IOException {
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        assert MainMessageLabel != null : "fx:id=\"MainMessageLabel\" was not injected: check your FXML file 'StudentMainScreen.fxml'.";
        Student student= (Student) SimpleClient.getClient().getUser();

        LocalTime currentTime=LocalTime.now();
        LocalTime startTime = LocalTime.of(12, 0); // 12:00:00
        LocalTime endTime = LocalTime.of(16, 0);
        boolean isBetween = !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
        if (isBetween) {
            MainMessageLabel.setText("Good Afternoon "+student.getFullName());
        } else {
            if(currentTime.isBefore(startTime)){
                MainMessageLabel.setText("Good Morning "+student.getFullName());
            }
            else
            {
                MainMessageLabel.setText("Good Evening "+student.getFullName());
            }
        }
    }

}
