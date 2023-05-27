package Server;

import Entities.*;
import Server.Events.ApiResponse;
import Server.Events.ResponseQuestion;
import Server.SimpleServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.google.gson.Gson;
import javassist.Loader;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class DataGenerator {

    static Faker faker = new Faker();
    static Random random = new Random();

    public static void generateData(Session session) throws IOException {
        GenerateStudents(session);
        GenerateGrades(session);
        School school = School.getInstance();
        ObjectMapper objectMapper = new ObjectMapper();
        SubjectWrapper subjects = objectMapper.readValue(new File("./src/main/resources/Server/SchoolSubjects.json"), SubjectWrapper.class);
        school.setSubjects(subjects.getSubjects());
        try {
            for (Subject subject : subjects.getSubjects()) {
                session.saveOrUpdate(subject);
                for (Course course : subject.getCourses()) {
                    course.setSubject(subject);
                    session.saveOrUpdate(course);
                }
                session.flush();
            }
            List<Subject> subjectList = subjects.getSubjects();
            GenerateTeachers(subjectList, session);
            GenerateQuestions(subjectList, session);
            GenerateClassExams(SimpleServer.retrieveExamForm(), session);
            session.getTransaction().commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void GenerateQuestions(List<Subject> subjectList, Session session) {
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
                        // TODO: Change Incorrect_answers to answers in ResponseQuestion
                        Question question = new Question();
                        responseQuestion.convert(question);
                        List<String> ans = responseQuestion.getIncorrect_answers();
                        ans.add(faker.number().numberBetween(0, 4), responseQuestion.getCorrect_answer());
                        question.setAnswers(ans);
                        question.setCourse(subjectList.get(i).getCourses().get(0));

                        questionsList.add(question);
                        session.save(question);
                        session.flush();
                    }
                    GenerateTestForms(questionsList, session);

                } else {
                    System.out.println("Error: " + responseCode);
                }

                // Close the connection
                connection.disconnect();

            } catch(IOException e){
                e.printStackTrace();
            }

        }
    }

    public static void GenerateTestForms(List<Question> questionsList, Session session) {
        if(questionsList != null) {
            for(int  i = 0; i < 3; i++) {
                ExamForm examForm = new ExamForm();
                int questionNum = 10;
                for(int  j = 0; j < questionNum;j++) {
                    examForm.addQuestion(questionsList.get((i * 10) + j));
                    examForm.AddQuestionsScores((int)(100 / questionNum));
                }
                List<Question> examQuestions = examForm.getQuestionList();
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
                session.saveOrUpdate(examForm);
            }
            session.flush();
        }
        else {
            System.out.println("No question Retrieved");
        }

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
                    if (faker.number().numberBetween(0, 2) == 1)
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


    public static void GenerateTeachers(List<Subject> subjects, Session session) {
        try {
            List<Long> IDS = SimpleServer.retrieveIDs();
            String salt = BCrypt.gensalt();
            int randomSubject, randomCourse;
            for (int i = 0; i < 50; i++) {
                String teacherFirstName = faker.name().firstName();
                String teacherLastName = faker.name().lastName();
                String teacherEmail = teacherFirstName + "_" + teacherLastName + "@gmail.com";
                String password = BCrypt.hashpw(faker.internet().password(), salt);
                List<Course> courses = new ArrayList<>();
                for (int j = 0; j < 5; j++) {
                    randomSubject = random.nextInt(subjects.size());
                    Subject subject = subjects.get(randomSubject);
                    for (int k = 0; k < 5; k++) {
                        randomCourse = random.nextInt(subject.getCourses().size());
                        courses.add(subject.getCourses().get(randomCourse));
                    }
                }

                Long ID = faker.number().randomNumber(9, false);
                while (IDS.contains(ID))
                {
                    ID = faker.number().randomNumber(9, false);
                }
                IDS.add(ID);
                Teacher teacher = new Teacher(ID, teacherFirstName, teacherLastName, Gender.Male, teacherEmail, password);
                if(i == 0)
                {
                    teacher.setEmail("admin");
                    teacher.setPassword(BCrypt.hashpw("1234", salt));
                    teacher.setCourseList(new ArrayList<>());
                    teacher.setGender(Gender.Female);
                    teacher.setFirstName("super");
                    teacher.setLastName("user");
                }
                for (Course course : courses) {
                    teacher.AddCourse(course);
                }

                session.saveOrUpdate(teacher);

            }
            session.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    //Generating grades and saving in the SQL server
    public static void GenerateGrades(Session session) {
        List<Student> students = SimpleServer.retrieveStudents();

        for(Student student : students)
        {
            for(int i = 0; i < 8;i++ ) {
                Grade grade = new Grade(random.nextInt(100),faker.educator().course(),faker.pokemon().name() , student);
                student.getGrades().add(grade);
                session.save(grade);
            }
        }
        session.flush();
    }


    public static void GenerateStudents(Session session) {
        List<Long> IDS = SimpleServer.retrieveIDs();
        for(int  i = 0; i < 10;i++)
        {
            Long ID = faker.number().randomNumber(9, false);
            while (IDS.contains(ID))
            {
                ID = faker.number().randomNumber(9, false);
            }
            IDS.add(ID);
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            Student student = new Student(ID, firstName,lastName);
            session.save(student);
            session.flush();
        }
    }
}
