package Events;

import Entities.Subject;

import java.util.List;

public class SubjectsOfTeacherMessageEvent {
    private List<Subject> subjects;
    public SubjectsOfTeacherMessageEvent(List<Subject> subjectList) {
        this.subjects = subjectList;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }
}