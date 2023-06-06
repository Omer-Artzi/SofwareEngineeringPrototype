package Events;

import Entities.StudentExam;

import java.util.List;

public class StudentExamsMessageEvent {
    private List<StudentExam> studentExams;

    public StudentExamsMessageEvent(List<StudentExam> studentExams) {
        this.studentExams = studentExams;
    }

    public List<StudentExam> getStudentExams() {
        return studentExams;
    }

    public void setStudentExams(List<StudentExam> studentExams) {
        this.studentExams = studentExams;
    }
}
