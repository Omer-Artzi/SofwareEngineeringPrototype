package Entities;

import java.util.List;

public class SubjectWrapper {
    private List<Subject> subjects;

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    @Override
    public String toString() {
        return "SubjectWrapper{" +
                "subjects=" + subjects +
                '}';
    }
}