package com.theironyard.spark.app.controllers;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.theironyard.spark.app.models.Apartment;
import com.theironyard.spark.app.models.User;
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
			return MustacheRenderer.getInstance().render(req, "apartment/details.html", model);
		}
	};
	
	public static final Route newForm = (Request req, Response res) -> {
		return MustacheRenderer.getInstance().render(req, "apartment/newForm.html");
	};
	
	public static final Route create = (Request req, Response res) -> {
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			System.out.println(req.queryMap("apartment").get("number_of_bathrooms"));
			Map<String, Object> map = req.queryMap("apartment")
					.toMap()
					.entrySet()
					.stream()
					.map(entry -> new AbstractMap.SimpleEntry<String, Object>(entry.getKey(), entry.getValue()[0]))
					.peek(entry -> entry.setValue(entry.getKey().equals("number_of_bathrooms") ? Double.parseDouble(entry.getValue().toString()) : entry.getValue()))
					.peek(entry -> entry.setValue(entry.getKey().equals("number_of_bedrooms") ? Integer.parseInt(entry.getValue().toString()) : entry.getValue()))
					.peek(entry -> entry.setValue(entry.getKey().equals("rent") ? Integer.parseInt(entry.getValue().toString()) : entry.getValue()))
					.peek(entry -> entry.setValue(entry.getKey().equals("square_footage") ? Integer.parseInt(entry.getValue().toString()) : entry.getValue()))
					.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
			Apartment apartment = new Apartment();
			apartment.fromMap(map);
			User currentUser = req.session().attribute("currentUser");
			currentUser.add(apartment);
			res.redirect("/apartments/listed");
			return "";
		}
	};

	public static final Route forCurrentUser = (Request req, Response res) -> {
		User currentUser = req.session().attribute("currentUser");
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("apartments", currentUser.getAll(Apartment.class));
			return MustacheRenderer.getInstance().render(req, "apartment/index.html", model);
		}
	};

}
