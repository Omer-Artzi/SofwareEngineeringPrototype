package Client.Controllers.MainViews.StaffViews.TeacherViews;

import Client.Events.SelectedClassExamEvent;
import Client.SimpleClient;
import Entities.SchoolOwned.ClassExam;
import Entities.Communication.Message;
import Entities.Communication.ExtraTime;
import Entities.Users.Principal;
import Entities.Users.Teacher;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ListSelectionView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


//** A controller to fill details about the Request to Extra Time**//
public class TeacherExtraTimeRequestController  {
    private ClassExam exam;
    private List<Principal> principals_list;
    @FXML
    private ListSelectionView<Principal> liad;
    @FXML
    private TextField NewTimeTextFiled;
    @FXML
    private AnchorPane principalListSelectionView;
    @FXML
    private TextArea TeacherNoteTF;
    @FXML
    private Button sendBT;
    @FXML
    private javafx.scene.layout.VBox VBox;


    @Subscribe
    public void updateExam(SelectedClassExamEvent event)
    {
        System.out.println("in @Subscribe");
        principals_list =event.getPrincipal();
        for(Principal item: principals_list)
            System.out.println(item);
        liad.getSourceItems().addAll(principals_list);
        exam=event.getExam();
    }

    @FXML
    void initialize() {

        EventBus.getDefault().register(this);
    }

    @FXML
    void NewTimeFunction(ActionEvent event) {

    }

    /* Send a notification to the relevant Principals*/
    @FXML
    void sendExtraTimeRequest(ActionEvent event) throws IOException {

        /* check if there is already ExtraTime request to this ClassExam */
        if(exam.getExtraTime()!=null) {
            JOptionPane.showMessageDialog(null, "you have already sent an ExtraTime Request for this ClassExam", "Error!", JOptionPane.ERROR_MESSAGE);
            return;
        }

        /* check if there is at least one selected principal */
        ObservableList<Principal> observablePrincipal = liad.getTargetItems();
        if (observablePrincipal.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please choose principal", "Error!", JOptionPane.ERROR_MESSAGE);
            return;
        }

        /* check if the teacher fill the extra time */
        String delta = (NewTimeTextFiled.getText());
        if (delta.equals("")||delta.isEmpty()){
            JOptionPane.showMessageDialog(null, "Please fill the extra time filled", "Error!", JOptionPane.ERROR_MESSAGE);
            return;
        }

        /* check if the the teacher fill a legal time */
        if (!delta.matches("-?\\d+")){
            JOptionPane.showMessageDialog(null, "please enter a legal time", "Error!", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Principal>SelectedPrincipal=new ArrayList<>();

        for (Principal item: observablePrincipal)
        {
            for(Principal item1: principals_list)
                if(item.getFullName().equals(item1.getFullName()))
                    SelectedPrincipal.add(item1);
        }

        String note=TeacherNoteTF.getText();
        Teacher teacher=((Teacher)(SimpleClient.getClient().getUser()));

        ExtraTime extraTime=new ExtraTime(exam, SelectedPrincipal,teacher,note);
        extraTime.setDelta(Integer.parseInt(delta));
        teacher.getExtraTimeList().add(extraTime);
        exam.setExtraTime(extraTime);
        teacher.getExtraTimeList().add(extraTime);
        Message message=new Message(1, "Extra time request", extraTime);

        SimpleClient.getClient().sendToServer(message);
    }
}
