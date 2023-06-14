package Entities.Users;

import Entities.SchoolOwned.ClassExam;
import Entities.Communication.ExtraTime;
import Entities.Enums;
import Entities.SchoolOwned.Course;
import Entities.SchoolOwned.Subject;
import Entities.StudentOwned.Grade;
import Entities.StudentOwned.StudentExam;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "Students")
@DiscriminatorValue("2")
public class Student extends Person implements Serializable, Comparable<Student> {

    private String personID;

    @OneToMany(mappedBy = "student",cascade = CascadeType.ALL)
    private List<Grade> grades = new ArrayList<>();
    @ManyToMany(mappedBy = "students",cascade = CascadeType.ALL)
    private List<Subject> subjects = new ArrayList<>();
    @ManyToMany(mappedBy = "students",cascade = CascadeType.ALL)
    private List<Course> courses = new ArrayList<>();
    @ManyToMany(mappedBy = "students",cascade = CascadeType.ALL,fetch = FetchType.EAGER,targetEntity = ClassExam.class)
    private List<ClassExam> classExams = new ArrayList<>();

    @OneToMany(mappedBy = "student",cascade = CascadeType.ALL)
    private List<StudentExam> studentExams = new ArrayList<>();
    public Student() {}

    public Student(String firstName, String lastName, Enums.Gender gender, String email, String password, String personID) {
        super(firstName, lastName, gender, email, password);
        setPersonID(personID);
    }

    public Student(String firstName, String lastName) {
        super(firstName, lastName);
    }


    public void setPersonID(String personID)
    {
        if(personID.chars().allMatch(Character::isDigit))
            this.personID = personID;
    }

    public String getPersonID()
    {
        return personID;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }
    public void addGrade(Grade grade){grades.add(grade);}
    public List<Course> getCourse() {
        return courses;
    }

    public void setCourse(List<Course> courses) {
        this.courses = courses;
    }
    public void addCourse(Course course)
    {
        if(!courses.contains(course))
            courses.add(course);
    }

    public List<StudentExam> getStudentExam() {
        return studentExams;
    }

    public void setStudentExam(List<StudentExam> studentExams) {
        this.studentExams = studentExams;
    }

    public void addStudentExam(StudentExam studentExam){studentExams.add(studentExam);}

    public List<ClassExam> getClassExams() {
        return classExams;
    }

    public void addClassExam(ClassExam classExam){classExams.add(classExam);}

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public int compareTo(Student other) {
        return this.getFirstName().compareTo(other.getFirstName()) + this.getLastName().compareTo(other.getLastName());
    }
    public void extraTimeRequest(ExtraTime data)
    {}

    @Override
    public void receiveExtraTime(ExtraTime data) {
        //TODO: Add extra time
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }


    public void setClassExams(List<ClassExam> classExams) {
        this.classExams = classExams;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;

    }

    public void addSubject(Subject subject) {
        if(!subjects.contains(subject)) {
            subjects.add(subject);
        }
    }
}
