/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.activityinfo.server.report.output;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;


public class LocalStorageProvider implements StorageProvider {

	private String folder;
	
	public LocalStorageProvider(String folder) {
		this.folder = folder.replace('\\', '/');
	}
	
	@Override
	public TempStorage allocateTemporaryFile(String mimeType, String suffix) throws IOException {
		String path = folder + "/img" + Long.toString((new Date()).getTime()) + suffix;
		OutputStream stream = new FileOutputStream(path);
		
		return new TempStorage("file://" + path, stream);
	}

}
