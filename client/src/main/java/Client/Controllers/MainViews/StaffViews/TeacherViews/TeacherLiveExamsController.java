package Client.Controllers.MainViews.StaffViews.TeacherViews;
import Client.Events.*;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
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
import javafx.scene.text.Text;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TeacherLiveExamsController extends SaveBeforeExit {
    Text text=new Text();
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

    /*
    @Subscribe
    public void chooseExam(ChooseExamEvent event){
        seeAnswerButton.setVisible(false);
        RequestExtraTimeBT.setDisable(false);
      /* Omer's function
    }
    */

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
    public void updateText(PrincipalApproveEvent event)
    {
        ExtraTime extraTime=event.getExtraTime();
        for(ClassExam item: examList){
            if(extraTime.getExam().getID()==item.getID())
            {
                item.setExtraTime(extraTime);
            }
        }
        /*
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

         */
    }

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
                        System.out.println("In green");
                        setStyle("-fx-background-color: #65A873;");
                    }
                } else {
                    setStyle("-fx-background-color: #DC6F6F;");
                }
            }
        }
    });
    }

    public void findText(ClassExam selectedExam)
    {
        System.out.println("hello ilan1");
            if (extraTimeList.isEmpty()) {
                System.out.println("In findText , the extra time list is empty");
                return;
            }
            if (SelectedExam==null)
            {
                System.out.println("In findText , the extra time list is empty");
                return;
            }
            System.out.println("hello ilan");
            for (ExtraTime item : extraTimeList) {
                if (selectedExam.equals(item.getExam())) {
                    if(item.getPrincipalNote()=="")
                        System.out.println("The data of principal note didnt save in database");
                    AnswerLabel.setText(AnswerLabel.getText() + " " + item.getPrincipalNote());
                    //AnswerLabel.setText("Liad");
                }
            }
    }

    @FXML
   void initialize() throws IOException {     // TODO:fill this function after merging with lior
        EventBus.getDefault().register(this);
        Message message=new Message(1, "Get Live Exams");
        SimpleClient.getClient().sendToServer(message);

        Person person =SimpleClient.getClient().getUser();
        if(person instanceof Principal||person instanceof Student) {

            RequestExtraTimeBT.setDisable(false);
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
        AnswerLabel.setVisible(false);
        AnswerTextFlow.setDisable(false);
        ExamsTable.refresh();
    }
/*
    @FXML
    void ChooseClassExam(MouseEvent event) {
        JOptionPane.showMessageDialog(null,SelectedExam.getExtraTime().getDecision(),"Class Exam Status",JOptionPane.INFORMATION_MESSAGE);
        AnswerTextFlow.setText("Answer: " + SelectedExam.getExtraTime().getDecision());
        AnswerLabel.setVisible(true);
        AnswerLabel.setText("Answer: " + SelectedExam.getExtraTime().getDecision());
    }
*/
    @Subscribe
    public void GetExtraTimeOfSpecificClassExam(extraTimeOfSpecificClassExam event){
        System.out.println("In @Subscribe");
        SelectedExtraTime=event.getExtraTime();
    }
    /*

    public void seeAnswer(ActionEvent event) throws IOException {
        Platform.runLater(()->{
            try {
                JOptionPane.showMessageDialog(null,SelectedExam.getExtraTime().getDecision(),"Class Exam Status",JOptionPane.INFORMATION_MESSAGE);
                AnswerTextFlow.setText(SelectedExam.getExtraTime().getDecision());
                System.out.println("hi from seeAnswer");
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        /*
        Message message=new Message(1, "Get extra time of specific class exam", SelectedExam);
        SimpleClient.getClient().sendToServer(message);

        Platform.runLater(() -> {
            if (SelectedExtraTime==null)
                System.out.println("extraTime null in Live Exam");
            AnswerTextFlow.setText("Answer: "+SelectedExtraTime.getDecision());
            AnswerLabel.setVisible(true);
            AnswerLabel.setText(AnswerLabel.getText() + " " + SelectedExtraTime.getDecision());
        });
        */

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
}
