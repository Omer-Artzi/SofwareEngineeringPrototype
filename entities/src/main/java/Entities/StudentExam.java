package Entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "StudentExams")
public class StudentExam implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Student")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassExam")
    private ClassExam classExam;

    @ElementCollection
    private List<Integer> studentAnswers = new ArrayList<>();

    // Switch to Grade class? Delete Grade Class?
    private int grade;

    private String status;
    // String - 1- "To Evaluate", 2 - "Approved"

    private double timeLeft;

    public StudentExam(Student student, ClassExam classExam, List<Integer> studentAnswers, int grade, String status)
    {
        this.student = student;
        this.student.AddStudentExam(this);
        this.classExam = classExam;
        this.classExam.AddStudentExam(this);
        this.timeLeft = this.classExam.getExamForm().getExamTime();
        this.studentAnswers = studentAnswers;
        this.grade = grade;
        this.status = status;
    }

    public Student getStudent()
    {
        return student;
    }

    public void setStudent(Student student)
    {
        this.student = student;
    }

    public ClassExam getClassExam()
    {
        return classExam;
    }

    public void setClassExam(ClassExam classExam)
    {
        this.classExam = classExam;
    }

    public List<Integer> getStudentAnswers()
    {
        return studentAnswers;
    }

    public void setStudentAnswers(List<Integer> studentAnswers)
    {
        this.studentAnswers = studentAnswers;
    }

    public int getGrade()
    {
        return grade;
    }

    public void setGrade(int grade)
    {
        this.grade = grade;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public double getTimeLeft(){return timeLeft;}
    public void setTimeLeft(double timeLeft){this.timeLeft=timeLeft;}



}
