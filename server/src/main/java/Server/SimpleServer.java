package Server;
import Server.Events.ApiResponse;
import Server.Events.ClientUpdateEvent;
import Server.Events.ResponseQuestion;

import com.google.gson.Gson;
import com.fasterxml.jackson.databind.ObjectMapper;
import Server.Events.*;
import Server.ocsf.AbstractServer;
import Server.ocsf.ConnectionToClient;
import Server.ocsf.SubscribedClient;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import Entities.*;
import org.mindrot.jbcrypt.BCrypt;

import static java.lang.Thread.sleep;

public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();
	static Session session;
	private static int transmissionID = 0;
	private static SessionFactory  sessionFactory;

	public static SessionFactory getSessionFactory() throws HibernateException, InterruptedException {
		if(sessionFactory == null) {
			Configuration configuration = new Configuration();
			configuration.addAnnotatedClass(Student.class);
			configuration.addAnnotatedClass(Grade.class);
			configuration.addAnnotatedClass(Subject.class);
			configuration.addAnnotatedClass(Course.class);
			configuration.addAnnotatedClass(Teacher.class);
			configuration.addAnnotatedClass(Question.class);
			configuration.addAnnotatedClass(ExamForm.class);
			configuration.addAnnotatedClass(ClassExam.class);
			configuration.addAnnotatedClass(Person.class);


			ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
					.applySettings(configuration.getProperties())
					.build();

			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		}
		return  sessionFactory;
	}
	public SimpleServer(int port) {
		super(port);
		try{
			EventBus.getDefault().register(this);
			SessionFactory sessionFactory = getSessionFactory();
			session = sessionFactory.openSession();
			session.beginTransaction();
			//DataGenerator.generateData();
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








	@Subscribe
	public void CloseServer(TerminationEvent event) throws IOException {
		Message message = new Message(1,"Server is closed");
		sendToAllClients(message);
		this.close();
	}

	//Generating grades and saving in the SQL server


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
			}else if(request.startsWith("Login")){
				List<String> credentials = (List<String>)message.getData();
				String password = credentials.get(1);
				Person user = retrieveUser(credentials.get(0));
				if(BCrypt.checkpw(password, user.getPassword()) && user != null)
				{
					response = "Success: User found";
					message.setData(user);
				}
				else
				{
					response = "Fail: User not found";
				}
				message.setMessage(response);
				client.sendToClient(message);
			} else if(request.startsWith("Get Subjects")){
				response ="Subjects";
				message.setMessage(response);
				message.setData(retrieveSubjects());
				client.sendToClient(message);
			} else if (message.getMessage().startsWith("Extra time request"))
			{
				response = "Extra Time Requested";
				message.setMessage(response);
				message.setData(new ExtraTimeRequestEvent((ExtraTime)message.getData()));
				sendToAllClients(message);
			}else if (message.getMessage().startsWith("Extra time approved"))
			{
				response = "Extra time approved";
				message.setMessage(response);
				sendToAllClients(message);

			}
			else if (message.getMessage().startsWith("Exam approved")) {
				try {
					session.save((ClassExam)message.getData());
					response = "Exam saved successfully";
				}
				catch (Exception e)
				{
					response = "Failed to save exam";
				}
				message.setMessage(response);
				client.sendToClient(message);
			} else if(request.startsWith("Get Exams Forms for Subject")){
				response ="Exams in Subject " + ((Subject)(message.getData())).getName();
				message.setMessage(response);
				message.setData(retrieveExamsForSubjects((Subject)(message.getData())));
				client.sendToClient(message);
			}else if(request.startsWith("Get Exams Forms for Course")){
				response ="Exams in Course " + ((Course)(message.getData())).getName();
				message.setMessage(response);
				message.setData(retrieveExamsForCourse((Course)(message.getData())));
				client.sendToClient(message);
			}
			//Client asked for the student list, we will pull it from the SQL server and send it over
			else if(request.startsWith("Get Students")){
				response ="Students";
				message.setMessage(response);
				message.setData(retrievetudents());
				client.sendToClient(message);
			}
			//Client asked for the grades list of a certain student, we will pull it from the SQL server and send it over
			else if(request.startsWith("Get Grades")){
				String studentID = request.substring(12);
				int iStudentID = Integer.parseInt(studentID);
				Student student = retreiveStudent(iStudentID);
				response = ("Grades of " + student.getFullName());
				message.setMessage(response);
				message.setData(student);
				client.sendToClient(message);
			}
			//The user decided to update a grade, we will update the SQL server and send the new grade list
			else if(request.startsWith("Change Grade"))
			{
				try {
					Grade newGrade = ((Grade) (message.getData()));
					response = "Grade Saved: " + newGrade.getStudent().getFullName() + "'s grade in " + newGrade.getCourse() + " was changed to " + newGrade.getGrade();
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
					response = ("Success: " + newStudent.getFullName() + " was successfully added to the database");
					message.setMessage(response);
					client.sendToClient(message);
				}
				catch (Exception e)
				{
					response = (newStudent.getFullName() + " could not be added to the database");
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
					response = ("Success: " + newGrade.getStudent().getFullName() + "'s grade in " + newGrade.getCourse() + ": " + newGrade.getSubject() + " was successfully added to the database");
					message.setMessage(response);
					client.sendToClient(message);
					System.out.println(response);

				}
				catch (Exception e)
				{
					e.printStackTrace();
					response = (newGrade.getStudent().getFullName() + "'s new grade could not be added to the database");
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

	private Person retrieveUser(String email) {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		try {
			CriteriaQuery<Teacher> query = builder.createQuery(Teacher.class);
			Root<Teacher> root = query.from(Teacher.class);
			query.where(builder.equal(root.get("email"), email));
			Person user = session.createQuery(query).getSingleResult();
			return user;
		}
		catch (Exception e)
		{
			CriteriaQuery<Student> query = builder.createQuery(Student.class);
			Root<Student> root = query.from(Student.class);
			query.where(builder.equal(root.get("email"), email));
			Person user = session.createQuery(query).getSingleResult();
			return user;
		}

	}


	private List<ExamForm> retrieveExamsForCourse(Course course) {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ExamForm> query = builder.createQuery(ExamForm.class);
		Root<ExamForm> root = query.from(ExamForm.class);
		query.where(builder.equal(root.get("course"), course));
		List<ExamForm> examForms = session.createQuery(query).getResultList();
		return examForms;
	}
	private List<ExamForm> retrieveExamsForSubjects(Subject subject) {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ExamForm> query = builder.createQuery(ExamForm.class);
		Root<ExamForm> root = query.from(ExamForm.class);
		query.where(builder.equal(root.get("subject"), subject));
		List<ExamForm> examForms = session.createQuery(query).getResultList();
		return examForms;
	}

	private List<Subject> retrieveSubjects() {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Subject> query = builder.createQuery(Subject.class);
		query.from(Subject.class);
		List<Subject> subjects = session.createQuery(query).getResultList();
		return subjects;
	}

	private Student retrieveStudent(int iStudentID) {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Student> query = builder.createQuery(Student.class);
		Root<Student> root = query.from(Student.class);
		query.where(builder.equal(root.get("ID"), iStudentID));
		Student student = session.createQuery(query).getSingleResult();
		return student;
	}


	private Object retrieveGrades(int iStudentID){
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
	public List<Student> retrievetudents()
	{
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Student> query = builder.createQuery(Student.class);
		query.from(Student.class);
		List<Student> students = session.createQuery(query).getResultList();
		return students;
	}

	public Student retreiveStudent(int iStudentID)
	{
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Student> query = builder.createQuery(Student.class);
		Root<Student> gradeRoot = query.from(Student.class);
		query.where(builder.equal(gradeRoot.get("ID"), iStudentID));
		Student student = session.createQuery(query).getSingleResult();
		return student;
	}
	}




