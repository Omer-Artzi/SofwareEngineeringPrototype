package Entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name="ClassExams")
public class ClassExam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "ClassExam_ID"),
            inverseJoinColumns = @JoinColumn(name = "Student_ID"))
    private List<Student> students = new ArrayList<>();

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
}
