package Events;

import Entities.ClassExam;

import java.util.List;

public class LiveExamsEvent {
    private List<ClassExam> liveExams;
    LiveExamsEvent(){}

    public LiveExamsEvent(List<ClassExam> liveExams) {
        this.liveExams = liveExams;
    }

    public List<ClassExam> getLiveExams() {
        return liveExams;
    }

    public void setLiveExams(List<ClassExam> liveExams) {
        this.liveExams = liveExams;
    }
}
