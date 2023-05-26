import Entities.*;
import Events.SubjectMessageEvent;
import Server.Events.ApiResponse;
import Server.Events.ResponseQuestion;
import com.github.javafaker.Faker;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import javafx.beans.property.SimpleStringProperty;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.BufferedReader;
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

public class ClassExamGradeController {



    @FXML
    private TableView<StudentExam> ClassExamTv;

    @FXML
    private Button EditExamBtn;

    @FXML
    private TableColumn<StudentExam, String> GradeColumn;

    @FXML
    private TableColumn<StudentExam, String> IDColumn;

    @FXML
    private TableColumn<StudentExam, String> NameColumn;

    @FXML
    private TableColumn<StudentExam, String> StatusColumn;

    @FXML
    private Button ViewExamBtn;

    @FXML
    private ComboBox<Subject> SubjectCombo;
    @FXML
    private ComboBox<Course> CourseCombo;
    @FXML
    private ComboBox<ClassExam> ExamIDCombo;


    List<Student> Students = new ArrayList<>();
    List<Question> Questions = new ArrayList<>();
    List<ExamForm> ExamForms = new ArrayList<>();
    List<Teacher> Teachers = new ArrayList<>();
    List<ClassExam> ClassExams = new ArrayList<>();
    List<Subject> Subjects = new ArrayList<>();


    @FXML
    void CourseComboAct(ActionEvent event)
    {

    }

    @FXML
    void EditExamBtnAct(ActionEvent event) {

    }

    @FXML
    void ExamIDComboAct(ActionEvent event) {

    }

    @FXML
    void SubjectComboAct(ActionEvent event) {

    }




