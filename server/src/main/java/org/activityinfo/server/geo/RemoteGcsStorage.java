package org.activityinfo.server.geo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import javax.servlet.http.HttpServletResponse;

import com.google.common.io.Resources;


public class RemoteGcsStorage implements GeometryStorage {

	@Override
	public InputStream openWkb(int adminLevelId) throws IOException {
		URL url = new URL("http://commondatastorage.googleapis.com/aigeo/" + adminLevelId  + ".wkb.gz");
		return new GZIPInputStream(url.openStream());
	}

	@Override
	public void serveJson(int adminLevelId, boolean gzip, HttpServletResponse response)
			throws IOException {
		URL url = new URL("http://commondatastorage.googleapis.com/aigeo/" + adminLevelId  + ".json.gz");
		response.setHeader("Content-Encoding", "gzip");
		Resources.copy(url, response.getOutputStream());
	}
}
