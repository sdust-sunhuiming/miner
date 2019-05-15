package org.processmining.Gather;

import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

public class Tuple {
	
	private Transition firstTrans;
	private Transition secondTrans;
	public Tuple tp;
	String firstt;
	int tracetotal=0;
	int logtotal=0;
	public Tuple(Transition t1,Transition t2)
	{
		firstTrans=t1;
		secondTrans=t2;
	}
	public Tuple(String t1,int ttotal,int ltotal)
	{
		firstt=t1;
		tracetotal=ttotal;
		logtotal=ltotal;
	}
	public Tuple(Transition t1,Tuple t2)
	{
		firstTrans=t1;
		tp=t2;
	}
	public Transition getFirst()
	{
		return firstTrans;
	}
	public String getFirstString()
	{
		return firstt;
	}
	public int gettracetotal()
	{
		return tracetotal;
	}
	public void settracetotal(int tem)
	{
		tracetotal=tem;
	}
	public int getlogtotal()
	{
		return logtotal;
	}
	public void setlogtotal(int logtot)
	{
		logtotal=logtot;
	}
	public Transition getSecond()
	{
		return secondTrans;
	}
	public int hashCode()
	{
		int i ;
		i = firstTrans.toString().hashCode()*100 + secondTrans.toString().hashCode();
		return i;
	}
	public boolean equals(Object obj)
	{
		if(obj instanceof Tuple)
		{
			Tuple t = (Tuple) obj;
			if(t.getFirst().toString().equals(firstTrans.toString())&&t.getSecond().toString().equals(secondTrans.toString()))
			{
				return true;
			}
		}
		return false;
	}
	public String toString()
	{
		return "("+firstTrans.toString()+","+secondTrans.toString()+")";
	}

}
