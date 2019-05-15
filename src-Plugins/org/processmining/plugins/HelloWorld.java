package org.processmining.plugins;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.processmining.Data.MyEvent;
import org.processmining.Data.MyLog;
import org.processmining.FootMatrix.FindRely;
import org.processmining.FootMatrix.FootMatrix;
import org.processmining.Gather.Triad;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;
public class HelloWorld {
        @Plugin(
                name = "AlphaTR indirect dependenies miner", 
                parameterLabels = {"Xlog"}, 
                		returnLabels = { "Petrinet" },
            			returnTypes = { Petrinet.class },
                userAccessible = true
        )
        @UITopiaVariant(
                affiliation = "SDUST", 
                author = "Huiming Sun", 
                email = "1296994010@qq.com"
        )
        public Petrinet doMining(PluginContext context,XLog log)
    	{
    		String label = "PetriNet";
    		XLog xlog1;
    		PetrinetImpl pnimpl = new PetrinetImpl(label);
    		//日志转换,获取变迁名
    		MyLog mylog = new MyLog(log);
    		
    		//初始化T
    		InitialTransition(mylog, pnimpl);
    		//生成足迹矩阵
    		FootMatrix footmatrix = CreateRightFootMatrix(mylog, pnimpl);
    		footmatrix.ShowMatrix();
    		FindRely[] hellorely= new FindRely[footmatrix.gettransrely().length];
    		System.arraycopy(footmatrix.gettransrely(), 0, hellorely, 0, footmatrix.gettransrely().length);
    		/*for(int i=0;i<hellorely.length;i++)
    		{
    			System.out.println("hellorely"+i+":"+hellorely[i]);
    		}*/
    		double[] confidencerely =  new double [footmatrix.gettransrely().length];
    		System.arraycopy(footmatrix.getconfidencerely(), 0, confidencerely, 0, footmatrix.gettransrely().length);
    		/*for(int i=0;i<hellorely.length;i++)
    		{
    			System.out.println("helloconfidencerely"+i+":"+confidencerely[i]);
    		}*/
    		//CreateBPPetrinet(pnimpl,footmatrix);
    		CreatePetrinet(pnimpl, footmatrix);
    		Set<String> choicenameset= new HashSet<String>();
    		choicenameset.addAll(footmatrix.getchoicenameset());
    		/*for(Transition t:pnimpl.getTransitions()){
    			for(int i=0;i<confidencerely.length;i++)
    			{
    				if(hellorely[i].getFlow().contains(t.toString())&&confidencerely[i]==1&&choicenameset.contains(hellorely[i].getStr())){
    					t.getAttributeMap().put(AttributeMap.LABEL, hellorely[i].getFlow()+":"+hellorely[i].getFlow()+"("+hellorely[i].getNumber()+")""=>"+hellorely[i].getStr());
    				}
    				if(hellorely[i].getFlow().contains(t.toString())&&confidencerely[i]==1&&!choicenameset.contains(hellorely[i].getStr())){
    					t.getAttributeMap().put(AttributeMap.LABEL, hellorely[i].getFlow()+":"+hellorely[i].getFlow()+"=>"+hellorely[i].getStr()+"("+hellorely[i].getNumber()+")");
    				}
    			
    		    }
    		}*/
    		//CreateBPPetrinet(pnimpl,footmatrix);
    		return pnimpl;
    		
    		
    	}
        private  void InitialTransition(MyLog mylog,PetrinetImpl pnimpl)
    	{
    		//得到所有的事件
    		ArrayList<MyEvent> myeventlist = new ArrayList<MyEvent>(mylog.getMyEventSet());
    		//初始化网
    				for(int i = 0; i < myeventlist.size() ; i ++)
    				{
    					MyEvent myevent = myeventlist.get(i);
    					String name = myevent.getName();
    					pnimpl.addTransition(name);
    				}
    	}
    	
    	private void CreatePetrinet(PetrinetImpl pnimpl,FootMatrix footmatrix)
    	{
    		ArrayList<Triad> casuallist = new ArrayList<Triad>(footmatrix.getDirectCasualSet());
    		ArrayList<Triad> selfcycledirectlist = new ArrayList<Triad>(footmatrix.getselfcycledirectset());
    		//ArrayList<Triad> bplist = new ArrayList<Triad>(footmatrix.getbpset());
    		ArrayList<Place> placelist = new ArrayList<Place>();
    		placelist.add(pnimpl.addPlace("Start"));
    		pnimpl.addArc(placelist.get(0), footmatrix.getFirstTransition());
    	    while(casuallist.size()>0)
    	    {
    	    	Triad triad = casuallist.remove(0);
    	    	int i=0;
    	    	Transition firstt = triad.getFirst();
    	    	Transition secondt = triad.getSecond();
    	    	Place place = pnimpl.addPlace("p"+i++);
    	    	int flag1=0,flag2=0;
    	    	for(int j=0;j<casuallist.size();j++)
    	    	{
    	    		Triad tempcasuall = casuallist.get(j);
    	    		
    	    		if(firstt.equals(tempcasuall.getFirst())&&!secondt.equals(tempcasuall.getSecond()))
    	    		{
    	    			flag1=1;
    	    			Transition bpend=tempcasuall.getSecond();
    	    			
    	    	    	//placelist.add(place);
    	    			casuallist.remove(tempcasuall);
    	    	    	j--;
    	    	    	pnimpl.addArc(place, bpend);
    	    		}
    	    		if(secondt.equals(tempcasuall.getSecond())&&!firstt.equals(tempcasuall.getFirst()))
    	    		{
    	    			flag2=1;
    	    			Transition start=tempcasuall.getFirst();
    	    			
    	    	    	//placelist.add(place);
    	    			casuallist.remove(tempcasuall);
    	    			j--;
    	    	    	pnimpl.addArc(start, place);
    	    	    
    	    		}
    	    		
    	    	//pnimpl.addArc(firstt, place);
    	    	//pnimpl.addArc(place, secondt);
    	    	//Place place = pnimpl.addPlace("p"+i);
    	    	//placelist.add(place);
    	    	}
    	    	for(int k=0;k<selfcycledirectlist.size();k++)
    	    	{
    	    		Triad selfcyclesirecttriad = selfcycledirectlist.get(k);
    	    		if(triad.getFirst().equals(selfcyclesirecttriad.getFirst()))
    	    		{
    	    			pnimpl.addArc(place, selfcyclesirecttriad.getSecond());
    	    		}
    	    		if(triad.getSecond().equals(selfcyclesirecttriad.getSecond()))
    	    		{
    	    			pnimpl.addArc(selfcyclesirecttriad.getFirst(),place);
    	    		}
    	    	
    	    	}
    	    	if(flag1==1&&flag2==1)
    	    	{
    	    		continue;
    	    	}
    	    	else{
    	    	pnimpl.addArc(firstt, place);
    	    	pnimpl.addArc(place, secondt);
    	    	}
    	    	
    	    }
    	   
    	    placelist.add(pnimpl.addPlace("End"));
    	    pnimpl.addArc(footmatrix.getLastTransition(),placelist.get(placelist.size()-1));
    		
    	}
    	
    	private  FootMatrix CreateRightFootMatrix(MyLog mylog,PetrinetImpl pnimpl)
    	{
    		//先生成足迹矩阵
    		FootMatrix footmatrix = new FootMatrix(mylog, pnimpl);
    		footmatrix.ShowMatrix();
    		return footmatrix;
    	}
    }