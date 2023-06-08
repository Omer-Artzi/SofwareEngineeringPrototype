package Client.Controllers.SubViews;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import org.controlsfx.control.textfield.CustomTextField;

public class ApproveOfPrincipalController {

    @FXML
    private CustomTextField CourseTF;

    @FXML
    private TextArea PrincipalD;

    @FXML
    private CustomTextField SubjectTF;

    @FXML
    private TextArea TeacherNoteTF;

    @FXML
    private Button sendBT;

    /*
    @Subscribe
    public void update(RequestDate event){

    }
    */

    @FXML
    void addCourse(ActionEvent event) {

    }

    @FXML
    void addSubject(ActionEvent event) {

    }

    @FXML
    void sendAnswer(ActionEvent event) {
        String answer=PrincipalD.getText();
       // NotificationEvent answerOfPrincipalr=new NotificationEvent()

    }

}
