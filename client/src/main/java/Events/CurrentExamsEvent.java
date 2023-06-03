package Events;

import Entities.Exam;

import java.util.List;

//** bring the current exam list**//
public class CurrentExamsEvent {
    private List<Exam> examsList;
    public CurrentExamsEvent(List<Exam> exams) {
        this.examsList = exams;
    }

    public List<Exam> getExams() {
        return examsList;
    }

    public void setSubjects(List<Exam> exams) {
        this.examsList = exams;
    }
}
