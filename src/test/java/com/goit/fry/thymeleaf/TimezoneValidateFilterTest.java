package com.goit.fry.thymeleaf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TimezoneValidateFilterTest {

	private static final Logger logger = LogManager.getRootLogger();
	private TimezoneValidateFilter filter;
	private HttpServletRequest req;
	private HttpServletResponse resp;
	private FilterChain chain;
	private FilterConfig filterConfig;
	private StringWriter writer;

	@BeforeAll
	static void init() {

		TemplateEngineFactory.enableFileResolver();
	}

	@BeforeEach
	void initMocks() {

		filter = new TimezoneValidateFilter();
		req = mock(HttpServletRequest.class);
		resp = mock(HttpServletResponse.class);
		chain = mock(FilterChain.class);
		filterConfig = mock(FilterConfig.class);

		writer = new StringWriter();
	}

	@ParameterizedTest
	@ValueSource (strings = {"GMT", "GMT-1", "GMT+2",
							"UTC", "UTC+2",
							"Europe/Kiev"})
	void doFilterCorrect(String tzStr) {

		logger.info("testing TimezoneValidateFilter::doFilterCorrect()");
		when(req.getParameter("timezone")).thenReturn(tzStr);
		try {
			when(resp.getWriter()).thenReturn(new PrintWriter(writer));
			filter.init(filterConfig);
			filter.doFilter(req, resp, chain);

			assertEquals("", writer.toString());
			verify(chain).doFilter(req, resp);
		}
		catch (Exception e) {

			assertNull(e);
		}
	}

	@ParameterizedTest
	@ValueSource (strings = {"GM", "GMT 1", "GMT2",
							"UTC-", "UTC 2", "UTC+2a",
							"Europe/Berdychiv"})
	void doFilterIncorrect(String tzStr) {

		logger.info("testing TimezoneValidateFilter::doFilterIncorrect()");
		when(req.getParameter("timezone")).thenReturn(tzStr);
		try {
			when(resp.getWriter()).thenReturn(new PrintWriter(writer));
			filter.init(filterConfig);
			filter.doFilter(req, resp, chain);

			assertFalse(writer.toString().isEmpty());
			verify(chain, never()).doFilter(req, resp);
		}
		catch (Exception e) {

			assertNull(e);
		}
	}
}