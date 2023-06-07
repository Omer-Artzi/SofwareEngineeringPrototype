import Entities.ExtraTime;
import Entities.Message;
import Entities.Principal;
import Events.ExtraTimeRequestsEvent;
import Events.NotificationEvent;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListExtraTimeRequestController extends SaveBeforeExit{
    private ExtraTime extraTimeSelected;
    private List<ExtraTime> extraTimeList = new ArrayList<>();
    private ObservableList<ExtraTime> data;
    @FXML
    private TableColumn<ExtraTime, String> InfoColumn;

    @FXML
    private ChoiceBox<String> DecisionCoiceBox;

    @FXML
    private TableView<ExtraTime> ExtraTimeList;

    @FXML
    private TableColumn<ExtraTime, String> IDColumn;

    @FXML
    private TextField NewTimeTextFiled;

    @FXML
    private TextArea PrincipalNote;

    @FXML
    private Button SendButton;

    @FXML
    private TableColumn<ExtraTime, String> SentByColumn;

    @FXML
    private Label DecisionLabel;

    @FXML
    private Label ExtraTimeLabel;

    /* get the request from a teacher */
    /*
    @Subscribe
    public void update(NotificationEvent event) throws IOException {
        Platform.runLater(()-> {
            try {
                System.out.println("In pricipal MainScreen");
                Principal user=((Principal)(SimpleClient.getClient().getUser())); //TODO: Notification
                if(event.IsFound(user)) {
                    event.show();
                    //JOptionPane.showMessageDialog(null, "HI", "ExtraTimerRequest", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
*/
    /* get the Extra Time request from the data base */
    @Subscribe
    public void update(ExtraTimeRequestsEvent event) {
        System.out.println("in listExtraTime");
        extraTimeList = event.getExtraTimeList();
        data.addAll(extraTimeList);
    }

    public void elementStatus(boolean b) {
        ExtraTimeLabel.setVisible(b);
        DecisionLabel.setVisible(b);
        SendButton.setVisible(b);
        PrincipalNote.setDisable(b);
        NewTimeTextFiled.setVisible(b);
        DecisionCoiceBox.setVisible(b);
        PrincipalNote.setVisible(b);
    }

    @FXML
    void initialize() throws IOException {
        EventBus.getDefault().register(this);
        elementStatus(false);
        Message message = new Message(1, "Get Extra Time Requests");
        SimpleClient.getClient().sendToServer(message);

        /* intialize the elements */
        data = ExtraTimeList.getItems();

        InfoColumn.setCellValueFactory(new PropertyValueFactory<>("teacherNote"));
        IDColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        SentByColumn.setCellValueFactory(new PropertyValueFactory<>("teacher"));

        String [] str={"YES", "NO"};
        DecisionCoiceBox.setItems(FXCollections.observableArrayList(str));

        /* select a request from the table */
        ExtraTimeList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                extraTimeSelected = newSelection;
                DecisionCoiceBox.setVisible(true);
                DecisionLabel.setVisible(true);
            }
        });

        /* check the decision */
        DecisionCoiceBox.setOnAction(event -> {
            if (!DecisionCoiceBox.getSelectionModel().isEmpty() || DecisionCoiceBox.getSelectionModel() != null) {
                    SendButton.setVisible(true);
                    PrincipalNote.setVisible(true);
                if (DecisionCoiceBox.getValue().equals("YES")) {
                    ExtraTimeLabel.setVisible(true);
                    NewTimeTextFiled.setVisible(true);
                }
                else if(DecisionCoiceBox.getValue().equals("NO")){
                    ExtraTimeLabel.setVisible(false);
                    NewTimeTextFiled.setVisible(false);
                }
            }
        });
    }

    @FXML
    void NewTimeFunction(ActionEvent event) {

    }

    @FXML
    void SendDecision(ActionEvent event) throws IOException {

        String decision = DecisionCoiceBox.getValue();
        String delta = (NewTimeTextFiled.getText());
        String principalNote = PrincipalNote.getText();

        if (decision.equals("YES")) {

            if (delta.equals("")||delta.isEmpty()){
                JOptionPane.showMessageDialog(null, "Please fill the extra time filled", "Error!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!delta.matches("-?\\d+")){
                JOptionPane.showMessageDialog(null, "Illegal Input", "Error!", JOptionPane.ERROR_MESSAGE);
                return;
            }

            extraTimeSelected.setDelta(Integer.parseInt(delta));
            extraTimeSelected.setDecision(decision);
            extraTimeSelected.setPrincipalNote(principalNote);
            Message message = new Message(1, "Extra time approved", extraTimeSelected);
            SimpleClient.getClient().sendToServer(message);

        } else {

            extraTimeSelected.setDecision(decision);
            extraTimeSelected.setPrincipalNote(principalNote);
            Message message = new Message(1, "Extra time rejected",extraTimeSelected );
            SimpleClient.getClient().sendToServer(message);

        }
    }
}

