package de.fuberlin.wiwiss.pubby.exporter.rdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.main.api.sax.EXIResult;

import de.fuberlin.wiwiss.pubby.exporter.GeoModelWriter;

public class RDFEXIWriter extends GeoModelWriter {

	@Override
	public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		try {
			EXIResult exiResult = new EXIResult(exiFactory);
			exiResult.setOutputStream(response.getOutputStream());
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			model.getWriter("RDF/XML").write(model, stream, null);
	        String xmlstring = new String(stream.toByteArray());
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler( exiResult.getHandler() );
			xmlReader.parse(new InputSource(new StringReader(xmlstring)));
		} catch (IOException | SAXException | EXIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // parse XML input
		response.getOutputStream().close();
		return null;
	}
	
}
