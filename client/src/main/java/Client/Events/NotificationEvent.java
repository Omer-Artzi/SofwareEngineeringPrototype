package Client.Events;

import Entities.Communication.ExtraTime;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class NotificationEvent {
    Notifications notification;
    ExtraTime extraTime;

    public NotificationEvent(){}
    public NotificationEvent(ExtraTime ex)
    {
        this.extraTime=ex;
        createNotification(extraTime);

    }

    public void createNotification(ExtraTime extraTime){
        notification=Notifications.create();
        notification.title("New time request");
        notification.text("From: "+ extraTime.getTeacher().getFullName());
        notification.position(Pos.BOTTOM_RIGHT);
        notification.hideAfter(Duration.seconds(60));

    }

    //**show the notification with the details about the request time **//
    public void show()
    {
        System.out.println("In notification.show()");
        try {
            Platform.runLater(() -> {
                notification.showInformation();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
