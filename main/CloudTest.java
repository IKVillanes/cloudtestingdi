package cloudtestingdi.main;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cloudtestingdi.configuration.Host;
import cloudtestingdi.configuration.Path;
import cloudtestingdi.connection.Client;
import cloudtestingdi.upload.UploadProject;

/**
 * Classe Class that enables the distribution
 * @author Gustavo Sávio <saviojp@gmail.com>
 */
public class CloudTest {
	
	@Deprecated
	public static void runner(String projectName, String[] classTests,
			String pathFilePermission, String projectPath) {

		Path.PERMISSION.setPath(pathFilePermission);
		Path.UPLOAD.setPath(projectPath);

		try {
			Thread t = new Thread(new UploadProject());
			t.start();
			t.join();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (String testSuite : classTests) {
			Thread t1 = new Thread(new Client(projectName, testSuite, null,
					false));
			t1.start();
		}
	}

	/**
	 * Runs JUnit tests distributed by methods
	 * 
	 * @param String
	 * @return String
	 * @since 23/11/2011
	 * @version 0.1
	 * 
	 */
	public static void runnerDistributingMethods(String projectName,
			String[] classTest, String pathFilePermission, String projectPath) {

		Path.PERMISSION.setPath(pathFilePermission);
		Path.UPLOAD.setPath(projectPath);

		File root = new File("/home/ec2-user/" + projectName + "/bin");
		URLClassLoader classLoader;

		for (String testSuite : classTest) {
			Class<?> clazz;
			Object obj = null;

			try {
				classLoader = URLClassLoader.newInstance(new URL[] { root
						.toURI().toURL() });
				clazz = Class.forName(testSuite, true, classLoader);
				obj = clazz.newInstance();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			ExecutorService e = Executors.newCachedThreadPool();

			for (int i = 0; i <= Host.getInstance().getSize() - 1; i++) {
				e.execute(new UploadProject());
			}

			e.shutdown();

			while (e.isTerminated() == false);

			ExecutorService e2 = Executors.newCachedThreadPool();

			Method[] methods = obj.getClass().getDeclaredMethods();

			for (Method m : methods) {
				e2.execute(new Client(projectName, testSuite, m.getName(), true));
			}

			e2.shutdown();

		}
	}
}