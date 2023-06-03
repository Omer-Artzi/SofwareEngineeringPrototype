import Entities.Message;
import Entities.School;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

public class TeacherSidebarController {
    private int messageId;
    @FXML
    private Button viewClassExamBT;
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

    @Subscribe
    public void nothing(){}


    @FXML
    void addQuestion(ActionEvent event) throws IOException {
        SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("TeacherAddQuestion");

    }

    @FXML
    void addTestForms(ActionEvent event) throws IOException {
        SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("AddExam");
    }

    @FXML
    void gradeExam(ActionEvent event) throws IOException {
        SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("TeacherExamGrade");
    }


    @FXML
    void showStatistics(ActionEvent event) throws IOException {
        SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("ShowStatistics");
    }

    @FXML
    void viewQuestion(ActionEvent event) throws IOException {
        SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("TeacherViewQuestions");
    }

    @FXML
    void viewClassExams(ActionEvent event) throws IOException {
        SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("ViewClassExams");
    }

    @FXML
    void viewTestForms(ActionEvent event) throws IOException {
        SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("ViewTestForms");
    }
    @FXML
    public void viewLiveExam(ActionEvent event) throws IOException {
        //EventBus.getDefault().register(this);
        //EventBus.getDefault().post(event);
        //Message message=new Message(1,"Get Live Exams");
        SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("ViewLiveExams");
        Platform.runLater(()-> {
            try {
                System.out.println("Liad!!!!!!!!!!");
                //System.out.println(School.getInstance().);
                Message message=new Message(messageId++,"Get Live Exams");
                SimpleClient.getClient().sendToServer(message);
                //System.out.println(School.getInstance().getClassExams().get(0).getID());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //SimpleClient.getClient().sendToServer(message);
        //SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("ViewLiveExams");
    }
    @FXML
    void initialize() {
        //EventBus.getDefault().register(this);
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
