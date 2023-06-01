import Entities.ClassExam;
import Entities.ExamForm;
import Entities.Message;
import Entities.Teacher;
import Events.CurrentExamsEvent;
import Events.SelectedClassExamEvent;
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

import javax.persistence.criteria.Join;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class LiveExamsController {
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
    public void update(CurrentExamsEvent event) {}
    @FXML
   void initialize() throws IOException {     // TODO:fill this function after merging with lior
        EventBus.getDefault().register(this);
        Teacher liad=new Teacher();
        Date currentDate=new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        Date nextWeekDate = calendar.getTime();
        ExamForm e1=new ExamForm();
        ExamForm e2=new ExamForm();
        ExamForm e3=new ExamForm();
        ClassExam E1=new ClassExam(e1,currentDate,nextWeekDate,2.0,liad);
        ClassExam E2=new ClassExam(e2,currentDate,nextWeekDate,2.0,liad);
        ClassExam E3=new ClassExam(e3,currentDate,nextWeekDate,2.0,liad);
        ObservableList<ClassExam> data = ExamsTable.getItems();
      //  SubjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
     //  CourseColumn.setCellValueFactory(new PropertyValueFactory<>("course"));
        StartTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
       // EndTimeColumn.setCellValueFactory(new PropertyValueFactory<>("finalSubmissionDate"));
        data.add(E1);
        data.add(E2);
        data.add(E3);

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
       // SimpleClient.getClient().sendToServer(message);
        System.out.println("SelectedClassExamEvent in LiveExamController2");

        //SimpleChatClient.setRoot("ExtraTimeRequest");
    }

}
