package Entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

@Entity
@Table(name = "ClassExams")
public class ClassExam implements Serializable
{
    // TODO change ID generation to be calculate by server and be dependent on course and subject
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ID;
    private Date startDate;
    private Date finalSubmissionDate;

    @ManyToOne
    @JoinColumn(name = "Teacher")
    private Teacher tester;

    @ManyToOne
    @JoinColumn(name = "ExamForm")
    private ExamForm examForm;

    @OneToMany(mappedBy = "classExam")
    private List<StudentExam> studentExams = new ArrayList<>();

    @ManyToMany
    @JoinTable(
    joinColumns = @JoinColumn(name = "ClassExamID"),
    inverseJoinColumns = @JoinColumn(name = "StudentID"))
    private List<Student> students = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "Subject")
    private Subject subject;
    @ManyToOne
    @JoinColumn(name = "Course")
    private Course course;



    private int approvedExamsNum;

    private double gradesMean;

    private double gradesVariance;
    // In Minutes
    private double examTime;
    private String code;

    public ClassExam() {
    }

    public ClassExam(ExamForm examForm, Date startDate, Date finalSubmissionDate, double examTime, Teacher tester, String code)
    {
        this.startDate=startDate;
        this.finalSubmissionDate=finalSubmissionDate;
        this.examTime = examTime;
        this.tester = tester;
        this.examForm = examForm;
        tester.addClassExam(this);
        this.approvedExamsNum=0;
        this.gradesMean=0;
        this.gradesVariance=0;
        this.code = code;
    }

    public int getID() {return ID;}

    public List<StudentExam> getStudentExams(){return studentExams;}
    public void setStudentExams(List<StudentExam> studentExams){this.studentExams= new ArrayList<>(studentExams);}
    public void addStudentExam(StudentExam studentExam)
    {
        if (!studentExams.contains(studentExam))
        {
            studentExams.add(studentExam);
        }
    }

    public void UpdateStudentExam(StudentExam studentExam)
    {
        System.out.println("index = " + studentExams.indexOf(studentExam));
        studentExams.set(studentExams.indexOf(studentExam), studentExam);
    }

    public List<Student> getStudents(){return students;}
    public void setSStudents(List<Student> students){this.students= new ArrayList<>(students);}
    public void addStudent(Student student)
    {
        if(!students.contains(student))
        {
            students.add(student);
        }
    }

    public Date getStartDate(){return startDate;}
    public void setStartDate(Date newDate){this.startDate=newDate;}
    public Date getFinalDate(){return finalSubmissionDate;}
    public void setFinalDate(Date newDate){this.finalSubmissionDate=newDate;}

    public ExamForm getExamForm(){return examForm;}
    public void setExamForm(ExamForm examForm){this.examForm=examForm;}

    public Teacher getTeacher(){return tester;}
    public void setTeacher(Teacher tester){this.tester=tester;}

    public int getApprovedExamsNum(){return approvedExamsNum;}
    public void setApprovedExamsNum(int approvedExamsNum){this.approvedExamsNum=approvedExamsNum;}

    public double getGradesMean(){return gradesMean;}
    public void setGradesMean(double gradesMean){this.gradesMean=gradesMean;}

    public double getGradesVariance(){return gradesVariance;}
    public void setGradesVariance(double gradesVariance){this.gradesVariance=gradesVariance;}
    public double getExamTime(){return examTime;}
    public void setExamTime(double examTime){this.examTime=examTime;}

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }

    public Date getFinalSubmissionDate() {
        return finalSubmissionDate;
    }

    public void setFinalSubmissionDate(Date finalSubmissionDate) {
        this.finalSubmissionDate = finalSubmissionDate;
    }

    public Teacher getTester() {
        return tester;
    }

    public void setTester(Teacher tester) {
        this.tester = tester;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }


    public void setID(int ID) {
        this.ID = ID;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
