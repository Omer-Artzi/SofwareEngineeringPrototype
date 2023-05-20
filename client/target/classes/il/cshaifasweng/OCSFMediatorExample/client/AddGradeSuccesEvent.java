package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Grade;
import il.cshaifasweng.OCSFMediatorExample.entities.Student;

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
