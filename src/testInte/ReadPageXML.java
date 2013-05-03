package testInte;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

/**
 * A possible starting point. Modified in a quick and dirty way to keep track
 * also of "the provenance of the provenance", i.e. the text version of the
 * pages and the xml responses to the query. This may vary between different
 * crawls at different times.
 */

public class ReadPageXML {

	public static final String DIRECTORY = "content/";
	
	// TODO: modified to reproduce one of the ProvBench examplea
	public static void main(String[] args) throws Exception {
		String totalNodeNumber = CountNodeNumber.countAllNodeNumber();
		File contentDir = new File (DIRECTORY);
		contentDir.mkdir();
		String title = "Newcastle";
		String revision = "10";
		int depth = 5;
		String uclimit = "3";
		startWithPage(title, revision, totalNodeNumber, depth, uclimit);
	}

	public static void startWithPage(String title, String rvlimit,
			String totalNodeNumber, int depth, String uclimit) throws Exception {
		title = title.replaceAll(" ", "%20");
		title = title.replaceAll("=", "%3D");
		title = title.replaceAll("[+]", "%2B");
		System.out.println(title);
		if (depth > 0){
			readXMLbyURL(
					"http://en.wikipedia.org/w/api.php?action=query&titles="
							+ title
							+ "&prop=revisions&rvlimit="
							+ rvlimit
							+ "&rvprop=ids|flags|user|timestamp|comment|size&format=xml",
					totalNodeNumber, depth, uclimit, rvlimit, title);
			
		}
	}

	public static void readXMLbyFileName(String fileName,
			String totalNodeNumber, int depth, String uclimit, String rvlimit)
			throws Exception {
		SAXBuilder builder = new SAXBuilder();

		File xmlFile = new File(fileName);
		Document doc = (Document) builder.build(xmlFile);
		readXML(doc, totalNodeNumber, depth, uclimit, rvlimit);
	}

	//TODO: changed here - saves also the xml response
	public static void readXMLbyURL(String fileName, String totalNodeNumber,
			int depth, String uclimit, String rvlimit,String title) throws Exception{
		GetXML.saveXML(DIRECTORY+title+".xml", fileName);
		readXMLbyURL(fileName,  totalNodeNumber, depth,  uclimit,  rvlimit);
		
	}
	
	public static void readXMLbyURL(String fileName, String totalNodeNumber,
			int depth, String uclimit, String rvlimit) throws Exception {

		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(fileName);
		readXML(doc, totalNodeNumber, depth, uclimit, rvlimit);
	}

	public static void readXML(Document doc, String totalNodeNumber, int depth,
			String uclimit, String rvlimit) throws Exception {

		
		Element root = doc.getRootElement();
		// System.out.println(root);

		List queryNo = root.getChildren("query");

		depth--;

		for (int i = 0; i < queryNo.size(); i++) {
			// I know stupid i,j,k,a,b,c....can chang to iterator, but not it is
			// not easy for understanding
			Element queryElenemt = (Element) queryNo.get(i);

			List pagesNo = queryElenemt.getChildren("pages");
			for (int j = 0; j < pagesNo.size(); j++) {
				Element pagesElement = (Element) pagesNo.get(j);

				List pageNo = pagesElement.getChildren("page");
				for (int k = 0; k < pageNo.size(); k++) {
					Element pageElement = (Element) pageNo.get(k);
					String pageid = pageElement.getAttribute("pageid")
							.getValue();
					String title = pageElement.getAttribute("title").getValue();
					System.out.println("pageid:" + pageid + "  title:" + title);

					List revisionNo = pageElement.getChildren("revisions");
					String user = "";
					// depth = depth - 1;
					for (int a = 0; a < revisionNo.size(); a++) {
						Element revisionElement = (Element) revisionNo.get(a);

						List revNo = revisionElement.getChildren("rev");

						for (int b = 0; b < revNo.size(); b++) {
							Element revElement = (Element) revNo.get(b);
							String revid = revElement.getAttribute("revid")
									.getValue();

							String parentid = revElement.getAttribute(
									"parentid").getValue();
							// String minor =
							// revElement.getAttribute("minor").getValue();
							user = revElement.getAttribute("user").getValue();
							String time = revElement.getAttribute("timestamp")
									.getValue();

							String comment = new String();
							try {
								comment = revElement.getAttribute("comment")
										.getValue();
							} catch (Exception e1) {
								comment = "null";
							}
							String size = new String();
							try {
								size = revElement.getAttribute("size")
										.getValue();
							} catch (Exception e1) {
								size = "0";
							}


							CreateGraph.getData(title, revid, parentid, user,
									time, comment, size, pageid,
									totalNodeNumber);

							//TODO: added - save the text of the page					
							title = title.replaceAll(" ", "%20");
							title = title.replaceAll("=", "%3D");
							title = title.replaceAll("[+]", "%2B");
							title = title.replaceAll("/", "_");
							
							GetXML.saveXML(DIRECTORY + title + "_" + revid
									+ ".html",
									"http://en.wikipedia.org/w/index.php?title="
											+ title + "&oldid=" + revid);
							
							
							ReadUserXML.startWithUser(user, uclimit,
									totalNodeNumber, depth, rvlimit);
						}

					}
				}
			}

			List query_continueNo = root.getChildren("query-continue");
			for (int c = 0; c < query_continueNo.size(); c++) {
				Element qc_revisionsElement = (Element) query_continueNo.get(c);
				List qc_revisionsNo = qc_revisionsElement
						.getChildren("revisions");
				for (int d = 0; d < qc_revisionsNo.size(); d++) {
					Element rvstartidElement = (Element) qc_revisionsNo.get(d);
					if (rvstartidElement == null)
						continue;		
					if (rvstartidElement.getAttribute("rvstartid") == null)
						continue;
					String rvstartid = rvstartidElement.getAttribute(
							"rvstartid").getValue();
					System.out.println("rvstartid:" + rvstartid);
				}
			}
		}

	}

}
