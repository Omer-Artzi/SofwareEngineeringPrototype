public class QuestionObject {
    private int questionId;
    private String question;
    private int percentage;

    public QuestionObject(){}
    public QuestionObject (int questionId, String question, int percentage){
        this.questionId = questionId;
        this.question = question;
        this.percentage = percentage;
    }

    public QuestionObject (int questionId, String question){
        this.questionId = questionId;
        this.question = question;
        this.percentage = 0;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public int getPercentage() {
        return percentage;
    }

    @Override
    public String toString() {
        return "QuestionObject{" +
                "question='" + question + '\'' +
                '}';
    }
}
