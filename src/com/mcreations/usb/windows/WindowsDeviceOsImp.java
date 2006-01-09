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

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.usb.UsbConst;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbDisconnectedException;
import javax.usb.UsbException;

import net.sf.libusb.Libusb;
import net.sf.libusb.SWIGTYPE_p_usb_dev_handle;
import net.sf.libusb.usb_device;
import net.sf.libusb.usb_device_descriptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.jusb.UsbControlIrpImp;
import com.ibm.jusb.UsbDeviceDescriptorImp;
import com.ibm.jusb.UsbDeviceImp;
import com.ibm.jusb.UsbIrpImp;
import com.ibm.jusb.os.UsbDeviceOsImp;


/**
 * UsbDeviceOsImp implemenation for Windows platform, which wraps a {@link usb_device}.
 * <p>
 * This must be set up before use.
 * <ul>
 * <li>The {@link #getUsbDeviceImp() UsbDeviceImp} must be set
 *     either in the constructor or by its {@link #setUsbDeviceImp(UsbDeviceImp) setter}.</li>
 * </ul>
 * @author Dan Streetman
 * @author Kambiz Darabi
 */
class WindowsDeviceOsImp extends UsbDeviceImp implements UsbDeviceOsImp
{
    /** Used for logging */
    Log log = LogFactory.getLog(WindowsDeviceOsImp.class);

    /** Contains all device information retrieved by libusb during device enumeration */
    private usb_device device;

    /** Handle which is non-null, when device is open */
    SWIGTYPE_p_usb_dev_handle handle = null;
    private static byte[] buffer = new byte[512];

    /**
       Creates a device by wrapping the given {@link usb_device}, which is
       the corresponding libusb-win32 struct of the USB device.
       @param device a UsbDeviceImp which will be configured later, natively, to contain
                         the device config parameters
     */
    public WindowsDeviceOsImp(usb_device device)
    {
        this.device = device;
        setUsbDeviceDescriptor(setupDescriptor(device));
    }

    private UsbDeviceDescriptor setupDescriptor(usb_device device)
    {
        usb_device_descriptor libusbDesc = device.getDescriptor();
        UsbDeviceDescriptorImp desc =
            new UsbDeviceDescriptorImp(
                (byte) libusbDesc.getBLength(),
                (byte) libusbDesc.getBDescriptorType(),
                (short) libusbDesc.getBcdUSB(),
                (byte) libusbDesc.getBDeviceClass(),
                (byte) libusbDesc.getBDeviceSubClass(),
                (byte) libusbDesc.getBDeviceProtocol(),
                (byte) libusbDesc.getBMaxPacketSize0(),
                (short) libusbDesc.getIdVendor(),
                (short) libusbDesc.getIdProduct(),
                (short) libusbDesc.getBcdDevice(),
                (byte) libusbDesc.getIManufacturer(),
                (byte) libusbDesc.getIProduct(),
                (byte) libusbDesc.getISerialNumber(),
                (byte) libusbDesc.getBNumConfigurations());

        return desc;
    }

    /**
     * Asynchronously submit a UsbControlIrpImp.
     * @see com.ibm.jusb.os.UsbDeviceOsImp#syncSubmit(com.ibm.jusb.UsbControlIrpImp)
     */
    public void asyncSubmit(UsbIrpImp irp)
        throws UsbException
    {
        syncSubmit(irp);
    }

    /**
     * Asynchronously submit a control request.
     * @see com.ibm.jusb.os.UsbDeviceOsImp#syncSubmit(com.ibm.jusb.UsbControlIrpImp)
     */
    public void asyncSubmit(UsbControlIrpImp irp)
        throws UsbException
    {
        syncSubmit(irp);
    }

    /**
     * Synchronously submit a control request.
     * @see com.ibm.jusb.os.UsbDeviceOsImp#syncSubmit(com.ibm.jusb.UsbControlIrpImp)
     */
    public void syncSubmit(UsbControlIrpImp irp)
        throws UsbException
    {
        syncSubmit((UsbIrpImp) irp);
    }

    /**
     * Synchronously submit a UsbIrpImp.
     * @see com.ibm.jusb.os.UsbDeviceOsImp#syncSubmit(com.ibm.jusb.UsbControlIrpImp)
     */
    public void syncSubmit(UsbIrpImp irp)
        throws UsbException
    {
        sendRequest(irp);

        // currently (2005-02-03) control messages are always synchronous in libusb
        irp.complete();
    }

