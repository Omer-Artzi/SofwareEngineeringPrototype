public class Answer {
    private String answer;
    private String number;
    public Answer(){}
    public Answer(String number,String answer)
    {
        this.number=number;
        this.answer=answer;
    }
    public String getNumber(){return number;}
    public void setNumber(String number) {
        this.number = number;
    }
    public String getAnswer(){return answer;}
    public void setAnswer(String answer){this.answer=answer;}

    public void setStyle(String s) {
    }
}
