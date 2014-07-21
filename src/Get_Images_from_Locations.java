import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class Get_Images_from_Locations {
	private static final String PHOTOS_PATH = "Photos/";
	private static final String LOCATIONs_FILE = "Locations.txt";
	public static void doIt()
	{
		try (BufferedReader br = new BufferedReader(new FileReader(LOCATIONs_FILE)))
		{
			int i = 0;
			boolean flag = true; //true if able to query 20 images at this position, false if not, then need to query and wait again 
			String strLocation = "";
			int count = 0; //count the number of failure, if exceed 10 times the move to the next position 
			while (i <= 30000)
			{
				if (flag == true)
				{
					while ((strLocation = br.readLine()) == null)
						;
					count = 0;
				}
				//remove space 
				strLocation = strLocation.replaceAll("\\s", "");
				//Get image itself
				if (getImageByLocation(strLocation) == true)
				{				
					//Get image information
					getImageXMLByLocation(strLocation);				
					
					i++;
					flag = true;
				}
				else 
				{
					flag = false;
					count ++; //increase the number of failure
					if (count == 10) //if reach a threshold
						//flag = true; //then move on
						return;
				}
			}
			System.out.println("Finished!\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void doIt2()
	{
		try (BufferedReader br = new BufferedReader(new FileReader(LOCATIONs_FILE)))
		{
			int i = 0;
			int iCountFailure = 0;
			String strLocation = "";
			
			while (true)
			{
				while (i <= 30000)
				{
					while ((strLocation = br.readLine()) == null)
						;					
					//remove space 
					strLocation = strLocation.replaceAll("\\s", "");
					
					//Get image itself
					if (getImageByLocation(strLocation) == true)
					{
						iCountFailure = 0;
						
						//Get image information
						getImageXMLByLocation(strLocation);										
						i++;
					}
					else
					{
						iCountFailure++;
						if (iCountFailure == 5)
							break;
					}
				}
				
				if (i==30000)
					break;
				while (getImageByLocation(strLocation) == false)
					Thread.sleep(600000);
			}
			System.out.println("Finished!\n");
		} catch (IOException e) {
			//e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			
		}
	}
	
	public static boolean getImageByLocation(String strLocation)
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
					u = new URL("http://maps.googleapis.com/maps/api/streetview?size=640x640&location=" + strLocation + "&fov=120&heading=" + strHeading + "&pitch=" + strPitch + "&sensor=false");			
				
					try{						
						con = (HttpURLConnection) u.openConnection();
						con.setConnectTimeout(20000);
						is = con.getInputStream();
												
						destinationFile = PHOTOS_PATH + strLocation + "_" + strPitch + "_" + strHeading + ".jpg";
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
						Thread.sleep(2000);

					} catch (IOException e) {
						//e.printStackTrace();
						return false;
					}
					catch (InterruptedException e)
					{
						//e.printStackTrace();
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
	
	public static void getImageXMLByLocation(String strLocation)
	{
		URL u = null;
		HttpURLConnection con = null;
		InputStream is;
		byte bytes[];
		
		try {
			u = new URL("http://cbk0.googleapis.com/cbk?output=xml&ll=" + strLocation);			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		try{
			con = (HttpURLConnection)u.openConnection();
			is = con.getInputStream();
							
			String destinationFile = PHOTOS_PATH + strLocation + ".xml";
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
			//e.printStackTrace();
			return;
		}
	}
}
