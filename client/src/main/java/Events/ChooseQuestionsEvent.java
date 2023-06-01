package Events;

import Entities.Course;
import Entities.Question;

import java.util.List;

public class ChooseQuestionsEvent {

    private List<Question> questions;

    private Course course;

    public ChooseQuestionsEvent() {}

    public ChooseQuestionsEvent(List<Question> questions, Course course) {
        this.questions = questions;
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
}
