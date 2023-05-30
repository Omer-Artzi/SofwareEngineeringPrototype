import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import Entities.*;
import Events.SubjectMessageEvent;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ViewQuestionsController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableColumn<Question, SimpleStringProperty> IdColumn;

    @FXML
    private Button backButton;

    @FXML
    private ChoiceBox<Course> coursePicker;

    @FXML
    private SubScene previewScene;

    @FXML
    private TableColumn<Question, SimpleStringProperty> questionTextColumn;

    @FXML
    private TableView<Question> questionsTable;

    @FXML
    private ChoiceBox<Subject> subjectPicker;

    @FXML
    private Pane previewWindow;

    List<Subject> subjectList;

    private Teacher teacher;

    @FXML
    void switchToPrimary(ActionEvent event) {

    }

    @FXML
    void initialize() {
        // subscribe to server
        EventBus.getDefault().register(this);

        // get logged in teacher
        teacher = (Teacher) SimpleClient.getUser();

        // set up table columns
        IdColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        questionTextColumn.setCellValueFactory(new PropertyValueFactory<>("questionData"));

        // populate subject picker
        subjectPicker.getItems().addAll(teacher.getSubjectList());

       /* try{
            RequestSubjectsAndCourses();
        } catch (IOException e) {
            System.out.println("Error retrieving subjects and courses");
        }*/

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
        coursePicker.setOnAction(e -> UpdateQuestions());
    }

    private void RequestQuestions() throws IOException {
        System.out.println("Requesting questions");
        Message request = new Message(1, "Get Questions for course");
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

    private void UpdatePreview() {
        Question selectedQuestion = questionsTable.getSelectionModel().getSelectedItem();
        previewWindow.getChildren().clear();
        //previewWindow.getChildren().add(selectedQuestion.getPreview());
    }

}
