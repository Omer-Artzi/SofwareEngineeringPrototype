package Events;

import Entities.Course;
import Entities.Question;

public class FinishEditExistingQuestionEvent {

    private Question question;

    private Course course;

    public FinishEditExistingQuestionEvent(Question question, Course course) {
        this.question = question;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
