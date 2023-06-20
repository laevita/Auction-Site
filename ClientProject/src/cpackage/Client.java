/*
* EE422C Final Project submission by
* <Eva Nance>
* <esn369>
* <17155>
* Spring 2023
*/

package cpackage;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Client extends Application {
	
	private static String host = "10.145.8.20"; 
	private BufferedReader fromServer;
	private PrintWriter toServer;
	public ArrayList<Product> productList = new ArrayList<>();
	Thread readerThread;
	public boolean hasAccess = false;
	public boolean userDoesntExist = false;
	public boolean passwordIncorrect = false;
	public boolean usernameTaken = false;
	Socket socketRef;
	public String currentUser = " ";

	public boolean userBought = false;
	public boolean productUnavailable = false;
	public boolean bidPlaced = false;
	public boolean invalidBid = false;

	public boolean responseReceived = true;
	
	public Controller2 controller2;

	
	@Override
	public void start(Stage stage) throws Exception {
		setUpNetworking();
		Parent root = null;
        try {
            //root = FXMLLoader.load(getClass().getResource("eHillsLogin.fxml"));
            
        	FXMLLoader loader = new FXMLLoader(getClass().getResource("eHillsLogin.fxml"));
        	root = loader.load();  	

        	Controller controller = loader.getController();
            controller.setClient(this);
        	        
            Scene scene = new Scene(root, 700, 500);
            stage.setScene(scene);
            stage.setTitle("eHills Login");
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }        
	}

	public static void main(String[] args) {		
		launch(args);
	}

	void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		Socket socket = new Socket(host, 8000);
		socketRef = socket;
		fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		toServer = new PrintWriter(socket.getOutputStream());

		readerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				String input;
				try {
					while ((input = fromServer.readLine()) != null) {
						System.out.println(input);
						processRequest(input);
						System.out.println("set response recieved to true");
						System.out.println(hasAccess);
					}
				} catch (Exception e) {
					if (socket.isClosed()) {
				        // Socket was intentionally closed
						
				    } else {
				        // Socket was closed unexpectedly
						e.printStackTrace();
				    }
				}
			}
		});

		readerThread.start();
	}

	protected void processRequest(String input) {
		String [] request = input.split("_", 0);
		
		//if the server is sending products
		//("P_" + name + "_" + description + "_" + CurrentPrice + "_" +BuyNowPrice() + "_" + foo.getSold());
		if(request[0].equals("P")) {
			try {
				Product p = new Product(request[1], request[2], Double.parseDouble(request[3]), Double.parseDouble(request[4]), Boolean.parseBoolean(request[5]));
				productList.add(p);				
			} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
				e.printStackTrace();
			}
		}
		//if the server is sending anything else (ie responses to bidding, user creation)
		
		//user has access to eHills
		else if(request[0].equals("LoggedIn")){
			hasAccess = true;
			userDoesntExist = false;
			passwordIncorrect = false;
			usernameTaken = false;
			responseReceived = true;
		}
		
		// username taken, account not created, no access
		else if(request[0].equals("UsernameTaken")) {
			hasAccess = false;
			userDoesntExist = false;
			passwordIncorrect = false;
			usernameTaken = true;	
			responseReceived = true;
		}
		
		// user is not in database, no access granted
		else if(request[0].equals("InvalidUser")) {
			hasAccess = false;
			userDoesntExist = true;
			passwordIncorrect = false;
			usernameTaken = false;
			responseReceived = true;
		}
		
		// incorrect password, no access
		else if(request[0].equals("IncorrectPassword")) {
			hasAccess = false;
			userDoesntExist = false;
			passwordIncorrect = true;
			usernameTaken = false;
			responseReceived = true;
		}
		
		// user placed bid that bought the item
		else if(request[0].equals("Bought")) {
			userBought = true;
			productUnavailable = false;
			bidPlaced = false;
			invalidBid = false;
			responseReceived = true;
		}
		
		// user tried to buy an item that was already bought
		else if(request[0].equals("ProductUnavailable")) {
			userBought = false;
			productUnavailable = true;
			bidPlaced = false;
			invalidBid = false;
			responseReceived = true;
		}
		
		// user placed a successful bid but did not purchase the item yet
		else if(request[0].equals("BidPlaced")) {
			userBought = false;
			productUnavailable = false;
			bidPlaced = true;
			invalidBid = false;
			responseReceived = true;
		}
		
		// user placed a bid lower than the highest bid
		else if(request[0].equals("InvalidBid")) {
			userBought = false;
			productUnavailable = false;
			bidPlaced = false;
			invalidBid = true;
			responseReceived = true;
		}
		
		//this one shouldn't happen once i'm done coding but it's just a safeguard
		else if(request[0].equals("ProductNotFound")) {
	
		}
		
		// make sure that this one does not change the response recieved semaphore! it can change at any time
		else if(request[0].equals("ProductSold")) {
			
			try {
				Product product = null;
				
				for(Product foo : productList) {
					if(request[1].equals(foo.getName())) {
						product = foo;
						break;
					}
				}
				
				if(product != null) {
					product.setSold(true);
					product.setCurrentPrice(Double.parseDouble(request[3]));
				}
				
				controller2.soldUpdate(request[1], request[2], request[3]);
				
			} catch (IndexOutOfBoundsException | NumberFormatException e) {
				e.printStackTrace();
			}	
		}
		
		// make sure that this one does not change the response recieved semaphore! it can change at any time
		else if(request[0].equals("UpdatedBid")) {

			try {
				Product product = null;
				
				for(Product foo : productList) {
					if(request[1].equals(foo.getName())) {
						product = foo;
						break;
					}
				}
				
				if(product != null) {
					product.setCurrentPrice(Double.parseDouble(request[3]));
				}
				
				controller2.bidUpdate(request[1], request[2], request[3]);
				
			} catch (IndexOutOfBoundsException | NumberFormatException e) {
				e.printStackTrace();
			}	
		}
	}

	protected void sendToServer(String string) {
		System.out.println("Sending to server: " + string);
		responseReceived = false;
		toServer.println(string);
		toServer.flush();
	} 
}

