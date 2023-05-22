package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name="Questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ID;
    @ManyToOne
    @JoinColumn(name = "courseID")
    private Course course;
    @ManyToOne
    @JoinColumn(name = "subjectID")
    private  Subject subject;
    private String questionData;
    @ElementCollection
    private List<String> Answers;
    private String correctAnswer;
    private String teacherNote;
    private String studentNote;
    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name ="Question_ID" ),
            inverseJoinColumns = @JoinColumn(name = "ExamForm_ID"))
    private List<ExamForm> examForms = new ArrayList<>();
public Question(){}
    public Question(Course course, Subject subject, String questionData,List<String>Answer, String correctAnswer,String teacherNote,String studentNote)
    {
        this.course=course;
        this.subject=subject;
        this.questionData=questionData;
        this.Answers=Answer;
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
    public Subject getSubject(){return subject;}
    public void setSubject(Subject newSubject){this.subject=newSubject;}
    public String getQuestionData(){return questionData;}
    public void setQuestionData(String questionData){this.questionData=questionData;}
    public List<String> getAnswers(){return Answers;}
    public void setAnswers(List<String> newAnswers){this.Answers=newAnswers;}
    public String getCorrectAnswer(){return correctAnswer;}
    public void setCorrectAnswer(String  correctAnswer){this.correctAnswer=correctAnswer;}
    public  String getTeacherNote(){return teacherNote;}
    public void setTeacherNote(String newNote){this.teacherNote=newNote;}
    public String getStudentNote(){return studentNote;}
    public void setStudentNote(String newNote){this.studentNote=newNote;}

}
