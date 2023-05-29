package Entities;

import com.github.javafaker.Faker;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="Courses")
public class Course implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private String Name;
    @ManyToMany
    @JoinTable(
    joinColumns = @JoinColumn(name = "Course_ID"),
    inverseJoinColumns = @JoinColumn(name ="Teacher_ID"))
    private List<Teacher> teachers; //list of teachers that teaches the course
    @ManyToOne
    @JoinColumn(name = "subjectID")
    private Subject subject;
    @OneToMany(mappedBy = "course")
    private List<Question> questions = new ArrayList<>();
    private String code;
    private static int codeNum = 0;

    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "Course_ID"),
            inverseJoinColumns = @JoinColumn(name = "ExamForm_ID"))
    private List<Entities.ExamForm> examForms = new ArrayList<>();



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
    public List<Teacher> getTeacherList(){return teachers;}
    public void setTeacherList(List<Teacher>TeacherList){this.teachers =TeacherList;}


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

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
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
}
