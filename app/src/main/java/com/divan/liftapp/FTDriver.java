package com.divan.liftapp;
/*
 * FTDI Driver Class
 * 
 * Copyright (C) 2011 @ksksue
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

/*
 * FT232RL
 * Baudrate : any
 * RX Data Size up to 60byte
 * TX Data Size up to 64byte
 */

import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class FTDriver {
	
    private static final int FTDI_VID = 0x0403;
    private static final int FTDI_PID = 0x6001;

    public static final int BAUD9600	= 9600;
    public static final int BAUD14400	= 14400;
    public static final int BAUD19200	= 19200;
    public static final int BAUD38400	= 38400;
    public static final int BAUD57600	= 57600;
    public static final int BAUD115200	= 115200;
    public static final int BAUD230400	= 230400;

    private static final String TAG = "FTDriver";

    private UsbManager mManager;
    private UsbDevice mDevice;
    private UsbDeviceConnection mDeviceConnection;
    private UsbInterface mInterface;

    private UsbEndpoint mFTDIEndpointIN;
    private UsbEndpoint mFTDIEndpointOUT;

	private StringBuilder history=new StringBuilder();

	private int sizeOfReadBuffer=0;

	public boolean isConnection(){
		if(mDeviceConnection==null||mFTDIEndpointIN==null||mDevice==null)
			return false;
		for (UsbDevice device :  mManager.getDeviceList().values()) {
			if(mDevice==device)return true;
		}
		return false;
	}

    public FTDriver(UsbManager manager) {
        mManager = manager;
    }
    
    // Open an FTDI USB Device
    public boolean begin(int baudrate,int sizeOfBuffer) {
		/* my shit*/
		//baudrate=2400;
		sizeOfReadBuffer=sizeOfBuffer;

        for (UsbDevice device :  mManager.getDeviceList().values()) {
			history.append("Device:"+device.toString()+"\n");
        	  Log.i(TAG,"Devices : "+device.toString());
            UsbInterface intf = findUSBInterfaceByVIDPID(device,FTDI_VID,FTDI_PID);
            if (setUSBInterface(device, intf)) {
				history.append("Interface found\n");
                break;
            }
        }
         
        if(!setFTDIEndpoints(mInterface)){
			history.append("setFTDIEndpoints=false \n");
        	return false;
        }
        
        initFTDIChip(mDeviceConnection,baudrate);
        
        history.append("Device Serial : "+mDeviceConnection.getSerial()+"\n");

        if(mDeviceConnection==null||mFTDIEndpointIN==null||mDevice==null)
			return false;
        return true;
    }

    // Close the device
    public void end() {
    	setUSBInterface(null,null);
    }

    // Read Binary Data
    public int read(byte[] buf) {
    	int i,len;
    	byte[] rbuf = new byte[64];//work while =64
		if(mDeviceConnection==null||mFTDIEndpointIN==null||mDevice==null)
			return -1;

		//len = mDeviceConnection.bulkTransfer(mFTDIEndpointIN, buf, buf.length, 0);
		len = mDeviceConnection.bulkTransfer(mFTDIEndpointIN, rbuf, rbuf.length, 0); // RX; buf.length;
		//history.append("after bulkTransfer\n");

		len=((len-2)<buf.length)?(len-2):buf.length;
		// FIXME shift rbuf's pointer 2 to 0. (I don't know how to do.) 
		for(i=0;i<len;++i) {
			buf[i] = rbuf[i+2];
		}
		return (len);
    }
	public boolean readInMy(byte[] buf){
		if(mDeviceConnection==null||mFTDIEndpointIN==null||mDevice==null)
			return false;
		byte[] rbuf = new byte[buf.length];
		int len = mDeviceConnection.bulkTransfer(mFTDIEndpointIN, rbuf, rbuf.length, 0);
		len=((len-2)<buf.length)?(len-2):buf.length;
		// FIXME shift rbuf's pointer 2 to 0. (I don't know how to do.)
		for(int i=0;i<len;++i) {
			buf[i] = rbuf[i+2];
		}
		return true;
	}

    // Write 1byte Binary Data
    public int write(byte[] buf) {
		return mDeviceConnection.bulkTransfer(mFTDIEndpointOUT, buf, 1, 0); // TX    	
    }
	
    // Write n byte Binary Data
    public int write(byte[] buf,int length) {
    	if(length > 64) {
    		return -1;
    	}
		return mDeviceConnection.bulkTransfer(mFTDIEndpointOUT, buf, length, 0); // TX    	
    }
    
    // TODO Implement these methods
/*    public void available() {
    	
    }
    
    public void peek() {
    	
    }
    
    public void flush() {
    	
    }
    
    public void print() {
    	
    }
    
    public void println() {
    	
    }
    */

    // Initial control transfer
	private void initFTDIChip(UsbDeviceConnection conn,int baudrate) {
		int baud = calcFTDIBaudrate(baudrate);
		conn.controlTransfer(0x40, 0, 0, 0, null, 0, 0);				//reset
		conn.controlTransfer(0x40, 0, 1, 0, null, 0, 0);				//clear Rx
		conn.controlTransfer(0x40, 0, 2, 0, null, 0, 0);				//clear Tx
		conn.controlTransfer(0x40, 0x02, 0x0000, 0, null, 0, 0);	//flow control none
		conn.controlTransfer(0x40, 0x03, baud, 0, null, 0, 0);		//set baudrate
		conn.controlTransfer(0x40, 0x04, 0x0008, 0, null, 0, 0);	//data bit 8, parity none, stop bit 1, tx off
	}
	
	/* Calculate a Divisor at 48MHz
	 * 9600	: 0x4138
	 * 11400	: 0xc107
	 * 19200	: 0x809c
	 * 38400	: 0xc04e
	 * 57600	: 0x0034
	 * 115200	: 0x001a
	 * 230400	: 0x000d
	 */
	private int calcFTDIBaudrate(int baud) {
		int divisor;
		if(baud <= 3000000) {
			divisor = calcFT232bmBaudBaseToDiv(baud, 48000000);
		} else {
			Log.e(TAG,"Cannot set baud rate : " + baud + ", because too high." );
			Log.e(TAG,"Set baud rate : 9600" );
			divisor = calcFT232bmBaudBaseToDiv(9600, 48000000);
		}
		return divisor;
	}

	// Calculate a divisor from baud rate and base clock for FT232BM, FT2232C and FT232LR
	// thanks to @titoi2
	private int calcFT232bmBaudBaseToDiv(int baud, int base) {
		int divisor;
		divisor = (base / 16 / baud)
		| (((base / 2 / baud) & 4) != 0 ? 0x4000 // 0.5
				: ((base / 2 / baud) & 2) != 0 ? 0x8000 // 0.25
						: ((base / 2 / baud) & 1) != 0 ? 0xc000 // 0.125
								: 0);
		return divisor;
	}
	
	private boolean setFTDIEndpoints(UsbInterface intf) {
		UsbEndpoint epIn,epOut;
		if(intf==null)
			return false;
    	epIn = intf.getEndpoint(0);
    	epOut = intf.getEndpoint(1);
		
    	if(epIn != null && epOut != null) {
    		mFTDIEndpointIN = intf.getEndpoint(0);
    		mFTDIEndpointOUT = intf.getEndpoint(1);
    		return true;
    	} else {
    		return false;
    	}
	}
	
    // Sets the current USB device and interface
    private boolean setUSBInterface(UsbDevice device, UsbInterface intf) {
        if (mDeviceConnection != null) {
            if (mInterface != null) {
                mDeviceConnection.releaseInterface(mInterface);
                mInterface = null;
            }
            mDeviceConnection.close();
            mDevice = null;
            mDeviceConnection = null;
        }

        if (device != null && intf != null) {
            UsbDeviceConnection connection = mManager.openDevice(device);
            if (connection != null) {
               history.append("open succeeded"+"\n");
                if (connection.claimInterface(intf, false)) {
					history.append("claim interface succeeded\n");
                	
                	if(device.getVendorId() == FTDI_VID && device.getProductId() == FTDI_PID) {
						history.append("Vendor ID : "+device.getVendorId()+"\n");
						history.append("Product ID : "+device.getProductId()+"\n");
                    	mDevice = device;
                    	mDeviceConnection = connection;
                    	mInterface = intf;
                    	return true;
                    }

                } else {
					history.append("claim interface failed\n");

                    connection.close();
                }
            } else {
				history.append("open failed\n");

            }
        }
        return false;
    }
    
    // searches for an interface on the given USB device by VID and PID
    private UsbInterface findUSBInterfaceByVIDPID(UsbDevice device,int vid, int pid) {
       history.append( "findUSBInterface " + device+"\n");
        int count = device.getInterfaceCount();
        for (int i = 0; i < count; i++) {
            UsbInterface intf = device.getInterface(i);
            if (device.getVendorId() == vid && device.getProductId() == pid) {
                return intf;
              }
        }
        return null;
    }
    
    // when insert the device USB plug into a USB port
	public boolean usbAttached(Intent intent) {
		UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
		UsbInterface intf = findUSBInterfaceByVIDPID(device, FTDI_VID,FTDI_PID);
		if (intf != null) {
			Log.d(TAG, "Found USB interface " + intf+"\n");
			setUSBInterface(device, intf);
			return true;
		} else {
			return false;
		}
	}
	
	// when remove the device USB plug from a USB port
	public void usbDetached(Intent intent) {
		UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
		String deviceName = device.getDeviceName();
		if (mDevice != null && mDevice.equals(deviceName)) {
			Log.d(TAG, "USB interface removed");
			setUSBInterface(null, null);
		}
	}

	public String getHistory() {
		return history.toString();
	}
}