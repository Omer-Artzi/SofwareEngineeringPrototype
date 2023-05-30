import Entities.Exam;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class LiveExamsController {
    @FXML
    private TableView<Exam> ExamsTable;
    @FXML
    private TableColumn<?, ?> CodeColumn;

    @FXML
    private TableColumn<?, ?> CourseColumn;

    @FXML
    private TableColumn<?, ?> EndTimeColumn;

    @FXML
    private Button RequestExtraTimeBT;

    @FXML
    private TableColumn<?, ?> StartTimeColumn;

    @FXML
    private TableColumn<?, ?> SubjectColumn;

    @FXML
    void RequestExtraTime(ActionEvent event) {

    }

}
