package org.processmining.Gather;

import org.processmining.Relation.Relation;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

public class Triad {
    private Tuple tuple;
	private Relation relation;
	public Triad(Tuple t,Relation r)
	{
		tuple = t;
		relation = r;
	}
	public Tuple getTuple()
	{
		return tuple;
	}
	public Relation getRelation()
	{
		return relation;
	}
	public Transition getFirst()
	{
		return tuple.getFirst();
	}
	public Transition getSecond()
	{
		return tuple.getSecond();
	}
	public int hashCode()
	{
		return tuple.hashCode();
	}
	public boolean equals(Object obj)
	{
		if(obj instanceof Triad)
		{
			Triad triad = (Triad) obj;
			if(triad.getTuple().equals(tuple)&&triad.getRelation().equals(relation))
			{
				return true;
			}
		}
		return false;
	}
	public String toString()
	{
		return "("+tuple.getFirst().toString()+relation.toString()+tuple.getSecond().toString()+")" ;
	}

}
