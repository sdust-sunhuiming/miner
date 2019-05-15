package org.processmining.FootMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.processmining.Data.MyEvent;
import org.processmining.Data.MyLog;
import org.processmining.Data.MyTrace;
import org.processmining.Gather.Triad;
import org.processmining.Gather.Tuple;
import org.processmining.Relation.Relation;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;

public class FootMatrix {
	private Map<String, Transition> StoTmap;
	private Map<Integer, Transition> ItoTmap;
	private Map<Transition, Integer> TtoImap;
	private MyLog mylog;
	private PetrinetImpl pnimpl;
	private Set<Triad> allrelationset;
	private Set<Triad> directfollowset;
	//Transition g,h,c,f;
	//private Set<Triad> indirectfollowset;
	private Set<Triad> directcasualset;
	private Set<Triad> selfcycledirectset;
	private Set<Triad> selfcycleset;
	public Set<Triad> backpacksset;
	private Set<Triad> norelset;
	public ArrayList<Tuple> selfcycledirectlist = new ArrayList<Tuple>();
	public ArrayList<Tuple> selfcyclelist = new ArrayList<Tuple>();
	//private Set<Triad> bpset;
	private Set<Triad> parallelset;
	public Set<String> allelementset = new HashSet<String>();//取出所有元素
	public Set<String> copycycleset = new HashSet<String>();//循环元素副本
	public Set<String> cycleset = new HashSet<String>();//取出所有循环元素
	public Set<String> choicenameset = new HashSet<String>();
	public Set<String> cyclenameset = new HashSet<String>();//获取的所有循环命名集合
	public Set<String> choiceset = new HashSet<String>();//取出所有选择元素
	public Set<String> traceelementset = new HashSet<String>();//取出迹中活动集合
	public Set<String> cychset = new HashSet<String>();//循环选择活动集合
	public Set allset = new HashSet();
	public Set<String> copycyclenameset = new HashSet<String>();//获取的所有循环命名集合

	public Set<String> ceclestrset = new HashSet<String>();
	public Set<Tuple> alltupleset = new HashSet<Tuple>();
	private Relation[][] MatrixofRelation;
	private String[][] MatrixtoString;
	public double[] supportrely;
	public double[] confidencerely;
	public FindRely[] transrely;

	public FootMatrix(MyLog log, PetrinetImpl pn) {
		mylog = log;
		pnimpl = pn;
		StoTmap = new HashMap<String, Transition>();
		TtoImap = new HashMap<Transition, Integer>();
		ItoTmap = new HashMap<Integer, Transition>();
		setMap();
		initial();
		createBaseRelation();
		createRelation();
		createMatrix();
	}

	private void setMap() {
		ArrayList<Transition> translist = new ArrayList<Transition>(pnimpl.getTransitions());
		for (int i = 0; i < translist.size(); i++) {
			Transition t = translist.get(i);
			StoTmap.put(t.toString(), t);
			TtoImap.put(t, i);
			ItoTmap.put(i, t);
		}
	}

	private void initial() {
		allrelationset = new HashSet<Triad>();
		directfollowset = new HashSet<Triad>();
		//indirectfollowset = new HashSet<Triad>();
		directcasualset = new HashSet<Triad>();
		backpacksset = new HashSet<Triad>();
		//indirectcasualset = new HashSet<Triad>();
		parallelset = new HashSet<Triad>();
		norelset = new HashSet<Triad>();
		selfcycleset = new HashSet<Triad>();
		selfcycledirectset = new HashSet<Triad>();
		//bpset= new HashSet<Triad>();
		int size = StoTmap.size();
		MatrixofRelation = new Relation[size][size];
		MatrixtoString = new String[size + 1][size + 1];
		for (int i = 0; i < MatrixofRelation.length; i++) {
			for (int j = 0; j < MatrixofRelation.length; j++) {
				MatrixofRelation[i][j] = Relation.NoRel;
			}
		}
		MatrixtoString[0][0] = " ";
	}

	private void createBaseRelation() {
		Set<Tuple> dfset = new HashSet<Tuple>();//直接跟随集合
		//Set<Tuple> idfset = new HashSet<Tuple>();
		for (int i = 0; i < mylog.size(); i++) {
			MyTrace trace = mylog.get(i);
			for (int j = 0; j < trace.size() - 1; j++) {
				MyEvent event = trace.get(j);
				String eventstr = event.getName();
				Transition transition = StoTmap.get(eventstr);

				MyEvent event2 = trace.get(j + 1);
				String eventstr2 = event2.getName();
				Transition transition2 = StoTmap.get(eventstr2);
				//transition2.getAttributeMap().put(AttributeMap.LABEL, "newName0");
				Tuple tuple = new Tuple(transition, transition2);
				dfset.add(tuple);
			}
		}

		Iterator<Tuple> dirit = dfset.iterator();
		while (dirit.hasNext()) {
			Tuple t = (Tuple) dirit.next();
			Triad triad = new Triad(t, Relation.DirectFollow);
			directfollowset.add(triad);
		}
		System.out.println("直接跟随关系:" + directfollowset);
	}

