package de.fuberlin.wiwiss.pubby.exporter;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;

public class HexTuplesWriter extends ModelWriter {
	@Override
	public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
		ExtendedIterator<Resource> it=super.write(model, response);
		if (!it.hasNext()) {
			response.getWriter().write("");
			response.getWriter().close();
		} else {
			StringBuilder builder=new StringBuilder();
			while(it.hasNext()) {
				Resource ind=it.next();
				StmtIterator it2 = ind.listProperties();
				while(it2.hasNext()) {
					Statement st=it2.next();
					builder.append("[ \""+st.getSubject().toString()+"\", \""+st.getPredicate().toString()+"\", ");
					if(st.getObject().isResource()) {
						builder.append("\""+st.getObject().toString()+"\", \"\", \"\" ]"+System.lineSeparator());
					}else if(st.getObject().isLiteral()) {
						builder.append("\""+st.getObject().asLiteral().getValue().toString()+"\", \""+st.getObject().asLiteral().getDatatypeURI()+"\", \"\" ]"+System.lineSeparator());
					}
				}	
				}	
			response.getWriter().write(builder.toString());
			response.getWriter().close();
		}
		return null;
	}
}
