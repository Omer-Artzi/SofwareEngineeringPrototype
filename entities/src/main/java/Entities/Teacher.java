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

    @OneToMany(mappedBy = "tester")
    private List<ClassExam> classExams = new ArrayList<>();

    public Teacher(Long ID, String firstName, String lastName, Gender gender, String email, String password) {
        super(ID, firstName, lastName, gender, email, password);
    }

    public List<Course> getCourseList() {
        return courses;
    }
    public void AddCourse(Course course) {
        if (!courses.contains(course))
        {
            courses.add(course);
            course.AddTeacher(this);
        }
    }

    public List<ClassExam> getClassExam() {
        return classExams;
    }

    public void setClassExam(List<ClassExam> classExam) {
        this.classExams = classExam;
    }
    public void AddClassExam(ClassExam classExam)
    {
        if (!classExams.contains(classExam))
        {
            this.classExams.add(classExam);
        }

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
