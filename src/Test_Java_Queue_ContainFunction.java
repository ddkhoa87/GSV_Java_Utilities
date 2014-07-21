import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Queue;
import java.util.Vector;


public class Test_Java_Queue_ContainFunction {
	public static void readToQueue(Queue<String> q)
	{
		BufferedReader fi;
		String strId;
		Boolean isExist_QueueContainFunction, isExist_ReadTextFileByLine = false;
		
		try{
			fi = new BufferedReader(new FileReader("panoids.txt"));
			while((strId = fi.readLine()) != null)
			{
				q.add(strId);
				
				if (strId.compareTo("tLas2nE_VNm-GOP6Sc1ZlA") == 0)
					isExist_ReadTextFileByLine = true;
			}
			
			fi.close();
		}
		catch (IOException e){
		
		}
		isExist_QueueContainFunction = q.contains("tLas2nE_VNm-GOP6Sc1ZlA");
		System.out.println("Queue.contains function:" + isExist_QueueContainFunction.toString());
		System.out.println("Read text file by line and String.compareto function:" + isExist_ReadTextFileByLine.toString());
		System.out.println(q.size());
	}
	public static void readToVector()
	{
		BufferedReader fi;
		String strId;
		Boolean isExist_VectorContainFunction, isExist_ReadTextFileByLine = false;
		Vector<String> v = new Vector<String>();
		try{
			fi = new BufferedReader(new FileReader("panoids.txt"));
			while((strId = fi.readLine()) != null)
			{
				v.add(strId);
				
				if (strId.compareTo("tLas2nE_VNm-GOP6Sc1ZlA") == 0)
					isExist_ReadTextFileByLine = true;
			}
			
			fi.close();
		}
		catch (IOException e){
		
		}
		isExist_VectorContainFunction = v.contains("tLas2nE_VNm-GOP6Sc1ZlA");
		System.out.println("Queue.contains function:" + isExist_VectorContainFunction.toString());
		System.out.println("Read text file by line and String.compareto function:" + isExist_ReadTextFileByLine.toString());
		System.out.println(v.size());
	}
}
