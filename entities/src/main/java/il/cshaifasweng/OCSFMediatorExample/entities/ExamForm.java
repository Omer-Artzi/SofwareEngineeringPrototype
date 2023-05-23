package il.cshaifasweng.OCSFMediatorExample.entities;
import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Entity
@Table(name="Exam_Forms")
public class ExamForm implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ID;
@ManyToOne
@JoinColumn(name = "CourseID")
    private Course course;
    @ManyToOne
    @JoinColumn(name = "TeacherID")
    private Teacher creator;
    private String Code;
    @ManyToOne
    @JoinColumn(name = "SubjectID")
    private Subject subject;
    private Date dateCreated;
    private Date lastUsed;
    private double examTime;
    @ManyToMany
    @JoinColumn(name = "examForms")
    private List<Question>QuestionList = new ArrayList<>();


    public ExamForm(){}
    public ExamForm(Course course, Teacher teacher, Subject subject, double examTime,List<Question>QuestionList){
        this.course=course;
        this.creator =teacher;
        this.subject=subject;
        this.examTime=examTime;
        this.QuestionList=QuestionList;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public Course getCourse(){return course;}
    public void setCourse(Course newCourse){this.course=newCourse;}
    public Teacher getCreator(){return creator;}
    public void setCreator(Teacher newTeacher){this.creator =newTeacher;}
    public double getExamTime(){return examTime;}
    public void setExamTime(double examTime){this.examTime=examTime;}
    public List<Question> getQuestionList(){return QuestionList;}
    public void setQuestionList(List<Question> newQuestionList){this.QuestionList=newQuestionList;}

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
    }
}

