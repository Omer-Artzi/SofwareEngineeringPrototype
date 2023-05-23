

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

public class AddExamController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<?> CourseBox;

    @FXML
    private ComboBox<?> SubjectBox;

    @FXML
    private TextArea footerText;

    @FXML
    private TextArea headerText;

    @FXML
    private TableView<?> questionTable;

    @FXML
    void initialize() {
        assert CourseBox != null : "fx:id=\"CourseBox\" was not injected: check your FXML file 'addExam.fxml'.";
        assert SubjectBox != null : "fx:id=\"SubjectBox\" was not injected: check your FXML file 'addExam.fxml'.";
        assert footerText != null : "fx:id=\"footerText\" was not injected: check your FXML file 'addExam.fxml'.";
        assert headerText != null : "fx:id=\"headerText\" was not injected: check your FXML file 'addExam.fxml'.";
        assert questionTable != null : "fx:id=\"questionTable\" was not injected: check your FXML file 'addExam.fxml'.";

    }

}