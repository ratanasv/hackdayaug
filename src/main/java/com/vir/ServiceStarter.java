package com.vir;
import java.io.IOException;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;


public class ServiceStarter {

	/**
	 * @param args
	 * @throws IrcException 
	 * @throws IOException 
	 * @throws NickAlreadyInUseException 
	 */
	public static void main(String[] args) throws NickAlreadyInUseException, IOException, IrcException {
		VirBot virBot = new VirBot();
		virBot.setVerbose(true);
		virBot.connect("irc.freenode.net");
		virBot.joinChannel("#SFOInterns");
		virBot.joinChannel("#elasticsearch");
		virBot.joinChannel("#openbsd");
		virBot.joinChannel("#cisco");
		virBot.joinChannel("#latex");
		virBot.joinChannel("#ubuntu-de-offtopic");
		virBot.joinChannel("#startups");
		virBot.joinChannel("#wikipedia-en");
		virBot.joinChannel("#RubyOnRails");
		virBot.joinChannel("#Reddit");
		virBot.joinChannel("#openstack");
		virBot.joinChannel("#ipv6");
		virBot.joinChannel("#ruby");
		virBot.joinChannel("#vim");
		virBot.joinChannel("#minecraft");
		virBot.joinChannel("#git");
		virBot.joinChannel("#python");
		virBot.joinChannel("#Node.js");
		virBot.joinChannel("#blueflood");
		virBot.joinChannel("#vir-experimental");
		//virBot.listChannels();
	}

}
