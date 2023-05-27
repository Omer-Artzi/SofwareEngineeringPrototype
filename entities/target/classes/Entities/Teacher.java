package Entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "Teachers")
@DiscriminatorValue("1")
public class Teacher extends Person{
    @ManyToMany(mappedBy = "teachers")
    private List<Course> courses = new ArrayList<>();
    public Teacher(String firstName, String lastName, Gender gender, String email, String password, List<Course> course_list) {
        super(firstName, lastName, gender, email, password);
    }

    public Teacher(String teacherFirstName, String teacherLastName, String teacherEmail, String password, List<Course> courses) {

    }

    public List<Course> getCourseList() {
        return courses;
    }

    public void setCourseList(List<Course> CourseList) {
        this.courses = CourseList;
    }

    public Teacher() {}
    public void extraTimeRequest(ExtraTime data){};
     public void receiveExtraTime(ExtraTime data)
     {

     }

}
