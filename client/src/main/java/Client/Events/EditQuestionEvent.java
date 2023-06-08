package Client.Events;

import Entities.SchoolOwned.Question;

public class EditQuestionEvent {
    private Question question;

    public EditQuestionEvent(){}
    public EditQuestionEvent(Question question){this.question=question;}
    public Question getQuestion(){return question;}
    public  void SetQuestion(Question question){this.question=question;}
}
