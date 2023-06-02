package Events;

import Entities.Course;
import Entities.Question;
import Entities.Subject;

import java.util.List;

public class ChooseQuestionsEvent {

    private List<Question> questions;
    private Subject subject;
    private Course course;

    public ChooseQuestionsEvent() {}

    public ChooseQuestionsEvent(List<Question> questions, Subject subject, Course course) {
        this.questions = questions;
        this.subject = subject;
        this.course = course;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }
}
