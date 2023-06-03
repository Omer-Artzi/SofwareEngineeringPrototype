import Entities.*;
import Events.NotificationEvent;
import Events.SelectedClassExamEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import org.controlsfx.control.ListSelectionView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//** A controller to fill details about the Request to Extra Time**//
public class TeacherExtraTimeRequestController {
    private ClassExam exam;
    private List<Principle>principles=new ArrayList<>();
    private List<Principle>selectedPrinciples=new ArrayList<>();
    @FXML
    private ListSelectionView<Principle> Principles;
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
        Principles.getSourceItems().addAll(principles);
        exam=event.getExam();
    }
    @FXML
    void initialize() {
        EventBus.getDefault().register(this);

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
        selectedPrinciples=Principles.getTargetItems();
        String note=TeacherNoteTF.getText();
        Teacher teacher=((Teacher)(SimpleClient.getClient().getUser()));
        ExtraTime extraTime=new ExtraTime(exam,selectedPrinciples,teacher,note);
        NotificationEvent NE=new NotificationEvent(extraTime);
        EventBus.getDefault().post(NE);
    }
}
