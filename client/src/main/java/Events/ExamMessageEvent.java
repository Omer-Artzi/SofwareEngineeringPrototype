package Events;

import Entities.ExamForm;

import java.util.List;

public class ExamMessageEvent {
    private  List<ExamForm> examForms;
    public ExamMessageEvent(List<ExamForm> data) {
        this.examForms = data;
    }

    public List<ExamForm> getExamForms() {
        return examForms;
    }

    public void setExamForms(List<ExamForm> examForms) {
        this.examForms = examForms;
    }
}
