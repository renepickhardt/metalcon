package de.metalcon.autocompleteServer;

import java.util.concurrent.BlockingQueue;

/**
 * 
 * Manages the command queue. As long as there are commands on the queue, the
 * head is run and removed. Waits if the queue is empty, starts working again if
 * a new command is added.
 * 
 * @author Christian
 * 
 */
public class Worker implements Runnable {
	private final BlockingQueue<Command> commands;

	public Worker(BlockingQueue<Command> commands) {
		this.commands = commands;
	}

	@Override
	public void run() {
		Command command;

		while (true) {
			try {
				command = this.commands.take();
				command.run(command.getContext());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
