
import Entities.*;
import Events.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;


import javax.swing.*;
import java.io.IOException;
import java.util.*;

public class AddExamController {
    private int examID;
    private Teacher teacher;
    private Subject chosenSubject = null;
    private Course chosenCourse = null;
    private String headerText;
    private String footerText;
    private String examNotesForStudent;
    private String examNotesForTeacher;
    private String examName; // necessary?
    private List<QuestionObject> questionObjectsList;
    private List<Question> addedQuestions = new ArrayList<>();

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

    /*
    @FXML
    void rowClicked(MouseEvent event) {
        System.out.println("row clicked");
        QuestionObject q = questionTable.getSelectionModel().getSelectedItem();
        VBoxGrade.setText(String.valueOf(q.getGradePercentage()));
        VBoxQuestionID.setText(String.valueOf(q.getQuestion().getId()));
        VBoxQuestionText.setText(q.getQuestion().getQuestionText());

    }*/

    @FXML
    void addNotesForStudent(ActionEvent event) {
        // open new input dialog for notes for student
        TextInputDialog dialog = new TextInputDialog();
        dialog.getDialogPane().setMinWidth(400);
        dialog.getDialogPane().setMinHeight(200);
        dialog.setTitle("Notes for student");
        dialog.setHeaderText("Add notes for student");
        dialog.setContentText("Notes:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            examNotesForStudent = result.get();
        }
    }

    @FXML
    void addNotesForTeacher(ActionEvent event) {
        // open new input dialog for notes for student
        TextInputDialog dialog = new TextInputDialog();
        dialog.getDialogPane().setMinWidth(400);
        dialog.getDialogPane().setMinHeight(200);
        dialog.setTitle("Notes for teacher");
        dialog.setHeaderText("Add notes for teacher");
        dialog.setContentText("Notes:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            examNotesForTeacher = result.get();
        }

    }

    @FXML
    void addQuestion(ActionEvent event) throws IOException {
        // switch to TeacherViewQuestions
        SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("TeacherViewQuestions");

        // send info to TeacherViewQuestions
        ChooseQuestionsEvent stMsg = new ChooseQuestionsEvent();
        stMsg.setCourse(CourseCB.getValue());
        stMsg.setSubject(SubjectCB.getValue());
        stMsg.setQuestions(addedQuestions);
        System.out.println("Course: " + CourseCB.getValue());
        EventBus.getDefault().post(stMsg);

    }

    @Subscribe
    public void setQuestions(SendChosenQuestionsEvent event) {
        Platform.runLater(()-> {
            try{
                System.out.println("received questions from TeacherViewQuestions: " + event.getQuestions());
                chosenSubject= event.getSubject();
                chosenCourse= event.getCourse();
                //SubjectCB.getItems().clear();
                //CourseCB.getItems().clear();
                //SubjectCB.getItems().add(chosenSubject);
                //CourseCB.getItems().add(chosenCourse);
                SubjectCB.setValue(chosenSubject);
                CourseCB.setValue(chosenCourse);
                SubjectCB.setDisable(true);
                CourseCB.setDisable(true);
                resetButton.setVisible(true);
                resetButton.setDisable(false);
                enable();
                addQuestionButton.setDisable(false);



                //List<Question> addedQuestions = event.getQuestions();
                for (Question q : event.getQuestions()) { // add questions to addedQuestions list if they are not already there
                    if(!addedQuestions.contains(q))
                        addedQuestions.add(q);
                }
                questionObjectsList.clear();
                for (Question q : addedQuestions) { // convert questions to questionTable
                    QuestionObject newQuestion = new QuestionObject(q.getID(), q.getQuestionData(), 0);
                    questionObjectsList.add(newQuestion);
                }
                questionTable.getItems().clear();
                questionTable.getItems().addAll(questionObjectsList);
                questionTable.refresh();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });}

    @FXML
    void previewTest(ActionEvent event) {

    }

    @FXML
    void saveTest(ActionEvent event) throws IOException {
        int sum = 0;
        for (QuestionObject qo : questionObjectsList) {
            sum += qo.getPercentage();
        }
        if (sum!= 100)
        {
            // show error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText("The sum of the grades must be 100");
            alert.showAndWait();
            return;
        }
        teacher = ((Teacher)(SimpleClient.getClient().getUser()));
        headerText= headerTextTF.getText();
        footerText= footerTextTF.getText();
        Date createdDate = new Date();
        List<Integer> grades = new ArrayList<>();
        for (Question q : addedQuestions) {
            for (QuestionObject qo : questionObjectsList) {
                if (q.getID() == qo.getQuestionId()) {
                    grades.add(qo.getPercentage());
                    break;
                }
            }
        }
        ExamForm examForm = new ExamForm(teacher, chosenSubject, chosenCourse, addedQuestions, grades, createdDate, headerText, footerText, examNotesForTeacher, examNotesForTeacher);
        Message message = new Message(1, "Add ExamForm: " + "Subject-" + chosenSubject + ", Course-" + chosenCourse);
        message.setData(examForm);
        SimpleClient.getClient().sendToServer(message);
    }

    @Subscribe
    void examSaved(GeneralEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exam Saved");
        alert.setHeaderText("Exam Saved");
        alert.setContentText("Exam Saved");
        alert.showAndWait();
        //resetForm(null);
        EventBus.getDefault().unregister(this);
        SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("AddExam");
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
            //SubjectCB.getSelectionModel().clearSelection();
            //CourseCB.getSelectionModel().clearSelection();
            headerTextTF.clear();
            footerTextTF.clear();
            questionTable.getItems().clear();
            questionTable.refresh();
            addedQuestions.clear();
            questionObjectsList.clear();
            disable();
            SubjectCB.getItems().clear();
            CourseCB.getItems().clear();
            SubjectCB.setDisable(false);
            Message message = new Message(1, "1Get Subjects of Teacher: " + SimpleClient.getClient().getUser().getID());
            SimpleClient.getClient().sendToServer(message);
        }
        else return;
    }

