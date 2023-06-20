/*
* EE422C Final Project submission by
* <Eva Nance>
* <esn369>
* <17155>
* Spring 2023
*/


package spackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;

public class Server extends Observable {
	
	public ArrayList<Product> productList = new ArrayList<>();
	public ArrayList<User> userList = new ArrayList<>();
	MongoDB mongo;
	Object bidLock = new Object();
	
	public static void main(String[] args) {
		new Server().runServer();		
	}
  
	private void runServer() {
		
		mongo = new MongoDB();
		mongo.setupMD();
		
		MongoIterable<Product> products = MongoDB.productCollection.find();
		for(Product p : products) {
			productList.add(p);
		}
		
		MongoIterable<User> users = MongoDB.userCollection.find();
		for(User u : users) {
			userList.add(u);
		}
		
		try {
			setUpNetworking();
		} catch(Exception e) {
			e.printStackTrace();
			return;
		}
	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(8000);
		while (true) {
			Socket clientSocket = serverSock.accept();
			ClientHandler handler = new ClientHandler(this, clientSocket);
			this.addObserver(handler);

			Thread t = new Thread(handler);
			t.start();
		}
	}
   
  
	class ClientHandler implements Runnable, Observer {

		private Server server;
		private Socket clientSocket;
		private BufferedReader fromClient;
		private PrintWriter toClient;

		protected ClientHandler(Server server, Socket clientSocket) {
			this.server = server;
			this.clientSocket = clientSocket;
			try {
				
			    fromClient = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
			    toClient = new PrintWriter(this.clientSocket.getOutputStream());
			} catch (IOException e) {		 
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			//receive info from user to determine login stuff
			
			for(Product foo : productList) {
				sendToClient("P_" + foo.getName() + "_" + foo.getDescription() + "_" 
							+ foo.getCurrentPrice() + "_" + foo.getBuyNowPrice() + "_" + foo.getSold());
			}
			
			String input;
			try {
				while ((input = fromClient.readLine()) != null) {
					System.out.println(input);
					processRequest(input);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		
		protected void processRequest(String input) {  

			String [] request = input.split("_", 0);
			System.out.println(request[0]);

			if (request.length > 0) {
				
				if(request[0].equals("createuser")) {
					// creates valid user
					
					System.out.println("yaayay");
					
					int result = createUser(request);
					
					if(result == 0) {
						sendToClient("LoggedIn");
					}
					// new user not created
					else {
						sendToClient("UsernameTaken");
					}				
				}
				
				//request format: login_user_pw
				else if(request[0].equals("login")) {
					// valid user + password
					int result = login(request);
					
					if(result == 0)		{
						sendToClient("LoggedIn");
					}
					// invalid username
					else if(result == 1) {
						sendToClient("InvalidUser");
					}
					// incorrect password
					else if(result == 2) {
						sendToClient("IncorrectPassword");
					}
				}
				else if(request[0].equals("bid")) {
					// buy now! client has just bought it
					
					int result = -2; 
					
					synchronized (bidLock) {
						result = bid(request);
					}
					
					if(result == 0)		{
						sendToClient("Bought");
					}
					//product has been sold already
					else if(result == 1) {
						sendToClient("ProductUnavailable");
					}
					// user placed a valid higher bid, but not sold yet
					else if(result == 2) {
						sendToClient("BidPlaced");
					}
					// bid wasn't high enough, failed bid
					else if(result == 3) {
						sendToClient("InvalidBid");
					}					
					//product not found
					else if(result == -1) {
						sendToClient("ProductNotFound");
					}
				}				
			}
		}
		
		public int createUser(String [] request) {
			try {
				String user = request[1];
				String password = request[2];
				
				// checks if it is an existing user
				for(User foo : userList) {
					if (foo.getUsername().equals(user) ) {
						return -1;
					}
				}
				
				// not an existing user
				MongoDB.createNewUser(user, password);
				userList.add(new User(user, password));	
				return 0;
				
			} catch(ArrayIndexOutOfBoundsException e) {
				
			}
			return 0;
		}
		
		public int login(String [] request) {	
			try {
				String user = request[1];
				String password = request[2];
				
				// checks if it is an existing user
				for(User foo : userList) {
					if (foo.getUsername().equals(user) ) {
						if(password.equals(foo.getPassword())) {
							//username and password match
							return 0;
						}
						else {
							//found username but incorrect password
							return 2;
						}
					}
				}
				//user not found
				return 1;
				
			} catch(ArrayIndexOutOfBoundsException e) {
			
			}
			return 1;
		}
		
		public int bid(String [] request) {
			try {
				String user = request[1];
				String productString = request[2];
				String bidString = request[3];				
				
				double bid = Double.parseDouble(bidString);
				System.out.println(bid);

				Product product = null;
				
				//find product in product list
				for(Product foo : productList) {
					if(productString.equals(foo.getName())) {
						product = foo;
						break;
					}
				}
				
				//product not found
				if(product == null) {
					return -1;
				}
				
				System.out.println(product.getBuyNowPrice());
				System.out.println(product.getCurrentPrice());
				System.out.println(bid >= product.getBuyNowPrice());


				//product found				
				//check that product is still for sale	
				
				//product has been sold already
				if(product.getSold()) {
					System.out.println("it is already been sold");
					return 1;
				}
				
				//check if bid is higher than buy now price
				//if it is, it's sold
				if(bid >= product.getBuyNowPrice()) {
					product.setSold(true);
					product.setCurrentPrice(bid);
					System.out.println("bid wins");
			        MongoDB.productCollection.replaceOne(Filters.eq("name", productString), product);
			        server.setChanged();
					server.notifyObservers("ProductSold_" + productString + "_" + user + "_" + bid);
			        return 0;
				}			
				
				//if bid is higher than current bid, update the value
				if(bid > product.getCurrentPrice()) {
					product.setCurrentPrice(bid);
					System.out.println("bid is updated");
			        MongoDB.productCollection.replaceOne(Filters.eq("name", productString), product);
			        server.setChanged();
					server.notifyObservers("UpdatedBid_" + productString + "_" + user + "_" + bid);

					return 2;
				}
				//if bid is not higher, dont update but let the user know
				System.out.println("bidTooLow");
				return 3;

				//inform customers of bid
				
			} catch(ArrayIndexOutOfBoundsException | NumberFormatException e) {
				
			}
			return 0;
		}

		@Override
		public void update(Observable o, Object arg) {
			this.sendToClient((String) arg);
		}
		

		protected void sendToClient(String string) {
			try {
				toClient.println(string);
				toClient.flush();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}