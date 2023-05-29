package Entities;

import javax.persistence.*;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "Students")
@DiscriminatorValue("2")
public class Student extends Person implements Serializable, Comparable<Student> {
    @OneToMany(mappedBy = "student")
    private List<Grade> grades = new ArrayList<>();


    @OneToMany(mappedBy = "student")
    private List<StudentExam> studentExams = new ArrayList<>();

    public Student() {}

    public Student(String firstName, String lastName, Gender gender, String email, String password) {
        super(firstName, lastName, gender, email, password);
    }

    public Student(String firstName, String lastName) {
        super(firstName, lastName);
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }

    public List<StudentExam> getStudentExam() {
        return studentExams;
    }

    public void setStudentExam(List<StudentExam> studentExams) {
        this.studentExams = studentExams;
    }

    public void AddStudentExam(StudentExam studentExam){studentExams.add(studentExam);}

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
}
