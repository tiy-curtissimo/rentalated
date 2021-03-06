package com.theironyard.spark.app.utilities;

import java.io.Closeable;
import org.javalite.activejdbc.Base;

public class AutoCloseableDb implements Closeable, AutoCloseable {

	public AutoCloseableDb() {
		Base.open("org.postgresql.Driver", getUrl(), getName(), getPassword());
	}

	@Override
	public void close() {
		Base.close();
	}

	public String getUrl() {
		if (url == null) {
			ProcessBuilder builder = new ProcessBuilder();
			if (builder.environment().get("JDBC_DATABASE_URL") != null) {
				url = builder.environment().get("JDBC_DATABASE_URL");
			} else {
				url = "jdbc:postgresql://localhost:5432/intro";
			}
		}
		return url;
	}

	public String getName() {
		if (name == null) {
			ProcessBuilder builder = new ProcessBuilder();
			if (builder.environment().get("JDBC_DATABASE_USERNAME") != null) {
				name = builder.environment().get("JDBC_DATABASE_USERNAME");
			} else {
				name = "intro";
			}
		}
		return name;
	}

	public String getPassword() {
		if (password == null) {
			ProcessBuilder builder = new ProcessBuilder();
			if (builder.environment().get("JDBC_DATABASE_PASSWORD") != null) {
				password = builder.environment().get("JDBC_DATABASE_PASSWORD");
			} else {
				password = "intro";
			}
		}
		return password;
	}

	private static String url;
	private static String name;
	private static String password;

}
