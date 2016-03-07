package com.cl.Factory;

import java.util.Date;

import com.cl.data.Load;
import com.cl.util.TimeUtil;

public class LoadFactory {
	public static Load create(String server, short cpu, short memory){
		return create(server, new Date(), cpu, memory);
	}
	
	public static Load create(String server, Date time, short cpu, short memory){
		return create(server, TimeUtil.toMinuteString(time), cpu, memory);
	}
	
	public static Load create(String server, String time, short cpu, short memory){
		Load load = new Load(server);
		load.setUtctime(time);
		load.setCpu(cpu);
		load.setMemory(memory);
		return load;
	}
}
