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

    @ManyToOne
    @JoinColumn(name = "Student")
    private Student student;

    @ManyToOne(cascade =CascadeType.ALL)
    @JoinColumn(name = "ClassExam")
    private ClassExam classExam;

    @ElementCollection
    private List<String> studentAnswers = new ArrayList<>();
    private int grade;
    private HSTS_Enums.StatusEnum status;

    private String teacherNote;
    private String scoreChangeReason;

    public StudentExam() {
    }

    public StudentExam(Student student, ClassExam classExam, List<String> studentAnswers, int grade, HSTS_Enums.StatusEnum status)
    {
        this.student = student;
        this.student.addStudentExam(this);
        this.classExam = classExam;
        this.classExam.addStudentExam(this);
        this.studentAnswers = studentAnswers;
        this.grade = grade;
        this.status = status;
    }

    public void update(StudentExam other)
    {
        this.grade = other.grade;
        this.studentAnswers = new ArrayList<>(other.getStudentAnswers());
        this.status = other.status;
    }

    public int getID()
    {
        return this.ID;
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

    public List<String> getStudentAnswers()
    {
        return studentAnswers;
    }

    public void setStudentAnswers(List<String> studentAnswers)
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

    public HSTS_Enums.StatusEnum getStatus()
    {
        return status;
    }

    public void setStatus(HSTS_Enums.StatusEnum status)
    {
        this.status = status;
    }
    public String TranslateStatus()
    {
        switch (status)
        {
            case Approved -> {
            return "Approved";
        }
            case ToEvaluate -> {
            return "To Evaluate";
        }
            case NotTaken -> {
                return "Not Taken";
            }
            case Disapproved -> {
                return "Disapproved";
            }
        }
        return "";
    }


    public String getTeacherNote()
    {
        return teacherNote;
    }

    public void setTeacherNote(String teacherNote)
    {
        this.teacherNote = teacherNote;
    }

    public String getScoreChangeReason()
    {
        return scoreChangeReason;
    }

    public void setScoreChangeReason(String scoreChangeReason)
    {
        this.scoreChangeReason = scoreChangeReason;
    }


}
