package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@Entity
@Table(name="Teachers")
public class Teacher implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ID;
    private String firstName;
    private String lastName;
    private String email;
    @ManyToMany(mappedBy = "teachers")
    private List<Course> courses = new ArrayList<>();

    private String password;

    public Teacher(String firstName, String lastName, String email, String password,List<Course> course_list) {
        this.courses = course_list;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public Teacher() {

    }
    public List<Course> getCourseList() {
        return courses;
    }

    public void setCourseList(List<Course> CourseList) {
        this.courses = CourseList;
    }

    public void setId(Long id) {
        this.ID = id;
    }

    public Long getId() {
        return ID;
    }
}

