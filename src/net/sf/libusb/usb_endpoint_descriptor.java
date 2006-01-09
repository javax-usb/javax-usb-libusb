/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.24
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package net.sf.libusb;

public class usb_endpoint_descriptor {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected usb_endpoint_descriptor(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(usb_endpoint_descriptor obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      swigCMemOwn = false;
      LibusbJNI.delete_usb_endpoint_descriptor(swigCPtr);
    }
    swigCPtr = 0;
  }

  public void setBLength(short bLength) {
    LibusbJNI.set_usb_endpoint_descriptor_bLength(swigCPtr, bLength);
  }

  public short getBLength() {
    return LibusbJNI.get_usb_endpoint_descriptor_bLength(swigCPtr);
  }

  public void setBDescriptorType(short bDescriptorType) {
    LibusbJNI.set_usb_endpoint_descriptor_bDescriptorType(swigCPtr, bDescriptorType);
  }

  public short getBDescriptorType() {
    return LibusbJNI.get_usb_endpoint_descriptor_bDescriptorType(swigCPtr);
  }

  public void setBEndpointAddress(short bEndpointAddress) {
    LibusbJNI.set_usb_endpoint_descriptor_bEndpointAddress(swigCPtr, bEndpointAddress);
  }

  public short getBEndpointAddress() {
    return LibusbJNI.get_usb_endpoint_descriptor_bEndpointAddress(swigCPtr);
  }

  public void setBmAttributes(short bmAttributes) {
    LibusbJNI.set_usb_endpoint_descriptor_bmAttributes(swigCPtr, bmAttributes);
  }

  public short getBmAttributes() {
    return LibusbJNI.get_usb_endpoint_descriptor_bmAttributes(swigCPtr);
  }

  public void setWMaxPacketSize(int wMaxPacketSize) {
    LibusbJNI.set_usb_endpoint_descriptor_wMaxPacketSize(swigCPtr, wMaxPacketSize);
  }

  public int getWMaxPacketSize() {
    return LibusbJNI.get_usb_endpoint_descriptor_wMaxPacketSize(swigCPtr);
  }

  public void setBInterval(short bInterval) {
    LibusbJNI.set_usb_endpoint_descriptor_bInterval(swigCPtr, bInterval);
  }

  public short getBInterval() {
    return LibusbJNI.get_usb_endpoint_descriptor_bInterval(swigCPtr);
  }

  public void setBRefresh(short bRefresh) {
    LibusbJNI.set_usb_endpoint_descriptor_bRefresh(swigCPtr, bRefresh);
  }

  public short getBRefresh() {
    return LibusbJNI.get_usb_endpoint_descriptor_bRefresh(swigCPtr);
  }

  public void setBSynchAddress(short bSynchAddress) {
    LibusbJNI.set_usb_endpoint_descriptor_bSynchAddress(swigCPtr, bSynchAddress);
  }

  public short getBSynchAddress() {
    return LibusbJNI.get_usb_endpoint_descriptor_bSynchAddress(swigCPtr);
  }

  public void setExtra(SWIGTYPE_p_unsigned_char extra) {
    LibusbJNI.set_usb_endpoint_descriptor_extra(swigCPtr, SWIGTYPE_p_unsigned_char.getCPtr(extra));
  }

  public SWIGTYPE_p_unsigned_char getExtra() {
    long cPtr = LibusbJNI.get_usb_endpoint_descriptor_extra(swigCPtr);
    return (cPtr == 0) ? null : new SWIGTYPE_p_unsigned_char(cPtr, false);
  }

  public void setExtralen(int extralen) {
    LibusbJNI.set_usb_endpoint_descriptor_extralen(swigCPtr, extralen);
  }

  public int getExtralen() {
    return LibusbJNI.get_usb_endpoint_descriptor_extralen(swigCPtr);
  }

  public usb_endpoint_descriptor() {
    this(LibusbJNI.new_usb_endpoint_descriptor(), true);
  }

}
