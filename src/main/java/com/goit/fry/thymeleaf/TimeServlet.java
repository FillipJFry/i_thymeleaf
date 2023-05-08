package com.goit.fry.thymeleaf;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {

	private DateFormat dtFormat;
	private TemplateEngine engine;

	@Override
	public void init() {

		dtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dtFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		engine = new TemplateEngine();

		FileTemplateResolver resolver = new FileTemplateResolver();
		resolver.setPrefix("./templates/");
		resolver.setSuffix(".html");
		resolver.setTemplateMode(TemplateMode.HTML);
		resolver.setOrder(engine.getTemplateResolvers().size());
		resolver.setCacheable(false);
		engine.addTemplateResolver(resolver);
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String timezoneStr = req.getParameter("timezone");
		if (timezoneStr != null) {
			timezoneStr = UTCHandler.replaceIfNecessary(timezoneStr);
			dtFormat.setTimeZone(TimeZone.getTimeZone(timezoneStr));
		}
		else {
			dtFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			timezoneStr = "UTC";
		}

		resp.setContentType("text/html");
		Map<String, Object> vars = new HashMap<>();
		vars.put("tzDescr", timezoneStr + " time");
		vars.put("timeStr", dtFormat.format(new Date()) + ' ' + timezoneStr);

		Context context = new Context(req.getLocale(), vars);
		try (PrintWriter out = resp.getWriter()) {

			engine.process("time", context, out);
		}
	}

	@Override
	public void destroy() {

		dtFormat = null;
		engine = null;
	}
}
