package Events;
import Entities.Exam;

import java.util.ArrayList;
import java.util.List;

//*** Teacher has a controller of Exam management. If she clicks on a button "Request Extra Time",
// the event is created and she will send the Exam instance too.
public class LiveExamsEvent {
    private List<Exam> exams=new ArrayList<>();
    public LiveExamsEvent(){}
    public LiveExamsEvent(List<Exam> exam){this.exams=exam;}
    public List<Exam> getExam(){return exams;}
}
