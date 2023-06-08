package Entities.SchoolOwned;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

public class School {
    private static School school=null;
    private static List<Subject> subjects = new ArrayList<>();
    private static List<Principal> principals = new ArrayList<>();
    private List<ClassExam> classExams = new ArrayList<>();

    // plus class exam list

    private School() {}

        public static School getInstance() {
        if(school == null)
        {
            school = new School();
        }
        return school;
    }

    public static void setSubjects(List<Subject> subjects) {
        School.subjects = subjects;
    }

    public static List<Subject> getSubjects() {
        return subjects;
    }

    public static School getSchool() {
        return school;
    }

    public static List<Principal> getPrincipals() {
        return principals;
    }

    public List<ClassExam> getClassExams() {
        return classExams;
    }

    public static void setSchool(School school) {
        School.school = school;
    }

    public static void setPrincipals(List<Principal> principals) {
        School.principals = principals;
    }

    public void setClassExams(List<ClassExam> classExams) {
        this.classExams = classExams;
    }
}
