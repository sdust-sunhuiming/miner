package org.processmining.Relation;

public enum Relation {
	DirectFollow,
	DirectCasually,
	Parallel,NoRel,
	reDirectFollow,
	reDirectCasually,IBpJ,JBpI,
	reParallel,reNoRel,
	Selfcycle,reSelfcycle	;
public String toString()
	{
		String symbale = new String();
		symbale = "";
		switch(this)
		{
		   case DirectFollow:
			   symbale = " >";
			   break;
		   case DirectCasually:
			   symbale = "->";
			   break;
		   case Parallel:
			   symbale = "||";
			   break;
		   case IBpJ:
			   symbale = "¡÷";
			   break;
		   case reDirectFollow:
			   symbale = " <";
			   break;
		  case reDirectCasually:
			   symbale = "<-";
			   break;
		   case reParallel:
			   symbale = "||";
			   break;
		   case JBpI:
			   symbale = "¡÷";
			   break;
		   case Selfcycle:
			   symbale = "@";
			   break;
		   case reSelfcycle:
			   symbale = "@";
			   break;
		   default:
			   symbale = " #";
			   break;
		}
		return symbale;
	}
	public Relation getReRelation()
	{
		Relation symbale;
		switch(this)
		{
		   case DirectFollow:
			   symbale = Relation.reDirectFollow;
			   break;
		   case IBpJ:
			   symbale = Relation.JBpI;
			   break;
		   case JBpI:
			   symbale = Relation.IBpJ;
			   break;
		  case DirectCasually:
			   symbale = Relation.reDirectCasually;
			   break;
		   case Parallel:
			   symbale = Relation.reParallel;
			   break;
		   case reDirectFollow:
			   symbale = Relation.DirectFollow;
			   break;
		   case reDirectCasually:
			   symbale = Relation.DirectCasually;
			   break;
		   case reParallel:
			   symbale = Relation.Parallel;
			   break;
		   case reSelfcycle:
			   symbale = Relation.Selfcycle;
			   break;
		   case Selfcycle:
			   symbale = Relation.reSelfcycle;
			   break;
		   default:
			   symbale = Relation.NoRel;
			   break;
		}
		return symbale;
	}
}
