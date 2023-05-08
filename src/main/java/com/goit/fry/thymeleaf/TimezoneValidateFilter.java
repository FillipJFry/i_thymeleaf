package com.goit.fry.thymeleaf;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.TimeZone;

@WebFilter(value = "/time/*")
public class TimezoneValidateFilter extends HttpFilter {

	private static final TimeZone gmtZone = TimeZone.getTimeZone("GMT");

	@Override
	protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {

		String timezoneStr = req.getParameter("timezone");
		if (timezoneStr == null) {
			chain.doFilter(req, resp);
			return;
		}

		TimeZone timezone = TimeZone.getTimeZone(timezoneStr);
		if (!UTCHandler.presentUTCstr(timezoneStr) &&
				!timezoneStr.equals("GMT") && timezone.equals(gmtZone)) {

			resp.setContentType("text/html");
			try (PrintWriter out = resp.getWriter()) {

				out.println("<html>");
				out.println("<body>");
				out.println("<head>");
				out.println("<title>Invalid time zone</title>");
				out.println("</head>");
				out.println("<body>");
				out.println("<p>this timezone is invalid: " + timezoneStr + "</p>");
				out.println("</body>");
				out.println("</html>");
			}
		}
		else
			chain.doFilter(req, resp);
	}
}
