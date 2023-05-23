import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class SideBarController {

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
    void addQuestion(ActionEvent event) {

    }

    @FXML
    void addTestForms(ActionEvent event) {

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
    void initialize() {
        InitializationAsserts();
    }

    void InitializationAsserts(){
        assert Menu != null : "fx:id=\"Menu\" was not injected: check your FXML file 'SideBar.fxml'.";
        assert addQuestionButton != null : "fx:id=\"addQuestionButton\" was not injected: check your FXML file 'SideBar.fxml'.";
        assert addTestFormsButton != null : "fx:id=\"addTestFormsButton\" was not injected: check your FXML file 'SideBar.fxml'.";
        assert gradeExamButton != null : "fx:id=\"gradeExamButton\" was not injected: check your FXML file 'SideBar.fxml'.";
        assert showStatisticsButton != null : "fx:id=\"showStatisticsButton\" was not injected: check your FXML file 'SideBar.fxml'.";
        assert viewQuestionButton != null : "fx:id=\"viewQuestionButton\" was not injected: check your FXML file 'SideBar.fxml'.";
        assert viewTestFormsButton != null : "fx:id=\"viewTestFormsButton\" was not injected: check your FXML file 'SideBar.fxml'.";

    }

}
