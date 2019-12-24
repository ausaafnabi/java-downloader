import java.io.*;
import java.net.*;
import java.util.*;

class Download extends Observable implements Runnable{
	private static final int MAX_BUFFER_SIZE = 1024;

	public static final String STATUS[] ={"Downloading","Paused","Complete","Cancelled","Error"};
	public static final int DOWNLOADING =0;
	public static final int PAUSED = 1;
	public static final int COMPLETE =2;
	public static final int CANCELLED = 3;
	public static final int ERROR = 4;
	
	private URL url;		 //download URL
	private int size;		 //size of the download in bytes
	private int downloaded;	 //number oof bytes downloaded
	private int status;		 // current status of the download

	//Constructor for Download.
	
	public Download(URL url){
		this.url =url;
		size = -1;
		downloaded = 0;
		status = DOWNLOADING;

		download();
	}

	//Fetch Download's URL.
	
	public String getURL(){
		return url.toString();
	}

	//Fetch URL size
	
	public int getSize(){
		return size;
	}
	//Get this download's progress
	
	public float getProgress(){
		return ((float)downloaded/size)*100;
	}

	//Get the download Status

	public int getStatus(){
		return status;
	}

	//pause this download.
	
	public void pause(){
		status = PAUSED;
		stateChanged();
	}

	//Resume this download
	
	public void resume(){
		status = DOWNLOADING;
		stateChanged();
		download();
	}

	//Cancel this download
	
	public void cancel(){
		status = CANCELLED;
		stateChanged();
	}

	//Mark this download as having an error
	
	private void error(){
		status = ERROR;
		stateChanged();
	}

	private void download(){
		Thread thread = new Thread(this);
		thread.start();
	}
	private String getFileName(URL url){
		String fileName = url.getFile();
		return fileName.substring (fileName.lastIndexOf('/')+1);
	}
	//Download file.
	
	public void run(){
		RandomAccessFile file = null;
		InputStream stream = null;

		try{
			//Open the Connection
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			//Specify the portion of the file to be downloaded
			connection.setRequestProperty("Range","bytes=" + downloaded + "-");
			//Connect to the server.
			connection.connect();

			if(connection.getResponseCode()/100 !=2){
				error();
			}

			int contentLength = connection.getContentLength();
			if(contentLength<1){
				error();
			}

			if(size==1){
				size = contentLength;
				stateChanged();
			}

			//Open file and Seek to end of it
			
			file = new RandomAccessFile(getFileName(url),"rw");
			file.seek(downloaded);

			stream = connection.getInputStream();
			while(status==DOWNLOADING){
				byte buffer[];
				if(size-downloaded>MAX_BUFFER_SIZE){
					buffer = new byte[MAX_BUFFER_SIZE];
				} else {
					buffer = new byte[size-downloaded];
				}
				//Read form server into buffer.
				int read = stream.read(buffer);
				if (read==-1)
					break;

				file.write(buffer,0,read);
				downloaded +=read;
				stateChanged();
			}
			/*Change status to complete if the point was 
			   reached because of downloading has finished*/
			   if (status == DOWNLOADING){
			   	status = COMPLETE;
			   	stateChanged();
			}
		} catch(Exception e){
			error();
		} finally {
			//close file
			if(file!=null){
				try{
					file.close();
				} catch (Exception e){}
			}

			if(stream != null){
				try{
					stream.close();
				} catch (Exception e){}
			}
		}
	}
	
	private void stateChanged(){
		setChanged();
		notifyObservers();
	}		

}
	

	
	


