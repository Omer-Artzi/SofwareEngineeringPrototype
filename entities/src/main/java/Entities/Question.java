package Entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="Questions")
public class Question implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ID;

    private String questionID;

    @ManyToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinTable(
    joinColumns = @JoinColumn(name = "QuestionID"),
    inverseJoinColumns = @JoinColumn(name ="CourseID"))
    private List<Course> courses = new ArrayList<>();

    // The question problem text
    private String questionData;
    @ElementCollection
    private List<String> incorrectAnswers;
    private String correctAnswer;
    private String teacherNote;
    private String studentNote;
    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name ="QuestionID" ),
            inverseJoinColumns = @JoinColumn(name = "ExamFormID"))
    private List<ExamForm> examForms = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SubjectID")
    private Subject subject = null;

    public Question(){}

    public Question(List<Course> courses, String questionData, List<String> incorrectAnswers, String correctAnswer, String teacherNote, String studentNote)
    {
        this.courses=courses;
        this.questionData=questionData;
        this.incorrectAnswers = incorrectAnswers;
        this.correctAnswer=correctAnswer;
        this.teacherNote=teacherNote;
        this.studentNote=studentNote;
    }

    public Question(String questionData, String correctAnswer, String teacherNote, String studentNote) {
        this.questionData = questionData;
        this.correctAnswer = correctAnswer;
        this.teacherNote = teacherNote;
        this.studentNote = studentNote;
    }


    public void setQuestionID(String ID)
    {
        this.questionID = ID;
    }
    public String getQuestionID()
    {
        return questionID;
    }

    public List<Course> getCourses(){return courses;}
    public void setCourses(List<Course> courses)
    {
        if (this.courses != courses)
            this.courses = new ArrayList<>(courses);
    }
    public void addSetCourse(Course course)
    {
        if(!this.courses.contains(course))
            courses.add(course);
    }

    public Subject getSubject(){return subject;}
    public void setSubject(Subject subject)
    {
        if (this.subject != subject)
            this.subject = subject;
    }

    public List<ExamForm> getExamForm(){return examForms;}
    public void setExamForm(List<ExamForm> examForms)
    {
        this.examForms = new ArrayList<>(examForms);
    }
    public void addExamForm(ExamForm examForm)
    {
        if(!this.examForms.contains(examForm))
            examForms.add(examForm);
    }


    public String getQuestionData(){return questionData;}
    public void setQuestionData(String questionData){this.questionData=questionData;}
    public List<String> getIncorrectAnswers(){return incorrectAnswers;}
    public void setIncorrectAnswers(List<String> newAnswers){this.incorrectAnswers =newAnswers;}
    public String getCorrectAnswer(){return correctAnswer;}
    public void setCorrectAnswer(String  correctAnswer){this.correctAnswer=correctAnswer;}
    public  String getTeacherNote(){return teacherNote;}
    public void setTeacherNote(String newNote){this.teacherNote=newNote;}
    public String getStudentNote(){return studentNote;}
    public void setStudentNote(String newNote){this.studentNote=newNote;}

    public int getID() {
        return ID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Question)) return false;
        Question question = (Question) o;
        return getID() == question.getID();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID());
    }
}
