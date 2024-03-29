package Client.Controllers.MainViews.StudentViews;

import Client.Controllers.MainViews.SaveBeforeExit;
import Client.Events.*;
import Client.SimpleChatClient;
import Client.SimpleClient;
import Entities.SchoolOwned.ClassExam;
import Entities.Communication.Message;
import Entities.SchoolOwned.ExamForm;
import Entities.Enums;
import Entities.SchoolOwned.Question;
import Entities.StudentOwned.StudentExam;
import Entities.Users.Person;
import Entities.Users.Student;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;
import java.io.IOException;
import java.util.Timer;
import java.util.*;


public class StudentDoExamDigitalController extends SaveBeforeExit {
    @FXML
    private FlowPane progressPane;

    @FXML
    private Label timeLeft;

    @FXML
    private Label title;

    @FXML
    private Label questionNumber;

    @FXML
    private Button nextButton;

    @FXML
    private Button previousButton;

    @FXML
    private Button submitButton;

    @FXML
    private Pane previewWindow;

    //NON FXML FIELDS
    private Person user;
    private ClassExam mainClassExam;
    private final StudentExam studentExam = new StudentExam();
    private ExamForm selectedForm;
    private List<Question> questionList;
    private final List<String> rightAnswers = new ArrayList<>();
    private final List<JFXButton> progressButtons = new ArrayList<>();
    private final Map<Question, String> studentQuestionsAnswers = new HashMap<>();

    //private List<String> studentAnswers;
    private Question currentQuestion;
    private int numberOfRightAnswers = 0;
    private int numberOfQuestionsAnswered = 0;
    private int timeInSeconds;
    int numberOfQuestions;
    int currentIndex = 0;
    int previousIndex = 0;
    boolean isPreview = false;


    // TODO: delete fields that are not used in the end of the project (numberOfQuestionsAnswered, numberOfRightAnswers...)

    // initialize the StudentDoExamDigital Screen
    @FXML
    void initialize() {
        System.out.println("Initializing Client.Controllers.MainPanelScreens.TeacherViewQuestionsController");
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        user = SimpleClient.getUser();
        nextButton.setVisible(true);
        previousButton.setVisible(false);
        submitButton.setVisible(false);
        AssertFXMLComponents();

        //CreateListeners();

        CreatePreviewScene();

        currentIndex = 0;
    }

