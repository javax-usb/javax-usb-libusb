/*
 * (c) 2004 m-creations GmbH. All rights reserved.
 */
package com.mcreations.usb.windows;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Iterator;

import javax.usb.UsbClaimException;
import javax.usb.UsbConfiguration;
import javax.usb.UsbConst;
import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbDisconnectedException;
import javax.usb.UsbEndpoint;
import javax.usb.UsbException;
import javax.usb.UsbInterface;
import javax.usb.UsbInterfacePolicy;
import javax.usb.UsbNotActiveException;
import javax.usb.UsbPipe;
import javax.usb.event.UsbServicesEvent;
import javax.usb.event.UsbServicesListener;
import javax.usb.util.DefaultUsbIrp;
import javax.usb.util.StandardRequest;


/**
 * Tests the functionality of JavaxUsb, which includes
 * the native methods in JavaxUsb.dll.
 *
 * @author darabi
 */
public class JavaxUsbTest extends TestCase
{
    private class PipeOpeningUsbListener extends DummyUsbServicesListener
        implements UsbInterfacePolicy
    {
        UsbPipe inPipe;
        UsbPipe outPipe;
		private UsbInterface intf;

        public void usbDeviceAttached(UsbServicesEvent event)
        {
            UsbDevice dev = event.getUsbDevice();
            UsbDeviceDescriptor desc = dev.getUsbDeviceDescriptor();

            try
            {
                if (!dev.isConfigured())
                {
                    // Configure the device
                    (new StandardRequest(dev)).setConfiguration((short) 1);
                } // end-if

                UsbConfiguration conf = dev.getUsbConfiguration((byte) 1);

                intf = conf.getUsbInterface((byte) 0);
                intf.claim(this);

                Iterator endptIter = intf.getUsbEndpoints().iterator();

                while (endptIter.hasNext())
                {
                    UsbEndpoint endpt = (UsbEndpoint) endptIter.next();

                    if (
                        (endpt.getType() == UsbConst.ENDPOINT_TYPE_BULK)
                            && (endpt.getUsbEndpointDescriptor().wMaxPacketSize() == 64))
                    {
                        if (inPipe == null &&
                            endpt.getDirection() == UsbConst.ENDPOINT_DIRECTION_IN)
                        {
                            inPipe = endpt.getUsbPipe();
                            inPipe.open();
                            byte [] inputBuffer = new byte[64];
                            DefaultUsbIrp irp = new DefaultUsbIrp(inputBuffer);
                            irp.setAcceptShortPacket(true);
                            inPipe.syncSubmit(irp);
                            int dataLength=irp.getActualLength();
                            System.out.println("Read " + dataLength + " bytes!");
                        }
                        else if(outPipe == null)
                        {
                        	byte [] outputBuffer = new byte[64];
                            outPipe = endpt.getUsbPipe();
                            outPipe.open();
                            outPipe.syncSubmit(outputBuffer);
                        }
                    }
                }
            }
            catch (Throwable exc)
            {
                System.err.println(exc);
            	if(intf != null)
            	{
            		try {
            			if(inPipe != null)
            				inPipe.close();
            			if(outPipe != null)
                			outPipe.close();
						intf.release();
					} catch (UsbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
            }
        }

        public boolean forceClaim(UsbInterface usbInterface)
        {
            return true;
        } // end-method
    }

    public class DummyUsbServicesListener implements UsbServicesListener
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

    public void testInitialise()
        throws UsbException
    {
        System.out.println(System.getProperty("java.library.path"));
        System.out.println(System.getProperty("user.dir"));
        System.getProperties().list(System.out);
        
        JavaxUsb.initialise();
    }

    public void testNativeTopologyUpdater()
        throws UsbException
    {
        WindowsUsbServices services = new WindowsUsbServices();

        ArrayList connected = new ArrayList();
        ArrayList disconnected = new ArrayList();

        // JavaxUsb.nativeTopologyUpdater(services, connected, disconnected);
        services.addUsbServicesListener(new DummyUsbServicesListener());
    }

    public static void main(String[] args)
    {
        junit.awtui.TestRunner.run(JavaxUsbTest.class);
    }
}
