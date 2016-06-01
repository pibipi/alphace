package com.alphace.thread;

import android.content.Context;

import com.alphace.service.BluetoothLeService;
import com.alphace.utils.SharedpreferencesUtil;

public class StateThread extends Thread {
	private Context context;
	private BluetoothLeService mBluetoothLeService;

	public StateThread(Context context, BluetoothLeService mBluetoothLeService) {
		super();
		this.context = context;
		this.mBluetoothLeService = mBluetoothLeService;
	}

	@Override
	public void run() {
		SharedpreferencesUtil sharedpreferencesUtil = new SharedpreferencesUtil(
				context);
		System.out.println(sharedpreferencesUtil.getMyDeviceMac() + "");
		while (true) {
			boolean state = mBluetoothLeService
					.getConnState(sharedpreferencesUtil.getMyDeviceMac());
			sharedpreferencesUtil.setConnectState(state);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
