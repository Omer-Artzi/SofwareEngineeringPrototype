package Client;

import Client.Events.*;
import Client.ocsf.AbstractClient;
import Entities.SchoolOwned.*;
import Entities.Communication.Message;
import Entities.Communication.ExtraTime;
import Entities.Communication.NewSubscriberEvent;
import Entities.StudentOwned.Grade;
import Entities.StudentOwned.StudentExam;
import Entities.Users.Person;
import Entities.Users.Principal;
import Entities.Users.Student;
import Entities.Users.Teacher;
import Events.ExamSavedEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import org.greenrobot.eventbus.EventBus;

import javax.swing.*;
import java.io.IOException;
import java.util.List;


public class SimpleClient extends AbstractClient {
    private static Person user;
    private static SimpleClient client = null;
    private static String IP;
    private static int port;
    private List<Student> students;

    private SimpleClient(String host, int port) {
        super(host, port);
        SimpleChatClient.setClient(this);
    }

    public static String getIP() {
        return IP;
    }

    public static void setIP(String IP) {
        SimpleClient.IP = IP;
    }

    public static void setPortNum(int port) {
        SimpleClient.port = port;
    }

    public static Person getUser() {
        return user;
    }

    public void setUser(Person user) {
        SimpleClient.user = user;
    }

