package com.bosssoft.platform.installer.wizard.cfg;

import java.util.ArrayList;
import java.util.List;

public class Servers {
	private List<Server> servers = new ArrayList<Server>();

	public void addServer(Server s) {
		this.servers.add(s);
	}

	public List<Server> getServers() {
		return this.servers;
	}
}