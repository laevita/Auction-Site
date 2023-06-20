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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Controller2 implements Initializable{

	private Stage stage;
    private Client client;

    private Map<String, Product> map = new HashMap<String, Product>();

    private String currentProductString;

    private Product currentProduct;

	@FXML
	private ListView<String> myListView;

	@FXML
	private ListView<String> auctionHistoryList;

	@FXML
	private Text productNameBox;

	@FXML
	private TextArea descriptionBox;

	@FXML
	private Text currentBidAmount;

	@FXML
	private Text buyNowAmount;

	@FXML
	private TextField bidAmount;

	@FXML
	private Text bidStatusMessage;

	@FXML
	private ImageView myImage;

	private File directory;
	private File[] files;

	private ArrayList<File> songs;
	private int songNumber;
	private Media media;
	private MediaPlayer mediaPlayer;


	Image kirby = new Image(getClass().getResourceAsStream("kirbo.jpg"));
	Image daniela = new Image(getClass().getResourceAsStream("daniela.jpg"));
	Image bathwater = new Image(getClass().getResourceAsStream("bathwater.jpg"));
	Image code = new Image(getClass().getResourceAsStream("codear.jpg"));
	Image quill = new Image(getClass().getResourceAsStream("quill.jpg"));

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		songs = new ArrayList<File>();
		
		songs.add(new File("C:\\Users\\evana\\eclipse-workspace\\ClientProject\\src\\cpackage\\listeners.mp3"));
		songs.add(new File("C:\\Users\\evana\\eclipse-workspace\\ClientProject\\src\\cpackage\\villana.mp3"));
		songs.add(new File("C:\\Users\\evana\\eclipse-workspace\\ClientProject\\src\\cpackage\\color.mp3"));
		songs.add(new File("C:\\Users\\evana\\eclipse-workspace\\ClientProject\\src\\cpackage\\love.mp3"));
		songs.add(new File("C:\\Users\\evana\\eclipse-workspace\\ClientProject\\src\\cpackage\\vansire.mp3"));

		songNumber = 0;

		media = new Media(songs.get(songNumber).toURI().toString());
		mediaPlayer = new MediaPlayer(media);
	}

	public void logout(ActionEvent e) {
		System.out.println("meow");
		try {
			client.socketRef.close();
        	stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        	stage.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@FXML
    public void handleListViewSelection(MouseEvent event) {
        String selectedItem = myListView.getSelectionModel().getSelectedItem();
        if(map.get(selectedItem).getSold()) {
            productNameBox.setText(selectedItem + " (SOLD)");
        }
        else {
            productNameBox.setText(selectedItem);
        }

        if(selectedItem.equals("Kirby Plushie")) {
        	myImage.setImage(kirby);
        }
        else if (selectedItem.equals("Daniela Isabel Caballero")){
        	myImage.setImage(daniela);

        }
        else if (selectedItem.equals("Gamer Girl Bathwater")){
        	myImage.setImage(bathwater);

        }
        else if (selectedItem.equals("Thomas's Code")){
        	myImage.setImage(code);

        }
        else if (selectedItem.equals("Quilladin Rex Precious Jones")){
        	myImage.setImage(quill);
        }

        descriptionBox.setText(map.get(selectedItem).getDescription());
        currentBidAmount.setText("$" + map.get(selectedItem).getCurrentPrice());
        buyNowAmount.setText("$" + map.get(selectedItem).getBuyNowPrice());
        currentProductString = selectedItem;
        currentProduct = map.get(selectedItem);
    	bidStatusMessage.setText("");
    }

	public void bid(ActionEvent e) {
		String bidString = bidAmount.getText();

        try {
            double bid = Double.parseDouble(bidString);
            client.sendToServer("bid_" + client.currentUser + "_" + currentProductString + "_" + bidString);

            while(!client.responseReceived) {
    			System.out.println();
    		}

            if(client.userBought) {
            	bidStatusMessage.setText("Sold! " + currentProductString + " is yours!");
            }
            else if(client.productUnavailable) {
            	bidStatusMessage.setText("Sorry, " + currentProductString + " is unavailable.");
            }
            else if(client.bidPlaced) {
            	bidStatusMessage.setText("Awesome! Your bid has been placed.");
            }
            else if(client.invalidBid) {
            	bidStatusMessage.setText("Sorry, that is not a valid bid.");
            }

        }

        catch(NumberFormatException x){
        	bidStatusMessage.setText("Please type in a real price.");
        }
	}

	public void setClient(Client client) {
        this.client = client;
        for(Product foo : client.productList) {
            myListView.getItems().add(foo.getName());
            map.put(foo.getName(), foo);
        }

        myListView.setOnMouseClicked(this::handleListViewSelection);
    }

	public void soldUpdate(String product, String user, String bid) {
		String selectedItem = myListView.getSelectionModel().getSelectedItem();
		if(selectedItem != null) {
			if(selectedItem.equals(product)) {
	            productNameBox.setText(selectedItem + " (SOLD)");
		        currentBidAmount.setText("$" + bid);
			}
		}

		Platform.runLater(() -> {
			auctionHistoryList.getItems().add(user + " bought " + product + " for $" + bid + "!");
		});
	}

	public void bidUpdate(String product, String user, String bid) {
		String selectedItem = myListView.getSelectionModel().getSelectedItem();
		if(selectedItem != null) {
			if(selectedItem.equals(product)) {
		        currentBidAmount.setText("$" + bid);
			}
		}

		Platform.runLater(() -> {
			auctionHistoryList.getItems().add(user + " bid $" + bid + " on " + product + "!");
		});
	}

	public void play(ActionEvent e) {
		Platform.runLater(() -> {
			mediaPlayer.play();
			System.out.println("play");
		});
	}

	public void pause(ActionEvent e) {
		mediaPlayer.pause();
	}

	public void next(ActionEvent e) {
		if(songNumber < songs.size() - 1) {
			songNumber++;
			mediaPlayer.stop();
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			play(e);
		}
		else {
			songNumber = 0;
			mediaPlayer.stop();
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			play(e);
		}
	}

	public void previous(ActionEvent e) {
		if(songNumber > 0) {
			songNumber--;
			mediaPlayer.stop();
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			play(e);
		}
		else {
			songNumber = songs.size() - 1;
			mediaPlayer.stop();
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			play(e);
		}
	}
}
