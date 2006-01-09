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

import javax.usb.UsbClaimException;
import javax.usb.UsbDisconnectedException;
import javax.usb.UsbException;
import javax.usb.UsbInterfacePolicy;
import javax.usb.UsbNotActiveException;

import net.sf.libusb.Libusb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.jusb.UsbInterfaceImp;
import com.ibm.jusb.os.DefaultUsbInterfaceOsImp;
import com.ibm.jusb.os.UsbInterfaceOsImp;


/**
 * UsbInterfaceOsImp implementation for Windows platform.
 * <p>
 * This must be set up before use.
 * <ul>
 * <li>The {@link #getUsbInterfaceImp() UsbInterfaceImp} must be set
 *     either in the constructor or by its {@link #setUsbInterfaceImp(UsbInterfaceImp) setter}.</li>
 * <li>The {@link #getWindowsDeviceOsImp() WindowsDeviceOsImp} must be set
 *     either in the constructor or by its {@link #setWindowsDeviceOsImp(WindowsDeviceOsImp) setter}.</li>
 * </ul>
 * @author Dan Streetman
 * @author Kambiz Darabi
 */
class WindowsInterfaceOsImp extends DefaultUsbInterfaceOsImp
    implements UsbInterfaceOsImp
{
    /** Used for logging */
    Log log = LogFactory.getLog(WindowsInterfaceOsImp.class);

    /** Constructor */
    public WindowsInterfaceOsImp(
        UsbInterfaceImp iface,
        WindowsDeviceOsImp device)
    {
        setUsbInterfaceImp(iface);
        setWindowsDeviceOsImp(device);
    }

    //*************************************************************************
    // Public methods

    /** @return The UsbInterfaceImp for this */
    public UsbInterfaceImp getUsbInterfaceImp()
    {
        return usbInterfaceImp;
    }

    /** @param iface The UsbInterfaceImp for this */
    public void setUsbInterfaceImp(UsbInterfaceImp iface)
    {
        usbInterfaceImp = iface;
    }

    /** @return The WindowsDeviceOsImp for this */
    public WindowsDeviceOsImp getWindowsDeviceOsImp()
    {
        return windowsDeviceOsImp;
    }

    /** @param device The WindowsDeviceOsImp for this */
    public void setWindowsDeviceOsImp(WindowsDeviceOsImp device)
    {
        windowsDeviceOsImp = device;
    }

    /** Claim this interface. */

    /*
       public void claim() throws UsbException
       {
               WindowsInterfaceRequest request = new WindowsInterfaceRequest.WindowsClaimInterfaceRequest(getInterfaceNumber());
               submit(request);
               request.waitUntilCompleted();
               if (0 != request.getError())
                       throw new UsbException("Could not claim interface : " + JavaxUsb.nativeGetErrorMessage(request.getError()));
       }
     */

    /** Release this interface. */

    /*
       public void release()
       {
               WindowsInterfaceRequest request = new WindowsInterfaceRequest.WindowsReleaseInterfaceRequest(getInterfaceNumber());
               try {
                       submit(request);
               } catch ( UsbException uE ) {
       //FIXME - log this
                               return;
                       }
                       request.waitUntilCompleted();
               }
     */

    /** @return if this interface is claimed. */

    /*
       public boolean isClaimed()
       {
               WindowsInterfaceRequest request = new WindowsInterfaceRequest.WindowsIsClaimedInterfaceRequest(getInterfaceNumber());
               try {
                       submit(request);
               } catch ( UsbException uE ) {
       //FIXME - log this
                               return false;
                       }
                       request.waitUntilCompleted();
                       if (0 != request.getError()) {
       //FIXME - log
                                       return false;
                       }
                       return request.isClaimed();
               }
     */
    public byte getInterfaceNumber()
    {
        if (!interfaceNumberSet)
        {
            interfaceNumber =
                usbInterfaceImp.getUsbInterfaceDescriptor().bInterfaceNumber();
            interfaceNumberSet = true;
        }

        return interfaceNumber;
    }

    /**
     * Submit a Request.
     * @param request The WindowsRequest.
     */

    /*
       void submit(WindowsRequest request) throws UsbException { getWindowsDeviceOsImp().submit(request); }
     */

    /**
     * Cancel a Request.
     * @param request The WindowsRequest.
     */

    /*
       void cancel(WindowsRequest request) { getWindowsDeviceOsImp().cancel(request); }
     */
    protected UsbInterfaceImp usbInterfaceImp = null;
    protected WindowsDeviceOsImp windowsDeviceOsImp = null;
    private boolean interfaceNumberSet = false;
    private byte interfaceNumber = 0;

    /**
       Claims the interface using libusb method. Note, that
       {@link UsbInterfaceImp} takes care of checking, whether
       this interface was already claimed.
       <p>
       This implementation currently ignores the argument policy.
     */
    public synchronized void claim(UsbInterfacePolicy policy)
        throws UsbClaimException, UsbException, UsbNotActiveException, 
            UsbDisconnectedException
    {
        log.debug("Entering claim.");

        // acquire the mutex, so we access libusb exclusively
        JavaxUsb.getMutex().acquire();

        try
        {
            int result =
                Libusb.usb_claim_interface(
                    getWindowsDeviceOsImp().getHandle(),
                    getInterfaceNumber());

            if (result != 0)
            {
                String msg =
                    "Couldn't claim interface. usb error: "
                    + Libusb.usb_strerror();
                log.debug(msg);
                throw new UsbClaimException(msg);
            }
        }
        finally
        {
            // release the mutex
            JavaxUsb.getMutex().release();
        }

        log.debug("Leaving claim");
    }

    /* (non-Javadoc)
     * @see javax.usb.UsbInterface#release()
     */
    public void release()
        throws UsbClaimException, UsbException, UsbNotActiveException, 
            UsbDisconnectedException
    {
        log.debug("Entering release.");

        // acquire the mutex, so we access libusb exclusively
        JavaxUsb.getMutex().acquire();

        try
        {
            int result =
                Libusb.usb_release_interface(
                    getWindowsDeviceOsImp().getHandle(),
                    getInterfaceNumber());

            if (result != 0)
            {
                String msg =
                    "Couldn't release interface. usb error: "
                    + Libusb.usb_strerror();
                log.debug(msg);
                throw new UsbClaimException(msg);
            }
        }
        finally
        {
            // release the mutex
            JavaxUsb.getMutex().release();
        }

        log.debug("Leaving release");
    }

    /* (non-Javadoc)
     * @see com.ibm.jusb.UsbInterfaceImp#getUsbInterfaceOsImp()
     */
    public UsbInterfaceOsImp getUsbInterfaceOsImp()
    {
        return this;
    }
}
