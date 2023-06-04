import Entities.*;
import Events.ChangePreviewEvent;
import Events.StartExamEvent;
import Server.SimpleServer;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import com.jfoenix.controls.JFXButton;


import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

//import static sun.swing.SwingUtilities2.submit;

public class StudentDoExamDigitalController {
    @FXML
    private FlowPane progressPane;

    @FXML
    private Label question;

    @FXML
    private Button nextButton;

    @FXML
    private Button previousButton;

    @FXML
    private Button submitButton;

    @FXML
    private Label timeLeft;

    @FXML
    private Label title;

    @FXML
    private Pane previewWindow;

    //NON FXML FIELDS
    private Person user;
    private ClassExam mainClassExam;
    private StudentExam studentExam = new StudentExam();
    private ExamForm selectedForm;
    private List<Question> questionList;
    private List<String> rightAnswers = new ArrayList<>();
    private List<String> studentAnswers;
    private List<JFXButton> progressButtons = new ArrayList<>();
    private Question currentQuestion;
    int numberOfQuestions;
    int currentIndex = 0;
    //private QuestionsObservable questionsObservable;
    //private Map<Question, String> studentAnswers = new HashMap<>();
    private int numberOfRightAnswers = 0;
    private int timeInSeconds;


    @FXML
    void initialize() {
        System.out.println("Initializing TeacherViewQuestionsController");
        EventBus.getDefault().register(this);
        user = SimpleClient.getUser();
        nextButton.setVisible(true);
        previousButton.setVisible(false);
        submitButton.setVisible(false);
        AssertFXMLComponents();

        CreateListeners();

        CreatePreviewScene();
        currentIndex = 0;

    }

