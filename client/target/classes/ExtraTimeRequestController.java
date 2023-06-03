import Entities.*;
import Events.NotificationEvent;
import Events.PrinciplesMessageEvent;
import Events.SelectedClassExamEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//** A controller to fill details about the Request to Extra Time**//
public class ExtraTimeRequestController {
    private ClassExam exam;
    private List<Principle>principles=new ArrayList<>();
    private List<Principle>selectedPrinciples=new ArrayList<>();
    @FXML
    private VBox CBvBox;
    //private VBox checkBoxContainer;
    @FXML
    private TextArea TeacherNoteTF;
    @FXML
    private Button sendBT;

    //**get the principles list from the server**//
    //@Subscribe
   // public void updatePrinciples(PrinciplesMessageEvent event){
    //    principles= event.getPrinciples();
    //}

    @Subscribe
    public void updateExam(SelectedClassExamEvent event)
    {
        System.out.println("in @Subscribe");
        principles=event.getPrinciple();
        selectedPrinciples=new ArrayList<>();
        for (int i = 0; i < principles.size(); i++) {
            // Create a CheckBox dynamically
            System.out.println("iteration number "+i);
            Principle p=new Principle("liad","arvatz",Gender.Female,"liad","1234");
            CheckBox checkBox1 = new CheckBox(principles.get(i).getFullName());
            CBvBox.getChildren().add(checkBox1);
            CheckBox checkBox = new CheckBox(principles.get(i).getFullName());
            System.out.println("name: "+principles.get(i).getFullName());
            CBvBox.getChildren().add(checkBox);
        }
        exam=event.getExam();
    }
    @FXML
    void initialize() {
        EventBus.getDefault().register(this);
        VBox CBvBox=new VBox();
        /*
        selectedPrinciples=new ArrayList<>();
        for (int i = 0; i < principles.size(); i++) {
            // Create a CheckBox dynamically
            CheckBox checkBox = new CheckBox(principles.get(i).getFullName());
            CBvBox.getChildren().add(checkBox);
            int finalI = i;
            checkBox.setOnAction(event -> {
                if (checkBox.isSelected())
                    selectedPrinciples.add(principles.get(finalI));
            });
        }
        */
       // checkBox.setOnAction(event1 -> {
         //   if (checkBox.isSelected())
         //       selectedPrinciples.add(principles.get(finalI));
        //});

    }

    /* Send a notification to the relevant Principles*/
    @FXML
    void sendExtraTimeRequest(ActionEvent event) throws IOException {
        String note=TeacherNoteTF.getText();
        Teacher teacher=((Teacher)(SimpleClient.getClient().getUser()));
        ExtraTime extraTime=new ExtraTime(exam,selectedPrinciples,teacher,note);
        NotificationEvent NE=new NotificationEvent(extraTime);
        EventBus.getDefault().post(NE);
    }
}
