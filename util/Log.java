package cloudtestingdi.util;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import cloudtestingdi.configuration.Path;


/**
 * Class of audit logs
 * 
 * @author Gustavo Sávio <saviojp@gmail.com>
 * 
 */
public class Log {

	public static void logme(String ipAdress, String content) {
		try {
			File dir = new File("/home/ec2-user/log");

			if (dir.exists()) {
				Calendar data = Calendar.getInstance();

				BufferedWriter bw = new BufferedWriter(new FileWriter(
						Path.USER.getPath() + "/log/log_" + ipAdress + "_"
								+ data.getTime() + ".txt"));

				bw.append(content);
				bw.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void logmeLocal(String projectName, String content) {
		try {

			File dir = new File(System.getProperty("user.dir"));

			if (dir.exists()) {
				BufferedWriter bw = new BufferedWriter(new FileWriter(
						projectName + "_log_result_.txt", true));

				bw.append(content);
				bw.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
