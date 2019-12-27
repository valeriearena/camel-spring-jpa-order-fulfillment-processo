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
 * @author Michael Hoffman, Pluralsight
 * 
 */
public class WebContextInitializer implements WebApplicationInitializer {

   @Override
   public void onStartup(ServletContext servletContext) throws ServletException {

      // Scans for classes annotated with @Configuration.
      AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();

      rootContext.register(Application.class);
      rootContext.setServletContext(servletContext);
      ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(rootContext));
      dispatcher.setLoadOnStartup(1);
      dispatcher.addMapping("/");
   }

}
