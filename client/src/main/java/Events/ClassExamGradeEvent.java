package Events;

public class ClassExamGradeEvent {
    String SubjectStr;
    String CourseStr;
    String ExamIDStr;

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

    public ClassExamGradeEvent(String SubjectStr, String CourseStr, String ExamIDStr)
    {
        this.SubjectStr = SubjectStr;
        this.CourseStr = CourseStr;
        this.ExamIDStr = ExamIDStr;
    }
}