    private void CreatePreviewScene() {
        try {
            Parent previewParent = SimpleChatClient.loadFXML("PreviewQuestion");
            previewWindow.getChildren().clear();
            previewWindow.getChildren().add(previewParent);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Subscribe
    public void getExam(StartExamEvent event) {
        mainClassExam = event.getClassExam();
        System.out.println("1. in getExam- mainClassExam: " + mainClassExam);
        System.out.println("2. in getExam- Exam Course: " + mainClassExam.getCourse());
        //selectedForm = SimpleServer.getExamForm(mainClassExam.getExamForm().getID());
        selectedForm = mainClassExam.getExamForm();
        System.out.println("3. in getExam- selectedForm: " + selectedForm);
        System.out.println(selectedForm);
        System.out.println(selectedForm.getQuestionList());
        title.setText("Exam in " + selectedForm.getSubject().getName() + " - " + selectedForm.getCourse().getName());
        studentExam.setStudent(((Student) (SimpleClient.getUser())));
        studentExam.setClassExam(mainClassExam);
        studentExam.setStatus(HSTS_Enums.StatusEnum.ToEvaluate);
        questionList = selectedForm.getQuestionList();
        numberOfQuestions = selectedForm.getQuestionList().size();
        renderProgress();
        timeInSeconds = (int) (mainClassExam.getExamTime() * 60);
        setTimer();
        currentQuestion = questionList.get(currentIndex);
        studentAnswers = new ArrayList<>(Collections.nCopies(numberOfQuestions, "0"));
        for (Question question : selectedForm.getQuestionList()) {
            rightAnswers.add(question.getCorrectAnswer());
        }
        System.out.println("4. in getExam- currentQuestion: " + currentQuestion);
        setTimer();
        ChangePreviewEvent newEvent = new ChangePreviewEvent();
        newEvent.setQuestion(currentQuestion);
        EventBus.getDefault().post(newEvent);
        System.out.println("5. after sending first question to preview");
    }

    private void setTimer() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(timeInSeconds>0) {
                    timeInSeconds--;
                    Platform.runLater(() ->{timeLeft.setText("Time Left: " + String.format("%02d",timeInSeconds/3600)+ ":" + String.format("%02d",(timeInSeconds%3600)/60)+ ":" + String.format("%02d",timeInSeconds%60));});

                }
                else {
                    try {
                        SimpleClient.getClient().sendToServer(new Message(1,"Exam Fail: Time Ended"));
                        timer.cancel();
                        SimpleChatClient.setRoot("ChooseExam");
                        JOptionPane.showMessageDialog(null,"Exam time ended and no exam was submitted", "Submission Exam", JOptionPane.WARNING_MESSAGE);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        };
        timer.schedule(task,0, 1000);
    }

        private void CreateListeners() {

            //create a listener for the next Question button
            nextButton.setOnAction(e -> {
                if (currentIndex < numberOfQuestions - 2) {
                    currentIndex++;
                    if (currentIndex != 0) {
                        previousButton.setVisible(true);
                    }
                    UpdateQuestion();
                }
                else {
                    nextButton.setVisible(false);
                    submitButton.setVisible(true);
                }
            });

            //create a listener for the previous Question button
            previousButton.setOnAction(e -> {
                if (currentIndex > 0) {
                    currentIndex--;
                    nextButton.setVisible(true);
                    submitButton.setVisible(false);
                    if (currentIndex == 0) {
                        previousButton.setVisible(false);
                    }
                    UpdateQuestion();
                }
                else {
                    nextButton.setVisible(true);
                    submitButton.setVisible(false);
                }
            });
        }

        @Subscribe
        private void UpdateQuestion() {
            currentQuestion = questionList.get(currentIndex);
            System.out.println("Updating preview");
            System.out.println("currentQuestion: " + currentQuestion);
            System.out.println(currentQuestion.getCorrectAnswer());
            ChangePreviewEvent event = new ChangePreviewEvent();
            event.setQuestion(currentQuestion);
            EventBus.getDefault().post(event);
        }

    /*
    @FXML
    void nextQuestion(ActionEvent event) {
        boolean isRight = false;
        {
            // checking answer
            RadioButton selectedButton = (RadioButton) options.getSelectedToggle();
            String userAnswer = selectedButton.getText();
            String rightAnswer = this.currentQuestion.getCorrectAnswer();
            if (userAnswer.trim().equalsIgnoreCase(rightAnswer.trim())) {
                isRight = true;
                this.numberOfRightAnswers++;
            }

            // saving Answer to hashMap
            //studentAnswers.put(this.currentQuestion, userAnswer);
        }
        Node circleNode = this.progressPane.getChildren().get(currentIndex - 1);
        ProgressCircleController controller = (ProgressCircleController) circleNode.getUserData();
        if (isRight) {
            controller.setRightAnswerColor();
        } else {
            controller.setWrongAnswerColor();
        }
        this.setNextQuestion();
    }*/


    @FXML
    void previousQuestion(ActionEvent event) {}
    @FXML
    void nextQuestion(ActionEvent event){}

    @FXML
    void submitExam(ActionEvent event) {
        System.out.println(this.studentAnswers);
        System.out.println(this.studentExam.getStudent());
        //boolean result = StudentExam.save(this.studentAnswers);
        /*if (result) {
            // show success notification
            // timer.cancel();
            // openMainStudentScreen();
        } else {
            // show error notification
        }*/
    }





    private void setNextQuestion() {
        if (!(currentIndex >= studentExam.getClassExam().getExamForm().getQuestionList().size())) {
            {
                // changing the color
                Node circleNode = this.progressPane.getChildren().get(currentIndex);
                ProgressCircleController controller = (ProgressCircleController) circleNode.getUserData();
                controller.setCurrentQuestionColor();
            }

            this.currentQuestion = this.studentExam.getClassExam().getExamForm().getQuestionList().get(currentIndex);
            List<String> options = new ArrayList<>();
            options.add(this.currentQuestion.getIncorrectAnswers().get(0));
            options.add(this.currentQuestion.getIncorrectAnswers().get(1));
            options.add(this.currentQuestion.getIncorrectAnswers().get(2));
            options.add(this.currentQuestion.getCorrectAnswer());
            Collections.shuffle(options);

            this.currentQuestion.setIncorrectAnswers(options);

            //this.questionsObservable.setQuestion(this.currentQuestion);
            currentIndex++;
        } else {
            nextButton.setVisible(false);
            submitButton.setVisible(true);
        }
    }

    private void renderProgress() {
        System.out.println("rendering progress: questionList.size() = " + questionList.size());
        progressButtons = new ArrayList<>();
        for (int i = 0; i < questionList.size(); i++) {
            JFXButton button = new JFXButton();
            button.setText(String.valueOf(i + 1));
            button.setPrefWidth(50);
            button.setPrefHeight(50);
            button.setStyle("-fx-background-color:  #669bbc; -fx-background-radius: 50px; -fx-text-fill: #ffffff; -fx-font-size: 15px; -fx-font-weight: bold; ");
            button.setUserData(i);
            button.setOnAction(e -> {
                int index = (int) button.getUserData();
                this.currentIndex = index-1;
                this.UpdateQuestion();
            });
            this.progressPane.getChildren().add(button);
        }
    }




    /*
    private void renderProgress() {
        for (int i = 0; i < questionList.size(); i++) {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass()
                            .getResource("ProgressCircle.fxml"));
            try {
                Node node = fxmlLoader.load();
                ProgressCircleController progressCircleFXMLController = fxmlLoader.getController();
                progressCircleFXMLController.setNumber((Integer)(i + 1));
                progressCircleFXMLController.setDefaultColor();
                progressPane.getChildren().add(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/


//////////////// timer methods //////////////////////
    /*private void setTimer() {
        double totalMin = (double)mainClassExam.getExamTime();
        this.timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("After 1 sec...");
                        convertTime();
                        if (totalMin <= 0) {
                            timer.cancel();
                            timing.setText("00:00:00");
                            // saveing data to database
                            //submitExam(null);
                            // "time is up" notification;
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    public void convertTime() {
        TimeUnit TimeUnit = null;
        min = TimeUnit.SECONDS.toMinutes(totalSec);
        sec = totalSec - (min * 60);
        hr = TimeUnit.MINUTES.toHours(min);
        min = min - (hr * 60);
        timing.setText(format(hr) + ":" + format(min) + ":" + format(sec));

        totalSec--;
    }

    private String format(long value) {
        if (value < 10) {
            return 0 + "" + value;
        }
        return value + "";
    }*/
//////////////////////////////////////////////////////////

    @FXML
    void AssertFXMLComponents() {
        assert nextButton != null : "fx:id=\"nextButton\" was not injected: check your FXML file 'StudentDoExamDigital.fxml'.";
        assert previewWindow != null : "fx:id=\"previewWindow\" was not injected: check your FXML file 'StudentDoExamDigital.fxml'.";
        assert previousButton != null : "fx:id=\"previousButton\" was not injected: check your FXML file 'StudentDoExamDigital.fxml'.";
        assert progressPane != null : "fx:id=\"progressPain\" was not injected: check your FXML file 'StudentDoExamDigital.fxml'.";
        assert submitButton != null : "fx:id=\"submitButton\" was not injected: check your FXML file 'StudentDoExamDigital.fxml'.";
        assert timeLeft != null : "fx:id=\"timeLeft\" was not injected: check your FXML file 'StudentDoExamDigital.fxml'.";
        assert title != null : "fx:id=\"title\" was not injected: check your FXML file 'StudentDoExamDigital.fxml'.";

    }
}