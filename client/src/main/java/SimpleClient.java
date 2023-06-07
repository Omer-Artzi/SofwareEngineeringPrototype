import Entities.*;
import Events.*;
import javafx.scene.Scene;
import ocsf.AbstractClient;
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
        this.user = user;
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
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
            ManualExamEvent stMsg = new ManualExamEvent();
            EventBus.getDefault().post(stMsg);
        }
        else if (messageText.startsWith("1Subjects of")) { //Added by Ilan 30.5
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
        else if (messageText.startsWith("ExtraTimeRequest data")) {////
            System.out.println("SelectedClassExamEvent in client");
            List<Object> data = (List<Object>) message.getData();
            if (data == null) {
                System.out.println("Empty exam in client");
            }
            if (((List<Principal>) data.get(1)).isEmpty()) {
                System.out.println("Empty principles in client");
            }
            System.out.println("SelectedClassExamEvent in client2");
            if (message.getData() == null || ((List<Object>) (message.getData())).isEmpty())
                System.out.println("Somethings wrong with message");
            SelectedClassExamEvent stMsg = new SelectedClassExamEvent((List<Object>) message.getData());
            if (stMsg == null)
                System.out.println("the event is null");
            EventBus.getDefault().post(stMsg);
        }
        else if (messageText.startsWith("Principles")) {
            PrinciplesMessageEvent stMsg = new PrinciplesMessageEvent((List<Principal>) message.getData());
            EventBus.getDefault().post(stMsg);
        }
        else if (messageText.startsWith("Live Exams")) {
            System.out.println("Live exams in client");
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
			System.out.println("In Client: approved");
			ExtraTime extraTime=(ExtraTime) message.getData();
			PrincipalApproveEvent approveEvent =new PrincipalApproveEvent((ExtraTime) message.getData());
			if(relevantUser(extraTime,"approve")) {
				approveEvent.show();
			}
        }
        else if (messageText.startsWith("Extra time rejected")) {
			ExtraTime extraTime=(ExtraTime) message.getData();
			PrincipalRejectEvent rejectEvent =new PrincipalRejectEvent((ExtraTime) message.getData());
			if(relevantUser(extraTime,"reject")) {
				rejectEvent.show();
			}
		}
		else if (messageText.startsWith("Extra Time Requested")) {
			System.out.println("IN Client Extra Time Requested");
			ExtraTime extraTime=(ExtraTime) message.getData();
			NotificationEvent notification =new NotificationEvent((ExtraTime) message.getData());
			if(relevantUser(extraTime,"request")) {
				notification.show();
			}
		}
        else if (messageText.startsWith("Exams in ")){
			EventBus.getDefault().post(new ExamMessageEvent((List<ClassExam>)message.getData()));
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
        else if (messageText.startsWith("Success")) {
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
				System.out.println("In relevantUser: approve");
				System.out.println(("name: "+user.getFullName()));
				if (user instanceof Student) {
					List<Student> students = extraTime.getExam().getStudents();
					for (Student item : students) {
						if (user.equals(item))
							return true;
					}
				}
				else if (user instanceof Teacher) {
					System.out.println("In relevantUser: approve: Teacher");
					Teacher teacher = extraTime.getTeacher();
					if (teacher.equals(user))
						return true;
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
            //JOptionPane.showMessageDialog(null,"Could not Connect to Server", "Connection Error",JOptionPane.WARNING_MESSAGE);
        }
    }
}
