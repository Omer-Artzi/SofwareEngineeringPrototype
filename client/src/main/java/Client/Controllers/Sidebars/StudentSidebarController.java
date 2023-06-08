package Client.Controllers.Sidebars;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class StudentSidebarController extends SideBar {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button mainPageButton;

    @FXML
    private Button takeExamsButton;

    @FXML
    private Label userTypeLabel;

    @FXML
    private Button viewGradesButton;

    @FXML
    void OnMainPageButtonPressed(ActionEvent event) {
        ChangeScene("StudentMainScreen");
    }

    @FXML
    void OnTakeExamsButtonPressed(ActionEvent event) {
        ChangeScene("ChooseExam");
    }

    @FXML
    void OnViewGradesButtonPressed(ActionEvent event) {
        ChangeScene("StudentViewExam");
    }

    @FXML
    void initialize() {
        assert mainPageButton != null : "fx:id=\"mainPageButton\" was not injected: check your FXML file 'StudentSidebar.fxml'.";
        assert takeExamsButton != null : "fx:id=\"takeExamsButton\" was not injected: check your FXML file 'StudentSidebar.fxml'.";
        assert userTypeLabel != null : "fx:id=\"userTypeLabel\" was not injected: check your FXML file 'StudentSidebar.fxml'.";
        assert viewGradesButton != null : "fx:id=\"viewGradesButton\" was not injected: check your FXML file 'StudentSidebar.fxml'.";

        userTypeLabel.setText("Logged in as: Student");
    }

}
