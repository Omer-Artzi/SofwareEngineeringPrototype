package Events;
import Entities.ExtraTime;
import Entities.Principal;
import Entities.Teacher;
import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class PrincipalDecisionEvent {

    private ExtraTime extraTime;
    public PrincipalDecisionEvent(){}

    public ExtraTime getExtraTime() {
        return extraTime;
    }

    public void setExtraTime(ExtraTime extraTime) {
        this.extraTime = extraTime;
    }

    public void show(){}

    public PrincipalDecisionEvent(ExtraTime extraTime){this.extraTime=extraTime;}


}
