package Client.Controllers.SubViews;

import Client.Events.ChangePreviewEvent;
import Client.Events.RequestStudentAnswerToQuestion;
import Client.Events.StudentAnswerToQuestion;
import Client.SimpleClient;
import Entities.SchoolOwned.Question;
import Entities.Users.Teacher;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

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

    private String selectedAnswer = "";

    @FXML
    void initialize() {
        AssertFXMLComponents();
        EventBus.getDefault().register(this);
        teacherNotes.setVisible(false);
    }

    @Subscribe
    public void SetQuestion(ChangePreviewEvent event) {
        System.out.println("Client.Controllers.MainViews.SubViews.PreviewQuestionController.SetQuestion");
        if(answers != null){
            answers.clear();
        }
        answersBox.getChildren().clear();
        this.question = event.getQuestion();
        System.out.println("selectedAnswer: " + event.getSelectedAnswer());
        if(event.getSelectedAnswer() != null){
            this.selectedAnswer = event.getSelectedAnswer();
        }
        PopulateQuestion();
        CreateAnswers();
    }

    private void PopulateQuestion() {
        questionText.setText(question.getQuestionData());
        studentNotes.setText(question.getStudentNote());
        if(Objects.equals(studentNotes.getText(), "")){
            studentNotes.setVisible(false);
        }
        //verify that the logged-in user is a teacher
        if(SimpleClient.getUser() instanceof Teacher){
            teacherNotes.setText(question.getTeacherNote());
            teacherNotes.setVisible(true);
        }
    }

    private void CreateAnswers() {
        answersToggleGroup = new ToggleGroup();
        List<String> answers = new ArrayList<>(question.getAnswers());
        //Collections.shuffle(answers);
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

    @Subscribe
    public void ReceiveAnswerRequest(RequestStudentAnswerToQuestion event) {
        System.out.println("Client.Controllers.MainViews.SubViews.PreviewQuestionController.ReceiveAnswerRequest");
        if (answersToggleGroup.getSelectedToggle() != null) {
            selectedAnswer = ((RadioButton) answersToggleGroup.getSelectedToggle()).getText();
        }
        System.out.println("sending back selectedAnswer: " + selectedAnswer);
        EventBus.getDefault().post(new StudentAnswerToQuestion(question, selectedAnswer));
    }


    private void AssertFXMLComponents() {
        assert answersBox != null : "fx:id=\"answersBox\" was not injected: check your FXML file 'PreviewQuestion.fxml'.";
        assert questionText != null : "fx:id=\"questionText\" was not injected: check your FXML file 'PreviewQuestion.fxml'.";
        assert studentNotes != null : "fx:id=\"studentNotes\" was not injected: check your FXML file 'PreviewQuestion.fxml'.";
        assert teacherNotes != null : "fx:id=\"teacherNotes\" was not injected: check your FXML file 'PreviewQuestion.fxml'.";
    }
}
