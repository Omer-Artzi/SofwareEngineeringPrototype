package Entities.StudentOwned;

import Entities.Enums;
import Entities.SchoolOwned.ClassExam;
import Entities.SchoolOwned.ExamForm;
import Entities.Users.Student;

import javax.persistence.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "StudentExams")
public class StudentExam implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ID;

    @ManyToOne
    @JoinColumn(name = "Student")
    private Student student;

    @ManyToOne //(cascade =CascadeType.ALL)
    @JoinColumn(name = "ClassExam")
    private ClassExam classExam;

    @ElementCollection
    private List<String> studentAnswers = new ArrayList<>();
    private int grade;

    private Enums.submissionStatus status;

    private String teacherNote;
    private String scoreChangeReason;
    private byte[] ManualExamByteArray;

    public StudentExam() {
    }

    public StudentExam(Student student, ClassExam classExam, List<String> studentAnswers, int grade, Enums.submissionStatus status) {
        this.student = student;
        this.student.addStudentExam(this);
        this.classExam = classExam;
        this.classExam.addStudentExam(this);
        this.studentAnswers = studentAnswers;
        this.grade = grade;
        this.status = status;
    }

    // copy constructor
    public StudentExam(StudentExam other) {
        this.student = other.student;
        this.classExam = other.classExam;
        this.studentAnswers = new ArrayList<>(other.studentAnswers);
        this.grade = other.grade;
        this.status = other.status;
        this.teacherNote = other.teacherNote;
        this.scoreChangeReason = other.scoreChangeReason;
        this.ManualExamByteArray = other.ManualExamByteArray;
    }

    public void update(StudentExam other) {
        this.grade = other.grade;
        this.studentAnswers = new ArrayList<>(other.getStudentAnswers());
        this.status = other.status;
        this.teacherNote = other.teacherNote;
        this.scoreChangeReason = other.scoreChangeReason;
        this.ManualExamByteArray = other.ManualExamByteArray;
    }

    public int getID() {
        return this.ID;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public ClassExam getClassExam() {
        return classExam;
    }

    public void setClassExam(ClassExam classExam) {
        this.classExam = classExam;
    }

    public List<String> getStudentAnswers() {
        return studentAnswers;
    }

    public void setStudentAnswers(List<String> studentAnswers) {
        this.studentAnswers = studentAnswers;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public Enums.submissionStatus getStatus() {
        return status;
    }

    public void setStatus(Enums.submissionStatus status) {
        this.status = status;
    }

    public String TranslateStatus() {
        switch (status) {
            case Approved -> {
                return "Approved";
            }
            case ToEvaluate -> {
                return "To Evaluate";
            }
            case NotTaken -> {
                return "Not Taken";
            }
            case Disapproved -> {
                return "Disapproved";
            }
        }
        return "";
    }

    public String getTeacherNote() {
        return teacherNote;
    }

    public void setTeacherNote(String teacherNote) {
        this.teacherNote = teacherNote;
    }

    public String getScoreChangeReason() {
        return scoreChangeReason;
    }

    public void setScoreChangeReason(String scoreChangeReason) {
        this.scoreChangeReason = scoreChangeReason;
    }

    public byte[] getExamFileByteArray() {
        return ManualExamByteArray;
    }

    public void setExamFileByteArray(byte[] examFile) {
        this.ManualExamByteArray = examFile;
    }

    public void SaveManualExamFileLocally()
    {
        try {
            ExamForm selectedForm = getClassExam().getExamForm();
            String fileName = System.getProperty("user.dir") + "\\src\\main\\ExamToCheck\\Exam_" + selectedForm.getCode() + "_" + selectedForm.getCourse().getName() + ".docx";
            File file = new File(fileName);
            FileOutputStream outputStream = new FileOutputStream(fileName);
            outputStream.write(ManualExamByteArray);
            outputStream.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
