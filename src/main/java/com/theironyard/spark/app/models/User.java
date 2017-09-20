package com.theironyard.spark.app.models;

import org.javalite.activejdbc.Model;
import org.mindrot.jbcrypt.BCrypt;

public class User extends Model {

	public User() {}

	public User(String email, String password, String firstName, String lastName) {
		setEmail(email);
		setPassword(password);
		setFirstName(firstName);
		setLastName(lastName);
	}

	public String getEmail() {
		return getString("email");
	}

	public void setEmail(String email) {
		set("email", email);
	}

	public String getPassword() {
		return getString("password");
	}

	public void setPassword(String password) {
		password = BCrypt.hashpw(password, BCrypt.gensalt());
		set("password", password);
	}

	public String getFirstName() {
		return getString("first_name");
	}

	public void setFirstName(String firstName) {
		set("first_name", firstName);
	}

	public String getLastName() {
		return getString("last_name");
	}

	public void setLastName(String lastName) {
		set("last_name", lastName);
	}

}
