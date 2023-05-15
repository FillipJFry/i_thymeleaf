package com.goit.fry.thymeleaf;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JavaxServletWebApplication;

import javax.servlet.ServletContext;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

public final class TemplateEngineFactory {

	private static boolean use_file_resolver = false;

	static void enableFileResolver() {

		use_file_resolver = true;
	}

	static TemplateEngine getEngine(ServletContext context) {

		TemplateEngine engine = new TemplateEngine();
		AbstractConfigurableTemplateResolver resolver =
				use_file_resolver ? getFileResolver() : getWebResolver(context);

		resolver.setSuffix(".html");
		resolver.setTemplateMode(TemplateMode.HTML);
		resolver.setOrder(engine.getTemplateResolvers().size());
		resolver.setCacheable(false);
		engine.addTemplateResolver(resolver);

		return engine;
	}

	static Path getTemplatesPath() {

		URL url = TemplateEngineFactory.class.getResource("/templates");
		assert url != null;

		try {
			return Path.of(url.toURI());
		}
		catch (Exception e) {

			throw new RuntimeException(e);
		}
	}

	private static AbstractConfigurableTemplateResolver getWebResolver(ServletContext context) {

		JavaxServletWebApplication webApp = JavaxServletWebApplication.buildApplication(context);
		WebApplicationTemplateResolver resolver = new WebApplicationTemplateResolver(webApp);
		resolver.setPrefix("/WEB-INF/classes/templates/");
		return resolver;
	}

	private static AbstractConfigurableTemplateResolver getFileResolver() {

		FileTemplateResolver resolver = new FileTemplateResolver();
		resolver.setPrefix(getTemplatesPath().toString() + '/');
		return resolver;
	}
}
