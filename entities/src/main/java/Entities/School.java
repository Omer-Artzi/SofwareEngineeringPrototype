package Entities;

import java.security.Principal;
import java.util.List;

public class School {
    private static School school;
    private static List<Subject> subjects;
    private static List<Principal> principals;

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
}
