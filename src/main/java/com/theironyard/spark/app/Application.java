package com.theironyard.spark.app;

import static spark.Spark.*;

import org.mindrot.jbcrypt.BCrypt;

import com.theironyard.spark.app.controllers.ApartmentApiController;
import com.theironyard.spark.app.controllers.ApartmentController;
import com.theironyard.spark.app.controllers.HomeController;
import com.theironyard.spark.app.controllers.SessionApiController;
import com.theironyard.spark.app.controllers.SessionController;
import com.theironyard.spark.app.controllers.UserController;
import com.theironyard.spark.app.filters.SecurityFilters;
import com.theironyard.spark.app.models.Apartment;
import com.theironyard.spark.app.models.ApartmentsUsers;
import com.theironyard.spark.app.models.User;
import com.theironyard.spark.app.utilities.AutoCloseableDb;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Application {

	public static void main(String[] args) throws SQLException, ClassNotFoundException {

		port(gtHerokuAssignedPort());

		String encryptedPassword = BCrypt.hashpw("password", BCrypt.gensalt());

		try (AutoCloseableDb db = new AutoCloseableDb()) {
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection(db.getUrl(), db.getName(), db.getPassword());
			Statement stmt = conn.createStatement();

			String sql = "CREATE TABLE IF NOT EXISTS apartments" + "(" + "  id bigserial PRIMARY KEY NOT NULL,"
					+ "  rent integer NOT NULL," + "  number_of_bedrooms integer NOT NULL,"
					+ "  number_of_bathrooms numeric(4, 2) NOT NULL," + "  square_footage integer NOT NULL,"
					+ "  address varchar(255)," + "  city varchar(255)," + "  state varchar(20)," + "  zip_code varchar(10),"
					+ "  user_id bigint not null," + "  is_active boolean not null default false" + ");" + "\n"
					+ "CREATE TABLE IF NOT EXISTS users" + "(" + "  id bigserial PRIMARY KEY NOT NULL, "
					+ "  email varchar(255) UNIQUE NOT NULL," + "  password varchar(255) NOT NULL,"
					+ "  first_name varchar(255) NOT NULL," + "  last_name varchar(255) NOT NULL" + ");" + "\n"
					+ "CREATE TABLE IF NOT EXISTS apartments_users (" + "	id BIGSERIAL PRIMARY KEY NOT NULL,"
					+ "	apartment_id BIGINT," + "	user_id BIGINT" + ");";

			stmt.executeUpdate(sql);

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

		enableCORS("http://localhost:4200", "*", "*");

		// before("/*", SecurityFilters.csrf);

		get("/", HomeController.index);

		path("/apartments", () -> {
			before("/new", SecurityFilters.authenticated("get"));
			get("/new", ApartmentController.newForm);

			before("/mine", SecurityFilters.authenticated("get"));
			get("/mine", ApartmentController.forCurrentUser);

			before("", SecurityFilters.authenticated("post"));
			post("", ApartmentController.create);

			get("/:id", ApartmentController.details);

			before("/:id/like", SecurityFilters.authenticated("post"));
			post("/:id/like", ApartmentController.like);

			before("/:id/activations", SecurityFilters.authenticated("post"));
			post("/:id/activations", ApartmentController.activate);

			before("/:id/deactivations", SecurityFilters.authenticated("post"));
			post("/:id/deactivations", ApartmentController.deactivate);
		});

		get("/login", SessionController.newForm);
		post("/login", SessionController.create);
		post("/logout", SessionController.destroy);
		get("/users/new", UserController.newForm);
		post("/users", UserController.create);

		path("/api", () -> {
			get("/apartments/:id", ApartmentApiController.details);
			get("/apartments", ApartmentApiController.index);
			post("/apartments", ApartmentApiController.create);

			post("/sessions", SessionApiController.create);
		});
	}

	private static int gtHerokuAssignedPort() {
		ProcessBuilder builder = new ProcessBuilder();
		if (builder.environment().get("PORT") != null) {
			return Integer.parseInt(builder.environment().get("PORT"));
		}
		return 4567;
	}

	private static void enableCORS(final String origin, final String methods, final String headers) {

		options("/*", (request, response) -> {

			String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
			if (accessControlRequestHeaders != null) {
				response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
			}

			String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
			if (accessControlRequestMethod != null) {
				response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
			}

			return "OK";
		});

		before((request, response) -> {
			response.header("Access-Control-Allow-Origin", origin);
			response.header("Access-Control-Request-Method", methods);
			response.header("Access-Control-Allow-Headers", headers);
			response.header("Access-Control-Allow-Credentials", "true");
		});
	}

}
