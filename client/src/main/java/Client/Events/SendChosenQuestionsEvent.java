package Client.Events;

import Client.Controllers.MainViews.StaffViews.TeacherViews.TeacherAddTestFormController;
import Entities.SchoolOwned.Course;
import Entities.SchoolOwned.Question;
import Entities.SchoolOwned.Subject;

import java.util.List;

public class SendChosenQuestionsEvent {
    List<Question> questions;

    Subject subject;

    Course course;

    private TeacherAddTestFormController.SaveState saveState;

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

    public TeacherAddTestFormController.SaveState getSaveState() {
        return saveState;
    }

    public void setSaveState(TeacherAddTestFormController.SaveState saveState) {
        this.saveState = saveState;
    }
}
