import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Get_LatLng_from_PanoIDs {
	public static void doIt()
	{
		FileWriter fo; //save all Lat, Lng
		try (BufferedReader br = new BufferedReader(new FileReader("panoids.txt")))
		{
			String strPanoId, strLatLng;
			int i = 0;
			
			fo = new FileWriter(new File("Locations.txt"));
			
			while ((strPanoId = br.readLine()) != null)
			{
				strLatLng = get_LatLng_from_PanoID(strPanoId);
				if (strLatLng != null)
					fo.write(strLatLng + "\n");
				System.out.println(i);
				i++;
			}
			
			fo.close();
			System.out.println("Finished!\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static String get_LatLng_from_PanoID(String strPanoId)
	{
		URL u = null;
		HttpURLConnection con = null;
		InputStream is;
		byte bytes[];
		
		try {
			u = new URL("http://cbk0.googleapis.com/cbk?output=xml&panoid=" + strPanoId);			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		try{
			con = (HttpURLConnection)u.openConnection();
			is = con.getInputStream();
			
						
			String destinationFile = "temp.xml";
			OutputStream os = new FileOutputStream(destinationFile);
			
			int len = con.getContentLength();
			
			bytes = new byte[2048];
			
			while ((len = is.read(bytes)) != -1)
				os.write(bytes, 0, len);

			is.close();
			os.flush();
			os.close();
			//parse the xml file the get adjacent panoids
			return parseLocationImageXML(destinationFile);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static String parseLocationImageXML(String strXMLfilePath)
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc;
		File file = new File(strXMLfilePath);		
		NodeList ListNodes;
		Node node;
		Attr attr;
		String strLat, strLng;
		
		if (file.exists() == false)
		{
			System.out.println("No file");
			return null;
		}
		
		try{
			db = dbf.newDocumentBuilder();
			doc = db.parse(file);
			
			///Data property
			ListNodes = doc.getElementsByTagName("data_properties");
			
			if (ListNodes.getLength() < 0)
			{
				System.out.println("No data");
				return null;
			}
			
			node = ListNodes.item(0);
			
			//Lat
			attr = (Attr) node.getAttributes().getNamedItem("lat");
			strLat = attr.getValue();
			
			//Lng
			attr = (Attr) node.getAttributes().getNamedItem("lng");
			strLng = attr.getValue();
			
			return strLat + ", " + strLng;
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			return null;
		}
	}
}
