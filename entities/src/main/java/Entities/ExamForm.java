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

    String examFormID;
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
    private List<Question> questionList = new ArrayList<>();

    @OneToMany(mappedBy = "examForm")
    private List<ClassExam> classExams = new ArrayList<>();

    @ElementCollection
    private List<Integer> questionsScores = new ArrayList<>();

    private String code;
    private String headerText;
    private String footerText;
    private String examNotesForTeacher;
    private String examNotesForStudent;
    private Date dateCreated;
    private Date lastUsed;

    private boolean used;

    private int examTime; // in minutes


    public ExamForm(){this.used = false;}
    public ExamForm(Course course, Teacher teacher, Subject subject, List<Question> questionList){
        this.course=course;
        this.creator =teacher;
        this.subject=subject;
        this.questionList=questionList;
        this.used = false;
    }

    public ExamForm(Teacher creator, Subject subject, Course course, List<Question> questionList,
                    List<Integer> questionsScores, Date dateCreated, String headerText, String footerText,
                    String examNotesForTeacher, String examNotesForStudent, int examTime) {
        this.course = course;
        this.creator = creator;
        this.subject = subject;
        this.questionList = questionList;
        this.questionsScores = questionsScores;
        this.dateCreated = dateCreated;
        this.headerText = headerText;
        this.footerText = footerText;
        this.examNotesForTeacher = examNotesForTeacher;
        this.examNotesForStudent = examNotesForStudent;
        this.used = false;
        this.examTime = examTime;
    }

    public int getID() {
        return ID;
    }

    public void setID(int examFormID) {
        this.ID = ID;
    }

    public String getExamFormID() {
        return examFormID;
    }

    public void setExamFormID(String examFormID) {
        this.examFormID = examFormID;
    }

    public String getCode() {
        Faker faker= new Faker();
        if(code == null)
        {
            code = subject.getCode() + course.getCode() + faker.bothify("##");
        }
        return code;
    }

    public void setCode(String code) {
        code = code;
    }

    public Course getCourse(){return course;}
    public void setCourse(Course newCourse){this.course=newCourse;}
    public Teacher getCreator(){return creator;}
    public void setCreator(Teacher newTeacher){this.creator =newTeacher;}

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

    public List<Question> getQuestionList(){return questionList;}
    public void setQuestionList(List<Question> newQuestionList){this.questionList=newQuestionList;}
    public void addQuestion(Question question) {
        questionList.add(question);
    }

    public List<ClassExam> getClassExam(){return classExams;}
    public void setClassExam(List<ClassExam> classExam){this.classExams=classExam;}
    public void addClassExam(ClassExam classExam)
    {
        if (!classExams.contains(classExam))
        {
            this.classExams.add(classExam);
            used = true;
        }
    }

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

    public double getExamTime(){return examTime;}
    public void setExamTime(int examTime){this.examTime=examTime;}
    public void getExamTime(int examTime){this.examTime=examTime;}

    public boolean getUsedStatus() {return used;}
    public void setUsedStatus(boolean used) {this.used = used;}

}


