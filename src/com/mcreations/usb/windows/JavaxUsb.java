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
//import net.sf.libusb.usb_device_descriptor;
import net.sf.libusb.usb_endpoint_descriptor;
import net.sf.libusb.usb_interface;
import net.sf.libusb.usb_interface_descriptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Hashtable;
import java.util.List;
import java.util.Iterator;

import javax.usb.UsbConst;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbException;
import javax.usb.UsbDisconnectedException;

import java.io.UnsupportedEncodingException;

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
    private static Log log = LogFactory.getLog(JavaxUsb.class);

    /** Since libusb is not thread safe, we have to synchronise access */
    private static Mutex mutex = new Mutex();

/*
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
            LOG_HOTPLUG, DEBUG, CLASS, "printEndpoint",
            "-- Endpoint - address = " + endpoint.getBEndpointAddress()
            + " , type = " + type + " , direction = " + direction);
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "printEndpoint",
            "      bEndpointAddress: " + endpoint.getBEndpointAddress());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "printEndpoint",
            "      bDescriptorType:     " + endpoint.getBDescriptorType());
//        log(
//            LOG_HOTPLUG, DEBUG, CLASS, "printEndpoint",
//            "      bmAttributes:     " + endpoint.getBmAttributes());
//        log(
//            LOG_HOTPLUG, DEBUG, CLASS, "printEndpoint",
//            "      wMaxPacketSize:   " + endpoint.getWMaxPacketSize());
//        log(
//            LOG_HOTPLUG, DEBUG, CLASS, "printEndpoint",
//            "      bInterval:        " + endpoint.getBInterval());
//        log(
//            LOG_HOTPLUG, DEBUG, CLASS, "printEndpoint",
//            "      bRefresh:         " + endpoint.getBRefresh());
//        log(
//            LOG_HOTPLUG, DEBUG, CLASS, "printEndpoint",
//            "      bSynchAddress:    " + endpoint.getBSynchAddress());
    }
*/
/*
    private static void printIfaceDesc(usb_interface_descriptor iface)
    {
        int i;

        log(
            LOG_HOTPLUG, DEBUG, CLASS, "printIfaceDesc",
            "    bInterfaceNumber:   " + iface.getBInterfaceNumber());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "printIfaceDesc",
            "    bAlternateSetting:  " + iface.getBAlternateSetting());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "printIfaceDesc",
            "    bNumEndpoints:      " + iface.getBNumEndpoints());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "printIfaceDesc",
            "    bInterfaceClass:    " + iface.getBInterfaceClass());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "printIfaceDesc",
            "    bInterfaceSubClass: " + iface.getBInterfaceSubClass());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "printIfaceDesc",
            "    bInterfaceProtocol: " + iface.getBInterfaceProtocol());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "printIfaceDesc",
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
*/
/*    private static void printInterface(usb_interface iface)
    {
        int i;

        for (i = 0; i < iface.getNum_altsetting(); i++)
        {
            usb_interface_descriptor ifDesc;
            ifDesc = Libusb.usb_interface_descriptor_index(iface.getAltsetting(),i);
            printIfaceDesc(ifDesc);
        }
    }
*/
/*    private static void printConfiguration(usb_config_descriptor config)
    {
        int i;

        log(
            LOG_HOTPLUG, DEBUG, CLASS, "printConfig",
            "  wTotalLength:         " + config.getWTotalLength());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "printConfig",
            "  bNumInterfaces:       " + config.getBNumInterfaces());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "printConfig",
            "  bConfigurationValue:  " + config.getBConfigurationValue());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "printConfig",
            "  iConfiguration:       " + config.getIConfiguration());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "printConfig",
            "  bmAttributes:         " + config.getBmAttributes());
        log(
            LOG_HOTPLUG, DEBUG, CLASS, "printConfig",
            "  MaxPower:             " + config.getMaxPower());

        for (i = 0; i < config.getBNumInterfaces(); i++)
        {
            usb_interface iface;
//            iface = Libusb.usb_interface_index(config.getInterface(),i);
             iface = Libusb.usb_interface_index(config.get_interface(),i);
            printInterface(iface);
        }
    }
*/
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
                log.fatal(func+"() "+msg);

                break;

            case ERROR :
                log.error(func+"() "+msg);

                break;

            case INFO :
                log.info(func+"() "+msg);

                break;

            case FUNC :
                log.trace(func+"() "+msg);

                break;

            case DEBUG :
                log.debug(func+"() "+msg);

                break;

            default :
                log.debug(func+"() "+msg);

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
            log(LOG_HOTPLUG, FUNC, CLASS, method,"Already initialised. Leaving initialise");
            return;
        }

        loadLibrary();

        // we want to access libusb exclusively
        mutex.acquire();

