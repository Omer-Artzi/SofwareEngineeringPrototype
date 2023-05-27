import Entities.*;
import Events.MessageEvent;
import Events.StudentExamEvent;
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
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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
    private ComboBox<String> SubjectCombo;
    @FXML
    private ComboBox<String> CourseCombo;
    @FXML
    private ComboBox<String> ExamIDCombo;


    List<Student> Students = new ArrayList<>();
    List<Question> Questions = new ArrayList<>();
    List<ExamForm> ExamForms = new ArrayList<>();
    List<Teacher> Teachers = new ArrayList<>();
    Teacher clientTeacher;
    List<ClassExam> ClassExams = new ArrayList<>();
    List<Subject> Subjects = new ArrayList<>();


    String chosenCourseStr;
    String chosenExamStr;
    String chosenSubjectStr;

    ClassExam chosenExam;

    @FXML
    void CourseComboAct(ActionEvent event)
    {
        chosenCourseStr = CourseCombo.getSelectionModel().getSelectedItem();
        ExamIDCombo.getItems().clear();
        ClassExamTv.getItems().clear();

        // select Exam
        // collect the courses of the subject
        List<ClassExam> teacherExams = clientTeacher.getClassExam();
        List<ClassExam> selectedExams = teacherExams.stream().filter(item-> item.getExamForm().getCourse().getName() == chosenCourseStr)
                .collect(Collectors.toList());

        if (selectedExams.isEmpty())
            return;
        for (int i = 0; i < selectedExams.size(); i++)
        {
            ExamIDCombo.getItems().add(Integer.toString(selectedExams.get(i).getID()));
        }
    }

    @FXML
    void EditExamBtnAct(ActionEvent event) throws IOException {
        if(ClassExamTv.getSelectionModel().getSelectedItem() != null) {
            SimpleChatClient.setRoot("StudentExamGrade");
            StudentExamGradeController controller = (StudentExamGradeController) SimpleChatClient.getScene().getProperties().get("controller");
            EventBus.getDefault().post(new StudentExamEvent(ClassExamTv.getSelectionModel().getSelectedItem()));
            // TODO virtual teacher
            //Message message = new Message(1, "Get Grades: " + StudentsTV.getSelectionModel().getSelectedItem().getID());
            //SimpleClient.getClient().sendToServer(message);
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("Error: No Student Was Chosen");
            alert.show();
            //alert.is
        }
    }

    @FXML
    void ExamIDComboAct(ActionEvent event) {
        chosenExamStr = ExamIDCombo.getSelectionModel().getSelectedItem();
        ClassExamTv.getItems().clear();

        // Select the exam by id
        List<ClassExam> selectedExams = clientTeacher.getClassExam().stream().filter(item -> Integer.toString(item.getID()).equals(chosenExamStr))
                .collect(Collectors.toList());
        if (selectedExams.isEmpty())
            return;
        chosenExam = selectedExams.get(0);

        // Assign the table data sources
        NameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getStudent().getFullName()));
        IDColumn.setCellValueFactory(param -> new SimpleStringProperty(Long.toString(param.getValue().getStudent().getID())));
        GradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        StatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        ClassExamTv.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // Loading the data to the table
        if(chosenExam != null)
        {
            ClassExamTv.getItems().addAll(chosenExam.getStudentExams());
        }
    }

    @FXML
    void SubjectComboAct(ActionEvent event) {
        chosenSubjectStr = SubjectCombo.getSelectionModel().getSelectedItem();
        CourseCombo.getItems().clear();
        ExamIDCombo.getItems().clear();
        ClassExamTv.getItems().clear();


        // select course
        // collect the courses of the subject
        List<Course> teacherCourses = clientTeacher.getCourseList();
        List<Course> subjectCourses = teacherCourses.stream().filter(item-> item.getSubject().getName() == chosenSubjectStr)
                .collect(Collectors.toList());

        for (int i = 0; i < subjectCourses.size(); i++)
        {
            CourseCombo.getItems().add(subjectCourses.get(i).getName());
        }
    }


    @Subscribe
    public void stub(MessageEvent event) {
    }
    //
    //
    //public void GenerateStudents() {
    //    Faker faker = new Faker();
    //    for(int  i = 0; i < 10;i++)
    //    {
    //        String firstName = faker.name().firstName();
    //        String lastName = faker.name().lastName();
    //        Students.add(new Student(firstName,lastName));
    //
    //    }
    //}
    //
    //
    //private void GenerateQuestions(List<Subject> subjectList) {
    //    Faker faker = new Faker();
    //    int questionAmount = 30;
    //    String[] requests = {
    //            "https://opentdb.com/api.php?amount=" + questionAmount + "&category=9&type=multiple",
    //            "https://opentdb.com/api.php?amount=" + questionAmount + "&category=17&type=multiple",
    //            "https://opentdb.com/api.php?amount=" + questionAmount + "&category=24&type=multiple",
    //            "https://opentdb.com/api.php?amount=" + questionAmount + "&category=23&type=multiple",
    //            "https://opentdb.com/api.php?amount=" + questionAmount + "&category=20&type=multiple",
    //            "https://opentdb.com/api.php?amount=" + questionAmount + "&category=26&type=multiple",
    //            "https://opentdb.com/api.php?amount=" + questionAmount + "&category=19&type=multiple",
    //            "https://opentdb.com/api.php?amount=" + questionAmount + "&category=27&type=multiple",
    //            "https://opentdb.com/api.php?amount=" + questionAmount + "&category=24&type=multiple",
    //            "https://opentdb.com/api.php?amount=" + questionAmount + "&category=18&type=multiple",};
    //    Random random = new Random();
    //    int randCourse;
    //    for (int i = 0; i < requests.length; i++) {
    //        try {
    //
    //            // Create URL object and open connection
    //            URL url = new URL(requests[i]);
    //            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    //
    //            // Set request method
    //            connection.setRequestMethod("GET");
    //
    //            // Get response code
    //            int responseCode = connection.getResponseCode();
    //            if (responseCode == HttpURLConnection.HTTP_OK) {
    //                // Read response
    //                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    //                StringBuilder response = new StringBuilder();
    //                String line;
    //                while ((line = reader.readLine()) != null) {
    //                    response.append(line);
    //                }
    //                reader.close();
    //
    //                // Process JSON response
    //                String jsonResponse = response.toString();
    //
    //                // Parse JSON response
    //                Gson gson = new Gson();
    //                ApiResponse apiResponse = gson.fromJson(response.toString(), ApiResponse.class);
    //                List<ResponseQuestion> questions = apiResponse.getResults();
    //                List<Question> questionsList = new ArrayList<>();
    //                for (ResponseQuestion responseQuestion : questions) {
    //                    Question question = new Question();
    //                    responseQuestion.convert(question);
    //                    List<String> ans = responseQuestion.getIncorrect_answers();
    //                    ans.add(faker.number().numberBetween(0, 4), responseQuestion.getCorrect_answer());
    //                    question.setAnswers(ans);
    //
    //                    question.setCourse(subjectList.get(i).getCourses().get(0));
    //                    //questionsList.add(question);
    //                    Questions.add(question);
    //                }
    //                //System.out.println(questions);
    //                //GenerateTestForms(questionsList);
    //
    //
    //            } else {
    //                System.out.println("Error: " + responseCode);
    //            }
    //
    //            // Close the connection
    //            connection.disconnect();
    //
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        }
    //
    //    }
    //}
    //
    //private void GenerateTestForms(List<Question> questionsList) {
    //    if(questionsList != null) {
    //        for(int  i = 0; i < 3; i++) {
    //            ExamForm examForm = new ExamForm();
    //            for(int  j = 0; j < 10;j++) {
    //                examForm.addQuestion(questionsList.get((i * 10) + j));
    //            }
    //            List<Question> examQuestions =  examForm.getQuestionList();
    //            Course examCourse = Teachers.get(1).getCourseList().get(0);
    //            Subject examSubject = examCourse.getSubject();
    //            examForm.setQuestionList(examQuestions);
    //            examForm.setSubject(examSubject);
    //            examForm.setCourse(examCourse);
    //            examForm.setCreator(examCourse.getTeacherList().get(0));
    //            LocalDate localDate = LocalDate.now();
    //            examForm.getCode();
    //            // Convert LocalDate to Date
    //            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    //            examForm.setDateCreated(date);
    //            examForm.setLastUsed(date);
    //            examForm.setQuestionsScores(new ArrayList<>(Collections.nCopies(10, 10)));
    //            ExamForms.add(examForm);
    //        }
    //    }
    //    else {
    //        System.out.println("No question Retrieved");
    //    }
    //
    //}
    //
    //
    //private void GenerateTeachers(List<Subject> subjects) {
    //    try {
    //        Teacher admin = new Teacher();
    //        String salt = BCrypt.gensalt();
    //        admin.setEmail("admin");
    //        admin.setPassword(BCrypt.hashpw("1234", salt));
    //        admin.setCourseList(new ArrayList<>());
    //        admin.setGender(Gender.Female);
    //        admin.setFirstName("super");
    //        admin.setLastName("user");
    //        Teachers.add(admin);
    //        Faker faker = new Faker();
    //        Random random = new Random();
    //        int randomSubject, randomCourse;
    //        for (int i = 0; i < 50; i++) {
    //            String teacherFirstName = faker.name().firstName();
    //            String teacherLastName = faker.name().lastName();
    //            String teacherEmail = teacherFirstName + "_" + teacherLastName + "@gmail.com";
    //            String password = BCrypt.hashpw(faker.internet().password(), salt);
    //            List<Course> courses = new ArrayList<>();
    //            for (int j = 0; j < 5; j++) {
    //                randomSubject = random.nextInt(subjects.size());
    //                Subject subject = subjects.get(randomSubject);
    //                for (int k = 0; k < 5; k++) {
    //                    randomCourse = random.nextInt(subject.getCourses().size());
    //
    //                    courses.add(subject.getCourses().get(randomCourse));
    //                }
    //            }
    //            Teacher teacher = new Teacher(teacherFirstName, teacherLastName, Gender.Male, teacherEmail, password);
    //            for (Course course : courses) {
    //                course.getTeachers().add(teacher);
    //                teacher.AddCourse(course);
    //            }
    //
    //            Teachers.add(teacher);
    //
    //        }
    //    }
    //    catch (Exception e)
    //    {
    //        e.printStackTrace();
    //    }
    //
    //}
    //
    //private static LocalDate generateRandomDate(LocalDate startDate, LocalDate endDate) {
    //    long startEpochDay = startDate.toEpochDay();
    //    long endEpochDay = endDate.toEpochDay();
    //    long randomEpochDay = ThreadLocalRandom.current().nextLong(startEpochDay, endEpochDay);
    //    return LocalDate.ofEpochDay(randomEpochDay);
    //}
    //
    //private static Date convertToDate(LocalDate localDate) {
    //    LocalDateTime localDateTime = localDate.atStartOfDay();
    //    ZoneId zoneId = ZoneId.systemDefault();
    //    ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
    //    return Date.from(zonedDateTime.toInstant());
    //}
    //private void GenerateClassExams(List<ExamForm> ExamForms)
    //{
    //    Faker faker = new Faker();
    //    if(ExamForms != null) {
    //
    //        // generate list of Class Exam
    //        for(int i = 0; i < 3; i++) {
    //
    //            // covert random date in the next 5 mouths to perform the test
    //            LocalDate currentDate = LocalDate.now();
    //            LocalDate randomDate = generateRandomDate(currentDate, currentDate.plusMonths(5));
    //            Date testDate = convertToDate(randomDate);
    //            Teacher teacher = Teachers.get(1);
    //            ClassExam classExam = new ClassExam(ExamForms.get(i), testDate.toString(), teacher);
    //
    //            System.out.println(ExamForms.get(i).getCourse().getName());
    //            // Generate for every Class Exam a list of studentExams
    //            List<Student> studentsTemp = new ArrayList<>(Students);
    //            int numberOfElements = faker.number().numberBetween(3,  studentsTemp.size());
    //            for (int examineeNum = 0; examineeNum < numberOfElements; examineeNum++) {
    //
    //                int randomIndex = faker.number().numberBetween(0,  studentsTemp.size());
    //                Student randomStudent = studentsTemp.get(randomIndex);
    //                studentsTemp.remove(randomIndex);
    //
    //                List<StudentExam> StudentExams = new ArrayList<>();
    //                List<Integer> studentAnswers =  new ArrayList<>();
    //                for(int  j = 0; j < classExam.getExamForm().getQuestionList().size() ;j++) {
    //                    studentAnswers.add(faker.number().numberBetween(1, 5));
    //                }
    //                String status = "";
    //                if (faker.number().numberBetween(0, 2) == 1)
    //                    status = "Approved";
    //                else
    //                    status = "To Evaluate";
    //
    //                StudentExams.add(new StudentExam(randomStudent, classExam, studentAnswers, faker.number().numberBetween(0, 101), status));
    //
    //            }
    //            ClassExams.add(classExam);
    //        }
    //    }
    //    else {
    //        System.out.println("No Exam Form Retrieved");
    //    }
    //}


    @FXML
    void initialize() throws IOException {
        EventBus.getDefault().register(this);


        // This block of lines will be removed when we will define teacherClient and extract from there the data
        //SimpleClient.getClient().sendToServer(new Message(0, "Get Subjects"));
        //while (Subjects.isEmpty()){System.out.println("Subjects is empty");}
        //GenerateTeachers(Subjects);
        //GenerateQuestions(Subjects);
        //GenerateStudents();
        //GenerateTestForms(Questions);
        //GenerateClassExams(ExamForms);

        // Change this line to get the specific teacher of the client
        //clientTeacher = Teachers.get(1);

        clientTeacher = (Teacher)SimpleClient.getClient().getUser();
        // Get teacher courses and return if the teacher not assigned to any course
        List<Course> teacherCourses = clientTeacher.getCourseList();
        if (teacherCourses.isEmpty())
        {
            System.out.println("Teacher Is not assign to any course");
            return;
        }

        // Get teacher assigned Subjects
        List<Subject> teacherSubjects = new ArrayList<>();
        for (int i = 0; i < teacherCourses.size(); i++)
        {
            Subject subject = teacherCourses.get(i).getSubject();
            if (!teacherSubjects.contains(subject))
                teacherSubjects.add(subject);
        }

        // select subject
        assert SubjectCombo != null : "fx:id=\"SubjectCombo\" was not injected: check your FXML file 'ClassExamGrade.fxml'.";
        for (int i = 0; i < teacherSubjects.size(); i++)
        {
            SubjectCombo.getItems().add(teacherSubjects.get(i).getName());
        }

    }

}
