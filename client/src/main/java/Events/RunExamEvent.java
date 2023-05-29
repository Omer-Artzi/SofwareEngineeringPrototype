package Events;
import Entities.Exam;

//*** Teacher has a controller of Exam management. If she clicks on a button "Request Extra Time",
// the event is created and she will send the Exam instance too.
public class RunExamEvent {
    private Exam exam;
    public RunExamEvent(){}
    public RunExamEvent(Exam exam){this.exam=exam;}
    public Exam getExam(){return exam;}
}
