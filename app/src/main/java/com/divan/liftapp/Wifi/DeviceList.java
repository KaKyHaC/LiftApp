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

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * A ListFragment that displays available peers on discovery and requests the
 * parent activity to handle user interaction events
 */
public class DeviceList implements PeerListListener {

    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    ProgressDialog progressDialog = null;
    View mContentView = null;
    private WifiP2pDevice device;

    public DeviceList(WiFiDirectActivity wiFiDirectActivity) {
        this.wiFiDirectActivity = wiFiDirectActivity;
        new WiFiPeerListAdapter(wiFiDirectActivity.fullscreenActivity,0,peers);
        //this.setListAdapter(new WiFiPeerListAdapter(getActivity(), R.layout.row_devices, peers));
    }

    WiFiDirectActivity wiFiDirectActivity;


    /**
     * @return this device
     */
    public WifiP2pDevice getDevice() {
        return device;
    }

    private static String getDeviceStatus(int deviceStatus) {
        Log.d(WiFiDirectActivity.TAG, "Peer status :" + deviceStatus);
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";

        }
    }

    /**
     * Initiate a connection with the peer.
     */

    /**
     * Array adapter for ListFragment that maintains WifiP2pDevice list.
     */
    private class WiFiPeerListAdapter extends ArrayAdapter<WifiP2pDevice> {

        private List<WifiP2pDevice> items;

        /**
         * @param context
         * @param textViewResourceId
         * @param objects
         */
        public WiFiPeerListAdapter(Context context, int textViewResourceId,
                List<WifiP2pDevice> objects) {
            super(context, textViewResourceId, objects);
            items = objects;

        }


    }

    /**
     * Update UI for this device.
     * 
     * @param device WifiP2pDevice object
     */
    public void updateThisDevice(WifiP2pDevice device) {
        this.device = device;
        wiFiDirectActivity.changeName(Security.setPref(device.deviceName,Security.prefLift));
        if(device.status==WifiP2pDevice.CONNECTED){
            findAndAddMac();
            /*wiFiDirectActivity.makeToast("addMac");
            wiFiDirectActivity.addMac(device.deviceAddress);*/

        }
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        wiFiDirectActivity.makeToast("список устройств обновлен");
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        peers.clear();
        peers.addAll(peerList.getDeviceList());
        tryConnect();
        findAndAddMac();
    }

    public List<WifiP2pDevice> getPeers(){return peers;}
    public void clearPeers() {
        peers.clear();
    }

    /**
     * 
     */
    public void onInitiateDiscovery() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * An interface-callback for the activity to listen to fragment interaction
     * events.
     */
    public void tryConnect(){
        if(!wiFiDirectActivity.isConnected&&device.status==WifiP2pDevice.AVAILABLE) {//TODO make correct if
            wiFiDirectActivity.makeToast("выбор устройства для подключения");
            //List<WifiP2pDevice> peers = getPeers();
            for (WifiP2pDevice device : peers) {
                boolean isHave = false;
                String curMac = device.deviceAddress;
                for (String mac : wiFiDirectActivity.macs) {
                    if (mac .equals( curMac))
                        isHave = true;
                }
                if (!isHave&&Security.hasPref(device.deviceName,Security.prefSetting)&&device.status==WifiP2pDevice.AVAILABLE) {
                    wiFiDirectActivity.makeToast("попытка подключения к " + device.deviceName);
                    WifiP2pConfig config = new WifiP2pConfig();
                    config.deviceAddress = device.deviceAddress;
                    config.wps.setup = WpsInfo.PBC;
                    config.groupOwnerIntent=15;
                    wiFiDirectActivity.connect(config);
                    asyncCancelConectAlgoritm(10000);
                    break;
                }else{
                    wiFiDirectActivity.makeToast("подключения не будет к "+device.deviceName);
//                    if(isHave)
//                        wiFiDirectActivity.makeToast("мас адрес "+device.deviceName+" уже есть");
//                    if(device.status!=WifiP2pDevice.AVAILABLE)
//                        wiFiDirectActivity.makeToast(device.deviceName+" status:"+getDeviceStatus(device.status));
                }
            }
        }else  wiFiDirectActivity.makeToast(getDeviceStatus(device.status));

    }
    private void asyncCancelConectAlgoritm(final int time_delay){
        new Thread(new Runnable() {
            @Override
            public void run() {
                wiFiDirectActivity.isConnected=true;
                try {
                    Thread.sleep(time_delay);
                }catch (InterruptedException e){}
                if(device.status!=WifiP2pDevice.CONNECTED) {
                    wiFiDirectActivity.isConnected=false;
                    wiFiDirectActivity.cancelConncet();
                }
            }
        }).start();
    }
    private void findAndAddMac(){
        for(WifiP2pDevice device:peers){
            if(device.status==WifiP2pDevice.CONNECTED)
                wiFiDirectActivity.addMac(device.deviceAddress);
        }
    }

    public int getDiviceStatus(){return device.status;}
    public interface DeviceActionListener {

        void showDetails(WifiP2pDevice device);

        void cancelDisconnect();

        void connect(WifiP2pConfig config);

        void disconnect();

    }

}
