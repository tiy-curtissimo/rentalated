package com.theironyard.spark.app.utilities;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;

import spark.Request;

public class MustacheRenderer {

	private static final MustacheRenderer instance = new MustacheRenderer("templates");

	private DefaultMustacheFactory factory;

	private MustacheRenderer(String folderName) {
		factory = new DefaultMustacheFactory(folderName);
	}

	public static MustacheRenderer getInstance() {
		return instance;
	}

	public String render(Request req, String templatePath, Map<String, Object> model) {
		if (model == null) {
			model = new HashMap<String, Object>();
		}
		model.put("currentUser", req.session().attribute("currentUser"));
		model.put("noUser", req.session().attribute("currentUser") == null);

		Mustache mustache = factory.compile(templatePath);
		StringWriter writer = new StringWriter();
		mustache.execute(writer, model);
		return writer.toString();
	}

}
