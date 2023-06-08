package Client.Events;

import Entities.SchoolOwned.ClassExam;

public class StartExamEvent {
    private ClassExam classExam;
    public StartExamEvent(ClassExam selectedExam) {
        classExam = selectedExam;
    }

    public ClassExam getClassExam() {
        return classExam;
    }

    public void setClassExam(ClassExam classExam) {
        this.classExam = classExam;
    }
}
