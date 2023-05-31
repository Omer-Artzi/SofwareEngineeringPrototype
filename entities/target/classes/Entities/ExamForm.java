package Entities;

import com.github.javafaker.Faker;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CourseID")
    private Course course;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TeacherID")
    private Teacher creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SubjectID")
    private Subject subject;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "examForms")
    private List<Question> QuestionList = new ArrayList<>();

    @OneToMany(mappedBy = "examForm")
    private List<ClassExam> classExams = new ArrayList<>();

    @ElementCollection
    private List<Integer> questionsScores = new ArrayList<>();

    private String Code;
    private Date dateCreated;
    private Date lastUsed;
    private double examTime;

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
        Faker faker= new Faker();
        if(Code == null)
        {
            Code = subject.getCode() + course.getCode() + faker.bothify("##");
        }
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

    public List<Integer> getQuestionsScores()
    {
        return questionsScores;
    }

    public void setQuestionsScores(List<Integer> questionsScores)
    {
        this.questionsScores = questionsScores;
    }
    public void AddQuestionsScores(int questionsScore)
    {
        this.questionsScores.add(questionsScore);
    }

    public List<Question> getQuestionList(){return QuestionList;}
    public void setQuestionList(List<Question> newQuestionList){this.QuestionList=newQuestionList;}

    public List<ClassExam> getClassExam(){return classExams;}
    public void setClassExam(List<ClassExam> classExam){this.classExams=classExam;}
    public void addClassExam(ClassExam classExam){this.classExams.add(classExam);}

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

    public void addQuestion(Question question) {
        QuestionList.add(question);
    }

}


