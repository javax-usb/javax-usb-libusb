/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.36
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package net.sf.libusb;

public class usb_device {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected usb_device(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(usb_device obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      swigCMemOwn = false;
      LibusbJNI.delete_usb_device(swigCPtr);
    }
    swigCPtr = 0;
  }

  public void setNext(usb_device value) {
    LibusbJNI.usb_device_next_set(swigCPtr, this, usb_device.getCPtr(value), value);
  }

  public usb_device getNext() {
    long cPtr = LibusbJNI.usb_device_next_get(swigCPtr, this);
    return (cPtr == 0) ? null : new usb_device(cPtr, false);
  }

  public void setPrev(usb_device value) {
    LibusbJNI.usb_device_prev_set(swigCPtr, this, usb_device.getCPtr(value), value);
  }

  public usb_device getPrev() {
    long cPtr = LibusbJNI.usb_device_prev_get(swigCPtr, this);
    return (cPtr == 0) ? null : new usb_device(cPtr, false);
  }

  public void setFilename(String value) {
    LibusbJNI.usb_device_filename_set(swigCPtr, this, value);
  }

  public String getFilename() {
    return LibusbJNI.usb_device_filename_get(swigCPtr, this);
  }

  public void setBus(usb_bus value) {
    LibusbJNI.usb_device_bus_set(swigCPtr, this, usb_bus.getCPtr(value), value);
  }

  public usb_bus getBus() {
    long cPtr = LibusbJNI.usb_device_bus_get(swigCPtr, this);
    return (cPtr == 0) ? null : new usb_bus(cPtr, false);
  }

  public void setDescriptor(usb_device_descriptor value) {
    LibusbJNI.usb_device_descriptor_set(swigCPtr, this, usb_device_descriptor.getCPtr(value), value);
  }

  public usb_device_descriptor getDescriptor() {
    long cPtr = LibusbJNI.usb_device_descriptor_get(swigCPtr, this);
    return (cPtr == 0) ? null : new usb_device_descriptor(cPtr, false);
  }

  public void setConfig(usb_config_descriptor value) {
    LibusbJNI.usb_device_config_set(swigCPtr, this, usb_config_descriptor.getCPtr(value), value);
  }

  public usb_config_descriptor getConfig() {
    long cPtr = LibusbJNI.usb_device_config_get(swigCPtr, this);
    return (cPtr == 0) ? null : new usb_config_descriptor(cPtr, false);
  }

  public void setDev(SWIGTYPE_p_void value) {
    LibusbJNI.usb_device_dev_set(swigCPtr, this, SWIGTYPE_p_void.getCPtr(value));
  }

  public SWIGTYPE_p_void getDev() {
    long cPtr = LibusbJNI.usb_device_dev_get(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_void(cPtr, false);
  }

  public void setDevnum(short value) {
    LibusbJNI.usb_device_devnum_set(swigCPtr, this, value);
  }

  public short getDevnum() {
    return LibusbJNI.usb_device_devnum_get(swigCPtr, this);
  }

  public void setNum_children(short value) {
    LibusbJNI.usb_device_num_children_set(swigCPtr, this, value);
  }

  public short getNum_children() {
    return LibusbJNI.usb_device_num_children_get(swigCPtr, this);
  }

  public void setChildren(SWIGTYPE_p_p_usb_device value) {
    LibusbJNI.usb_device_children_set(swigCPtr, this, SWIGTYPE_p_p_usb_device.getCPtr(value));
  }

  public SWIGTYPE_p_p_usb_device getChildren() {
    long cPtr = LibusbJNI.usb_device_children_get(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_p_usb_device(cPtr, false);
  }

  public usb_device() {
    this(LibusbJNI.new_usb_device(), true);
  }

}
