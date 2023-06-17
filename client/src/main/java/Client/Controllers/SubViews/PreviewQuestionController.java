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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class PreviewQuestionController {

    public VBox mainPane;

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
        System.out.println("entering populate question");
        PopulateQuestion();
        CreateAnswers();
    }

    private void PopulateQuestion() {
        System.out.println("Client.Controllers.MainViews.SubViews.PreviewQuestionController.PopulateQuestion");
        questionText.setText(question.getQuestionData());
        // set question font and size
        questionText.setStyle("-fx-font-size: " + "Calibri" + "; -fx-font-size: " + "17" + ";" + "-fx-wrap-text: true" + ";" + "-fx-text-alignment: center_left" + ";");
        if (question.getQuestionData().length() > 100) {
            questionText.setWrapText(true);
        }
        if (question.getQuestionData().length() > 200) {
            questionText.setPrefHeight(100);
        }
        if (question.getQuestionData().length() > 300) {
            questionText.setPrefHeight(150);
        }
        if (question.getQuestionData().length() > 400) {
            questionText.setPrefHeight(200);
        }
        if (question.getStudentNote() != null && !question.getStudentNote().trim().isEmpty()) {
            if(!mainPane.getChildren().contains(studentNotes)){
                System.out.println("adding studentNotes");
                mainPane.getChildren().add(1,studentNotes);
            }
            System.out.println("studentNotes is not empty");
            System.out.println("studentNotes: " + question.getStudentNote());
            studentNotes.setText(question.getStudentNote());
        }
        else {
            if(mainPane.getChildren().contains(studentNotes)){
                System.out.println("removing studentNotes");
                mainPane.getChildren().remove(studentNotes);
            }
            System.out.println("studentNotes is empty");
            studentNotes.setVisible(false);
            studentNotes.setLineSpacing(0);
        }
        //studentNotes.setText(question.getStudentNote());
        /*if(Objects.equals(studentNotes.getText(), "")){
            studentNotes.setVisible(false);
        }*/
        //mainPane.getChildren().remove(teacherNotes);
        //verify that the logged-in user is a teacher
        if(SimpleClient.getUser() instanceof Teacher){
            //mainPane.getChildren().add(4,teacherNotes);
            teacherNotes.setText(question.getTeacherNote());
            teacherNotes.setVisible(true);
        }
        System.out.println("question populated");
    }

    private void CreateAnswers() {
        answersToggleGroup = new ToggleGroup();
        List<String> answers = new ArrayList<>(question.getAnswers());
        //Collections.shuffle(answers);
        //answers.add(question.getCorrectAnswer());
        for (String questionAnswer : answers) {
            RadioButton answer = new RadioButton(questionAnswer);
            answer.setStyle("-fx-font-size: " + "Calibri" + "; -fx-font-size: " + "15" + ";" + "-fx-alignment: CENTER_LEFT" + ";" + "-fx-wrap-text: true"  + ";");
            //edit padding
            answer.setPadding(new javafx.geometry.Insets(5, 5, 5, 5));
            if(answer.getText().equals(selectedAnswer)){
                answer.setSelected(true);
            }
            answer.setToggleGroup(answersToggleGroup);
            //answers.add(questionAnswer);
            answersBox.getChildren().add(answer);
            answersBox.setPrefHeight(200);
            answersBox.setPrefWidth(100);
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
