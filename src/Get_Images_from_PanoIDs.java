import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Get_Images_from_PanoIDs {
	private static final String PHOTOS_PATH = "Photos/";
	private static final String PANOIDs_FILE = "panoids.txt";
	public static void doIt()
	{
		try (BufferedReader br = new BufferedReader(new FileReader(PANOIDs_FILE)))
		{
			int i = 0;
			String strPanoId;
			
			while ((strPanoId = br.readLine()) != null && i <= 10000)
			{
				//Get image itself
				if (getImageById(strPanoId) == true)
				{				
					//Get image information
					getImageXML(strPanoId);				
					
					i++;
				}
			}
			System.out.println("Finished!\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean getImageById(String strPanoId)
	{
		URL u = null;
		HttpURLConnection con = null;
		byte bytes[];
		InputStream is;
		
	
		try {
			String strPitch, strHeading;
			String destinationFile;
			OutputStream os;
			int len;
			for (int pitch = 0 ; pitch < 40 ; pitch += 30)
				for (int heading = 0 ; heading < 360 ; heading += 36)
				{
					strPitch = Integer.toString(pitch);
					strHeading = Integer.toString(heading);
					u = new URL("http://maps.googleapis.com/maps/api/streetview?size=640x640&pano=" + strPanoId + "&fov=120&heading=" + strHeading + "&pitch=" + strPitch + "&sensor=false");			
				
					try{
						con = (HttpURLConnection) u.openConnection();
						is = con.getInputStream();
												
						destinationFile = PHOTOS_PATH + strPanoId + "_" + strPitch + "_" + strHeading + ".jpg";
						os = new FileOutputStream(destinationFile );
						
						bytes = new byte[2048];
						while ((len = is.read(bytes)) != -1)
						{
							os.write(bytes, 0, len);
						}
						
						is.close();
						os.flush();
						os.close();
						
						//Wait for some time, if we request too fast, GSV may return the 403 response code
						Thread.sleep(1000);

					} catch (IOException e) {
						e.printStackTrace();
						return false;
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
						return false;
					}
				}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static void getImageXML(String strPanoid)
	{
		URL u = null;
		HttpURLConnection con = null;
		InputStream is;
		byte bytes[];
		
		try {
			u = new URL("http://cbk0.googleapis.com/cbk?output=xml&panoid=" + strPanoid);			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		try{
			con = (HttpURLConnection)u.openConnection();
			is = con.getInputStream();
							
			String destinationFile = PHOTOS_PATH + strPanoid + ".xml";
			OutputStream os = new FileOutputStream(destinationFile);
			
			int len = con.getContentLength();
			
			bytes = new byte[2048];
			
			while ((len = is.read(bytes)) != -1)
				os.write(bytes, 0, len);

			is.close();
			os.flush();
			os.close();
			
			//parse the xml file the get adjacent panoids
			//String arrInfo[] = parseImageXML(destinationFile);

		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
}
