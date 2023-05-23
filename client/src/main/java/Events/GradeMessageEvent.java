package Events;

import Entities.Grade;
import Entities.Message;
import Entities.Student;

import java.util.List;

public class GradeMessageEvent {
    private Message message;
    private List<Grade> grades;
    private Student student;

    public Message getMessage() {
        return message;
    }

    public GradeMessageEvent(Message message) {
        this.message = message;
    }
    public GradeMessageEvent(Message message,Student student) {
        this.message = message;
        this.student = student;
    }

    public GradeMessageEvent(Message message, Student student, List<Grade> grades) {
        this.message = message;
        this.grades = grades;
        this.student = student;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
