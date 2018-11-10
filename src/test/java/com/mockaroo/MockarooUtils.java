package com.mockaroo;

import java.util.Comparator;

public class MockarooUtils implements Comparator<String> {

	@Override
	public int compare(String s1, String s2) {
		return s1.length()-s2.length();
	}

	
}