package hw2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapHeader;
import org.jnetpcap.PcapIf;
import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.nio.JMemory;
import org.jnetpcap.packet.JRegistry;
import org.jnetpcap.packet.Payload;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import hw2.packet.*;

public class DeviceController {
	ArrayList<PcapIf> allDevs = new ArrayList<PcapIf>();
	// 오류 메시지를 담을 수 있는 변수 생성
	StringBuilder errbuf = new StringBuilder();

	public DeviceController() throws IOException {

		int r = Pcap.findAllDevs(allDevs, errbuf);
		int i = 0;
		for (PcapIf d : allDevs) {
			System.out.println(i++);
			System.out.println(d.getName());
			System.out.println(d.getDescription());
		}
		if (r == Pcap.NOT_OK || allDevs.isEmpty())
			throw new IOException("네트워크 장치를 찾을 수 없습니다");
	}

	public PcapIf getDevice(int i) {
		return allDevs.get(i);
	}

	/**
	 * 전체 디바이스로 부터 패킷 정보 출력
	 * 
	 * @param num : 전체 디바이스 탐색 횟수
	 * @throws IOException
	 */
	public void getPackets(PcapIf device, int limit) throws IOException {

		Pcap pcap = Pcap.openLive(device.getName(), Pcap.DEFAULT_SNAPLEN, Pcap.MODE_PROMISCUOUS, Pcap.DEFAULT_TIMEOUT,
				errbuf);

		if (pcap == null)
			throw new IOException("device open failed" + errbuf.toString());

		Ethernet eth = new Ethernet();
		Ip4 ip = new Ip4();
		Tcp tcp = new Tcp();
		Payload payload = new Payload();

		PcapHeader header = new PcapHeader(JMemory.POINTER);
		JBuffer buf = new JBuffer(JMemory.POINTER);
		int id = JRegistry.mapDLTToId(pcap.datalink());

		int num = 0;
		while (pcap.nextEx(header, buf) == Pcap.NEXT_EX_OK && num < limit) {

			PcapPacket packet = new PcapPacket(header, buf);
			packet.scan(id);
			if (packet.hasHeader(eth)) {
				System.out.println("Ethernet");
				System.out.println("source : " + FormatUtils.mac(eth.source()) + "destination : "
						+ FormatUtils.mac(eth.destination()));

			}
			;
			if (packet.hasHeader(ip)) {
				System.out.println("Ip");
				System.out.println("source : " + FormatUtils.ip(ip.source()) + "destination : "
						+ FormatUtils.ip(ip.destination()) + "protocol : " + ip.type());

			}
			if (packet.hasHeader(tcp)) {
				System.out.println("TCP");
				System.out.println("source : " + tcp.source() + "destination : " + tcp.destination());
			}
			if (packet.hasHeader(payload)) {
				System.out.println("Payload");
				System.out.println("payload length : " + payload.getLength());
				System.out.println(payload.toHexdump());
			}

			num++;
		}
		pcap.close();
	}
	
	public List<Packet> getPacketObjs(PcapIf device, int limit) throws IOException {
		
		List<Packet> packetList = new ArrayList<>();
		
		Pcap pcap = Pcap.openLive(device.getName(), Pcap.DEFAULT_SNAPLEN, Pcap.MODE_PROMISCUOUS, Pcap.DEFAULT_TIMEOUT,
				errbuf);

		if (pcap == null)
			throw new IOException("device open failed" + errbuf.toString());

		Ethernet eth = new Ethernet();
		Ip4 ip = new Ip4();
		Tcp tcp = new Tcp();
		Payload payload = new Payload();

		PcapHeader header = new PcapHeader(JMemory.POINTER);
		JBuffer buf = new JBuffer(JMemory.POINTER);
		int id = JRegistry.mapDLTToId(pcap.datalink());

		int num = 0;
		while (pcap.nextEx(header, buf) == Pcap.NEXT_EX_OK && num < limit) {

			PcapPacket packet = new PcapPacket(header, buf);
			packet.scan(id);
			packetList.add(parsePacket(packet, id));
			num++;
		}
		pcap.close();
		return packetList;
	}
	
	public Packet parsePacket(PcapPacket packet, int id) {
		Ethernet eth = new Ethernet();
		Ip4 ip = new Ip4();
		Tcp tcp = new Tcp();
		Payload payload = new Payload();

		hw2.packet.Ethernet ethernetObj = null;
		hw2.packet.Ip ipObj = null;
		hw2.packet.TransportLayer transportObj = null;
		hw2.packet.PayLoad payloadObj = null;

		packet.scan(id);

		if (packet.hasHeader(eth)) {
			String srcMac = FormatUtils.mac(eth.source());
			String destMac = FormatUtils.mac(eth.destination());
			System.out.println("source : " + srcMac + " destination : " + destMac);
			ethernetObj = new hw2.packet.Ethernet(srcMac, destMac, eth.type());
		}
		if (packet.hasHeader(ip)) {
			String srcIp = FormatUtils.ip(ip.source());
			String destIp = FormatUtils.ip(ip.destination());
			int ttl = ip.ttl();
			int length = ip.length();
			System.out.println("source : " + srcIp + " destination : " + destIp);
			ipObj = new hw2.packet.Ip(srcIp, destIp, ttl, length);
		}
		if (packet.hasHeader(tcp)) {
			int srcPort = tcp.source();
			int destPort = tcp.destination();
			long seq = tcp.seq();
			long ack = tcp.ack();
			int flags = tcp.flags();
			int checksum = tcp.checksum();
			System.out.println("source : " + srcPort + " destination : " + destPort);
			transportObj = new hw2.packet.TransportLayer(String.valueOf(srcPort), String.valueOf(destPort), seq, ack,
					flags, checksum);
		}
		if (packet.hasHeader(payload)) {
			int length = payload.getLength();
			String data = payload.toHexdump();
			System.out.println("payload length : " + length);
			System.out.println(data);
			payloadObj = new hw2.packet.PayLoad(length, data);
		}

		long timestamp = packet.getCaptureHeader().timestampInMillis();
		long frameNumber = packet.getFrameNumber();

		hw2.packet.Packet packetObj = new hw2.packet.Packet(timestamp, frameNumber);
		if (ethernetObj != null)
			packetObj.setEthernet(ethernetObj);
		if (ipObj != null)
			packetObj.setIp(ipObj);
		if (transportObj != null)
			packetObj.setTransportLayer(transportObj);
		if (payloadObj != null)
			packetObj.setPayLoad(payloadObj);

		return packetObj;
	}

}
