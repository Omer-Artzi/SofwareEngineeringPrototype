package Server;

import Entities.*;
import Server.Events.ApiResponse;
import Server.Events.ResponseQuestion;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.google.gson.Gson;
import org.hibernate.Session;
import org.mindrot.jbcrypt.BCrypt;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class DataGenerator {
    public static void generateData() throws IOException {
         List<Student> students = DataGenerator.generateStudents();
         Faker faker = new Faker();
        generateGrades(students);
        School school = School.getInstance();
        ObjectMapper objectMapper = new ObjectMapper();
        SubjectWrapper subjects = objectMapper.readValue(new File("./src/main/resources/Server/SchoolSubjects.json"), SubjectWrapper.class);
        school.setSubjects(subjects.getSubjects());
        try {
            for (Subject subject : subjects.getSubjects()) {
                SimpleServer.session.saveOrUpdate(subject);
                for (Course course : subject.getCourses()) {
                    course.setSubject(subject);
                    SimpleServer.session.saveOrUpdate(course);
                }
                SimpleServer.session.flush();
            }
            List<Subject> subjectList = subjects.getSubjects();
            generateTeachers(subjectList);
            generateQuestions(subjectList);
            SimpleServer.session.getTransaction().commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    private static void generateQuestions(List<Subject> subjectList) {
        int questionAmount = 30;
        String[] requests = {
                "https://opentdb.com/api.php?amount="+ questionAmount + "&category=9&type=multiple",
                "https://opentdb.com/api.php?amount="+ questionAmount + "&category=17&type=multiple",
                "https://opentdb.com/api.php?amount="+ questionAmount + "&category=24&type=multiple",
                "https://opentdb.com/api.php?amount="+ questionAmount + "&category=23&type=multiple",
                "https://opentdb.com/api.php?amount="+ questionAmount + "&category=20&type=multiple",
                "https://opentdb.com/api.php?amount="+ questionAmount + "&category=26&type=multiple",
                "https://opentdb.com/api.php?amount="+ questionAmount + "&category=19&type=multiple",
                "https://opentdb.com/api.php?amount="+ questionAmount + "&category=27&type=multiple",
                "https://opentdb.com/api.php?amount="+ questionAmount + "&category=24&type=multiple",
                "https://opentdb.com/api.php?amount="+ questionAmount + "&category=18&type=multiple",};
        Random random = new Random();
        int randCourse;
        for (int i = 0; i < requests.length;i++) {
            try {

                // Create URL object and open connection
                URL url = new URL(requests[i]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Set request method
                connection.setRequestMethod("GET");

                // Get response code
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Process JSON response
                    String jsonResponse = response.toString();

                    // Parse JSON response
                    Gson gson = new Gson();
                    ApiResponse apiResponse = gson.fromJson(response.toString(), ApiResponse.class);
                    List<ResponseQuestion> questions = apiResponse.getResults();
                    List<Question> questionsList = new ArrayList<>();
                    for(ResponseQuestion responseQuestion: questions)
                    {
                        Question question = new Question();
                        responseQuestion.convert(question);
                        question.setCourse(subjectList.get(i).getCourses().get(0));
                        questionsList.add(question);
                        SimpleServer.session.save(question);
                        SimpleServer.session.flush();
                    }
                    //System.out.println(questions);
                    generateTestForms(questionsList);


                } else {
                    System.out.println("Error: " + responseCode);
                }

                connection.disconnect();

            } catch(IOException e){
                e.printStackTrace();
            }

        }
    }
    private static void generateTestForms(List<Question> questionsList) {
        if(questionsList != null) {
            for(int  i = 0; i < 3; i++) {
                ExamForm examForm = new ExamForm();
                for(int  j = 0; j < 10;j++) {
                    examForm.addQuestion(questionsList.get((i * 10) + j));
                }
                List<Question> examQuestions =  examForm.getQuestionList();
                Course examCourse = examQuestions.get(0).getCourse();
                Subject examSubject = examCourse.getSubject();
                examForm.setQuestionList(examQuestions);
                examForm.setSubject(examSubject);
                examForm.setCourse(examCourse);
                examForm.setCreator(examCourse.getTeacherList().get(0));
                LocalDate localDate = LocalDate.now();
                examForm.getCode();
                // Convert LocalDate to Date
                Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                examForm.setDateCreated(date);
                examForm.setLastUsed(date);
                SimpleServer.session.saveOrUpdate(examForm);
            }
            SimpleServer.session.flush();
        }
        else {
            System.out.println("No question Retrieved");
        }

    }
    private static void generateTeachers(List<Subject> subjects) {
        try {
            Teacher admin = new Teacher();
            String salt = BCrypt.gensalt();
            admin.setEmail("admin");
            admin.setPassword(BCrypt.hashpw("1234", salt));
            admin.setGender(Gender.Female);
            admin.setFirstName("super");
            admin.setLastName("user");
            SimpleServer.session.saveOrUpdate(admin);
            SimpleServer.session.flush();
            HashSet<Subject> tempSubjects = new HashSet<Subject>();
            HashSet<Course> tempCourses = new HashSet<Course>();
            Faker faker = new Faker();
            Random random = new Random();
            int randomSubject, randomCourse;
            for (int i = 0; i < 50; i++) {
                String teacherFirstName = faker.name().firstName();
                String teacherLastName = faker.name().lastName();
                String teacherEmail = teacherFirstName + "_" + teacherLastName + "@gmail.com";
                String password = BCrypt.hashpw(faker.internet().password(), salt);
                List<Course> coursesList = new ArrayList<>();
                List<Subject> subjectsList = new ArrayList<>();
                for (int j = 0; j < 5; j++) {
                    randomSubject = random.nextInt(subjects.size());
                    Subject subject = subjects.get(randomSubject);
                    subjectsList.add(subject);
                    for (int k = 0; k < 5; k++) {
                        randomCourse = random.nextInt(subject.getCourses().size());
                        coursesList.add(subject.getCourses().get(randomCourse));
                    }
                }
                Teacher teacher = new Teacher(teacherFirstName, teacherLastName, Gender.Male, teacherEmail, password, coursesList, subjectsList);
                for (Course course : coursesList) {
                    course.getTeachers().add(teacher);
                    if(!(tempCourses.contains(course))){
                        tempCourses.add(course);
                        course.getTeachers().add(admin);
                    }
                }
                for (Subject subject : subjectsList) {
                    subject.getTeachers().add(teacher);
                    if(!(tempSubjects.contains(subject))){
                        tempSubjects.add(subject);
                        subject.getTeachers().add(admin);
                    }
                }
                SimpleServer.session.saveOrUpdate(teacher);
            }
            SimpleServer.session.flush();
            List<Course> allCourses = new ArrayList<>();
            List<Subject> allSubjects = new ArrayList<>();
            allSubjects.addAll(tempSubjects);
            allCourses.addAll(tempCourses);
            admin.setSubjectList(allSubjects);
            admin.setCourseList(allCourses);
            SimpleServer.session.saveOrUpdate(admin);
            SimpleServer.session.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    private static void generateGrades(List<Student> students) {
        Faker faker = new Faker();
        Random r = new Random();
        for(Student student : students)
        {
            for(int i = 0; i < 8;i++ ) {
                Grade grade = new Grade(r.nextInt(100),faker.educator().course(),faker.pokemon().name() , student);
                student.getGrades().add(grade);
                SimpleServer.session.save(grade);
            }
        }
        SimpleServer.session.flush();
    }
    public static List<Student> generateStudents() {
        Faker faker = new Faker();
        List<Student> students = new ArrayList<>();
        for(int  i = 0; i < 10;i++)
        {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            Student student = new Student(firstName,lastName);
            students.add(student);
            SimpleServer.session.save(student);
            SimpleServer.session.flush();
        }
        return  students;
    }


    private static LocalDate GenerateRandomDate(LocalDate startDate, LocalDate endDate) {
        long startEpochDay = startDate.toEpochDay();
        long endEpochDay = endDate.toEpochDay();
        long randomEpochDay = ThreadLocalRandom.current().nextLong(startEpochDay, endEpochDay);
        return LocalDate.ofEpochDay(randomEpochDay);
    }

    private static Date ConvertToDate(LocalDate localDate) {
        LocalDateTime localDateTime = localDate.atStartOfDay();
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        return Date.from(zonedDateTime.toInstant());
    }
    private static void GenerateClassExams(List<ExamForm> ExamForms, Session session)
    {
        Faker faker = new Faker();
        if(ExamForms != null) {
            // generate list of Class Exam
            for(int i = 0; i < 3; i++) {

                // covert random date in the next 5 mouths to perform the test
                LocalDate currentDate = LocalDate.now();
                LocalDate randomDate = GenerateRandomDate(currentDate, currentDate.plusMonths(5));
                Date testDate = ConvertToDate(randomDate);
                Teacher teacher =  SimpleServer.retrieveTeachers().get(0);
                ClassExam classExam = new ClassExam(ExamForms.get(i), testDate.toString(), teacher);

                teacher.AddCourse(ExamForms.get(i).getCourse());
                session.saveOrUpdate(teacher);
                session.flush();

                // Generate for every Class Exam a list of studentExams
                List<Student> studentsTemp = new ArrayList<>(SimpleServer.retrieveStudents());
                int numberOfElements = faker.number().numberBetween(3,  studentsTemp.size());
                List<StudentExam> StudentExams = new ArrayList<>();
                for (int examineeNum = 0; examineeNum < numberOfElements; examineeNum++) {
                    int randomIndex = faker.number().numberBetween(0,  studentsTemp.size());
                    Student randomStudent = studentsTemp.get(randomIndex);
                    studentsTemp.remove(randomIndex);
                    List<Integer> studentAnswers =  new ArrayList<>();
                    for(int  j = 0; j < classExam.getExamForm().getQuestionList().size() ;j++) {
                        studentAnswers.add(faker.number().numberBetween(1, 5));
                    }
                    String status = "";
                    if (faker.number().numberBetween(0, 1) == 1)
                        status = "Approved";
                    else
                        status = "To Evaluate";
                    //status = "To Evaluate";
                    StudentExam currentExam = new StudentExam(randomStudent, classExam, studentAnswers, -1, status);
                    StudentExams.add(currentExam);

                }
                session.saveOrUpdate(classExam);
                session.flush();

                for(int examineeNum = 0; examineeNum < StudentExams.size() ;examineeNum++) {
                    session.saveOrUpdate(StudentExams.get(examineeNum));
                    session.flush();
                }
            }
        }
        else {
            System.out.println("No Exam Form Retrieved");
        }
    }

}


