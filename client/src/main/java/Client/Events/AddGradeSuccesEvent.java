package Client.Events;

import Entities.StudentOwned.Grade;

public class AddGradeSuccesEvent {
    private Grade grade;
    public AddGradeSuccesEvent(Grade data) {
        this.grade = data;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }
}
