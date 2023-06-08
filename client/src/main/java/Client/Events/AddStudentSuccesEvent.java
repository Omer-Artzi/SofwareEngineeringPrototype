package Client.Events;

import Entities.Users.Student;

public class AddStudentSuccesEvent {
    private Student student;
    public AddStudentSuccesEvent(Student data) {
        this.student = data;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
