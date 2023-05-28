import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import Entities.Question;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class QuestionSceneController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private VBox answersBox;

    @FXML
    private Label questionText;

    @FXML
    private Label studentNotes;

    @FXML
    private Label teacherNotes;

    private Question question;

    private List<RadioButton> answers;

    private ToggleGroup answersToggleGroup;

    @FXML
    void initialize() {
        AssertFXMLComponents();
        CreateAnswers();

    }

    private void PopulateQuestion() {
        questionText.setText(question.getQuestionData());
        studentNotes.setText(question.getStudentNote());
        // TODO: verify that the logged in user is a teacher
        teacherNotes.setText(question.getTeacherNote());
    }

    private void CreateAnswers() {
        // TODO: handle saving the answer
        answersToggleGroup = new ToggleGroup();
        for (String questionAnswer : question.getAnswers()) {
            RadioButton answer = new RadioButton(questionAnswer);
            answer.setToggleGroup(answersToggleGroup);
            answers.add(answer);
            answersBox.getChildren().add(answer);
        }
    }
    private void AssertFXMLComponents() {
        assert answersBox != null : "fx:id=\"answersBox\" was not injected: check your FXML file 'QuestionScene.fxml'.";
        assert questionText != null : "fx:id=\"questionText\" was not injected: check your FXML file 'QuestionScene.fxml'.";
        assert studentNotes != null : "fx:id=\"studentNotes\" was not injected: check your FXML file 'QuestionScene.fxml'.";
        assert teacherNotes != null : "fx:id=\"teacherNotes\" was not injected: check your FXML file 'QuestionScene.fxml'.";
    }
}
