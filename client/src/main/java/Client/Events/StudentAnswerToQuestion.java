package Client.Events;

import Entities.SchoolOwned.Question;

public class StudentAnswerToQuestion {

    private Question question;

    private String selectedAnswer = null;

    public StudentAnswerToQuestion(Question question, String selectedAnswer) {
        this.question = question;
        this.selectedAnswer = selectedAnswer;
    }

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
