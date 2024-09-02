package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Student;
import model.services.StudentService;

public class StudentListController implements Initializable, DataChangeListener {

	private StudentService service;

	@FXML
	private TableView<Student> tableViewStudent;

	@FXML
	private TableColumn<Student, Integer> tableColumnId;

	@FXML
	private TableColumn<Student, String> tableColumnName;

	@FXML
	private TableColumn<Student, String> tableColumnCpf;

	@FXML
	private TableColumn<Student, String> tableColumnPhone;

	@FXML
	private TableColumn<Student, Date> tableColumnBirthDate;
	
	@FXML
	private TableColumn<Student, Integer> tableColumnPeriod;

	@FXML
	private TableColumn<Student, Student> tableColumnEDIT;

	@FXML
	private TableColumn<Student, Student> tableColumnREMOVE;

	@FXML
	private Button btNew;

	private ObservableList<Student> obsList;

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Student obj = new Student();
		createDialogForm(obj, "/gui/StudentForm.fxml", parentStage);
	}

	public void setStudentService(StudentService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	public void updateTableView() {
		if (service == null)
			throw new IllegalStateException("Service was null!");
		List<Student> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewStudent.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
		tableColumnPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		//Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
		tableColumnPeriod.setCellValueFactory(new PropertyValueFactory<>("period"));
		
		
		

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewStudent.prefHeightProperty().bind(stage.heightProperty());
	}

	private void createDialogForm(Student obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			StudentFormController controller = loader.getController();
			controller.setStudent(obj);
			controller.setStudentService(new StudentService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter teacher data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();

	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Student, Student>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Student obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> createDialogForm(obj, "/gui/StudentForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Student, Student>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Student obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Student obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Comfirmation", "re you sure to delete?");
		
		if (result.get() == ButtonType.OK) {
			if(service == null)
				throw new IllegalStateException("Service was null!");
			try {
				service.remove(obj);
				updateTableView();
			}
			catch (DbIntegrityException e) {
				Alerts.showAlert("Error remove object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
