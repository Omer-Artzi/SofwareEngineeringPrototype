package Client.Events;

import Entities.SchoolOwned.ClassExam;

public class ExamEndedMessageEvent {
    private ClassExam classExam;

    public ExamEndedMessageEvent(ClassExam classExam) {
        this.classExam = classExam;
    }

    public ClassExam getClassExam() {
        return classExam;
    }

    public void setClassExam(ClassExam classExam) {
        this.classExam = classExam;
    }
}
