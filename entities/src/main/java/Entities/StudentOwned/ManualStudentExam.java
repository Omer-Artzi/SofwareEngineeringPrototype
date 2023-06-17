package Entities.StudentOwned;

import Entities.SchoolOwned.ExamForm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public byte[] getExamFileByteArray() {
        return examFile;
    }

    public void setExamFileByteArray(byte[] examFile) {
        this.examFile = examFile;
    }

    public void SaveManualExamFileLocally()
    {
        try {
            ExamForm selectedForm = studentExam.getClassExam().getExamForm();
            String fileName = System.getProperty("user.dir") + "\\src\\main\\ExamToCheck\\Exam_" + selectedForm.getExamFormID() + "_" + selectedForm.getCourse().getName() + ".docx";
            File file = new File(fileName);
            FileOutputStream outputStream = new FileOutputStream(fileName);
            outputStream.write(examFile);
            outputStream.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