//        usb_version version;
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

//            version = Libusb.usb_get_version();
        }
        finally
        {
            mutex.release();
        }

//        usb_version_dll dllVersion = version.getDll();

//        String msg =
//            "Libusb-Win32 DLL version:\t" + dllVersion.getMajor() + "."
//            + dllVersion.getMinor() + "." + dllVersion.getMicro() + "."
//            + dllVersion.getNano();

//        log(LOG_DEFAULT, DEBUG, "JavaxUsb", "init", msg);

//        usb_version_driver driverVersion = version.getDriver();

        // When using the filter driver: if no USB device is attached, 
        // the result is -1, even when the filter driver service is running
//        if (driverVersion.getMajor() != -1)
//        {
//            msg = "Libusb-Win32 service driver version:\t"
//                + driverVersion.getMajor() + "." + driverVersion.getMinor()
//                + "." + driverVersion.getMicro() + "."
//                + driverVersion.getNano();
//            log(LOG_DEFAULT, DEBUG, CLASS, method, msg);
//        }

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
            log(LOG_HOTPLUG, FUNC, CLASS, "loadLibrary", "Getting java.library.path");
//            String temp = "java.library.path="+System.getProperty("java.library.path");
            
//            System.out.println("java.library.path="+System.getProperty("java.library.path")); 	
            System.loadLibrary(LIBRARY_NAME);
        }
        catch (Exception e)
        {
            throw new UsbException(
                EXCEPTION_WHILE_LOADING_SHARED_LIBRARY + " <"
                + System.mapLibraryName(LIBRARY_NAME) + "> : \n" + e.getMessage());
        }
        catch (Error e)
        {
            throw new UsbException(
                ERROR_WHILE_LOADING_SHARED_LIBRARY + " <"
                + System.mapLibraryName(LIBRARY_NAME) + "> : \n" + e.getMessage());
        }
    }

    public static void isReturnCodeError(int error) throws UsbException, UsbDisconnectedException
    {
      if( error >=0) return;
//      log.debug("isReturnCodeError() throwing an exception on "+error);
      if(error == -19) throw new UsbDisconnectedException("libusb reports device has been disconnected: "+error+", "+Libusb.usb_strerror());
      throw errorToUsbException(error);
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
//    static native String nativeGetErrorMessage(int error);
      static String nativeGetErrorMessage(int error)
      {
        return(Libusb.usb_strerror());
      }

    /**
     * @param windowsDeviceOsImp
     * @return
     */
//    static native int nativeGetActiveConfigurationNumber(
//        WindowsDeviceOsImp windowsDeviceOsImp);

    /**
     * @param windowsDeviceOsImp
     * @param i
     * @return
     */
//    static native int nativeGetActiveInterfaceSettingNumber(
//        WindowsDeviceOsImp windowsDeviceOsImp,
//        int i);

    
    private static void attachParentUsbPort(UsbHubImp hub, UsbDeviceImp device)
    {
      List ports = hub.getUsbPorts();
      Iterator iterator = ports.iterator();
      while(iterator.hasNext())
      {
        UsbPortImp usbPortImp = (UsbPortImp)iterator.next();
        if(usbPortImp.isUsbDeviceAttached()) continue;
        if(log.isDebugEnabled()) log.debug("attachParentUsbPort(), found empty port");
        device.setParentUsbPortImp(usbPortImp);
        usbPortImp.attachUsbDeviceImp(device);
        return;
      }

      byte port = hub.getNumberOfPorts();
      port++;
      UsbPortImp usbPortImp = hub.getUsbPortImp((byte)port);
      if( usbPortImp == null)
      {
        log.debug("attachParentUsbPort(), WARNING resizing hub to "+port+" ports");
        hub.resize((byte)port);
        usbPortImp = hub.getUsbPortImp((byte)port);
      }
      usbPortImp.attachUsbDeviceImp(device);
      device.setParentUsbPortImp(usbPortImp);
    }


    /**
     * Return if the compared devices are the same device.
     * FIXME relies on the device serial number string to be unique for each device (this may not always be true
     *  if we could examine the port number / path, we could make a more definative argument
     *  Testing starts with easy things to compare and becomes progressivly more complicated
     * <p>
     * If either of the device's descriptors are null, this returns false.
     * @param dev1 The first device.
     * @param dev2 The second device.
     * @return If the devices appear to be equal.
     */
    protected static boolean isUsbDevicesEqual(UsbDeviceImp dev1,UsbDeviceImp dev2)
    {
        try
        {
            UsbDeviceDescriptor desc1 = dev1.getUsbDeviceDescriptor();
            UsbDeviceDescriptor desc2 = dev2.getUsbDeviceDescriptor();

            if( dev1.isUsbHub() != dev2.isUsbHub() ) return(false);
            if( dev1.getSpeed() != dev2.getSpeed() ) return(false);
            if( !desc1.equals(desc2) ) return(false);
            if( !dev1.getSerialNumberString().equals(dev2.getSerialNumberString() )) return(false);
            return(true);
        }
        catch (UnsupportedEncodingException uee)
        { 
            return(false);
        }
        catch (NullPointerException npE)
        {
            return(false);
        }
        catch(UsbDisconnectedException ude)
        {
            log.debug("isUsbDevicesEqual() device no longer exists");
            return(false);
        }
        catch (UsbException  ue)
        {
            return(false);
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
     * @param device The UsbDeviceImp to add.
     * @param currentDevices - the list of devices that were connected before this call to topology update
     * @param disconnected The List of all devices that can possibly disconnected
     * @param connected The List of connected devices that are newely connected
     * @return The new UsbDeviceImp or existing UsbDeviceImp.
     */
    private static void checkUsbDeviceImp(UsbHubImp hub, UsbDeviceImp device, List currentDevices, List connected)
    {
        boolean found = false;
        String method = "checkUsbDeviceImp";

        // look for this devices in the current device list, if found remove from the list
        // because later, the remaining items in current device list will be removed.
        Iterator iterator = currentDevices.iterator();
        while( iterator.hasNext())
        {
          UsbDeviceImp usbDeviceFromList = (UsbDeviceImp)iterator.next();
          if( isUsbDevicesEqual(usbDeviceFromList, device))
          {
              iterator.remove();
              found = true;
          }
        }

        // If the device was not found in the list, it must be new.  Add it to the connected list
        if(!found)
        {  
          if(log.isDebugEnabled()) log.debug(method+" found new device");
          connected.add(device);
          attachParentUsbPort(hub,device);
        }
    }


    /**
     * looks at the bus through libusb and attempts to update our knowlege of the system
     * by putting new devices into the connectedDevices list and removing still present items from the 
     * disconnected List
     * @param services
     * @param connectedDevices - at entry contains no devices, at exit contains newly found devices
     * @param disconnectedDevices -at entry contains all known devices, at exit contains newly removed devices
     * @return 0 if no change, else -1
     */
    static int nativeTopologyUpdater(WindowsUsbServices services, List connectedDevices, List disconnectedDevices)
    {
        String method = "nativeTopologyUpdater()";

        UsbHubImp rootHub = services.getRootUsbHubImp();
        if (rootHub == null)
        {	// this shouldn't happen, as the root hub is setup during initialisation
            throw new RuntimeException("The (virtual) root hub couldn't be retrieved.");
        }

        // acquire a lock, so we don't interfere with others trying to access libusb
        mutex.acquire();
        try
        {
            int busCount = Libusb.usb_find_busses();
            int deviceCount = Libusb.usb_find_devices();
            if(log.isDebugEnabled()) log.debug(method + "   Found " + busCount + " new busses. and " + deviceCount + " new devices.");

            // if no devices have changed do a quick exit as the calling method looks at the return value
            // to determin if more work has to be done
            if (deviceCount == 0)
                return( 0 );

            // if we get here a change has been detected by libusb, examine the structure of the bus as presented by libusb to 
            // what has changed.  To do this we build all the devices listed in libusb and compare them to our internal list
            usb_bus libusb_bus = Libusb.usb_get_busses();
            while (libusb_bus != null)
            {// for all busses
                log.debug("");
                log.debug( method +" Scanning bus " + libusb_bus.getDirname());

                usb_device libusb_dev = libusb_bus.getDevices();
                while (libusb_dev != null)
                { // for all devices on this bus
                    if(log.isDebugEnabled()) log.debug(method + " Device: " + libusb_dev.getFilename());

                    if (libusb_dev.getConfig() == null)
                    {
                        log(LOG_HOTPLUG, ERROR, CLASS, method,"Couldn't retrieve descriptors for device '"+ libusb_bus.getDirname() + "/" + libusb_dev.getFilename() + "'");
                        libusb_dev = libusb_dev.getNext();
                        continue;
                    }

                    UsbDeviceImp usbDev = buildDevice(libusb_dev, libusb_bus );
                    // usbDev is a device that is being reported as existing by libusb
                    // if it is found in our list of disconnectedDevices, remove it and add it to 
                    // connected devices.
                    checkUsbDeviceImp(rootHub, usbDev, disconnectedDevices, connectedDevices);
                    libusb_dev = libusb_dev.getNext();
                } // end for all devices on this bus
                libusb_bus = libusb_bus.getNext();
            } // end for all busses
        }
        finally
        {
            mutex.release();
        }

        return -1;
    }



    /**
    * @param iface
    * @param usb_endpoint_descriptor
    */
    private static void buildEndpoint(UsbInterfaceImp iface,usb_endpoint_descriptor endPointDesc)
    {
      UsbEndpointDescriptorImp desc =
        new UsbEndpointDescriptorImp((byte) endPointDesc.getBLength(),(byte) endPointDesc.getBDescriptorType(),
                (byte) endPointDesc.getBEndpointAddress(),(byte) endPointDesc.getBmAttributes(),
                (byte) endPointDesc.getBInterval(),(short) endPointDesc.getWMaxPacketSize());

        UsbEndpointImp ep = new UsbEndpointImp(iface, desc);
        UsbPipeImp pipe = null;

        WindowsInterfaceOsImp windowsInterfaceOsImp = (WindowsInterfaceOsImp) iface.getUsbInterfaceOsImp();

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
          pipe.setUsbPipeOsImp(new WindowsPipeOsImp((UsbControlPipeImp) pipe, windowsInterfaceOsImp));
          break;

        case UsbConst.ENDPOINT_TYPE_ISOCHRONOUS :
          pipe = new UsbPipeImp(ep, null);
          pipe.setUsbPipeOsImp(new WindowsPipeOsImp(pipe, windowsInterfaceOsImp));
          break;

        case UsbConst.ENDPOINT_TYPE_BULK :
          pipe = new UsbPipeImp(ep, null);
          pipe.setUsbPipeOsImp(new WindowsPipeOsImp(pipe, windowsInterfaceOsImp));
          break;

        case UsbConst.ENDPOINT_TYPE_INTERRUPT :
          pipe = new UsbPipeImp(ep, null);
          pipe.setUsbPipeOsImp(new WindowsPipeOsImp(pipe, windowsInterfaceOsImp));
          break;

        default :
          String emsg = "Invalid UsbEndpoint type " + ep.getType();
          log.error("buildEndpoint() "+emsg);
          throw new RuntimeException(emsg);
        }
    }


    /**
    * @param usbConfig
    * @param ifaceDesc
    */
    private static void buildInterface(UsbConfigurationImp usbConfig, usb_interface_descriptor ifaceDesc)
    {
        UsbInterfaceDescriptorImp desc = new UsbInterfaceDescriptorImp((byte) ifaceDesc.getBLength(),
          (byte) ifaceDesc.getBDescriptorType(),(byte) ifaceDesc.getBInterfaceNumber(),
          (byte) ifaceDesc.getBAlternateSetting(),(byte) ifaceDesc.getBNumEndpoints(),
          (byte) ifaceDesc.getBInterfaceClass(),(byte) ifaceDesc.getBInterfaceSubClass(),
          (byte) ifaceDesc.getBInterfaceProtocol(),(byte) ifaceDesc.getIInterface());

        UsbInterfaceImp iface = new UsbInterfaceImp(usbConfig, desc);
        boolean active = ((0 == ifaceDesc.getBAlternateSetting())? true : false);
        /* If the config is not active, neither are its interface settings */
        if(usbConfig.isActive() && active)
        {
          iface.setActiveSettingNumber(iface.getUsbInterfaceDescriptor().bAlternateSetting());
        }
        else
        {
          log(LOG_HOTPLUG, FUNC, CLASS, "buildInterface","inteface not active");
        
        }

        WindowsDeviceOsImp windowsDeviceOsImp = (WindowsDeviceOsImp) iface.getUsbConfigurationImp().getUsbDeviceImp();
        WindowsInterfaceOsImp windowsInterfaceOsImp = new WindowsInterfaceOsImp(iface, windowsDeviceOsImp);
        iface.setUsbInterfaceOsImp(windowsInterfaceOsImp);

        for (int i = 0; i < ifaceDesc.getBNumEndpoints(); i++)
        {
            buildEndpoint(iface, Libusb.usb_endpoint_descriptor_index(ifaceDesc.getEndpoint(),i));
        }
    }


    private static void buildConfig(UsbDeviceImp usbDev,usb_config_descriptor config)
    {
      UsbConfigurationDescriptorImp desc = new UsbConfigurationDescriptorImp((byte) config.getBLength(), 
        (byte) config.getBDescriptorType(),(short) config.getWTotalLength(),
        (byte) config.getBNumInterfaces(),(byte) config.getBConfigurationValue(),
        (byte) config.getIConfiguration(),(byte) config.getBmAttributes(), 
        (byte) config.getMaxPower());

      UsbConfigurationImp usbConfig = new UsbConfigurationImp(usbDev, desc);
      usbDev.addUsbConfigurationImp(usbConfig);

      // FIXME: since most devices only support one configuration we will use the first one as default
      // this is probably ok for devices that support more than one configuration as well
      // unless it has been somehow previously set
      if(config.getBConfigurationValue() == 1)
      {
//    	log(LOG_HOTPLUG, FUNC, CLASS, "buildConfiguration","WARNING Using config " + config.getBConfigurationValue()+" as active; no checking.");
        config.setIConfiguration((byte)1);
        usbDev.setActiveUsbConfigurationNumber((byte)config.getBConfigurationValue());
      }
        
      for (int i = 0; i < config.getBNumInterfaces(); i++)
      {
        usb_interface iface;
        iface = Libusb.usb_interface_index(config.get_interface(),i);

        for (int j = 0; j < iface.getNum_altsetting(); j++)
        {
          usb_interface_descriptor ifaceDesc;
          ifaceDesc = Libusb.usb_interface_descriptor_index(iface.getAltsetting(),j);
          buildInterface(usbConfig, ifaceDesc);
        }
      }
    }


    /**
    * Creates a UsbDevice or UsbHub, depending on the type of
    * argument dev, fills in the values from the config of dev
    * and checks, whether this device
    * @param dev
    * @param bus
    */
    static UsbDeviceImp buildDevice( usb_device dev, usb_bus bus)
    {
      UsbDeviceImp usbDev;

      if( dev.getDescriptor().getBDeviceClass() == Libusb.USB_CLASS_HUB)
      {	
        usbDev = new WindowsHubOsImp(dev);
        // note that libusb doesn't give us information about the number of ports
      }
      else
      {
        usbDev = new WindowsDeviceOsImp(dev);
        usbDev.setSpeed(UsbConst.DEVICE_SPEED_UNKNOWN);          // libusb doesn't tell the speed
        // now build all configurations
        for (int i = 0; i < dev.getDescriptor().getBNumConfigurations(); i++)
        {
          buildConfig(usbDev,Libusb.usb_config_descriptor_index(dev.getConfig(),i));
        }
      }                
      return(usbDev);
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
/*    private static UsbConfigurationImp createUsbConfigurationImp(
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
        // BUG - Java (IBM JVM at least) does not handle certain JNI byte -> Java byte (or shorts) 
        // Email ddstreet@ieee.org for more info 
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
*/
    /** @return A new UsbEndpointImp */
/*    private static UsbEndpointImp createUsbEndpointImp(
        UsbInterfaceImp iface,
        byte length,
        byte type,
        byte endpointAddress,
        byte attributes,
        byte interval,
        short maxPacketSize)
    {
        // BUG - Java (IBM JVM at least) does not handle certain JNI byte -> Java byte (or shorts) 
        // Email ddstreet@ieee.org for more info 
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

                String emsg = "Invalid UsbEndpoint type " + ep.getType();
                log.error("createUsbEndpointImp"+emsg);
                throw new RuntimeException(emsg);
        }

        return ep;
    }
*/
    //*************************************************************************
    // Setup methods
/*    private static UsbDeviceDescriptor configureUsbDeviceImp(
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
        // BUG - Java (IBM JVM at least) does not handle certain JNI byte -> Java byte (or shorts) 
        // Email ddstreet@ieee.org for more info 
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

                // log 
                targetDevice.setSpeed(UsbConst.DEVICE_SPEED_UNKNOWN);

                break;
        }

        return desc;
    }
*/
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
             log(LOG_HOTPLUG, DEBUG, "JavaxUsb", "connectUsbDeviceImp","device connect : ");

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
//        tracing = enable;
    }

    //*************************************************************************
    // Class variables
    private static boolean libraryLoaded = false;
    private static Hashtable msgLevelTable = new Hashtable();
    private static int traceLevel;

    //*************************************************************************
    // Class constants
//    public static final String LIBRARY_NAME = "LibusbJNI";
    public static final String LIBRARY_NAME = "usbJNI";
    public static final String ERROR_WHILE_LOADING_SHARED_LIBRARY = "Error while loading shared library";
    public static final String EXCEPTION_WHILE_LOADING_SHARED_LIBRARY = "Exception while loading shared library";
//    private static final int SPEED_UNKNOWN = 0;
//    private static final int SPEED_LOW = 1;
//    private static final int SPEED_FULL = 2;
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
