package com.theironyard.spark.app.controllers;

import java.security.KeyStore.Entry;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.mindrot.jbcrypt.BCrypt;

import com.theironyard.spark.app.models.User;
import com.theironyard.spark.app.utilities.AutoCloseableDb;
import com.theironyard.spark.app.utilities.MustacheRenderer;

import spark.Request;
import spark.Response;
import spark.Route;

public class UserController {

	public static final Route newForm = (Request req, Response res) -> {
		return MustacheRenderer.getInstance().render("users/newForm.html", null);
	};
	
	public static final Route create = (Request req, Response res) -> {
		Map<String, String> map = req.queryMap("user")
			.toMap()
			.entrySet()
			.stream()
			.map(entry -> new AbstractMap.SimpleEntry<String, String>(entry.getKey(), entry.getValue()[0]))
			.peek(entry -> entry.setValue(entry.getKey() == "password" ? BCrypt.hashpw(entry.getValue(), BCrypt.gensalt()) : entry.getValue()))
			.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
		User user = new User();
		user.fromMap(map);
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			user.saveIt();
			req.session().attribute("currentUser", user);
			res.redirect("/");
			return "";
		}
	};
	
}













