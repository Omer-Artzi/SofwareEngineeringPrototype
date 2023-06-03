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

    @OneToMany(mappedBy = "creator")
    private List<ExamForm> examForms = new ArrayList<>();

    @OneToMany(mappedBy = "teacher")
    private List<ExtraTime> extraTimes = new ArrayList<>();


    public Teacher() {}
    public Teacher(String firstName, String lastName, HSTS_Enums.Gender gender, String email, String password, List<Course> course_list, List<Subject> subject_list) {
        super(firstName, lastName, gender, email, password);
        this.courses = course_list;
        this.subjects = subject_list;
    }

    public void setCourses(List<Course> courses) {
        this.courses = new ArrayList<>(courses);
    }
    public List<Course> getCourses() {
        return courses;
    }
    public void addCourse(Course course) {
        if (!courses.contains(course))
        {
            courses.add(course);
            course.addTeacher(this);
        }
    }


    public List<ClassExam> getClassExam() {
        return classExams;
    }

    public void setClassExam(List<ClassExam> classExam) {
        this.classExams = classExam;
    }
    public void addClassExam(ClassExam classExam)
    {
        if (!classExams.contains(classExam))
        {
            this.classExams.add(classExam);
        }
    }

    public List<ExamForm> getExamForm() {
        return examForms;
    }

    public void setExamForm(List<ExamForm> classExam) {
        this.examForms = examForms;
    }
    public void addExamForm(ExamForm examForm)
    {
        if (!examForms.contains(examForm))
        {
            this.examForms.add(examForm);
        }
    }

    public List<Subject> getSubjectList() {
        return subjects;
    }
    public void setSubjects(List<Subject> SubjectList) {
        this.subjects = new ArrayList<>(SubjectList);
    }
    public List<Subject> getSubjects(){return subjects;}
    public void addSubject(Subject subject)
    {
        if(!this.subjects.contains(subject))
            subjects.add(subject);
    }

    public void extraTimeRequest(ExtraTime data){}
     public void receiveExtraTime(ExtraTime data)
     {

     }

}
