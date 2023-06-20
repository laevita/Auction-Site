/*
* EE422C Final Project submission by
* <Eva Nance>
* <esn369>
* <17155>
* Spring 2023
*/

package spackage;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Filter;

public class MongoDB {
    private static MongoClient mc;
    private static MongoDatabase db;
    
    public static MongoCollection<Product> productCollection;
    public static MongoCollection<User> userCollection;

    private static final String URI = "mongodb+srv://evanance:7fTw6yMAUmam4An0@cluster0.o8exxms.mongodb.net/?retryWrites=true&w=majority";
    private static final String DB = "FinalProject";
    
    private static final String PRODUCT_COLLECTION = "Products";
    private static final String USER_COLLECTION = "Users";

    public static void main(String[] args){
    	MongoDB mongo = new MongoDB();
        mongo.setupMD();
//        initUsers();
//        initProducts();

    }
    
    public void setupMD(){
        CodecRegistry pojoCodecRegistry = org.bson.codecs.configuration.CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), org.bson.codecs.configuration.CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        mc = MongoClients.create(URI);
        db = mc.getDatabase(DB).withCodecRegistry(pojoCodecRegistry);
        userCollection = db.getCollection(USER_COLLECTION, User.class);
        productCollection = db.getCollection(PRODUCT_COLLECTION, Product.class);
    }

    public static void initProducts(){
        Product product = new Product("Kirby Plushie", "A cute little plushie of Kirby!", 10.00, 50.00, false);
        productCollection.insertOne(product);
        product = new Product("Daniela Isabel Caballero", "Are you really objectifying women in ECE?", 100.00, 1000.00, false);
        productCollection.insertOne(product);
        product = new Product("Gamer Girl Bathwater", "IYKYK", 75.00, 150.00, false);
        productCollection.insertOne(product);
        product = new Product("Thomas's Code", "We know you want to take the easy way out of your coding project", 50.00, 200.00, false);
        productCollection.insertOne(product);
        product = new Product("Quilladin Rex Precious Jones", "He WILL eat you in your sleep...", 5.00, 50.00, false);
        productCollection.insertOne(product);
    }

    public static void createNewUser(String username, String password){
        User user = new User(username, password);
        userCollection.insertOne(user);
    }

    
    public static void initUsers(){
        User user = new User("evita", "password");
        userCollection.insertOne(user);
    }
}