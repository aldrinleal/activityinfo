package org.activityinfo.polygons;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import org.opengis.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeatureCollection;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.OutputStreamOutStream;
import com.vividsolutions.jts.io.WKBWriter;

public class WkbOutput implements OutputWriter {

	private DataOutputStream dataOut;
	private WKBWriter writer = new WKBWriter();
	private File outputFile;
	private ByteArrayOutputStream baos;
	private int numFeatures = 0;


	public WkbOutput(File outputDir, int adminLevelId) throws IOException {
        outputFile = new File(outputDir, adminLevelId + ".wkb.gz");
		baos = new ByteArrayOutputStream();
		dataOut = new DataOutputStream(baos);
	}

	
	public void start(SimpleFeatureCollection features) throws IOException {
		
	}
 
	
	public void write(int adminEntityId, Geometry geometry) throws IOException {
		if(!geometry.isValid()) {
			throw new IllegalStateException(adminEntityId + " has invalid geometry");
		}
		dataOut.writeInt(adminEntityId);		
		writer.write(geometry, new OutputStreamOutStream(dataOut));
		numFeatures ++;
	}

	public void close() throws IOException {		
		DataOutputStream out = new DataOutputStream(
				new GZIPOutputStream(
						new FileOutputStream(outputFile)));
		out.writeInt(numFeatures);
		out.write(baos.toByteArray());
		out.close();
	}

}
