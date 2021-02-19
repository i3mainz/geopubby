package de.fuberlin.wiwiss.pubby.util;

public class Condition {

	String property;
	
	String value;
	
	String operator;
	
	public String toSHACL(){
		StringBuilder builder=new StringBuilder();
		builder.append("sh:condition ["+System.lineSeparator()+" sh:property ["+System.lineSeparator()+" sh:path "+property+";"+System.lineSeparator()+"");
		switch(operator) {
		case "PropertyIsEqualTo":
			if(value.startsWith("http")) {
				builder.append("sh:equals "+value+";"+System.lineSeparator()+"]"+System.lineSeparator()+"]");
			}else {
				builder.append("sh:equals \""+value+"\";"+System.lineSeparator()+"]"+System.lineSeparator()+"]");
			}
			return builder.toString();
		case "PropertyIsNotEqualTo":
			if(value.startsWith("http")) {
				builder.append("sh:disjoint "+value+";"+System.lineSeparator()+"]"+System.lineSeparator()+"]");
			}else {
				builder.append("sh:disjoint \""+value+"\";"+System.lineSeparator()+"]"+System.lineSeparator()+"]");
			}
			return builder.toString();
		case "PropertyIsLessThan":
			if(value.startsWith("http")) {
				builder.append("sh:lessThan "+value+";"+System.lineSeparator()+"]"+System.lineSeparator()+"]");
			}else {
				builder.append("sh:maxExclusive \""+value+"\";"+System.lineSeparator()+"]"+System.lineSeparator()+"]");
			}
			return builder.toString();
		case "PropertyIsLessThanOrEqualTo":
			if(value.startsWith("http")) {
				builder.append("sh:lessThanOrEquals "+value+";"+System.lineSeparator()+"]"+System.lineSeparator()+"]");
			}else {
				builder.append("sh:maxInclusive \""+value+"\";"+System.lineSeparator()+"]"+System.lineSeparator()+"]");
			}
			return builder.toString();
		case "PropertyIsGreaterThan":
			if(value.startsWith("http"))  {
				builder.append("sh:minExclusive \""+value+"\";"+System.lineSeparator()+"]"+System.lineSeparator()+"]");
			}
			return builder.toString();
		case "PropertyIsGreaterThanOrEqualTo":
			if(value.startsWith("http"))  {
				builder.append("sh:minInclusive \""+value+"\";"+System.lineSeparator()+"]"+System.lineSeparator()+"]");
			}
			return builder.toString();
		case "PropertyIsLike":
			if(value.startsWith("http"))  {
				builder.append("sh:pattern \""+value+"\";"+System.lineSeparator()+"]"+System.lineSeparator()+"]");
			}
			return builder.toString();
		case "PropertyIsNull":
			//Exists Query
			return builder.toString();
		case "PropertyIsBetween":
			break;
		case "Intersects":
			break;
		case "Equals":
			break;
		case "Disjoint":
			break;
		case "Touches":
			break;
		case "Within":
			break;
		case "Overlaps":
			break;
		case "Crosses":
			break;
		case "Contains":
			break;
		case "DWithin":
			break;
		case "Beyond":
			break;
		case "BBOX":
			break;
		}
		return builder.toString();
	}
	
	@Override
	public String toString() {
		return "property: "+property+" condition: "+operator+" value: "+value;
	}
}