	private void createRelation() {
		Set<Tuple> alltuple = new HashSet<Tuple>();
		Set<Tuple> dirtuple = new HashSet<Tuple>();

		ArrayList<Triad> dirlist = new ArrayList<Triad>(directfollowset);

		for (int i = 0; i < dirlist.size(); i++) {
			dirtuple.add(dirlist.get(i).getTuple());
		}
		alltuple.addAll(dirtuple);
		//生成parallel关系
		Set<Tuple> parttuple = new HashSet<Tuple>();
		Set<Tuple> paralleltuple = new HashSet<Tuple>();
		Tuple tuple = null;
		Tuple retuple = null;
		ArrayList<Tuple> alltuplelist = new ArrayList<Tuple>(alltuple);
		ArrayList<Tuple> alltuplelist1 = new ArrayList<Tuple>(alltuple);
		for (Tuple a : alltuplelist) {
			System.out.println("tuplelisttest:" + a);
		}
		while (alltuplelist.size() > 0) {

			tuple = alltuplelist.remove(0);

			allelementset.add(tuple.getFirst().toString());//把元素添加到所有集合中
			allelementset.add(tuple.getSecond().toString());

			retuple = new Tuple(tuple.getSecond(), tuple.getFirst());

			System.out.println();
			if (alltuplelist.contains(retuple)) {
				alltuplelist.remove(retuple);
				paralleltuple.add(tuple);
				System.out.println("tuple." + tuple.getFirst().toString());
			} else {
				parttuple.add(tuple);
			}
		}
		/* 把所有元素集合转化为数组操作 */String[] allelementarr = new String[allelementset.size()];
		allelementset.toArray(allelementarr);
		System.out.println("allelementset:" + allelementset);
		for (int i = 0; i < allelementarr.length; i++) {
			System.out.printf(allelementarr[i] + "  ");
		}
		String[] tracestr = new String[mylog.size()];

		for (int i = 0; i < mylog.size(); i++) {
			MyTrace temptrace = mylog.get(i);
			String str = null;
			for (int j = 0; j < temptrace.size(); j++) {
				if(j==0)
				{str=temptrace.get(j).toString();}
				else{
				str = str + temptrace.get(j).toString();
				}
			}
			tracestr[i] = str;
		}
		for (int i = 0; i < mylog.size(); i++) {
			System.out.println("tracestr[" + i + "]" + tracestr[i]);
		}
		int[] logtotal = new int[allelementarr.length];//日志元素总计矩阵
		int[][] tracetotal = new int[mylog.size()][allelementarr.length];//轨迹元素计数矩阵
		for (int i = 0; i < allelementarr.length; i++) {
			logtotal[i] = 0;
		} //初始化日志活动统计矩阵。
		for (int i = 0; i < mylog.size(); i++) {
			for (int j = 0; j < allelementarr.length; j++) {
				tracetotal[i][j] = 0;
			}
		} //初始化迹活动统计矩阵
		Set<String> termchoiceset = new HashSet<String>();

		for (int i = 0; i < mylog.size(); i++) {
			MyTrace testtrace = mylog.get(i);
			traceelementset.clear();
			for (int j = 0; j < testtrace.size(); j++) {
				MyEvent testeventtotal = testtrace.get(j);
				traceelementset.add(testtrace.get(j).toString());
				System.out.println("testtrace.get(j)" + testtrace.get(j).toString());
				for (int k = 0; k < allelementarr.length; k++) {
					if (testeventtotal.toString().equals(allelementarr[k].toString())) {
						if (tracetotal[i][k] != 0) {
							cycleset.add(allelementarr[k].toString());
						}
						tracetotal[i][k] = tracetotal[i][k] + 1;
					}
				}

			}
			System.out.println("traceelementset" + traceelementset);
			System.out.println("allelementset" + allelementset);
			termchoiceset.addAll(allelementset);
			termchoiceset.removeAll(traceelementset);
			choiceset.addAll(termchoiceset);
			System.out.println("choiceset0:" + choiceset);
		} //统计迹中的活动出现次数
		System.out.println("choiceset1:" + choiceset);
		System.out.println("+++++++++cycleset:+++++++++" + cycleset);
		for (int i = 0; i < allelementarr.length; i++) {
			for (int j = 0; j < mylog.size(); j++) {
				logtotal[i] += tracetotal[j][i];
			}
		} //统计日志活动出现次数
		for (int i = 0; i < allelementarr.length; i++) {
			if (logtotal[i] < mylog.size()) {
				choiceset.add(allelementarr[i]);
			}
			System.out.printf("%d  ", logtotal[i]);
		}
		choiceset.removeAll(cycleset);
		System.out.println("choicesetfinal" + choiceset);
		System.out.println("*******logtotal********,\n");
		for (int i = 0; i < mylog.size(); i++) {
			for (int j = 0; j < allelementarr.length; j++) {
				System.out.printf("%d ", tracetotal[i][j]);
			}
			System.out.println("\n");
		}
		//证明定理：所有循环中的活动至少能找到一条回路。
		Tuple temptuple = null;
		Tuple endtetuple = null;
		String[] cyclesettoarr = new String[cycleset.size()];
		int sl=0;
		TT:for(int i=0;i<mylog.size();i++)
		{
			MyTrace a = mylog.get(i);
			for(int j=0;j<a.size();j++)
			{
				if(cycleset.contains(a.get(j).toString())&&sl<cycleset.size())
				{
					cyclesettoarr[sl++]=a.get(j).toString();
					
				}
			}
		}
		cychset.addAll(choiceset);
		cychset.addAll(cycleset);
		//cycleset.toArray(cyclesettoarr);
		for(int i=0;i<cycleset.size();i++){
			System.out.println("new cyclesettoarr"+cyclesettoarr[i]);
		}
		for (int i = 0; i < cyclesettoarr.length; i++) {
			String tempstr = cyclesettoarr[i];
			String findstr = null;
			int flag = 0;//找到一条回路就停止的标记
			int flag1 = 0;
			System.out.println("tempstr:" + tempstr);
			System.out.println("alltuplelist1.size:" + alltuplelist1.size());
			for (int j = 0; j < alltuplelist1.size() && flag == 0; j++) {
				System.out.println("进入第一层循环");
				if (cyclesettoarr[i].equals(alltuplelist1.get(j).getFirst().toString())
						&& cychset.contains(alltuplelist1.get(j).getSecond().toString())) {
					temptuple = alltuplelist1.get(j);
					if (flag1 == 0) {
						tempstr = tempstr + alltuplelist1.get(j).getSecond().toString();
					} else {
						tempstr = tempstr + "|" + alltuplelist1.get(j).getSecond().toString();
					}
					System.out.println("temptuple1:" + temptuple);
					flag1++;
				}
				if (cyclesettoarr[i].equals(alltuplelist1.get(j).getSecond().toString())) {
					endtetuple = alltuplelist1.get(j);
					System.out.println("endtetuple1:" + endtetuple);
				}
			} //找到该二元组。
				//findstr=fdcycle(temptuple,endtetuple,alltuplelist1,tempstr);

			aa: for (int m = 0; m < alltuplelist1.size() && flag == 0; m++) {
				int flag2 = 0;
				for (int k = 0; k < alltuplelist1.size(); k++) {
					System.out.println("进入第2层循环");
					if (temptuple.getSecond().equals(temptuple.getFirst())) {
						ceclestrset.add(tempstr);
						break aa;
					}
					if ((temptuple.getSecond().equals(endtetuple.getFirst())
							&& endtetuple.getSecond().equals(temptuple.getFirst()))) {
						tempstr = tempstr + endtetuple.getSecond().toString();
						ceclestrset.add(tempstr);
						System.out.println("此时他俩相等");
						flag = 1;
						break;
					}
					if (temptuple.getSecond().toString().equals(alltuplelist1.get(k).getFirst().toString())
							&& cychset.contains(alltuplelist1.get(k).getSecond().toString())) {

						tempstr = tempstr + alltuplelist1.get(k).getSecond().toString();
						System.out.println("tempstr2:" + tempstr);
						System.out.println("alltuplelist[]k" + alltuplelist1.get(k));
						temptuple = new Tuple(temptuple.getFirst(), alltuplelist1.get(k).getSecond());
						System.out.println("temptuple2:" + temptuple);
						flag2++;
						break;
					}
				}
			}
		}
		System.out.println("ceclestrser:" + ceclestrset);
		copycycleset.addAll(cycleset);
		//找到一组循环并且进行命名
		for(int i=0;i<cyclesettoarr.length;i++)
		{
			System.out.println("寻找===="+cyclesettoarr[i]);
		}
		System.out.println("xunzhao++++"+copycycleset);
		for (int i = 0; i < cyclesettoarr.length && copycycleset.size() > 0 ; i++) {
            if(copycycleset.contains(cyclesettoarr[i])){
			String str = cyclesettoarr[i];
			String str1 = cyclesettoarr[i];
			String str2 = cyclesettoarr[i];
			copycycleset.remove(str);
			System.out.println("str+" + str);
			for (int m = 0; m < alltuplelist1.size(); m++) {

				for (int k = 0; k < alltuplelist1.size(); k++) {
					if (alltuplelist1.get(k).getFirst().toString().equals(str2)
							&& copycycleset.contains(alltuplelist1.get(k).getSecond().toString())) {
						str = str + alltuplelist1.get(k).getSecond().toString();
						str2 = alltuplelist1.get(k).getSecond().toString();
						copycycleset.remove(alltuplelist1.get(k).getSecond().toString());
						System.out.println("str1+" + str + "str2" + str2);
					}
					if (alltuplelist1.get(k).getSecond().toString().equals(str1)
							&& copycycleset.contains(alltuplelist1.get(k).getFirst().toString())) {
						str = alltuplelist1.get(k).getFirst().toString() + str;
						str1 = alltuplelist1.get(k).getFirst().toString();
						copycycleset.remove(alltuplelist1.get(k).getFirst().toString());
						System.out.println("str2+" + str);
					}
				}
			}
			System.out.println("str3" + str);
			cyclenameset.add(str);
			System.out.println("xunzhao-----"+copycycleset);
            }
		}
		copycyclenameset.clear();
		copycyclenameset.addAll(cyclenameset);
		System.out.println("+++++++++++cyclenameset:" + cyclenameset);
		//找到选择名
		Set<String> copychoiceset = new HashSet<String>();
		copychoiceset.addAll(choiceset);
		String[] choicesettoarr = new String[choiceset.size()];
		choiceset.toArray(choicesettoarr);
		for (int i = 0; i < choicesettoarr.length && copychoiceset.size() > 0&&copychoiceset.contains(choicesettoarr[i]); i++) {

			String str = choicesettoarr[i];
			String str1 = choicesettoarr[i];
			String str2 = choicesettoarr[i];
			copychoiceset.remove(str);
			System.out.println("str+" + str);
			for (int m = 0; m < alltuplelist1.size(); m++) {

				for (int k = 0; k < alltuplelist1.size(); k++) {
					if (alltuplelist1.get(k).getFirst().toString().equals(str2)
							&& copychoiceset.contains(alltuplelist1.get(k).getSecond().toString())) {
						copychoiceset.remove(alltuplelist1.get(k).getSecond().toString());
						str2 = alltuplelist1.get(k).getSecond().toString();
						copychoiceset.remove(alltuplelist1.get(k).getSecond().toString());
						System.out.println("choicestr1+" + str + "choicestr2" + str2);
						System.out.println("一层删除copy"+copychoiceset);
					}
					if (alltuplelist1.get(k).getSecond().toString().equals(str1)
							&& copychoiceset.contains(alltuplelist1.get(k).getFirst().toString())) {
						copychoiceset.remove(alltuplelist1.get(k).getFirst().toString());
						str1 = alltuplelist1.get(k).getFirst().toString();
						copychoiceset.remove(alltuplelist1.get(k).getFirst().toString());
						System.out.println("choicestr2+" + str);
						System.out.println("第二层str1"+str1);
					}
				}
			}
			System.out.println("choicestr3" + str);
			choicenameset.add(str);
		}
		choicenameset.addAll(copychoiceset);
		System.out.println("choicenameset:" + choicenameset);
		System.out.println("****copychoicenameset:" + copychoiceset);
		//将循环名集合转化为数组
		String[] choicenamesettoarr = new String[choicenameset.size()];
		choicenameset.toArray(choicenamesettoarr);
		String[] cyclenamesettoarr = new String[cyclenameset.size()];
		cyclenameset.toArray(cyclenamesettoarr);
		int[] cyclemaxarr = new int[cyclenamesettoarr.length];
		int[] cycleelementmax =new int[cycleset.size()];
		for (int i = 0; i < cyclemaxarr.length; i++) {
			cyclemaxarr[i] = 0;
		}
		for(int i=0;i<cycleelementmax.length;i++)
		{
			cycleelementmax[i]=0;
		}
		for(int i=0;i<cyclesettoarr.length;i++)
		{
			String temp=cyclesettoarr[i];
			int max = 0;
			for (int j = 0; j < mylog.size(); j++){
				int fromIndex = 0;
				int count = 0;
				while (true) {
					int index = tracestr[j].indexOf(temp, fromIndex);
					if (-1 != index) {
						fromIndex = index + 1;
						count++;
					} else {
						break;
					}
				}
				if (count > max) {
					max = count;
				}
			}
			cycleelementmax[i]=max;
		}
		for(int i=0; i < cyclenamesettoarr.length; i++)
		{
			for(int j=0;j<cycleelementmax.length;j++)
			{
				cyclemaxarr[i]=cycleelementmax[0];
				if(cyclenamesettoarr[i].contains(cyclesettoarr[j])&&cycleelementmax[j]<cyclemaxarr[i])
				{
					cyclemaxarr[i]=cycleelementmax[j];
				}
			}
		}
		/*for (int i = 0; i < cyclenamesettoarr.length; i++) {
			int max = 0;
			for (int j = 0; j < mylog.size(); j++) {
				int fromIndex = 0;
				int count = 0;
				while (true) {
					int index = tracestr[j].indexOf(cyclenamesettoarr[i], fromIndex);
					if (-1 != index) {
						fromIndex = index + 1;
						count++;
					} else {
						break;
					}
				}
				if (count > max) {
					max = count;
				}

			}
				
			cyclemaxarr[i] = max;
		}*/
		for (int i = 0; i < cyclemaxarr.length; i++) {
			System.out.println("最后" + cyclemaxarr[i]);
		}
		/*cyclenameset.clear();
		for (int i = 0; i < cyclenamesettoarr.length; i++) {
			cyclenameset.add(cyclenamesettoarr[i] + ":0");
		}
		for (int i = 0; i < cyclenamesettoarr.length; i++) {
			//System.out.println(cyclemaxarr[i]);
			for (int j = 0; j < cyclemaxarr[i]; j++) {
				cyclenameset.add(cyclenamesettoarr[i] + ":" + (j + 1));
			}
		}
		System.out.println("带有循环次数的循环名集合" + cyclenameset);*/
		int total = 0;

		//切分trace
		String[] cyclenumbersettoarr = new String[cyclenameset.size()];
		cyclenameset.toArray(cyclenumbersettoarr);
		total = cyclenameset.size();
		Set<FindRely> rely = new HashSet();
		Set<FindRely> copyrely = new HashSet();
		//寻找循环的就近选择依赖
		System.out.println("寻找循环影响的选择++++++++++++++++++++++++++");

		for (int i = 0; i < cyclenamesettoarr.length; i++) {
			
			System.out.println("进入第1层");
			for (int j = 0; j < tracestr.length; j++) {
				if (!tracestr[j].contains(cyclenamesettoarr[i])) {
					continue;
				}
				System.out.println("进入第2层");
				String str = cyclenamesettoarr[i];
				String[] cut = tracestr[j].split(/* "("+ */cyclenamesettoarr[i]/* +")+" */);
				int pos = 0;
				for (int s = 1; s < cut.length; s++) {
					if (cut[s] == null) {
						break;
					}
					System.out.println("cut[" + s + "]:" + cut[s]);
					FindRely re = new FindRely();
					re.setStr(str);
					re.setNumber(++pos);
					for (int m = 0; m < cut[s].length(); m++) {
						System.out.println("cut[s].substring(m)" + cut[s].substring(m, m + 1));
						if (choicenameset.contains(cut[s].substring(m, m + 1))) {
							//System.out.println("p" + p);
							System.out.println("包含");
							re.addFlow(String.valueOf(cut[s].charAt(m)));
							break;
						} else {
							System.out.println("不包含");
						}
					}
					if (!re.getFlow().isEmpty()) {
						System.out.println("添加这个类");
						rely.add(re);
					}
				}

			}
		}
		//寻找选择的就近依赖
		System.out.println("寻找选择影响的选择+++++++++++++++++++++++++++++");
		for (int i = 0; i < choicenamesettoarr.length; i++) {
			int p = 0;
			System.out.println("xuanze进入第1层");
			for (int j = 0; j < tracestr.length; j++) {
				if (!tracestr[j].contains(choicenamesettoarr[i])) {
					continue;
				}
				System.out.println("xuanze进入第2层");
				String str = choicenamesettoarr[i];
				String[] cut = tracestr[j].split(/* "("+ */choicenamesettoarr[i]/* +")+" */);
				for (int s = 1; s < cut.length; s++) {
					if (cut[s] == null) {
						break;
					}
					System.out.println("cut[" + s + "]:" + cut[s]);
					FindRely re = new FindRely();
					re.setStr(str);
					re.setNumber(0);
					for (int m = 0; m < cut[s].length(); m++) {
						System.out.println("cut[s].substring(m)" + cut[s].substring(m, m + 1));
						System.out.println("xuanze  choicenameset"+choicenameset);
						System.out.println("xuanze  cut[s].substring(m, m + 1)"+cut[s].substring(m, m + 1));
						if (choicenameset.contains(cut[s].substring(m, m + 1))) {
							System.out.println("p" + p);
							System.out.println("xuanze包含");
							re.addFlow(String.valueOf(cut[s].charAt(m)));
							break;
						} else {
							System.out.println("xuanze不包含");
						}
					}
					if (!re.getFlow().isEmpty()) {
						System.out.println("xuanze添加这个类");
						rely.add(re);
					}
				}

			}
		}
		//寻找选择影响的循环结构
		System.out.println("寻找选择影响的循环");
		for (int i = 0; i < choicenamesettoarr.length; i++) {
			int p = 0;
			System.out.println("进入第1层");
			for (int j = 0; j < tracestr.length; j++) {
				if (!tracestr[j].contains(choicenamesettoarr[i])) {
					continue;
				}
				System.out.println("进入第2层");
				String str = choicenamesettoarr[i];
				System.out.println("xunhuanyuingxiang xuanze str  "+str);
				String[] cut = tracestr[j].split(/* "("+ */choicenamesettoarr[i]/* +")+" */);
				for (int s = 1; s < cut.length; s++) {
					if (cut[s] == null) {
						break;
					}
					System.out.println("cut[" + s + "]:" + cut[s]);
					FindRely re = new FindRely();
					re.setStr(str);
					//re.setNumber(0);
					for (int m = 0; m < cyclenamesettoarr.length; m++) {
						String secondcut[]= cut[s].split(/* "("+ */cyclenamesettoarr[m]/* +")+" */);
						if (secondcut.length<2){
							System.out.println("不包含");
							continue;
						} else {
							re.setNumber(secondcut.length-1);
							re.addFlow(String.valueOf(cyclenamesettoarr[m]));
							rely.add(re);
							break;
						}
					}
				}

			}
		}
		/*copyrely.addAll(rely);
		for (FindRely Rely1 : rely) {
			System.out.println(Rely1);
			System.out.println("rely" + Rely1.getStr());
			System.out.println("rely" + Rely1.getNumber());
			System.out.println("rely" + Rely1.getFlow());
		}
		for(FindRely rely2 : copyrely)
		{
			System.out.println("rely2"+rely2.getFlow());
			for(FindRely rely3 : copyrely)
			{
				System.out.println("rely3"+rely3.getFlow());
				if(rely2.getStr().equals(rely3.getStr())&&rely2.getNumber()==rely3.getNumber()&&!rely2.getFlow().equals(rely3.getFlow())&&rely2.getFlow().containsAll(rely3.getFlow()))
				{
					rely.remove(rely3);
				}
				if(rely2.getStr().equals(rely3.getStr())&&rely2.getNumber()==rely3.getNumber()&&!rely2.getFlow().equals(rely3.getFlow())&&rely3.getFlow().contains(rely2.getFlow()))
				{
					rely.remove(rely2);
				}
				if(rely3.getFlow().containsAll(rely2.getFlow())&&copycyclenameset.contains(rely3.getStr())&&choicenameset.contains(rely2.getStr()))
				{
					rely.remove(rely2);
				}
			}
		}
		for (FindRely Rely4 : rely) {
			System.out.println("f"+Rely4);
			
		}*/
		FindRely[] relytoarr = new FindRely[rely.size()];
		rely.toArray(relytoarr);
		int relycount[]  = new int [rely.size()];
		for(int k=0;k<rely.size();k++ )
		{
			relycount[k]=0;
		}
		//统计跟随的数量
for (int i = 0; i < cyclenamesettoarr.length; i++) {
			
			System.out.println("统计循环进入第1层");
			for (int j = 0; j < tracestr.length; j++) {
				if (!tracestr[j].contains(cyclenamesettoarr[i])) {
					continue;
				}
				System.out.println("统计循环进入第2层");
				String str = cyclenamesettoarr[i];
				String[] cut = tracestr[j].split(/* "("+ */cyclenamesettoarr[i]/* +")+" */);
				int pos = 0;
				for (int s = 1; s < cut.length; s++) {
					if (cut[s] == null) {
						break;
					}
					System.out.println("统计循环cut[" + s + "]:" + cut[s]);
					FindRely re = new FindRely();
					re.setStr(str);
					re.setNumber(++pos);
					for (int m = 0; m < cut[s].length(); m++) {
						System.out.println("统计循环cut[s].substring(m)" + cut[s].substring(m, m + 1));
						if (choicenameset.contains(cut[s].substring(m, m + 1))) {
							//System.out.println("p" + p);
							System.out.println("统计循环包含");
							re.addFlow(String.valueOf(cut[s].charAt(m)));
							break;
						} else {
							System.out.println("统计循环不包含");
						}
					}
					if (!re.getFlow().isEmpty()) {
						for(int k=0;k<rely.size();k++)
						{
							if(re.equals(relytoarr[k]))
							{
								relycount[k]++;
							}
						}
					}
				}

			}
		}
		//寻找选择的就近依赖
		for (int i = 0; i < choicenamesettoarr.length; i++) {
			int p = 0;
			System.out.println("进入第1层统计");
			for (int j = 0; j < tracestr.length; j++) {
				if (!tracestr[j].contains(choicenamesettoarr[i])) {
					continue;
				}
				System.out.println("进入第2层统计");
				String str = choicenamesettoarr[i];
				String[] cut = tracestr[j].split(/* "("+ */choicenamesettoarr[i]/* +")+" */);
				for (int s = 1; s < cut.length; s++) {
					if (cut[s] == null) {
						break;
					}
					System.out.println("统计cut[" + s + "]:" + cut[s]);
					FindRely re = new FindRely();
					re.setStr(str);
					re.setNumber(0);
					for (int m = 0; m < cut[s].length(); m++) {
						System.out.println("统计cut[s].substring(m)" + cut[s].substring(m, m + 1));
						if (choicenameset.contains(cut[s].substring(m, m + 1))) {
							System.out.println("p" + p);
							System.out.println("统计包含");
							re.addFlow(String.valueOf(cut[s].charAt(m)));
							break;
						} else {
							System.out.println("统计不包含");
						}
					}
					if (!re.getFlow().isEmpty()) {
						System.out.println("统计添加这个类");
						for(int k=0;k<rely.size();k++)
						{
							if(re.equals(relytoarr[k]))
							{
								relycount[k]++;
							}
						}
					}
				}

			}
		}
		//统计选择影响循环次数的依赖个数
		for (int i = 0; i < choicenamesettoarr.length; i++) {
			int p = 0;
			System.out.println("进入第1层");
			for (int j = 0; j < tracestr.length; j++) {
				if (!tracestr[j].contains(choicenamesettoarr[i])) {
					continue;
				}
				System.out.println("进入第2层");
				String str = choicenamesettoarr[i];
				String[] cut = tracestr[j].split(/* "("+ */choicenamesettoarr[i]/* +")+" */);
				for (int s = 1; s < cut.length; s++) {
					if (cut[s] == null) {
						break;
					}
					System.out.println("cut[" + s + "]:" + cut[s]);
					FindRely re = new FindRely();
					re.setStr(str);
					//re.setNumber(0);
					for (int m = 0; m < cyclenamesettoarr.length; m++) {
						String secondcut[]= cut[s].split(/* "("+ */cyclenamesettoarr[m]/* +")+" */);
						if (secondcut.length<2){
							System.out.println("不包含");
							continue;
						} else {
							re.setNumber(secondcut.length-1);
							re.addFlow(String.valueOf(cyclenamesettoarr[m]));
							//rely.add(re);
						}
					}
					for(int k=0;k<rely.size();k++)
					{
						if(re.equals(relytoarr[k]))
						{
							relycount[k]++;
						}
					}
				}

			}
		}
		supportrely = new double [relytoarr.length];
		confidencerely = new double [relytoarr.length];
		transrely=new FindRely[rely.size()];
		System.arraycopy(relytoarr, 0, transrely, 0, relytoarr.length);
		for(int i=0;i<relytoarr.length;i++)
		{
			System.out.println("transrely"+i+":"+transrely[i]);
		}
		System.out.println("mylog.size"+mylog.size());
		for(int i=0;i<relytoarr.length;i++)
		{
			System.out.println("relycount"+relycount[i]);
			supportrely[i]=(double)relycount[i]/(double)mylog.size();
		}
		for(int i=0;i<relytoarr.length;i++)
		{
			int count=0;
			for(int j=0;j<relytoarr.length;j++)
			{
				if(relytoarr[i].getStr().equals(relytoarr[j].getStr())&&choicenameset.contains(relytoarr[i].getStr()))
				{
					count+=relycount[j];
				}
				if(relytoarr[i].getStr().equals(relytoarr[j].getStr())&&cyclenameset.contains(relytoarr[i].getStr())&&relytoarr[i].getNumber()==relytoarr[j].getNumber())
				{
					count+=relycount[j];
				}
			}
			System.out.println("count"+count);
			System.out.println("relycount"+relycount[i]);
			confidencerely[i]=(double)relycount[i]/(double)count;
		}
		for(int k=0;k<rely.size();k++)
		{
			System.out.println("final"+relytoarr[k]+":"+relycount[k]+"supportrely:"+supportrely[k]+"confidencerely:"+confidencerely[k]);
		}
		for(int k=0;k<rely.size();k++)
		{
			if(confidencerely[k]==1.0 && supportrely[k]>0 && choicenameset.contains(relytoarr[k].getStr()) && cyclenameset.contains(relytoarr[k].getFlow().get(0)))
			{
				System.out.println("choice("+relytoarr[k].getStr()+")"+"=>"+"loop("+relytoarr[k].getFlow().get(0)+","+relytoarr[k].getNumber()+")");
			}
			if(confidencerely[k]==1.0 && supportrely[k]>0 && choicenameset.contains(relytoarr[k].getStr()) && choicenameset.contains(relytoarr[k].getFlow().get(0)))
			{
				System.out.println("choice("+relytoarr[k].getStr()+")"+"=>"+"choice("+relytoarr[k].getFlow()+")");
			}
			if(confidencerely[k]==1.0 && supportrely[k]>0 && cyclenameset.contains(relytoarr[k].getStr()))
			{
				System.out.println("loop("+relytoarr[k].getStr()+","+relytoarr[k].getNumber()+")=>"+"choice("+relytoarr[k].getFlow().get(0)+")");
			}
			
		}
		

		System.out.println("dirtuple" + dirtuple);
		System.out.println("parttuple" + parttuple);
		Set<Tuple> dircasualtuple = new HashSet<Tuple>(dirtuple);
		dircasualtuple.retainAll(parttuple);
		System.out.println("dircasualtuple" + dircasualtuple);

		ArrayList<Tuple> parallellist = new ArrayList<Tuple>(paralleltuple);
		ArrayList<Tuple> dircasuallist = new ArrayList<Tuple>(dircasualtuple);
		ArrayList<Tuple> copydircasuallist = new ArrayList<Tuple>(dircasualtuple);
		
		//ArrayList<Tuple> indircasuallist = new ArrayList<Tuple>(indircasualtuple);
		for (int i = 0; i < parallellist.size(); i++) {
			Tuple t = parallellist.get(i);
			parallelset.add(new Triad(t, Relation.Parallel));
		}
		Transition temp = null;
		for (int i = 0; i < copydircasuallist.size(); i++)
		{
			Tuple t1 = copydircasuallist.get(i);
			if(t1.getFirst().equals(t1.getSecond()))
			{
				copydircasuallist.remove(t1);
				dircasuallist.remove(t1);
				temp=t1.getFirst();
				selfcyclelist.add(t1);
			}
		}
		for (int i = 0; i < copydircasuallist.size(); i++)
		{
			Tuple t2 = copydircasuallist.get(i);
			if(t2.getFirst().equals(temp))
			{
				dircasuallist.remove(t2);
				selfcycledirectlist.add(t2);
			}
			if(t2.getSecond().equals(temp))
			{
				dircasuallist.remove(t2);
				selfcycledirectlist.add(t2);
			}
		}
		System.out.println("selfcyclelist"+selfcyclelist);
		System.out.println("selfcycledirectlistfinal"+selfcycledirectlist);
        System.out.println("dircasuallistfinal"+dircasuallist);
		for (int i = 0; i < dircasuallist.size(); i++) {
			Tuple t = dircasuallist.get(i);
			directcasualset.add(new Triad(t, Relation.DirectCasually));
		}
		for (int i = 0; i < selfcycledirectlist.size(); i++)
		{
			Tuple t = selfcycledirectlist.get(i);
			if(selfcycledirectlist.size()!=0){
			selfcycledirectset.add(new Triad(t, Relation.DirectCasually));
			}
		}
		for (int i = 0; i < selfcyclelist.size(); i++)
		{
			Tuple t = selfcyclelist.get(i);
			selfcycleset.add(new Triad(t, Relation.Selfcycle));
		}

		for (int i = 0; i < ItoTmap.size(); i++) {
			Transition tra = ItoTmap.get(i);
			for(int j=0;j<selfcyclelist.size();j++)
			if(tra!=selfcyclelist.get(j).getFirst()){
			Tuple tup = new Tuple(tra, tra);
			norelset.add(new Triad(tup, Relation.NoRel));
			}
		}
		allrelationset.addAll(directcasualset);
		allrelationset.addAll(parallelset);
		allrelationset.addAll(norelset);
		allrelationset.addAll(selfcycledirectset);
		allrelationset.addAll(selfcycleset);
		Iterator<String> iter = allelementset.iterator();
		Iterator<String> iter2 = allelementset.iterator();
		
		while (iter2.hasNext()) {
			allset.add(StoTmap.get(iter2.next()));
		}

		
		allrelationset.addAll(norelset);
		System.out.println("直接因果跟随关系：" + directcasualset);
		System.out.println("并发关系：" + parallelset);
		System.out.println("无关：" + norelset);
		System.out.println("allelement：" + allelementset);
		System.out.println("directfollowset" + directfollowset);
		
		int l = 0;
		List indexlist = new ArrayList();
		
		System.out.println(indexlist);
		
		int fg1 = 0, fg2 = 0;
		ao: for (int i = 0; i < allelementarr.length; i++) {
			int fg0 = logtotal[i] - mylog.size();
			for (int j = 0; j < allelementarr.length; j++) {
				if (fg0 == logtotal[j]) {
					fg1 = 1;
					i = allelementarr.length + 3;
					break ao;
				}
			}
		}
		System.out.println("fg1" + fg1);
		ko: for (int i = 0; i < mylog.size(); i++) {
			for (int j = 0; j < allelementarr.length; j++) {
				for (int k = 0; k < allelementarr.length; k++) {
					if (tracetotal[i][j] - tracetotal[i][k] == 1) {
						fg2 = 1;
						break ko;
					}

				}
			}
		}
		System.out.println("fg2" + fg2);
		
	}

	

