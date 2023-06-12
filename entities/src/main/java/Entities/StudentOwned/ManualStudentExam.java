package Entities.StudentOwned;

import java.io.Serializable;

public class ManualStudentExam implements Serializable {

    private StudentExam studentExam;
    private byte[] examFile;

    public ManualStudentExam(StudentExam studentExam, byte[] examFile) {
        this.studentExam = studentExam;
        this.examFile = examFile;
    }

    // copy constructor
    public ManualStudentExam(ManualStudentExam other) {
        this.studentExam = other.studentExam;
        this.examFile = other.examFile;
    }

    public StudentExam getStudentExam() {
        return studentExam;
    }

    public void setStudentExam(StudentExam studentExam) {
        this.studentExam = studentExam;
    }

    public byte[] getExamFile() {
        return examFile;
    }

    public void setExamFile(byte[] examFile) {
        this.examFile = examFile;
    }
}
