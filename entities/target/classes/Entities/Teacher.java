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
    /*
    public static List<Entities.Teacher> generateTeachers() {
        List<Entities.Teacher> teachers = new ArrayList<Entities.Teacher>();
        teachers.add(new Entities.Teacher(1,"Moran","Feldman",Entities.Gender.Male,"Moran@gmail.com","1234","Data Structures"));
        teachers.add(new Entities.Teacher(2,"Dan","Feldman",Entities.Gender.Male,"Dan@gmail.com","1234","OOP"));
        teachers.add(new Entities.Teacher(3,"Ariel","Amsalem",Entities.Gender.Male,"Ariel@gmail.com","1234","Linear Algebra 1"));
        teachers.add(new Entities.Teacher(4,"Shuly","Wintner",Entities.Gender.Male,"Shuly@gmail.com","1234","Intro to CS"));
        teachers.add(new Entities.Teacher(5,"Or","Meir",Entities.Gender.Male,"Orn@gmail.com","1234","Discrete Math"));
        teachers.add(new Entities.Teacher(6,"Rachel","Kolodny",Entities.Gender.Female,"Rachel@gmail.com","1234","Operating Systems"));
        teachers.add(new Entities.Teacher(7,"Malki","Grosman",Entities.Gender.Female,"Malki@gmail.com","1234","SWE"));
        teachers.add(new Entities.Teacher(8,"Shir","Sneh",Entities.Gender.Female,"Shir@gmail.com","1234","SWE"));
        return teachers;
    }*/

}
