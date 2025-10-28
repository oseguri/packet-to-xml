package hw2.packet;

public class Packet {
	long timestamp;
	long frame_number;
	
	Ethernet eth;
	Ip ip;
	TransportLayer transport;
	PayLoad payload;
	
	public Packet(long timestamp, long frame_number) {
		this.timestamp = timestamp;
		this.frame_number = frame_number;
	}
	
	public void setEthernet(Ethernet eth) {
		this.eth = eth;
	}
	
	public void setIp(Ip ip) {
		this.ip = ip;
	}
	
	public void setTransportLayer(TransportLayer tp) {
		this.transport = tp;
	}
	
	public void setPayLoad(PayLoad pl) {
		this.payload = pl;
	}
	
	public String toXMLString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("<Packet timestamp=\"").append(timestamp).append("\" frame_number=\"").append(frame_number).append("\">\n");

	    if (eth != null) {
	        sb.append("  <Ethernet type=\"").append(eth.type()).append("\">\n");
	        sb.append("    <SourceMAC>").append(eth.sourcemac()).append("</SourceMAC>\n");
	        sb.append("    <DestinationMAC>").append(eth.destinationmac()).append("</DestinationMAC>\n");
	        sb.append("  </Ethernet>\n");
	    }

	    if (ip != null) {
	        sb.append("  <Ip ttl=\"").append(ip.ttl()).append("\" length=\"").append(ip.length()).append("\">\n");
	        sb.append("    <SourceIp>").append(ip.sourceip()).append("</SourceIp>\n");
	        sb.append("    <DestinationIp>").append(ip.destinationip()).append("</DestinationIp>\n");
	        sb.append("  </Ip>\n");
	    }

	    if (transport != null) {
	        sb.append("  <TransportLayer sequenceNum=\"").append(transport.sequenceNum())
	          .append("\" acknowledgementNum=\"").append(transport.acknowledgementNum())
	          .append("\" flags=\"").append(transport.flags())
	          .append("\" checksum=\"").append(transport.checksum())
	          .append("\">\n");
	        sb.append("    <SourcePort>").append(transport.sourcePort()).append("</SourcePort>\n");
	        sb.append("    <DestinationPort>").append(transport.destinationPort()).append("</DestinationPort>\n");
	        sb.append("  </TransportLayer>\n");
	    }

	    if (payload != null) {
	        sb.append("  <Payload length=\"").append(payload.length()).append("\">\n");
	        sb.append("    <Data>").append(payload.data()).append("</Data>\n");
	        sb.append("  </Payload>\n");
	    }

	    sb.append("</Packet>");
	    return sb.toString();
	}

	
}
