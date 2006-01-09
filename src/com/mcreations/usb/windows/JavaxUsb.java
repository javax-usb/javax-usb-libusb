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

import com.ibm.jusb.UsbConfigurationDescriptorImp;
import com.ibm.jusb.UsbConfigurationImp;
import com.ibm.jusb.UsbControlPipeImp;
import com.ibm.jusb.UsbDeviceDescriptorImp;
import com.ibm.jusb.UsbDeviceImp;
import com.ibm.jusb.UsbEndpointDescriptorImp;
import com.ibm.jusb.UsbEndpointImp;
import com.ibm.jusb.UsbHubImp;
import com.ibm.jusb.UsbInterfaceDescriptorImp;
import com.ibm.jusb.UsbInterfaceImp;
import com.ibm.jusb.UsbPipeImp;
import com.ibm.jusb.UsbPortImp;

import net.sf.libusb.Libusb;
import net.sf.libusb.usb_bus;
import net.sf.libusb.usb_config_descriptor;
import net.sf.libusb.usb_device;
import net.sf.libusb.usb_device_descriptor;
import net.sf.libusb.usb_endpoint_descriptor;
import net.sf.libusb.usb_interface;
import net.sf.libusb.usb_interface_descriptor;
import net.sf.libusb.usb_version;
import net.sf.libusb.usb_version_dll;
import net.sf.libusb.usb_version_driver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.usb.UsbConst;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbException;
import javax.usb.UsbPlatformException;


/**
 * Interface to/from JNI.
 * @author Dan Streetman
 * @author Kambiz Darabi
 */
class JavaxUsb
{
    public static final String VERSION = "0.9.0";

    //*************************************************************************
    // Public methods
    private static boolean initialised;
    private static boolean tracing = true;
    private static Log log = LogFactory.getLog(JavaxUsb.class);

    /** Since libusb is not thread safe, we have to synchronise access */
    private static Mutex mutex = new Mutex();

    // FIXME remove this debug field
    private static int numberOfRecognizedDevices = 0;

