package Client.Events;

import Entities.SchoolOwned.Question;

import java.util.List;

public class CourseQuestionsListEvent {
    private List<Question> questions;
    public CourseQuestionsListEvent(List<Question> questionList) {
        this.questions = questionList;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setSubjects(List<Question> subjects) {
        this.questions = questions;
    }
}