package br.com.todolist.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class SobreController {
	@FXML
	private Button btClose;
	
	@FXML
	public void btClickClose(){
		Stage stage = (Stage) btClose.getScene().getWindow();
		stage.close();
	}
}
