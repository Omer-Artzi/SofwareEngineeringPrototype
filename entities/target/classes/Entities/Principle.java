package Entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "Principles")
@DiscriminatorValue("3")
public class Principle extends Person {

    @ManyToMany(mappedBy = "principles")
    private List<ExtraTime> extraTimeRequests = new ArrayList<>();

    //@OneToMany(mappedBy = "principle")
    //private List<Teacher> teachers = new ArrayList<>();


    // In school of principal?
    //@OneToMany(mappedBy = "principle")
    //private List<ClassExam> classExams = new ArrayList<>();

    public Principle(String firstName, String lastName, Gender gender, String email, String password){
        super(firstName, lastName, gender, email, password);
    }

    //public void setTeachers(List<Teacher> teachers)
    //{
    //    teachers = new ArrayList<>(teachers);
    //}
    //
    //public List<Teacher> getTeachers()
    //{
    //    return teachers;
    //}
    //
    //public void addTeacher(Teacher teacher)
    //{
    //    if(!teachers.contains(teacher))
    //    {
    //        teachers.add(teacher);
    //    }
    //}

    //void setClassExams(List<ClassExam> classExams)
    //{
    //    classExams = new ArrayList<>(classExams);
    //}
    //
    //List<ClassExam> getClassExams()
    //{
    //    return classExams;
    //}
    //
    //void addClassExam(ClassExam classExam)
    //{
    //    if(!classExams.contains(classExam))
    //    {
    //        classExams.add(classExam);
    //    }
    //}

    public void extraTimeRequest(ExtraTime data) {
        for(Principle principal:data.getPrincipals()) {
            if(principal.getFullName() == getFullName())
            //TODO: pop message that extra time is requested when conditions are met
            extraTimeRequests.add(data);
        }
    }

    @Override
    public void receiveExtraTime(ExtraTime data) {

    }

}
