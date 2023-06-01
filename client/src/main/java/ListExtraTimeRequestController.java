import Entities.ExtraTime;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import java.io.IOException;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ListExtraTimeRequestController {

    @FXML
    private TableColumn<ExtraTime, String> CodeColumn;

    @FXML
    private TableView<ExtraTime> ExtraTimeList;

    @FXML
    private TableColumn<ExtraTime, String> IDColumn;

    @FXML
    private TableColumn<ExtraTime, String> SentByColumn;
    @FXML
    private Button SeeDetailsBT;
    @FXML
    void initialize(){

    }
    @FXML
    void SeeDetails(ActionEvent event) throws IOException {

        SimpleChatClient.setRoot("ApproveOfPrinciple");
    }

}
