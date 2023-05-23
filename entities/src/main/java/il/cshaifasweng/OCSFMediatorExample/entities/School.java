package il.cshaifasweng.OCSFMediatorExample.entities;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

public class School {
    private static School school;
    private static List<Subject> subjects;
    private static List<Principal> principals;

    private School() {}

        public static School getInstance() {
        if(school == null)
        {
            school = new School();
        }
        return  school;
    }

    public static void setSubjects(List<Subject> subjects) {
        School.subjects = subjects;
    }

    public static List<Subject> getSubjects() {
        return subjects;
    }
}
