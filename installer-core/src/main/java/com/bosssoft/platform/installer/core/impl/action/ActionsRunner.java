package com.bosssoft.platform.installer.core.impl.action;

import com.bosssoft.platform.installer.core.action.IAction;

public class ActionsRunner extends Thread {
	private boolean suspendRequested = false;

	public ActionsRunner() {
	}

	public ActionsRunner(IAction[] actions) {
	}

	public void requestSuspend() {
		this.suspendRequested = true;
	}

	private synchronized void checkSuspended() throws InterruptedException {
		while (this.suspendRequested)
			wait();
	}

	public synchronized void requestResume() {
		this.suspendRequested = false;
		notify();
	}

	public void run() {
		for (int i = 0; i < 1000; i++) {
			try {
				checkSuspended();
			} catch (InterruptedException ex1) {
				ex1.printStackTrace();
			}
			try {
				Thread.sleep(5L);
			} catch (InterruptedException localInterruptedException1) {
			}
		}
		System.out.println("test over");
	}
}