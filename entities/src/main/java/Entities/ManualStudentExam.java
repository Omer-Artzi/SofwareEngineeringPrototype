package Entities;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.Serializable;

public class ManualStudentExam implements Serializable {

    private StudentExam studentExam;
    private DocumentWrapper examFile;

    public ManualStudentExam(StudentExam studentExam, DocumentWrapper examFile) {
        this.studentExam = studentExam;
        this.examFile = examFile;
    }

    public StudentExam getStudentExam() {
        return studentExam;
    }

    public void setStudentExam(StudentExam studentExam) {
        this.studentExam = studentExam;
    }

    public DocumentWrapper getExamFile() {
        return examFile;
    }

    public void setExamFile(DocumentWrapper examFile) {
        this.examFile = examFile;
    }
}
