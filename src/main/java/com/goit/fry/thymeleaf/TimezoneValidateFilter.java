package com.goit.fry.thymeleaf;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@WebFilter(value = "/time/*")
public class TimezoneValidateFilter extends HttpFilter {

	private static final TimeZone gmtZone = TimeZone.getTimeZone("GMT");
	private TemplateEngine engine;

	@Override
	public void init(FilterConfig config) throws ServletException {

		engine = TemplateEngineFactory.getEngine(config.getServletContext());
	}

	@Override
	protected void doFilter(HttpServletRequest req, HttpServletResponse resp,
							FilterChain chain) throws IOException, ServletException {

		String timezoneStr = req.getParameter("timezone");
		if (timezoneStr == null) {
			chain.doFilter(req, resp);
			return;
		}

		TimeZone timezone = TimeZone.getTimeZone(timezoneStr);
		if (!UTCHandler.presentUTCstr(timezoneStr) &&
				!timezoneStr.equals("GMT") && timezone.equals(gmtZone)) {

			resp.setContentType("text/html");
			Map<String, Object> vars = new HashMap<>();
			vars.put("tzDescr", "Invalid time zone");
			vars.put("timeStr", "this timezone is invalid: " + timezoneStr);

			Context context = new Context(req.getLocale(), vars);
			try (PrintWriter out = resp.getWriter()) {

				engine.process("time", context, out);
			}
		}
		else
			chain.doFilter(req, resp);
	}
}
