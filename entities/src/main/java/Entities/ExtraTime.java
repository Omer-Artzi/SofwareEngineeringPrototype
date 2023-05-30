package Entities;

import javax.persistence.*;
import java.util.List;
@Entity
@Table(name = "ExtraTime")
public class ExtraTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ID;
    private String TeacherNote;
    private  Exam exam;
    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "ExtraTime_ID"),
            inverseJoinColumns = @JoinColumn(name ="Principle_ID"))
    private List<Principle> principles;
    public ExtraTime(){}
    public ExtraTime(Exam exam,List<Principle> principles,String teacherNote)
    {
        this.exam=exam;
        this.principles=principles;
        this.TeacherNote=teacherNote;
    }
    public Exam getExam(){return exam;}
    public void setExam(Exam newExam){this.exam=newExam;}
    public String getNote(){return TeacherNote;}
    public void setNote(String newNote){this.TeacherNote=newNote;}
    public List<Principle> getPrincipals() {
        return this.principles;
    }
    public void setPrinciples(List<Principle> newPrinciples){this.principles=newPrinciples;}
}
