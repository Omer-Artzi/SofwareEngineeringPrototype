package Entities;

import java.util.List;


// Deprecated. ClassExam is the new entity
public class Exam extends ExamForm{

    private int ID;
    private String date;
    private List<Grade>gradeList;

    public Exam(){}
    public Exam(Course course, Teacher teacher, Subject subject, double examTime, List<Question> QuestionList,String date,List<Grade>gradeList)
    {
        super(course,teacher,subject,examTime,QuestionList);
        this.date=date;
        this.gradeList=gradeList;
    }
    public String getDate(){return date;}
    public void setDate(String newDate){this.date=newDate;}
    public List<Grade> getGradeList(){return gradeList;}
    public void setGradeList(List<Grade>gradeList){this.gradeList=gradeList;}
}
