package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.Subject;

import java.util.List;

public class SubjectMessageEvent {
    private List<Subject> subjects;
    public SubjectMessageEvent(List<Subject> subjectList) {
        this.subjects = subjectList;

    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }
}