    @Subscribe
    public void getStarterData(NewSubscriberEvent event) {
        try {
            int msgId=0;
            Message message = new Message(msgId, "Get Students");
            SimpleClient.getClient().sendToServer(message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Subscribe
    public void getSubject(SubjectMessageEvent event) {
        System.out.println("Subject Received");
        Subjects = event.getSubjects();
        if (Subjects.isEmpty())
            System.out.println("Subject is still empty");
    }




    public void GenerateStudents() {
        Faker faker = new Faker();
        for(int  i = 0; i < 10;i++)
        {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            Students.add(new Student(firstName,lastName));

        }
    }


    private void GenerateQuestions(List<Subject> subjectList) {
        int questionAmount = 30;
        String[] requests = {
                "https://opentdb.com/api.php?amount=" + questionAmount + "&category=9&type=multiple",
                "https://opentdb.com/api.php?amount=" + questionAmount + "&category=17&type=multiple",
                "https://opentdb.com/api.php?amount=" + questionAmount + "&category=24&type=multiple",
                "https://opentdb.com/api.php?amount=" + questionAmount + "&category=23&type=multiple",
                "https://opentdb.com/api.php?amount=" + questionAmount + "&category=20&type=multiple",
                "https://opentdb.com/api.php?amount=" + questionAmount + "&category=26&type=multiple",
                "https://opentdb.com/api.php?amount=" + questionAmount + "&category=19&type=multiple",
                "https://opentdb.com/api.php?amount=" + questionAmount + "&category=27&type=multiple",
                "https://opentdb.com/api.php?amount=" + questionAmount + "&category=24&type=multiple",
                "https://opentdb.com/api.php?amount=" + questionAmount + "&category=18&type=multiple",};
        Random random = new Random();
        int randCourse;
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
                    List<Question> questionsList = new ArrayList<>();
                    for (ResponseQuestion responseQuestion : questions) {
                        Question question = new Question();
                        responseQuestion.convert(question);
                        question.setCourse(subjectList.get(i).getCourses().get(0));
                        questionsList.add(question);
                        Questions.add(question);
                    }
                    //System.out.println(questions);
                    GenerateTestForms(questionsList);


                } else {
                    System.out.println("Error: " + responseCode);
                }

                // Close the connection
                connection.disconnect();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void GenerateTestForms(List<Question> questionsList) {
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
                ExamForms.add(examForm);
            }
        }
        else {
            System.out.println("No question Retrieved");
        }

    }


    private void GenerateTeachers(List<Subject> subjects) {
        try {
            Teacher admin = new Teacher();
            String salt = BCrypt.gensalt();
            admin.setEmail("admin");
            admin.setPassword(BCrypt.hashpw("1234", salt));
            admin.setCourseList(new ArrayList<>());
            admin.setGender(Gender.Female);
            admin.setFirstName("super");
            admin.setLastName("user");
            Teachers.add(admin);
            Faker faker = new Faker();
            Random random = new Random();
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
                Teacher teacher = new Teacher(teacherFirstName, teacherLastName, Gender.Male, teacherEmail, password, courses);
                for (Course course : courses) {
                    course.getTeachers().add(teacher);
                }

                Teachers.add(teacher);

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    private static LocalDate generateRandomDate(LocalDate startDate, LocalDate endDate) {
        long startEpochDay = startDate.toEpochDay();
        long endEpochDay = endDate.toEpochDay();
        long randomEpochDay = ThreadLocalRandom.current().nextLong(startEpochDay, endEpochDay);
        return LocalDate.ofEpochDay(randomEpochDay);
    }

    private static Date convertToDate(LocalDate localDate) {
        LocalDateTime localDateTime = localDate.atStartOfDay();
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        return Date.from(zonedDateTime.toInstant());
    }


    private void GenerateClassExams(List<ExamForm> ExamForms)
    {
        Faker faker = new Faker();
        if(ExamForms != null) {

            // generate list of Class Exam
            for(int i = 0; i < 3; i++) {

                // covert random date in the next 5 mouths to perform the test
                LocalDate currentDate = LocalDate.now();
                LocalDate randomDate = generateRandomDate(currentDate, currentDate.plusMonths(5));
                Date testDate = convertToDate(randomDate);
                Teacher teacher = Teachers.get(faker.number().numberBetween(0, Teachers.size()-1));
                ClassExam classExam = new ClassExam(ExamForms.get(i), testDate.toString(), teacher);


                // Generate for every Class Exam a list of studentExams
                List<Student> studentsTemp = new ArrayList<>(Students);
                int numberOfElements = faker.number().numberBetween(3,  studentsTemp.size());
                for (int examineeNum = 0; examineeNum < numberOfElements; examineeNum++) {

                    int randomIndex = faker.number().numberBetween(0,  studentsTemp.size());
                    Student randomStudent = studentsTemp.get(randomIndex);
                    studentsTemp.remove(randomIndex);

                    List<StudentExam> StudentExams = new ArrayList<>();
                    List<Integer> studentAnswers =  new ArrayList<>();
                    for(int  j = 0; j < classExam.getExamForm().getQuestionList().size() ;j++) {
                        studentAnswers.add(faker.number().numberBetween(1, 5));
                    }
                    String status = "";
                    if (faker.number().numberBetween(0, 2) == 1)
                        status = "Approved";
                    else
                        status = "To Evaluate";

                    StudentExams.add(new StudentExam(randomStudent, classExam, studentAnswers, faker.number().numberBetween(0, 101), status));

                }
                ClassExams.add(classExam);
            }
        }
        else {
            System.out.println("No Exam Form Retrieved");
        }
    }


    @FXML
    void initialize() throws IOException {
        EventBus.getDefault().register(this);


        SimpleClient.getClient().sendToServer(new Message(0, "Get Subjects"));
        System.out.println("check1");
        while (Subjects.isEmpty()){System.out.println("Subjects is empty");}
        System.out.println("check2");



        GenerateTeachers(Subjects);
        GenerateQuestions(Subjects);
        GenerateStudents();
        GenerateTestForms(Questions);
        GenerateClassExams(ExamForms);
        System.out.println("check3");



        NameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getStudent().getFullName()));

        IDColumn.setCellValueFactory(param -> new SimpleStringProperty(Long.toString(param.getValue().getStudent().getID())));
        GradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        StatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        ClassExamTv.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        //List<ClassExam> classExamList;
        if(ClassExams != null)
        {
            ClassExamTv.getItems().addAll(ClassExams.get(0).getStudentExams());
        }
    }

}
