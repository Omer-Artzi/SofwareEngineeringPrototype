package Client.Events;

import Entities.SchoolOwned.ClassExam;
import Entities.SchoolOwned.ExamForm;

import java.util.List;

public class ExamMessageEvent {
    private  List<ClassExam> classExams;
    private List<ExamForm> examForms;
    public ExamMessageEvent(List<ClassExam> data) {
        this.classExams = data;
    }

    public ExamMessageEvent() {
    }

    public List<ClassExam> getClassExams() {
        return classExams;
    }

    public void setClassExams(List<ClassExam> examForms) {
        this.classExams = examForms;
    }

    public List<ExamForm> getExamForms() {
        return examForms;
    }

    public void setExamForms(List<ExamForm> examForms) {
        this.examForms = examForms;
    }
}
