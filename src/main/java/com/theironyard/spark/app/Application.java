package com.theironyard.spark.app;

import static spark.Spark.*;

import org.mindrot.jbcrypt.BCrypt;

import com.theironyard.spark.app.controllers.ApartmentApiController;
import com.theironyard.spark.app.controllers.ApartmentController;
import com.theironyard.spark.app.controllers.HomeController;
import com.theironyard.spark.app.controllers.SessionController;
import com.theironyard.spark.app.controllers.UserController;
import com.theironyard.spark.app.filters.SecurityFilters;
import com.theironyard.spark.app.models.Apartment;
import com.theironyard.spark.app.models.User;
import com.theironyard.spark.app.utilities.AutoCloseableDb;

public class Application {

	public static void main(String[] args) {
		
		String encryptedPassword = BCrypt.hashpw("password", BCrypt.gensalt());
		
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			User.deleteAll();
			User curtis = new User("curtis@schlak", encryptedPassword, "Curtis", "Schlak");
			curtis.saveIt();
			User shawna = new User("shawna@schmidt", encryptedPassword, "Shawna", "Schmidt");
			shawna.saveIt();

			Apartment.deleteAll();
			new Apartment(6500, 10, 12.5, 2850, "123 Cowboy Way", "Houston", "TX", "77006", curtis).saveIt();
			new Apartment(6500, 3, 4.5, 850, "123 Main St.", "San Francisco", "CA", "95125", curtis).saveIt();
		}
		
		get ("/", HomeController.index);
		
		before("/apartments/new", SecurityFilters.authenticated("get"));
		get   ("/apartments/new", ApartmentController.newForm);

		before("/apartments/listed", SecurityFilters.authenticated("get"));
		get   ("/apartments/listed", ApartmentController.forCurrentUser);
		
		before("/apartments",   SecurityFilters.authenticated("post"));
		post  ("/apartments",   ApartmentController.create);

		get ("/apartments/:id", ApartmentController.details);
		get ("/login",          SessionController.newForm);
		post("/login",          SessionController.create);
		post("/logout",         SessionController.destroy);
		get ("/users/new",      UserController.newForm);
		post("/users",          UserController.create);
		
		
		path("/api", () -> {
			get ("/apartments/:id", ApartmentApiController.details);
			post("/apartments", ApartmentApiController.create);
		});
	}

}



















