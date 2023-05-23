import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import Entities.Course;
import Entities.Question;
import Entities.Subject;
import Events.SubjectMessageEvent;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

    @FXML
    void switchToPrimary(ActionEvent event) {

    }

    @FXML
    void initialize() {
        // subscribe to server
        EventBus.getDefault().register(this);

        // TODO: Add subjects and courses to choice boxes
        try{
            RequestSubjectsAndCourses();
        } catch (IOException e) {
            System.out.println("Error retrieving subjects and courses");
        }

        // get subjects and courses from server


        //TODO: Add questions to table

    }

    @FXML
    void RequestSubjectsAndCourses() throws IOException {
        SimpleClient.getClient().sendToServer("RetrieveSubjects");
    }

    @Subscribe
    void PopulateDropdownMenus(SubjectMessageEvent event) {
        subjectList = event.getSubjects();
        subjectPicker.getItems().addAll(subjectList);
    }

}
