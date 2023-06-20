package Client.Controllers.MainViews.StaffViews.TeacherViews;

import Client.Controllers.MainViews.SaveBeforeExit;
import Client.Events.*;
import Client.SimpleChatClient;
import Client.SimpleClient;
import Entities.Communication.Message;
import Entities.SchoolOwned.Course;
import Entities.SchoolOwned.Question;
import Entities.SchoolOwned.Subject;
import Entities.Users.Teacher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import org.controlsfx.control.ListSelectionView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TeacherAddQuestionController extends SaveBeforeExit {
    private final Course courseOfEditQuestion = null;
    private List<Course> courseOfTeacher;
    private List<Subject> subjectOfTeacher;
    private int counter;
    @FXML
    private Label questionDataLabel;
    @FXML
    private Pane previewWindow;
    @FXML
    private ListSelectionView<Course> Courses;
    @FXML
    private TableColumn<Answer, String> AnswerColumn;
    @FXML
    private TableView<Answer> AnswerTable;
    @FXML
    private TableColumn<Answer, String> NumberColumn;
    @FXML
    private TextArea QuestionDataTF;
    @FXML
    private TextArea StudentNote;
    @FXML
    private TextArea TeacherNote;
    @FXML
    private Button addQuestionButton;
    @FXML
    private ChoiceBox<String> addSubjectChoiceBox;
    @FXML
    private ChoiceBox<String> CorrectAnswerCB;
    @FXML
    private Button previewButton;
    private ContextualState state = ContextualState.ADD;



    @Subscribe
    public void editQuestion(StartEditExistingQuestionEvent event) {
        state = ContextualState.EDIT;
        String correct_answer = "";
        Question question = event.getQuestion();
        TeacherNote.setText(question.getTeacherNote());
        StudentNote.setText(question.getStudentNote());
        QuestionDataTF.setText(question.getQuestionData());
        addSubjectChoiceBox.setValue(question.getSubject().getName());
        ObservableList<Course> coursesOfQuestion = FXCollections.observableArrayList(question.getCourses());
        Courses.setTargetItems(coursesOfQuestion);
        List<String> answers = question.getAnswers();
        List<Answer> a = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            Answer answer = new Answer(String.valueOf(i), answers.get(i - 1));
            if (answer.getAnswer().equals(question.getCorrectAnswer()))
                correct_answer = answer.getNumber();
            a.add(answer);
        }
        CorrectAnswerCB.setValue(correct_answer);
        AnswerTable.setItems(FXCollections.observableArrayList(a));
    }

    @Subscribe
    public void endCreateQuestion(EndCreateQuestionEvent event) throws IOException {
        JOptionPane.showMessageDialog(null, event.getStatus(), "Question Status", JOptionPane.INFORMATION_MESSAGE);
        if(state.equals(ContextualState.EDIT))
            SimpleChatClient.setRoot("TeacherViewQuestions");
        else
            SimpleChatClient.setRoot("TeacherAddQuestion");
        EventBus.getDefault().unregister(this);
    }

    public void Elements(boolean b) {
        StudentNote.setDisable(b);
        TeacherNote.setDisable(b);
        QuestionDataTF.setDisable(b);
        previewButton.setDisable(b);
        CorrectAnswerCB.setDisable(b);
        addQuestionButton.setDisable(b);
    }

    @FXML
    void initialize() throws IOException {
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        Teacher teacher = ((Teacher) (SimpleClient.getClient().getUser()));
        Courses.setDisable(true);
        StudentNote.setDisable(true);
        Elements(true);
        courseOfTeacher = teacher.getCourses();
        subjectOfTeacher = teacher.getSubjects();

        List<String> subject_name = new ArrayList<>();
        for (Subject item : subjectOfTeacher)
            subject_name.add(item.getName());
        addSubjectChoiceBox.setItems(FXCollections.observableArrayList(subject_name));

        ObservableList<Answer> data = AnswerTable.getItems();

        //notes = new ArrayList<String>();
        NumberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        AnswerColumn.setCellValueFactory(new PropertyValueFactory<>("answer"));

        //** choose Subject in ChoiceBox **//
        addSubjectChoiceBox.setOnAction(event -> {
            if (addSubjectChoiceBox.getSelectionModel().isEmpty() || addSubjectChoiceBox.getSelectionModel() == null) {
            }
            else {
                Courses.setDisable(false);
                Courses.getSourceItems().clear();
                List<Course> SelectedCourse = new ArrayList<>();
                for (Course item : courseOfTeacher) {
                    if (item.getSubject().getName().equals(addSubjectChoiceBox.getValue())) {
                        SelectedCourse.add(item);
                    }
                }
                Courses.getSourceItems().addAll(SelectedCourse);
                Elements(false);
                String[] str = {"1", "2", "3", "4"};
                Elements(false);
                List<Answer> a = new ArrayList<>();
                Answer ans1 = new Answer("");
                a.add(ans1);
                Answer ans2 = new Answer("");
                a.add(ans2);
                Answer ans3 = new Answer("");
                a.add(ans3);
                Answer ans4 = new Answer("");
                a.add(ans4);
                for (int i = 0; i < 4; i++) {
                    a.get(i).setNumber(String.valueOf(i + 1));
                    data.add(a.get(i));
                    CorrectAnswerCB.setItems(FXCollections.observableArrayList(str));
                }
            }
        });

        //** make the table editable function **//
        AnswerTable.setEditable(true);
        NumberColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        NumberColumn.setOnEditCommit(event -> {
            Answer item = event.getRowValue();
            item.setNumber(event.getNewValue());
        });
        AnswerColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        AnswerColumn.setOnEditCommit(event -> {
            Answer item = event.getRowValue();
            item.setAnswer(event.getNewValue());
        });

        try {
            Parent previewParent = SimpleChatClient.loadFXML("PreviewQuestion");
            previewWindow.getChildren().clear();
            previewWindow.getChildren().add(previewParent);

        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
/* check Question fileds and if everthing is llegal save the question in database and exit from the controller*/
    @FXML
    void addQuestion(ActionEvent event) throws IOException {

        Question question = CollectData();

        /* check if the input is valid */
        String errorMSG = "";
        if (question.getCourses().isEmpty())
            errorMSG = "Error: Please choose course";
        else if (question.getQuestionData() == "")
            errorMSG = "Error: Please fiil question's information";
        else if (counter != 4)
            errorMSG = "Error: Please fiil question's answers";
        else if (question.getCorrectAnswer() == "")
            errorMSG = "Error: Please choose correct answer";
        if (errorMSG != "") {
            JOptionPane.showMessageDialog(null, errorMSG, "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Message message = new Message(1, "Save Question");
        message.setData(question);
        SimpleClient.getClient().sendToServer(message);
        if (state.equals(ContextualState.EDIT))
            SaveQuestionButtonPressed(question);

    }

    private Question CollectData() {
        counter = 0;
        /*get the data of question from TextFile*/
        String data = QuestionDataTF.getText();

        /*get the number of correct answer from ChoiceBox*/
        String correctAnswer = CorrectAnswerCB.getValue();

        /* get list of answers of question from table */
        String correct = "";
        List<String> answers = new ArrayList<>();
        List<Answer> dataList = AnswerTable.getItems();
        int i = 1;
        for (Answer answer : dataList) {
            if (String.valueOf(i ).equals(correctAnswer)) {
                correct = answer.getAnswer();
            }
            answers.add(answer.getAnswer());
            if (!answer.getAnswer().equals(""))
                counter++;
            i++;
        }


        /* get the subject from the ChoiceBox */
        String selectedSubject = addSubjectChoiceBox.getValue();
        Subject subject = null;
        for (Subject item : subjectOfTeacher) {
            if (item.getName().equals(selectedSubject)) {
                subject = item;
            }
        }

        /* get the list of courses from the element */
        ObservableList<Course> observableCourses = Courses.getTargetItems();
        List<Course> SelectedCourses = new ArrayList<>();
        for (Course item : observableCourses) {
            for (Course item1 : courseOfTeacher)
                if (item.getName().equals(item1.getName()))
                    SelectedCourses.add(item1);
        }

        /* get the notes */
        String studentNote = StudentNote.getText();
        String teacherNote = TeacherNote.getText();

        Question question = new Question(SelectedCourses, data, answers, correct, teacherNote, studentNote);
        question.setSubject(subject);

        System.out.println("subject is: "+subject);
        return question;
    }

    private void SaveQuestionButtonPressed(Question question) {
        try {
            SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("TeacherViewQuestions");
            FinishEditExistingQuestionEvent editQuestionEvent = new FinishEditExistingQuestionEvent(question, courseOfEditQuestion);
            EventBus.getDefault().post(editQuestionEvent);
            //EventBus.getDefault().unregister(this);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void seePreview(ActionEvent event) throws IOException {
        Question question = CollectData();

        ChangePreviewEvent event1 = new ChangePreviewEvent();
        event1.setQuestion(question);
        EventBus.getDefault().post(event1);
    }

    private enum ContextualState {
        ADD, EDIT
    }

    //////////////////////////// ***save before exit ***/////////////////////////////////////////////////////
    @Override
    public boolean CheckForUnsavedData() {
        if (!Courses.getTargetItems().isEmpty() && !CorrectAnswerCB.getValue().isEmpty() && !QuestionDataTF.getText().isEmpty() ) {
            List<Answer> dataList = AnswerTable.getItems();
            if(dataList.size()==4){
                System.out.println("Found unsaved data");
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    @Override
    public void SaveData() {
        ActionEvent event = new ActionEvent();
        try {
            addQuestion(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //////////////////////////// *** Answer class ***/////////////////////////////////////////////////////
/* Answer Class the enable to insert objects type Answer objects to table */
    public static class Answer {
        private String answer;
        private String number;
        public Answer(String answer){this.answer=answer;}
        public Answer(String number,String answer)
        {
            this.number=number;
            this.answer=answer;
        }
        public Answer(){}
        public String getNumber(){return number;}
        public void setNumber(String number) {
            this.number = number;
        }
        public String getAnswer(){return answer;}
        public void setAnswer(String answer){this.answer=answer;}

        public void setStyle(String s) {
        }
    }
}