package Events;
import Entities.ExtraTime;
import Entities.Teacher;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import javax.swing.*;
import java.io.IOException;

public class PrincipalApproveEvent extends PrincipalDecisionEvent{

    Notifications notification;
    public PrincipalApproveEvent() {}

    public PrincipalApproveEvent(ExtraTime extraTime) {
        super(extraTime);
        createNotification(extraTime);
    }

    public void createNotification(ExtraTime extraTime){
        notification = Notifications.create();
        notification.title("Extra time request approved");
        notification.text("Extra time in min: " + this.getExtraTime().getDelta()+"\nPrincipal note: " + this.getExtraTime().getPrincipalNote());
        notification.position(Pos.BOTTOM_RIGHT);
        notification.hideAfter(Duration.seconds(60));
    }


    public void show() {
        System.out.println("In show()");
        try {
            Platform.runLater(() -> {
                System.out.println("In show() in ApproveEvent");
                notification.show();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
