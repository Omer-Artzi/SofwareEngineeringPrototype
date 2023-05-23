package il.cshaifasweng.OCSFMediatorExample.entities;

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
    /*
    public static List<Teacher> generateTeachers() {
        List<Teacher> teachers = new ArrayList<Teacher>();
        teachers.add(new Teacher(1,"Moran","Feldman",Gender.Male,"Moran@gmail.com","1234","Data Structures"));
        teachers.add(new Teacher(2,"Dan","Feldman",Gender.Male,"Dan@gmail.com","1234","OOP"));
        teachers.add(new Teacher(3,"Ariel","Amsalem",Gender.Male,"Ariel@gmail.com","1234","Linear Algebra 1"));
        teachers.add(new Teacher(4,"Shuly","Wintner",Gender.Male,"Shuly@gmail.com","1234","Intro to CS"));
        teachers.add(new Teacher(5,"Or","Meir",Gender.Male,"Orn@gmail.com","1234","Discrete Math"));
        teachers.add(new Teacher(6,"Rachel","Kolodny",Gender.Female,"Rachel@gmail.com","1234","Operating Systems"));
        teachers.add(new Teacher(7,"Malki","Grosman",Gender.Female,"Malki@gmail.com","1234","SWE"));
        teachers.add(new Teacher(8,"Shir","Sneh",Gender.Female,"Shir@gmail.com","1234","SWE"));
        return teachers;
    }*/

}
