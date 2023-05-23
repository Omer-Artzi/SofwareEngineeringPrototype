package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;
import java.util.List;

public class SimpleClient extends AbstractClient {

	private static SimpleClient client = null;
	private List<Student> students;

	private SimpleClient(String host, int port) {
		super(host, port);
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		Message message = (Message) msg;
		if (message.getMessage().startsWith("Students")) {
			studentMessageEvent stMsg = new studentMessageEvent(message);
			stMsg.setStudents((List<Student>) message.getData());
			EventBus.getDefault().post(stMsg);
		} else if (message.getMessage().startsWith("Subjects")) {
			SubjectMessageEvent stMsg = new SubjectMessageEvent((List<Subject>) message.getData());
			EventBus.getDefault().post(stMsg);
		} else if (message.getMessage().startsWith("Grades")) {
			GradeMessageEvent stMsg = new GradeMessageEvent(message);
			Student student = (Student) message.getData();
			List<Grade> grades = student.getGrades();
			stMsg.setStudent(student);
			if (grades == null || grades.isEmpty()) {
				String warning = "The student's grades could not be found(or there aren't any)";
				JOptionPane.showMessageDialog(null, warning, "Database Error", JOptionPane.WARNING_MESSAGE);

			} else {
				stMsg.setGrades(grades);

			}
			EventBus.getDefault().post(stMsg);

		} else if (message.getMessage().equals("client added successfully")) {
			EventBus.getDefault().post(new NewSubscriberEvent(message));
		}else if(message.getMessage().startsWith("Exams in ")){
			EventBus.getDefault().post(new ExamMessageEvent((List<ExamForm>)message.getData()));
		}else if (message.getMessage().equals("Error! we got an empty message")) {
			EventBus.getDefault().post(new ErrorEvent(message));
		} else if (message.getMessage().startsWith("Grade Saved")) {
		} else if (message.getMessage().startsWith("Success")) {
		} else if (message.getMessage().startsWith("Failed to save grade")) {
			String warning = "The grade could not be saved";
			JOptionPane.showMessageDialog(null, message, "Database Error", JOptionPane.WARNING_MESSAGE);
		} else if (message.getMessage().equals("Server is closed")) {
			String warning = "further updates cannot be made until connection is re-established";
			JOptionPane.showMessageDialog(null, warning, "Error: The Server Was Closed ", JOptionPane.WARNING_MESSAGE);
		} else {
			EventBus.getDefault().post(new MessageEvent(message));
		}
	}

	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient("localhost", 3000);
		}
		return client;
	}

	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}

}
