package hw2.packet;

public record Ethernet(String sourcemac
		,String destinationmac
		, int type 
		) {}
