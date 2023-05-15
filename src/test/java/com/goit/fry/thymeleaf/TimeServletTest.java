package com.goit.fry.thymeleaf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TimeServletTest {

	private static final Logger logger = LogManager.getRootLogger();
	private static String htmlTempl;
	private TimeServlet servlet;
	private HttpServletRequest req;
	private HttpServletResponse resp;
	private StringWriter writer;

	@BeforeAll
	static void init() {

		try {

			Path templatesPath = TemplateEngineFactory.getTemplatesPath();
			templatesPath = templatesPath.resolve("time.html");

			htmlTempl = Files.readString(templatesPath);
			TemplateEngineFactory.enableFileResolver();
		}
		catch (IOException e) {

			assertNull(e);
		}
	}

	@BeforeEach
	void initMocks() {

		servlet = new TimeServlet(TemplateEngineFactory.getEngine(null));
		req = mock(HttpServletRequest.class);
		resp = mock(HttpServletResponse.class);
		writer = new StringWriter();
	}

	@Test
	void service() {

		logger.info("testing TimeServlet::service()");
		try {
			when(req.getCookies()).thenReturn(new Cookie[]{});
			when(resp.getWriter()).thenReturn(new PrintWriter(writer));

			servlet.init();
			servlet.service(req, resp);
			servlet.destroy();
		}
		catch (Exception e) {

			e.printStackTrace();
			assertNull(e);
		}

		checkHTMLisCorrect("UTC");
	}

	@Test
	void serviceWithCookie() {

		final String timezone = "GMT+5";

		logger.info("testing TimeServlet::serviceWithCookie()");
		try {
			when(req.getCookies()).thenReturn(new Cookie[]{new Cookie("tz", timezone)});
			when(resp.getWriter()).thenReturn(new PrintWriter(writer));

			servlet.init();
			servlet.service(req, resp);
			servlet.destroy();
		}
		catch (Exception e) {

			e.printStackTrace();
			assertNull(e);
		}

		checkHTMLisCorrect(timezone);
	}

	private void checkHTMLisCorrect(String timezone) {

		timezone = timezone.replace("+", "\\+");
		String patternStr = htmlTempl.replace("<title th:text=\"${tzDescr}\">",
						"<title>" + timezone + " time")
									.replace("<p th:text=\"${timeStr}\">",
						"<p>([1-9][0-9]+-[0-9]+-[0-9]+ [0-9]+:[0-9]+:[0-9]+) "
										+ timezone);
		Pattern p = Pattern.compile(patternStr);

		String body = writer.toString();
		logger.info("response body: " + body);
		Matcher m = p.matcher(body);
		assertTrue(m.find());

		DateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		assertDoesNotThrow(() -> dtFormat.parse(m.group(1)));
	}
}