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


    @OneToMany(mappedBy = "tester")
    private List<ClassExam> classExams = new ArrayList<>();


    public Teacher(String firstName, String lastName, Gender gender, String email, String password, List<Course> course_list, List<Subject> subject_list) {
        super(firstName, lastName, gender, email, password);
        this.courses = course_list;
        this.subjects = subject_list;
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
