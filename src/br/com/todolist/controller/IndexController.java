package br.com.todolist.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import br.com.todolist.io.TarefaIO;
import br.com.todolist.model.StatusTarefa;
import br.com.todolist.model.Tarefa;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class IndexController implements Initializable, ChangeListener<Tarefa> {

	@FXML
	private Label lbDataR;

	@FXML
	private DatePicker dpDataPicker;

	@FXML
	private DatePicker dpDataConclusao;
	@FXML
	private Label lbStatusTar;

	@FXML
	private Label lbComents;

	@FXML
	private Text lbConclusao;

	@FXML
	private TextField tfCodigo;

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
	private MenuItem miExport;

	@FXML
	private MenuItem miSair;

	@FXML
	private MenuItem miSobre;

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
	public void miExport(ActionEvent event) {
		FileFilter filter = new FileNameExtensionFilter("Arquivos HTML", "html", "htm");
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(filter);
		if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			File arqSelecionado = chooser.getSelectedFile();
			if (!arqSelecionado.getAbsolutePath().endsWith(".html")) {
				arqSelecionado = new File(arqSelecionado + ".html");
			}
			try {
				TarefaIO.exportHtml(tarefas, arqSelecionado);

			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Erro ao exportar as tarefas" + e.getMessage(), "Erro",
						JOptionPane.ERROR_MESSAGE);
			}

		}
	}

	@FXML
	public void miSair(ActionEvent event) {
		System.exit(0);
	}

	@FXML
	public void miSobre() {
		try {
			AnchorPane root = (AnchorPane) FXMLLoader
					.load(getClass().getResource("/br/com/todolist/view/Feitopor.fxml"));
			Scene scene = new Scene(root, 389, 487);
			scene.getStylesheets().add(getClass().getResource("/br/com/todolist/view/application.css").toExternalForm());
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.initStyle(StageStyle.UNDECORATED);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();
		} catch (Exception e) {
e.printStackTrace();
		}

	}

	@FXML
	void btAdiarClick(ActionEvent event) {
		if (tarefa != null) {
			int dias = Integer.parseInt(
					JOptionPane.showInputDialog(null, "Quantos dias você deseja adiar?", JOptionPane.QUESTION_MESSAGE));
			LocalDate novaData = tarefa.getDataLimite().plusDays(dias);
			tarefa.setDataLimite(novaData);
			tarefa.setStatus(StatusTarefa.ADIADA);
			try {
				TarefaIO.saveTarefas(tarefas);
				// exibe um joptionpane informando que foi adiada
				// e a nova data para realização
				JOptionPane.showMessageDialog(null, "A tarefa foi adiada para: " + novaData, "Aviso",
						JOptionPane.INFORMATION_MESSAGE);
				carregarTarefas();
				limpar();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Erro ao carregar as tarefas" + e.getMessage(), "Erro",
						JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}

	@FXML
	void btConcluirClick(ActionEvent event) {
		if (tarefa != null) {
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd 'de' MM 'de' yyyy");
			JOptionPane.showMessageDialog(null, "Tarefa concluída");
			tarefa.setDataConcluida(LocalDate.now());
			tarefa.setStatus(StatusTarefa.CONCLUIDA);
			try {
				TarefaIO.saveTarefas(tarefas);

			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "A Tarefa já foi concluída" + e.getMessage(), "Erro",
						JOptionPane.ERROR_MESSAGE);

			}
		}
	}

	@FXML
	void btExcluirClick(ActionEvent event) throws IOException {
		if (tarefa != null) {
			int resposta = JOptionPane.showConfirmDialog(null, "Deseja excluir a tarefa" + tarefa.getId() + "?",
					"Confirmar exclusão", JOptionPane.YES_NO_OPTION);
			if (resposta == 0) {
				tarefas.remove(tarefa);

				try {
					TarefaIO.saveTarefas(tarefas);
					carregarTarefas();
					limpar();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Erro ao concluir tarefa: " + e.getMessage(), "Erro",
							JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		}
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
		} else if (tfClassTarefa.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Informe a importâcia da tarefa", "Informe", JOptionPane.ERROR_MESSAGE);
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
			// verifica se a tarefa é nula
			if (tarefa == null) {
				// instanciando a tarefa
				tarefa = new Tarefa();
				tarefa.setDataCriacao(LocalDate.now());
				tarefa.setStatus(StatusTarefa.ABERTA);
			}
			// popular tarefa
			tarefa.setDataLimite(dpDataPicker.getValue());
			tarefa.setDescricao(tfStatus.getText());
			tarefa.setComentarios(taComents.getText());

			System.out.println(tarefa.formatToSave());
			// TODO inserir no banco dados
			try {
				if (tarefa.getId() == 0) {
					TarefaIO.insert(tarefa);
				} else {
					TarefaIO.saveTarefas(tarefas);
				}
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
		tfCodigo.clear();
		dpDataPicker.requestFocus();

		btAdiar.setDisable(true);
		btExcluir.setDisable(true);
		btConcluir.setDisable(true);
		dpDataPicker.setDisable(false);
		btSalvar.setDisable(false);
		lbClassTarefa.setDisable(false);
		tvTarefa.getSelectionModel().clearSelection();

		dpDataPicker.setDisable(false);
		tfClassTarefa.setDisable(false);
		taComents.setDisable(false);
		tfStatus.setDisable(false);
		tfCodigo.setDisable(false);
		dpDataConclusao.setVisible(false);
		lbConclusao.setVisible(false);

		try {
			tfCodigo.setText(TarefaIO.proximoId() + "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// definir os parâmetros para as colunas do TableView
		tcData.setCellValueFactory(new PropertyValueFactory<>("dataLimite"));
		tcTarefa.setCellValueFactory(new PropertyValueFactory<>("status"));
		dpDataConclusao.setOpacity(1);
		tfStatus.setDisable(true);
		try {
			tfCodigo.setText(TarefaIO.proximoId() + "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ajusta a formatação da coluna da data
		tcData.setCellFactory(call -> {
			return new TableCell<Tarefa, LocalDate>() {
				@Override
				protected void updateItem(LocalDate item, boolean empty) {
					DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MMM/yyyy");
					if (!empty) {
						setText(item.format(fmt));
					} else {
						setText("");
					}
				};
			};
		});

		tvTarefa.setRowFactory(call -> new TableRow<Tarefa>() {
			protected void updateItem(Tarefa item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null) {
					setStyle("");
				} else if (item.getStatus() == StatusTarefa.CONCLUIDA) {
					setStyle("-fx-background-color: #80ff80");
				} else if (item.getDataLimite().isBefore(LocalDate.now())) {
					setStyle("-fx-background-color: #ff4d4d");
				} else if (item.getStatus() == StatusTarefa.ADIADA) {
					setStyle("-fx-background-color: #ffff80");
				} else {
					setStyle("-fx-background-color: cyan");
				}
			};
		});
			
		//avisa o usuário caso tenha uma tarefa atrasada
		
		// evento de seleção de item na tarefa
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
		// passo a referência para a variável global
		tarefa = newValue;
		if (tarefa != null) {
			taComents.setText(tarefa.getComentarios());
			tfStatus.setText(tarefa.getStatus() + "");
			dpDataPicker.setValue(tarefa.getDataCriacao());
			tfCodigo.setText(tarefa.getId() + "");

			btAdiar.setDisable(false);
			btExcluir.setDisable(false);
			btConcluir.setDisable(false);
			dpDataPicker.setDisable(true);
			dpDataPicker.setOpacity(1);
			dpDataConclusao.setVisible(false);
			lbConclusao.setVisible(false);
			if (tarefa.getStatus() == StatusTarefa.CONCLUIDA) {
				btAdiar.setDisable(true);
				btConcluir.setDisable(true);
				btSalvar.setDisable(true);
				taComents.setDisable(true);
				tfStatus.setEditable(false);
				dpDataPicker.setEditable(false);
				tfCodigo.setEditable(false);
				btExcluir.setDisable(false);
				dpDataConclusao.setValue(tarefa.getDataConcluida());
				dpDataConclusao.setVisible(true);
				lbConclusao.setVisible(true);

			} else if (tarefa.getStatus() == StatusTarefa.ABERTA) {
				btAdiar.setDisable(false);
				btConcluir.setDisable(false);
				btSalvar.setDisable(false);
			} else {
				btAdiar.setDisable(true);
				btConcluir.setDisable(false);
				btSalvar.setDisable(false);
				taComents.setDisable(false);
				dpDataPicker.setDisable(false);
				tfCodigo.setDisable(false);
			}

		}
	}

}
