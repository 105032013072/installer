package com.bosssoft.platform.installer.io.listener;

public class DefaultFileOperationEvent implements IFileOperationEvent {
	private String source;
	private String dest;
	private String operationType;
	private String message;

	public DefaultFileOperationEvent() {
	}

	public DefaultFileOperationEvent(String source, String dest) {
		this.source = source;
		this.dest = dest;

		this.message = this.source;
	}

	public DefaultFileOperationEvent(String source, String dest, String operationType) {
		this(source, dest);
		this.operationType = operationType;
	}

	public DefaultFileOperationEvent(String source, String dest, String operationType, String message) {
		this(source, dest, operationType);
		this.message = message;
	}

	public String getDest() {
		return this.dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public String getOperationType() {
		return this.operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public String getSource() {
		return this.source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("source:").append(this.source).append('\n');
		sb.append("dest:").append(this.dest).append('\n');
		sb.append("operationType:").append(this.operationType).append('\n');
		sb.append("message:").append(this.message);
		return sb.toString();
	}
}