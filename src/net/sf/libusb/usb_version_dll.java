/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.36
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package net.sf.libusb;

public class usb_version_dll {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected usb_version_dll(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(usb_version_dll obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      swigCMemOwn = false;
      LibusbJNI.delete_usb_version_dll(swigCPtr);
    }
    swigCPtr = 0;
  }

  public void setMajor(int value) {
    LibusbJNI.usb_version_dll_major_set(swigCPtr, this, value);
  }

  public int getMajor() {
    return LibusbJNI.usb_version_dll_major_get(swigCPtr, this);
  }

  public void setMinor(int value) {
    LibusbJNI.usb_version_dll_minor_set(swigCPtr, this, value);
  }

  public int getMinor() {
    return LibusbJNI.usb_version_dll_minor_get(swigCPtr, this);
  }

  public void setMicro(int value) {
    LibusbJNI.usb_version_dll_micro_set(swigCPtr, this, value);
  }

  public int getMicro() {
    return LibusbJNI.usb_version_dll_micro_get(swigCPtr, this);
  }

  public void setNano(int value) {
    LibusbJNI.usb_version_dll_nano_set(swigCPtr, this, value);
  }

  public int getNano() {
    return LibusbJNI.usb_version_dll_nano_get(swigCPtr, this);
  }

  public usb_version_dll() {
    this(LibusbJNI.new_usb_version_dll(), true);
  }

}
