package Entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name="questions")
public class Question implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ID;
    @ManyToOne
    @JoinColumn(name = "courseID")
    private Course course;
    private String questionData;
    @ElementCollection
    private List<String> incorrectAnswers;
    private String correctAnswer;
    private String teacherNote;
    private String studentNote;
    private String code;
    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name ="Question_ID" ),
            inverseJoinColumns = @JoinColumn(name = "ExamForm_ID"))
    private List<ExamForm> examForms = new ArrayList<>();

    @ManyToOne
    private Subject subject;
public Question(){}
    public Question(Course course, String questionData,List<String>Answer, String correctAnswer,String teacherNote,String studentNote)
    {
        this.course=course;
        this.questionData=questionData;
        this.incorrectAnswers =Answer;
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

    public Course getCourse(){return course;}
    public void setCourse(Course newCourse){this.course=newCourse;}
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<ExamForm> getExamForms() {
        return examForms;
    }

    public void setExamForms(List<ExamForm> examForms) {
        this.examForms = examForms;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }
}
