package org.processmining.FootMatrix;

import java.util.ArrayList;

public class FindRely implements Comparable<FindRely> {
	private String str;
	private int number;
	private ArrayList<String> flow = new ArrayList<>();
	

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	
	public ArrayList<String> getFlow() {
		return flow;
	}

	public void setFlow(ArrayList<String> flow) {
		this.flow = flow;
	}
	
	public void addFlow(String str){
		flow.add(str);
	}

	@Override
	public int compareTo(FindRely o) {
		// TODO Auto-generated method stub
		if (str.compareTo(o.str) != 0) {
			return str.compareTo(o.str);
		} else if (!flow.containsAll(o.flow)||!o.flow.containsAll(flow)) {
			return flow.hashCode() - o.flow.hashCode();
		} else {
			return number - o.number;
		}
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((flow == null) ? 0 : flow.hashCode());
		result = prime * result + number;
		result = prime * result + ((str == null) ? 0 : str.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FindRely other = (FindRely) obj;
		if (flow == null) {
			if (other.flow != null)
				return false;
		} else if (!flow.equals(other.flow))
			return false;
		if (number != other.number)
			return false;
		if (str == null) {
			if (other.str != null)
				return false;
		} else if (!str.equals(other.str))
			return false;
		return true;
	}

	public String toString() {
		return "FindRely [str=" + str + ", number=" + number + ", flow=" + flow + "]";
	}

	
}
