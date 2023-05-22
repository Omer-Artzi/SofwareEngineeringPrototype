package il.cshaifasweng.OCSFMediatorExample.server;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.fasterxml.jackson.databind.ObjectMapper;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;
import com.github.javafaker.Faker;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();
	private static Session session;
	private static int transmissionID = 0;

	public static SessionFactory getSessionFactory() throws HibernateException {
		Configuration configuration = new Configuration();
		configuration.addAnnotatedClass(Student.class);
		configuration.addAnnotatedClass(Grade.class);
		configuration.addAnnotatedClass(Subject.class);
		configuration.addAnnotatedClass(Course.class);
		configuration.addAnnotatedClass(Teacher.class);
		configuration.addAnnotatedClass(Question.class);
		configuration.addAnnotatedClass(ExamForm.class);



		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
				.applySettings(configuration.getProperties())
				.build();

		return configuration.buildSessionFactory(serviceRegistry);
	}
	public SimpleServer(int port) {
		super(port);
		try{
			EventBus.getDefault().register(this);
			SessionFactory sessionFactory = getSessionFactory();
			session = sessionFactory.openSession();
			session.beginTransaction();
			//generateStudents();
			//generateGrades();
			generateData();
			//session.getTransaction().commit();
		}
		catch (Exception exception)
		{
			if (session != null)
			{
				session.getTransaction().rollback();
			}
			System.err.println("An error occurred, changes have been rolled back.");
			exception.printStackTrace();
			JOptionPane.showMessageDialog(null, "A connection to the database could not be formed, please check the MySQL Server is installed and running(Check Console for more info)", "Database Error", JOptionPane.WARNING_MESSAGE);
		}
	}

	private void generateData() throws IOException {
		School school = School.getInstance();
		ObjectMapper objectMapper = new ObjectMapper();
		SubjectWrapper subjects = objectMapper.readValue(new File("./src/main/resources/il/cshaifasweng/OCSFMediatorExample/server/SchoolSubjects.json"),SubjectWrapper.class);
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
			generateTeachers(subjectList);
			generateQuestions(subjectList);
			session.getTransaction().commit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private void generateQuestions(List<Subject> subjectList) {
		String requests[] = {"https://opentdb.com/api.php?amount=50&category=9&type=multiple",
				"https://opentdb.com/api.php?amount=50&category=17&type=multiple",
				"https://opentdb.com/api.php?amount=50&category=24&type=multiple",
				"https://opentdb.com/api.php?amount=50&category=23&type=multiple",
				"https://opentdb.com/api.php?amount=50&category=20&type=multiple",
				"https://opentdb.com/api.php?amount=50&category=26&type=multiple",
				"https://opentdb.com/api.php?amount=50&category=19&type=multiple",
				"https://opentdb.com/api.php?amount=50&category=27&type=multiple",
				"https://opentdb.com/api.php?amount=50&category=24&type=multiple",
				"https://opentdb.com/api.php?amount=50&category=18&type=multiple",};
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
					for(ResponseQuestion responseQuestion: questions)
					{
						Question question = new Question();
						responseQuestion.convert(question);
						question.setSubject(subjectList.get(i));
						randCourse = random.nextInt(subjectList.get(i).getCourses().size());
						question.setCourse(subjectList.get(i).getCourses().get(randCourse));
						session.save(question);
						session.flush();
					}
					System.out.println(questions);


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

	private void generateTeachers(List<Subject> subjects) {
		try {
			Faker faker = new Faker();
			Random random = new Random();
			int randomSubject, randomCourse;
			for (int i = 0; i < 50; i++) {
				String teacherFirstName = faker.name().firstName();
				String teacherLastName = faker.name().lastName();
				String teacherEmail = teacherFirstName + "_" + teacherLastName + "@gmail.com";
				String password = faker.internet().password();
				List<Course> courses = new ArrayList<>();
				for (int j = 0; j < 3; j++) {
					randomSubject = random.nextInt(subjects.size());
					Subject subject = subjects.get(randomSubject);
					for (int k = 0; k < 3; k++) {
						randomCourse = random.nextInt(subject.getCourses().size());
						courses.add(subject.getCourses().get(randomCourse));
					}
				}
				Teacher teacher = new Teacher(teacherFirstName, teacherLastName, teacherEmail, password, courses);
				for (Course course : courses) {
					course.getTeachers().add(teacher);
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

	@Subscribe
	public void CloseServer(TerminationEvent event) throws IOException {
		Message message = new Message(1,"Server is closed");
		sendToAllClients(message);
		session.getTransaction().commit();
		session.close();
		this.close();
	}

	//Generating grades and saving in the SQL server
	private void generateGrades() {
		List<Student> students = sendStudents();
		Faker faker = new Faker();
		Random r = new Random();
		for(Student student : students)
		{
			for(int i = 0; i < 8;i++ ) {
				Grade grade = new Grade(r.nextInt(100),faker.educator().course(),faker.pokemon().name() , student);
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
		try {
			//we got an empty message, so we will send back an error message with the error details.
			if (request.isBlank()){
				response = "Error! we got an empty message";
				message.setMessage(response);
				client.sendToClient(message);
			}
			else if(request.startsWith("Get Subjects")){
				response ="Subjects";
				message.setMessage(response);
				message.setData(getSubjects());
				client.sendToClient(message);
			}else if(request.startsWith("Get Exams Forms for Subject")){
				response ="Exams in " + ((Subject)(message.getData())).getName();
				message.setMessage(response);
				message.setData(getExamsForSubjects((Subject)(message.getData())));
				client.sendToClient(message);
			}else if(request.startsWith("Get Exams Forms for Course")){
				response ="Exams in " + ((Course)(message.getData())).getName();
				message.setMessage(response);
				message.setData(getExamsForCourse((Course)(message.getData())));
				client.sendToClient(message);
			}
			//Client asked for the student list, we will pull it from the SQL server and send it over
			else if(request.startsWith("Get Students")){
				response ="Students";
				message.setMessage(response);
				message.setData(sendStudents());
				client.sendToClient(message);
			}
			//Client asked for the grades list of a certain student, we will pull it from the SQL server and send it over
			else if(request.startsWith("Get Grades")){
				String studentID = request.substring(12);
				int iStudentID = Integer.parseInt(studentID);
				Student student = getStudent(iStudentID);
				response = ("Grades of " + student.getStudentName());
				message.setMessage(response);
				message.setData(student);
				client.sendToClient(message);



			}
			//The user decided to update a grade, we will update the SQL server and send the new grade list
			else if(request.startsWith("Change Grade"))
			{
				try {
					Grade newGrade = ((Grade) (message.getData()));
					response = "Grade Saved: " + newGrade.getStudent().getStudentName() + "'s grade in " + newGrade.getCourse() + " was changed to " + newGrade.getGrade();
					message.setMessage(response);
					session.merge(newGrade);
					session.flush();
					client.sendToClient(message);
				}
				catch (Exception e)
				{
					response = "Failed to save grade";
					message.setMessage(response);
					client.sendToClient(message);
				}
			}
			//we got a request to add a new client as a subscriber.
			else if (request.equals("add client")){
				SubscribersList.add(subscribedClient);
				response = "client added successfully";
				message.setMessage(response);
				client.sendToClient(message);
				EventBus.getDefault().post(new ClientUpdateEvent(SubscribersList.size()));
			}else if(request.startsWith("Client Closed")){
				response ="";
				for(SubscribedClient subscriber: SubscribersList)
				{
					if(subscriber.getClient().equals(client)) {
						SubscribersList.remove(subscriber);
						break;
					}

				}
				EventBus.getDefault().post(new ClientUpdateEvent(SubscribersList.size()));
			}
			else if(request.startsWith("Add Student")){
				Student newStudent = (Student) (message.getData());
				try {
					session.save(newStudent);
					session.flush();
					response = ("Success: " + newStudent.getStudentName() + " was successfully added to the database");
					message.setMessage(response);
					client.sendToClient(message);
				}
				catch (Exception e)
				{
					response = (newStudent.getStudentName() + " could not be added to the database");
				}
			}
			else if(request.startsWith("Add Grade")){
				Grade newGrade = ((Grade)(message.getData()));
				try {
					session.save(newGrade);
					Student updatedStudent = newGrade.getStudent();
					updatedStudent.getGrades().add(newGrade);
					session.merge(updatedStudent);
					session.flush();
					response = ("Success: " + newGrade.getStudent().getStudentName() + "'s grade in " + newGrade.getCourse() + ": " + newGrade.getSubject() + " was successfully added to the database");
					message.setMessage(response);
					client.sendToClient(message);
					System.out.println(response);

				}
				catch (Exception e)
				{
					e.printStackTrace();
					response = (newGrade.getStudent().getStudentName() + "'s new grade could not be added to the database");
					System.out.println(response);
				}
			}
			else{
				//we got a message from client we couldn't identify, so we will send back to all clients the message
				message.setMessage(request);
				response = "[Unrecognized Message]";
				client.sendToClient(message);
			}
			transmission.setResponse(response);
			EventBus.getDefault().post(new TransmissionEvent(transmission));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private List<ExamForm> getExamsForCourse(Course course) {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ExamForm> query = builder.createQuery(ExamForm.class);
		Root<ExamForm> root = query.from(ExamForm.class);
		query.where(builder.equal(root.get("course"), course));
		List<ExamForm> examForms = session.createQuery(query).getResultList();
		return examForms;
	}
	private List<ExamForm> getExamsForSubjects(Subject subject) {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ExamForm> query = builder.createQuery(ExamForm.class);
		Root<ExamForm> root = query.from(ExamForm.class);
		query.where(builder.equal(root.get("subject"), subject));
		List<ExamForm> examForms = session.createQuery(query).getResultList();
		return examForms;
	}

	private List<Subject> getSubjects() {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Subject> query = builder.createQuery(Subject.class);
		query.from(Subject.class);
		List<Subject> subjects = session.createQuery(query).getResultList();
		return subjects;
	}

	private Student getStudent(int iStudentID) {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Student> query = builder.createQuery(Student.class);
		Root<Student> root = query.from(Student.class);
		query.where(builder.equal(root.get("ID"), iStudentID));
		Student student = session.createQuery(query).getSingleResult();
		return student;
	}


	private Object sendGrades ( int iStudentID){
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
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	public void generateStudents() {
		Faker faker = new Faker();
		for(int  i = 0; i < 10;i++)
		{
			String name = faker.name().fullName();
			Student student = new Student(name);
			session.save(student);
			session.flush();
		}
	}
	public List<Student> sendStudents()
	{
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Student> query = builder.createQuery(Student.class);
		query.from(Student.class);
		List<Student> students = session.createQuery(query).getResultList();
		return students;
	}



}
