package il.cshaifasweng.OCSFMediatorExample.entities;

import il.cshaifasweng.OCSFMediatorExample.entities.Grade;

import javax.persistence.*;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Students")
public class Student implements Serializable,Comparable<Student> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ID;
    private String studentName;
    @OneToMany(mappedBy = "student")
    private List<Grade> grades = new ArrayList<>();

    public Student(String studentName) {
        this.studentName = studentName;
    }

    public Student() {

    }
    public Student(Student student) {
        this.ID = student.getID();
        this.grades = student.getGrades();
        this.studentName = student.getStudentName();

    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }

    @Override
    public String toString() {
        return "Student{" +
                "ID=" + ID +
                ", studentName='" + studentName + '\'' +
                ", grades=" + grades +
                '}';
    }

    @Override
    public int compareTo(Student other) {
        return this.studentName.compareTo(other.getStudentName());
    }
}
