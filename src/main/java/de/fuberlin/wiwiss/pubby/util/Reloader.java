package de.fuberlin.wiwiss.pubby.util;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletContext;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;

import de.fuberlin.wiwiss.pubby.Configuration;
import de.fuberlin.wiwiss.pubby.ConfigurationException;

public class Reloader implements Runnable {

	Configuration config;
	
	ServletContext context;
	
	String configfile;
	
	String realPath;
	
	String error;
	
	public Reloader(Configuration config,String error,String configfile,String realpath) {
		this.config=config;
		this.error=error;
		this.configfile=configfile;
		this.realPath=realpath;
	}
	@Override
	public void run() {
		if(config==null) {
			try {
				String configFileName = this.configfile;
				if (configFileName == null) {
					throw new ConfigurationException("Missing context parameter \"config-file\" in /WEB-INF/web.xml");
				}
				File configFile = new File(configFileName);
				if (!configFile.isAbsolute()) {
					configFile = new File(this.realPath + "/WEB-INF/" + configFileName);
				}
				String url = configFile.getAbsoluteFile().toURI().toString();
				try {
					Model m = FileManager.get().loadModel(url);
					Configuration conf = Configuration.create(m);
					this.config=conf;
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
				error=ex.getMessage();
			}
		}		
	}

}
