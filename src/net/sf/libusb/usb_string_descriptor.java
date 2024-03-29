/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.36
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package net.sf.libusb;

public class usb_string_descriptor {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected usb_string_descriptor(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(usb_string_descriptor obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      swigCMemOwn = false;
      LibusbJNI.delete_usb_string_descriptor(swigCPtr);
    }
    swigCPtr = 0;
  }

  public void setBLength(short value) {
    LibusbJNI.usb_string_descriptor_bLength_set(swigCPtr, this, value);
  }

  public short getBLength() {
    return LibusbJNI.usb_string_descriptor_bLength_get(swigCPtr, this);
  }

  public void setBDescriptorType(short value) {
    LibusbJNI.usb_string_descriptor_bDescriptorType_set(swigCPtr, this, value);
  }

  public short getBDescriptorType() {
    return LibusbJNI.usb_string_descriptor_bDescriptorType_get(swigCPtr, this);
  }

  public void setWData(SWIGTYPE_p_unsigned_short value) {
    LibusbJNI.usb_string_descriptor_wData_set(swigCPtr, this, SWIGTYPE_p_unsigned_short.getCPtr(value));
  }

  public SWIGTYPE_p_unsigned_short getWData() {
    long cPtr = LibusbJNI.usb_string_descriptor_wData_get(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_unsigned_short(cPtr, false);
  }

  public usb_string_descriptor() {
    this(LibusbJNI.new_usb_string_descriptor(), true);
  }

}
