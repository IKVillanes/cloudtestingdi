package cloudtestingdi.configuration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Classe Class that defines the hosts
 * @author Gustavo Sávio <saviojp@gmail.com>
 */
public class Host {

	private Queue<String> queue;
	private ArrayList<String> address;
	private static Host host = new Host();

	/**
	 * Initializes the queue of addresses
	 */
	private Host() {
		this.address = new ArrayList<String>();
		this.queue = new LinkedList<String>();
		this.queue.offer("23.20.2.133");
	}
	
	/**
	 * @return an instance of host (Host)
	 */
	public static Host getInstance() {
		return host;
	}
	
	/**
	 * @return the queue ( Queue<String>)
	 */
	public Queue<String> getIPs() {
		return this.queue;
	}

	/**
	 * @return list of address formated (ArrayList<String>)
	 */
	public ArrayList<String> getRemoteAddress() {

		for (String ip : this.queue) {
			this.address.add("ec2-user@ec2-" + ip.replace(".", "-")
					+ ".compute-1.amazonaws.com");
		}
		return this.address;
	}

	/**
	 * @return size of the queue (int)
	 */
	public int getSize() {
		return this.queue.size();
	}

	/**
	 * @return return next host (String)
	 */
	public synchronized String getNextHost() {
		if (!this.queue.isEmpty()) {
			String machine = this.queue.remove();
			this.queue.offer(machine);
			return machine;
		}
		return null;
	}
}