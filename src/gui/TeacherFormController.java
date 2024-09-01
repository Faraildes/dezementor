package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Teacher;
import model.exceptions.ValidationException;
import model.services.TeacherService;

public class TeacherFormController implements Initializable {

	private Teacher entity;
	
	private TeacherService service;
	
	private List<DataChangeListener> dataChangeListerners = new ArrayList<>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private TextField txtCpf;
	
	@FXML
	private TextField txtPhone;
	
	@FXML
	private TextField txtSalary;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Label labelErrorCpf;
	
	@FXML
	private Label labelErrorPhone;
	
	@FXML
	private Label labelErrorSalary;
	
	@FXML
	private Button btCancel;
	
	@FXML
	private Button btSave;
	
	public void setTeacher (Teacher entity) {
		this.entity = entity;
	}
	
	public void setTeacherService(TeacherService service) {
		this.service = service;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListerners.add(listener);
	}
	
	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if(entity == null)
			throw new IllegalStateException("Entity was null!");
		if (service == null)
			throw new IllegalStateException("Service was null!");
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		}
		catch (ValidationException e) {
			setErrorMessage(e.getErrors());
		}
		
		catch (DbException e) {
			Alerts.showAlert("Error savin object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void notifyDataChangeListeners() {
		for(DataChangeListener listener : dataChangeListerners) {
			listener.onDataChanged();
		}		
	}

	private Teacher getFormData() {
		
		Teacher obj = new Teacher();
		
		ValidationException exception = new ValidationException("Validation errror");
				
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty!");
		}
		obj.setName(txtName.getText());
				
		if (txtCpf.getText() == null || txtCpf.getText().trim().equals("")) {
			exception.addError("cpf", "Field can't be empty!");
		}
		obj.setCpf(txtCpf.getText());
		
		if (txtPhone.getText() == null || txtPhone.getText().trim().equals("")) {
			exception.addError("phone", "Field can't be empty!");
		}
		obj.setPhone(txtPhone.getText());
		
		if (txtSalary.getText() == null || txtSalary.getText().trim().equals("")) {
			exception.addError("salary", "Field can't be empty!");
		}
		obj.setSalary(Utils.tryParseToDouble(txtSalary.getText()));
		
		if(exception.getErrors().size() > 0) 
			throw exception;
		
		return obj;
	}
	
	private void setErrorMessage(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if (fields.contains("name")) 
			labelErrorName.setText(errors.get("name"));
			
		if (fields.contains("cpf")) 
			labelErrorCpf.setText(errors.get("cpf"));
			
		if (fields.contains("phone")) 
			labelErrorPhone.setText(errors.get("phone"));
		
		if (fields.contains("salary")) 
			labelErrorSalary.setText(errors.get("salary"));
		
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
		Constraints.setTextFieldMaxLength(txtCpf, 11);
		Constraints.setTextFieldMaxLength(txtPhone, 15);
		Constraints.setTextFieldDouble(txtSalary);
	}
	
	public void updateFormeData() {
		if(entity == null) {
			throw new IllegalStateException("Entity was null!");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());		
	}
}
