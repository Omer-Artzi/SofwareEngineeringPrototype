package Events;
import Entities.ClassExam;
import Entities.Principle;

import java.util.List;

/* This event send the selected live exam to RequestExtraTime controller*/
public class SelectedClassExamEvent {
    private ClassExam exam;
    private List<Object> objectList;
    private List<Principle> principle;
    public SelectedClassExamEvent(List<Object>object) {
        this.exam=(ClassExam) object.get(0);
        this.principle=(List<Principle>) object.get(1);
    }
    public SelectedClassExamEvent(ClassExam exam, List<Principle>principles) {
        this.objectList.add(exam);
        this.objectList.add(principles);
    }
    public SelectedClassExamEvent(ClassExam exam) {
        this.exam = exam;
    }

    public void setExam(ClassExam exam) {
        this.exam = exam;
    }

    public ClassExam getExam() {
        return exam;
    }

    public List<Principle> getPrinciple() {
        return principle;
    }

    public void setPrinciple(List<Principle> principle) {
        this.principle = principle;
    }
}
