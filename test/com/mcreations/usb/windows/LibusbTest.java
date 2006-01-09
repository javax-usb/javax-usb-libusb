/*
 * (c) 2004 m-creations GmbH. All rights reserved.
 */
package com.mcreations.usb.windows;

import junit.framework.TestCase;

import net.sf.libusb.Libusb;
import net.sf.libusb.usb_bus;
import net.sf.libusb.usb_device;
import net.sf.libusb.usb_device_descriptor;

import java.util.ArrayList;

import javax.usb.UsbException;
import javax.usb.event.UsbServicesEvent;
import javax.usb.event.UsbServicesListener;


/**
 * Tests the functionality of JavaxUsb, which includes
 * the native methods in JavaxUsb.dll.
 *
 * @author darabi
 */
public class LibusbTest extends TestCase
{
    private class DummyUsbServicesListener implements UsbServicesListener
    {
        public void usbDeviceAttached(UsbServicesEvent event)
        {
            try
            {
                System.out.println(
                    "Added: " + event.getUsbDevice().getProductString());
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }

        public void usbDeviceDetached(UsbServicesEvent event)
        {
            try
            {
                System.out.println(
                    "Removed: " + event.getUsbDevice().getProductString());
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public static void testInit()
    {
        int busCount = Libusb.usb_find_busses();
        System.out.println("Found " + busCount + " new busses.");

        int deviceCount = Libusb.usb_find_devices();
        System.out.println("Found " + deviceCount + " new devices.");

        usb_bus bus = Libusb.usb_get_busses();

        while (bus != null)
        {
            System.out.println("Scanning bus " + bus.getDirname());

            usb_device dev = bus.getDevices();

            int index = 1;

            while (dev != null)
            {
                usb_device_descriptor devDesc = dev.getDescriptor();
                System.out.println("Device: " + dev.getFilename());

                dev = dev.getNext();
            }

            bus = bus.getNext();
        }
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(LibusbTest.class);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp()
        throws Exception
    {
        super.setUp();
        System.loadLibrary("LibusbJNI");
        Libusb.usb_init();
        Libusb.usb_set_debug(5);
    }
}
