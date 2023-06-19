package Client.Events;

import Entities.Communication.ExtraTime;

import java.util.List;

public class extraTimeOfSpecificClassExam {

    private ExtraTime extraTime;

    public extraTimeOfSpecificClassExam(ExtraTime extraTime) {
        this.extraTime = extraTime;
    }

    public ExtraTime getExtraTime() {
        return extraTime;
    }

    public void setExtraTimeList(ExtraTime extraTimeList) {
        this.extraTime = extraTimeList;
    }
}
