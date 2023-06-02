package Events;

import Entities.Course;
import Entities.Question;
import Entities.Subject;

import java.util.List;

public class SendChosenQuestionsEvent {
    private List<Question> questions;
    private Subject subject;
    private Course course;


    public SendChosenQuestionsEvent(List<Question> questions, Subject selectedSubject, Course selectedCourse) {
        this.questions = questions;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
