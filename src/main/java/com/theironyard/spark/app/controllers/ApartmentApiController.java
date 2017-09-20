package com.theironyard.spark.app.controllers;

import com.theironyard.spark.app.models.Apartment;
import com.theironyard.spark.app.utilities.AutoCloseableDb;
import com.theironyard.spark.app.utilities.JsonHelper;
import spark.Request;
import spark.Response;
import spark.Route;

public class ApartmentApiController {

	public static final Route details = (Request req, Response res) -> {
		int id = Integer.parseInt(req.params("id"));
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			res.header("Content-Type", "application/json");
			return Apartment.findById(id).toJson(true);
		}
	};

	public static final Route create = (Request req, Response res) -> {
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			Apartment apartment = new Apartment();
			apartment.fromMap(JsonHelper.toMap(req.body()));
			apartment.saveIt();
			res.status(201);
			return apartment.toJson(true);
		}
	};

}
