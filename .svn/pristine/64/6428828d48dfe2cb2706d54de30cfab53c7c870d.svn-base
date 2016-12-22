/*
* 2014-12-10 下午2:11:23
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.logs;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.mbgo.search.util.NullPathException;
import com.mbgo.search.util.ResourceReader;

public class LogAnalyzeTest {

	private static String LOG_BASE_FOLDER = "G:/mbsearch/log/";
	
	public void costLogs(File f, LoggerBeanManager manager) throws NullPathException, IOException {
		ResourceReader reader = new ResourceReader(f);
		reader.load();
		String line = null;
		
		while((line = reader.readLine()) != null) {
			manager.cost(line);
		}
		reader.close();
	}
	
	@Test
	public void testQg() throws NullPathException, IOException {
		LoggerBeanManager manager = new LoggerBeanManager();
		
		File folder = new File(LOG_BASE_FOLDER);
		if(folder.isDirectory()) {
			File[] files = folder.listFiles();
			for(File f : files) {
				if(f.isFile()) {
					costLogs(f, manager);
				}
			}
		}
		manager.show();
	}
}
