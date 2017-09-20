package com.theironyard.spark.app.filters;

import static spark.Spark.halt;
import spark.Filter;
import spark.Request;
import spark.Response;

public class SecurityFilters {
	
	public static Filter authenticated(String method) {
		return (Request req, Response res) -> {
			if (req.requestMethod().equals(method.toUpperCase()) && req.session().attribute("currentUser") == null) {
				res.redirect("/login?returnPath=" + req.pathInfo());
				halt();
			}
		};
	} 
	
}
