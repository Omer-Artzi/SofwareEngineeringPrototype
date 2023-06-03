import Entities.*;
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
import java.util.Date;
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

    public List<ClassExam> SelectedExams(List<ClassExam> examList) throws IOException {
        Date CurrentDate=new Date();
        List<ClassExam>LiveExam=new ArrayList<>();

        /* First check : if the ClassExam's start and end date is between the current date*/
        for(ClassExam item: examList)
        {
            if (item.getStartDate().after(CurrentDate)||item.getStartDate().equals(CurrentDate))
                if (item.getFinalSubmissionDate().before(CurrentDate))
                    LiveExam.add(item);
        }
        /* Second check : show only the exams of the current student/ teacher, or show all in case the user is principle*/
        Person person=SimpleClient.getClient().getUser();

        /* case 1: the user is principle*/
        if(person instanceof Principle)
            return LiveExam;

        /*case 2: the user is teacher */
        else if(person instanceof Teacher){
            RequestExtraTimeBT.setVisible(true);
            for(ClassExam item: LiveExam)
                if(!item.getTeacher().equals(person))
                    LiveExam.remove(item);
        }

        /* case 3: the user is Student */
        else{
            boolean b=false;
            for(ClassExam item: LiveExam){
                for(Student item1: item.getStudents())
                {
                    if (item1.equals(person))
                       b=true;
                }
                if(b==false)
                    LiveExam.remove(item);
            }
        }
        return LiveExam;
    }


    @Subscribe
    public void update(LiveExamsEvent event) throws IOException {
        System.out.println("In LiveExamsController");
        examList=event.getLiveExams();
        List<ClassExam>LiveExam=SelectedExams(event.getLiveExams());
        if (!examList.isEmpty())
        {
            for (ClassExam item:examList)
            {
                System.out.println("hi");
                //if(item.)
            }
        }
        data.addAll(LiveExam);
    }


    @FXML
   void initialize() throws IOException {     // TODO:fill this function after merging with lior
        EventBus.getDefault().register(this);
        RequestExtraTimeBT.setVisible(false);
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
