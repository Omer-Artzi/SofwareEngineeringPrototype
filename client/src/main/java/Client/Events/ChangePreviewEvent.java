package Client.Events;

import Entities.SchoolOwned.Question;

public class ChangePreviewEvent {
    private Question question;

    private String selectedAnswer = null;

    public void setQuestion(Question question) {
        this.question = question;
    }

    public void setSelectedAnswer(String selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public Question getQuestion() {
        return question;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }
}
