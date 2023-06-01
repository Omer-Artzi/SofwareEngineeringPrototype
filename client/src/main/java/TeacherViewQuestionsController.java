import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import Entities.*;
import Events.*;
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

public class TeacherViewQuestionsController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addQuestionsToExamButton;

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

    private Teacher teacher;

    List<Question> chosenQuestions;

    private PreviewQuestionController previewController;

    @FXML
    void switchToPrimary(ActionEvent event) {

    }

    @FXML
    void initialize() {
        System.out.println("Initializing TeacherViewQuestionsController");

        // subscribe to server
        EventBus.getDefault().register(this);

        // get logged in teacher
        teacher = (Teacher) SimpleClient.getUser();

        // set up table columns
        IdColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        questionTextColumn.setCellValueFactory(new PropertyValueFactory<>("questionData"));

        // create a listener for the table
        questionsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                UpdatePreview(newSelection);
            }
        });

        // populate subject picker
        try {
            RequestSubjects();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //subjectPicker.getItems().addAll(teacher.getSubjectList());

        //create a listener for the subject picker
        subjectPicker.setOnAction(e -> UpdateCourses());

        // create a listener for the course picker
        coursePicker.setOnAction(e -> {
            try {
                RequestQuestions();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // create preview scene
        try {
            Parent previewParent = SimpleChatClient.loadFXML("PreviewQuestion");
            previewWindow.getChildren().clear();
            previewWindow.getChildren().add(previewParent);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


       /* try{
            RequestSubjectsAndCourses();
        } catch (IOException e) {
            System.out.println("Error retrieving subjects and courses");
        }*/

    }

    // sends a server requests for the Subjects of the logged in teacher
    private void RequestSubjects() throws IOException {
        System.out.println("TeacherViewQuestions requesting subjects");
        Message message = new Message(1, "1Get Subjects of Teacher: " + SimpleClient.getClient().getUser().getID());
        SimpleClient.getClient().sendToServer(message);
    }

    // receives a list of subjects from the server
    @Subscribe
    public void updateSubjects(SubjectsOfTeacherMessageEvent event) throws IOException {

        subjectPicker.getItems().addAll(event.getSubjects());
    }

    void RequestSubjectsAndCourses() throws IOException {
        System.out.println("Requesting subjects and courses");
        Message request = new Message(1, "Get Subjects");
        SimpleClient.getClient().sendToServer(request);
    }

    @Subscribe
    public void PopulateDropdownMenus(SubjectMessageEvent event) {
        System.out.println("Populating dropdown menus");
        subjectList = event.getSubjects();
        subjectPicker.getItems().clear();
        subjectPicker.getItems().addAll(subjectList);
        subjectPicker.setOnAction(e -> UpdateCourses());
    }

    private void PopulateSubjects() {
        subjectPicker.getItems().clear();
        subjectPicker.getItems().addAll(teacher.getSubjectList());
        subjectPicker.setOnAction(e -> UpdateCourses());
    }

    private void UpdateCourses() {
        Subject selectedSubject = subjectPicker.getValue();
        coursePicker.getItems().clear();
        // check for null selectedSubject
        if (selectedSubject == null) {
            return;
        }
        coursePicker.getItems().addAll(selectedSubject.getCourses());
        coursePicker.setOnAction(e -> {
            try {
                RequestQuestions();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void RequestQuestions() throws IOException {
        System.out.println("Requesting questions");
        Message request = new Message(1, "Get Questions for Course: " + coursePicker.getValue().toString());
        request.setData(coursePicker.getValue());
        SimpleClient.getClient().sendToServer(request);
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

    @Subscribe
    public void PopulateQuestions(CourseQuestionsListEvent event) {
        System.out.println("Populating questions");
        questionsTable.getItems().clear();
        questionsTable.getItems().addAll(event.getQuestions());

    }

    @Subscribe
    private void UpdatePreview(Question selectedQuestion) {
        System.out.println("Updating preview");
        ChangePreviewEvent event = new ChangePreviewEvent();
        event.setQuestion(selectedQuestion);
        EventBus.getDefault().post(event);
    }

    @Subscribe
    public void ChooseQuestions(ChooseQuestionsEvent event) {
        System.out.println("Choosing questions");

        // set subject and course
        System.out.println(event.getCourse().getSubject().toString());
        subjectPicker.getItems().clear();
        subjectPicker.getItems().add(event.getCourse().getSubject());
        subjectPicker.setValue(event.getCourse().getSubject());
        subjectPicker.disableProperty().setValue(true);
        coursePicker.setValue(event.getCourse());
        coursePicker.disableProperty().setValue(true);

        // add a checkbox column to the table
        TableColumn<Question, Boolean> checkBoxColumn = new TableColumn<>("Choose");
        //checkBoxColumn.setCellValueFactory(new PropertyValueFactory<>("chosen"));

        checkBoxColumn.setCellValueFactory(
                cell -> {
                    Question question = cell.getValue();
                    CheckBox checkBox = new CheckBox();
                    checkBox.selectedProperty().setValue(false);
                    checkBox
                            .selectedProperty()
                            .addListener((ov, old_val, new_val) -> CheckboxPressed(question));
                    return new SimpleObjectProperty(checkBox);
                });

        questionsTable.getColumns().add(checkBoxColumn);

        List<Question> previousChoices = event.getQuestions();
        if (chosenQuestions != null) {
            for (Question question : previousChoices) {
                // find the question in the table
                for (Question tableQuestion : questionsTable.getItems()) {
                    if (tableQuestion.getID() == question.getID()) {
                        // check the checkbox
                        CheckBox checkBox = (CheckBox) checkBoxColumn.getCellObservableValue(tableQuestion);
                        checkBox.setSelected(true);
                        chosenQuestions.add(tableQuestion);
                    }
                }
            }
        } else {
            chosenQuestions = new ArrayList<>();
        }

        addQuestionsToExamButton.setVisible(true);
    }

    void CheckboxPressed(Question question) {
        if (chosenQuestions.contains(question)) {
            chosenQuestions.remove(question);
        } else {
            chosenQuestions.add(question);
        }
    }

    @FXML
    void addQuestionsToExamButtonPressed(ActionEvent event) {
        System.out.println("Back button pressed");
        System.out.println("Chosen questions: " + chosenQuestions);

        try {
            SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("AddExam");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SendChosenQuestionsEvent chooseQuestionsEvent = new SendChosenQuestionsEvent(chosenQuestions);
        EventBus.getDefault().post(chooseQuestionsEvent);
    }

}
