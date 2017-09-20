package com.theironyard.spark.app.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.theironyard.spark.app.models.Apartment;
import com.theironyard.spark.app.utilities.AutoCloseableDb;
import com.theironyard.spark.app.utilities.MustacheRenderer;

import spark.Request;
import spark.Response;
import spark.Route;

public class HomeController {

	public static final Route index = (Request req, Response res) -> {
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			List<Apartment> apartments = Apartment.where("is_active = ?", true);
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("apartments", apartments);
			return MustacheRenderer.getInstance().render(req, "home/index.html", model);
		}
	};

}
