import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;

public class TeacherMainScreenController extends SaveBeforeExit {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label MainMessageLabel;

    @FXML
    void initialize() {

        EventBus.getDefault().register(this);

        assert MainMessageLabel != null : "fx:id=\"MainMessageLabel\" was not injected: check your FXML file 'TeacherMainScreen.fxml'.";

    }

}