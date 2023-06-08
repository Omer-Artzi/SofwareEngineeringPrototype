package Client.Events;
import Entities.StudentOwned.StudentExam;


public class StudentExamEvent {
    private StudentExam exam;

    public StudentExam getStudentExam() {
        return exam;
    }

    public void setStudentExam(StudentExam exam) {
        this.exam = exam;
    }

    public StudentExamEvent(StudentExam exam) {
        this.exam = exam;
    }


}
