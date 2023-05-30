package Entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "Teachers")
@DiscriminatorValue("1")
public class Teacher extends Person{
    // Updated by Ilan 27.5
    @ManyToMany(mappedBy = "teachers")
    private List<Subject> subjects = new ArrayList<>();
    @ManyToMany(mappedBy = "teachers")
    private List<Course> courses = new ArrayList<>();


    public Teacher(String firstName, String lastName, Gender gender, String email, String password, List<Course> course_list, List<Subject> subject_list) {
        super(firstName, lastName, gender, email, password);
        this.courses = course_list;
        this.subjects = subject_list;
    }

    public Teacher(String teacherFirstName, String teacherLastName, String teacherEmail, String password, List<Course> courses, List<Subject> subjects) {

    }

    public List<Course> getCourseList() {
        return courses;
    }

    public List<Subject> getSubjectList() {
        return subjects;
    }

    public void setCourseList(List<Course> CourseList) {
        this.courses = CourseList;
    }

    public void setSubjectList(List<Subject> SubjectList) {
        this.subjects = SubjectList;
    }

    public Teacher() {}
    public void extraTimeRequest(ExtraTime data){}
     public void receiveExtraTime(ExtraTime data)
     {

     }

}
