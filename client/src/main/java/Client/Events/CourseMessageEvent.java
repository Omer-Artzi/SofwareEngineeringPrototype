package Client.Events;

import Entities.SchoolOwned.Course;

import java.util.List;

public class CourseMessageEvent {

    private List<Course> courses;

    public CourseMessageEvent(List<Course> courseList) {
        this.courses = courseList;

    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setSubjects(List<Course> courses) {
        this.courses = courses;
    }
}

