package Client.Controllers.Sidebars;

import Client.SimpleChatClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PrincipalSidebarController extends SideBar {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addQuestionButton;

    @FXML
    private Button addTestFormsButton;

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
    private Button viewRequestsListButton;

    @FXML
    private Button mainPageButton;

    @FXML
    public void OnMainPageButtonPressed(ActionEvent event) {
        ChangeScene("PrincipalMainScreen");
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
    void extraTimeList(ActionEvent event) throws IOException {
        //ChangeScene("ListExtraTimeRequest");
        SimpleChatClient.NewSetRoot("ListExtraTimeRequest");
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
        ChangeScene("PreviewTestForm");
    }

    @FXML
    void initialize() {
        InitializationAsserts();
        userTypeLabel.setText("Logged in as: Principal");
    }

    void InitializationAsserts(){
        //assert addQuestionButton != null : "fx:id=\"addQuestionButton\" was not injected: check your FXML file 'PrincipalSidebar.fxml'.";
       // assert addTestFormsButton != null : "fx:id=\"addTestFormsButton\" was not injected: check your FXML file 'PrincipalSidebar.fxml'.";
        assert showStatisticsButton != null : "fx:id=\"showStatisticsButton\" was not injected: check your FXML file 'PrincipalSidebar.fxml'.";
        assert userTypeLabel != null : "fx:id=\"userTypeLabel\" was not injected: check your FXML file 'PrincipalSidebar.fxml'.";
        assert viewClassExamsButton != null : "fx:id=\"viewClassExamsButton\" was not injected: check your FXML file 'PrincipalSidebar.fxml'.";
        assert viewQuestionButton != null : "fx:id=\"viewQuestionButton\" was not injected: check your FXML file 'PrincipalSidebar.fxml'.";
        assert viewTestFormsButton != null : "fx:id=\"viewTestFormsButton\" was not injected: check your FXML file 'PrincipalSidebar.fxml'.";
    }

}
