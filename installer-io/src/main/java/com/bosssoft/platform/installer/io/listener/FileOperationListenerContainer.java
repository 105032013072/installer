package com.bosssoft.platform.installer.io.listener;

import java.util.ArrayList;
import java.util.List;

public class FileOperationListenerContainer {
	private List<IFileOperationListener> listeners = new ArrayList<IFileOperationListener> ();

	public void fireOperationStarted(IFileOperationEvent event) {
		synchronized (this.listeners) {
			for (IFileOperationListener listener : this.listeners)
				listener.beforeOperation(event);
		}
	}

	public void fireOperationFinished(IFileOperationEvent event) {
		synchronized (this.listeners) {
			for (IFileOperationListener listener : this.listeners)
				listener.afterOperation(event);
		}
	}

	public void addListeter(IFileOperationListener listener) {
		if (listener == null) {
			return;
		}
		synchronized (this.listeners) {
			this.listeners.add(listener);
		}
	}

	public void removeListener(IFileOperationListener listener) {
		if (listener == null) {
			return;
		}
		synchronized (this.listeners) {
			this.listeners.remove(listener);
		}
	}

	public IFileOperationListener[] getAllListeners() {
		return (IFileOperationListener[]) this.listeners.toArray(new IFileOperationListener[0]);
	}
}