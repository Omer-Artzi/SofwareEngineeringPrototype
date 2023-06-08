package Client.Events;

import Entities.SchoolOwned.Course;

import java.util.List;

public class CoursesOfTeacherEvent {
    private List<Course> courses;
    public CoursesOfTeacherEvent(List<Course> courses) {
        this.courses = courses;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
}