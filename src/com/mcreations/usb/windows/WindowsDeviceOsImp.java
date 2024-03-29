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
import javax.usb.util.UsbUtil;

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
            if (log.isDebugEnabled()) log.debug( "sendRequest() irp instance of UsbControlIrpImp");
              
            UsbControlIrpImp cIrp = (UsbControlIrpImp) irp;

            // wait until we can access libusb safely
            JavaxUsb.getMutex().acquire();

            try
            {
                // set configuration has its own libusb method
                if( (cIrp.bRequest() == UsbConst.REQUEST_SET_CONFIGURATION) && (cIrp.bmRequestType() ==0) )
                {
                    int retval = Libusb.usb_set_configuration( getHandle(),cIrp.wValue());
                    JavaxUsb.isReturnCodeError(retval);
                    return;
                }

                byte[] data = cIrp.getData();
                if( (cIrp.bmRequestType() & UsbConst.REQUESTTYPE_DIRECTION_MASK) == UsbConst.REQUESTTYPE_DIRECTION_OUT)
                { // this patch is needed because cIrp.wLength is not used as the argument to usb_control_msg
                  // for reads, this is ok as the transfer size is governed by the device,
                  // but when sending data a specific length needs to be send
                  // FIXME - generate a rule in the SWIG processing to allow control of the length argument
                    if(log.isDebugEnabled()) log.debug("sendRequest() direction OUT Len: "+cIrp.wLength());
                    byte[] data1 = cIrp.getData();
                    data = new byte[cIrp.wLength()];
                    for(int i =0;i<data.length;i++)
                    {
                      data[i]=data1[i];
                    }
                }

                if(log.isDebugEnabled()) log.debug( "sendRequest() bmRequestType: "+UsbUtil.toHexString(cIrp.bmRequestType())+
                                                      "  bRequest: "+UsbUtil.toHexString(cIrp.bRequest())+
                                                      "  wValue: "+UsbUtil.toHexString(cIrp.wValue())+
                                                      "  wIndex: "+UsbUtil.toHexString(cIrp.wIndex())+
                                                      "  wLength: "+UsbUtil.toHexString(cIrp.wLength()));

                int retval = Libusb.usb_control_msg(handle,cIrp.bmRequestType(),cIrp.bRequest(), cIrp.wValue(), cIrp.wIndex(),data ,JavaxUsb.getIoTimeout());
                JavaxUsb.isReturnCodeError(retval);	// throws an exception if retval is less than 0
                cIrp.setActualLength(retval);
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
          int retval = Libusb.usb_get_string_simple(handle, index, buffer);
          JavaxUsb.isReturnCodeError(retval);     // throws an exception if retval is less than 0
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
                    String msg = "Couldn't open device " + device.getFilename() + " due to error: " + Libusb.usb_strerror();
                    log.debug(msg);
                    throw new UsbException(msg);
                }

                if (log.isDebugEnabled())
                {
                    int bufSize = 256;
                    int retval;
                    byte[] buf = new byte[bufSize];
                    int manu = device.getDescriptor().getIManufacturer();

                    if (manu > 0)
                    {
                        retval = Libusb.usb_get_string_simple(handle, manu, buf);
                        JavaxUsb.isReturnCodeError(retval);     // throws an exception if retval is less than 0
                        log.debug("Manufacturer : <" + JavaxUsb.bytes2String(buf)+">");
                    }

                    int prod = device.getDescriptor().getIProduct();

                    if (prod > 0)
                    {
                        retval = Libusb.usb_get_string_simple(handle, prod, buf);
                        JavaxUsb.isReturnCodeError(retval);     // throws an exception if retval is less than 0
                        log.debug("Product      : <" + JavaxUsb.bytes2String(buf)+">");
                    }

                    int serial = device.getDescriptor().getISerialNumber();
                    if (serial > 0)
                    {
                        retval = Libusb.usb_get_string_simple(handle, serial, buf);
                        JavaxUsb.isReturnCodeError(retval);     // throws an exception if retval is less than 0
                        log.debug("Serial Number: <" + JavaxUsb.bytes2String(buf)+">");
                    }
                } // end if isDebugEnabled()
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
            int retval = Libusb.usb_close(handle);

            if (retval != 0)
            {
                String msg = "Couldn't close device " + device.getFilename()+ " due to error: " + Libusb.usb_strerror();
                log.debug(msg);
                throw new UsbException(msg);
            }
            JavaxUsb.isReturnCodeError(retval);     // throws an exception if retval is less than 0

        }
        finally
        {
            JavaxUsb.getMutex().release();
        }

        handle = null;
    }
}
