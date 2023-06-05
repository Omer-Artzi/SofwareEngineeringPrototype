package Events;

import Entities.ExamForm;
import Entities.Message;

public class ClassExamGradeEvent {
    String SubjectStr;
    String CourseStr;
    String ExamIDStr;
    int examFormID;

    public String getSubjectStr() {
        return SubjectStr;
    }
    public String getCourseStr() {
        return CourseStr;
    }
    public String getExamIDStr()
    {
        return ExamIDStr;
    }
    public int getExamFormID() {return examFormID;}

    public ClassExamGradeEvent(String SubjectStr, String CourseStr, String ExamIDStr, int examFormID)
    {
        this.SubjectStr = SubjectStr;
        this.CourseStr = CourseStr;
        this.ExamIDStr = ExamIDStr;
        this.examFormID = examFormID;
    }


}
