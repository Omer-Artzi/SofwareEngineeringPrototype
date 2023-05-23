package Events;

import Entities.Grade;
import Entities.Message;

public class EditGradeSuccessEvent {
    private Message message;
    private Grade grade;

    public Message getMessage() {
        return message;
    }

    public EditGradeSuccessEvent(Message message) {
        this.message = message;
    }

    public EditGradeSuccessEvent(Grade grade) {
        this.grade = grade;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }
}
