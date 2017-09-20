package com.theironyard.spark.app.controllers;

import org.mindrot.jbcrypt.BCrypt;

import com.theironyard.spark.app.models.User;
import com.theironyard.spark.app.utilities.AutoCloseableDb;
import com.theironyard.spark.app.utilities.MustacheRenderer;

import spark.Request;
import spark.Response;
import spark.Route;

public class SessionController {

	public static final Route newForm = (Request req, Response res) -> {
		return MustacheRenderer.getInstance().render(req, "session/newForm.html", null);
	};

	public static final Route create = (Request req, Response res) -> {
		String email = req.queryParams("email");
		String password = req.queryParams("password");
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			User user = User.first("email = ?", email);
			System.out.println("User: " + user);
			if (user != null && BCrypt.checkpw(password, user.getPassword())) {
				System.out.println("Hey, this passed.");
				req.session().attribute("currentUser", user);
			}
			res.redirect("/");
			return "";
		}
	};

	public static final Route destroy = (Request req, Response res) -> {
		req.session().removeAttribute("currentUser");
		res.redirect("/");
		return "";
	};

}
