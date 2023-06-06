package Entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
@Entity
@Table(name = "ExtraTime")
public class ExtraTime implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ID;
    private String TeacherNote;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Teacher")
    private Teacher teacher;

    @OneToOne
    private  ClassExam exam;
    private String PrincipalNote;
    private int delta;
    private String decision; //TODO: change to enum
    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "ExtraTime_ID"),
            inverseJoinColumns = @JoinColumn(name ="Principal_ID"))
    private List<Principal> principals;
    public ExtraTime(){}
    public ExtraTime(ClassExam exam, List<Principal> principals, Teacher teacher, String teacherNote)
    {
        this.exam=exam;
        this.principals = principals;
        this.teacher=teacher;
        this.TeacherNote=teacherNote;
        this.PrincipalNote="";
        this.delta=0;
        this.decision="";
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getTeacherNote() {
        return TeacherNote;
    }

    public void setTeacherNote(String teacherNote) {
        TeacherNote = teacherNote;
    }

    public String getPrincipalNote() {
        return PrincipalNote;
    }

    public void setPrincipalNote(String principalNote) {
        PrincipalNote = principalNote;
    }

    public int getDelta(){return delta;}

    public void setDelta(int delta){this.delta=delta;}

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public void setPrincipals(List<Principal> principals) {
        this.principals = principals;
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
