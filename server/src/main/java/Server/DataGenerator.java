package Server;

import Entities.SchoolOwned.ClassExam;
import Entities.SchoolOwned.ExamForm;
import Entities.Enums;
import Entities.SchoolOwned.Course;
import Entities.SchoolOwned.Question;
import Entities.SchoolOwned.School;
import Entities.SchoolOwned.Subject;
import Entities.StudentOwned.StudentExam;
import Entities.Wrappers.SubjectWrapper;
import Entities.Users.Principal;
import Entities.Users.Student;
import Entities.Users.Teacher;
import Server.Events.ApiResponse;
import Server.Events.ResponseQuestion;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.google.gson.Gson;
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
import java.util.stream.Collectors;

public class DataGenerator {
    static Faker faker = new Faker();
    static Random rand = new Random();
    public static void generateData() throws IOException {
       System.out.println("Generating Subjects and Courses");
        List<Subject> subjects = generateSubjectsAndCourses();
        System.out.println("Generating Students");
        assert subjects != null;
        List<Student> students = generateStudents(subjects.get(0).getCourses());
        System.out.println("Generating Teachers");
        generateTeachers(subjects);
        System.out.println("Generating Questions");
        List<Question> questionsList = generateQuestions(subjects);
        System.out.println("Generating Test Forms");
        List<ExamForm> examFormList = generateTestForms(questionsList);
        System.out.println("Generating Class Exams");
        List<ClassExam> classExams = generateClassExams(examFormList,students);
        System.out.println("Generating Student Exams");
        List<StudentExam> studentExams = generateStudentExams(classExams, students);
        assert classExams != null;
        System.out.println("Generating Principals");
        generatePrincipals();
        SimpleServer.session.getTransaction().commit();
        System.out.println("Generation Done");

    }
    private static List<Subject>  generateSubjectsAndCourses() throws IOException {
        School school = School.getInstance();
        ObjectMapper objectMapper = new ObjectMapper();
        SubjectWrapper subjects = objectMapper.readValue(new File("./src/main/resources/Server/SchoolSubjects.json"), SubjectWrapper.class);
        School.setSubjects(subjects.getSubjects());
        try {
            for (Subject subject : subjects.getSubjects()) {
                for(Course course:subject.getCourses())
                {
                    course.setSubject(subject);
                    subject.addSetCourse(course);
                }
                SimpleServer.session.save(subject);
                SimpleServer.session.flush();
            }

            return subjects.getSubjects();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

    }

    private static List<Question> generateQuestions(List<Subject> subjectList) {
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
        List<Question> questionsList = new ArrayList<>();
        for (int i = 0; i < requests.length; i++) {
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

                    for(ResponseQuestion responseQuestion: questions)
                    {
                        Question question = new Question();
                        responseQuestion.convert(question);

                        // question subjects link
                        Subject subject = subjectList.get(i);
                        question.setSubject(subject);
                        String QuestionID = OperationUtils.IDZeroPadding(subject.getId().toString(), 2)
                                + OperationUtils.IDZeroPadding(Integer.toString(subject.getQuestionNumber()), 3);
                        question.setQuestionID(QuestionID);
                        subject.addQuestion(question);

                        // question courses link
                        List<Course> courses = subjectList.get(i).getCourses();
                        question.setCourses(courses);
                        questionsList.add(question);
                        SimpleServer.session.save(question);
                        SimpleServer.session.flush();
                    }


                }
                else
                {
                    System.out.println("Error: " + responseCode);
                    connection.disconnect();
                    //return null;
                }

            }
            catch(IOException e)
            {
                e.printStackTrace();
                return null;
            }

        }
        return questionsList;
    }
    private static List<ExamForm> generateTestForms(List<Question> questionsList) {
        if(questionsList != null) {
            Teacher teacher = SimpleServer.retrieveTeachers().get(0);
            List<ExamForm> examForms = new ArrayList<>();
            for(int  i = 0; i < 30; i++) {
                ExamForm examForm = new ExamForm();
                for(int  j = 0; j < 10;j++) {
                    examForm.addQuestion(questionsList.get((i * 10) + j));
                    examForm.AddQuestionsScores(10);
                }
                // examForm - question link
                List<Question> examQuestions = examForm.getQuestionList();

                // examForm - subject link
                Course examCourse = examQuestions.get(0).getCourses().get(0);
                Subject examSubject = examCourse.getSubject();
                examForm.setSubject(examSubject);
                //examSubject.addExamForm(examForm);

                // examForm - course link
                examForm.setCourse(examCourse);
                examCourse.addExamForm(examForm);

                // examForm - teacher link
                examForm.setCreator(teacher);
                teacher.addExamForm(examForm);

                examForm.getCode();


                Date date = ConvertToDate(LocalDateTime.now());
                //Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                examForm.setDateCreated(date);
                examForm.setLastUsed(date);


                String examFormID = OperationUtils.IDZeroPadding(Long.toString(examSubject.getId()), 2)
                        + OperationUtils.IDZeroPadding(Long.toString(examCourse.getId()), 2) +
                        OperationUtils.IDZeroPadding(Long.toString(examCourse.getExamFormsNumber()), 2);

                examForm.setExamFormID(examFormID);
                examForms.add(examForm);

                SimpleServer.session.saveOrUpdate(examForm);
            }
            SimpleServer.session.flush();
            return examForms;
        }
        else {
            System.out.println("No question Retrieved");
            return null;
        }

    }
    private static void generateTeachers(List<Subject> subjects) {
        try {
            String salt = BCrypt.gensalt();
            Teacher admin = null;
            HashSet<Subject> tempSubjects = new HashSet<Subject>();
            HashSet<Course> tempCourses = new HashSet<Course>();

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
                Teacher teacher = new Teacher(teacherFirstName, teacherLastName, Enums.Gender.Male, teacherEmail, password, coursesList, subjectsList);
                if(i == 0)
                {
                    teacher.setEmail("admin");
                    teacher.setPassword(BCrypt.hashpw("1234", salt));
                    teacher.setGender(Enums.Gender.Female);
                    teacher.setFirstName("super");
                    teacher.setLastName("user");
                    admin = teacher;
                }
                for (Course course : coursesList) {
                    teacher.addCourse(course);
                    course.getTeachers().add(teacher);
                    if(!(tempCourses.contains(course))){
                        tempCourses.add(course);
                        course.getTeachers().add(admin);
                    }
                }

                for (Subject subject : subjectsList) {
                    teacher.addSubject(subject);
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
            admin.setSubjects(allSubjects);
            admin.setCourses(allCourses);
            SimpleServer.session.saveOrUpdate(admin);
            //SimpleServer.session.saveOrUpdate(principal);
            SimpleServer.session.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    private static List<Principal> generatePrincipals() {
        try {
            List<Principal> principals = new ArrayList<>();
            String salt = BCrypt.gensalt(); // TODO
            Principal admin = new Principal("PrincipalFirstName", "PrincipalLastName", Enums.Gender.Male, "admin1", "admin2");
            admin.setEmail("adminP");
            admin.setPassword(BCrypt.hashpw("1234", salt)); // TODO
            //admin.setPassword("1234");
            admin.setGender(Enums.Gender.Female);
            SimpleServer.session.save(admin);
            principals.add(admin);
            SimpleServer.session.flush();
            for (int i = 0; i < 5; i++) {
                String PrincipalFirstName = faker.name().firstName();
                String PrincipalLastName = faker.name().lastName();
                String PrincipalEmail = PrincipalFirstName + "_" + PrincipalLastName + "@gmail.com";
                String password = BCrypt.hashpw(faker.internet().password(), salt); // TODO
                //String password = "1234";
                Principal principal = new Principal(PrincipalFirstName, PrincipalLastName, Enums.Gender.Male, PrincipalEmail, password);
                /*
                if(i == 0)
                {
                    principal.setEmail("p");
                    principal.setPassword(BCrypt.hashpw("1234", salt));
                    principal.setGender(Gender.Female);
                    principal.setFirstName("superUser");
                    principal.setLastName("Principal");
                }
                */
                principals.add(principal);
                SimpleServer.session.saveOrUpdate(principal);
            }
            SimpleServer.session.flush();
            return principals;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    public static List<Student> generateStudents(List<Course> courses) {
        List<Student> students = new ArrayList<>();
        String salt = BCrypt.gensalt();
        List<String> IDList = new ArrayList<>();
        for(int  i = 0; i < 40;i++)
        {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String password = BCrypt.hashpw(faker.internet().password(), salt);
            String studentEmail = firstName + "_" + lastName + "@gmail.com";
            String personID =  faker.number().digits(9);
            while (IDList.contains(personID)) { personID = faker.number().digits(9);}
            IDList.add(personID);
            Student student;
            if (i == 0)
                student = new Student("Super", "Student", Enums.Gender.Female, "student",
                                      BCrypt.hashpw("1234", salt), "123456789");
            else
                student = new Student(firstName, lastName, Enums.Gender.Female, studentEmail, password, personID);
            for (int  j = 0; j < 2;j++)
            {
                int courseNum = faker.number().numberBetween(0, courses.size());
                student.addCourse(courses.get(courseNum));
            }
            students.add(student);
            SimpleServer.session.save(student);

            SimpleServer.session.flush();
        }
        return students;
    }

    private static LocalDateTime GenerateRandomDate(LocalDate startDate, LocalDate endDate) {
        long startEpochDay = startDate.toEpochDay();
        long endEpochDay = endDate.toEpochDay();
        long randomEpochDay = ThreadLocalRandom.current().nextLong(startEpochDay, endEpochDay);
        return LocalDate.ofEpochDay(randomEpochDay).atTime(faker.number().numberBetween(8, 17), 0);
    }

    private static Date ConvertToDate(LocalDateTime localDate) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = localDate.atZone(zoneId);
        return Date.from(zonedDateTime.toInstant());
    }

    private static List<ClassExam> generateClassExams(List<ExamForm> examForms,List<Student> students)
    {
        if(examForms != null) {
            // generate list of Class Exam
            List<ClassExam> classExams = new ArrayList<>();
            for(ExamForm examForm: examForms) {

                // generate random start and end time
                LocalDate currentDate = LocalDate.now();
                LocalDateTime randomDay = GenerateRandomDate(currentDate, currentDate.plusMonths(5));
                Date testStartDate = ConvertToDate(randomDay);
                double examTime = faker.number().numberBetween(1, 4);
                int examDays = faker.number().numberBetween(0, 3);
                int examHours = faker.number().numberBetween((int)examTime, 23);
                Date testEndDate = ConvertToDate(randomDay.plusHours(examHours).plusDays(examDays));

                Teacher teacher =  SimpleServer.retrieveTeachers().get(0);
                //String code = Long.toString(faker.number().randomNumber(5, false));
                String accessCode = "11aa";
                Enums.ExamType examType = Enums.ExamType.values()[rand.nextInt(2)];
                ClassExam classExam = new ClassExam(examForm, testStartDate, testEndDate, examTime*60, teacher, accessCode,examForm.getCourse(),examForm.getSubject(),examType);
                classExam.setStudents(students);
                examForm.addClassExam(classExam);

                teacher.addClassExam(classExam);
                for(Student student: students)
                {
                    student.addClassExam(classExam);
                    SimpleServer.session.saveOrUpdate(student);
                }

                classExams.add(classExam);
                SimpleServer.session.saveOrUpdate(classExam);
                SimpleServer.session.flush();
            }
            return classExams;
        }
        else {
            System.out.println("No Exam Form Retrieved");
            return null;
        }

    }
    public static List<StudentExam> generateStudentExams(List<ClassExam> classExams,List<Student> students)
    {

        List<StudentExam> studentExams = new ArrayList<>();
        for(ClassExam classExam: classExams)
        {
            int i = 0;
            for(Student student:students)
            {
                int randGrade = rand.nextInt(100);
                Enums.submissionStatus status = (Enums.submissionStatus.values()[rand.nextInt(4)]);

                if (status != Enums.submissionStatus.Approved)
                    randGrade = -1;

                if (status == Enums.submissionStatus.Approved || status == Enums.submissionStatus.ToEvaluate) {
                    List<String> studentAnswers = new ArrayList<>();
                    List<Question> questions = classExam.getExamForm().getQuestionList();
                    for (Question question:questions) {
                        studentAnswers.add(question.getAnswers().get(faker.number().numberBetween(0, 4)));
                    }

                    StudentExam currentExam = new StudentExam(student, classExam, studentAnswers, randGrade, status);
                    student.addClassExam(classExam);
                    studentExams.add(currentExam);
                } else {
                    StudentExam currentExam = new StudentExam(student, classExam, null, randGrade, status);
                    studentExams.add(currentExam);
                }
                classExam = OperationUtils.UpdateClassExamStats(classExam);
                classExam.setExamToEvaluate(classExam.getStudentExams().stream().filter(studentExam ->
                                studentExam.getStatus().equals(Enums.submissionStatus.ToEvaluate)).collect(Collectors.toList())
                        .size());
                i++;
            }
        }
        for(StudentExam studentExam:studentExams) {
            SimpleServer.session.saveOrUpdate(studentExam);
            SimpleServer.session.flush();
        }

        return studentExams;

    }

}
