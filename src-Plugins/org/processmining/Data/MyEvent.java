package org.processmining.Data;


public class MyEvent implements Comparable<MyEvent> {
	private String name;
	public MyEvent()
	{
		name = new String();
		name = "";
	}
	public MyEvent(String n)
	{
		name = new String();
		name = n;
	}
	public String setName(String n)
	{
		name = n;
		return this.getName();
	}
	public String getName()
	{
		return name;
	}
	public int hashCode()
	{
		return name.hashCode();
		
	}
	public boolean equals(Object obj)
	{
		boolean bool = false;
	   if (obj instanceof MyEvent)
	   {
		   MyEvent new_name = (MyEvent) obj;
		   if(new_name.getName().equals(name))
		   {
			   bool =true;
		   }
		}
		return bool;
	}
    public String toString()
    {
    	return this.getName();
    }
	@Override
	public int compareTo(MyEvent o) {
		// TODO Auto-generated method stub
		int size = name.hashCode() - o.getName().hashCode();
		return size;
	}
}
