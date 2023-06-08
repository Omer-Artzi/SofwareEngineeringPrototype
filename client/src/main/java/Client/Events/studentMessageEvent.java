package Client.Events;

import Entities.Communication.Message;
import Entities.Users.Student;

import java.util.List;

public class studentMessageEvent {
    private Message message;
    private List<Student> students;

    public Message getMessage() {
        return message;
    }

    public studentMessageEvent(Message message) {
        this.message = message;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
}
