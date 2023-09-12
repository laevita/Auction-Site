# Auction-Site

My auction project is composed of two subprojects: the server project and the client
project. The server side has its main class, Server, as well as the subclasses Product, User, and
MongoDB. When the Server starts, it first creates a new MongoDB object, which connects to an
online database of Products and Users. The server then reads from these existing Products and
Users and stores them into ArrayLists for use in the auction. It then sets up a server Socket and
waits indefinitely for new Clients to connect. When a Client connects, the server creates a
Runnable ClientHandler Thread that observes the Server and sends the new Client the Product
data using a PrintWriter. The ClientHandler then indefinitely waits to read information from the
Client using a BufferedReader, and when it receives information, it processes it and sends back
a response depending on what was requested and whether the server could grant that request
(i.e. logging in, bidding on an item, etc.). Anytime a valid bid is placed or an item is sold, the
server communicates this with the MongoDB database and notifies its observers so that they
can be aware of the change.


On the client side, there are the classes Client, Controller, Controller2, and Product. The
Product class is identical to that of the server side. Client connects to the server Socket and
then launches a ReaderThread that constantly reads information from the Server using a
BufferedReader and then processes that information to decide what it should do (i.e. whether
the Server has sent Product information, has determined that a requested bid is invalid, etc.).
The Client also launches a GUI that is controlled by Controller (the login page) and Controller2
(the bidding page). Depending on what information Controller and Controller2 receive from the
GUI, they will send information to the Server using a PrintWriter and wait to hear a response
back to decide what to do with the action the user has performed.
