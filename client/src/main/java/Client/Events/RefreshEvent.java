package Client.Events;

import Entities.Users.Student;

public class RefreshEvent {
    private Student student;

    public RefreshEvent() {
    }

    public RefreshEvent(Student student) {
        this.student = student;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
