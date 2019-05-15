package org.processmining.Data;

import java.util.ArrayList;
import java.util.TreeSet;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

public class MyLog extends ArrayList<MyTrace> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private TreeSet<MyEvent> myeventset ;
	public MyLog(XLog xlog)
	{
		myeventset = new TreeSet<MyEvent>();
		changeXLogtoMyLog(xlog);
	}
	private void changeXLogtoMyLog(XLog log)
	{
		for(XTrace xtrace:log)
		{
			MyTrace mytrace = new MyTrace();
			for(XEvent xevent:xtrace)
			{
				XAttributeMap attributemap = xevent.getAttributes();
				XAttribute attribute = attributemap.get("concept:name");
				
				MyEvent myevent = new MyEvent(attribute.toString());
				mytrace.add(myevent);
				myeventset.add(myevent);
				
			}
			add(mytrace);
		}
		
	}
	public TreeSet<MyEvent> getMyEventSet()
	{
		return myeventset;
	}
	public MyTrace getFirstTrace()
	{
		return get(0);
	}
	

}
