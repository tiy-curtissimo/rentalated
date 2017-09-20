package com.theironyard.spark.app.filters;

import static spark.Spark.halt;

import java.util.UUID;

import spark.Filter;
import spark.Request;
import spark.Response;

public class SecurityFilters {
	
	public static Filter csrf = (Request req, Response res) -> {
		String token = req.session().attribute("csrf");
		if (token == null) {
			token = UUID.randomUUID().toString();
			req.session().attribute("csrf", token);
		}
		if (req.requestMethod().equals("POST") && !req.queryParamOrDefault("csrf", "").equals(token)) {
			halt(401, "Bad CSRF token");
		}
	};
	
	public static Filter authenticated(String method) {
		return (Request req, Response res) -> {
			if (req.requestMethod().equals(method.toUpperCase()) && req.session().attribute("currentUser") == null) {
				res.redirect("/login?returnPath=" + req.pathInfo());
				halt();
			}
		};
	} 
	
}
