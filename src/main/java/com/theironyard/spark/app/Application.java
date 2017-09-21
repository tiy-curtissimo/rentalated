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
import com.theironyard.spark.app.models.ApartmentsUsers;
import com.theironyard.spark.app.models.User;
import com.theironyard.spark.app.utilities.AutoCloseableDb;

public class Application {

	public static void main(String[] args) {

		port(gtHerokuAssignedPort());

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

			ApartmentsUsers.deleteAll();
		}

		before("/*", SecurityFilters.csrf);

		get ("/", HomeController.index);

		path("/apartments", () -> {
			before("/new", SecurityFilters.authenticated("get"));
			get   ("/new", ApartmentController.newForm);

			before("/mine", SecurityFilters.authenticated("get"));
			get   ("/mine", ApartmentController.forCurrentUser);

			before("",   SecurityFilters.authenticated("post"));
			post  ("",   ApartmentController.create);

			get   ("/:id", ApartmentController.details);

			before("/:id/like", SecurityFilters.authenticated("post"));
			post  ("/:id/like", ApartmentController.like);

			before("/:id/activations", SecurityFilters.authenticated("post"));
			post  ("/:id/activations", ApartmentController.activate);

			before("/:id/deactivations", SecurityFilters.authenticated("post"));
			post  ("/:id/deactivations", ApartmentController.deactivate);
		});


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

	private static int gtHerokuAssignedPort() {
		ProcessBuilder builder = new ProcessBuilder();
		if (builder.environment().get("PORT") != null) {
			return Integer.parseInt(builder.environment().get("PORT"));
		}
		return 4567;
	}

}



















