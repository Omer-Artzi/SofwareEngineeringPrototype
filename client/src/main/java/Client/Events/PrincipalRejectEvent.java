package Client.Events;

import Entities.Communication.ExtraTime;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class PrincipalRejectEvent extends PrincipalDecisionEvent{

    public PrincipalRejectEvent(){}
    Notifications notification;
    public  PrincipalRejectEvent(ExtraTime extraTime) {
        super(extraTime);
        //createNotification(extraTime);
    }


    public void createNotification(ExtraTime extraTime){
       // try {
           // Platform.runLater(() -> {
                notification=Notifications.create();
                notification.title("Extra time request rejected");
                notification.text("Principal note: "+this.getExtraTime().getPrincipalNote());
                notification.position(Pos.BOTTOM_RIGHT);
                notification.hideAfter(Duration.seconds(60));
                notification.showInformation();
           // });

        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
    }

    public void show()
    {
        System.out.println("In show()");
        try {
            Platform.runLater(() -> {
                notification=Notifications.create();
                notification.title("Extra time request rejected");
                notification.text("Principal note: "+this.getExtraTime().getPrincipalNote());
                notification.position(Pos.BOTTOM_RIGHT);
                notification.hideAfter(Duration.seconds(60));
                notification.showInformation();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
