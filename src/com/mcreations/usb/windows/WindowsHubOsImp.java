/*
 * Copyright (c) 2005 m-creations GmbH  http://www.m-creations.com/
 * Copyright (c) 2003 Dan Streetman (ddstreet@ieee.org)
 * Copyright (c) 2003 International Business Machines Corporation
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/CPLv1.0.htm
 */
package com.mcreations.usb.windows;

import java.util.List;

import javax.usb.UsbHub;
import javax.usb.UsbPort;

import net.sf.libusb.usb_device;

import com.ibm.jusb.UsbHubImp;


/**
 * Windows implementation of an UsbHub is just a thin wrapper
 * around {@link com.ibm.jusb.UsbHubImp}.
 * 
 * @author Kambiz Darabi
 */
public class WindowsHubOsImp extends WindowsDeviceOsImp implements UsbHub
{
    /** Delegate for the UsbHub methods */
    private UsbHubImp usbHub;

    /**
     *
     */
    public WindowsHubOsImp(usb_device device)
    {
        super(device);

        // initially, we have 1 port :-)
        usbHub = new UsbHubImp(
                1,
                getUsbDeviceDescriptor());
    }

    /* (non-Javadoc)
     * @see javax.usb.UsbHub#getNumberOfPorts()
     */
    public byte getNumberOfPorts()
    {
        return usbHub.getNumberOfPorts();
    }

    /* (non-Javadoc)
     * @see javax.usb.UsbHub#getUsbPorts()
     */
    public List getUsbPorts()
    {
        return usbHub.getUsbPorts();
    }

    /* (non-Javadoc)
     * @see javax.usb.UsbHub#getUsbPort(byte)
     */
    public UsbPort getUsbPort(byte number)
    {
        return usbHub.getUsbPort(number);
    }

    /* (non-Javadoc)
     * @see javax.usb.UsbHub#getAttachedUsbDevices()
     */
    public List getAttachedUsbDevices()
    {
        return usbHub.getAttachedUsbDevices();
    }

    /* (non-Javadoc)
     * @see javax.usb.UsbHub#isRootUsbHub()
     */
    public boolean isRootUsbHub()
    {
        return usbHub.isRootUsbHub();
    }
}
