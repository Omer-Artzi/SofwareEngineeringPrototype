package Entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ClassExams")
public class ClassExam implements Serializable
{
    // TODO change ID generation to be calc by server and be dependent on course and subject
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ID;
    private String date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Teacher")
    private Teacher tester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ExamForm")
    private ExamForm examForm;

    @OneToMany(mappedBy = "classExam")
    private List<StudentExam> studentExams = new ArrayList<>();

    public ClassExam(ExamForm examForm, String date, Teacher tester)
    {
        this.examForm = examForm;
        this.date=date;
        this.tester = tester;
        //tester.AddClassExam(this);
    }

    public int getID() {return ID;}

    public List<StudentExam> getStudentExams(){return studentExams;}
    public void setStudentExams(List<StudentExam> studentExams){this.studentExams=studentExams;}
    public void AddStudentExam(StudentExam studentExam){studentExams.add(studentExam);}
    public String getDate(){return date;}
    public void setDate(String newDate){this.date=newDate;}
    public ExamForm getExamForm(){return examForm;}
    public void setExamForm(ExamForm examForm){this.examForm=examForm;}

    public Teacher getTeacher(){return tester;}
    public void setTeacher(Teacher tester){this.tester=tester;}
}