	private String fdcycle(org.processmining.Gather.Tuple temptuple, org.processmining.Gather.Tuple endtetuple) {
		// TODO Auto-generated method stub
		return null;
	}

	private Tuple Tuple(String flag, int i, int j) {
		// TODO Auto-generated method stub
		return null;
	}

	private void createMatrix() {
		ArrayList<Triad> alltriadlist = new ArrayList<Triad>(allrelationset);
		for (int i = 0; i < alltriadlist.size(); i++) {
			Triad triad = alltriadlist.get(i);
			Transition first = triad.getFirst();
			Transition second = triad.getSecond();
			Relation relation = triad.getRelation();
			int f = TtoImap.get(first);
			int s = TtoImap.get(second);
			MatrixofRelation[f][s] = relation;
			MatrixofRelation[s][f] = relation.getReRelation();
		}
	}

	public Set<Relation> getRowRelation(Transition t) {
		Set<Relation> set = new HashSet<Relation>();
		int len = TtoImap.get(t);
		for (int i = 0; i < MatrixofRelation.length; i++) {
			set.add(MatrixofRelation[len][i]);
		}
		return set;
	}

	public Set<Relation> getColumnRelation(Transition t) {
		Set<Relation> set = new HashSet<Relation>();
		int len = TtoImap.get(t);
		for (int i = 0; i < MatrixofRelation.length; i++) {
			set.add(MatrixofRelation[i][len]);
		}
		return set;
	}

