package com.tv.xeeng.game.room;

import com.tv.xeeng.memcached.data.XEDataUtils;

import java.util.List;
import java.util.Random;

public class XEGameConstants {
    public static final List<String> BLACKLIST_WORDS = XEDataUtils.loadBlacklistWords();

	public static List<String> TABLE_NAMES = null;
	private static Random rangen = new Random();
	static {
		TABLE_NAMES = XEDataUtils.loadPredefinedTableNames();
	}
	public int getRandom(int max) {
		return rangen.nextInt(max);
	}
	public static String getRandomName() {
		return TABLE_NAMES.get(rangen.nextInt(TABLE_NAMES.size()));
	}
}
