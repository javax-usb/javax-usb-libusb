/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.24
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

  public void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      swigCMemOwn = false;
      LibusbJNI.delete_usb_string_descriptor(swigCPtr);
    }
    swigCPtr = 0;
  }

  public void setBLength(short bLength) {
    LibusbJNI.set_usb_string_descriptor_bLength(swigCPtr, bLength);
  }

  public short getBLength() {
    return LibusbJNI.get_usb_string_descriptor_bLength(swigCPtr);
  }

  public void setBDescriptorType(short bDescriptorType) {
    LibusbJNI.set_usb_string_descriptor_bDescriptorType(swigCPtr, bDescriptorType);
  }

  public short getBDescriptorType() {
    return LibusbJNI.get_usb_string_descriptor_bDescriptorType(swigCPtr);
  }

  public void setWData(SWIGTYPE_p_unsigned_short wData) {
    LibusbJNI.set_usb_string_descriptor_wData(swigCPtr, SWIGTYPE_p_unsigned_short.getCPtr(wData));
  }

  public SWIGTYPE_p_unsigned_short getWData() {
    long cPtr = LibusbJNI.get_usb_string_descriptor_wData(swigCPtr);
    return (cPtr == 0) ? null : new SWIGTYPE_p_unsigned_short(cPtr, false);
  }

  public usb_string_descriptor() {
    this(LibusbJNI.new_usb_string_descriptor(), true);
  }

}
