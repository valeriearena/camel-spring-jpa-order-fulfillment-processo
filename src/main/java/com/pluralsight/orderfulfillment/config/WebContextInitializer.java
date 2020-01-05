package com.pluralsight.orderfulfillment.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Initializes the web application in place of a descriptor.
 * Loads Spring context.
 *
 * WebContextInitializer implements WebApplicationInitializer
 * WebApplicationInitializer is automatically detected is the key to how Servlet 3.x containers automcatically register services.
 *
 * How does Spring integrate with Servlet 3.x (Tomcat 7 & 8)?
 * Implementations of WebApplicationInitializer is detected automatically
 * by SpringServletContainerInitializer, which itself is bootstrapped automatically by any Servlet 3.0 container because
 * SpringServletContainerInitializer implements ServletContainerInitializer.
 *
 * SpringServletContainerInitializer is loaded and instantiated and
 * onStartup(java.util.Set<java.lang.Class<?>>, javax.servlet.ServletContext) is
 * invoked by any Servlet 3.0-compliant container during container startup
 * assuming that the spring-web module JAR is present on the classpath.
 * This occurs by detecting the spring-web module's
 * META-INF/services/javax.servlet.ServletContainerInitializer service provider configuration file.
 *
 * When Servlet 3.0-compliant container starts up, it scans jars for
 * META-INF/services/javax.servlet.ServletContainerInitializer​.
 * When the container finds the file, it reads the path, instantiates the implementation,
 * and invokes the onStartup method.
 *
 *
 * See its SpringServletContainerInitializer Javadoc for details on this bootstrapping mechanism.
 */
public class WebContextInitializer implements WebApplicationInitializer {

  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {

    // Scans for classes annotated with @Configuration.
    AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();

    rootContext.register(AppConfig.class);
    rootContext.setServletContext(servletContext);
    ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(rootContext));
    dispatcher.setLoadOnStartup(1);
    dispatcher.addMapping("/");
  }

}