    // Create the preview scene, which is the scene that shows the question and the answers
    private void CreatePreviewScene() {
        try {
            Parent previewParent = SimpleChatClient.loadFXML("PreviewQuestion");
            previewWindow.getChildren().clear();
            previewWindow.getChildren().add(previewParent);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // This method is from the ChooseExam Screen, when the student clicks on the "Start Exam" button
    // It gets the exam from the server and sets the exam form and the class exam
    @Subscribe
    public void getExam(StartExamEvent event) {
        mainClassExam = event.getClassExam();
        user = SimpleClient.getUser();
        //System.out.println("1. in getExam- mainClassExam: " + mainClassExam);
        //System.out.println("2. in getExam- Exam Course: " + mainClassExam.getCourse());
        //selectedForm = SimpleServer.getExamForm(mainClassExam.getExamForm().getID());
        selectedForm = mainClassExam.getExamForm();
        //System.out.println("3. in getExam- selectedForm: " + selectedForm);
        //System.out.println(selectedForm);
        //System.out.println(selectedForm.getQuestionList());
        title.setText("Exam in " + selectedForm.getSubject().getName() + " - " + selectedForm.getCourse().getName());
        studentExam.setStudent(((Student) (SimpleClient.getUser())));
        studentExam.setClassExam(mainClassExam);
        studentExam.setStatus(Enums.submissionStatus.ToEvaluate);
        questionList = selectedForm.getQuestionList();
        numberOfQuestions = selectedForm.getQuestionList().size();
        //System.out.println("Number of questions: " + numberOfQuestions);
        renderProgress();
        timeInSeconds = (int) (mainClassExam.getExamTime() * 60);
        setTimer();
        currentQuestion = questionList.get(currentIndex);
        //studentAnswers = new ArrayList<>(Collections.nCopies(numberOfQuestions, "0"));
        for (Question question : questionList){ // shuffle the answers of each question
            List<String> sortedAnswers = question.getAnswers();
            Collections.shuffle(sortedAnswers);
            question.setAnswers(sortedAnswers);
            rightAnswers.add(question.getCorrectAnswer());
            //studentQuestionsAnswers.put(question, "0");
        }
        //System.out.println("4. in getExam- currentQuestion: " + currentQuestion);
        setTimer();
        //progressButtons.get(currentIndex).setStyle("-fx-border-color: #000000; -fx-border-width: 5px;");
        //String style = progressButtons.get(currentIndex).getStyle();
        //progressButtons.get(currentIndex).setStyle(style.substring(0,style.length()-1)+";-fx-border-color: #000000; -fx-border-width: 5px;");
        questionNumber.setText("Question " + (currentIndex+1) + " out of " + numberOfQuestions + ":");
        changeNextProgressButton(currentIndex);
        ChangePreviewEvent newEvent = new ChangePreviewEvent();
        newEvent.setQuestion(currentQuestion);
        EventBus.getDefault().post(newEvent);
        //System.out.println("5. after sending first question to preview");
    }

    @Subscribe
    public void getExamForPreview(StartExamPreviewEvent event) {
        System.out.println("in getExamForPreview");
        isPreview = true;
        selectedForm = StartExamPreviewEvent.getExamForm();
        //title.setText("Exam in " + selectedForm.getSubject().getName() + " - " + selectedForm.getCourse().getName());
        questionList = selectedForm.getQuestionList();
        numberOfQuestions = selectedForm.getQuestionList().size();
        renderProgress();
        //timeInSeconds = (int) (selectedForm.getExamTime() * 60);
        timeInSeconds = 60*60;
        setTimer();
        currentQuestion = questionList.get(currentIndex);
        for (Question question : questionList){ // shuffle the answers of each question
            List<String> sortedAnswers = question.getAnswers();
            Collections.shuffle(sortedAnswers);
            question.setAnswers(sortedAnswers);
            rightAnswers.add(question.getCorrectAnswer());
        }
        setTimer();
        questionNumber.setText("Question " + (currentIndex+1) + " out of " + numberOfQuestions + ":");
        changeNextProgressButton(currentIndex);
        ChangePreviewEvent newEvent = new ChangePreviewEvent();
        newEvent.setQuestion(currentQuestion);
        EventBus.getDefault().post(newEvent);
    }

    // get the answer of the student from the preview window
    @Subscribe
    public void getAnswer(StudentAnswerToQuestion event){
        System.out.println("///// In getAnswer /////");
        System.out.println("Answer received: " +event.getSelectedAnswer());
        Question question = event.getQuestion();
        int flag = 0;
        for (String answer : question.getAnswers()) {
            if (answer.equals(event.getSelectedAnswer())) {
                flag = 1;
                break;
            }
        }
        if (flag == 0) {
            System.out.println("Answer not found");
            return;
        }
        if (event.getSelectedAnswer() == null || event.getSelectedAnswer().equals("0")) { // if the student didn't answer the question
            System.out.println("Question " + question.getID() + " not answered");
            return;
        }
        System.out.println("Map Return Value: " + studentQuestionsAnswers.put(question, event.getSelectedAnswer()));
        if (studentQuestionsAnswers.put(question, event.getSelectedAnswer()) == null) { // if the question wasn't answered before
            System.out.println("Answer to question " + question.getID() + " was added");
            numberOfQuestionsAnswered++;
        }
        else // if the question was answered before
        {
            System.out.println("Answer to question " + question.getID() + " was updated");
        }
        System.out.println("Number of questions answered: " + numberOfQuestionsAnswered);
        //progressButtons.get(currentIndex-1).setStyle("-fx-background-color: #80d780;");
        progressButtons.get(previousIndex).setStyle("-fx-background-color:  #80d780; -fx-background-radius: 50px; -fx-text-fill: #ffffff; -fx-font-size: 15px; -fx-font-weight: bold;");
        System.out.println("///// End of getAnswer /////");
    }

    // set the timer for the exam
    private void setTimer() // set the timer for the exam
    {
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
                        SimpleClient.getClient().sendToServer(new Message(1, "Exam Fail: Time Ended"));
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

        // method to create listeners for the buttons in the exam (Right now it's not in use - replaced by separate listeners for each button)
        /*private void CreateListeners() // create listeners for the buttons
        {
            //create a listener for the next Question button
            nextButton.setOnAction(e -> {
                if (currentIndex < numberOfQuestions - 1) {
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
        }*/

        // method to update the question preview
        @Subscribe
        private void UpdateQuestion() {
            currentQuestion = questionList.get(currentIndex);
            System.out.println("//////// Updating preview ////////");
            System.out.println("currentQuestion: " + currentQuestion + " currentIndex: " + currentIndex + "Correct answer: " + currentQuestion.getCorrectAnswer());
            questionNumber.setText("Question " + (currentIndex+1) + " out of " + numberOfQuestions + ":");
            RequestStudentAnswerToQuestion request = new RequestStudentAnswerToQuestion();
            EventBus.getDefault().post(request);
            ChangePreviewEvent event = new ChangePreviewEvent();
            event.setQuestion(currentQuestion);
            System.out.println("currentQuestion: " + currentQuestion);
            System.out.println("studentQuestionsAnswers: " + studentQuestionsAnswers.get(currentQuestion));
            printMapHelper(studentQuestionsAnswers);
            if (studentQuestionsAnswers.get(currentQuestion) != null) {
                event.setSelectedAnswer(studentQuestionsAnswers.get(currentQuestion));
            }
            else {
                event.setSelectedAnswer("0");
            }
            EventBus.getDefault().post(event);
            System.out.println("//////// End of Updating preview ////////");
        }

        // method to print the map of questions and answers (for debugging)
        public void printMapHelper(Map<Question, String> map) {
            for (Map.Entry<Question, String> entry : map.entrySet()) {
                System.out.println("Question: " + entry.getKey().getID() + ", " + entry.getKey().getQuestionData() + ". Answer: " + entry.getValue());
            }
        }

    // method to handle the transition to the previous question
    @FXML
    void previousQuestion(ActionEvent event) {
            previousIndex = currentIndex;
        if (currentIndex > 0) {
            //String style= progressButtons.get(currentIndex).getStyle();
            //progressButtons.get(currentIndex).setStyle(style.substring(0,127) + " -fx-border-color: #ffffff; -fx-border-width: 0px; -fx-border-radius: 00px;");
            changePreviousProgressButton(currentIndex);
            currentIndex--;
            //style= progressButtons.get(currentIndex).getStyle();
            //progressButtons.get(currentIndex).setStyle(style.substring(0,127) + " -fx-border-color: #000000; -fx-border-width: 5px; -fx-border-radius: 45px;");
            changeNextProgressButton(currentIndex);
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
    }

    // method to handle the transition to the next question
    @FXML
    void nextQuestion(ActionEvent event){
        previousIndex = currentIndex;
        if (currentIndex < numberOfQuestions - 1) {
            //System.out.println("currentIndex - before: " + currentIndex + " numberOfQuestions: " + numberOfQuestions);
            //String style= progressButtons.get(currentIndex).getStyle();
            //progressButtons.get(currentIndex).setStyle(style.substring(0,127) + " -fx-border-color: #ffffff; -fx-border-width: 0px; -fx-border-radius: 00px;");
            changePreviousProgressButton(currentIndex);
            currentIndex++;
            //style= progressButtons.get(currentIndex).getStyle();
            //progressButtons.get(currentIndex).setStyle(style.substring(0,127) + " -fx-border-color: #000000; -fx-border-width: 5px; -fx-border-radius: 45px;");
            changeNextProgressButton(currentIndex);
            if (currentIndex > 0) {
                previousButton.setVisible(true);
            }
            UpdateQuestion();
            if (currentIndex == numberOfQuestions - 1) {
                nextButton.setVisible(false);
                if (isPreview == false) {
                    submitButton.setVisible(true);
                }
            }
        }
        else {
            nextButton.setVisible(false);
            if (isPreview == false) {
                submitButton.setVisible(true);
            }
        }
    }

    // method to handle the submission of the exam
    @FXML
    void submitExam(ActionEvent event) throws IOException {
        //System.out.println(this.studentAnswers);
        //System.out.println(this.studentExam.getStudent());
        System.out.println("///// Submitting exam /////");
        RequestStudentAnswerToQuestion request = new RequestStudentAnswerToQuestion();
        EventBus.getDefault().post(request);
        // TODO - examForm grade percentage is missing!
        int num_answered = 0;
        int num_correct = 0;
        int grade = 0;
        int i= 0;
        for (Question q : questionList){
            int percentage = selectedForm.getQuestionsScores().get(i);
            i++;
            String studentAnswer = studentQuestionsAnswers.get(q);
            if (studentAnswer != null){
                numberOfQuestionsAnswered++;
                num_answered++;
                if (studentAnswer.equals(q.getCorrectAnswer())){
                    numberOfRightAnswers++;
                    num_correct++;
                    grade += percentage;
                }
            }
        }
        System.out.println("numberOfQuestionsAnswered: " + numberOfQuestionsAnswered + ". numberOfQuestions: " + numberOfQuestions);
        System.out.println("numberOfRightAnswers: " + numberOfRightAnswers);
        System.out.println("num_answered: " + num_answered + ". num_correct: " + num_correct);
        ButtonType submit = new ButtonType("Submit", ButtonBar.ButtonData.YES);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", submit, ButtonType.CANCEL);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Are you sure you want to submit the exam?");
        if (timeInSeconds > 0){
        if ( num_answered < numberOfQuestions) {
            alert.setHeaderText("You have not answered all the questions. Are you sure you would like to submit anyway?");}
        else {
            alert.setHeaderText("You answered all the questions. Would you like to submit the exam?");}
            Optional<ButtonType> result = alert.showAndWait();
            /*if (result.get() == ButtonType.OK){
                try {
                    SimpleClient.getClient().sendToServer(new Message(1, "Exam Submitted"));
                    SimpleChatClient.setRoot("ChooseExam");
                    JOptionPane.showMessageDialog(null, "Exam submitted successfully", "Submission Exam", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // ... user chose CANCEL or closed the dialog
                return;
            }*/
            if (result.get() == ButtonType.CANCEL){
                return;
            }
        }
        else {
            alert.setHeaderText("Time is up!");
            Optional<ButtonType> result = alert.showAndWait();
            /*if (result.get() == ButtonType.OK){
                try {
                    SimpleClient.getClient().sendToServer(new Message(1, "Exam Submitted"));
                    SimpleChatClient.setRoot("ChooseExam");
                    JOptionPane.showMessageDialog(null, "Exam submitted successfully", "Submission Exam", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // ... user chose CANCEL or closed the dialog
                return;
            }*/
        }
        System.out.println("Grade : " + grade);
        studentExam.setGrade(grade);
        List<String> studentAnswers = new ArrayList<>();
        for (Question q : questionList){
            studentAnswers.add(studentQuestionsAnswers.get(q));
        }
        studentExam.setStudentAnswers(studentAnswers);
        studentExam.setClassExam(mainClassExam);
        studentExam.setStudent((Student) user);
        studentExam.setStatus(Enums.submissionStatus.ToEvaluate);
        Message msg= new Message(1, "Digital Exam for student ID: " + SimpleClient.getUser().getID());
        msg.setData(studentExam);
        SimpleClient.getClient().sendToServer(msg);
        //SimpleChatClient.setRoot("ChooseExam");
        JOptionPane.showMessageDialog(null, "Exam submitted successfully", "Submission Exam", JOptionPane.INFORMATION_MESSAGE);
        //boolean result = StudentExam.save(this.studentAnswers);
        /*if (result) {
            // show success notification
            // timer.cancel();
            // openMainStudentScreen();
        } else {
            // show error notification
        }*/

    }

    // method to initialize the buttons on the right side of the screen
    private void renderProgress() {
        System.out.println("rendering progress: questionList.size() = " + questionList.size());
        for (int i = 0; i < questionList.size(); i++) {
            JFXButton button = new JFXButton();
            button.setText(String.valueOf(i + 1));
            button.setPrefWidth(50);
            button.setPrefHeight(50);
            button.setStyle("-fx-background-color:  #669bbc; -fx-background-radius: 50px; -fx-text-fill: #ffffff; -fx-font-size: 15px; -fx-font-weight: bold;");
            button.setUserData(i);
            button.setOnAction(e -> {
                System.out.println("button " + button.getText() + " pressed");
                int index = (int) button.getUserData();
                //String style= progressButtons.get(currentIndex).getStyle();
                //progressButtons.get(currentIndex).setStyle(style.substring(0,127) + " -fx-border-color: #ffffff; -fx-border-width: 0px; -fx-border-radius: 00px;");
                System.out.println("/////////////// index before= " + currentIndex + " ///////////////");
                previousIndex = currentIndex;
                changePreviousProgressButton(previousIndex);
                this.currentIndex = index;
                //style= progressButtons.get(currentIndex).getStyle();
                //progressButtons.get(currentIndex).setStyle(style.substring(0,127) + " -fx-border-color: #000000; -fx-border-width: 5px; -fx-border-radius: 45px;");
                System.out.println("/////////////// index after= " + currentIndex + " ///////////////");
                changeNextProgressButton(currentIndex);
                this.UpdateQuestion();
                previousButton.setVisible(index != 0);
                if (index == questionList.size()-1) {
                    nextButton.setVisible(false);
                    submitButton.setVisible(true);
                }
                else {
                    nextButton.setVisible(true);
                    submitButton.setVisible(false);
                }
            });
            progressButtons.add(button);
            this.progressPane.getChildren().add(button);
        }
    }

    private void changePreviousProgressButton(int index) {
        System.out.println("changing previous button: " + index);
        String style= progressButtons.get(index).getStyle();
        progressButtons.get(index).setStyle(style.substring(0,128) + " -fx-border-color: #ffffff; -fx-border-width: 0px; -fx-border-radius: 00px;");
    }

    private void changeNextProgressButton(int index) {
        String style= progressButtons.get(index).getStyle();
        progressButtons.get(index).setStyle(style.substring(0,128) + " -fx-border-color: rgba(0,0,0,0.37); -fx-border-width: 3px; -fx-border-radius: 47px;");
    }

    // TODO: check if this method works in real time (when the principal approves the extra time)
    // add extra time to exam in case that the principal approved it
    @Subscribe
    public void getExtraTime(PrincipalApproveEvent event) {
        int addedTime = event.getExtraTime().getDelta();
        System.out.println("addedTime: " + addedTime);
        timeInSeconds += addedTime*60;
    }

    // method to end the exam in case that the time is up in the server
    @Subscribe
    public void endExam(ExamEndedEvent event) throws IOException {
            Platform.runLater(() -> {
                try {
                    System.out.println("exam saved successfully in the server");
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Exam Saved");
                    alert.setHeaderText("Exam Saved");
                    alert.setContentText("Exam Saved Successfully");
                    alert.showAndWait();
                    EventBus.getDefault().unregister(this);
                    SimpleChatClient.setRoot("StudentMainScreen");
                    //JOptionPane.showMessageDialog(null, "Exam was successfully saved", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    ///////////////////////// SaveBeforeExit /////////////////////////
    @Override
    public boolean CheckForUnsavedData() {
        System.out.println("CheckForUnsavedData Client.Controllers.MainPanelScreens.SaveBeforeExit");
        return true;
    }

    @Override
    public void SaveData() {
        ActionEvent event = new ActionEvent();
        try {
            submitExam(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//////////////////////////////////////////////////////////////////////
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