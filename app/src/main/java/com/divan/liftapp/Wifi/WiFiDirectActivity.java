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

import android.bluetooth.BluetoothClass;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import com.divan.liftapp.FullscreenActivity;
import com.divan.liftapp.R;
import com.divan.liftapp.Wifi.DeviceList.DeviceActionListener;

import java.lang.reflect.Method;
import java.util.List;

/**
 * An activity that uses WiFi Direct APIs to discover and connect with available
 * devices. WiFi Direct APIs are asynchronous and rely on callback mechanism
 * using interfaces to notify the application of operation success or failure.
 * The application should also register a BroadcastReceiver for notification of
 * WiFi state related events.
 */
public class WiFiDirectActivity extends AsyncTask<Void,String,Void> implements ChannelListener, DeviceActionListener {

    public static final String TAG = "wifidirectdemo",FILEMACS="macs.txt";
    final String pathToMacs=FullscreenActivity.pathSDcard+'/'+FullscreenActivity.SettingFolder+"/"+FILEMACS;
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;

    public final FullscreenActivity fullscreenActivity;
    public DeviceList deviceList;
    public DeviceDetail deviceDetail;
    public List<String> macs;
    public String clientMac="";

    private boolean isFinding=false;
    public boolean isConnected=false;
    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    public WiFiDirectActivity(FullscreenActivity fullscreenActivity) {
        this.fullscreenActivity = fullscreenActivity;
        deviceDetail=new DeviceDetail(fullscreenActivity);
        deviceList=new DeviceList(this);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) fullscreenActivity.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(fullscreenActivity, fullscreenActivity.getMainLooper(), null);

        macs=Utils.getConnectedMacs(pathToMacs);



    }

    @Override
    protected Void doInBackground(Void... voids) {
        while (true) {
            if(isCancelled()){
                return null;
            }
            if (!isFinding) {
                isFinding = true;
                if (!isWifiP2pEnabled) {
                    publishProgress("WiFiP2PDisabled");

                } else {
                    publishProgress("Start Discover");
                     final DeviceList fragment = deviceList;
                    fragment.onInitiateDiscovery();
                    manager.discoverPeers(channel, new ActionListener() {

                        @Override
                        public void onSuccess() {
                            List<WifiP2pDevice> e=deviceList.getPeers();
                            Toast.makeText(fullscreenActivity, "Discovery onSuccess",
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int reasonCode) {
                            Toast.makeText(fullscreenActivity, "Discovery Failed : " + reasonCode,
                                    Toast.LENGTH_SHORT).show();
                            isFinding=false;
                        }
                    });
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        fullscreenActivity.unregisterReceiver(receiver);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(fullscreenActivity,"Start WiFi Task",Toast.LENGTH_SHORT).show();
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        fullscreenActivity.registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Toast.makeText(fullscreenActivity,"Finish WiFi Task",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Toast.makeText(fullscreenActivity,values[0],Toast.LENGTH_SHORT).show();
    }

    /**
     * Remove all peers and clear all fields. This is called on
     * BroadcastReceiver receiving a state change event.
     */
    public void resetData() {
        isFinding=false;

        if (deviceList != null) {
            deviceList.clearPeers();
        }
        if (deviceDetail != null) {
            deviceDetail.resetViews();
        }
    }


    @Override
    public void showDetails(WifiP2pDevice device) {
        /*DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);*/
        deviceDetail.showDetails(device);
        if(device.status== WifiP2pDevice.CONNECTED)
        {
            addMac(clientMac);
        }

    }

    @Override
    public void connect(final WifiP2pConfig config) {

        manager.connect(channel, config, new ActionListener() {

            @Override
            public void onSuccess() {
                isConnected=true;
                Toast.makeText(fullscreenActivity, "Connect succesed!",
                        Toast.LENGTH_SHORT).show();
                clientMac=(config.deviceAddress);
                manager.createGroup(channel, new ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(fullscreenActivity,"Groupe created success",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i) {

                    }
                });
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(fullscreenActivity, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void disconnect() {
        Toast.makeText(fullscreenActivity,"disconnect",Toast.LENGTH_SHORT).show();

        isFinding=false;
        isConnected=false;
       /* final DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);*/
        deviceDetail.resetViews();
        manager.removeGroup(channel, new ActionListener() {

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);

            }

            @Override
            public void onSuccess() {
                Toast.makeText(fullscreenActivity,"remove success",Toast.LENGTH_SHORT).show();
                //fragment.getView().setVisibility(View.GONE);
            }

        });
    }

    @Override
    public void onChannelDisconnected() {
        isFinding=false;
        isConnected=false;
        // we will try once more
        if (manager != null && !retryChannel) {
            Toast.makeText(fullscreenActivity, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
            resetData();
            retryChannel = true;
            manager.initialize(fullscreenActivity, fullscreenActivity.getMainLooper(), this);
        } else {
            Toast.makeText(fullscreenActivity,
                    "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void cancelDisconnect() {
        //isFinding=false;

        /*
         * A cancel abort request by user. Disconnect i.e. removeGroup if
         * already connected. Else, request WifiP2pManager to abort the ongoing
         * request
         */
        if (manager != null) {
            final DeviceList fragment =  deviceList;
            if (fragment.getDevice() == null
                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
                disconnect();
            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
                    || fragment.getDevice().status == WifiP2pDevice.INVITED) {

                manager.cancelConnect(channel, new ActionListener() {

                    @Override
                    public void onSuccess() {
                        Toast.makeText(fullscreenActivity, "Aborting connection",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(fullscreenActivity,
                                "Connect abort request failed. Reason Code: " + reasonCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }
    public void changeName(String name){
       /* try {
            Method m = manager.getClass().getMethod(
                    "setDeviceName",
                    new Class[] { WifiP2pManager.Channel.class, String.class,
                            WifiP2pManager.ActionListener.class });

            m.invoke(manager,channel, name, new WifiP2pManager.ActionListener() {
                public void onSuccess() {
                    //Code for Success in changing name
                }

                public void onFailure(int reason) {
                    //Code to be done while name change Fails
                }
            });
        } catch (Exception e) {

            e.printStackTrace();
        }*/
    }
    public void addMac(String mac){
        macs.add(clientMac);
      //  Utils.addMac(pathToMacs,clientMac);
    }

}
