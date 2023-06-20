package Client.Controllers.MainViews.StaffViews.TeacherViews;

import Client.Controllers.MainViews.SaveBeforeExit;
import Client.Controllers.SubViews.PreviewQuestionController;
import Client.Events.*;
import Client.SimpleChatClient;
import Client.SimpleClient;
import Entities.Communication.Message;
import Entities.SchoolOwned.Course;
import Entities.SchoolOwned.Question;
import Entities.SchoolOwned.Subject;
import Entities.Users.Person;
import Entities.Users.Principal;
import Entities.Users.Teacher;
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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

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
    private TableColumn<Question, SimpleStringProperty> IdColumn;

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

    private TeacherAddTestFormController.SaveState storedAddTestFormState;



    @FXML
    void initialize() {
        System.out.println("Initializing Client.Controllers.MainPanelScreens.TeacherViewQuestionsController");

        HandleViewState();

        // subscribe to server
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        // get logged-in User (Teacher or Principal)
        user = SimpleClient.getUser();

        // set up table columns
        IdColumn.setCellValueFactory(new PropertyValueFactory<>("questionID"));
        questionTextColumn.setCellValueFactory(new PropertyValueFactory<>("questionData"));

        IdColumn.setStyle("-fx-alignment: CENTER;");

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
        ContextualButton.setVisible(!(SimpleClient.getUser() instanceof Principal));
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
        Collections.sort(coursePicker.getItems());
    }

    /*private void UpdateQuestions() {
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
    }*/

    private void CheckboxPressed(Question question) {
        if (chosenQuestions.contains(question)) {
            System.out.println("Removing question from chosen questions");
            chosenQuestions.remove(question);
        } else {
            System.out.println("Adding question to chosen questions");
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
            SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("TeacherAddTestForm");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Sending chosen questions to TeacherAddTestForm, subject: " + subjectPicker.getSelectionModel().getSelectedItem().getName() + ", course: " + coursePicker.getSelectionModel().getSelectedItem().getName());
        SendChosenQuestionsEvent chooseQuestionsEvent = new SendChosenQuestionsEvent(chosenQuestions, subjectPicker.getSelectionModel().getSelectedItem(), coursePicker.getSelectionModel().getSelectedItem());
        chooseQuestionsEvent.setSaveState(storedAddTestFormState);
        System.out.println("Sending event: " + chooseQuestionsEvent);
        EventBus.getDefault().post(chooseQuestionsEvent);
        EventBus.getDefault().unregister(this);
    }

    private void SetPickersCourse(Course course){
        System.out.println(course.getSubject().toString());
        subjectPicker.getSelectionModel().select(course.getSubject());
        coursePicker.getSelectionModel().select(course);
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

    /**
     * Sends a request to the server for the subjects of the logged in teacher
      */
    private void RequestSubjectsOfTeacher() throws IOException {
        System.out.println("TeacherViewQuestions requesting subjects");
        Message message = new Message(1, "1Get Subjects of Teacher: " + SimpleClient.getClient().getUser().getID());
        SimpleClient.getClient().sendToServer(message);
    }

    /**
     * Sends a request to the server for all subjects
     */
    void RequestAllSubjects() throws IOException {
        System.out.println("Requesting all subjects");
        Message request = new Message(1, "Get Subjects");
        SimpleClient.getClient().sendToServer(request);
    }

    /**
     * Sends a request to the server for all questions of a course
     */
    private void RequestQuestions() throws IOException {
        System.out.println("Requesting questions");
        Message request = new Message(1, "Get Questions for Course: " + coursePicker.getValue().toString());
        request.setData(coursePicker.getValue());
        SimpleClient.getClient().sendToServer(request);
    }


// responses to messages from the server


    /**
     * Receives a list of subjects from the server and populates the subject picker
      */
    @Subscribe
    public void updateSubjects(SubjectsOfTeacherMessageEvent event) throws IOException {

        /*subjectPicker.getItems().addAll(event.getSubjects());
        Collections.sort(subjectPicker.getItems());*/

        System.out.println("Populating dropdown menus");
        subjectList = event.getSubjects();
        subjectPicker.getItems().clear();
        subjectPicker.getItems().addAll(subjectList);
        Collections.sort(subjectPicker.getItems());
        subjectPicker.setOnAction(e -> UpdateCourses());
    }

    /**
     * Receives a list of subjects from the server and populates the subject picker
     */
    @Subscribe
    public void PopulateDropdownMenus(SubjectMessageEvent event) {
        System.out.println("Populating dropdown menus");
        subjectList = event.getSubjects();
        subjectPicker.getItems().clear();
        subjectPicker.getItems().addAll(subjectList);
        Collections.sort(subjectPicker.getItems());
        subjectPicker.setOnAction(e -> UpdateCourses());
    }

    /**
     * Triggered when course is selected, loads the course's questions into the table
     */
    @Subscribe
    public void PopulateQuestions(CourseQuestionsListEvent event) {
        System.out.println("Populating questions");
        questionsTable.getItems().clear();
        questionsTable.getItems().addAll(event.getQuestions());

    }

    /**
     * Receives a question from the table
     * @param selectedQuestion
     */
    @Subscribe
    private void UpdatePreview(Question selectedQuestion) {
        this.selectedQuestion = selectedQuestion;
        ContextualButton.setDisable(false);
        System.out.println("Updating preview");
        ChangePreviewEvent event = new ChangePreviewEvent();
        event.setQuestion(selectedQuestion);
        EventBus.getDefault().post(event);
    }

    /**
     * handles adjusting the screen to perform as the question chooser for createExam
     * adds a checkbox column to the table
     * @param event
     */
    @Subscribe
    public void ChooseQuestions(ChooseQuestionsEvent event) {
        System.out.println("Choosing questions");

        storedAddTestFormState = event.getSaveState();
        System.out.println("Stored state: " + storedAddTestFormState);

        // change state
        state = ContextualState.CHOOSE;

        // set subject and course
        DisablePickers();
        SetPickersCourse(event.getCourse());


        // add a checkbox column to the table
        TableColumn<Question, Boolean> checkBoxColumn = new TableColumn<>("Choose");

        List<Question> previousChoices = event.getQuestions();
        System.out.println("Previous choices: " + previousChoices);
        chosenQuestions.clear();
        chosenQuestions.addAll(previousChoices);

        checkBoxColumn.setCellValueFactory(
                cell -> {
                    Question question = cell.getValue();
                    CheckBox checkBox = new CheckBox();
                    if(chosenQuestions.contains(question)) {
                        System.out.println("Contains question: " + question);
                        checkBox.selectedProperty().setValue(true);
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
        checkBoxColumn.setStyle("-fx-alignment: CENTER;");
        checkBoxColumn.setMaxWidth(50);

        HandleChooseState();
    }

    /**
     * handles adjusting returning from editing a question
     * @param event triggered when returning from editing a question
     */
    @Subscribe
    public void ReturnFromEditQuestion(FinishEditExistingQuestionEvent event){
        System.out.println("Returning from edit question");

        SetPickersCourse(event.getCourse());
    }

}
