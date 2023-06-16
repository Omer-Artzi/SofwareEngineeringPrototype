package Server;

import Entities.Communication.ExtraTime;
import Entities.Communication.Message;
import Entities.Communication.Transmission;
import Entities.Enums;
import Entities.SchoolOwned.*;
import Entities.StudentOwned.Grade;
import Entities.StudentOwned.ManualStudentExam;
import Entities.StudentOwned.StudentExam;
import Entities.Users.Person;
import Entities.Users.Principal;
import Entities.Users.Student;
import Entities.Users.Teacher;
import Server.Events.ClientUpdateEvent;
import Server.Events.ExtraTimeRequestEvent;
import Server.Events.TerminationEvent;
import Server.Events.TransmissionEvent;
import Server.ocsf.AbstractServer;
import Server.ocsf.ConnectionToClient;
import Server.ocsf.SubscribedClient;
import com.github.javafaker.Faker;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.hibernate.service.ServiceRegistry;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.swing.*;
import java.awt.desktop.QuitResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.*;

public class SimpleServer extends AbstractServer {
    private static final ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();
    private static final List<Person> LoggedInUsers = new ArrayList<>();
    public static Session session;
    public Session session2;
    private static int transmissionID = 0;
    private static SessionFactory sessionFactory;
    private static String IP;
    private static int port;


