package com.bbkmobile.iqoo.bean.vo;

import java.util.List;

public class SugSearchResultObject extends ResultObject<List<String>>{

	private String word;
	private SugRecApp rec ;
	
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public SugRecApp getRec() {
		return rec;
	}
	public void setRec(SugRecApp rec) {
		this.rec = rec;
	}
	
}
