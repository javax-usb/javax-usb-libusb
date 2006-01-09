/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.24
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package net.sf.libusb;

public class usb_interface_descriptor {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected usb_interface_descriptor(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(usb_interface_descriptor obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      swigCMemOwn = false;
      LibusbJNI.delete_usb_interface_descriptor(swigCPtr);
    }
    swigCPtr = 0;
  }

  public void setBLength(short bLength) {
    LibusbJNI.set_usb_interface_descriptor_bLength(swigCPtr, bLength);
  }

  public short getBLength() {
    return LibusbJNI.get_usb_interface_descriptor_bLength(swigCPtr);
  }

  public void setBDescriptorType(short bDescriptorType) {
    LibusbJNI.set_usb_interface_descriptor_bDescriptorType(swigCPtr, bDescriptorType);
  }

  public short getBDescriptorType() {
    return LibusbJNI.get_usb_interface_descriptor_bDescriptorType(swigCPtr);
  }

  public void setBInterfaceNumber(short bInterfaceNumber) {
    LibusbJNI.set_usb_interface_descriptor_bInterfaceNumber(swigCPtr, bInterfaceNumber);
  }

  public short getBInterfaceNumber() {
    return LibusbJNI.get_usb_interface_descriptor_bInterfaceNumber(swigCPtr);
  }

  public void setBAlternateSetting(short bAlternateSetting) {
    LibusbJNI.set_usb_interface_descriptor_bAlternateSetting(swigCPtr, bAlternateSetting);
  }

  public short getBAlternateSetting() {
    return LibusbJNI.get_usb_interface_descriptor_bAlternateSetting(swigCPtr);
  }

  public void setBNumEndpoints(short bNumEndpoints) {
    LibusbJNI.set_usb_interface_descriptor_bNumEndpoints(swigCPtr, bNumEndpoints);
  }

  public short getBNumEndpoints() {
    return LibusbJNI.get_usb_interface_descriptor_bNumEndpoints(swigCPtr);
  }

  public void setBInterfaceClass(short bInterfaceClass) {
    LibusbJNI.set_usb_interface_descriptor_bInterfaceClass(swigCPtr, bInterfaceClass);
  }

  public short getBInterfaceClass() {
    return LibusbJNI.get_usb_interface_descriptor_bInterfaceClass(swigCPtr);
  }

  public void setBInterfaceSubClass(short bInterfaceSubClass) {
    LibusbJNI.set_usb_interface_descriptor_bInterfaceSubClass(swigCPtr, bInterfaceSubClass);
  }

  public short getBInterfaceSubClass() {
    return LibusbJNI.get_usb_interface_descriptor_bInterfaceSubClass(swigCPtr);
  }

  public void setBInterfaceProtocol(short bInterfaceProtocol) {
    LibusbJNI.set_usb_interface_descriptor_bInterfaceProtocol(swigCPtr, bInterfaceProtocol);
  }

  public short getBInterfaceProtocol() {
    return LibusbJNI.get_usb_interface_descriptor_bInterfaceProtocol(swigCPtr);
  }

  public void setIInterface(short iInterface) {
    LibusbJNI.set_usb_interface_descriptor_iInterface(swigCPtr, iInterface);
  }

  public short getIInterface() {
    return LibusbJNI.get_usb_interface_descriptor_iInterface(swigCPtr);
  }

  public void setEndpoint(usb_endpoint_descriptor endpoint) {
    LibusbJNI.set_usb_interface_descriptor_endpoint(swigCPtr, usb_endpoint_descriptor.getCPtr(endpoint));
  }

  public usb_endpoint_descriptor getEndpoint() {
    long cPtr = LibusbJNI.get_usb_interface_descriptor_endpoint(swigCPtr);
    return (cPtr == 0) ? null : new usb_endpoint_descriptor(cPtr, false);
  }

  public void setExtra(SWIGTYPE_p_unsigned_char extra) {
    LibusbJNI.set_usb_interface_descriptor_extra(swigCPtr, SWIGTYPE_p_unsigned_char.getCPtr(extra));
  }

  public SWIGTYPE_p_unsigned_char getExtra() {
    long cPtr = LibusbJNI.get_usb_interface_descriptor_extra(swigCPtr);
    return (cPtr == 0) ? null : new SWIGTYPE_p_unsigned_char(cPtr, false);
  }

  public void setExtralen(int extralen) {
    LibusbJNI.set_usb_interface_descriptor_extralen(swigCPtr, extralen);
  }

  public int getExtralen() {
    return LibusbJNI.get_usb_interface_descriptor_extralen(swigCPtr);
  }

  public usb_interface_descriptor() {
    this(LibusbJNI.new_usb_interface_descriptor(), true);
  }

}
