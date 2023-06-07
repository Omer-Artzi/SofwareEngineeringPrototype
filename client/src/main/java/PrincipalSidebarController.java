import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.io.IOException;

public class PrincipalSidebarController implements SideBar {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label Menu;

    @FXML
    private Button addQuestionButton;

    @FXML
    private Button addTestFormsButton;

    @FXML
    private Button gradeExamButton;

    @FXML
    private Button showStatisticsButton;

    @FXML
    private Label userTypeLabel;

    @FXML
    private Button viewClassExamsButton;

    @FXML
    private Button viewQuestionButton;

    @FXML
    private Button viewTestFormsButton;

    @FXML
    void addQuestion(ActionEvent event) {

    }

    @FXML
    void addTestForms(ActionEvent event) {

    }

    @FXML
    void gradeExam(ActionEvent event) {

    }

    @FXML
    void showStatistics(ActionEvent event) throws IOException {
        // The principal's statistics button should lead it to the new, principal-oriented stats window.
        System.out.println("In principal sidebar, trying to go to ShowStatAndReports.");
        changeScene("ShowStatsAndReports");
    }

    @FXML
    void viewClassExams(ActionEvent event) {

    }

    @FXML
    void viewQuestion(ActionEvent event) throws IOException {
        changeScene("TeacherViewQuestions");
    }

    @FXML
    void viewTestForms(ActionEvent event) throws IOException {
        changeScene("viewLiveExams");
    }

    @FXML
    void initialize() {
        assert Menu != null : "fx:id=\"Menu\" was not injected: check your FXML file 'PrincipalSidebar.fxml'.";
        assert addQuestionButton != null : "fx:id=\"addQuestionButton\" was not injected: check your FXML file 'PrincipalSidebar.fxml'.";
        assert addTestFormsButton != null : "fx:id=\"addTestFormsButton\" was not injected: check your FXML file 'PrincipalSidebar.fxml'.";
        assert gradeExamButton != null : "fx:id=\"gradeExamButton\" was not injected: check your FXML file 'PrincipalSidebar.fxml'.";
        assert showStatisticsButton != null : "fx:id=\"showStatisticsButton\" was not injected: check your FXML file 'PrincipalSidebar.fxml'.";
        assert userTypeLabel != null : "fx:id=\"userTypeLabel\" was not injected: check your FXML file 'PrincipalSidebar.fxml'.";
        assert viewClassExamsButton != null : "fx:id=\"viewClassExamsButton\" was not injected: check your FXML file 'PrincipalSidebar.fxml'.";
        assert viewQuestionButton != null : "fx:id=\"viewQuestionButton\" was not injected: check your FXML file 'PrincipalSidebar.fxml'.";
        assert viewTestFormsButton != null : "fx:id=\"viewTestFormsButton\" was not injected: check your FXML file 'PrincipalSidebar.fxml'.";

        userTypeLabel.setText("Logged in as: Principal");
    }

}
