package com.cl.util;

import java.util.List;

import com.cl.data.Load;

public class LoadUtil {
	public static Load average(List<Load> loads) {
		if (loads == null || loads.size() == 0) {
			return null;
		}
		Load load = new Load(loads.get(0).getServername());
		load.setUtctime(loads.get(0).getUtctime());
		int cpuTotal = 0, memoryTotal = 0;
		for (int i = 0; i < loads.size(); i++) {
			cpuTotal += loads.get(i).getCpu();
			memoryTotal += loads.get(i).getMemory();
		}
		load.setCpu((short) (cpuTotal / loads.size()));
		load.setMemory((short) (memoryTotal / loads.size()));
		return load;
	}

}
