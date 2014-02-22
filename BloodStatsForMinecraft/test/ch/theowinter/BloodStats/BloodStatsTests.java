package ch.theowinter.BloodStats;

import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class BloodStatsTests {
	@Test
	public void getAUnixTimeStamp(){
		//Pretty dumb test
		BloodStats stats = new BloodStats();
		stats.getUnixTimeStamp();
		assertTrue(stats.getUnixTimeStamp().length()>1);
	}
}
