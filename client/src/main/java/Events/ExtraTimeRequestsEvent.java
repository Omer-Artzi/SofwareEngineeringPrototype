package Events;
import java.util.List;
import Entities.ExtraTime;

public class ExtraTimeRequestsEvent {
    private List<ExtraTime> extraTimeList;

    public ExtraTimeRequestsEvent(List<ExtraTime> extraTimeList) {
        this.extraTimeList = extraTimeList;
    }

    public List<ExtraTime> getExtraTimeList() {
        return extraTimeList;
    }

    public void setExtraTimeList(List<ExtraTime> extraTimeList) {
        this.extraTimeList = extraTimeList;
    }

}
