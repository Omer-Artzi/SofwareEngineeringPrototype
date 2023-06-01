package Events;

import Entities.*;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.util.List;

public class notificationEvent {
    ExtraTime extraTime;
   // private List<Teacher>teacherList; TODO: check how to get the teacher who sent the request
    public notificationEvent(){}
    public notificationEvent(ExtraTime ex)
    {
        extraTime=ex;
    }

    //** check if the principle who get the event is selected when teacher selected the principles she wants to request from them**//
    public boolean IsFound(Person user){
        for (Principle item: extraTime.getPrincipals())
        {
            if (item.getID()==user.getID())
                return true;
        }
        return false;
    }

    //**show the notification with the details about the request time **//
    public void show()
    {
       Notifications notification=Notifications.create();
       notification.title("New Request Time");
      // notification.text("From"+ extraTime..getFullName());
      notification.position(Pos.BOTTOM_LEFT);
      notification.onAction(event -> openController());
      notification.show();
    }

    //** click on the notification will jump the principle to see more details about the request**//
    public void openController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ApproveOfPrinciple.fxml"));
            Parent root = loader.load();

            // Create a new stage and set the loaded controller as its controller
            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            // Show the stage
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
