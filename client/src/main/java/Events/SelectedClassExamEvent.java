package Events;
import Entities.ClassExam;
import Entities.Principal;

import java.util.List;

/* This event send the selected live exam to RequestExtraTime controller*/
public class SelectedClassExamEvent {
    private ClassExam exam;
    private List<Object> objectList;
    private List<Principal> principal;
    public SelectedClassExamEvent(List<Object>object) {
        this.exam=(ClassExam) object.get(0);
        this.principal =(List<Principal>) object.get(1);
    }
    public SelectedClassExamEvent(ClassExam exam, List<Principal> principals) {
        this.objectList.add(exam);
        this.objectList.add(principals);
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

    public List<Principal> getPrinciple() {
        return principal;
    }

    public void setPrinciple(List<Principal> principal) {
        this.principal = principal;
    }
}
