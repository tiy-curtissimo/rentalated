package com.theironyard.spark.app.controllers;

import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import com.theironyard.spark.app.models.User;
import com.theironyard.spark.app.utilities.AutoCloseableDb;
import com.theironyard.spark.app.utilities.JsonHelper;

import spark.Request;
import spark.Response;
import spark.Route;

public class SessionApiController {

	public static final Route create = (Request req, Response res) -> {
		String json = req.body();
		Map credentials = JsonHelper.toMap(json);
		String email = credentials.get("email").toString();
		String password = credentials.get("password").toString();
		res.header("Content-Type", "application/json");
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			User user = User.first("email = ?", email);
			System.out.println("User: " + user);
			if (user != null && BCrypt.checkpw(password, user.getPassword())) {
				req.session().attribute("currentUser", user);
				res.status(201);
				return user.toJson(true);
			}
			res.status(200);
			return "{}";
		}
	};

	public static final Route destroy = (Request req, Response res) -> {
		req.session().removeAttribute("currentUser");
		res.redirect("/");
		return "";
	};

}
