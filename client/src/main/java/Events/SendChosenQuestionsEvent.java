package Events;

import Entities.Question;

import java.util.List;

public class SendChosenQuestionsEvent {
    List<Question> questions;

    public SendChosenQuestionsEvent(List<Question> questions) {
        this.questions = questions;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
