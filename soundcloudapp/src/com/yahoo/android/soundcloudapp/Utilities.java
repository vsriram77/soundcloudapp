package com.yahoo.android.soundcloudapp;

public  class Utilities {
	public static String millisecToTime(long currentDuration, long totalDuration) {
		String time;
		long currMin = currentDuration/60000;
		long currSec = (currentDuration/1000) % 60;
		long totalMin = totalDuration/60000;
		long totalSec = (totalDuration/1000)%60;
		String currSeparator = ":";
		String totalSeparator = ":";
		
		if(currSec < 10) {
			currSeparator = ":0";
		}
		
		if(totalSec < 10) {
			totalSeparator = ":0";
		}

		time = currMin + currSeparator + currSec + "/" + totalMin + totalSeparator + totalSec;
		return time;
	}
	
	public static long getProgressPercentage(long currentDuration, long totalDuration) {
		return (currentDuration*100/totalDuration);
	}
	
	public static int progressToTime(int progressPct, int totalDuration) {
		return (progressPct*totalDuration)/100;
	}
}
