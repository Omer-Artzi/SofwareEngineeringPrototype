package Entities.StudentOwned;

import Entities.SchoolOwned.ClassExam;
import Entities.Enums;
import Entities.SchoolOwned.ExamForm;
import Entities.Users.Student;

import javax.persistence.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private Enums.submissionStatus status;

    private String teacherNote;
    private String scoreChangeReason;

    private ManualStudentExam manualExam;

    public StudentExam() {
    }

    public StudentExam(Student student, ClassExam classExam, List<String> studentAnswers, int grade, Enums.submissionStatus status)
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
        this.teacherNote = other.teacherNote;
        this.scoreChangeReason = other.scoreChangeReason;
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

    public Enums.submissionStatus getStatus()
    {
        return status;
    }

    public void setStatus(Enums.submissionStatus status)
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

    public ManualStudentExam getManualExam()
    {
        return manualExam;
    }

    public void setManualExam(ManualStudentExam manualExam)
    {
        this.manualExam = manualExam;
    }


}
