package hw2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import hw2.packet.Packet;
import hw2.xml.XMLgenerator;

public class Main {
	public static void main(String[] args) throws IOException {
		DeviceController dc = new DeviceController();
		List<Packet> packetList = new ArrayList<>();
		
		packetList = dc.getPacketObjs(dc.getDevice(4), 15);
		
		StringBuilder packetString = new StringBuilder();
		
		for(Packet p : packetList) {
			packetString.append(p.toXMLString());
			packetString.append("\r\n");
		}
		
		XMLgenerator.makeXML("packets.xml", packetString.toString());
		System.out.println("종료");
		
	}
}
