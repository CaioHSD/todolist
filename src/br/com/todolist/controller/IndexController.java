package br.com.todolist.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import br.com.todolist.io.TarefaIO;
import br.com.todolist.model.StatusTarefa;
import br.com.todolist.model.Tarefa;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class IndexController implements Initializable, ChangeListener<Tarefa> {

	@FXML
	private Label lbDataR;

	@FXML
	private DatePicker dpDataPicker;

	@FXML
	private Label lbStatusTar;

	@FXML
	private Label lbComents;

	@FXML
	private TextField tfStatus;

	@FXML
	private Label lbClassTarefa;

	@FXML
	private TextField tfClassTarefa;

	@FXML
	private TextArea taComents;

	@FXML
	private Button btSalvar;

	@FXML
	private Button btAdiar;

	@FXML
	private Button btConcluir;

	@FXML
	private Button btExcluir;

	@FXML
	private Button btLimpar;

	@FXML
	private TableColumn<Tarefa, LocalDate> tcData;

	@FXML
	private TableColumn<Tarefa, String> tcTarefa;

	@FXML
	private TableView<Tarefa> tvTarefa;

	private List<Tarefa> tarefas;

	@FXML
	private Tarefa tarefa;

	@FXML
	void btAdiarClick(ActionEvent event) {

	}

	@FXML
	void btConcluirClick(ActionEvent event) {
		
	}

	@FXML
	void btExcluirClick(ActionEvent event) {

	}

	@FXML
	void btLimparClick(ActionEvent event) {
		
		limpar();
	}

	@FXML
	void btSalvarClick(ActionEvent event) {
		// VALIDANDO OS CAMPOS
		if (dpDataPicker.getValue() == null) {
			JOptionPane.showMessageDialog(null, "Informe uma data", "Informe", JOptionPane.ERROR_MESSAGE);
			dpDataPicker.requestFocus();
		} else if (tfStatus.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Informe o status da tarefa", "Informe", JOptionPane.ERROR_MESSAGE);
			dpDataPicker.requestFocus();
		} else if (tfClassTarefa.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Informe a importância dessa tarefa", "Informe",
					JOptionPane.ERROR_MESSAGE);
			dpDataPicker.requestFocus();
		} else if (dpDataPicker.getValue().isBefore(LocalDate.now())) {
			JOptionPane.showMessageDialog(null, "A data não deve ser anterior à data atual", "Data inválida",
					JOptionPane.ERROR_MESSAGE);
			dpDataPicker.requestFocus();
		} else {
			// instanciando a tarefa
			tarefa = new Tarefa();
			// popular tarefa
			tarefa.setDataCriacao(LocalDate.now());
			tarefa.setStatus(StatusTarefa.ABERTA);
			tarefa.setDataLimite(dpDataPicker.getValue());
			tarefa.setDescricao(tfStatus.getText());
			tarefa.setComentarios(taComents.getText());
			
			System.out.println(tarefa.formatToSave());
			// TODO inserir no banco dados
			try {
				TarefaIO.insert(tarefa);
				// limpar os campos
				limpar();
				// carregar as tarefas
				carregarTarefas();
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(null, "Arquivo não encontrado" + e.getMessage(), "Erro",
						JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Erro ao gravar" + e.getMessage(), "Erro",
						JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
			
		}

	}

	private void limpar() {
		tarefa = null;
		dpDataPicker.setValue(null);
		tfClassTarefa.clear();
		taComents.clear();
		tfStatus.clear();
		dpDataPicker.requestFocus();
		
		btAdiar.setDisable(true);
		btExcluir.setDisable(true);
		btConcluir.setDisable(true);
		dpDataPicker.setDisable(false);
		tvTarefa.getSelectionModel().clearSelection();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// definir os parâmetros para as colunas do TableView
		tcData.setCellValueFactory(new PropertyValueFactory<>("dataLimite"));
		tcTarefa.setCellValueFactory(new PropertyValueFactory<>("descricao"));
		//ajusta a formatação da coluna da data
		tcData.setCellFactory(call -> {

			return new TableCell<Tarefa, LocalDate>() {
				@Override
				protected void updateItem(LocalDate item, boolean empty) {
					DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MMM/yyyy");
					if(!empty) {
						setText(item.format(fmt));
					}else {
						setText("");
					}

				};
			};
		});
		//evento de seleção de item na tarefa
		tvTarefa.getSelectionModel().selectedItemProperty().addListener(this);
		
		btAdiar.setDisable(true);
		btExcluir.setDisable(true);
		btConcluir.setDisable(true);
		
		carregarTarefas();
	}

	public void carregarTarefas() {
		try {
			tarefas = TarefaIO.read();
			tvTarefa.setItems(FXCollections.observableArrayList(tarefas));
			tvTarefa.refresh();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Erro ao carregar as tarefas" + e.getMessage(), "Erro",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	@Override
	public void changed(ObservableValue<? extends Tarefa> observable, Tarefa oldValue, Tarefa newValue) {
		//passo a referência para a variável global
		tarefa = newValue;
		if(tarefa != null) {
			taComents.setText(tarefa.getComentarios());
			tfStatus.setText(tarefa.getStatus()+"");
			dpDataPicker.setValue(tarefa.getDataCriacao());
			
			btAdiar.setDisable(false);
			btExcluir.setDisable(false);
			btConcluir.setDisable(false);
			dpDataPicker.setDisable(true);
			dpDataPicker.setOpacity(1);
		}
		
	}

}
