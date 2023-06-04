import java.net.URL;
import java.util.*;

import Entities.Question;
import Events.ChangePreviewEvent;
import Events.RequestStudentAnswerToQuestion;
import Events.StudentAnswerToQuestion;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PreviewQuestionController {

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

    private String selectedAnswer = null;

    @FXML
    void initialize() {
        AssertFXMLComponents();
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void SetQuestion(ChangePreviewEvent event) {
        System.out.println("PreviewQuestionController.SetQuestion");
        if(answers != null){
            answers.clear();
        }
        answersBox.getChildren().clear();
        this.question = event.getQuestion();
        if(event.getSelectedAnswer() != null){
            this.selectedAnswer = event.getSelectedAnswer();
        }
        PopulateQuestion();
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
        List<String> answers = new ArrayList<>(question.getIncorrectAnswers());
        Collections.shuffle(answers);
        //answers.add(question.getCorrectAnswer());
        for (String questionAnswer : answers) {
            RadioButton answer = new RadioButton(questionAnswer);
            if(answer.getText().equals(selectedAnswer)){
                answer.setSelected(true);
            }
            answer.setToggleGroup(answersToggleGroup);
            //answers.add(questionAnswer);
            answersBox.getChildren().add(answer);
        }

    }

    public void ReceiveAnswerRequest(RequestStudentAnswerToQuestion event) {
        System.out.println("PreviewQuestionController.ReceiveAnswerRequest");
        if (answersToggleGroup.getSelectedToggle() != null) {
            selectedAnswer = ((RadioButton) answersToggleGroup.getSelectedToggle()).getText();
        }
        EventBus.getDefault().post(new StudentAnswerToQuestion(question, selectedAnswer));
    }


    private void AssertFXMLComponents() {
        assert answersBox != null : "fx:id=\"answersBox\" was not injected: check your FXML file 'PreviewQuestion.fxml'.";
        assert questionText != null : "fx:id=\"questionText\" was not injected: check your FXML file 'PreviewQuestion.fxml'.";
        assert studentNotes != null : "fx:id=\"studentNotes\" was not injected: check your FXML file 'PreviewQuestion.fxml'.";
        assert teacherNotes != null : "fx:id=\"teacherNotes\" was not injected: check your FXML file 'PreviewQuestion.fxml'.";
    }
}
