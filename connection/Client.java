package cloudtestingdi.connection;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;

import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;

import cloudtestingdi.configuration.Host;
import cloudtestingdi.configuration.Path;

/**
 * Classe Class client
 * 
 * @author Gustavo Sávio <saviojp@gmail.com>
 */
public class Client implements Serializable, Runnable {

	private static final long serialVersionUID = -3336965634015526432L;
	private Socket client;
	private ObjectOutputStream oos;
	private String projectName;
	private String testSuite;
	private String methodName;
	private boolean isMethod;
	private String logErrorHost;

	public Client(String projectName, String testSuite, String methodName,
			boolean isMethod) {
		this.projectName = projectName;
		this.testSuite = testSuite;
		this.methodName = methodName;
		this.isMethod = isMethod;
	}

	public boolean getIsMethod() {
		return this.isMethod;
	}

	public String getTestSuite() {
		return this.testSuite;
	}

	public String getMethodName() {
		return this.methodName;
	}

	public String getProjectName() {
		return this.projectName;
	}

	/**
	 * Send object to client server
	 * 
	 * @throws IOException
	 * @throws Exception
	 * @since 05/10/2011
	 * @version 0.1
	 * 
	 */
	@Override
	public void run() {

		try {
			String host = Host.getInstance().getNextHost();

			System.out.println("Request to " + host);

			this.logErrorHost = "ec2-user@ec2-" + host.replace(".", "-")
					+ ".compute-1.amazonaws.com";

			// Server - Executor
			this.client = new Socket(host, 1099);
			this.client.setSoTimeout(1800000);

			this.oos = new ObjectOutputStream(this.client.getOutputStream());

			// Send request to the server running
			this.oos.writeObject(new Client(this.projectName, this.testSuite,
					this.methodName, this.isMethod));
			this.oos.flush();

			// Receives the server response
			InputStreamReader streamReader = new InputStreamReader(
					this.client.getInputStream());
			BufferedReader reader = new BufferedReader(streamReader);

			String line = null;

			StringBuffer sb = new StringBuffer();

			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
				System.out.println(line);
			}

			/*
			 * Debug
			 * 
			 * Log.logmeLocal(this.projectName, sb.toString());
			 */

			streamReader.close();
			reader.close();

			this.oos.close();
			this.client.close();

		} catch (Exception e) {
			System.out.println(this.logErrorHost);
			e.printStackTrace();
		}
	}

	/**
	 * JUnit runs on the server and returns the console output
	 * 
	 * @param String
	 * @return String
	 * @throws IOException
	 * @throws InterruptedException
	 * @since 05/10/2011
	 * @version 0.2
	 * 
	 */
	public String execute() throws IOException, InterruptedException {

		String command = "java -cp " + Path.JUNIT.getPath() + ":"
				+ Path.USER.getPath() + this.projectName + "/bin:. "
				+ "org.junit.runner.JUnitCore " + this.testSuite;

		Process p = Runtime.getRuntime().exec(command);

		StringBuilder result = new StringBuilder();

		InputStreamReader isr = new InputStreamReader(p.getInputStream());

		BufferedReader br = new BufferedReader(isr);

		String line = null;

		while ((line = br.readLine()) != null) {
			result.append(new String(line + "\n"));
		}

		isr.close();

		return result.toString().trim();
	}

	/**
	 * Runs a TestCase
	 * 
	 * @param String
	 * @return String
	 * @since 23/11/2011
	 * @version 0.1
	 * 
	 */
	public String executeMethod(String clazzName, String methodName,
			String projectName) {

		Class<?> clazz;
		Object retobj = null;
		File root = new File("/home/ec2-user/" + projectName + "/bin");
		URLClassLoader classLoader;
		boolean flagFailure = false;

		try {
			classLoader = URLClassLoader.newInstance(new URL[] { root.toURI()
					.toURL() });

			clazz = Class.forName(clazzName, true, classLoader);
			Class<?> partypes[] = new Class[1];
			partypes[0] = String.class;

			Constructor<?> ct = clazz.getConstructor(partypes);
			Object arglist[] = new Object[1];
			arglist[0] = new String(methodName);

			retobj = ct.newInstance(arglist);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		TestResult r = ((TestCase) retobj).run();

		StringBuilder result = new StringBuilder();

		result.append("Class: " + clazzName + " Method: " + methodName + "\n");

		for (Enumeration<TestFailure> failure = r.failures(); failure
				.hasMoreElements();) {

			TestFailure f = failure.nextElement();
			result.append(f.trace() + "\n");
			flagFailure = true;
		}

		if (flagFailure) {
			result.append("FAILURES!!!\n");
		}

		result.append("Tests run:" + r.runCount() + "," + " Failures: "
				+ r.failureCount() + "\n \n");

		return result.toString();
	}
}