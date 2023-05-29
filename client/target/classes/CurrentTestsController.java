import Entities.Exam;
import Events.CurrentExamsEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import java.util.List;

public class CurrentTestsController {
    @FXML
    private TableColumn<Exam, String> CourseColumn;
    @FXML
    private TableColumn<Exam, String> DateColumn;
    @FXML
    private TableColumn<Exam, String> SubjectColumn;
    @FXML
    private TableColumn<Exam, String> TimeColumn;
    @FXML
    private TableView<Exam> currentTestsQuestions;
    private List<Exam> exams;
    @FXML
    private Button ExtraTimeBT;
    @Subscribe
    public void updateData(CurrentExamsEvent event)
    {
        exams=event.getExams();
    }

    //** This Controller shows the list of Run exam **//
    @FXML
    void initialize(){
        EventBus.getDefault().register(this);
        SubjectColumn.setCellValueFactory(new PropertyValueFactory<>("Subject"));
        CourseColumn.setCellValueFactory(new PropertyValueFactory<>("Course"));
        DateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        TimeColumn.setCellValueFactory(new PropertyValueFactory<>("Time"));
    }

    @FXML
    void RequestExtraTime(ActionEvent event) {
       Exam exam= currentTestsQuestions.getSelectionModel().getSelectedItem();
       //** put the code of Edan to "switch" to ExtraTimeRequestController and sending exam **//
    }

}
