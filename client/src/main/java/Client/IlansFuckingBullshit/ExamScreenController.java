package Client.IlansFuckingBullshit;

import Client.Controllers.SubViews.ProgressCircleController;
import Client.Events.StartExamEvent;
import Client.SimpleClient;
import Entities.SchoolOwned.ClassExam;
import Entities.SchoolOwned.ExamForm;
import Entities.Enums;
import Entities.SchoolOwned.Question;
import Entities.StudentOwned.StudentExam;
import Entities.Users.Student;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

//import static sun.swing.SwingUtilities2.submit;

public class ExamScreenController {
    private class QuestionsObservable {
        Property<String> question = new SimpleStringProperty();
        Property<String> option1 = new SimpleStringProperty();
        Property<String> option2 = new SimpleStringProperty();
        Property<String> option3 = new SimpleStringProperty();
        Property<String> option4 = new SimpleStringProperty();
        Property<String> answer = new SimpleStringProperty();

        public void setQuestion(Question question) {
            this.question.setValue(question.getQuestionData());
            ArrayList<String> options = new ArrayList<String>();
            //for (String answer : question.getAnswers()) {options.add(answer);}
            for (int i = 0; i < 3; i++) {
                options.add(question.getAnswers().get(i));
            }
            options.add(question.getCorrectAnswer());
            Collections.shuffle(options);
            this.option1.setValue(options.get(0));
            this.option2.setValue(options.get(1));
            this.option3.setValue(options.get(2));
            this.option4.setValue(options.get(3));
            this.answer.setValue(question.getCorrectAnswer());
        }
    }

    @FXML
    private ToggleGroup options;

    @FXML
    private RadioButton option1;

    @FXML
    private RadioButton option2;

    @FXML
    private RadioButton option3;

    @FXML
    private RadioButton option4;

    @FXML
    private FlowPane progressPane;

    @FXML
    private Label question;

    @FXML
    private Button nextButton;

    @FXML
    private Button submitButton;

    @FXML
    private Label timing;

    @FXML
    private Label title;

    //NON FXML FIELDS
    //private ExamForm examForm;
    private ClassExam mainClassExam;
    private StudentExam studentExam = new StudentExam();
    private List<Question> questionList;
    private List<String> rightAnswers;
    private List<String> studentAnswers;
    private Question currentQuestion;
    int numberOfQuestions;
    int currentIndex = 0;
    private QuestionsObservable questionsObservable;
    //private Map<Question, String> studentAnswers = new HashMap<>();
    private Integer numberOfRightAnswers = 0;
    private int timeInSeconds;
    //private Student student;

    //    timer fields
    private long min, sec, hr, totalSec = 0;
    private Timer timer;

    @FXML
    void initialize() {
        EventBus.getDefault().register(this);
        nextButton.setVisible(true);
        submitButton.setVisible(false);

        this.questionsObservable = new QuestionsObservable();
        bindFields();

        this.option1.setSelected(true);
    }

    @Subscribe
    public void getExam(StartExamEvent event) {
        mainClassExam = event.getClassExam();
        System.out.println(mainClassExam);
        System.out.println(mainClassExam.getCourse());

        createDigitalExam(mainClassExam);
        timeInSeconds = (int) (mainClassExam.getExamTime() * 60);
        studentExam.setStudent(((Student) (SimpleClient.getUser())));
        studentExam.setClassExam(mainClassExam);
        studentExam.setStatus(Enums.submissionStatus.ToEvaluate);


    }

