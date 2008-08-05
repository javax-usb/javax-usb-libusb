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

import java.util.LinkedList;
import java.util.List;

import javax.usb.UsbConst;
import javax.usb.UsbEndpoint;
import javax.usb.UsbException;

import net.sf.libusb.Libusb;
import net.sf.libusb.SWIGTYPE_p_usb_dev_handle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.jusb.UsbIrpImp;
import com.ibm.jusb.UsbPipeImp;
import com.ibm.jusb.os.DefaultUsbPipeOsImp;
import com.ibm.jusb.os.UsbPipeOsImp;


/**
 * UsbPipeOsImp implementation for Windows platform.
 * <p>
 * This must be set up before use.
 * <ul>
 * <li>The {@link #getUsbPipeImp() UsbPipeImp} must be set
 *     either in the constructor or by its {@link #setUsbPipeImp(UsbPipeImp) setter}.</li>
 * <li>The {@link #getWindowsInterfaceOsImp() WindowsInterfaceOsImp} must be set
 *     either in the constructor or by its {@link #setWindowsInterfaceOsImp(WindowsInterfaceOsImp) setter}.</li>
 * </ul>
 * @author Dan Streetman
 * @author Kambiz Darabi
 */
public class WindowsPipeOsImp extends DefaultUsbPipeOsImp
    implements UsbPipeOsImp
{
    /** Used for logging */
    Log log = LogFactory.getLog(WindowsPipeOsImp.class);

    /** Constructor */
    public WindowsPipeOsImp(
        UsbPipeImp pipe,
        WindowsInterfaceOsImp iface)
    {
        setUsbPipeImp(pipe);
        setWindowsInterfaceOsImp(iface);
    }

    /** @return The UsbPipeImp for this */
    public UsbPipeImp getUsbPipeImp()
    {
        return usbPipeImp;
    }

    /** @param usbPipeImp The UsbPipeImp for this */
    public void setUsbPipeImp(UsbPipeImp pipe)
    {
        usbPipeImp = pipe;
    }

    /** @return The WindowsInterfaceOsImp */
    public WindowsInterfaceOsImp getWindowsInterfaceOsImp()
    {
        return windowsInterfaceOsImp;
    }

    /** @param iface The WindowsInterfaceOsImp */
    public void setWindowsInterfaceOsImp(WindowsInterfaceOsImp iface)
    {
        windowsInterfaceOsImp = iface;
    }

    /**
     * Asynchronous submission using a UsbIrpImp.
     * @param irp the UsbIrpImp to use for this submission
     * @exception javax.usb.UsbException if error occurs
     */

    /*
       public void asyncSubmit( UsbIrpImp irp ) throws UsbException
       {
               WindowsPipeRequest request = usbIrpImpToWindowsPipeRequest(irp);
               getWindowsInterfaceOsImp().submit(request);
               synchronized(inProgressList) {
                       inProgressList.add(request);
               }
       }
     */

    /**
     * Stop all submissions in progress
     */

    /*
       public void abortAllSubmissions()
       {
               Object[] requests = null;
               synchronized(inProgressList) {
                       requests = inProgressList.toArray();
                       inProgressList.clear();
               }
               for (int i=0; i<requests.length; i++)
                       getWindowsInterfaceOsImp().cancel((WindowsPipeRequest)requests[i]);
               for (int i=0; i<requests.length; i++)
                               ((WindowsPipeRequest)requests[i]).waitUntilCompleted();
       }
     */

    /** @param request The WindowsRequest that completed. */

    /*
       public void windowsRequestComplete(WindowsRequest request)
       {
               synchronized (inProgressList) {
                       inProgressList.remove(request);
               }
       }
     */

    /**
     * Create a WindowsPipeRequest to wrap a UsbIrpImp.
     * @param usbIrpImp The UsbIrpImp.
     * @return A WindowsPipeRequest for a UsbIrpImp.
     */

    /*
       protected WindowsPipeRequest usbIrpImpToWindowsPipeRequest(UsbIrpImp usbIrpImp)
       {
               WindowsPipeRequest request = new WindowsPipeRequest(getPipeType(),getEndpointAddress());
               request.setUsbIrpImp(usbIrpImp);
               request.setCompletion(this);
               return request;
       }
     */

    /** @return The endpoint address */
    protected byte getEndpointAddress()
    {
        if (0 == endpointAddress)
            endpointAddress =
                usbPipeImp.getUsbEndpointImp().getUsbEndpointDescriptor()
                              .bEndpointAddress();

        return endpointAddress;
    }

    /** @return The pipe type */
    protected byte getPipeType()
    {
        if (0 == pipeType)
            pipeType = usbPipeImp.getUsbEndpointImp().getType();

        return pipeType;
    }

    private UsbPipeImp usbPipeImp = null;
    private WindowsInterfaceOsImp windowsInterfaceOsImp = null;
    protected byte pipeType = 0;
    protected byte endpointAddress = 0;
    protected List inProgressList = new LinkedList();

    /**
       Opens the corresponding device of this pipe to get a
       libusb device handle.
       @see com.ibm.jusb.os.UsbPipeOsImp#open()
     */
    public void open()
        throws UsbException
    {
//        log.debug("Entering open()");

        // we don't need to store the handle, here, so just call getHandle
  //      getWindowsInterfaceOsImp().getWindowsDeviceOsImp().getHandle();
//        log.debug("Leaving open()");
    }

    /**
       @see com.ibm.jusb.os.UsbPipeOsImp#close()
     */
    public void close()
    {
        log.debug("Closing Pipe");

/*        try
        {
            // what shall "close a pipe" do? We close the corresponding device.
            getWindowsInterfaceOsImp().getWindowsDeviceOsImp().close();
        }
        catch (UsbException e)
        {
            log.debug("Leaving close() with UsbException " + e.getMessage());
            throw new RuntimeException(e);
        }
*/
//        log.debug("Leaving close()");
    }

    /**
       Delegates to the {@link WindowsDeviceOsImp} of this pipe.
     * @see com.ibm.jusb.os.UsbPipeOsImp#asyncSubmit(com.ibm.jusb.UsbIrpImp)
     */
    private void submitIrp(
        UsbIrpImp irp,
        int timeout)
        throws UsbException
    {

        UsbEndpoint ep = getUsbPipeImp().getUsbEndpoint();
        SWIGTYPE_p_usb_dev_handle handle =
            getWindowsInterfaceOsImp().getWindowsDeviceOsImp().getHandle();
        int epType = ep.getType();
        int epDir = ep.getDirection();
        int result = 0;
        String action = "";

        // lock the access to the libusb
        JavaxUsb.getMutex().acquire();
//        log.debug("Entering submitIrp, epType: "+epType+"  epDir: "+epDir+" timeout: "+timeout+" ms");

        try
        {
            switch (epType)
            {
                case UsbConst.ENDPOINT_TYPE_BULK :

                    if (epDir == UsbConst.ENDPOINT_DIRECTION_OUT)
                    {
                        action = "bulk write";

                        int chunkSize = 4096;
                        byte[] data = irp.getData();
                        int requested = data.length;
                        int written = 0;
                        int res;

                        while (written < requested)
                        {
                            byte[] buf;
                            int toWrite =
                                Math.min(requested - written, chunkSize);
                            buf = new byte[toWrite];
                            System.arraycopy(data, written, buf, 0, toWrite);
                            written += toWrite;
                            res = Libusb.usb_bulk_write(
                                    handle,
                                    getEndpointAddress(),
                                    buf,
                                    timeout);

                            if (log.isDebugEnabled())
                                log.debug(
                                    "Called bulk_write with buffer of size "
                                    + buf.length + ", res = " + res);

                            if (res != toWrite)
                            {
                                result = res;

                                break;
                            }
                        }
                    }
                    else if (epDir == UsbConst.ENDPOINT_DIRECTION_IN)
                    {
                        action = "bulk read";

//                        byte[] bytes = new byte[64];
                        result =
                            Libusb.usb_bulk_read(
                                handle,
                                getEndpointAddress(),
                                irp.getData(),
                                timeout);

                        // fixme: remove this debug msg
                        log.debug("---------- result: " + result);
                    }

                    break;

                case UsbConst.ENDPOINT_TYPE_INTERRUPT :

                    if (epDir == UsbConst.ENDPOINT_DIRECTION_OUT)
                    {
                        action = "interrupt write";
                        result = Libusb.usb_interrupt_write(handle,getEndpointAddress(),irp.getData(),timeout);

                    }
                    else if (epDir == UsbConst.ENDPOINT_DIRECTION_IN)
                    {
                        action = "interrupt read";
                        result = Libusb.usb_interrupt_read(handle,getEndpointAddress(),irp.getData(),timeout);
                    }
                    break;
                default :
                    throw new RuntimeException("WindowsPipeOsImp.submitIrp: end point ("+epType+") type not (yet) supported!");
            }

            if (result < 0)
            {
                String msg = "submitIrp: Error during " + action + ", error code " + result + ": "+ Libusb.usb_strerror();
                log.debug(msg);
                throw new UsbException(msg);
            }
            else
            {
                irp.setActualLength(result);
            }

//            if (log.isDebugEnabled())
//                log.debug("Result of " + action + " operation: " + " Irp.actualLength =  " + irp.getActualLength());
        }
        finally
        {
            // release the lock, so access to libusb is possible, again
            JavaxUsb.getMutex().release();

            // we currently use libusb synchronous calls, only
            irp.complete();
        }
//        log.debug("Leaving submitIrp");
    }

    /**
     * Delegates to the {@link WindowsDeviceOsImp} of this pipe.
     * @see com.ibm.jusb.os.UsbPipeOsImp#asyncSubmit(com.ibm.jusb.UsbIrpImp)
     */
    public void asyncSubmit(UsbIrpImp irp)
        throws UsbException
    {
        submitIrp(irp,JavaxUsb.getIoTimeout());
    }

    public void syncSubmit(UsbIrpImp irp)
        throws UsbException
    {
        submitIrp(irp, JavaxUsb.getIoTimeout());
    }
}
