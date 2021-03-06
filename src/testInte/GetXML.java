package testInte;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Get XML using the wikipedia API.
 * -> get revisions and save them locally in XML
 */

public class GetXML {
	public static void main(String[] args) throws Exception {
//		saveXML("d:\\test.xml",
//				"http://en.wikipedia.org/w/api.php?action=query&titles=Albert%20Einstein&prop=revisions&rvlimit=5&format=xml");
//		saveXML("d:\\test2.xml",
//				"http://en.wikipedia.org/w/api.php?action=query&titles=Albert%20Einstein&prop=revisions&rvlimit=10&format=xml");
//		saveXML("d:\\user.xml",
//		"http://en.wikipedia.org/w/api.php?action=query&list=usercontribs&ucuser=Farpointer&uclimit=5&format=xml");
		saveXML("test.xml",
				"http://en.wikipedia.org/w/api.php?action=query&titles=SPARQL&prop=revisions&rvlimit=5&rvprop=ids|flags|user|timestamp|comment|size&format=xml");
	//	saveXML("d:\\test2.xml",
	//			"http://en.wikipedia.org/w/api.php?action=query&titles=Monkey&prop=revisions&rvlimit=10&format=xml");
	}

	public static void saveXML(String objectFile, String URL) throws Exception {
		
		File object = new File(objectFile);
		if (object.exists()){
			System.out.println ("File already exists: "+ objectFile);
			return;
		}
		
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(objectFile), "UTF-8"));
		// d:\\test.xml is your path of xml
		pw.println(getWebCon(URL));
		// http://www.baidu.com is a URL that you want to borrow
		pw.flush();
		pw.close();
	}

	public static String getWebCon(String domain) {
		// System.out.println("Strat read...("+domain+")");
		StringBuffer sb = new StringBuffer();
		try {
			java.net.URL url = new java.net.URL(domain);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					url.openStream()));
			String line;
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			System.out.println(sb.toString());
			in.close();
		} catch (Exception e) { // Report any errors that arise
			sb.append(e.toString());
			System.err.println(e);
			System.err
					.println("Usage:   java   HttpClient   <URL>   [<filename>]");
		}
		return sb.toString();
	}
	

}