    @Override
    protected void handleMessageFromServer(Object msg) throws IOException {
        System.out.println("Converting message...");
        Message message = (Message) msg;
        String messageText = message.getMessage();
        System.out.println("Message Received: " + message.getMessage());
        if (messageText.startsWith("Students")) {
            studentMessageEvent stMsg = new studentMessageEvent(message);
            stMsg.setStudents((List<Student>) message.getData());
            EventBus.getDefault().post(stMsg);
        }
        else if (messageText.startsWith("class exams for student ID")) { //Added by Omer 3.6
            ExamMessageEvent stMsg = new ExamMessageEvent((List<ClassExam>) message.getData());
            EventBus.getDefault().post(stMsg);
        }
        else if (messageText.endsWith("has run out of time, it is now closed")) { //Added by Omer 3.6
            ExamEndedMessageEvent stMsg = new ExamEndedMessageEvent((ClassExam) message.getData());
            EventBus.getDefault().post(stMsg);
        }
        else if (messageText.startsWith("Manual Exam")) { //Added by Omer 3.6
            ExamEndedEvent stMsg = new ExamEndedEvent();
            EventBus.getDefault().post(stMsg);
        } else if (messageText.startsWith("Digital Exam")) {
            ExamEndedEvent stMsg = new ExamEndedEvent();
            EventBus.getDefault().post(stMsg);
        } else if (messageText.startsWith("1Subjects of")) { //Added by Ilan 30.5
            SubjectsOfTeacherMessageEvent stMsg = new SubjectsOfTeacherMessageEvent((List<Subject>) message.getData());
            EventBus.getDefault().post(stMsg);
        }
        else if (messageText.startsWith("1Courses of")) { //Added by Ilan 30.5
            CoursesOfTeacherEvent stMsg = new CoursesOfTeacherEvent((List<Course>) message.getData());
            EventBus.getDefault().post(stMsg);
        }
        else if (messageText.startsWith("Subjects")) {
            SubjectMessageEvent stMsg = new SubjectMessageEvent((List<Subject>) message.getData());
            EventBus.getDefault().post(stMsg);
        }
        else if (message.getMessage().startsWith("Extra Time Requests")) {    //Added by Liad 10/06
            ExtraTimeRequestsEvent stMsg = new ExtraTimeRequestsEvent((List<ExtraTime>) message.getData());
            EventBus.getDefault().post(stMsg);
        }
        else if (messageText.startsWith("ExtraTimeRequest data")) { //Added by liad
            System.out.println("SelectedClassExamEvent in client");
            List<Object> data = (List<Object>) message.getData();
            SelectedClassExamEvent stMsg = new SelectedClassExamEvent((List<Object>) message.getData());
            EventBus.getDefault().post(stMsg);
        }
        else if (messageText.startsWith("Principals")) {
            PrincipalsMessageEvent stMsg = new PrincipalsMessageEvent((List<Principal>) message.getData());
            EventBus.getDefault().post(stMsg);
        }
        else if (messageText.startsWith("Live Exams")) {
            LiveExamsEvent stMsg = new LiveExamsEvent((List<ClassExam>) message.getData());
            EventBus.getDefault().post(stMsg);
        }
        // TODO maybe change from LiveExamsEvent to something more appropriate
        else if (messageText.startsWith("Success: Retrieved ALL class exams")) {
            System.out.println("All class exams in client");
            LiveExamsEvent stMsg = new LiveExamsEvent((List<ClassExam>) message.getData());
            EventBus.getDefault().post(stMsg);
        }
        else if (messageText.startsWith("Student Exams For Student")) {
            StudentExamsMessageEvent stMsg = new StudentExamsMessageEvent((List<StudentExam>) message.getData());
            EventBus.getDefault().post(stMsg);
        }
        else if (messageText.startsWith("Grades")) {
            GradeMessageEvent stMsg = new GradeMessageEvent(message);
            Student student = (Student) message.getData();
            List<Grade> grades = student.getGrades();
            stMsg.setStudent(student);
            if (grades == null || grades.isEmpty()) {
                String warning = "The student's grades could not be found(or there aren't any)";
                JOptionPane.showMessageDialog(null, warning, "Database Error", JOptionPane.WARNING_MESSAGE);

            }
            else {
                stMsg.setGrades(grades);

            }
            EventBus.getDefault().post(stMsg);

		}
		else if (messageText.equals("client added successfully")) {
			EventBus.getDefault().post(new NewSubscriberEvent(message));
		}
		else if (messageText.startsWith("Extra time approved")) {
			ExtraTime extraTime=(ExtraTime) message.getData();
			PrincipalApproveEvent approveEvent =new PrincipalApproveEvent((ExtraTime) message.getData());
			if(relevantUser(extraTime,"approve")) {
                EventBus.getDefault().post(approveEvent);
                user = SimpleClient.getClient().getUser();
                if(!(user instanceof Student))
				    approveEvent.show();
                EventBus.getDefault().post(new PrincipalDecisionEvent((ExtraTime) message.getData()));
			}
        }
        else if (messageText.startsWith("Extra time rejected")) {
			ExtraTime extraTime=(ExtraTime) message.getData();
			PrincipalRejectEvent rejectEvent =new PrincipalRejectEvent((ExtraTime) message.getData());
			if(relevantUser(extraTime,"reject")) {
				rejectEvent.show();
                EventBus.getDefault().post(new PrincipalDecisionEvent((ExtraTime) message.getData()));
			}
		}
		else if (messageText.startsWith("Extra Time Requested")) {
			ExtraTime extraTime=(ExtraTime) message.getData();
			NotificationEvent notification =new NotificationEvent((ExtraTime) message.getData());
			if(relevantUser(extraTime,"request")) {
				notification.show();
			}
		}else if (messageText.startsWith("Exam Forms in ")){
            System.out.println("IN Client Exam Forms number: " + ((List<ExamForm>)message.getData()).size());
            ExamMessageEvent event = new ExamMessageEvent();
            event.setExamForms((List<ExamForm>)message.getData());
            EventBus.getDefault().post(event);
        }
        else if (messageText.startsWith("Class Exams in ")){
			EventBus.getDefault().post(new ExamMessageEvent((List<ClassExam>)message.getData()));
		}
        else if (messageText.startsWith("Question added successfully")){
            EndCreateQuestionEvent event=new EndCreateQuestionEvent("Question added successfully");
            //EventBus.getDefault().post(new GeneralEvent(new Message(0, "Success")));
            EventBus.getDefault().post(event);
        }
        else if (messageText.startsWith("new question could not be added to the database")){
            EventBus.getDefault().post(new EndCreateQuestionEvent( "question could not be added"));
            //EventBus.getDefault().post(new GeneralEvent(new Message(0, "failed")));
        }
		else if (messageText.startsWith("Success: new ExamForm")){
			EventBus.getDefault().post(new GeneralEvent(new Message(0, "Success")));
		}
        else if (messageText.equals("Error! we got an empty message")) {
            EventBus.getDefault().post(new ErrorEvent(message));
        }
        else if (messageText.startsWith("Grade Saved")) {
        }
        else if (messageText.startsWith("Success: StudentExam Approved")) {
            RefreshPerson event = new RefreshPerson("Success", (Person) message.getData());
            EventBus.getDefault().post(event);
        }
        else if (messageText.startsWith("all extra time requests")) {                             //////////!!!!!!!!!!
            extraTimeOfSpecificClassExam event = new extraTimeOfSpecificClassExam((ExtraTime) message.getData());
            System.out.println("In Client!!!!!");
            EventBus.getDefault().post(event);
        }
        else if (messageText.startsWith("Failure: Failed to save StudentExam")) {
            RefreshPerson event = new RefreshPerson("Failure", null);
            EventBus.getDefault().post(event);
            //EventBus.getDefault().post(new GeneralEvent(new Message(0, "Failure")));
        }
        else if (messageText.startsWith("Success: User logged in")) {
            EventBus.getDefault().post(new UserMessageEvent((Person) message.getData(), "Success"));
        }
        else if (messageText.startsWith("Fail: User")) {
            System.out.println("Fail : User");
            EventBus.getDefault().post(new UserMessageEvent((Person) message.getData(), "Fail"));
        }
        else if (messageText.startsWith("User retrieved")) {
            EventBus.getDefault().post(new UserMessageEvent((Person) message.getData(), "Success"));
        }
        else if (messageText.startsWith("User Failed to be retrieved")) {
            EventBus.getDefault().post(new UserMessageEvent((Person) message.getData(), "Fail"));
        }
        else if (messageText.startsWith("Success")) {
        }
        else if(message.getMessage().startsWith("Exam Saved Successfully"))
        {
            ExamSavedEvent event = new ExamSavedEvent();
            EventBus.getDefault().post(event);

        }
        else if (messageText.startsWith("Failed to save grade")) {
            String warning = "The grade could not be saved";
            JOptionPane.showMessageDialog(null, message, "Database Error", JOptionPane.WARNING_MESSAGE);
        }
        else if (messageText.equals("Server is closed")) {
            String warning = "further updates cannot be made until connection is re-established";
            JOptionPane.showMessageDialog(null, warning, "Error: The Server Was Closed ", JOptionPane.WARNING_MESSAGE);
        }
        else if (messageText.startsWith("Questions in Course")) {
            CourseQuestionsListEvent stMsg = new CourseQuestionsListEvent((List<Question>) message.getData());
            // System.out.println("Check");
            EventBus.getDefault().post(stMsg);
        }
        else {
            EventBus.getDefault().post(new MessageEvent(message));
        }
    }

