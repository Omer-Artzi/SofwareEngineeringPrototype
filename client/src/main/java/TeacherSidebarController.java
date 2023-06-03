import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;

public class TeacherSidebarController implements SideBar {

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
    private Button viewQuestionButton;

    @FXML
    private Button viewTestFormsButton;

    @FXML
    private Button viewClassExamsButton;

    @FXML
    private Label userTypeLabel;

    @FXML
    void addQuestion(ActionEvent event) throws IOException {
        //SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("AddQuestion");
        changeScene("AddQuestion");


    }

    @FXML
    void addTestForms(ActionEvent event) throws IOException {
        //SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("AddExam");
        changeScene("AddExam");
    }

    @FXML
    void gradeExam(ActionEvent event) throws IOException {
        //SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("TeacherExamGrade");
        changeScene("TeacherExamGrade");
    }


    @FXML
    void showStatistics(ActionEvent event) throws IOException {
        //SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("ShowStatistics");
        changeScene("ShowStatistics");
    }

    @FXML
    void viewQuestion(ActionEvent event) throws IOException {
        //SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("TeacherViewQuestions");
        changeScene("TeacherViewQuestions");
    }

    @FXML
    void viewClassExams(ActionEvent event) throws IOException {
        //SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("ViewClassExams");
        changeScene("viewLiveExams");
    }

    @FXML
    void viewTestForms(ActionEvent event) throws IOException {
        //SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("ViewTestForms");
        changeScene("ViewTestForms");
    }

    @FXML
    void initialize() {
        InitializationAsserts();
        userTypeLabel.setText("Logged in as: Teacher");
    }

    void InitializationAsserts(){
        assert Menu != null : "fx:id=\"Menu\" was not injected: check your FXML file 'TeacherSidebar.fxml'.";
        assert addQuestionButton != null : "fx:id=\"addQuestionButton\" was not injected: check your FXML file 'TeacherSidebar.fxml'.";
        assert addTestFormsButton != null : "fx:id=\"addTestFormsButton\" was not injected: check your FXML file 'TeacherSidebar.fxml'.";
        assert gradeExamButton != null : "fx:id=\"gradeExamButton\" was not injected: check your FXML file 'TeacherSidebar.fxml'.";
        assert showStatisticsButton != null : "fx:id=\"showStatisticsButton\" was not injected: check your FXML file 'TeacherSidebar.fxml'.";
        assert viewQuestionButton != null : "fx:id=\"viewQuestionButton\" was not injected: check your FXML file 'TeacherSidebar.fxml'.";
        assert viewTestFormsButton != null : "fx:id=\"viewTestFormsButton\" was not injected: check your FXML file 'TeacherSidebar.fxml'.";

    }

}
