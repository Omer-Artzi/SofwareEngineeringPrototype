package Events;

import Entities.ClassExam;

import java.util.List;

public class ExamMessageEvent {
    private  List<ClassExam> classExams;
    public ExamMessageEvent(List<ClassExam> data) {
        this.classExams = data;
    }

    public List<ClassExam> getClassExams() {
        return classExams;
    }

    public void setClassExams(List<ClassExam> examForms) {
        this.classExams = examForms;
    }
}
