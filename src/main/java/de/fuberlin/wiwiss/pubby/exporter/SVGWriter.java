package de.fuberlin.wiwiss.pubby.exporter;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.json.JSONException;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;

import de.fuberlin.wiwiss.pubby.vocab.GEO;


/**
 * Writes a GeoPubby instance as SVG.
 */
public class SVGWriter extends ModelWriter {
	
	@Override
	public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
		ExtendedIterator<Resource> it=super.write(model, response);
		if(!it.hasNext()) {
			response.getWriter().write("");
			response.getWriter().close();	
		}else {

			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			StringWriter strwriter=new StringWriter();
			try {
			XMLStreamWriter writer=new IndentingXMLStreamWriter(factory.createXMLStreamWriter(strwriter));
			writer.writeStartDocument();
			writer.writeStartElement("svg");
			writer.writeAttribute("version", "1.1");
			writer.writeAttribute("baseProfile", "full");
			writer.writeAttribute("width", "800mm");
			writer.writeAttribute("height", "600mm");
			writer.writeAttribute("viewBox", "-400 -300 800 600");

		Double lat=null,lon=null;
		while(it.hasNext()) {
			Resource ind=it.next();
			StmtIterator it2 = ind.listProperties();

			while(it2.hasNext()) {
				Statement curst=it2.next();
				if(GEO.ASWKT.getURI().equals(curst.getPredicate().getURI().toString()) ||
						GEO.P_GEOMETRY.getURI().equals(curst.getPredicate().getURI())
						|| 
						GEO.P625.getURI().equals(curst.getPredicate().getURI())) {
					try {
						Geometry geom=reader.read(curst.getObject().asLiteral().getString());	
						if(geom.getGeometryType().equalsIgnoreCase("point")) {
							lat=geom.getCentroid().getCoordinate().getY();
							lon=geom.getCentroid().getCoordinate().getX();							
						}else {
							writer.writeStartElement("polyline");
							writer.writeAttribute("stroke", "black");
							writer.writeAttribute("stroke-width","3");
							String points="";
							
							for(int j=0;j<geom.getCoordinates().length-1;j+=2) {
								points+=geom.getCoordinates()[j]+","+geom.getCoordinates()[j+1];
							}
							writer.writeAttribute("points", points);
							writer.writeEndElement();	
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else if(GEO.P_LAT.getURI().equals(curst.getPredicate().getURI().toString())){
					lat=curst.getObject().asLiteral().getDouble();
				}else if(GEO.P_LONG.getURI().equals(curst.getPredicate().getURI().toString())){
					lon=curst.getObject().asLiteral().getDouble();
				}else if(GEO.GEORSSPOINT.getURI().equals(curst.getPredicate().getURI().toString())){
					lat=Double.valueOf(curst.getObject().asLiteral().getString().split(" ")[0]);
					lon=Double.valueOf(curst.getObject().asLiteral().getString().split(" ")[1]);
				}
			}
			}
		it.close();
		if(lat!=null && lon!=null) {
			writer.writeStartElement("circle");
			writer.writeAttribute("stroke", "black");
			writer.writeAttribute("stroke-width","3");
			writer.writeAttribute("r", "10");
			writer.writeAttribute("cy", lat+"");
			writer.writeAttribute("cx", lon+"");
			writer.writeEndElement();
		}
		
		writer.writeEndElement();
		writer.writeEndDocument();
		writer.flush();
		}catch(XMLStreamException e) {
			e.printStackTrace();
		}
		try {
			response.getWriter().write(strwriter.toString());
			response.getWriter().close();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		return null;
		
	}

}
