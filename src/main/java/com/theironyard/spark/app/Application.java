package com.theironyard.spark.app;

import static spark.Spark.get;
import static spark.Spark.post;

import com.theironyard.spark.app.controllers.ApartmentController;
import com.theironyard.spark.app.controllers.HomeController;
import com.theironyard.spark.app.controllers.SessionController;

public class Application {

	public static void main(String[] args) {
		get ("/",               HomeController.index);
		get ("/apartments/:id", ApartmentController.details);
		get ("/login",          SessionController.newForm);
		post("/login",          SessionController.create);
	}

}


















