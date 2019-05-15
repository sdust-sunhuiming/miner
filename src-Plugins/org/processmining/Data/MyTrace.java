package org.processmining.Data;

import java.util.ArrayList;

public class MyTrace extends ArrayList<MyEvent> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public MyEvent getFirstEvent()
	{
		return get(0);
	}
	public MyEvent getLastEvent()
	{
		return get(size()-1);
	}
}
