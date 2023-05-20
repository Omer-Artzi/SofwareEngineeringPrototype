package il.cshaifasweng.OCSFMediatorExample.client;


import java.awt.event.ActionEvent;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.NewSubscriberEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Student;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;


public class PrimaryController {
	@FXML
	private int msgId;
	@FXML
	private Button getGradesButton;
	@FXML
	private  TableView<Student> StudentsTV;
	@FXML
	private TableColumn<Student, SimpleStringProperty> IDColumn;
	@FXML
	private TableColumn<Student,SimpleStringProperty> NameColumn;
	@FXML
	private Button addStudentButton;
	@FXML
	private Button refreshButton;

	@Subscribe
	public void getStarterData(NewSubscriberEvent event) {
		try {
			Message message = new Message(msgId, "Get Students");
			SimpleClient.getClient().sendToServer(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@FXML
	public void requestGrades() throws IOException
	{
		if(StudentsTV.getSelectionModel().getSelectedItem() != null) {
			SimpleChatClient.setRoot("secondary");
			Message message = new Message(1, "Get Grades: " + StudentsTV.getSelectionModel().getSelectedItem().getID());
			SimpleClient.getClient().sendToServer(message);
		}
		else {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error!");
			alert.setHeaderText("Error: No Student Was Chosen");
			alert.show();
		}
	}

	@FXML
	void initialize() {
		EventBus.getDefault().register(this);
		IDColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
		NameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
		StudentsTV.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		List<Student> studentList = SimpleClient.getClient().getStudents();
		if(studentList != null) {
			StudentsTV.getItems().addAll(studentList);
		}
	}
	@Subscribe
	public void setStudents(studentMessageEvent message)
	{
		List<Student> studentList = message.getStudents();
		if(studentList != null) {
			StudentsTV.getItems().clear();
			SimpleClient.getClient().setStudents(studentList);
			StudentsTV.getItems().addAll(studentList);
		}
		else
		{
			JOptionPane.showMessageDialog(null, "No students in the Database", "Database Error ", JOptionPane.WARNING_MESSAGE);
		}
	}
	@FXML
	public void addStudent() throws IOException {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Student Name");
		dialog.setHeaderText("Enter the student's name:");
		dialog.showAndWait().ifPresent(name -> {
			// Process the entered student name
			Message message = new Message(msgId,"Add Student: " + name);
			Student newStudent = new Student(name);
			message.setData(newStudent);
			try {
				SimpleClient.getClient().sendToServer(message);
				List<Student> students = StudentsTV.getItems();
				newStudent.setID(students.size()+1);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		});

	}
	@FXML
	public void refreshStudents() throws IOException {
		Message message = new Message(1, "Get Students");
		SimpleClient.getClient().sendToServer(message);
	}
	@Subscribe
	public void callbackAddStudent(AddStudentSuccesEvent event)
	{
		StudentsTV.getItems().add(event.getStudent());
		StudentsTV.refresh();
	}

}
