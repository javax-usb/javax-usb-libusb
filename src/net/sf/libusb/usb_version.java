/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.24
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package net.sf.libusb;

public class usb_version {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected usb_version(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(usb_version obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      swigCMemOwn = false;
      LibusbJNI.delete_usb_version(swigCPtr);
    }
    swigCPtr = 0;
  }

  public usb_version_driver getDriver() {
    long cPtr = LibusbJNI.get_usb_version_driver(swigCPtr);
    return (cPtr == 0) ? null : new usb_version_driver(cPtr, false);
  }

  public usb_version_dll getDll() {
    long cPtr = LibusbJNI.get_usb_version_dll(swigCPtr);
    return (cPtr == 0) ? null : new usb_version_dll(cPtr, false);
  }

  public usb_version() {
    this(LibusbJNI.new_usb_version(), true);
  }

}
