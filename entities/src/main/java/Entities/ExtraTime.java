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
    private Teacher teacher;
    private  ClassExam exam;
    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "ExtraTime_ID"),
            inverseJoinColumns = @JoinColumn(name ="Principle_ID"))
    private List<Principle> principles;
    public ExtraTime(){}
    public ExtraTime(ClassExam exam,List<Principle> principles,Teacher teacher,String teacherNote)
    {
        this.teacher=teacher;
        this.exam=exam;
        this.principles=principles;
        this.TeacherNote=teacherNote;
    }
    public ClassExam getExam(){return exam;}
    public void setExam(ClassExam newExam){this.exam=newExam;}
    public Teacher getTeacher(){return teacher;}
    public void setTeacher(Teacher newTeacher){this.teacher=newTeacher;}
    public String getNote(){return TeacherNote;}
    public void setNote(String newNote){this.TeacherNote=newNote;}
    public List<Principle> getPrincipals() {
        return this.principles;
    }
    public void setPrinciples(List<Principle> newPrinciples){this.principles=newPrinciples;}
}
