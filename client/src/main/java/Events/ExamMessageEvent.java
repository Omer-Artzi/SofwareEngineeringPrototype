package Events;

import Entities.ClassExam;
import Entities.ExamForm;

import java.util.List;

public class ExamMessageEvent {
    private  List<ClassExam> classExams;
    public ExamMessageEvent(List<ClassExam> data) {
        this.classExams = data;
    }

    public List<ClassExam> getExamForms() {
        return classExams;
    }

    public void setExamForms(List<ClassExam> examForms) {
        this.classExams = examForms;
    }
}
