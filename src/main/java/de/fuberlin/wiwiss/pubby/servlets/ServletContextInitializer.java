package de.fuberlin.wiwiss.pubby.servlets;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;

import de.fuberlin.wiwiss.pubby.Configuration;
import de.fuberlin.wiwiss.pubby.ConfigurationException;
import de.fuberlin.wiwiss.pubby.util.Reloader;

public class ServletContextInitializer implements ServletContextListener {
	public final static String SERVER_CONFIGURATION =
			ServletContextInitializer.class.getName() + ".serverConfiguration";
	public final static String ERROR_MESSAGE =
			ServletContextInitializer.class.getName() + ".errorMessage";
	
	private ScheduledExecutorService scheduler;
	
	public static boolean initConfiguration(ServletContext context){
		System.out.println("Called init configuration!");
	    try {
			String configFileName = context.getInitParameter("config-file");
			if (configFileName == null) {
				throw new ConfigurationException("Missing context parameter \"config-file\" in /WEB-INF/web.xml");
			}
			File configFile = new File(configFileName);
			if (!configFile.isAbsolute()) {
				configFile = new File(context.getRealPath("/") + "/WEB-INF/" + configFileName);
			}
			String url = configFile.getAbsoluteFile().toURI().toString();
			System.out.println("SPARQL Endpoint: "+url);
			try {
				FileManager.get().resetCache();
				Model m = FileManager.get().loadModel(url);
				Configuration conf = Configuration.create(m);
				context.setAttribute(SERVER_CONFIGURATION, conf);
			} catch (JenaException ex) {
			    if(ex.getCause()!=null){
			        				throw new ConfigurationException(
						"Error parsing configuration file <" + url + ">: " + 
						ex.getMessage()+"\nCause: "+ex.getCause().getMessage());
			    }else{
			        StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    ex.printStackTrace(pw);
				throw new ConfigurationException(
						"Error parsing configuration file <" + url + ">: " + 
						ex.getMessage()+"\n"+sw.toString());			        
			    }

			}
		} catch (ConfigurationException ex) {
			System.out.println(ex.getMessage());
			log(ex, context);
			return false;
		}
		return true;
	}
	
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		final ServletContext context = sce.getServletContext();
		Boolean result=ServletContextInitializer.initConfiguration(context);
		/*if(!result) {
			Configuration config=(Configuration) context.getAttribute(SERVER_CONFIGURATION);
			String error=context.getAttribute(ERROR_MESSAGE).toString();
			scheduler=Executors.newSingleThreadScheduledExecutor();
			scheduler.scheduleAtFixedRate(new Reloader(config,error,context.getInitParameter("config-file"),context.getRealPath("/")), 3000, 30000, TimeUnit.SECONDS);			
		}*/
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// Do nothing special.
	}
	
	private static void log(Exception ex, ServletContext context) {
		context.log("######## PUBBY CONFIGURATION ERROR ######## ");
		context.log(ex.getMessage());
		context.log("########################################### ");
		context.setAttribute(ERROR_MESSAGE, ex.getMessage());
	}
}
