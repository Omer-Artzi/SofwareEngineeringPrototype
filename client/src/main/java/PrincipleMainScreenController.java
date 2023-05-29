import Events.notificationEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

public class PrincipleMainScreenController {

    @FXML
    private Button ExtraTimeRequestBT;

    @FXML
    private Label Menu;

    @FXML
    private Label MenuBack;

    @FXML
    private Button gradeExamButton;

    @FXML
    private Button seeCurrentTestsBT;

    @FXML
    private Button showStatisticsButton;

    @FXML
    private AnchorPane slider;

    @FXML
    private Button viewQuestionButton;

    @FXML
    private Button viewTestFormsButton;

    @Subscribe
    public void notification(notificationEvent event) throws IOException {
        if(event.IsFound(SimpleClient.getClient().getUser()))
            event.show();
    }
    @FXML
    void ExtraTimeRequest(ActionEvent event) throws IOException {
        SimpleChatClient.setRoot("");
    }

    @FXML
    void gradeExam(ActionEvent event) {

    }

    @FXML
    void seeCurrentTests(ActionEvent event) {

    }

    @FXML
    void showStatistics(ActionEvent event) {

    }

    @FXML
    void viewQuestion(ActionEvent event) {

    }

    @FXML
    void viewTestForms(ActionEvent event) {

    }

}
