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
            inverseJoinColumns = @JoinColumn(name ="Principal_ID"))
    private List<Principal> principals;
    public ExtraTime(){}
    public ExtraTime(ClassExam exam, List<Principal> principals, Teacher teacher, String teacherNote)
    {
        this.teacher=teacher;
        this.exam=exam;
        this.principals = principals;
        this.TeacherNote=teacherNote;
    }
    public ClassExam getExam(){return exam;}
    public void setExam(ClassExam newExam){this.exam=newExam;}
    public Teacher getTeacher(){return teacher;}
    public void setTeacher(Teacher newTeacher){this.teacher=newTeacher;}
    public String getNote(){return TeacherNote;}
    public void setNote(String newNote){this.TeacherNote=newNote;}
    public List<Principal> getPrincipals() {
        return this.principals;
    }
    public void setPrinciples(List<Principal> newPrincipals){this.principals = newPrincipals;}
}
