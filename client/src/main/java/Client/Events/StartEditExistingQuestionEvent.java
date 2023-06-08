package Client.Events;

import Entities.SchoolOwned.Course;
import Entities.SchoolOwned.Question;

public class StartEditExistingQuestionEvent {
    private Question question;

    private Course course;
//*get this event and send finishEvent
    public StartEditExistingQuestionEvent(Question question, Course course) {
        this.question = question;
        this.course = course;
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
