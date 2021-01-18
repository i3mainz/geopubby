package de.fuberlin.wiwiss.pubby.util;

public class Condition {

	String property;
	
	String value;
	
	String operator;
	
	@Override
	public String toString() {
		StringBuilder builder=new StringBuilder();
		builder.append("sh:property [\n sh:path "+property+";\n");
		builder.append("sh:");
		return builder.toString();
	}
	
}
