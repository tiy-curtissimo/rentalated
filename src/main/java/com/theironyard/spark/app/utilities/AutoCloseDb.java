package com.theironyard.spark.app.utilities;

import java.io.Closeable;
import org.javalite.activejdbc.Base;

public class AutoCloseDb implements Closeable, AutoCloseable {

  public AutoCloseDb() {
    Base.open("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/intro", "intro", "intro");
  }

	@Override
	public void close() {
		Base.close();
	}

}
