package com.theironyard.spark.app;

import static spark.Spark.get;
import static spark.Spark.before;
import static spark.Spark.post;
import static spark.Spark.path;

import com.theironyard.spark.app.controllers.ApartmentApiController;
import com.theironyard.spark.app.controllers.ApartmentController;
import com.theironyard.spark.app.controllers.HomeController;
import com.theironyard.spark.app.controllers.SessionController;
import com.theironyard.spark.app.models.Apartment;
import com.theironyard.spark.app.models.User;
import com.theironyard.spark.app.utilities.AutoCloseDb;

public class Application {

	public static void main(String[] args) {
		try (AutoCloseDb db = new AutoCloseDb()) {
			User.deleteAll();
			new User("curtis.schlak@theironyard.com", "password", "Curtis", "Schlak").saveIt();

			Apartment.deleteAll();
			new Apartment(6200, 4, 5.5, 1450, "123 Main St.", "San Francisco", "CA", "95125").saveIt();
			new Apartment(6200, 4, 5.5, 1450, "123 Cowboy Way", "Houston", "TX", "77006").saveIt();
		}

		get("/", 			   HomeController.index);
		get("/apartments/:id", ApartmentController.details);
		get("/login",          SessionController.newForm);
		post("/login",         SessionController.create);
		
		path("/admin", () -> {
			before("/*", (req, res) -> {
				if (req.session().attribute("currentUser") == null) {
					res.redirect("/login");
				}
			});
			
			get("/users", (req, res) -> "Look at all the funny people...");
		});

		path("/api", () -> {
			get("/apartments/:id", ApartmentApiController.details);
			post("/apartments", ApartmentApiController.create);
		});

	}

}


















