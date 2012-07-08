package cloudtestingdi.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Classe Class server
 * 
 * @author Gustavo Sávio <saviojp@gmail.com>
 */
public class Server {

	public static void main(String[] args) throws IOException {
		ExecutorService pool = Executors.newFixedThreadPool(2);
		ServerSocket server = null;

		try {
			server = new ServerSocket(1099);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Server ready!");

		while (true) {
			final Socket client = server.accept();
			client.setSoTimeout(1800000);

			Runnable task = new Runnable() {
				@Override
				public void run() {
					try {
						runningSocket(client);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			pool.execute(task);
		}

	}

	public static void runningSocket(Socket client) throws IOException,
			ClassNotFoundException, InterruptedException {
		ObjectInputStream ois = new ObjectInputStream(client.getInputStream());

		Client c = (Client) ois.readObject();
		String result;

		if (c.getIsMethod()) {
			result = c.executeMethod(c.getTestSuite(), c.getMethodName(),
					c.getProjectName());
		} else {
			result = c.execute();
		}

		/*
		 * Debug
		 * 
		 * Log.logme(client.getInetAddress().getHostAddress(), result);
		 * System.out.println(client.getInetAddress().getHostAddress());
		 * System.out.println(result);
		 */

		PrintWriter writer = new PrintWriter(client.getOutputStream());
		writer.print(result);
		writer.flush();
		writer.close();
	}
}