package hw2.packet;

public record Ip (String sourceip
		,String destinationip
		,int ttl
		,int length){

}
