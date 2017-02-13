/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.divan.liftapp.Wifi;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.divan.liftapp.FullscreenActivity;
import com.divan.liftapp.R;
import com.divan.liftapp.Setting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class DeviceDetail implements ConnectionInfoListener {

	public static final String IP_SERVER = "192.168.49.1";
	public static int PORT = 8988;
	private static boolean server_running = false;

	protected static final int CHOOSE_FILE_RESULT_CODE = 20;
	private WifiP2pDevice device;
	private WifiP2pInfo info;
	ProgressDialog progressDialog = null;

	FullscreenActivity fullscreenActivity;

	public DeviceDetail(FullscreenActivity fullscreenActivity) {
		this.fullscreenActivity = fullscreenActivity;
	}

	@Override
	public void onConnectionInfoAvailable(final WifiP2pInfo info) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		if (!server_running){
			Setting setting=new Setting(fullscreenActivity.SettingFolder,fullscreenActivity.settingFile);
			new ServerAsyncTask(fullscreenActivity,setting.getUri().getPath())
					.execute();
			server_running = true;
		}
	}

	/**
	 * Updates the UI with device data
	 * 
	 * @param device the device to be displayed
	 */
	public void showDetails(WifiP2pDevice device) {
		this.device = device;
		if(device.status==WifiP2pDevice.CONNECTED)
		{

		}
	/*	this.getView().setVisibility(View.VISIBLE);
		TextView view = (TextView) mContentView.findViewById(R.id.device_address);
		view.setText(device.deviceAddress);
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText(device.toString());
*/
	}

	/**
	 * Clears the UI fields after a disconnect or direct mode disable operation.
	 */
	public void resetViews() {
		/*mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
		TextView view = (TextView) mContentView.findViewById(R.id.device_address);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.group_owner);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.status_text);
		view.setText(R.string.empty);
		mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
		this.getView().setVisibility(View.GONE);*/
	}

	/**
	 * A simple server socket that accepts connection and writes some data on
	 * the stream.
	 */
	public static class ServerAsyncTask extends AsyncTask<String, String, String> {

		private final FullscreenActivity fullscreenActivity;
		private final String settingPath;

		public ServerAsyncTask(FullscreenActivity fullscreenActivity, String settingPath) {
			this.fullscreenActivity = fullscreenActivity;
			this.settingPath = settingPath;
		}


		@Override
		protected String doInBackground(String... params) {
			try {
				while(server_running) {
					ServerSocket serverSocket = new ServerSocket(PORT);
					Log.d(WiFiDirectActivity.TAG, "Server: Socket opened");
					Socket client = serverSocket.accept();
					Log.d(WiFiDirectActivity.TAG, "Server: connection done");
					publishProgress("Connection done",null);
					final File f = new File(settingPath);
					File dirs = new File(f.getParent());
					if (!dirs.exists())
						dirs.mkdirs();
					f.createNewFile();

					Log.d(WiFiDirectActivity.TAG, "server: copying files " + f.toString());
					InputStream inputstream = client.getInputStream();
					copyFile(inputstream, new FileOutputStream(f));
					serverSocket.close();
					publishProgress("Set setting from wifi","q");
				}
				server_running = false;
//				return f.getAbsolutePath();
				return  null;
			} catch (IOException e) {
				Log.e(WiFiDirectActivity.TAG, e.getMessage());
				return null;
			}
		}

		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				Toast.makeText(fullscreenActivity,"finish WiFi direct",Toast.LENGTH_SHORT).show();
				fullscreenActivity.SetSettingFromWiFi();
				//fullscreenActivity.RunWiFiTusk();
			}

		}

		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
				Toast.makeText(fullscreenActivity,"start WiFi transaction",Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			if(values[0]!=null) {
				Toast.makeText(fullscreenActivity, values[0], Toast.LENGTH_SHORT).show();
				if(values[1]!=null)
					fullscreenActivity.SetSettingFromWiFi();
			}

		}
	}

	public static boolean copyFile(InputStream inputStream, OutputStream out) {
		byte buf[] = new byte[1024];
		int len;
		try {
			while ((len = inputStream.read(buf)) != -1) {
				out.write(buf, 0, len);

			}
			out.close();
			inputStream.close();
		} catch (IOException e) {
			Log.d(WiFiDirectActivity.TAG, e.toString());
			return false;
		}
		return true;
	}

}
