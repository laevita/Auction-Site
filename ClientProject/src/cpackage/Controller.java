/*
* EE422C Final Project submission by
* <Eva Nance>
* <esn369>
* <17155>
* Spring 2023
*/

package cpackage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Controller implements Initializable{
	
	private Stage stage;
	private Scene scene;
	private Parent root;
		
    private Client client;
	 
	@FXML
    private TextField usernameTextField;
	
	@FXML
    private TextField passwordTextField;
	
	@FXML
    private Text loginErrorMessage;
	
	@FXML
	private Text welcomeMessage;
	
	private Media media;
	private MediaPlayer mediaPlayer;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {    
		media = new Media(new File("C:\\Users\\evana\\eclipse-workspace\\ClientProject\\src\\cpackage\\meow.mp3").toURI().toString());
		mediaPlayer = new MediaPlayer(media);
		
		ScaleTransition scale = new ScaleTransition();
		scale.setNode(welcomeMessage);
		scale.setDuration(Duration.millis(2000));
		scale.setCycleCount(TranslateTransition.INDEFINITE);
		scale.setInterpolator(Interpolator.LINEAR);
		scale.setByX(0.3);
		scale.setByY(0.3);
		scale.setAutoReverse(true);
		scale.play();
	}	
  
	public void login(ActionEvent e) {	
		//request format: login_user_pw
		client.sendToServer("login_" + usernameTextField.getText() + "_" + passwordTextField.getText());	
		while(!client.responseReceived) {}
		if(client.hasAccess) {
			
			 mediaPlayer.seek(Duration.ZERO); 
			 mediaPlayer.play();
			    
			client.currentUser = usernameTextField.getText();
			try {
						
				FXMLLoader loader = new FXMLLoader(getClass().getResource("eHills.fxml"));
	        	root = loader.load(); 
	        	Controller2 controller = loader.getController();
	            controller.setClient(client);
	            client.controller2 = controller;
	        	stage = (Stage)((Node)e.getSource()).getScene().getWindow();
				scene = new Scene(root);
				stage.setScene(scene);
		        stage.setTitle("eHills");
		        stage.setResizable(false);
				stage.show();
				
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		else if(client.userDoesntExist){
			loginErrorMessage.setText("Sorry, that username doesn't exist.");
		}
		else if(client.passwordIncorrect) {
			loginErrorMessage.setText("Sorry, that password is incorrect.");
		}
	}
	
	public void createAccount(ActionEvent e) {
		client.sendToServer("createuser_" + usernameTextField.getText() + "_" + passwordTextField.getText());	
		while(!client.responseReceived) {
			System.out.println();
		}
		if(client.hasAccess) {
			
			mediaPlayer.seek(Duration.ZERO); 
			mediaPlayer.play();
			 
			client.currentUser = usernameTextField.getText();
			try {				
				FXMLLoader loader = new FXMLLoader(getClass().getResource("eHills.fxml"));
	        	root = loader.load(); 
	        	Controller2 controller = loader.getController();
	            controller.setClient(client);
	            client.controller2 = controller;
	        	stage = (Stage)((Node)e.getSource()).getScene().getWindow();
				scene = new Scene(root);
				stage.setScene(scene);
		        stage.setTitle("eHills");
		        stage.setResizable(false);	        
				stage.show();

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		else if(client.usernameTaken){
			loginErrorMessage.setText("Sorry, that username is already taken.");
		}
	}

	
	public void setClient(Client client) {
        this.client = client;
    }
}