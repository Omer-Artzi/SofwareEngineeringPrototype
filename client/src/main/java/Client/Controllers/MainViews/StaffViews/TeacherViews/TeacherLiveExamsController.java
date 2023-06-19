package Client.Controllers.MainViews.StaffViews.TeacherViews;
import Client.Events.*;
import javafx.scene.control.TextArea;
import Client.Controllers.MainViews.SaveBeforeExit;
import Client.SimpleChatClient;
import Client.SimpleClient;
import Entities.Communication.ExtraTime;
import Entities.SchoolOwned.ClassExam;
import Entities.Communication.Message;
import Entities.Users.Person;
import Entities.Users.Principal;
import Entities.Users.Student;
import Entities.Users.Teacher;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TeacherLiveExamsController extends SaveBeforeExit {
    @FXML
    private Button seeDecisionButton;
    List<ExtraTime>extraTimeList=new ArrayList<>();
    ObservableList<ClassExam> data;
    private List<ClassExam> examList=new ArrayList<>();
    private ClassExam SelectedExam=null;
    private ExtraTime SelectedExtraTime=null;
    @FXML
    private Button CreateNewExamButton;
    @FXML
    private VBox vBox;
    @FXML
    private Label AnswerLabel;
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
    @FXML
    private TextArea AnswerTextFlow;

    @FXML
    private CheckBox FutureExamCheckBox;

    @FXML
    private CheckBox PastExamCheckBox;
    @FXML
    private CheckBox CurrentExamCheckBox;

    /*
    @Subscribe
    public void chooseExam(ChooseExamEvent event){
        seeAnswerButton.setVisible(false);
        RequestExtraTimeBT.setDisable(false);
      /* Omer's function
    }
    */

    /////////////////////////////*** Extra's ***///////////////////////////////////////////
    public void colorRows(List<ClassExam>list,String color)
    {
        data.addAll(list);
        ExamsTable.setRowFactory(tv -> new TableRow<>(){
            @Override
            protected void updateItem(ClassExam item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    // Set the default background color for empty rows
                    setStyle("");
                } else {
                    // Set the background color based on some condition
                    setStyle(color);
                }
            }
        });

    }

    @FXML
    void CurrentExamCheckBoxFunc(ActionEvent event) throws IOException {
        Date CurrentDate = new Date();
        List<ClassExam> CurrentExams = new ArrayList<>();
        for (ClassExam item : examList) {
            if (item.getStartDate().before(CurrentDate) || item.getStartDate().equals(CurrentDate)) {
                if (item.getFinalSubmissionDate().after(CurrentDate)) {
                    CurrentExams.add(item);
                }
            }
        }
        CurrentExams = SelectedExams(CurrentExams);
        colorRows(CurrentExams,"-fx-background-color: #74E391");
    }

    @FXML
    void FutureExamCheckBoxFunc(ActionEvent event) throws IOException {
        Date CurrentDate = new Date();
        List<ClassExam> CurrentExams = new ArrayList<>();
        for (ClassExam item : examList) {
            if (item.getStartDate().after(CurrentDate) || item.getStartDate().equals(CurrentDate)) {
                CurrentExams.add(item);
            }
        }
        CurrentExams = SelectedExams(CurrentExams);
        colorRows(CurrentExams,"-fx-background-color: #7BAAE0");
    }

    @FXML
    void PastExamCheckBoxFunc(ActionEvent event) throws IOException {
        Date CurrentDate = new Date();
        List<ClassExam> CurrentExams = new ArrayList<>();
        for (ClassExam item : examList) {
            if (item.getStartDate().before(CurrentDate) || item.getStartDate().equals(CurrentDate)) {
                if (item.getFinalSubmissionDate().before(CurrentDate)) {
                    CurrentExams.add(item);
                }
            }
        }
        CurrentExams = SelectedExams(CurrentExams);
        colorRows(CurrentExams,"-fx-background-color: #F26666");

    }
