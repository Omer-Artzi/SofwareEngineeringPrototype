package Client.Events;
import Entities.Communication.ExtraTime;

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
