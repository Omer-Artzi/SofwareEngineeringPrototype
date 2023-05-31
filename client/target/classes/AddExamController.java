import Entities.*;
import Events.*;
import antlr.ASTFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;


import javax.swing.*;
import java.io.IOException;
import java.util.*;

public class AddExamController {
    private int examID;
    private Teacher teacher;
    private Subject chosenSubject;
    private Course chosenCourse;
    private String headerText;
    private String footerText;
    private String examNotesForStudent;
    private String examNotesForTeacher;
    private String examName; // necessary?
    private List<QuestionObject> questionObjectsList;

    private List<Subject> teacherSubjects;
    @FXML
    private int msgId;
    @FXML
    private ComboBox<Course> CourseCB;

    @FXML
    private ComboBox<Subject> SubjectCB;

    @FXML
    private Button addNotesForStudentButton;

    @FXML
    private Button addNotesForTeacherButton;

    @FXML
    private Button addQuestionButton;

    @FXML
    private Button previewTestButton;

    @FXML
    private Button saveTestButton;

    @FXML
    private Button resetButton;

    @FXML
    private TextArea footerTextTF;

    @FXML
    private TextArea headerTextTF;

    @FXML
    private TableView<QuestionObject> questionTable;

    @FXML
    private TableColumn<QuestionObject, Integer> gradePercentageColumn;

    @FXML
    private TableColumn<QuestionObject, Integer> questionIdColumn;

    @FXML
    private TableColumn<QuestionObject, String> questionTextColumn;

    @FXML
    void addNotesForStudent(ActionEvent event) {
        ASTFactory PopupBuilder = null;


    }

    @FXML
    void addNotesForTeacher(ActionEvent event) {

    }

    @FXML
    void addQuestion(ActionEvent event) throws IOException {
        // switch to TeacherViewQuestions
        SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("TeacherViewQuestions");

        // send info to TeacherViewQuestions
        ChooseQuestionsEvent stMsg = new ChooseQuestionsEvent();
        stMsg.setCourse(CourseCB.getValue());
        System.out.println("Course: " + CourseCB.getValue());
        EventBus.getDefault().post(stMsg);
        // מוקדש לעידן באהבה
    }

    @Subscribe
    public void setQuestions(SendChosenQuestionsEvent event) {
        System.out.println("received questions from TeacherViewQuestions: " + event.getQuestions());


        List<Question> addedQuestions = event.getQuestions();
        for (Question q : addedQuestions) {
            QuestionObject newQuestion = new QuestionObject(q.getID(), q.getQuestionData(), 0);
            questionObjectsList.add(newQuestion);
        }
        questionTable.getItems().clear();
        questionTable.getItems().addAll(questionObjectsList);
        questionTable.refresh();
    }

    @FXML
    void previewTest(ActionEvent event) {

    }

    @FXML
    void saveTest(ActionEvent event) {
        headerText= headerTextTF.getText();
        footerText= footerTextTF.getText();
        examNotesForStudent= addNotesForStudentButton.getText();

    }

    @FXML
    void resetForm(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Please Confirm");
        alert.setHeaderText("Are you sure you want to reset the form?");
        alert.setContentText("All the data you have entered will be lost");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            resetButton.setVisible(false);
            SubjectCB.getSelectionModel().clearSelection();
            CourseCB.getSelectionModel().clearSelection();
            headerTextTF.clear();
            footerTextTF.clear();
            questionTable.getItems().clear();
            questionTable.refresh();
            disable();
            Message message = new Message(1, "1Get Subjects of Teacher: " + SimpleClient.getClient().getUser().getID());
            SimpleClient.getClient().sendToServer(message);
        }
        else return;
    }

    @Subscribe
    public void updateSubjects(SubjectsOfTeacherMessageEvent eventSUB) throws IOException {
        resetButton.setVisible(false);
        teacher = (Teacher)SimpleClient.getClient().getUser();
        teacherSubjects=eventSUB.getSubjects();
        if (teacherSubjects != null){
            Collections.sort(teacherSubjects);
            SubjectCB.getItems().addAll(teacherSubjects);
        }
        else {
            JOptionPane.showMessageDialog(null, "Could not Retrieve any subjects", "DataBase Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    @FXML
    void initialize() throws IOException {
        EventBus.getDefault().register(this);
        disable();
        questionIdColumn.setCellValueFactory(new PropertyValueFactory<>("questionId"));
        questionTextColumn.setCellValueFactory(new PropertyValueFactory<>("question"));
        gradePercentageColumn.setCellValueFactory(new PropertyValueFactory<>("percentage"));
        questionTable.setEditable(false);
        questionTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        questionObjectsList = new ArrayList<>();
        Message message = new Message(1, "1Get Subjects of Teacher: " + SimpleClient.getClient().getUser().getID());
        SimpleClient.getClient().sendToServer(message);
    }


    @FXML // activated when user selects a Subject
    public void onSubjectSelection(ActionEvent event){
        if (SubjectCB.getSelectionModel().getSelectedItem() == null){
            return;
        }
        else{
            List<Course> courses = SubjectCB.getSelectionModel().getSelectedItem().getCourses();
            if (courses != null){
                SubjectCB.setDisable(true); // TODO : add an option for teacher to change subject (maybe disable all other buttons until he chose subject and course)
                Collections.sort(courses);
                CourseCB.getItems().addAll(courses);
                CourseCB.setDisable(false);
                resetButton.setVisible(true);
            }
            else {
                JOptionPane.showMessageDialog(null, "Could not Retrieve any courses", "DataBase Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    @FXML // activated when user selects a Course
    public void onCourseSelection(ActionEvent event) throws IOException {
        if (CourseCB.getSelectionModel().getSelectedItem() == null){
            return;
        }
        else{
            CourseCB.setDisable(true);
            Message message= new Message(++msgId, "Get Questions for Course: " + CourseCB.getSelectionModel().getSelectedItem());
            message.setData(CourseCB.getSelectionModel().getSelectedItem());
            SimpleClient.getClient().sendToServer(message);
        }
    }


    @Subscribe
    public void updateScreen(CourseQuestionsListEvent event) {
        CourseCB.setDisable(true);
        questionTable.setDisable(false);
        List<QuestionObject> questionObjectsList = new ArrayList<>();
        /*for (Question question : event.getQuestions()) {
            //System.out.println(question.getQuestionData());
            QuestionObject item = new QuestionObject(question.getID(), question.getQuestionData(), 0);
            questionObjectsList.add(item);
        }
        if (questionObjectsList != null) {
            questionTable.getItems().clear();
            questionTable.getItems().addAll(questionObjectsList);
            questionTable.refresh();
        }*/
        enable();
    }


    void disable(){
        CourseCB.setDisable(true);
        headerTextTF.setDisable(true);
        footerTextTF.setDisable(true);
        //questionTable.setDisable(true);
        addQuestionButton.setDisable(true);
        addNotesForStudentButton.setDisable(true);
        addNotesForTeacherButton.setDisable(true);
        previewTestButton.setDisable(true);
        saveTestButton.setDisable(true);
    }

    void enable(){
        headerTextTF.setDisable(false);
        footerTextTF.setDisable(false);
        //questionTable.setDisable(false);
        addQuestionButton.setDisable(false);
        addNotesForStudentButton.setDisable(false);
        addNotesForTeacherButton.setDisable(false);
        previewTestButton.setDisable(false);
        saveTestButton.setDisable(false);
    }

}
