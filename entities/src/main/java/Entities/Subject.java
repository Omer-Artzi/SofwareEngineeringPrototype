package Entities;

import com.github.javafaker.Faker;
import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "Subjects")
public class Subject implements Serializable, Comparable<Subject> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private String name;
    @OneToMany(mappedBy = "subject")
    private List<Course> courses = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "Subject_ID"),
            inverseJoinColumns = @JoinColumn(name ="Student_ID" ))
    private List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "subject")
    private List<ExamForm> examForms = new ArrayList<>();

    // Updated by Ilan 27.5
    @ManyToMany
    @JoinTable(
    joinColumns = @JoinColumn(name = "SubjectID"),
    inverseJoinColumns = @JoinColumn(name ="TeacherID" ))
    private List<Teacher> teachers = new ArrayList<>(); //list of teachers that teaches the course
    private int questionNumber;
    private String code;
    private static int codeNum = 0;

    // New paste, check if works
    @OneToMany(mappedBy = "subject")
    private List<Question> questions = new ArrayList<>();
    @OneToMany(mappedBy = "subject")
    private List<ClassExam> classExams = new ArrayList<>();

    public Subject()
    {
        code = Integer.toString(++codeNum);
        questionNumber = 0;
    }

    public Subject(Long id, String name, List<Course> courses) {
        this.ID = id;
        this.name = name;
        this.courses = courses;
        code = Integer.toString(++codeNum);
        questionNumber = 0;
    }

    public Subject(String name) {
        this.name = name;
        code = Integer.toString(++codeNum);
        questionNumber = 0;
    }

    public Subject(List<Course> courses) {
        this.courses = courses;
        code = Integer.toString(++codeNum);
        questionNumber = 0;
    }

    public void setId(Long id) {
        this.ID = id;
    }

    public Long getId() {
        return ID;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
    public void addSetCourse(Course course)
    {
        if(!this.courses.contains(course))
            courses.add(course);
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = new ArrayList<>(questions);
        questionNumber = questions.size();
    }
    public void addQuestion(Question question)
    {
        if(!this.questions.contains(question)) {
            questions.add(question);
            questionNumber++;
        }
    }

    public List<ExamForm> getExamForms() {
        return examForms;
    }

    public void setExamForms(List<ExamForm> examForms) {
        this.examForms = new ArrayList<>(examForms);
    }
    public void addExamForm(ExamForm examForm)
    {
        if(!this.examForms.contains(examForm)) {
            examForms.add(examForm);
        }
    }


    public List<Teacher> getTeachers(){return teachers;}

    public void setTeachers(List<Teacher> teachers){this.teachers = teachers;}

    public String getCode() {
        if(code == null)
        {
            Faker faker = new Faker();
            code = faker.bothify("##");
        }
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getQuestionNumber() {return questionNumber;}

    @Override
    public int compareTo(Subject o) {
        return this.getName().compareTo(o.getName());
    }
}
