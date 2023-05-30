

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import Entities.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class TeacherMainScreenController  {
    @FXML
    private Button seeCurrentTestsBT;
    @FXML
    private Label Menu;
    @FXML
    private Button viewExamBT;
    @FXML
    private Label MenuBack;

    @FXML
    private Button addQuestionButton;

    @FXML
    private Button addTestFormsButton;

    @FXML
    private Button gradeExamButton;

    @FXML
    private Button showStatisticsButton;

    @FXML
    private AnchorPane slider;

    @FXML
    private Button viewQuestionButton;

    @FXML
    private Button viewTestFormsButton;

    @FXML
    void seeCurrentTests(ActionEvent event) throws IOException {
        SimpleChatClient.setRoot("currentTests");
    }

    @FXML
    void addQuestion(ActionEvent event) throws IOException {
        Message message = new Message(1, "Get Subjects");
        SimpleClient.getClient().sendToServer(message);
        SimpleChatClient.setRoot("addQuestion");
    }

    @FXML
    void addTestForms(ActionEvent event) throws IOException {
        SimpleChatClient.setRoot("addExam");
    }

    @FXML
    void gradeExam(ActionEvent event) {

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
    @FXML
    void viewExam(ActionEvent event) throws IOException {
        Message message = new Message(1, "Get Live Exams");
        SimpleClient.getClient().sendToServer(message);
        SimpleChatClient.setRoot("addQuestion");
    }

    @FXML
    void initialize() {
        assert Menu != null : "fx:id=\"Menu\" was not injected: check your FXML file 'TeacherMainScreen.fxml'.";
        assert MenuBack != null : "fx:id=\"MenuBack\" was not injected: check your FXML file 'TeacherMainScreen.fxml'.";
        assert addQuestionButton != null : "fx:id=\"addQuestionButton\" was not injected: check your FXML file 'TeacherMainScreen.fxml'.";
        assert addTestFormsButton != null : "fx:id=\"addTestFormsButton\" was not injected: check your FXML file 'TeacherMainScreen.fxml'.";
        assert gradeExamButton != null : "fx:id=\"gradeExamButton\" was not injected: check your FXML file 'TeacherMainScreen.fxml'.";
        assert showStatisticsButton != null : "fx:id=\"showStatisticsButton\" was not injected: check your FXML file 'TeacherMainScreen.fxml'.";
        assert slider != null : "fx:id=\"slider\" was not injected: check your FXML file 'TeacherMainScreen.fxml'.";
        assert viewQuestionButton != null : "fx:id=\"viewQuestionButton\" was not injected: check your FXML file 'TeacherMainScreen.fxml'.";
        assert viewTestFormsButton != null : "fx:id=\"viewTestFormsButton\" was not injected: check your FXML file 'TeacherMainScreen.fxml'.";

    }

}
