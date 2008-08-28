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

import com.ibm.jusb.UsbDeviceImp;
import com.ibm.jusb.UsbHubImp;
import com.ibm.jusb.os.AbstractUsbServices;
import com.ibm.jusb.util.RunnableManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbServices;
import javax.usb.event.UsbServicesEvent;
//import javax.usb.util.UsbUtil;


/**
 * UsbServices implementation for Windows platform.
 * @author Dan Streetman
 * @author Kambiz Darabi
 */
public class WindowsUsbServices extends AbstractUsbServices
    implements UsbServices
{
    /** Used for logging */
    Log log = LogFactory.getLog(WindowsUsbServices.class);

    public WindowsUsbServices()
        throws UsbException
    {
           
        JavaxUsb.initialise();

        topologyUpdateManager.setMaxSize(Long.MAX_VALUE);

        checkProperties();

        startTopologyListener();
    }

    //*************************************************************************
    // Public methods

    /** @return The virtual USB root hub */
    public synchronized UsbHub getRootUsbHub()
        throws UsbException
    {
        if(log.isDebugEnabled()) log.debug( "getRootUsbHub() requested hub" );

        synchronized (topologyLock)
        {
            if (!firstUpdateDone)
            {
                try
                {
                    topologyLock.wait(MAX_FIRST_UPDATE_DELAY);
                }
                catch (InterruptedException iE)
                {
                }
            }
        }

        return getRootUsbHubImp();

        // this was the original implementation (the constructor didn't exist)
        //		JavaxUsb.loadLibrary(); 
        //
        //		if (!isListening()) {
        //			synchronized (topologyLock) {
        //				startTopologyListener();
        //
        //				try {
        //					topologyLock.wait();
        //				} catch ( InterruptedException iE ) {
        //					throw new UsbException("Interrupted while enumerating USB devices, try again");
        //				}
        //			}
        //		}
        //
        //		if ( 0 != topologyListenerError ) throw new UsbException( COULD_NOT_ACCESS_USB_SUBSYSTEM + " : " + topologyListenerError );
        //
        //		if ( 0 != topologyUpdateResult ) throw new UsbException( COULD_NOT_ACCESS_USB_SUBSYSTEM + " : " + topologyUpdateResult );
        //
        //		return getRootUsbHubImp();
    }

    /** @return The minimum API version this supports. */
    public String getApiVersion()
    {
        return WINDOWS_API_VERSION;
    }

    /** @return The version number of this implementation. */
    public String getImpVersion()
    {
        return WINDOWS_IMP_VERSION;
    }

    /** @return Get a description of this UsbServices implementation. */
    public String getImpDescription()
    {
        return WINDOWS_IMP_DESCRIPTION;
    }

    //*************************************************************************
    // Private methods

    /** Set variables from user-specified properties */
    private void checkProperties()
    {
        Properties p = null;

        try
        {
            p = UsbHostManager.getProperties();
        }
        catch (Exception e)
        {
            return;
        }

        try
        {
            if (p.containsKey(TOPOLOGY_UPDATE_DELAY_KEY))
                topologyUpdateDelay = Integer.decode(p.getProperty(TOPOLOGY_UPDATE_DELAY_KEY)).intValue();
        }
        catch (Exception e)
        {
        }

        try
        {
            if (p.containsKey(TOPOLOGY_UPDATE_NEW_DEVICE_DELAY_KEY))
                topologyUpdateNewDeviceDelay = Integer.decode(p.getProperty(TOPOLOGY_UPDATE_NEW_DEVICE_DELAY_KEY)).intValue();
        }
        catch (Exception e)
        {
        }

        try
        {
            if (p.containsKey(TOPOLOGY_UPDATE_USE_POLLING_KEY))
                topologyUpdateUsePolling = Boolean.valueOf(p.getProperty(TOPOLOGY_UPDATE_USE_POLLING_KEY)).booleanValue();
        }
        catch (Exception e)
        {
        }

        try
        {
            if (p.containsKey(TRACING_KEY))
                JavaxUsb.setTracing(Boolean.valueOf(p.getProperty(TRACING_KEY)).booleanValue());
        }
        catch (Exception e)
        {
        }

        //FIXME - the names of the tracers should be more generically processed
        try
        {
            if (p.containsKey(TRACE_DEFAULT_KEY))
                JavaxUsb.setTraceType(Boolean.valueOf(p.getProperty(TRACE_DEFAULT_KEY)).booleanValue(),"default");
        }
        catch (Exception e)
        {
        }

        try
        {
            if (p.containsKey(TRACE_HOTPLUG_KEY))
                JavaxUsb.setTraceType(
                    Boolean.valueOf(p.getProperty(TRACE_HOTPLUG_KEY))
                               .booleanValue(),
                    "hotplug");
        }
        catch (Exception e)
        {
        }

        try
        {
            if (p.containsKey(TRACE_XFER_KEY))
                JavaxUsb.setTraceType(
                    Boolean.valueOf(p.getProperty(TRACE_XFER_KEY)).booleanValue(),
                    "xfer");
        }
        catch (Exception e)
        {
        }

        try
        {
            if (p.containsKey(TRACE_URB_KEY))
                JavaxUsb.setTraceType(
                    Boolean.valueOf(p.getProperty(TRACE_URB_KEY)).booleanValue(),
                    "urb");
        }
        catch (Exception e)
        {
        }

        try
        {
            if (p.containsKey(TRACE_LEVEL_KEY))
                JavaxUsb.setTraceLevel(
                    Integer.decode(p.getProperty(TRACE_LEVEL_KEY)).intValue());
        }
        catch (Exception e)
        {
        }
    }


    /** @return If the topology listener is listening */
/*    private boolean isListening()
    {
        try
        {
            return topologyListener.isAlive();
        }
        catch (NullPointerException npE)
        {
            return false;
        }
    }
*/

    /** Start Topology Change Listener Thread */
    private void startTopologyListener()
    {
        Runnable r = null;
        String threadName = null;
                         
        if (topologyUpdateUsePolling)
        {
            threadName = "USB Topology Poller";
            if(log.isDebugEnabled())log.debug( "startTopologyListner started TopologyListner using polling with thread name <"+threadName+">" );
            r = new Runnable()
                    {
                        public void run()
                        {
                            while (true)
                            {
                                topologyUpdateMutex.acquire();
                                updateTopology();
                                topologyUpdateMutex.release();

                                try
                                {
                                    Thread.sleep(topologyUpdateDelay);
                                }
                                catch (InterruptedException iE)
                                {
                                }
                            }
                        }
                    };
        }
        else
        {
            threadName = "USB Topology Listener";
            r = new Runnable()
                    {
                        public void run()
                        {
                            topologyListenerExit(JavaxUsb.nativeTopologyListener(WindowsUsbServices.this));
                        }
                    };
        }

        topologyListener = new Thread(r);

        topologyListener.setDaemon(true);
        topologyListener.setName(threadName);

//      topologyListenerError = 0;
        topologyListener.start();
    }


    /** Update the topology and fire connect/disconnect events */
    private void updateTopology()
    {
        List connectedDevices = new ArrayList();
        List disconnectedDevices = new ArrayList();
        
        fillDeviceList(getRootUsbHubImp(),disconnectedDevices);
        while(disconnectedDevices.remove(getRootUsbHubImp()));

        int updates = JavaxUsb.nativeTopologyUpdater(this, connectedDevices, disconnectedDevices);
        if(updates == 0) return;  // if there are no changes go home early

        // if something has changed continue on

        // disconnectedDevices contains all devices removed
        Iterator iterator = disconnectedDevices.iterator();
        while(iterator.hasNext())
        {
            UsbDeviceImp device = (UsbDeviceImp) iterator.next();
            if(log.isDebugEnabled()) log.debug( "updateTopology() disconnecting device: "+device );
            device.disconnect();
            listenerImp.usbDeviceDetached(new UsbServicesEvent(this, (UsbDevice)device ));
        }

        // connectedDevices contains all new devices found
        iterator = connectedDevices.iterator();
        while(iterator.hasNext())
        {
            UsbDeviceImp device = (UsbDeviceImp) iterator.next();
            
            // fixme: setActiveConfig... is omitted to find out, whether it
            // is really needed in libusb implementation
//          setActiveConfigAndInterfaceSettings(device);

            // Let's wait a bit before each new device's event, so its driver can have some time to
            // talk to it without interruptions.  FIXME, why is this delay here?
            try
            {
                if( !(device instanceof WindowsHubOsImp) ) 
                {
                  if(log.isDebugEnabled()) log.debug( "sleeping to let new device settle" );
                  Thread.sleep(topologyUpdateNewDeviceDelay);
                }
            }
            catch (InterruptedException iE)
            {
            }
            try
            {
              if(log.isDebugEnabled()) log.debug( "updateTopology() found device: "+device.getSerialNumberString() );
            }
            catch(Exception e)
            {
            }
            if(log.isDebugEnabled()) log.debug( "updateTopology() connecting device: "+device );

            listenerImp.usbDeviceAttached(new UsbServicesEvent(this, (UsbDevice) device));
        }

        synchronized (topologyLock)
        {
            firstUpdateDone = true;
            topologyLock.notifyAll();
        }
    }


    /** Is called from native code to enqueue an update topology request. */
/*    private void topologyChange()
    {
        try
        {
            Thread.sleep(topologyUpdateDelay);
        }
        catch (InterruptedException iE)
        {
        }

        Runnable r =
            new Runnable()
            {
                public void run()
                {
                    updateTopology();
                }
            };

        topologyUpdateManager.add(r);
    }
*/

    /**
     * Called when the topology listener exits.
     * @param error The return code of the topology listener.
     */
    private void topologyListenerExit(int error)
    {
        //FIXME - disconnet all devices
//        topologyListenerError = error;

        synchronized (topologyLock)
        {
            topologyLock.notifyAll();
        }
    }

    /**
     * Check a device.
     * <p>
     * If the device exists, the existing device is removed from the disconnected list and returned.
     * If the device is new, it is added to the connected list and returned.  If the new device replaces
     * an existing device, the old device is retained in the disconnected list, and the new device is returned.
     * @param hub The parent UsbHubImp.
     * @param p The parent port number.
     * @param device The UsbDeviceImp to add.
     * @param disconnected The List of disconnected devices.
     * @param connected The List of connected devices.
     * @return The new UsbDeviceImp or existing UsbDeviceImp.
     */
/*    private UsbDeviceImp checkUsbDeviceImp(
        UsbHubImp hub,
        int p,
        UsbDeviceImp device,
        List connected,
        List disconnected)
    {
        if(log.isDebugEnabled()) log.debug("checkUsbDeviceImp() Entered with device " + device);

        byte port = (byte) p;
        UsbPortImp usbPortImp = hub.getUsbPortImp(port);

        if (null == usbPortImp)
        {
            hub.resize(port);
            usbPortImp = hub.getUsbPortImp(port);
        }

        if(log.isDebugEnabled()) log.debug("checkUsbDeviceImp() Hub now has " + hub.getNumberOfPorts() + " ports");

        if (!usbPortImp.isUsbDeviceAttached())
        {
            connected.add(device);
            device.setParentUsbPortImp(usbPortImp);
            if(log.isDebugEnabled()) log.debug("checkUsbDeviceImp() Leaving with device " + device);

            return device;
        }

        UsbDeviceImp existingDevice = usbPortImp.getUsbDeviceImp();

        if (isUsbDevicesEqual(existingDevice, device))
        {
            disconnected.remove(existingDevice);
            if(log.isDebugEnabled()) log.debug("checkUsbDeviceImp Leaving with device " + existingDevice);

            return existingDevice;
        }
        else
        {
            connected.add(device);
            device.setParentUsbPortImp(usbPortImp);
            if(log.isDebugEnabled()) log.debug("checkUsbDeviceImp Leaving with device " + device);

            return device;
        }
    }
*/
    /**
     * Return if the specified devices appear to be equal.
     * <p>
     * If either of the device's descriptors are null, this returns false.
     * @param dev1 The first device.
     * @param dev2 The second device.
     * @return If the devices appear to be equal.
     */
    protected boolean isUsbDevicesEqual(
        UsbDeviceImp dev1,
        UsbDeviceImp dev2)
    {
        try
        {
            UsbDeviceDescriptor desc1 = dev1.getUsbDeviceDescriptor();
            UsbDeviceDescriptor desc2 = dev2.getUsbDeviceDescriptor();

            return (dev1.isUsbHub() == dev1.isUsbHub())
                && (dev1.getSpeed() == dev2.getSpeed()) && desc1.equals(desc2);
        }
        catch (NullPointerException npE)
        {
            return false;
        }
    }

    //*************************************************************************
    // Instance variables
    private RunnableManager topologyUpdateManager = new RunnableManager();
//    private int topologyListenerError = 0;
//    private int topologyUpdateResult = 0;
    private Object topologyLock = new Object();

    /** We have to prevent concurrent runs of topology update */
    private Mutex topologyUpdateMutex = new Mutex();
    private Thread topologyListener = null;
    protected boolean topologyUpdateUsePolling = TOPOLOGY_UPDATE_USE_POLLING;
    protected int topologyUpdateDelay = TOPOLOGY_UPDATE_DELAY;
    protected int topologyUpdateNewDeviceDelay =
        TOPOLOGY_UPDATE_NEW_DEVICE_DELAY;
    private boolean firstUpdateDone = false;

    //*************************************************************************
    // Class constants
    public static final String COULD_NOT_ACCESS_USB_SUBSYSTEM =
        "Could not access USB subsystem.";
    public static final int MAX_FIRST_UPDATE_DELAY = 10000; /* 10 seconds */

    /* If not polling, this is the delay in ms after getting a connect/disconnect notification
     * before checking for device updates.  If polling, this is the number of ms between polls.
     */
    public static final int TOPOLOGY_UPDATE_DELAY = 5000; /* 5 seconds */
    public static final String TOPOLOGY_UPDATE_DELAY_KEY = "com.mcreations.usb.windows.WindowsUsbServices.topologyUpdateDelay";

    /* This is a delay when new devices are found, before sending the notification event that there is a new device.
     * This delay is per-device.
     */
    public static final int TOPOLOGY_UPDATE_NEW_DEVICE_DELAY = 500; /* 1/2 second per device */
    public static final String TOPOLOGY_UPDATE_NEW_DEVICE_DELAY_KEY = "com.mcreations.usb.windows.WindowsUsbServices.topologyUpdateNewDeviceDelay";

    /* Whether to use polling to wait for connect/disconnect notification */
    public static final boolean TOPOLOGY_UPDATE_USE_POLLING = true;
    public static final String TOPOLOGY_UPDATE_USE_POLLING_KEY ="com.mcreations.usb.windows.WindowsUsbServices.topologyUpdateUsePolling";

    /* The key in the properties file for this setting. */
    public static final String TRACING_KEY = "com.mcreations.usb.windows.WindowsUsbServices.JNI.tracing";
    public static final String TRACE_LEVEL_KEY = "com.mcreations.usb.windows.WindowsUsbServices.JNI.trace_level";
    public static final String TRACE_DEFAULT_KEY = "com.mcreations.usb.windows.WindowsUsbServices.JNI.trace_default";
    public static final String TRACE_HOTPLUG_KEY = "com.mcreations.usb.windows.WindowsUsbServices.JNI.trace_hotplug";
    public static final String TRACE_XFER_KEY = "com.mcreations.usb.windows.WindowsUsbServices.JNI.trace_xfer";
    public static final String TRACE_URB_KEY = "com.mcreations.usb.windows.WindowsUsbServices.JNI.trace_urb";
    public static final String WINDOWS_API_VERSION = "0.10.0";
    public static final String WINDOWS_IMP_VERSION = "0.10.0";
    public static final String WINDOWS_IMP_DESCRIPTION = "\t" + "JSR80 : javax.usb" + "\n" + "\n"
        + "Implementation for Windows 98, 2000, and XP.\n" + "\n" + "\n" + "*"
        + "\n"
        + "* Copyright (c) 1999 - 2003, International Business Machines Corporation."
        + "\n" + "* All Rights Reserved." + "\n" + "*" + "\n"
        + "* This software is provided and licensed under the terms and conditions"
        + "\n" + "* of the Common Public License:" + "\n"
        + "* http://oss.software.ibm.com/developerworks/opensource/license-cpl.html"
        + "\n" + "\n" + "http://javax-usb.org/" + "\n" + "\n";


    /**
     * Fill the List with all devices.
     * @param device The device to add.
     * @param list The list to add to.
     */
    private void fillDeviceList(UsbDeviceImp device,List list)
    {
        list.add(device);

        if (device.isUsbHub())
        {
            UsbHubImp hub = (UsbHubImp) device;

            //FIXME - Iterators can throw ConcurrentModificationException!
            Iterator iterator = hub.getAttachedUsbDevices().iterator();

            while (iterator.hasNext())
                fillDeviceList((UsbDeviceImp) iterator.next(), list);
        }
    }


    /**
     * Find and set the active config and interface settings for this device.
     * @param device The UsbDeviceImp.
     */
/*    protected void setActiveConfigAndInterfaceSettings(UsbDeviceImp device)
    {
        WindowsDeviceOsImp windowsDeviceOsImp =
            (WindowsDeviceOsImp) device.getUsbDeviceOsImp();
        int config =
            JavaxUsb.nativeGetActiveConfigurationNumber(windowsDeviceOsImp);

        if (0 < config)
            device.setActiveUsbConfigurationNumber((byte) config);
        else

            return; // either the device is unconfigured or there was an error, so we can't continue 
        Iterator interfaces =
            device.getActiveUsbConfiguration().getUsbInterfaces().iterator();

        while (interfaces.hasNext())
        {
            UsbInterfaceImp usbInterfaceImp =
                (UsbInterfaceImp) interfaces.next();
            int setting = 0;

            if (1 < usbInterfaceImp.getNumSettings())
            {
                byte interfaceNumber =
                    usbInterfaceImp.getUsbInterfaceDescriptor()
                                       .bInterfaceNumber();
                setting =
                    JavaxUsb.nativeGetActiveInterfaceSettingNumber(
                        windowsDeviceOsImp,
                        UsbUtil.unsignedInt(interfaceNumber));

                if (0 <= setting)
                    usbInterfaceImp.setActiveSettingNumber((byte) setting);
            }
            else
            {
                // If there is only one setting, just set it to the active setting. 
                usbInterfaceImp.setActiveSettingNumber(
                    usbInterfaceImp.getUsbInterfaceDescriptor()
                                       .bAlternateSetting());
            }
        }
    }
  */
}
