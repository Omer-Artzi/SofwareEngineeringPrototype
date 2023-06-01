package Entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Grades")
public class Grade implements Serializable,Comparable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ID;
    private int grade;
    private String subject;
    private String course;
    @ManyToOne
    @JoinColumn(name = "studentID")
    private Student student;

    public Grade(int grade, String subject,String course, Student student) {
        this.grade = grade;
        this.subject = subject;
        this.course = course;
        this.student = student;
    }

    public Grade() {
    }

    @Override
    public String toString() {
        return "Grade{" +
                "ID=" + ID +
                ", grade=" + grade +
                ", subject='" + subject + '\'' +
                ", course='" + course + '\'' +
                ", student=" + student +
                '}';
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public int getID() {
        return ID;
    }

    @Override
    public int compareTo(Object other) {
        return this.subject.compareTo(((Grade)other).subject);
    }
}
