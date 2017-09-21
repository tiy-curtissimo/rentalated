package com.theironyard.spark.app.utilities;

import java.io.Closeable;
import org.javalite.activejdbc.Base;

public class AutoCloseableDb implements Closeable, AutoCloseable {

	public AutoCloseableDb() {
		System.out.println("USING URL " + getUrl());
		Base.open("org.postgresql.Driver", getUrl(), getName(), getPassword());
	}

	@Override
	public void close() {
		Base.close();
	}

	private String getUrl() {
		if (url == null) {
			ProcessBuilder builder = new ProcessBuilder();
			if (builder.environment().get("DATABASE_URL") != null) {
				url = builder.environment().get("DATABASE_URL");
			} else {
				url = "jdbc:postgresql://localhost:5432/intro";
			}
		}
		return url;
	}

	private String getName() {
		if (name == null) {
			ProcessBuilder builder = new ProcessBuilder();
			if (builder.environment().get("DATABASE_URL") == null) {
				name = "intro";
			}
		}
		return name;
	}

	private String getPassword() {
		if (password == null) {
			ProcessBuilder builder = new ProcessBuilder();
			if (builder.environment().get("DATABASE_URL") == null) {
				password = "intro";
			}
		}
		return password;
	}

	private static String url;
	private static String name;
	private static String password;

}
