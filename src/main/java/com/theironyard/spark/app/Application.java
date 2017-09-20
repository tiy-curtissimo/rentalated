package com.theironyard.spark.app;

import static spark.Spark.*;

import org.javalite.activejdbc.Base;
import org.mindrot.jbcrypt.BCrypt;

import com.theironyard.spark.app.controllers.ApartmentApiController;
import com.theironyard.spark.app.controllers.ApartmentController;
import com.theironyard.spark.app.controllers.HomeController;
import com.theironyard.spark.app.controllers.SessionController;
import com.theironyard.spark.app.models.Apartment;
import com.theironyard.spark.app.models.User;
import com.theironyard.spark.app.utilities.AutoCloseableDb;

public class Application {

	public static void main(String[] args) {
		
		String encryptedPassword = BCrypt.hashpw("password", BCrypt.gensalt());
		
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			User.deleteAll();
			new User("curtis.schlak@theironyard.com", encryptedPassword, "Curtis", "Schlak").saveIt();
			
			Apartment.deleteAll();
			new Apartment(6500, 10, 12.5, 2850, "123 Cowboy Way", "Houston", "TX", "77006").saveIt();
			new Apartment(6500, 3, 4.5, 850, "123 Main St.", "San Francisco", "CA", "95125").saveIt();
		}
		
		get ("/",               HomeController.index);
		get ("/apartments/:id", ApartmentController.details);
		get ("/login",          SessionController.newForm);
		post("/login",          SessionController.create);
		
		path("/api", () -> {
			get ("/apartments/:id", ApartmentApiController.details);
			post("/apartments", ApartmentApiController.create);
		});
	}

}



















