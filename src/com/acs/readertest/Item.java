package com.acs.readertest;

import android.text.Spanned;

public class Item {
 	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	String name;
	String id;
	String date;


	Item(String n, String i, String date){
		this.name=n;
		this.id=i;
		this.date = date;
		
	}
		
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
		

	
	
}
