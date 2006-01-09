/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.24
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package net.sf.libusb;

public class usb_version_driver {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected usb_version_driver(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(usb_version_driver obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      swigCMemOwn = false;
      LibusbJNI.delete_usb_version_driver(swigCPtr);
    }
    swigCPtr = 0;
  }

  public void setMajor(int major) {
    LibusbJNI.set_usb_version_driver_major(swigCPtr, major);
  }

  public int getMajor() {
    return LibusbJNI.get_usb_version_driver_major(swigCPtr);
  }

  public void setMinor(int minor) {
    LibusbJNI.set_usb_version_driver_minor(swigCPtr, minor);
  }

  public int getMinor() {
    return LibusbJNI.get_usb_version_driver_minor(swigCPtr);
  }

  public void setMicro(int micro) {
    LibusbJNI.set_usb_version_driver_micro(swigCPtr, micro);
  }

  public int getMicro() {
    return LibusbJNI.get_usb_version_driver_micro(swigCPtr);
  }

  public void setNano(int nano) {
    LibusbJNI.set_usb_version_driver_nano(swigCPtr, nano);
  }

  public int getNano() {
    return LibusbJNI.get_usb_version_driver_nano(swigCPtr);
  }

  public usb_version_driver() {
    this(LibusbJNI.new_usb_version_driver(), true);
  }

}