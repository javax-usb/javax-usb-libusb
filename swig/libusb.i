%module Libusb

typedef unsigned char u_int8_t;
typedef unsigned short u_int16_t;
typedef char int8_t;
typedef short int16_t;

%{
#include "usb.h"

// access a pointer as an array
struct usb_interface *usb_interface_index(struct usb_interface *iface, unsigned index)
    { return iface+index; }

struct usb_device *usb_device_index(struct usb_device *dev, unsigned index)
    { return dev+index; }

struct usb_config_descriptor *usb_config_descriptor_index(struct usb_config_descriptor *dev, unsigned index)
    { return dev+index; }

struct usb_interface_descriptor *usb_interface_descriptor_index(struct usb_interface_descriptor *iface_desc, unsigned index)
    { return iface_desc+index; }

struct usb_endpoint_descriptor *usb_endpoint_descriptor_index(struct usb_endpoint_descriptor *ep, unsigned index)
    { return ep+index; }

%}

// the name usb_device is used for the struct and for
// this method, so we rename the method
%rename(usb_get_device) usb_device(usb_dev_handle *);

// the win dll doesn't contain the global usb_busses
%ignore usb_busses;


// 'interface' conflicts with java key word
//
// BUT swig has a bug: arguments to the getter/setter
//     methods are NOT renamed
//
// So, in usb_config_descriptor, setInterface(usb_interface interface)
// has to be corrected manually, before javac
//
// %rename interface usbInterface;

// Defined typemaps which map a char * to a Java byte array
%include "chararray.i"

// apply it to the struct member 'extra' which
// points to the bytes of extra descriptors
%apply (char* BYTE) { (char *extra) }; 

// apply the (char*,size_t) typemap to these argument names
%apply (char* BUFFER, size_t SIZE) { (char *buf, size_t buflen) };

// apply the (char*,int) typemap to these argument names
%apply (char* BUFFER, int SIZE) { (char *bytes, int size) };
%apply (char* BUFFER, int SIZE) { (char *buf, int size) };

// apply the (void*,int) typemap to these argument names
%apply (void* BUFFER, int SIZE) { (void *buf, int size) };

%include "usb.h"

// various wrappers and convenience functions
// these help treat pointers as arrays
struct usb_interface *usb_interface_index(struct usb_interface *iface, unsigned index);
struct usb_device *usb_device_index(struct usb_device *dev, unsigned index);
struct usb_endpoint_descriptor *usb_endpoint_descriptor_index(struct usb_endpoint_descriptor *ep, unsigned index);
struct usb_config_descriptor *usb_config_descriptor_index(struct usb_config_descriptor *dev, unsigned index);
struct usb_interface_descriptor *usb_interface_descriptor_index(struct usb_interface_descriptor *iface_desc, unsigned index);


