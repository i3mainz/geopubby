package de.fuberlin.wiwiss.pubby.servlets;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;

import de.fuberlin.wiwiss.pubby.Configuration;
import de.fuberlin.wiwiss.pubby.ConfigurationException;

public class ServletContextInitializer implements ServletContextListener {
	public final static String SERVER_CONFIGURATION =
			ServletContextInitializer.class.getName() + ".serverConfiguration";
	public final static String INITPROCESS =
			ServletContextInitializer.class.getName() + ".initProcess";
	public final static String ERROR_MESSAGE =
			ServletContextInitializer.class.getName() + ".errorMessage";
	
	public static boolean initConfiguration(ServletContext context){
		context.setAttribute(INITPROCESS, true);
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
			try {
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
			log(ex, context);
			context.setAttribute(INITPROCESS, false);
			return false;
		}
		context.setAttribute(INITPROCESS, false);
		return true;
	}
	
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		final ServletContext context = sce.getServletContext();
		//final Timer timer = new Timer();
		ServletContextInitializer.initConfiguration(context);
		/*if(!result){
            final TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Boolean result=ServletContextInitializer.initConfiguration(context);
                    if(result){
                        timer.cancel();
                        timer.purge();
                    }
                }
            };
            timer.schedule(task, 30000);
		}*/
		/*
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
			try {
				Model m = FileManager.get().loadModel(url);
				Configuration conf = Configuration.create(m);
				context.setAttribute(SERVER_CONFIGURATION, conf);
			} catch (JenaException ex) {
			    if(ex.getCause()!=null){
			        				throw new ConfigurationException(
						"Error parsing configuration file <" + url + ">: " + 
						ex.getMessage()+"\nCause: "+ex.getCause().getMessage());
			    }else{
				throw new ConfigurationException(
						"Error parsing configuration file <" + url + ">: " + 
						ex.getMessage());			        
			    }

			}
		} catch (ConfigurationException ex) {
			log(ex, context);
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