    /**
     * Different types of requests result in different calls to
     * Libusb methods, so here, we have to distinguish the different
     * cases and call the appropriate methods.
     *
     * @param irp
     */
    private void sendRequest(UsbIrpImp irp)
        throws UsbException
    {
        // open the device, if it isn't
        if (handle == null)
            handle = getHandle();

        if (irp instanceof UsbControlIrpImp)
        {
            UsbControlIrpImp cIrp = (UsbControlIrpImp) irp;

            // wait until we can access libusb safely
            JavaxUsb.getMutex().acquire();

            try
            {
                // set configuration has its own libusb method
                if (cIrp.bRequest() == UsbConst.REQUEST_SET_CONFIGURATION)
                {
                    Libusb.usb_set_configuration(
                        getHandle(),
                        cIrp.wValue());

                    return;
                }

                // FIXME timeout value hard-coded
                int result =
                    Libusb.usb_control_msg(
                        handle,
                        cIrp.bmRequestType(),
                        cIrp.bRequest(),
                        cIrp.wValue(),
                        cIrp.wIndex(),
                        cIrp.getData(),
                        5000);
            }
            finally
            {
                JavaxUsb.getMutex().release();
            }
        }
        else
        {
            throw new RuntimeException(
                "Implement asyncSubmit for non-control Irp");
        }
    }

    /* (non-Javadoc)
     * @see javax.usb.UsbDevice#asyncSubmit(java.util.List)
     */
    public void asyncSubmit(List list)
        throws UsbException, IllegalArgumentException, UsbDisconnectedException
    {
        super.asyncSubmit(list);
    }

    /* (non-Javadoc)
     * @see javax.usb.UsbDevice#syncSubmit(java.util.List)
     */
    public void syncSubmit(List list)
        throws UsbException, IllegalArgumentException, UsbDisconnectedException
    {
        super.syncSubmit(list);
    }

    /* (non-Javadoc)
     * @see com.ibm.jusb.UsbDeviceImp#getUsbDeviceOsImp()
     */
    public UsbDeviceOsImp getUsbDeviceOsImp()
    {
        return this;
    }

    /**
     * @return Returns the device.
     */
    public usb_device getDevice()
    {
        return device;
    }

    /* (non-Javadoc)
     * @see javax.usb.UsbDevice#getString(byte)
     */
    public String getString(byte index)
        throws UsbException, UnsupportedEncodingException, 
            UsbDisconnectedException
    {
        if (handle == null)
            handle = getHandle();

        // wait until we can access libusb safely
        JavaxUsb.getMutex().acquire();

        try
        {
            Libusb.usb_get_string_simple(handle, index, buffer);
        }
        finally
        {
            JavaxUsb.getMutex().release();
        }

        return JavaxUsb.bytes2String(buffer);
    }

    /**
       Returns the device handle, which is the result of opening
       the device and
       is needed for calls to libusb methods. If called, when
       the device is already opened, the current handle is
       returned, so it is safe to call this method multiple
       times.
       @return the device handle
     */
    SWIGTYPE_p_usb_dev_handle getHandle()
        throws UsbException
    {
        if (handle == null)
        {
            // we need exclusive access
            JavaxUsb.getMutex().acquire();

            try
            {
                handle = Libusb.usb_open(device);

                if (handle == null)
                {
                    String msg =
                        "Couldn't open device " + device.getFilename()
                        + " due to error: " + Libusb.usb_strerror();
                    log.debug(msg);
                    throw new UsbException(msg);
                }

                if (log.isDebugEnabled())
                {
                    int bufSize = 256;
                    int ret;
                    byte[] buf = new byte[bufSize];
                    int manu = device.getDescriptor().getIManufacturer();

                    if (manu > 0)
                    {
                        ret = Libusb.usb_get_string_simple(handle, manu, buf);

                        if (ret > 0)
                        {
                            log.debug(
                                "Manufacturer : " + JavaxUsb.bytes2String(buf));
                        }
                        else
                        {
                            log.debug(
                                "Unable to fetch manufacturer string\r\n");
                        }
                    }

                    int prod = device.getDescriptor().getIProduct();

                    if (prod > 0)
                    {
                        ret = Libusb.usb_get_string_simple(handle, prod, buf);

                        if (ret > 0)
                            log.debug(
                                "Product      : " + JavaxUsb.bytes2String(buf));
                        else
                            log.debug("Unable to fetch product string\r\n");
                    }

                    int serial = device.getDescriptor().getISerialNumber();

                    if (serial > 0)
                    {
                        ret = Libusb.usb_get_string_simple(handle, serial, buf);

                        if (ret > 0)
                        {
                            log.debug(
                                "Serial Number: " + JavaxUsb.bytes2String(buf));
                        }
                        else
                        {
                            log.debug(
                                "Unable to fetch serial number string\r\n");
                        }
                    }
                }
            }
            finally
            {
                JavaxUsb.getMutex().release();
            }
        }

        return handle;
    }

    /**
       Closes the device, if it was previously opened. If
       device is not opened, nothing happens.
     */
    void close()
        throws UsbException
    {
        if (handle == null)
            return;

        // we need exclusive access
        JavaxUsb.getMutex().acquire();

        try
        {
            int result = Libusb.usb_close(handle);

            if (result != 0)
            {
                String msg =
                    "Couldn't close device " + device.getFilename()
                    + " due to error: " + Libusb.usb_strerror();
                log.debug(msg);
                throw new UsbException(msg);
            }
        }
        finally
        {
            JavaxUsb.getMutex().release();
        }

        // set this device to unconfigured state
        setActiveUsbConfigurationNumber((byte) 0);
        handle = null;
    }
}
