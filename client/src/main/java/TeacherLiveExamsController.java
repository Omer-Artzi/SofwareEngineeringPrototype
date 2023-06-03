import Entities.ClassExam;
import Entities.Message;
import Events.LiveExamsEvent;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TeacherLiveExamsController {
    //TableView<ClassExam> data;
    ObservableList<ClassExam> data;
    private List<ClassExam> examList=new ArrayList<>();
    private ClassExam SelectedExam=null;
    @FXML
    private TableView<ClassExam> ExamsTable;

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

    @Subscribe
    public void update(LiveExamsEvent event) {
        System.out.println("In LiveExamsController");
        examList=event.getLiveExams();
        //data = FXCollections.observableArrayList(
       //         event.getLiveExams()
       // );
        if (!examList.isEmpty())
        {
            for (ClassExam item:examList)
            {
                System.out.println("hi");
            }
        }
        data.addAll(examList);
    }


    @FXML
   void initialize() throws IOException {     // TODO:fill this function after merging with lior
        EventBus.getDefault().register(this);
        //data=new TableView<>();
        data = ExamsTable.getItems();
        SubjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        CourseColumn.setCellValueFactory(new PropertyValueFactory<>("course"));
        StartTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        EndTimeColumn.setCellValueFactory(new PropertyValueFactory<>("finalSubmissionDate"));
        //data.get
        //data.addAll(StartTimeColumn, EndTimeColumn);
        //When select a row in the table
        ExamsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                SelectedExam=newSelection;
            }
        });
    }

    @FXML
    void RequestExtraTime(ActionEvent event) throws IOException {
        SimpleChatClient.setRoot("ExtraTimeRequest");
        Message message=new Message(1, "Get ExtraTimeRequest data",SelectedExam);
        System.out.println("SelectedClassExamEvent in LiveExamController");
        Platform.runLater(()-> {
            try {
                SimpleClient.getClient().sendToServer(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        System.out.println("SelectedClassExamEvent in LiveExamController2");

    }

}