	public void changeInCasualRelation(Set<Triad> s) {
		ArrayList<Triad> tl = new ArrayList<Triad>(s);
		for (int i = 0; i < tl.size(); i++) {
			Triad triad = tl.get(i);
			System.out.println(triad);
			Transition t1 = triad.getFirst();
			Transition t2 = triad.getSecond();
			int t1pos = TtoImap.get(t1);
			int t2pos = TtoImap.get(t2);
			changeRelation(t1pos, t2pos, Relation.DirectCasually);
		}
		reSetRelation();
	}

	private void changeRelation(int i, int j, Relation r) {
		MatrixofRelation[i][j] = r;
		MatrixofRelation[j][i] = r.getReRelation();
	}

	private void reSetRelation() {
		directcasualset.clear();
		directcasualset.addAll(getNewRelation(Relation.DirectCasually));
		//indirectcasualset.clear();
		//indirectcasualset.addAll(getNewRelation(Relation.InDirectCasually));

		norelset.clear();
		norelset.addAll(getNewRelation(Relation.NoRel));
	}

	private Set<Triad> getNewRelation(Relation r) {
		Set<Triad> set = new HashSet<Triad>();
		for (int i = 0; i < MatrixofRelation.length; i++) {
			for (int j = 0; j < MatrixofRelation.length; j++) {
				Relation rel = MatrixofRelation[i][j];
				if (rel.equals(r)) {
					Transition t1 = ItoTmap.get(i);
					Transition t2 = ItoTmap.get(j);
					set.add(new Triad(new Tuple(t1, t2), r));
				}
			}
		}
		return set;
	}

