package hw2.packet;

public record TransportLayer(
        String sourcePort
		,String destinationPort
		,long sequenceNum
		,long acknowledgementNum
		,int flags
		,int checksum
		){}
