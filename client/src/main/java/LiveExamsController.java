import Entities.ClassExam;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class LiveExamsController {
    @FXML
    private TableView<ClassExam> ExamsTable;
    @FXML
    private TableColumn<ClassExam, String> CodeColumn;

    @FXML
    private TableColumn<ClassExam, String> CourseColumn;

    @FXML
    private TableColumn<ClassExam, String> EndTimeColumn;

    @FXML
    private Button RequestExtraTimeBT;

    @FXML
    private TableColumn<ClassExam, String> StartTimeColumn;

    @FXML
    private TableColumn<ClassExam, String> SubjectColumn;

    @FXML
    void initialize() throws IOException {
        ObservableList<ClassExam> data = ExamsTable.getItems();
        SubjectColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        CourseColumn.setCellValueFactory(new PropertyValueFactory<>("answer"));
        StartTimeColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        EndTimeColumn.setCellValueFactory(new PropertyValueFactory<>("answer"));
        CodeColumn.setCellValueFactory(new PropertyValueFactory<>("number"));



    }

        @FXML
    void RequestExtraTime(ActionEvent event) {


    }

}
