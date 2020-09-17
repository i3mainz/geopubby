package de.fuberlin.wiwiss.pubby.exporter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.io.ParseException;


import de.fuberlin.wiwiss.pubby.util.ReprojectionUtils;
import de.fuberlin.wiwiss.pubby.vocab.GEO;

public class XLSWriter extends GeoModelWriter {

	public XLSWriter(String epsg) {
		super(epsg);
	}
	
	@Override
	public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
		ExtendedIterator<Resource> it = super.write(model, response);
		if (!it.hasNext()) {
			response.getWriter().write("");
			response.getWriter().close();
		} else {
			Workbook wb = new HSSFWorkbook();
			Sheet sheet = wb.createSheet("sheet");
			Integer colNum = 1;
			Row firstRow = sheet.createRow(0);
			Row secondRow = sheet.createRow(1);
			StringWriter strwriter = new StringWriter();
			while (it.hasNext()) {
				Resource ind = it.next();
				if (ind.hasProperty(GEO.EPSG)) {
					sourceCRS = "EPSG:" + ind.getProperty(GEO.EPSG).getObject().asLiteral().getValue().toString();
				}
				StmtIterator it2 = ind.listProperties();
				while (it2.hasNext()) {
					Statement curst = it2.next();
					boolean handled = this.handleGeometry(curst, ind, model);
					if (!handled) {
						firstRow.createCell(colNum).setCellValue(curst.getPredicate().getURI());
						secondRow.createCell(colNum++).setCellValue(curst.getObject().toString());
					}
				}
			}
			if (geom != null) {
				Coordinate coord = geom.getCoordinate();
				lat = coord.getY();
				lon = coord.getX();
			}
			if (lon != null && lat != null) {
				firstRow.createCell(0).setCellValue("the_geom");
				if (geom == null && this.epsg != null) {
					try {
						geom = reader.read("Point(" + lon + " " + lat + ")");
						geom = ReprojectionUtils.reproject(geom, sourceCRS, epsg);
						secondRow.createCell(0).setCellValue(geom.toText());
					} catch (ParseException e) {
						secondRow.createCell(0).setCellValue("Point (" + lat + " " + lon + ")");
					}
				} else if (geom != null) {
					secondRow.createCell(0).setCellValue(
							"Point (" + geom.getCoordinate().getX() + " " + geom.getCoordinate().getY() + ")");
				} else {
					secondRow.createCell(0).setCellValue("Point (" + lat + " " + lon + ")");
				}
				lat = null;
				lon = null;
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				wb.write(baos);
				wb.close();
				response.getWriter().write(new String(baos.toByteArray(), "UTF-8"));
				response.getWriter().close();
			} catch (IOException e) {
				try {
					wb.close();
					baos.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				response.getWriter().write(strwriter.toString());
				response.getWriter().close();
			}

		}
		return null;
	}

}
