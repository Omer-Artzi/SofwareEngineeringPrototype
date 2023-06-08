package Client.Events;

import Entities.SchoolOwned.Course;
import Entities.SchoolOwned.Question;
import Entities.SchoolOwned.Subject;

import java.util.List;

public class SendChosenQuestionsEvent {
    List<Question> questions;

    Subject subject;

    Course course;

    public SendChosenQuestionsEvent(List<Question> questions, Subject subject, Course course) {
        this.questions = questions;
        this.subject = subject;
        this.course = course;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public Subject getSubject() {
        return subject;
    }

    public Course getCourse() {
        return course;
    }
}