/////////////////////////////////*** End Extra's ***/////////////////////////////////////////////////////
    //***
    public List<ClassExam> SelectedExams(List<ClassExam> examList) throws IOException {

        List<ClassExam>LiveExam=new ArrayList<>();
        System.out.println("IN SELECTED EXAMS");
        // First check : if the ClassExam's start and end date is between the current date//
       LiveExam=examList;
        // Second check : show only the exams of the current student/ teacher, or show all in case the user is principal//
        Person person=Client.SimpleClient.getClient().getUser();

        // case 1: the user is principal//
        if(person instanceof Principal)
            return LiveExam;

        //case 2: the user is teacher //
        else if(person instanceof Teacher){
            RequestExtraTimeBT.setVisible(true);
            for(ClassExam item: LiveExam)
                if(!item.getTeacher().equals(person)) {
                    System.out.println("remoce from live exam list");
                    LiveExam.remove(item);
                }
        }

        // case 3: the user is Student //
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

/*
    @Subscribe
    public void getExtraTimeRequests(ExtraTimeRequestsEvent event){
        extraTimeList=event.getExtraTimeList();
        for(ExtraTime ite: extraTimeList)
        {
            System.out.println("Principal note is: "+ite.getPrincipalNote());
        }
    }
*/

    @Subscribe
    public void GetExtraTimeOfSpecificClassExam(extraTimeOfSpecificClassExam event){
        SelectedExtraTime=event.getExtraTime();
        System.out.println("in @Subscribe function");
        if(SelectedExam==null)
            System.out.println("List is empty!");
        JOptionPane.showMessageDialog(null, "HIIIII", "Database Error", JOptionPane.WARNING_MESSAGE);
    }

/*
    @Subscribe
    public void updateText(PrincipalApproveEvent event)
    {
        ExtraTime extraTime=event.getExtraTime();
        for(ClassExam item: examList){
            if(extraTime.getExam().getID()==item.getID())
            {
                item.setExtraTime(extraTime);
            }
        }

        Platform.runLater(() -> {

            try {
                System.out.println("get the extra time");
                ExtraTime extraTime = event.getExtraTime();
                extraTimeList.add(extraTime);
                ExamsTable.refresh();
            }catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
 */

/* get the class exams from server , then insert them to the table and sort them by start and end date */
    @Subscribe
    public void update(LiveExamsEvent event) throws IOException {
        examList=event.getLiveExams();
        //data.addAll(examList);
        data.addAll(SelectedExams(examList));
        Date CurrentDate=new Date();
        ExamsTable.setRowFactory(tv -> new TableRow<>(){
            @Override
            protected void updateItem(ClassExam item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                // Set the default background color for empty rows
                setStyle("");
            } else {
                // Set the background color based on some condition
                if (item.getStartDate().before(CurrentDate)||item.getStartDate().equals(CurrentDate)) {
                    if (item.getFinalSubmissionDate().after(CurrentDate)) {
                        setStyle("-fx-background-color: #74E391");
                    } else if (item.getFinalSubmissionDate().before(CurrentDate) || item.getFinalSubmissionDate().equals(CurrentDate)) {
                        setStyle("-fx-background-color: #F26666");
                    }
                }
                else{
                    setStyle("-fx-background-color: #7BAAE0");
                }
            }
        }
    });
    }

    @FXML
   void initialize() throws IOException {

        EventBus.getDefault().register(this);
        Message message=new Message(1, "Get Live Exams");
        SimpleClient.getClient().sendToServer(message);

        Person person =SimpleClient.getClient().getUser();

        if(person instanceof Principal||person instanceof Student) {
            RequestExtraTimeBT.setVisible(false);
            CreateNewExamButton.setVisible(false);
        }

        vBox=new VBox();
        data = ExamsTable.getItems();
        SubjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        CourseColumn.setCellValueFactory(new PropertyValueFactory<>("course"));
        StartTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        EndTimeColumn.setCellValueFactory(new PropertyValueFactory<>("finalSubmissionDate"));

        ExamsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                SelectedExam=newSelection;
                if(SelectedExam.getExtraTime()!=null) {
                    JOptionPane.showMessageDialog(null,SelectedExam.getExtraTime().getDecision(),"Class Exam Status",JOptionPane.INFORMATION_MESSAGE);
                    AnswerTextFlow.setText("Answer: " + SelectedExam.getExtraTime().getDecision());
                    AnswerLabel.setVisible(true);
                    AnswerLabel.setText("Answer: " + SelectedExam.getExtraTime().getDecision());
                }
            }
        });
        ExamsTable.refresh();
        AnswerLabel.setVisible(false);
        AnswerTextFlow.setDisable(false);
        ExamsTable.refresh();
        CurrentExamCheckBox.setVisible(false);
        PastExamCheckBox.setVisible(false);
        FutureExamCheckBox.setVisible(false);
    }

//////////////////////////////*** while pressing on create new exam button ***/////////////////////////////
    @FXML
    void createNewExam(ActionEvent event) throws IOException {
        SelectedExam = ExamsTable.getSelectionModel().getSelectedItem();
        if(SelectedExam == null){
            JOptionPane.showMessageDialog(null, "Please choose exam", "Error!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        LoadExamEvent loadExamEvent =new LoadExamEvent();
        loadExamEvent.setClassExam(SelectedExam);
        SimpleChatClient.setRoot("TeacherCreateClassExam");
        EventBus.getDefault().post(loadExamEvent);

    }

    ///////////////////////////// *** while pressing on extra time button ***////////////////////////////////
    @FXML
    void RequestExtraTime(ActionEvent event) throws IOException {
        if (SelectedExam == null) {
            JOptionPane.showMessageDialog(null, "Please choose exam", "Error!", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date currentDate = new Date();
        if (SelectedExam.getStartDate().after(currentDate) || SelectedExam.getFinalSubmissionDate().before(currentDate)){
            JOptionPane.showMessageDialog(null, "Invalid choose", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SimpleChatClient.setRoot("ExtraTimeRequest");
        Message message=new Message(1, "Get ExtraTimeRequest data",SelectedExam);
        Platform.runLater(()-> {
            try {
                SimpleClient.getClient().sendToServer(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
//////////////////////////////*** while pressing on see decision button ***//////////////////////////////////
    @FXML
    void seeDecisionFunc(ActionEvent event) throws IOException {
        if (SelectedExam==null){
            JOptionPane.showMessageDialog(null, "Please choose exam", "Error!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Message message=new Message(1, "Get extra time of specific class exam", SelectedExam);
        SimpleClient.getClient().sendToServer(message);
    }
}
