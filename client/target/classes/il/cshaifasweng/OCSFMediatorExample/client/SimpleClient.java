package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import javax.swing.*;
import java.util.Collections;
import java.util.List;

public class SimpleClient extends AbstractClient {

	private static SimpleClient client = null;
	private List<Student> students;

	private SimpleClient(String host, int port) {
		super(host, port);
	}

	@Override
	protected void handleMessageFromServer(Object msg){
		Message message = (Message) msg;
		if(message.getMessage().startsWith("Students")){
			studentMessageEvent stMsg = new studentMessageEvent(message);
			List<Student> students = (List<Student>) message.getData();
			Collections.sort(students);
			stMsg.setStudents(students);
			EventBus.getDefault().post(stMsg);
		}else if(message.getMessage().startsWith("Grades")){
			GradeMessageEvent stMsg = new GradeMessageEvent(message);
			Student student = (Student) message.getData();
			List<Grade> grades = student.getGrades();
			Collections.sort(grades);
			stMsg.setStudent(student);
			if(grades != null && !(grades.isEmpty()))
			{
				stMsg.setGrades(grades);

			}
			else
			{
				String warning = "The student's grades could not be found(or there aren't any)";
				JOptionPane.showMessageDialog(null, warning, "Database Error", JOptionPane.WARNING_MESSAGE);
			}
			EventBus.getDefault().post(stMsg);

		}  else if(message.getMessage().equals("client added successfully")){
			EventBus.getDefault().post(new NewSubscriberEvent(message));
		}else if(message.getMessage().equals("Error! we got an empty message")){
			EventBus.getDefault().post(new EditGradeSuccessEvent(message));
		}
		else if(message.getMessage().startsWith("Grade Saved")){
			EventBus.getDefault().post(new EditGradeSuccessEvent((Grade)(message.getData())));
		}else if(message.getMessage().startsWith("Success: Student")){
			EventBus.getDefault().post(new AddStudentSuccesEvent((Student)(message.getData())));
		}else if(message.getMessage().startsWith("Success: Grade")){
			EventBus.getDefault().post(new AddGradeSuccesEvent((Grade)(message.getData())));
		}else if(message.getMessage().startsWith("Failed: Student")){
			String warning = "The student could not be saved";
			JOptionPane.showMessageDialog(null,warning,"Database Error",JOptionPane.WARNING_MESSAGE);
		}else if(message.getMessage().startsWith("Failed: Grade")) {
			String warning = "The grade could not be saved";
			JOptionPane.showMessageDialog(null, warning, "Database Error", JOptionPane.WARNING_MESSAGE);
		}else if(message.getMessage().startsWith("Failed: Couldn't")) {
			String warning = "The client could not retrieve data from the server";
			JOptionPane.showMessageDialog(null, warning, "Database Error", JOptionPane.WARNING_MESSAGE);
		}else if(message.getMessage().equals("Server is closed")) {
			String warning = "further updates cannot be made until connection is re-established";
			JOptionPane.showMessageDialog(null, warning, "Error: The Server Was Closed ", JOptionPane.WARNING_MESSAGE);
		}
		else {
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
