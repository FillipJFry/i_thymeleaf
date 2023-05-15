package com.goit.fry.thymeleaf;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {

	private DateFormat dtFormat;
	private TemplateEngine engine = null;

	public TimeServlet() { }

	TimeServlet(TemplateEngine engine) {

		this.engine = engine;
	}

	@Override
	public void init() {

		dtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dtFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		if (engine == null)
			engine = TemplateEngineFactory.getEngine(getServletContext());
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String timezoneStr = req.getParameter("timezone");
		if (timezoneStr != null) {
			timezoneStr = UTCHandler.replaceIfNecessary(timezoneStr);
			dtFormat.setTimeZone(TimeZone.getTimeZone(timezoneStr));
		}
		else {
			timezoneStr = getTimeZoneFromCookie(req);

			if (timezoneStr == null)
				timezoneStr = "UTC";
			dtFormat.setTimeZone(TimeZone.getTimeZone(timezoneStr));
		}
		resp.addCookie(new Cookie("tz", timezoneStr));

		Map<String, Object> vars = new HashMap<>();
		vars.put("tzDescr", timezoneStr + " time");
		vars.put("timeStr", dtFormat.format(new Date()) + ' ' + timezoneStr);

		Context context = new Context(req.getLocale(), vars);
		try (PrintWriter out = resp.getWriter()) {

			engine.process("time", context, out);
		}
	}

	private String getTimeZoneFromCookie(HttpServletRequest req) {

		Cookie[] cookies = req.getCookies();
		for (Cookie cookie : cookies)
			if ("tz".equals(cookie.getName()))
				return cookie.getValue();

		return null;
	}

	@Override
	public void destroy() {

		dtFormat = null;
		engine = null;
	}
}
