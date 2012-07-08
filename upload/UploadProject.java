package cloudtestingdi.upload;

import java.io.IOException;

import cloudtestingdi.configuration.Host;
import cloudtestingdi.configuration.Path;


/**
 * Class that performs upload of projects
 * 
 * @author Gustavo Sávio <saviojp@gmail.com>
 */
public class UploadProject implements Runnable {

	/**
	 * Upload of project
	 * 
	 * @param String
	 * @param String
	 * @return void
	 * @throws IOException
	 * @throws InterruptedException
	 * @since 04/10/2011
	 * @version 0.2
	 * 
	 */

	@Override
	public void run() {
		long init = System.currentTimeMillis();

		String h = Host.getInstance().getNextHost();

		System.out.println("Distributing the Application... " + h);

		String host = "ec2-user@ec2-" + h.replace(".", "-")
				+ ".compute-1.amazonaws.com";

		String command = "scp -r -C -c arcfour -i " + Path.PERMISSION.getPath()
				+ " " + Path.UPLOAD.getPath() + " " + host + ":"
				+ Path.USER.getPath();
		
		System.out.println(command);
		try {
			Process p = Runtime.getRuntime().exec(command);
			p.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		long end = System.currentTimeMillis();
		long diff = end - init;

		System.out.println("Distributed application in " + (diff / 1000)
				+ " seconds.\n");
	}
}