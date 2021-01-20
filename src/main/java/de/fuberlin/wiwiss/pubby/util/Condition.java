package de.fuberlin.wiwiss.pubby.util;

public class Condition {

	String property;
	
	String value;
	
	String operator;
	
	@Override
	public String toString() {
		StringBuilder builder=new StringBuilder();
		builder.append("sh:condition [\n sh:property [\n sh:path "+property+";\n");
		switch(operator) {
		case "PropertyIsEqualTo":
		case "PropertyIsNotEqualTo":
		case "PropertyIsLessThan":
		case "PropertyIsLessThanOrEqualTo":
		case "PropertyIsGreaterThan":
		case "PropertyIsGreaterThanOrEqualTo":
		case "PropertyIsLike":
		case "PropertyIsNull":
		case "PropertyIsBetween":
		case "Intersects":
		case "Equals":
		case "Disjoint":
		case "Touches":
		case "Within":
		case "Overlaps":
		case "Crosses":
		case "Contains":
		case "DWithin":
		case "Beyond":
		case "BBOX":
		}
		builder.append("sh:");
		return builder.toString();
	}
	
}
