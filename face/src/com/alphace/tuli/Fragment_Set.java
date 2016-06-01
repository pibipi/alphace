package com.alphace.tuli;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.alphace.yuyan.R;

public class Fragment_Set extends Fragment {
	private ScrollView background;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_set, null);
		init(view);
		background.smoothScrollTo(0, 1000);
		return view;
	}

	private void init(View view) {
		background = (ScrollView) view.findViewById(R.id.background);
	}
}