    public void createDigitalExam(ClassExam selectedExam) {
        ExamForm selectedForm = selectedExam.getExamForm();
        title.setText("Exam in " + selectedForm.getSubject().getName() + " - " + selectedForm.getCourse().getName());
        questionList = selectedForm.getQuestionList();
        numberOfQuestions = selectedForm.getQuestionList().size();
        studentAnswers = new ArrayList<>(Collections.nCopies(numberOfQuestions, "0"));
        Random random = new Random();

        /*for (Question question : questionList) {
            Client.Controllers.SubViews.ProgressCircleController controller = new Client.Controllers.SubViews.ProgressCircleController();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ProgressCircle.fxml"));
            loader.setController(controller);
            try {
                Node node = loader.load();
                node.setUserData(controller);
                progressPane.getChildren().add(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        for (Question question : selectedForm.getQuestionList()) {
            rightAnswers.add(question.getCorrectAnswer());
        }
    }

    @FXML
    void nextQuestion(ActionEvent event) {
        /*boolean isRight = false;
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
        Client.Controllers.SubViews.ProgressCircleController controller = (Client.Controllers.SubViews.ProgressCircleController) circleNode.getUserData();
        if (isRight) {
            controller.setRightAnswerColor();
        } else {
            controller.setWrongAnswerColor();
        }*/
        this.setNextQuestion();
    }

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
    /*
    @FXML
    void initialize() {
        assert options != null : "fx:id=\"Options\" was not injected: check your FXML file 'ExamScreen.fxml'.";
        assert option1 != null : "fx:id=\"option1\" was not injected: check your FXML file 'ExamScreen.fxml'.";
        assert option2 != null : "fx:id=\"option2\" was not injected: check your FXML file 'ExamScreen.fxml'.";
        assert option3 != null : "fx:id=\"option3\" was not injected: check your FXML file 'ExamScreen.fxml'.";
        assert option4 != null : "fx:id=\"option4\" was not injected: check your FXML file 'ExamScreen.fxml'.";
        assert progressPane != null : "fx:id=\"progressPain\" was not injected: check your FXML file 'ExamScreen.fxml'.";
        assert question != null : "fx:id=\"question\" was not injected: check your FXML file 'ExamScreen.fxml'.";
        assert nextButton != null : "fx:id=\"nextButton\" was not injected: check your FXML file 'ExamScreen.fxml'.";
        assert submitButton != null : "fx:id=\"submitButton\" was not injected: check your FXML file 'ExamScreen.fxml'.";
        assert timing != null : "fx:id=\"timing\" was not injected: check your FXML file 'ExamScreen.fxml'.";
        assert title != null : "fx:id=\"title\" was not injected: check your FXML file 'ExamScreen.fxml'.";

    }*/




    private void bindFields() {
        this.question.textProperty().bind(this.questionsObservable.question);
        this.option4.textProperty().bind(this.questionsObservable.option4);
        this.option3.textProperty().bind(this.questionsObservable.option3);
        this.option2.textProperty().bind(this.questionsObservable.option2);
        this.option1.textProperty().bind(this.questionsObservable.option1);
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
            options.add(this.currentQuestion.getAnswers().get(0));
            options.add(this.currentQuestion.getAnswers().get(1));
            options.add(this.currentQuestion.getAnswers().get(2));
            options.add(this.currentQuestion.getCorrectAnswer());
            Collections.shuffle(options);

            this.currentQuestion.setAnswers(options);

            this.questionsObservable.setQuestion(this.currentQuestion);
            currentIndex++;
        } else {
            nextButton.setVisible(false);
            submitButton.setVisible(true);
        }
    }

    private void getData() {
        if (studentExam.getClassExam().getExamForm() != null) {
            this.questionList = studentExam.getClassExam().getExamForm().getQuestionList();
            Collections.shuffle(this.studentExam.getClassExam().getExamForm().getQuestionList());
            renderProgress();
            setNextQuestion();
            setTimer();
        }
    }

    private void renderProgress() {
        for (int i = 0; i < this.questionList.size(); i++) {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass()
                            .getResource("/fxml/student/ProgressCircleFXML.fxml"));
            try {
                Node node = fxmlLoader.load();
                ProgressCircleController progressCircleFXMLController = fxmlLoader.getController();
                progressCircleFXMLController.setNumber(i + 1);
                progressCircleFXMLController.setDefaultColor();
                progressPane.getChildren().add(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    //   timer methods

    private String format(long value) {
        if (value < 10) {
            return 0 + "" + value;
        }
        return value + "";
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

    private void setTimer() {
        double totalMin = this.studentExam.getClassExam().getExamForm().getExamTime();
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
                            submitExam(null);
                            // "time is up" notification;
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }


}