    private static void printEndpoint(usb_endpoint_descriptor endpoint)
    {
        String type;
        int typ =
            ((byte) endpoint.getBmAttributes()) & UsbConst.ENDPOINT_TYPE_MASK;

        switch (typ)
        {
            case UsbConst.ENDPOINT_TYPE_BULK :
                type = "bulk";

                break;

            case UsbConst.ENDPOINT_TYPE_CONTROL :
                type = "control";

                break;

            case UsbConst.ENDPOINT_TYPE_INTERRUPT :
                type = "interrupt";

                break;

            case UsbConst.ENDPOINT_TYPE_ISOCHRONOUS :
                type = "isochronous";

                break;

            default :
                type = "unknown (" + typ + ")";

                break;
        }

        ;

        String direction;
        int dir =
            ((byte) endpoint.getBEndpointAddress())
                & UsbConst.ENDPOINT_DIRECTION_MASK;

        switch (dir)
        {
            case UsbConst.ENDPOINT_DIRECTION_IN :
                direction = "in";

                break;

            case UsbConst.ENDPOINT_DIRECTION_OUT :
                direction = "out";

                break;

            default :
                direction = "unknown (" + dir + ")";

                break;
        }

        ;
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "log",
            "-- Endpoint - address = " + endpoint.getBEndpointAddress()
            + " , type = " + type + " , direction = " + direction);
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "log",
            "      bEndpointAddress: " + endpoint.getBEndpointAddress());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "log",
            "      bDescriptorType:     " + endpoint.getBDescriptorType());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "log",
            "      bmAttributes:     " + endpoint.getBmAttributes());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "log",
            "      wMaxPacketSize:   " + endpoint.getWMaxPacketSize());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "log",
            "      bInterval:        " + endpoint.getBInterval());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "log",
            "      bRefresh:         " + endpoint.getBRefresh());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "log",
            "      bSynchAddress:    " + endpoint.getBSynchAddress());
    }

    private static void printAltsetting(usb_interface_descriptor iface)
    {
        int i;

        log(
            LOG_HOTPLUG, DEBUG, CLASS, "log",
            "    bInterfaceNumber:   " + iface.getBInterfaceNumber());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "log",
            "    bAlternateSetting:  " + iface.getBAlternateSetting());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "log",
            "    bNumEndpoints:      " + iface.getBNumEndpoints());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "log",
            "    bInterfaceClass:    " + iface.getBInterfaceClass());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "log",
            "    bInterfaceSubClass: " + iface.getBInterfaceSubClass());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "log",
            "    bInterfaceProtocol: " + iface.getBInterfaceProtocol());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "log",
            "    iInterface:         " + iface.getIInterface());

        for (i = 0; i < iface.getBNumEndpoints(); i++)
        {
            usb_endpoint_descriptor epDesc;

            epDesc =
                Libusb.usb_endpoint_descriptor_index(
                    iface.getEndpoint(),
                    i);
            printEndpoint(epDesc);
        }
    }

    private static void printInterface(usb_interface iface)
    {
        int i;

        for (i = 0; i < iface.getNum_altsetting(); i++)
        {
            usb_interface_descriptor ifDesc;
            ifDesc =
                Libusb.usb_interface_descriptor_index(
                    iface.getAltsetting(),
                    i);
            printAltsetting(ifDesc);
        }
    }

    private static void printConfiguration(usb_config_descriptor config)
    {
        int i;

        log(
            LOG_HOTPLUG, DEBUG, CLASS, "log",
            "  wTotalLength:         " + config.getWTotalLength());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "log",
            "  bNumInterfaces:       " + config.getBNumInterfaces());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "log",
            "  bConfigurationValue:  " + config.getBConfigurationValue());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "log",
            "  iConfiguration:       " + config.getIConfiguration());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "log",
            "  bmAttributes:         " + config.getBmAttributes());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "log",
            "  MaxPower:             " + config.getMaxPower());

        for (i = 0; i < config.getBNumInterfaces(); i++)
        {
            usb_interface iface;
            iface = Libusb.usb_interface_index(
                    config.getInterface(),
                    i);
            printInterface(iface);
        }
    }

    /**
     * Log messages to System.err, if the level <= current traceLevel.
     * @param logname The logger name, one of:
     * <ul>
     * <li>{@link #LOG_DEFAULT}
     * <li>{@link #LOG_HOTPLUG}
     * <li>{@link #LOG_XFER}
     * <li>{@link #LOG_URB}
     * </ul>
     * @param level The log level, one of the following (with increasing verbosity):
     * <ul>
     * <li>{@link #CRITICAL}
     * <li>{@link #ERROR}
     * <li>{@link #INFO}
     * <li>{@link #FUNC}
     * <li>{@link #DEBUG}
     * <li>{@link #OTHER}
     * </ul>
     * @param file The filename.
     * @param func The function.
     * @param line The line number.
     * @param msg The log message.
     */
    public static void log(
        String logname,
        int level,
        String file,
        String func,
        String msg)
    {
        switch (level)
        {
            case CRITICAL :
                log.fatal(msg);

                break;

            case ERROR :
                log.error(msg);

                break;

            case INFO :
                log.info(msg);

                break;

            case FUNC :
                log.trace(msg);

                break;

            case DEBUG :
                log.debug(msg);

                break;

            default :
                log.debug(msg);

                break;
        }

        //        System.err.println(
        //                "J:[" + logname + "](" + level + ") " + file + "." + func
        //                + " - " + msg);
    }

    public static void initialise()
        throws UsbException
    {
        String method = "initialise";
        log(LOG_HOTPLUG, FUNC, CLASS, method, "Entering initialise");

        if (initialised)
        {
            log(
                LOG_HOTPLUG, FUNC, CLASS, method,
                "Already initialised. Leaving initialise");

            return;
        }

        loadLibrary();

        // we want to access libusb exclusively
        mutex.acquire();

        usb_version version;
        try
        {
            Libusb.usb_init();

            if (traceLevel >= OTHER)
            {
                //  debug levels:   
                //    0: no debug messages
                //    1: only errors
                //    3: warnings + errors
                //    4 and higher: errors, warnings, normal messages
                Libusb.usb_set_debug(255);
            }

            version = Libusb.usb_get_version();
        }
        finally
        {
            mutex.release();
        }

        usb_version_dll dllVersion = version.getDll();

        String msg =
            "Libusb-Win32 DLL version:\t" + dllVersion.getMajor() + "."
            + dllVersion.getMinor() + "." + dllVersion.getMicro() + "."
            + dllVersion.getNano();

        log(LOG_DEFAULT, DEBUG, "JavaxUsb", "init", msg);

        usb_version_driver driverVersion = version.getDriver();

        // When using the filter driver: if no USB device is attached, 
        // the result is -1, even when the filter driver service is running
        if (driverVersion.getMajor() != -1)
        {
            msg = "Libusb-Win32 service driver version:\t"
                + driverVersion.getMajor() + "." + driverVersion.getMinor()
                + "." + driverVersion.getMicro() + "."
                + driverVersion.getNano();
            log(LOG_DEFAULT, DEBUG, CLASS, method, msg);
        }

        initialised = true;
    }

    /** Load native library */
    private static void loadLibrary()
        throws UsbException
    {
        if (libraryLoaded)
            return;

        try
        {
            System.loadLibrary(LIBRARY_NAME);
        }
        catch (Exception e)
        {
            throw new UsbException(
                EXCEPTION_WHILE_LOADING_SHARED_LIBRARY + " "
                + System.mapLibraryName(LIBRARY_NAME) + " : " + e.getMessage());
        }
        catch (Error e)
        {
            throw new UsbException(
                ERROR_WHILE_LOADING_SHARED_LIBRARY + " "
                + System.mapLibraryName(LIBRARY_NAME) + " : " + e.getMessage());
        }
    }

    /**
     * Convert the error code to a UsbException.
     * @param error The error code.
     * @return A UsbException.
     */
    public static UsbException errorToUsbException(int error)
    {
        return new UsbException(nativeGetErrorMessage(error));
    }

    /**
     * Convert the error code to a UsbException using the specified text.
     * <p>
     * The string is prepended to the detail message with a colon separating
     * the specified text from the error message.
     * @param error The error code.
     * @param string The string to use in the UsbException.
     * @return A UsbException.
     */
    public static UsbException errorToUsbException(
        int error,
        String string)
    {
        return new UsbException(string + " : " + nativeGetErrorMessage(error));
    }

    //*************************************************************************
    // Native methods
    //*********************************
    // JavaxUsbTopologyListener methods

    /**
     * Call the native function that listens for topology changes
     * @param services The WindowsUsbServices instance.
     * @return The error that caused the listener to exit.
     */
    static native int nativeTopologyListener(WindowsUsbServices services);

    //*********************************
    // JavaxUsbError methods

    /**
     * @param error the error number
     * @return the message associated with the specified error number
     */
    static native String nativeGetErrorMessage(int error);

    /**
     * @param windowsDeviceOsImp
     * @return
     */
    static native int nativeGetActiveConfigurationNumber(
        WindowsDeviceOsImp windowsDeviceOsImp);

    /**
     * @param windowsDeviceOsImp
     * @param i
     * @return
     */
    static native int nativeGetActiveInterfaceSettingNumber(
        WindowsDeviceOsImp windowsDeviceOsImp,
        int i);

    /**
     * Return if the specified devices appear to be equal.
     * <p>
     * If either of the device's descriptors are null, this returns false.
     * @param dev1 The first device.
     * @param dev2 The second device.
     * @return If the devices appear to be equal.
     */
    protected static boolean isUsbDevicesEqual(
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

    /**
     * Check for the existence of a device in the given <code>connected</code> and
     * <code>disconnected</code> lists.
     * <p>
     * If the device exists, the existing device is removed from the disconnected list and the new
     * device added to the connected list: that means, that the device was disconnected and then
     * reconnected, because the main loop in {@link JavaxUsb#topologyUpdater} only calls
     * {@link JavaxUsb#buildDevice} in case of changes in the topology (Libusb.usb_find_devices != 0).
     * <p>
     * If the device is new, it is added to the connected list and returned.  If the new device replaces
     * an existing device, the old device is retained in the disconnected list, and the new device is returned.
     * @param hub The parent UsbHubImp.
     * @param p The parent port number.
     * @param device The UsbDeviceImp to add.
     * @param disconnected The List of disconnected devices.
     * @param connected The List of connected devices.
     * @return The new UsbDeviceImp or existing UsbDeviceImp.
     */
    private static UsbDeviceImp checkUsbDeviceImp(
        UsbHubImp hub,
        int p,
        UsbDeviceImp device,
        List connected,
        List disconnected)
    {
        String meth = "checkUsbDeviceImp";
        log(LOG_HOTPLUG, FUNC, CLASS, meth, "Entered with device " + device);

        byte port = (byte) p;
        UsbPortImp usbPortImp = hub.getUsbPortImp(port);

        if (null == usbPortImp)
        {
            hub.resize(port);
            usbPortImp = hub.getUsbPortImp(port);
        }

        log(
            LOG_HOTPLUG, DEBUG, CLASS, meth,
            "Hub now has " + hub.getNumberOfPorts() + " ports");

        UsbDeviceImp existingDevice = usbPortImp.getUsbDeviceImp();

        // If they are equal, it means, that the
        // device was disconnected and then reconnected,
        // because the main loop in topologyUpdater only
        // calls buildDevice in case of changes in the
        // topology (Libusb.usb_find_devices != 0).
        if (isUsbDevicesEqual(existingDevice, device))
        {
            disconnected.remove(existingDevice);
            log(
                LOG_HOTPLUG, FUNC, CLASS, meth,
                "Removed disconnected device " + existingDevice);
        }

        if (usbPortImp.isUsbDeviceAttached())
        {
            usbPortImp.detachUsbDeviceImp(usbPortImp.getUsbDeviceImp());
        }

        connected.add(device);
        device.setParentUsbPortImp(usbPortImp);
        log(LOG_HOTPLUG, FUNC, CLASS, meth, "Leaving with device " + device);

        return device;
    }

    private static void buildConfig(
        UsbDeviceImp usbDev,
        usb_config_descriptor config)
    {
        //	    (*env)->CallStaticObjectMethod(
        //	            env, JavaxUsb, createUsbConfigurationImp, usbDeviceImp,
        //	            config_desc->bLength, config_desc->bDescriptorType, 
        //	            config_desc->wTotalLength, config_desc->bNumInterfaces, 
        //	            config_desc->bConfigurationValue, config_desc->iConfiguration,
        //	            config_desc->bmAttributes, config_desc->MaxPower, is_active);
        UsbConfigurationDescriptorImp desc =
            new UsbConfigurationDescriptorImp(
                (byte) config.getBLength(), (byte) config.getBDescriptorType(),
                (short) config.getWTotalLength(),
                (byte) config.getBNumInterfaces(),
                (byte) config.getBConfigurationValue(),
                (byte) config.getIConfiguration(),
                (byte) config.getBmAttributes(), (byte) config.getMaxPower());

        UsbConfigurationImp usbConfig = new UsbConfigurationImp(usbDev, desc);
        usbDev.addUsbConfigurationImp(usbConfig);

        for (int i = 0; i < config.getBNumInterfaces(); i++)
        {
            usb_interface iface;
            iface = Libusb.usb_interface_index(
                    config.getInterface(),
                    i);

            for (int j = 0; j < iface.getNum_altsetting(); j++)
            {
                usb_interface_descriptor ifaceDesc;

                ifaceDesc =
                    Libusb.usb_interface_descriptor_index(
                        iface.getAltsetting(),
                        j);
                buildInterface(usbConfig, ifaceDesc);
            }
        }

        // FIXME: where do we set the active configuration ?
        //			to retrieve it, we have to use a request
        // if (active)
        //     device.setActiveUsbConfigurationNumber(configValue);
    }

    /**
     * @param usbConfig
     * @param ifaceDesc
     */
    private static void buildInterface(
        UsbConfigurationImp usbConfig,
        usb_interface_descriptor ifaceDesc)
    {
        log(
            LOG_HOTPLUG, FUNC, CLASS, "buildInterface",
            "Entering with config " + usbConfig);

        UsbInterfaceDescriptorImp desc =
            new UsbInterfaceDescriptorImp(
                (byte) ifaceDesc.getBLength(),
                (byte) ifaceDesc.getBDescriptorType(),
                (byte) ifaceDesc.getBInterfaceNumber(),
                (byte) ifaceDesc.getBAlternateSetting(),
                (byte) ifaceDesc.getBNumEndpoints(),
                (byte) ifaceDesc.getBInterfaceClass(),
                (byte) ifaceDesc.getBInterfaceSubClass(),
                (byte) ifaceDesc.getBInterfaceProtocol(),
                (byte) ifaceDesc.getIInterface());

        UsbInterfaceImp iface = new UsbInterfaceImp(usbConfig, desc);

        boolean active = ((0 == ifaceDesc.getBAlternateSetting())
            ? true
            : false);

        /* If the config is not active, neither are its interface settings */
        if (usbConfig.isActive() && active)
            iface.setActiveSettingNumber(
                iface.getUsbInterfaceDescriptor().bAlternateSetting());

        WindowsDeviceOsImp windowsDeviceOsImp =
            (WindowsDeviceOsImp) iface.getUsbConfigurationImp().getUsbDeviceImp();
        WindowsInterfaceOsImp windowsInterfaceOsImp =
            new WindowsInterfaceOsImp(iface, windowsDeviceOsImp);
        iface.setUsbInterfaceOsImp(windowsInterfaceOsImp);

        for (int i = 0; i < ifaceDesc.getBNumEndpoints(); i++)
        {
            buildEndpoint(
                iface,
                Libusb.usb_endpoint_descriptor_index(
                    ifaceDesc.getEndpoint(),
                    i));
        }

        log(
            LOG_HOTPLUG, FUNC, CLASS, "buildInterface",
            "Leaving with interface " + iface);
    }

    /**
     * @param iface
     * @param usb_endpoint_descriptor
     */
    private static void buildEndpoint(
        UsbInterfaceImp iface,
        usb_endpoint_descriptor endPointDesc)
    {
        UsbEndpointDescriptorImp desc =
            new UsbEndpointDescriptorImp(
                (byte) endPointDesc.getBLength(),
                (byte) endPointDesc.getBDescriptorType(),
                (byte) endPointDesc.getBEndpointAddress(),
                (byte) endPointDesc.getBmAttributes(),
                (byte) endPointDesc.getBInterval(),
                (short) endPointDesc.getWMaxPacketSize());

        UsbEndpointImp ep = new UsbEndpointImp(iface, desc);
        UsbPipeImp pipe = null;

        WindowsInterfaceOsImp windowsInterfaceOsImp =
            (WindowsInterfaceOsImp) iface.getUsbInterfaceOsImp();

        switch ((int) ep.getType())
        {
            // FIXME we use simple WindowsPipeOsImp for control and isochronous endpoint
            //		is this allowed?
            //		case UsbConst.ENDPOINT_TYPE_CONTROL:
            //			pipe = new UsbControlPipeImp( ep, null );
            //			pipe.setUsbPipeOsImp( new WindowsControlPipeImp( (UsbControlPipeImp)pipe, windowsInterfaceOsImp ) );
            //			break;
            //		case UsbConst.ENDPOINT_TYPE_ISOCHRONOUS:
            //			pipe = new UsbPipeImp( ep, null );
            //			pipe.setUsbPipeOsImp( new WindowsIsochronousPipeImp( pipe, windowsInterfaceOsImp ) );
            //			break;
            case UsbConst.ENDPOINT_TYPE_CONTROL :
                pipe = new UsbControlPipeImp(ep, null);
                pipe.setUsbPipeOsImp(
                    new WindowsPipeOsImp(
                        (UsbControlPipeImp) pipe, windowsInterfaceOsImp));

                break;

            case UsbConst.ENDPOINT_TYPE_ISOCHRONOUS :
                pipe = new UsbPipeImp(ep, null);
                pipe.setUsbPipeOsImp(
                    new WindowsPipeOsImp(pipe, windowsInterfaceOsImp));

                break;

            case UsbConst.ENDPOINT_TYPE_BULK :
                pipe = new UsbPipeImp(ep, null);
                pipe.setUsbPipeOsImp(
                    new WindowsPipeOsImp(pipe, windowsInterfaceOsImp));

                break;

            case UsbConst.ENDPOINT_TYPE_INTERRUPT :
                pipe = new UsbPipeImp(ep, null);
                pipe.setUsbPipeOsImp(
                    new WindowsPipeOsImp(pipe, windowsInterfaceOsImp));

                break;

            default :

                //FIXME - log?
                throw new RuntimeException(
                    "Invalid UsbEndpoint type " + ep.getType());
        }
    }

    /**
     * Creates a UsbDevice or UsbHub, depending on the type of
     * argument dev, fills in the values from the config of dev
     * and checks, whether this device
     * @param dev
     * @param bus
     * @param parentHub
     * @param parentport
     * @param connectedDevices
     * @param disconnectedDevices
     */
    static void buildDevice(
        usb_device dev,
        usb_bus bus,
        UsbHubImp parentHub,
        int parentport,
        List connectedDevices,
        List disconnectedDevices)
    {
        String meth = "buildDevice";
        log(
            LOG_HOTPLUG, FUNC, CLASS, meth,
            "Entered buildDevice with device " + dev.getFilename());

        UsbDeviceImp usbDev;

        if (dev.getDescriptor().getBDeviceClass() == Libusb.USB_CLASS_HUB)
        {
            log(LOG_HOTPLUG, DEBUG, CLASS, meth, "Device is a hub.");

            // note that libusb doesn't give us information about
            // the number of ports
            usbDev = new WindowsHubOsImp(dev);
        }
        else
        {
            log(LOG_HOTPLUG, DEBUG, CLASS, meth, "Device is NOT a hub.");
            usbDev = new WindowsDeviceOsImp(dev);
        }

        // libusb doesn't tell the speed
        usbDev.setSpeed(UsbConst.DEVICE_SPEED_UNKNOWN);

        // now build all configurations
        for (int i = 0; i < dev.getDescriptor().getBNumConfigurations(); i++)
        {
            buildConfig(
                usbDev,
                Libusb.usb_config_descriptor_index(
                    dev.getConfig(),
                    i));
        }

        checkUsbDeviceImp(
            parentHub, parentport, usbDev, connectedDevices, disconnectedDevices);

        log.debug("Leaving buildDevice with device " + dev.getFilename());
    }

    /**
     * @param services
     * @param connectedDevices
     * @param disconnectedDevices
     * @return
     */
    static int nativeTopologyUpdater(
        WindowsUsbServices services,
        List connectedDevices,
        List disconnectedDevices)
    {
        String method = "topologyUpdater";
        log(LOG_DEFAULT, FUNC, CLASS, method, "Entering topologyUpdater.");

        UsbHubImp rootHub = services.getRootUsbHubImp();

        if (rootHub == null)
        {
            // this shouldn't happen, as the root hub is setup during initialisation
            throw new RuntimeException(
                "The (virtual) root hub couldn't be retrieved.");
        }

        // acquire a lock, so we don't interfere with others
        // trying to access libusb
        mutex.acquire();

        try
        {
            int busCount = Libusb.usb_find_busses();
            String msg = "Found " + busCount + " new busses.";
            log(LOG_HOTPLUG, DEBUG, CLASS, method, msg);

            int deviceCount = Libusb.usb_find_devices();
            msg = "Found " + deviceCount + " new devices.";
            log(LOG_HOTPLUG, DEBUG, CLASS, method, msg);

            if (deviceCount == 0)
            {
                // nothing has changed, so all devices which
                // were present before (they are in disconnectedDevices)
                // are still present, now
                for (int i = 0; i < disconnectedDevices.size(); i++)
                {
                    connectedDevices.add(disconnectedDevices.get(i));
                    disconnectedDevices.remove(i);
                }
                return 0;
            }

            usb_bus bus = Libusb.usb_get_busses();

            int portNum = 1;

            while (bus != null)
            {
                msg = "Scanning bus " + bus.getDirname();
                log(LOG_HOTPLUG, DEBUG, CLASS, method, msg);

                usb_device dev = bus.getDevices();

                int index = 1;

                while (dev != null)
                {
                    usb_device_descriptor devDesc = dev.getDescriptor();
                    msg = "Device: " + dev.getFilename();
                    log(LOG_HOTPLUG, DEBUG, CLASS, method, msg);

                    if (dev.getConfig() == null)
                    {
                        log(
                            LOG_HOTPLUG, ERROR, CLASS, method,
                            "Couldn't retrieve descriptors for device '"
                            + bus.getDirname() + "/" + dev.getFilename() + "'");

                        dev = dev.getNext();

                        continue;
                    }

                    for (
                        int i = 0;
                            i < dev.getDescriptor().getBNumConfigurations();
                            i++)
                    {
                        usb_config_descriptor config;
                        config =
                            Libusb.usb_config_descriptor_index(
                                dev.getConfig(),
                                i);
                        printConfiguration(config);
                    }

                    buildDevice(
                        dev, bus, rootHub, portNum++, connectedDevices,
                        disconnectedDevices);

                    dev = dev.getNext();
                }

                bus = bus.getNext();
            }
        }
        finally
        {
            mutex.release();
        }

        return -1;
    }

    /**
     * Converts the bytes in argument buf to a String
     * in default encoding while removing trailing <code>zero</code>
     * bytes.
     * @param buf
     * @return
     */
    static String bytes2String(byte[] buf)
    {
        int i;

        for (i = 0; i < buf.length; i++)
            if (buf[i] == 0)
                break;

        return new String(buf, 0, i);
    }

    //*************************************************************************
    // Creation methods

    /** @return A new UsbConfigurationImp */
    private static UsbConfigurationImp createUsbConfigurationImp(
        UsbDeviceImp device,
        byte length,
        byte type,
        short totalLen,
        byte numInterfaces,
        byte configValue,
        byte configIndex,
        byte attributes,
        byte maxPowerNeeded,
        boolean active)
    {
        /* BUG - Java (IBM JVM at least) does not handle certain JNI byte -> Java byte (or shorts) */
        /* Email ddstreet@ieee.org for more info */
        length += 0;
        type += 0;
        numInterfaces += 0;
        configValue += 0;
        configIndex += 0;
        attributes += 0;
        maxPowerNeeded += 0;

        UsbConfigurationDescriptorImp desc =
            new UsbConfigurationDescriptorImp(
                length, type, totalLen, numInterfaces, configValue, configIndex,
                attributes, maxPowerNeeded);

        UsbConfigurationImp config = new UsbConfigurationImp(device, desc);

        if (active)
            device.setActiveUsbConfigurationNumber(configValue);

        return config;
    }

    /** @return A new UsbEndpointImp */
    private static UsbEndpointImp createUsbEndpointImp(
        UsbInterfaceImp iface,
        byte length,
        byte type,
        byte endpointAddress,
        byte attributes,
        byte interval,
        short maxPacketSize)
    {
        /* BUG - Java (IBM JVM at least) does not handle certain JNI byte -> Java byte (or shorts) */
        /* Email ddstreet@ieee.org for more info */
        length += 0;
        type += 0;
        endpointAddress += 0;
        attributes += 0;
        interval += 0;
        maxPacketSize += 0;

        UsbEndpointDescriptorImp desc =
            new UsbEndpointDescriptorImp(
                length, type, endpointAddress, attributes, interval,
                maxPacketSize);

        UsbEndpointImp ep = new UsbEndpointImp(iface, desc);
        UsbPipeImp pipe = null;

        WindowsInterfaceOsImp windowsInterfaceOsImp =
            (WindowsInterfaceOsImp) iface.getUsbInterfaceOsImp();

        switch (ep.getType())
        {
            // FIXME we use simple WindowsPipeOsImp for control and isochronous endpoint
            //		is this allowed?
            //		case UsbConst.ENDPOINT_TYPE_CONTROL:
            //			pipe = new UsbControlPipeImp( ep, null );
            //			pipe.setUsbPipeOsImp( new WindowsControlPipeImp( (UsbControlPipeImp)pipe, windowsInterfaceOsImp ) );
            //			break;
            //		case UsbConst.ENDPOINT_TYPE_ISOCHRONOUS:
            //			pipe = new UsbPipeImp( ep, null );
            //			pipe.setUsbPipeOsImp( new WindowsIsochronousPipeImp( pipe, windowsInterfaceOsImp ) );
            //			break;
            case UsbConst.ENDPOINT_TYPE_CONTROL :
                pipe = new UsbControlPipeImp(ep, null);
                pipe.setUsbPipeOsImp(
                    new WindowsPipeOsImp(
                        (UsbControlPipeImp) pipe, windowsInterfaceOsImp));

                break;

            case UsbConst.ENDPOINT_TYPE_ISOCHRONOUS :
                pipe = new UsbPipeImp(ep, null);
                pipe.setUsbPipeOsImp(
                    new WindowsPipeOsImp(pipe, windowsInterfaceOsImp));

                break;

            case UsbConst.ENDPOINT_TYPE_BULK :
                pipe = new UsbPipeImp(ep, null);
                pipe.setUsbPipeOsImp(
                    new WindowsPipeOsImp(pipe, windowsInterfaceOsImp));

                break;

            case UsbConst.ENDPOINT_TYPE_INTERRUPT :
                pipe = new UsbPipeImp(ep, null);
                pipe.setUsbPipeOsImp(
                    new WindowsPipeOsImp(pipe, windowsInterfaceOsImp));

                break;

            default :

                //FIXME - log?
                throw new RuntimeException(
                    "Invalid UsbEndpoint type " + ep.getType());
        }

        return ep;
    }

    //*************************************************************************
    // Setup methods
    private static UsbDeviceDescriptor configureUsbDeviceImp(
        UsbDeviceImp targetDevice,
        byte length,
        byte type,
        byte deviceClass,
        byte deviceSubClass,
        byte deviceProtocol,
        byte maxDefaultEndpointSize,
        byte manufacturerIndex,
        byte productIndex,
        byte serialNumberIndex,
        byte numConfigs,
        short vendorId,
        short productId,
        short bcdDevice,
        short bcdUsb,
        int speed)
    {
        /* BUG - Java (IBM JVM at least) does not handle certain JNI byte -> Java byte (or shorts) */
        /* Email ddstreet@ieee.org for more info */
        length += 0;
        type += 0;
        deviceClass += 0;
        deviceSubClass += 0;
        deviceProtocol += 0;
        maxDefaultEndpointSize += 0;
        manufacturerIndex += 0;
        productIndex += 0;
        serialNumberIndex += 0;
        numConfigs += 0;
        vendorId += 0;
        productId += 0;
        bcdDevice += 0;
        bcdUsb += 0;

        UsbDeviceDescriptorImp desc =
            new UsbDeviceDescriptorImp(
                length, type, bcdUsb, deviceClass, deviceSubClass,
                deviceProtocol, maxDefaultEndpointSize, vendorId, productId,
                bcdDevice, manufacturerIndex, productIndex, serialNumberIndex,
                numConfigs);

        targetDevice.setUsbDeviceDescriptor(desc);

        switch (speed)
        {
            case SPEED_LOW :
                targetDevice.setSpeed(UsbConst.DEVICE_SPEED_LOW);

                break;

            case SPEED_FULL :
                targetDevice.setSpeed(UsbConst.DEVICE_SPEED_FULL);

                break;

            case SPEED_UNKNOWN :
                targetDevice.setSpeed(UsbConst.DEVICE_SPEED_UNKNOWN);

                break;

            default :

                /* log */
                targetDevice.setSpeed(UsbConst.DEVICE_SPEED_UNKNOWN);

                break;
        }

        return desc;
    }

    /**
     * Connect a device to a hub's port.
     * @param hub The parent hub.
     * @param port The parent port.
     * @param device The device to connect.
     */
    public static void connectUsbDeviceImp(
        UsbHubImp hub,
        int port,
        UsbDeviceImp device)
    {
        try
        {
            device.connect(hub, (byte) port);
        }
        catch (UsbException uE)
        {
            log(
                LOG_HOTPLUG, ERROR, "JavaxUsb", "connectUsbDeviceImp",
                "UsbException while connecting : " + uE.toString());
        }
    }

    //*********************************
    // Tracing/Logging methods

    /**
     * Set the level of tracing, between 0 (only critical errors)
     * and 5 (debugging and other information).
     * @param level The level of tracing.
     */
    static void setTraceLevel(int level)
    {
        traceLevel = level;
    }

    /**
     * Enable (or disable) tracing of a certain type of data. Cf. javax.usb.properties
     * for details.
     *
     * @param enable If tracing of data should be enabled.
     * @param type The type of data (one of "xfer", "urb", "hotplug", or "default")
     */
    static void setTraceType(
        boolean enable,
        String type)
    {
        msgLevelTable.put(type, enable
            ? Boolean.TRUE
            : Boolean.FALSE);
    }

    /**
     * Enable (or disable) tracing.
     * @param enable If tracing of data should be enabled.
     */
    static void setTracing(boolean enable)
    {
        tracing = enable;
    }

    //*************************************************************************
    // Class variables
    private static boolean libraryLoaded = false;
    private static Hashtable msgLevelTable = new Hashtable();
    private static int traceLevel;

    //*************************************************************************
    // Class constants
    public static final String LIBRARY_NAME = "LibusbJNI";
    public static final String ERROR_WHILE_LOADING_SHARED_LIBRARY =
        "Error while loading shared library";
    public static final String EXCEPTION_WHILE_LOADING_SHARED_LIBRARY =
        "Exception while loading shared library";
    private static final int SPEED_UNKNOWN = 0;
    private static final int SPEED_LOW = 1;
    private static final int SPEED_FULL = 2;
    public static final String LOG_DEFAULT = "default";
    public static final String LOG_XFER = "xfer";
    public static final String LOG_HOTPLUG = "hotplug";
    public static final String LOG_URB = "urb";

    /** Used for logging */
    private static final String CLASS = "JavaxUsb";

    /**
     * Level parameter to {@link #log()} for
     * critical error messages, this is the default.
     */
    public static final int CRITICAL = 0;

    /**
     * Level parameter to {@link #log()} for
     * error messages.
     */
    public static final int ERROR = 1;

    /**
     * Level parameter to {@link #log()} for
     * general information about progess etc.
     */
    public static final int INFO = 2;

    /**
     * Level parameter to {@link #log()} for
     * function entry/exit.
     */
    public static final int FUNC = 3;

    /**
     * Level parameter to {@link #log()} for
     * debug messages.
     */
    public static final int DEBUG = 4;

    /**
     * Level parameter to {@link #log()} for
     * all other (very detailed) logging (including logging of libusb calls).
     */
    public static final int OTHER = 5;
    public static final int REQUEST_PIPE = 1;
    public static final int REQUEST_SET_INTERFACE = 2;
    public static final int REQUEST_SET_CONFIGURATION = 3;
    public static final int REQUEST_CLAIM_INTERFACE = 4;
    public static final int REQUEST_IS_CLAIMED_INTERFACE = 5;
    public static final int REQUEST_RELEASE_INTERFACE = 6;
    public static final int REQUEST_ISOCHRONOUS = 7;

    // used in native part AND in WindowsPipeRequest
    static final int PIPE_CONTROL = 1;
    static final int PIPE_BULK = 2;
    static final int PIPE_INTERRUPT = 3;
    static final int PIPE_ISOCHRONOUS = 4;

    /**
     * @return
     */
    public static int getIoTimeout()
    {
        // TODO Make this configurable
        return 5000;
    }

    static Mutex getMutex()
    {
        return mutex;
    }
}