    public SimpleServer(int port) {
        super(port);
        try {
            EventBus.getDefault().register(this);
            SessionFactory sessionFactory = getSessionFactory(null);
            session = sessionFactory.openSession();
            session.beginTransaction();
            String cfg = sessionFactory.getProperties().get("hibernate.hbm2ddl.auto").toString();
            if (cfg.equals("create") || cfg.equals("create-drop") || cfg.equals("create-only")) {
                DataGenerator.generateData();
            }
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    List<ClassExam> classExams = retrieveClassExam();
                    for (ClassExam classExam : classExams) {
                        if (classExam.getExamStatus() == Enums.examStatus.Active && classExam.getFinalSubmissionDate().before(Date.valueOf(LocalDateTime.now().toLocalDate()))) {
                            classExam.setExamStatus(Enums.examStatus.Inactive);
                            Message message = new Message(1, "The exam " + classExam.getID() + " has run out of time, it is now closed");
                            message.setData(classExam);
                            sendToAllClients(message);
                        }
                    }
                }
            };
            timer.schedule(task, 0, 1000 * 60);
        }
        catch (Exception exception) {
            if (session != null) {
                session.getTransaction().rollback();
            }
            System.err.println("An error occurred, changes have been rolled back.");
            exception.printStackTrace();
            JOptionPane.showMessageDialog(null, "A connection to the database could not be formed, please check the MySQL Server is installed and running(Check Console for more info)", "Database Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static SessionFactory getSessionFactory(Map<String, String> properties) throws HibernateException, InterruptedException {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration();
            configuration.addAnnotatedClass(Student.class);
            configuration.addAnnotatedClass(Grade.class);
            configuration.addAnnotatedClass(Subject.class);
            configuration.addAnnotatedClass(Course.class);
            configuration.addAnnotatedClass(Teacher.class);
            configuration.addAnnotatedClass(Question.class);
            configuration.addAnnotatedClass(ExamForm.class);
            configuration.addAnnotatedClass(ClassExam.class);
            configuration.addAnnotatedClass(StudentExam.class);
            configuration.addAnnotatedClass(Person.class);
            configuration.addAnnotatedClass(ExtraTime.class);
            configuration.addAnnotatedClass(Principal.class);
            if (properties != null) {
                for (Map.Entry<String, String> entry : properties.entrySet()) {
                    configuration.setProperty(entry.getKey(), entry.getValue());
                }
            }

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        }
        return sessionFactory;
    }

    public static String getIP() {
        return IP;
    }

    public static void setIP(String IP) {
        SimpleServer.IP = IP;
    }

    public static int getLocalPort() {
        return port;
    }

    public static void setLocalPort(int port) {
        SimpleServer.port = port;
    }

    public static List<Student> retrieveStudents() {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Student> query = builder.createQuery(Student.class);
        query.from(Student.class);
        List<Student> students = session.createQuery(query).getResultList();
        return students;
    }

    public static List<Subject> retrieveSubjects() {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Subject> query = builder.createQuery(Subject.class);
        query.from(Subject.class);
        List<Subject> subjects = session.createQuery(query).getResultList();
        return subjects;
    }

    public static List<Teacher> retrieveTeachers() {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Teacher> query = builder.createQuery(Teacher.class);
        query.from(Teacher.class);
        List<Teacher> teachers = session.createQuery(query).getResultList();
        return teachers;
    }

    public static List<ExamForm> retrieveExamForm() {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ExamForm> query = builder.createQuery(ExamForm.class);
        query.from(ExamForm.class);
        List<ExamForm> exams = session.createQuery(query).getResultList();
        return exams;
    }

    public static ExamForm getExamForm(int iExamFormID) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ExamForm> query = builder.createQuery(ExamForm.class);
        Root<ExamForm> root = query.from(ExamForm.class);
        query.where(builder.equal(root.get("ID"), iExamFormID));
        return session.createQuery(query).getSingleResult();
    }

    @Subscribe
    public void CloseServer(TerminationEvent event) throws IOException {
        System.out.println("Server is closed");
        Message message = new Message(1, "Server is closed");
        sendToAllClients(message);
        if (session != null) {
            if (session.getTransaction().getStatus().equals(TransactionStatus.ACTIVE))
                session.getTransaction().commit();
            session.close();
        }
        this.close();
    }

    //Generating grades and saving in the SQL server
    private void generateGrades() {
        List<Student> students = retrieveStudents();
        Faker faker = new Faker();
        Random r = new Random();
        for (Student student : students) {
            for (int i = 0; i < 8; i++) {
                Grade grade = new Grade(r.nextInt(100), faker.educator().course(), faker.pokemon().name(), student);
                student.getGrades().add(grade);
                session.save(grade);
            }
        }
        session.flush();
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        Message message = (Message) msg;
        String response;
        String request = message.getMessage();
        Transmission transmission = new Transmission();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        transmission.setTimeReceived(LocalDateTime.now().format(dtf));
        transmission.setTimeSent(message.getTimeStamp().format(dtf));
        transmission.setRequest(message.getMessage());
        SubscribedClient subscribedClient = new SubscribedClient(client);
        transmission.setClient(subscribedClient.getClient().toString());
        transmission.setID(transmissionID++);
        System.out.println("Message Received: " + request);

        // Todo: find better solution to start and commit transaction
        if (!session.getTransaction().isActive()) {
            session.beginTransaction();
        }
        try {
            //we got an empty message, so we will send back an error message with the error details.
            if (request.isBlank()) {
                response = "Error! we got an empty message";
                message.setMessage(response);
                client.sendToClient(message);
            }
            else if (request.startsWith("Login")) {
                System.out.println("Login request received");
                List<String> credentials = (List<String>) message.getData();
                String password = credentials.get(1);
                Person user = retrieveUser(credentials.get(0));
                if (BCrypt.checkpw(password, user.getPassword()) && user != null) {
                    if (LoggedInUsers.contains(user)) {
                        response = "Fail: User already logged in";
                    }
                    else {
                        LoggedInUsers.add(user);
                        System.out.println("User " + user.getFullName() + " logged in");
                        response = "Success: User logged in";
                        message.setData(user);
                    }
                }
                else {
                    response = "Fail : User not found, user not logged in";
                }
                message.setMessage(response);
                client.sendToClient(message);
            }
            else if (request.startsWith("Logout")) {
                Boolean loggedout = LogUserOut((Person) message.getData());
                if (loggedout) {
                    response = "Success: User logged out";
                }
                else {
                    response = "Fail: User not logged in";
                }
                message.setMessage(response);
                client.sendToClient(message);
            }
            else if (request.startsWith("1Get Subjects of Teacher")) {  // Added by Ilan 30.5
                String teacherID = request.substring(26);
                System.out.println("Teacher ID: " + teacherID); /////
                int iTeacherID = Integer.parseInt(teacherID);
                Teacher teacher = getTeacher(iTeacherID);
                response = ("1Subjects of: " + teacher.getFullName());
                System.out.println(response); /////
                message.setMessage(response);
                message.setData(getSubjects(iTeacherID));
                System.out.println("Subjects: " + teacher.getSubjectList()); /////
                client.sendToClient(message);
            }
            else if (request.startsWith("1Get Courses of Teacher")) {  // Added by Ilan 30.5
                String teacherID = request.substring(25);
                System.out.println("Teacher ID: " + teacherID); /////
                int iTeacherID = Integer.parseInt(teacherID);
                Teacher teacher = getTeacher(iTeacherID);
                response = ("1Courses of: " + teacher.getFullName());
                System.out.println(response); /////
                message.setMessage(response);
                message.setData(getCourses(iTeacherID));
                System.out.println("Subjects: " + teacher.getSubjectList()); /////
                client.sendToClient(message);
            }
            else if (request.startsWith("Get Subjects")) {
                response = "Subjects";
                message.setMessage(response);
                message.setData(getSubjects());
                client.sendToClient(message);
            }
            else if (request.startsWith("Add New Class Exam")) {
                try {
                    response = "Exam Saved Successfully";
                    message.setMessage(response);
                    session.saveOrUpdate((ClassExam) (message.getData()));
                    session.flush();
                    client.sendToClient(message);
                }
                catch (IOException e) {
                    e.printStackTrace();
                    response = "Failed to save exam";
                }
            }
            else if (request.startsWith("Get Student Exams For Student ID:")) {
                response = request.substring(4);
                int studentID = Integer.parseInt(request.substring(34));
                message.setMessage(response);
                message.setData(retrieveStudentExams(studentID));
                client.sendToClient(message);
            }
            else if (request.startsWith("Get class exams for student ID")) {
                response = request.substring(4);
                int studentID = Integer.parseInt(request.substring(32));
                message.setMessage(response);
                List<ClassExam> classExams = getExamsForStudent(studentID);
                message.setData(classExams);
                client.sendToClient(message);
            }
            else if (request.startsWith("Get ExtraTimeRequest data")) {
                /*
                response = "ExtraTimeRequest data";
                message.setMessage(response);
                message.setData(new ExtraTimeRequestEvent((ExtraTime) message.getData()));
                sendToAllClients(message);
                */
                response ="ExtraTimeRequest data";
                message.setMessage(response);
                List<Object>data=new ArrayList<>();
                data.add(message.getData());
                data.add(getPrincipals());
                message.setData(data);
                client.sendToClient(message);
            }
            else if (message.getMessage().startsWith("Extra time request")) {
                ExtraTime extraTime = (ExtraTime) (message.getData());
                try {
                    session.save(extraTime);

                    session.flush();

                    response = ("Extra Time Requested");
                    message.setMessage(response);
                    //client.sendToClient(message);
                    sendToAllClients(message);
                }
                catch (Exception e) {
                    response = (" could not be added to the database");
                }
                //response = "Extra Time Requested";
                //message.setMessage(response);
                //message.setData(new ExtraTimeRequestEvent((ExtraTime)message.getData()));
                //sendToAllClients(message);
            }
            else if (request.startsWith("Manual Exam")) {
                try {
                    response = "Manual Exam Received";
                    message.setMessage(response);

                    StudentExam studentExam = (StudentExam) message.getData();
                    StudentExam oldStudentExam = getStudentExamFromClassExam(studentExam.getStudent().getID(), studentExam.getClassExam().getID());
                    if (oldStudentExam == null) {
                        System.out.println("oldStudentExam is null");
                        return;
                    }

                    // Student Link
                    Student student = (Student) retrieveUser(studentExam.getStudent().getEmail());
                    oldStudentExam.setStudent(student);
                    student.addStudentExam(oldStudentExam);

                    // ClassExam Link
                    ClassExam classExam = retrieveClassExam(studentExam.getClassExam().getID());
                    oldStudentExam.setClassExam(classExam);
                    classExam.addStudentExam(oldStudentExam);

                    // no entities attributes set
                    oldStudentExam.update(studentExam);
                    //studentExam.update(oldStudentExam);

                    session.saveOrUpdate(oldStudentExam);

                    studentExam.SaveManualExamFileLocally();

                    System.out.println("Document Saved successfully.");
                    client.sendToClient(message);

                }
                catch (Exception e) {
                    response = "Manual Exam could not be saved";
                    e.printStackTrace();
                }
            }else if (request.startsWith("Digital Exam")) {
                try {
                    response = "Digital Exam Received";
                    message.setMessage(response);

                    StudentExam studentExam = (StudentExam) message.getData();
                    StudentExam oldStudentExam = getStudentExamFromClassExam(studentExam.getStudent().getID(), studentExam.getClassExam().getID());
                    if (oldStudentExam == null) {
                        System.out.println("oldStudentExam is null");
                        return;
                    }

                    // Student Link
                    Student student = (Student) retrieveUser(studentExam.getStudent().getEmail());
                    oldStudentExam.setStudent(student);
                    student.addStudentExam(oldStudentExam);

                    // ClassExam Link
                    ClassExam classExam = retrieveClassExam(studentExam.getClassExam().getID());
                    oldStudentExam.setClassExam(classExam);
                    classExam.addStudentExam(oldStudentExam);

                    // no entities attributes set
                    oldStudentExam.update(studentExam);
                    //studentExam.update(oldStudentExam);

                    session.saveOrUpdate(oldStudentExam);

                    System.out.println("DigitalExam Saved successfully.");
                    client.sendToClient(message);
                }
                catch (Exception e) {
                    response = "Digital Exam could not be saved";
                    e.printStackTrace();
                }
            }
            else if (message.getMessage().startsWith("Extra time approved")) {
                try {
                    ExtraTime extraTime=(ExtraTime) message.getData();

                    SessionFactory SessionFactory = getSessionFactory(null);
                   // session2 = sessionFactory.openSession();
                    //session2.beginTransaction();

                     session2 = SessionFactory.openSession();
                    Transaction tx2 = session2.beginTransaction();

                    session2.saveOrUpdate(extraTime);

                    tx2.commit();
                    session2.close();
                    session.merge(extraTime);
                   // session.flush();
                    response = "Extra time approved";
                }
                catch (Exception e) {
                    response = (" Extra time request - approve could not be added to the database");
                    e.printStackTrace();
                }
                message.setMessage(response);
                sendToAllClients(message);

            }
            else if (message.getMessage().startsWith("Extra time rejected")) {
                try {
                    ExtraTime extraTime=(ExtraTime) message.getData();
                    SessionFactory SessionFactory = getSessionFactory(null);
                    Session session2 = SessionFactory.openSession();
                    Transaction tx2 = session2.beginTransaction();

                    session2.saveOrUpdate(extraTime);

                    tx2.commit();
                    //session.flush();
                    response = "Extra time rejected";
                }
                catch (Exception e) {
                    e.printStackTrace();
                    response = (" Extra time request- Reject could not be added to the database");

                }
                message.setMessage(response);
                sendToAllClients(message);

            }
            else if (message.getMessage().startsWith("Exam approved")) {
                try {
                    session.save(message.getData());
                    response = "Exam saved successfully";
                }
                catch (Exception e) {
                    response = "Failed to save exam";
                }
                message.setMessage(response);
                client.sendToClient(message);
            }
            else if (request.startsWith("Get Class Exams for Subject")) {
                response = "Exams in Subject " + ((Subject) (message.getData())).getName();
                message.setMessage(response);
                message.setData(getExamsForSubjects((Subject) (message.getData())));
                client.sendToClient(message);
            }
            else if (request.startsWith("Get Class Exams  for Course")) {
                response = "Exams in Course " + ((Course) (message.getData())).getName();
                message.setMessage(response);
                message.setData(getExamsForCourse((Course) (message.getData())));
                client.sendToClient(message);
            }
            else if (request.startsWith("Get Exam Forms For Subject")) {
                response = "Exam Forms in Subject " + ((Subject) (message.getData())).getName();
                message.setMessage(response);
                message.setData(getExamFormForSubjects((Subject) (message.getData())));
                client.sendToClient(message);
            }
            else if (request.startsWith("Get Exam Forms For Course")) {
                response = "Exam Forms in Course " + ((Course) (message.getData())).getName();
                System.out.println("Get Exam Forms for Course");
                message.setMessage(response);
                message.setData(getExamFormForCourse((Course) (message.getData())));
                client.sendToClient(message);
            }
            else if (request.startsWith("Get Exams for Subject")) {
                response = "Exams in Subject " + ((Subject) (message.getData())).getName();
                message.setMessage(response);
                message.setData(getExamsForSubjects((Subject) (message.getData())));
                client.sendToClient(message);
            }else if (request.startsWith("Get extra time of specific class exam")) {/////////// LIAD ADDITION
                response = "extra time of specific class exam";
                message.setMessage(response);
                ClassExam classExam=(ClassExam) (message.getData());
                ExtraTime extraTime=getExtraTimeForClassExam(classExam);
                message.setData(extraTime);
                if(extraTime==null)
                {
                    System.out.println("the extra time i get is null :(");
                }
                client.sendToClient(message);/////
            }
            else if (request.startsWith("Get Exams For Course")) {
                response = "Exams in Course " + ((Course) (message.getData())).getName();
                message.setMessage(response);
                message.setData(getExamsForCourse((Course) (message.getData())));
                client.sendToClient(message);
            }
            else if (request.startsWith("Get Questions for Course")) {
                response = "Questions in Course " + ((Course) (message.getData())).getName();
                System.out.println(response);
                message.setMessage(response);
                message.setData(getQuestionsForCourse((Course) (message.getData())));
                client.sendToClient(message);
            }
            //Client asked for the student list, we will pull it from the SQL server and send it over
            else if (request.startsWith("Get Students")) {
                response = "Students";
                message.setMessage(response);
                message.setData(retrieveStudents());
                client.sendToClient(message);
            }
            else if (request.startsWith("Get Live Exams")) {
                response = "Live Exams";
                message.setMessage(response);
                message.setData(retrieveClassExam());
                client.sendToClient(message);
            }
            else if (request.startsWith("Get Extra Time Requests")) { /////
                response = "Extra Time Requests";
                message.setMessage(response);
                message.setData(getExtraTime());
                client.sendToClient(message);
            }
            //Client asked for the grades list of a certain student, we will pull it from the SQL server and send it over
            else if (request.startsWith("Get Grades")) {
                String studentID = request.substring(12);
                int iStudentID = Integer.parseInt(studentID);
                Student student = getStudent(iStudentID);
                response = ("Grades of " + student.getFullName());
                message.setMessage(response);
                message.setData(student);
                client.sendToClient(message);
            }
            //The user decided to update a grade, we will update the SQL server and send the new grade list
            else if (request.startsWith("Change Grade")) {
                try {
                    Grade newGrade = ((Grade) (message.getData()));
                    response = "Grade Saved: " + newGrade.getStudent().getFullName() + "'s grade in " + newGrade.getCourse() + " was changed to " + newGrade.getGrade();
                    message.setMessage(response);
                    session.merge(newGrade);
                    session.flush();
                    client.sendToClient(message);
                }
                catch (Exception e) {
                    response = "Failed to save grade";
                    message.setMessage(response);
                    client.sendToClient(message);
                }
            }
            //we got a request to add a new client as a subscriber.
            else if (request.equals("add client")) {
                SubscribersList.add(subscribedClient);
                response = "client added successfully";
                message.setMessage(response);
                client.sendToClient(message);
                EventBus.getDefault().post(new ClientUpdateEvent(SubscribersList.size()));
            }
            else if (request.startsWith("Client Closed")) {
                Boolean loggedout = LogUserOut((Person) message.getData());
                response = "";
                for (SubscribedClient subscriber : SubscribersList) {
                    if (subscriber.getClient().equals(client)) {
                        SubscribersList.remove(subscriber);
                        break;
                    }

                }
                EventBus.getDefault().post(new ClientUpdateEvent(SubscribersList.size()));
            }
            else if (request.startsWith("Add Student")) {
                Student newStudent = (Student) (message.getData());
                try {
                    session.save(newStudent);
                    session.flush();
                    response = ("Success: " + newStudent.getFullName() + " was successfully added to the database");
                    message.setMessage(response);
                    client.sendToClient(message);
                }
                catch (Exception e) {
                    response = (newStudent.getFullName() + " could not be added to the database");
                }
            }
            else if (request.startsWith("Add Grade")) {
                Grade newGrade = ((Grade) (message.getData()));
                try {
                    session.save(newGrade);
                    Student updatedStudent = newGrade.getStudent();
                    updatedStudent.getGrades().add(newGrade);
                    session.merge(updatedStudent);
                    session.flush();
                    response = ("Success: " + newGrade.getStudent().getFullName() + "'s grade in " + newGrade.getCourse() + ": " + newGrade.getSubject() + " was successfully added to the database");
                    message.setMessage(response);
                    client.sendToClient(message);
                    System.out.println(response);

                }
                catch (Exception e) {
                    e.printStackTrace();
                    response = (newGrade.getStudent().getFullName() + "'s new grade could not be added to the database");
                    System.out.println(response);
                }
            }
            else if (request.startsWith("Save Question")) {
                Question newQuestion = ((Question) (message.getData()));
                try {
                    Question questionToSave=new Question();

                    List<Course>courses=getCourses();
                    List<Course>coursesToSave=new ArrayList<>();
                    for(Course item: courses)
                    {
                        for (Course item1: newQuestion.getCourses()){
                            if(item1.getId().equals(item.getId())){
                                coursesToSave.add(item);
                            }
                        }
                    }
                    questionToSave.setCourses(coursesToSave);

                    List<Subject>subjects=getSubjects();
                    for(Subject item: subjects)
                    {
                        if(newQuestion.getCourses().get(0).getId().equals(item.getId()))
                            questionToSave.setSubject(item);
                    }

                    questionToSave.setAnswers(newQuestion.getAnswers());

                    questionToSave.setCorrectAnswer(newQuestion.getCorrectAnswer());

                    questionToSave.setQuestionData(newQuestion.getQuestionData());

                    questionToSave.setStudentNote(newQuestion.getStudentNote());

                    questionToSave.setTeacherNote(newQuestion.getTeacherNote());

                    List<ExamForm>examForms=new ArrayList<>();
                    questionToSave.setExamForm(examForms);
                    String codeToSave=createCodeOfQuestion(newQuestion.getSubject());
                    questionToSave.setQuestionID(codeToSave);
                    session.save(questionToSave);
                    session.flush();

                    response = ("Question added successfully");
                    message.setMessage(response);
                    client.sendToClient(message);
                    System.out.println(response);

                }
                catch (Exception e) {
                    e.printStackTrace();
                    response = ("new question could not be added to the database");
                    System.out.println(response);
                }
            }
            else if (request.startsWith("Add ExamForm")) { // Added by Ilan
                ExamForm newExamForm = ((ExamForm) (message.getData()));
                // generate exam code
                String examCode = createCodeOfExam(newExamForm); // TODO: Ilan- Added in 17.6, Check if it works
                System.out.println("Exam code: " + examCode);
                newExamForm.setExamFormID(examCode);
                try {
                    session.save(newExamForm);
                    session.flush();
                    response = ("Success: new ExamForm in " + newExamForm.getCourse() + " was successfully added to the database");
                    message.setMessage(response);
                    client.sendToClient(message);
                    System.out.println(response);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    response = ("ExamForm in " + newExamForm.getCourse() + " could not be added to the database");
                    System.out.println(response);
                }
            }
            // Lior's addition
            else if (request.startsWith("Change Student Exam")) {
                StudentExam studentExam = ((StudentExam) (message.getData()));
                try {
                    // draw student from database and update him
                    StudentExam studentExamToChange = session.get(StudentExam.class, studentExam.getID());
                    studentExamToChange.update(studentExam);
                    session.update(studentExamToChange);
                    session.flush();

                    // Update exam's stats
                    ClassExam exam = studentExamToChange.getClassExam();
                    exam.UpdateStudentExam(studentExamToChange);
                    exam = OperationUtils.UpdateClassExamStats(exam);
                    session.update(exam);
                    session.flush();
                    response = ("Success: StudentExam Approved");
                    message.setMessage(response);
                    message.setData(exam.getTeacher());
                    client.sendToClient(message);
                    System.out.println(response);

                }
                catch (Exception e) {
                    e.printStackTrace();
                    response = ("Failure: Failed to save StudentExam");
                    System.out.println(response);
                }
            }
            // Liad's addition, but it was previously lost in a merge ;(
            else if (request.startsWith("Get Live Exams")) {
                List<ClassExam> listOfAllExams = retrieveClassExam();
                response = ("Live Exams");
                message.setMessage(response);
                message.setData(listOfAllExams);
                client.sendToClient(message);
            }
            // Alon's addition, to get all exams that exist for the sake of stats and reports
            else if (request.startsWith("Retrieve all class exams")) {
                System.out.println("Server attempting to retrieve all class exams.");
                List<ClassExam> listOfAllExams = retrieveClassExam();
                response = ("Success: Retrieved ALL class exams");
                message.setMessage(response);
                message.setData(listOfAllExams);
                client.sendToClient(message);
            }
            else {
                //we got a message from client we couldn't identify, so we will send back to all clients the message
                message.setMessage(request);
                response = "[Unrecognized Message]";
                client.sendToClient(message);
            }
            transmission.setResponse(response);
            EventBus.getDefault().post(new TransmissionEvent(transmission));
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
        // Check if there were new changes in the database before commit
        if (session.getTransaction().getStatus().equals(TransactionStatus.ACTIVE)) {
            session.getTransaction().commit();
        }
        //session.close();
    }

    private Boolean LogUserOut(Person user) {
        boolean userRemoved = LoggedInUsers.remove(user);
        System.out.println("user: " + (user.getFullName() + " removed: " + userRemoved));
        return userRemoved;
    }

    private Person retrieveUser(String email) {
        CriteriaBuilder builder = session.getCriteriaBuilder();

        // TODO: refactor to a more generic version
        // TODO: check if user is already logged in

        try {
            CriteriaQuery<Teacher> query = builder.createQuery(Teacher.class);
            Root<Teacher> root = query.from(Teacher.class);
            query.where(builder.equal(root.get("email"), email));
            Person user = session.createQuery(query).getSingleResult();
            return user;
        }
        catch (Exception e) {
            try {
                CriteriaQuery<Student> query = builder.createQuery(Student.class);
                Root<Student> root = query.from(Student.class);
                query.where(builder.equal(root.get("email"), email));
                Person user = session.createQuery(query).getSingleResult();
                return user;
            }
            catch (Exception e2) {
                try {
                    CriteriaQuery<Principal> query = builder.createQuery(Principal.class);
                    Root<Principal> root = query.from(Principal.class);
                    query.where(builder.equal(root.get("email"), email));
                    Person user = session.createQuery(query).getSingleResult();
                    return user;
                }
                catch (Exception e3) {
                    System.out.println("User not found");
                    return null;
                }
            }

        }

    }

    private String createCodeOfExam(ExamForm examForm){
        List<ExamForm> examsFormsForCourse = getExamFormForCourse(examForm.getCourse());
        int size = examsFormsForCourse.size();

        String subjectID = OperationUtils.IDZeroPadding(String.valueOf(examForm.getSubject().getId()),2);
        String courseID = OperationUtils.IDZeroPadding(String.valueOf(examForm.getCourse().getId()),2);
        String examFormNumber = OperationUtils.IDZeroPadding(String.valueOf(size),2);
        return subjectID+courseID+examFormNumber;
    }

    private StudentExam getStudentExamFromClassExam(int studentID, int classExamID) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<StudentExam> query = builder.createQuery(StudentExam.class);
        Root<StudentExam> root = query.from(StudentExam.class);
        query.where(builder.and(builder.equal(root.get("student"), studentID), builder.equal(root.get("classExam"), classExamID)));
        StudentExam studentExam = session.createQuery(query).getSingleResult();
        return studentExam;
    }
    public String createCodeOfQuestion(Subject subject){
        List<Question>questionsOfSubject=getQuestionsBySubject(subject.getId());
        int size=questionsOfSubject.size();

        String subCode= OperationUtils.IDZeroPadding(String.valueOf(subject.getId()),2);
        String questionCode=OperationUtils.IDZeroPadding(String.valueOf(size),3);
        return subCode+questionCode;
    }

    private List<Question> getQuestionsBySubject(long SubjectID)
    {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Subject> query = builder.createQuery(Subject.class);
        Root<Subject> root = query.from(Subject.class);
        query.where(builder.equal(root.get("ID"), SubjectID));
        Subject subject = session.createQuery(query).getSingleResult();
        return subject.getQuestions();
    }

    private List<ClassExam> getExamsForStudent(int studentID) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Student> query = builder.createQuery(Student.class);
        Root<Student> root = query.from(Student.class);
        query.where(builder.equal(root.get("ID"), studentID));
        Student student = session.createQuery(query).getSingleResult();
        return student.getClassExams();
    }

    private List<ClassExam> getExamsForCourse(Course course) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ClassExam> query = builder.createQuery(ClassExam.class);
        Root<ClassExam> root = query.from(ClassExam.class);
        query.where(builder.equal(root.get("course"), course));
        List<ClassExam> examForms = session.createQuery(query).getResultList();
        return examForms;
    }

    private List<ClassExam> getExamsForSubjects(Subject subject) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ClassExam> query = builder.createQuery(ClassExam.class);
        Root<ExamForm> root = query.from(ExamForm.class);
        query.where(builder.equal(root.get("subject"), subject));
        List<ClassExam> classExams = session.createQuery(query).getResultList();
        return classExams;
    }
    ///STILL Dont Know if work!!
    /*private ExtraTime getExtraTimeForClassExam(ClassExam exam) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ClassExam> query = builder.createQuery(ClassExam.class);
        Root<ClassExam> root = query.from(ClassExam.class);
        query.where(builder.equal(root.get("ID"), exam));
        ExtraTime extraTime = (ExtraTime) session.createQuery(query).getResultList();
        return extraTime;
    }*/

    // function to get the ExtraTime for a specific ClassExam
    private ExtraTime getExtraTimeForClassExam(ClassExam exam) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ExtraTime> query = builder.createQuery(ExtraTime.class);
        Root<ExtraTime> root = query.from(ExtraTime.class);
        query.where(builder.equal(root.get("classExam"), exam));
        ExtraTime extraTime = session.createQuery(query).getSingleResult();
        return extraTime;
    }

    private List<ExamForm> getExamFormForSubjects(Subject subject) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ExamForm> query = builder.createQuery(ExamForm.class);
        Root<ExamForm> root = query.from(ExamForm.class);
        query.where(builder.equal(root.get("subject"), subject));
        List<ExamForm> classExams = session.createQuery(query).getResultList();
        return classExams;
    }

    private List<ExamForm> getExamFormForCourse(Course course) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ExamForm> query = builder.createQuery(ExamForm.class);
        Root<ExamForm> root = query.from(ExamForm.class);
        query.where(builder.equal(root.get("course"), course));
        List<ExamForm> examForms = session.createQuery(query).getResultList();
        return examForms;
    }

    private List<Question> getQuestionsForCourse(Course course) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Question> query = builder.createQuery(Question.class);
        Root<Question> root = query.from(Question.class);
        //query.where(builder.equal(root.get("course"), course));
        query.where(builder.isMember(course, root.get("courses")));
        List<Question> questions = session.createQuery(query).getResultList();
        return questions;
    }

    private List<Subject> getSubjects() {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Subject> query = builder.createQuery(Subject.class);
        query.from(Subject.class);
        List<Subject> subjects = session.createQuery(query).getResultList();
        return subjects;
    }

    private List<Subject> getSubjects(int iTeacherid) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Teacher> query = builder.createQuery(Teacher.class);
        Root<Teacher> root = query.from(Teacher.class);
        query.where(builder.equal(root.get("ID"), iTeacherid));
        List<Subject> subjects = session.createQuery(query).getSingleResult().getSubjects();
        return subjects;
    }

    private List<Course> getCourses(int iTeacherid) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Teacher> query = builder.createQuery(Teacher.class);
        Root<Teacher> root = query.from(Teacher.class);
        query.where(builder.equal(root.get("ID"), iTeacherid));
        Teacher teacher = session.createQuery(query).getSingleResult();
        return teacher.getCourses();
    }

    private List<ClassExam> retrieveClassExam() {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ClassExam> query = builder.createQuery(ClassExam.class);
        query.from(ClassExam.class);
        List<ClassExam> exams = session.createQuery(query).getResultList();
        System.out.println("Checking for dead exams");
        return exams;
    }

    private ClassExam retrieveClassExam(int classExamID) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ClassExam> query = builder.createQuery(ClassExam.class);
        Root<ClassExam> root = query.from(ClassExam.class);
        query.where(builder.equal(root.get("ID"), classExamID));
        ClassExam exam = session.createQuery(query).getSingleResult();
        return exam;
    }


    private List<ExtraTime> getExtraTime() {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ExtraTime> query = builder.createQuery(ExtraTime.class);
        query.from(ExtraTime.class);
        List<ExtraTime> extraTime = session.createQuery(query).getResultList();
        return extraTime;
    }

    private List<Course> getCourses() {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Course> query = builder.createQuery(Course.class);
        query.from(Course.class);
        List<Course> courses = session.createQuery(query).getResultList();
        return courses;
    }

    private List<Principal> getPrincipals() {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Principal> query = builder.createQuery(Principal.class);
        query.from(Principal.class);
        List<Principal> principals = session.createQuery(query).getResultList();
        return principals;
    }

    private Student getStudent(int iStudentID) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Student> query = builder.createQuery(Student.class);
        Root<Student> root = query.from(Student.class);
        query.where(builder.equal(root.get("ID"), iStudentID));
        Student student = session.createQuery(query).getSingleResult();
        return student;
    }

    private Teacher getTeacher(int iTeacherID) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Teacher> query = builder.createQuery(Teacher.class);
        Root<Teacher> root = query.from(Teacher.class);
        query.where(builder.equal(root.get("ID"), iTeacherID));
        Teacher teacher = session.createQuery(query).getSingleResult();
        return teacher;
    }

    private List<StudentExam> retrieveStudentExams(int studentID) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Student> query = builder.createQuery(Student.class);
        Root<Student> root = query.from(Student.class);
        query.where(builder.equal(root.get("ID"), studentID));
        Student student = session.createQuery(query).getSingleResult();
        return student.getStudentExam();
    }

    private Object retrieveGrades(int iStudentID) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Grade> query = builder.createQuery(Grade.class);
        Root<Grade> gradeRoot = query.from(Grade.class);
        Join<Grade, Student> studentJoin = gradeRoot.join("student");
        query.where(builder.equal(studentJoin.get("ID"), iStudentID));
        List<Grade> grades = session.createQuery(query).getResultList();
        return grades;
    }

    public void sendToAllClients(Message message) {
        try {
            for (SubscribedClient SubscribedClient : SubscribersList) {
                SubscribedClient.getClient().sendToClient(message);
            }
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
