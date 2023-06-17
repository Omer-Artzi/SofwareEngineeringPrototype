package Client.Controllers.Sidebars;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;

public class TeacherSidebarController extends SideBar {

    @FXML
    private Button mainPageButton;

    @FXML
    private Button addQuestionButton;

    @FXML
    private Button addTestFormsButton;

    @FXML
    private Button gradeExamButton;

    @FXML
    private Button showStatisticsButton;

    @FXML
    private Button viewQuestionButton;

    @FXML
    private Button viewTestFormsButton;


    @FXML
    private Label userTypeLabel;

    @FXML
    void OnMainPageButtonPressed(ActionEvent event) {
        ChangeScene("TeacherMainScreen");
    }

    @FXML
    void addQuestion(ActionEvent event) throws IOException {
        ChangeScene("TeacherAddQuestion");
    }

    @FXML
    void addTestForms(ActionEvent event) throws IOException {
        ChangeScene("TeacherAddTestForm");
    }

    @FXML
    void gradeExam(ActionEvent event) {
        ChangeScene("TeacherExamGrade");
    }

    @FXML
    void createExam(ActionEvent event) {

        ChangeScene("TeacherCreateClassExam");
    }

    @FXML
    void showStatistics(ActionEvent event)  {
        ChangeScene("ShowStatistics");
    }

    @FXML
    void viewQuestion(ActionEvent event) {
        ChangeScene("TeacherViewQuestions");
    }

    @FXML
    void viewClassExams(ActionEvent event) {
        ChangeScene("TeacherViewLiveExams");
    }

    @FXML
    void viewTestForms(ActionEvent event) {
        ChangeScene("TeacherViewLiveExams"/*"ViewTestForms"*/);
    }

    @FXML
    void initialize() {
        InitializationAsserts();
        userTypeLabel.setText("Logged in as: Teacher");
    }

    void InitializationAsserts(){
        assert addQuestionButton != null : "fx:id=\"addQuestionButton\" was not injected: check your FXML file 'TeacherSidebar.fxml'.";
        assert addTestFormsButton != null : "fx:id=\"addTestFormsButton\" was not injected: check your FXML file 'TeacherSidebar.fxml'.";
        assert gradeExamButton != null : "fx:id=\"gradeExamButton\" was not injected: check your FXML file 'TeacherSidebar.fxml'.";
        assert showStatisticsButton != null : "fx:id=\"showStatisticsButton\" was not injected: check your FXML file 'TeacherSidebar.fxml'.";
        assert viewQuestionButton != null : "fx:id=\"viewQuestionButton\" was not injected: check your FXML file 'TeacherSidebar.fxml'.";
        assert viewTestFormsButton != null : "fx:id=\"viewTestFormsButton\" was not injected: check your FXML file 'TeacherSidebar.fxml'.";
    }

}
