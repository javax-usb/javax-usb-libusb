README for javax.usb Windows
============================

Author: Kambiz Darabi (darabi at m-creations dot com)
Last modification: 2005-08-22


The code in javax-usb-ri-windows is a modified version of the code originally 
written by Dan Streetman. His code used an incomplete driver to enumerate the 
USB devices.

Other functionality was not provided, as his original notes in this file
suggest:

<quote>
This is INCOMPLETE!  So far, the JavaxUsb.dll successfully allows javax.usb
to enumerate all connected devices.  No hotplug support is included yet.
A "service" (i.e. kernel driver) must be created to allow actual communication.
This is PRE-ALPHA and not fully functional!  Feel free to discuss this on the
mailing list, but please don't bother telling me that it doesn't work.  I know!
</quote>

I decided to take the LibUsb-Win32 library, wrap it with SWIG into a DLL 
(LibusbJNI.dll)and use that Java binding to make possible what I needed 
badly: using JSyncManager to synchronise a PalmOS handheld over USB.

This was my sole intention and I didn't pay attention to anything else. I
had to refactor the original code heavily, in a short amount of time,
leaving behind a lot of garbage which needs to be cleaned up. Javadoc
comments might reflect the changes that I have made, or not! The code I
used (and which you can find in the directories javax-usb and javax-usb-ri)
is NOT the 1.0 release of JSR80, but a CVS snapshot of the time when I
started work. If someone finds the time to diff it against the released 1.0
code, I would make necessary modifications.

Please DO NOT use this code, if you are not a programmer and don't know how to
debug (and hopefully fix) the code!! It is not yet ready for end users.

As I currently don't have much time to test the code on anything else than
JSyncManager sync with Palm devices, I hope that others will be so kind to
do so and contribute back (unified) patches, which I will deliberately
incorporate into the code base.

If it works for you, I would like to know your configuration to be able to
document it in the next releases.


Other resources
===============

LibUsb-Win32 web site is http://libusb-win32.sourceforge.net/

SWIG web site is http://www.swig.org/

JSR80 project web site is http://javax-usb.org





Requirements
============


Java	Standard Edition >= 1.3 (tested on 1.3 and 1.4)

Libusb-Win32	I used filter driver version 20041118 (not tested with normal device driver)

And for building the code:
 
SWIG	http://www.swig.org/ (I used swigwin 1.3.24)

ANT	http://ant.apache.org/ >= 1.6


Build
=====


The ANT tool uses XML buildfiles to create the project.  Run ant in
the toplevel directory to list all available targets.
You need jsr80.jar, jsr80_ri.jar and jsr80_windows.jar to run it.

The build.xml file doesn't contain a target for LibusbJNI.dll.
You have to run swig manually in the swig subdir and compile the
generated files. The makefile might help (but I'm not sure).


Installation
============


Add lib/jsr80_windows.jar to your CLASSPATH and put jsr80.jar,
jsr80_ri.jar, commons-logging.jar, and log4j.jar into the same directory.

Install the Libusb-Win32 filter driver and verify with the provided tools,
that your device is correctly recognised by the driver.

Copy LibusbJNI.dll into the Windows PATH (e.g. WINDOWS\system32) or
alternatively to the bin dir of your JRE (Windows always looks in the same
dir as the executable, which is java.exe or javaw.exe in our case).


If you want to change the default settings, modify javax.usb.properties and
put it into the CLASSPATH before jsr80_windows.jar.


