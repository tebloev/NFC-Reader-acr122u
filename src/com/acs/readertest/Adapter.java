package com.acs.readertest;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Adapter extends BaseAdapter {

	ArrayList<Item> data = new ArrayList<Item>();
	Context context;

	public Adapter(Context context, ArrayList<Item> arr) {
		if (arr != null) {
			data = arr;
		}
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int num) {
		// TODO Auto-generated method stub
		return data.get(num);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int i, View someView, ViewGroup arg2) {
		//Получение объекта inflater из контекста
		LayoutInflater inflater = LayoutInflater.from(context);
		//Если someView (View из ListView) вдруг оказался равен
		//null тогда мы загружаем его с помошью inflater 
		if (someView == null) {
			someView = inflater.inflate(R.layout.list, arg2, false);
		}
		//Обявляем наши текствьюшки и связываем их с разметкой
		TextView header = (TextView) someView.findViewById(R.id.item_name);
		TextView subHeader = (TextView) someView.findViewById(R.id.item_date);
		TextView subSubHeader = (TextView) someView.findViewById(R.id.item_id);
		
		//Устанавливаем в каждую текствьюшку соответствующий текст
		// сначала заголовок
		header.setText(data.get(i).name);
		// потом подзаголовок
		subHeader.setText(data.get(i).date);
		subSubHeader.setText(data.get(i).id);
		return someView;
	}

}
  