	public Set<Triad> getDirectFollowSet() {

		return directfollowset;
	}
	public Set<Triad> getselfcycledirectset()
	{
		return selfcycledirectset;
	}
	/*
	 * public Set<Triad> getbpset() { return bpset; }
	 */

	public Set<Triad> getDirectCasualSet() {
		return directcasualset;
	}
	public Set<String> getchoicenameset()
	{
		return choicenameset;
	}

	public Set<Triad> getNoRelSet() {
		return norelset;
	}

	public Map<String, Transition> getStoTmap() {
		return StoTmap;
	}

	public Map<Transition, Integer> getTtoImap() {
		return TtoImap;
	}

	public Map<Integer, Transition> getItoTmap() {
		return ItoTmap;
	}

	public Relation[][] getMatrixofRelation() {
		return MatrixofRelation;
	}
	public FindRely[] gettransrely()
	{
		return transrely;
	}
	public double[] getconfidencerely (){
		return confidencerely;
		
	}

	public void ShowMatrix() {
		for (int i = 1; i < MatrixtoString.length; i++) {//越界问题i-1
			MatrixtoString[0][i] = ItoTmap.get(i - 1).toString();
			MatrixtoString[i][0] = ItoTmap.get(i - 1).toString();
		}
		for (int i = 0; i < MatrixofRelation.length; i++) {
			for (int j = 0; j < MatrixofRelation.length; j++) {
				MatrixtoString[i + 1][j + 1] = MatrixofRelation[i][j].toString();
			}
		}
		for (int i = 0; i < MatrixtoString.length; i++) {
			for (int j = 0; j < MatrixtoString.length; j++) {
				System.out.printf("%4s", MatrixtoString[i][j]);
			}
			System.out.println();
		}
	}

	public Transition getFirstTransition() {
		Transition t = StoTmap.get(mylog.getFirstTrace().getFirstEvent().getName());
		return t;

	}

	public Transition getLastTransition() {
		Transition t = StoTmap.get(mylog.getFirstTrace().getLastEvent().getName());
		return t;
	}

}
