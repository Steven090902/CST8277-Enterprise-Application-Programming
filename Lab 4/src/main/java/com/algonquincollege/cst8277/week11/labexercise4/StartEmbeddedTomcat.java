/**
 * File: StartEmbeddedTomcat.java <br>
 * Course materials (21F) CST 8277
 * 
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @date Mar 29, 2021
 * @author (original) Mike Norman
 * @date 2020 11
 */
package com.algonquincollege.cst8277.week11.labexercise4;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Set;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import ch.qos.logback.access.tomcat.LogbackValve;

/**
 * <b>Description</b></br>
 * </br>
 * Main class that starts an instance of an Embedded Tomcat Server
 * 
 * @see https://devcenter.heroku.com/articles/create-a-java-web-application-using-embedded-tomcat
 */
public class StartEmbeddedTomcat {
	private static final Class< ?> MY_KLASS = MethodHandles.lookup().lookupClass();
	private static final Logger logger = LoggerFactory.getLogger( MY_KLASS);

	private static final String STARTING_EMBEDDED_TOMCAT_MSG = "starting Embedded Tomcat ...";
	private static final String STOPPING_EMBEDDED_TOMCAT = "stopping Embedded Tomcat ...";
	private static final String SOMETHING_WENT_WRONG_MSG = "Something went wrong starting Embedded Tomcat";

	private static final String EXIT_ON_FAILURE_PROP = "org.apache.catalina.startup.EXIT_ON_INIT_FAILURE";
	private static final String TMPDIR_PROP = "java.io.tmpdir";
	private static final String WEBAPP_DIR = "src/main/webapp/";
	private static final String TOMCAT_EMBEDDED_PORT_PROP = "tomcat.embedded.port";
	private static final String JERSEY_SERVLET_NAME = "jersey-container-servlet";
	private static final String DEFAULT_PORT = "9090";

	public static void main( String[] args) {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
		System.setProperty( EXIT_ON_FAILURE_PROP, "true");
		Tomcat tomcat = new Tomcat();
		Runtime.getRuntime().addShutdownHook( new Thread( () -> {
			try {
				logger.info( STOPPING_EMBEDDED_TOMCAT);
				tomcat.stop();
			} catch ( LifecycleException e) {
				// ignore
			}
		}));
		String tmpdir = new File( System.getProperty( TMPDIR_PROP)).getAbsolutePath();
		tomcat.setBaseDir( tmpdir);
		LogbackValve valve = new LogbackValve();
		valve.setQuiet( true);
		valve.setFilename( LogbackValve.DEFAULT_FILENAME);
		tomcat.getHost().getPipeline().addValve( valve);
		tomcat.setAddDefaultWebXmlToWebapp( false);
		StandardContext standardContext = (StandardContext) tomcat.addWebapp( "",
				new File( WEBAPP_DIR).getAbsolutePath());
		Tomcat.addServlet( standardContext, JERSEY_SERVLET_NAME, resourceConfig());
		standardContext.addServletMappingDecoded( "/*", JERSEY_SERVLET_NAME);
		tomcat.getConnector().setPort( Integer.valueOf( System.getProperty( TOMCAT_EMBEDDED_PORT_PROP, DEFAULT_PORT)));
		try {
			logger.info( STARTING_EMBEDDED_TOMCAT_MSG);
			tomcat.start();
		} catch ( Exception e) {
			logger.error( SOMETHING_WENT_WRONG_MSG, e);
		}
		tomcat.getServer().await();
	}

	private static ServletContainer resourceConfig() {
		Set< Class< ?>> restResourceClasses = new HashSet<>();
		restResourceClasses.add( HelloWorldRestResource.class);
		restResourceClasses.add( HealthCheckResource.class);
		ResourceConfig rc = new ResourceConfig( restResourceClasses);
		JacksonJaxbJsonProvider jacksonJaxbJsonProvider = new JacksonJaxbJsonProvider();
		ObjectMapper mapper = new ObjectMapper().registerModule( new JavaTimeModule())
				.configure( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
				// lenient parsing of JSON - if a field has a type, don't fall to pieces
				.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		jacksonJaxbJsonProvider.setMapper( mapper);
		rc.register( jacksonJaxbJsonProvider);
		return new ServletContainer( rc);
	}

}