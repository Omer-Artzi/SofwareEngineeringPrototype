package Events;

import Entities.Question;

public class ChangePreviewEvent {
    private Question question;

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Question getQuestion() {
        return question;
    }
}
