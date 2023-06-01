package Entities;

import com.github.javafaker.Faker;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="Courses")
public class Course implements Serializable, Comparable<Course> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private String Name;
    //list of teachers that teaches the course
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
    joinColumns = @JoinColumn(name = "Course_ID"),
    inverseJoinColumns = @JoinColumn(name ="Teacher_ID"))
    private List<Teacher> teachers;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            joinColumns = @JoinColumn(name = "CourseID"),
            inverseJoinColumns = @JoinColumn(name = "StudentID"))
    private List<Student> students = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "subjectID")
    private Subject subject;

    @ManyToMany(mappedBy = "courses")
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<ExamForm> examForms = new ArrayList<>();

    private String code;
    private static int codeNum = 0;

    public Course(){
        code = Integer.toString(++codeNum);
    }
    public Course(String Name,List<Teacher>TeacherList)
    {
        this.Name=Name;
        this.teachers =TeacherList;
        code = Integer.toString(++codeNum);;
    }

    public Course(String name) {
        code = Integer.toString(++codeNum);
    }


    public String getName(){return Name;}
    public void setName(String newName){this.Name=newName;}

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
    }

    public void AddTeacher(Teacher teacher)
    {
        if (!teachers.contains(teacher))
            teachers.add(teacher);
    }

    public List<Student> getStudents(){return students;}
    public void setStudents(List<Student> students){this.students =students;}

    public void AddStudents(Student student)
    {
        if (!students.contains(student))
            students.add(student);
    }

    public List<Question> getQuestions(){return questions;}
    public void setQuestions(List<Question> students){this.questions =questions;}

    public void AddQuestion(Question question)
    {
        if (!questions.contains(question))
            questions.add(question);
    }

    public List<ExamForm> getExamForms(){return examForms;}
    public void setExamForms(List<ExamForm> examForms){this.examForms = examForms;}

    public void AddExamForm(ExamForm examForm)
    {
        if (!examForms.contains(examForm))
            examForms.add(examForm);
    }


    public void setId(Long id) {
        this.ID = id;
    }

    public Long getId() {
        return ID;
    }

    @Override
    public String toString() {
        return Name;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public String getCode() {

        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public int compareTo(Course o) {
        return this.getName().compareTo(o.getName());
    }
}
