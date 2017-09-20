package com.theironyard.spark.app.utilities;

import java.io.Closeable;
import org.javalite.activejdbc.Base;

public class AutoCloseableDb implements Closeable, AutoCloseable {

	public AutoCloseableDb() {
		Base.open("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/intro", "intro", "intro");
	}

	@Override
	public void close() {
		Base.close();
	}

}
