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
 * WebApplicationInitializer is automatically detected and is the key to how Servlet 3.x containers automcatically register services.
 *
 * How does Spring integrate with Servlet 3.x (Tomcat 7 & 8)?
 * Implementations of WebApplicationInitializer are detected automatically
 * by SpringServletContainerInitializer, which itself is bootstrapped automatically by any Servlet 3.0 container because
 * SpringServletContainerInitializer implements ServletContainerInitializer.
 *
 * ServletContainerInitializer​ is defined in Servlet 3.x standards and is an interface with a single method called onStartup.
 * Spring uses the Servlet 3.x ServletContainerInitializer to start up the Spring IoC container and load the application context.
 * (Similar to how Jersey uses the Servlet 3.x ServletContainerInitializer to register resources without XML.)
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