    @Subscribe
    public void updateSubjects(SubjectsOfTeacherMessageEvent eventSUB) throws IOException {
        resetButton.setVisible(false);
        teacher = (Teacher) SimpleClient.getClient().getUser();
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
        questionTable.setEditable(true);
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
                chosenSubject= SubjectCB.getSelectionModel().getSelectedItem(); // save the chosen subject
                SubjectCB.setDisable(true);
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
            chosenCourse = CourseCB.getSelectionModel().getSelectedItem(); // save the chosen course
            CourseCB.setDisable(true);
            Message message= new Message(++msgId, "Get Questions for Course: " + CourseCB.getSelectionModel().getSelectedItem());
            message.setData(CourseCB.getSelectionModel().getSelectedItem());
            SimpleClient.getClient().sendToServer(message);
        }
    }

    @FXML
    void rowClicked(MouseEvent event) {
        if (questionTable.getSelectionModel().getSelectedItem() == null){
            return;
        }
        else{
            QuestionObject questionObject = questionTable.getSelectionModel().getSelectedItem();
            int index = questionTable.getSelectionModel().getSelectedIndex();
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Edit Grade Percentage");
            dialog.setHeaderText("Edit Grade Percentage");
            dialog.setContentText("Please enter the new grade percentage:");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()){
                try {
                    int newGradePercentage = Integer.parseInt(result.get());
                    if (newGradePercentage < 0 || newGradePercentage > 100){
                        JOptionPane.showMessageDialog(null, "Grade Percentage must be between 0 and 100", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    else{
                        questionObject.setPercentage(newGradePercentage);
                        questionObjectsList.set(index, questionObject);
                        questionTable.getItems().clear();
                        questionTable.getItems().addAll(questionObjectsList);
                        questionTable.refresh();
                    }
                }
                catch (NumberFormatException e){
                    JOptionPane.showMessageDialog(null, "Grade Percentage must be a number", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        }
    }


    @Subscribe
    public void updateScreen(CourseQuestionsListEvent event) {
        CourseCB.setDisable(true);
        questionTable.setDisable(false);
        addQuestionButton.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        /*List<QuestionObject> questionObjectsList = new ArrayList<>();
        for (Question question : event.getQuestions()) {
            //System.out.println(question.getQuestionData());
            QuestionObject item = new QuestionObject(question.getID(), question.getQuestionData(), 0);
            questionObjectsList.add(item);
        }
        if (questionObjectsList != null) {
            questionTable.getItems().clear();
            questionTable.getItems().addAll(questionObjectsList);
            questionTable.refresh();
        }*/
        //enable();
        addQuestionButton.setDisable(false);
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
        //addQuestionButton.setDisable(false);
        addNotesForStudentButton.setDisable(false);
        addNotesForTeacherButton.setDisable(false);
        previewTestButton.setDisable(false);
        saveTestButton.setDisable(false);
    }

}
