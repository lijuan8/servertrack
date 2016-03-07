package com.cl.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cl.Factory.LoadFactory;
import com.cl.data.Load;

public class LoadUtilTest {

	@Test
	public void test() {
		List<Load> loads = new ArrayList<Load>();
		String time = "201601011234";
		for(int i = 0; i < 10; i ++){
			loads.add(LoadFactory.create("server", time, (short)(9999), (short)(9999)));
		}
		Load result = LoadUtil.average(loads);
		Assert.assertTrue(result.getCpu() == 9999);
	}

}