    /* Check the user to send the decision of principal */
	public boolean relevantUser(ExtraTime extraTime,String type){

		System.out.println("In relevantUser " +type);
		System.out.println(("name: "+user.getFullName()));

		/* get the current user */
        Person user= null;
        try {
            user = SimpleClient.getClient().getUser();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(type.equals("request")) {
			if (user instanceof Principal) {
				List<Principal> principals = extraTime.getPrincipals();
				for (Principal item : principals) {
					if (user.equals(item))
						return true;
				}
			}
		}

		/*if the user is a teacher , anyway a notification will be sent */
		if(type.equals("reject")) {
			if (user instanceof Teacher) {
				Teacher teacher = extraTime.getTeacher();
				if (teacher.equals(user))
					return true;
			}
		}

		/* only if the event is reject, we check if the student exist in class exam list */
		 if(type.equals("approve")) {
				if (user instanceof Student) {
					List<Student> students = extraTime.getExam().getStudents();
					for (Student item : students) {
						if (user.equals(item))
							return true;
					}
				}
				else if (user instanceof Teacher) {
					Teacher teacher = extraTime.getTeacher();
                    return teacher.equals(user);
				}
			}
		return false;
	}

	public static SimpleClient getClient() throws IOException {
		if (client == null) {
			client = new SimpleClient(IP, port);
		}
		return client;
	}

	public List<Student> getStudents() {
		return students;
	}

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public void openClientConnect() throws IOException {
        try {
            client.openConnection();
            Message message = new Message(1, "add client");
            SimpleClient.getClient().sendToServer(message);
            System.out.println("Connection Successful, moving to homepage");
            SimpleChatClient.setScene(new Scene(SimpleChatClient.loadFXML("login"), 1024, 768));
            SimpleChatClient.getClientStage().setScene(SimpleChatClient.getScene());
            SimpleChatClient.getClientStage().centerOnScreen();
        }
        catch (Exception e) {
            System.out.println("Could not connect to server");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setContentText("Could not connect to server, please check with your admin that it is up");
            alert.showAndWait();
            //JOptionPane.showMessageDialog(null,"Could not Connect to Server", "Connection Error",JOptionPane.WARNING_MESSAGE);
        }
    }
}
