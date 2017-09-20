package com.theironyard.spark.app.controllers;

import java.util.HashMap;
import java.util.Map;

import com.theironyard.spark.app.models.Apartment;
import com.theironyard.spark.app.utilities.AutoCloseableDb;
import com.theironyard.spark.app.utilities.MustacheRenderer;

import spark.Request;
import spark.Response;
import spark.Route;

public class ApartmentController {

	public static final Route details = (Request req, Response res) -> {
		String idAsString = req.params("id");
		int id = Integer.parseInt(idAsString);

		try (AutoCloseableDb db = new AutoCloseableDb()) {
			Apartment apartment = Apartment.findById(id);
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("apartment", apartment);
			return MustacheRenderer.getInstance().render("apartment/details.html", model);
		}
	};

}
