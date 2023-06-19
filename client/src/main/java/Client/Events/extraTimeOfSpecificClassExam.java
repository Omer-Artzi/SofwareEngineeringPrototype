package Client.Events;

import Entities.Communication.ExtraTime;

import java.util.List;

public class extraTimeOfSpecificClassExam {

    private String message;
    private ExtraTime extraTime;

    public extraTimeOfSpecificClassExam(ExtraTime extraTime,String message) {
        this.extraTime = extraTime;
        this.message=message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ExtraTime getExtraTime() {
        return extraTime;
    }

    public void setExtraTimeList(ExtraTime extraTimeList) {
        this.extraTime = extraTimeList;
    }
}
