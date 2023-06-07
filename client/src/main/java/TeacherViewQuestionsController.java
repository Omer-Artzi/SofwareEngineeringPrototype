import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import Entities.Course;
import Entities.Message;
import Entities.Person;
import Entities.Question;
import Entities.Subject;
import Entities.Teacher;
import Events.*;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;

public class TeacherViewQuestionsController extends SaveBeforeExit {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button ContextualButton;

    @FXML
    private ChoiceBox<Course> coursePicker;

    @FXML
    private SubScene previewScene;

    @FXML
    private TableView<Question> questionsTable;

    @FXML
    private TableColumn<Question, IntegerProperty> IdColumn;

    @FXML
    private TableColumn<Question, SimpleStringProperty> questionTextColumn;

    @FXML
    private ChoiceBox<Subject> subjectPicker;

    @FXML
    private Pane previewWindow;

    List<Subject> subjectList;

    //private Teacher teacher;

    private Person user;

    List<Question> chosenQuestions = new ArrayList<>();

    private PreviewQuestionController previewController;

    private ContextualState state = ContextualState.VIEW;

    private enum ContextualState {
        VIEW, CHOOSE
    }

    private Question selectedQuestion;

    @FXML
    void initialize() {
        System.out.println("Initializing TeacherViewQuestionsController");

        HandleViewState();

        // subscribe to server
        EventBus.getDefault().register(this);

        // get logged-in User (Teacher or Principal)
        user = SimpleClient.getUser();

        // set up table columns
        IdColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        questionTextColumn.setCellValueFactory(new PropertyValueFactory<>("questionData"));

        PopulateSubjects();

        CreateListeners();

        CreatePreviewScene();

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

    private void HandleViewState(){
        state = ContextualState.VIEW;
        ContextualButton.setText("Edit Question");
        ContextualButton.setDisable(true);
        ContextualButton.setVisible(true);
    }

    private void HandleChooseState(){
        state = ContextualState.CHOOSE;
        ContextualButton.textProperty().setValue("Add Questions to Exam");
        ContextualButton.setVisible(true);
        ContextualButton.setDisable(false);
    }

    private void PopulateSubjects() {
        subjectPicker.getItems().clear();
        try {
            if(user instanceof Teacher){
                RequestSubjectsOfTeacher();
            } else {
                RequestAllSubjects();
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void CreateListeners() {

        //create a listener for the subject picker
        subjectPicker.setOnAction(e -> UpdateCourses());

        // create a listener for the course picker
        coursePicker.setOnAction(e -> {
            try {
                if(coursePicker.getValue() != null)
                    RequestQuestions();
            }
            catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // create a listener for the table
        questionsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                UpdatePreview(newSelection);
            }
        });
    }

    private void UpdateCourses() {
        Subject selectedSubject = subjectPicker.getValue();
        coursePicker.getItems().clear();
        // check for null selectedSubject
        if (selectedSubject == null) {
            return;
        }
        coursePicker.getItems().addAll(selectedSubject.getCourses());
    }

    private void UpdateQuestions() {
        System.out.println("Updating questions");
        Course selectedCourse = coursePicker.getValue();
        questionsTable.getItems().clear();

        // check for null selectedCourse
        if (selectedCourse == null) {
            return;
        }

        //questionsTable.setItems((ObservableList<Question>) selectedCourse.getQuestions());

        questionsTable.getItems().addAll(selectedCourse.getQuestions());
        //questionsTable.setOnMouseClicked(e -> UpdatePreview());
    }

    private void CheckboxPressed(Question question) {
        if (chosenQuestions.contains(question)) {
            chosenQuestions.remove(question);
        } else {
            chosenQuestions.add(question);
        }
    }

    private void EditQuestionButtonPressed(){

        try {
            SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("TeacherAddQuestion");
            StartEditExistingQuestionEvent editQuestionEvent = new StartEditExistingQuestionEvent(selectedQuestion, coursePicker.getValue());
            EventBus.getDefault().post(editQuestionEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void AddQuestionsButtonPressed(){
        System.out.println("Chosen questions: " + chosenQuestions);

        try {
            SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("AddExam");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SendChosenQuestionsEvent chooseQuestionsEvent = new SendChosenQuestionsEvent(chosenQuestions, subjectPicker.getValue(), coursePicker.getValue());
        EventBus.getDefault().post(chooseQuestionsEvent);
    }

    private void SetPickersCourse(Course course){
        System.out.println(course.getSubject().toString());
        subjectPicker.getItems().clear();
        subjectPicker.getItems().add(course.getSubject());
        subjectPicker.setValue(course.getSubject());
        coursePicker.setValue(course);
    }

    private void DisablePickers() {
        subjectPicker.disableProperty().setValue(true);
        coursePicker.disableProperty().setValue(true);
    }

// javafx action events

    @FXML
    private void ContextualButtonPressed(ActionEvent event) {
        System.out.println("Contextual button pressed");
        switch (state) {
            case VIEW:
                EditQuestionButtonPressed();
                break;
            case CHOOSE:
                AddQuestionsButtonPressed();
                break;
        }
    }

// send requests to the server

    // sends a server requests for the Subjects of the logged in teacher
    private void RequestSubjectsOfTeacher() throws IOException {
        System.out.println("TeacherViewQuestions requesting subjects");
        Message message = new Message(1, "1Get Subjects of Teacher: " + SimpleClient.getClient().getUser().getID());
        SimpleClient.getClient().sendToServer(message);
    }

    void RequestAllSubjects() throws IOException {
        System.out.println("Requesting all subjects");
        Message request = new Message(1, "Get Subjects");
        SimpleClient.getClient().sendToServer(request);
    }

    private void RequestQuestions() throws IOException {
        System.out.println("Requesting questions");
        Message request = new Message(1, "Get Questions for Course: " + coursePicker.getValue().toString());
        request.setData(coursePicker.getValue());
        SimpleClient.getClient().sendToServer(request);
    }


// responses to messages from the server


    // receives a list of subjects from the server
    @Subscribe
    public void updateSubjects(SubjectsOfTeacherMessageEvent event) throws IOException {

        subjectPicker.getItems().addAll(event.getSubjects());
    }

    @Subscribe
    public void PopulateDropdownMenus(SubjectMessageEvent event) {
        System.out.println("Populating dropdown menus");
        subjectList = event.getSubjects();
        subjectPicker.getItems().clear();
        subjectPicker.getItems().addAll(subjectList);
        subjectPicker.setOnAction(e -> UpdateCourses());
    }

    @Subscribe
    public void PopulateQuestions(CourseQuestionsListEvent event) {
        System.out.println("Populating questions");
        questionsTable.getItems().clear();
        questionsTable.getItems().addAll(event.getQuestions());

    }

    @Subscribe
    private void UpdatePreview(Question selectedQuestion) {
        this.selectedQuestion = selectedQuestion;
        ContextualButton.setDisable(false);
        System.out.println("Updating preview");
        ChangePreviewEvent event = new ChangePreviewEvent();
        event.setQuestion(selectedQuestion);
        EventBus.getDefault().post(event);
    }

    @Subscribe
    public void ChooseQuestions(ChooseQuestionsEvent event) {
        System.out.println("Choosing questions");

        // change state
        state = ContextualState.CHOOSE;

        // set subject and course
        DisablePickers();
        SetPickersCourse(event.getCourse());


        // add a checkbox column to the table
        TableColumn<Question, Boolean> checkBoxColumn = new TableColumn<>("Choose");
        //checkBoxColumn.setCellValueFactory(new PropertyValueFactory<>("chosen"));

        List<Question> previousChoices = event.getQuestions();
        System.out.println("Previous choices: " + previousChoices);

        checkBoxColumn.setCellValueFactory(
                cell -> {
                    Question question = cell.getValue();
                    CheckBox checkBox = new CheckBox();
                    if(previousChoices.contains(question)) {
                        System.out.println("Contains question: " + question);
                        checkBox.selectedProperty().setValue(true);
                        chosenQuestions.add(question);
                    }
                    else
                        checkBox.selectedProperty().setValue(false);
                    //checkBox.selectedProperty().setValue(false);
                    checkBox
                            .selectedProperty()
                            .addListener((ov, old_val, new_val) -> CheckboxPressed(question));
                    return new SimpleObjectProperty(checkBox);
                });

        questionsTable.getColumns().add(checkBoxColumn);

        HandleChooseState();
    }

    @Subscribe
    public void ReturnFromEditQuestion(FinishEditExistingQuestionEvent event){
        System.out.println("Returning from edit question");

        SetPickersCourse(event.getCourse());
    }

//  SaveBeforeExit methods

    /*@Override
    public boolean CheckForUnsavedData() {
        System.out.println("Checking for unsaved data in ViewQuestions");
        return true;
    }*/

}
