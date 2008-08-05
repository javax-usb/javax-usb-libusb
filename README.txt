README for javax.usb Windows
============================

Code status: development
Todo
	isocronous data transfer
	TESTING, TESTING, TESTING
	install package for end users and application developers

Author: Mike Crowe (mcrowe at gcdataconcepts dot com)
Last modification: 2008-Jul-30
Updated code to use latest release of libusb-win32, (libusb-win32-device-bin-0.1.12.1).  
Get Reports now work through syncSubmit and UsbControlIrps.  Interrupt in data flow working.  
It appears that Kambiz had bulk tranfers working, but I am unclear as to the exact status.  
Support still lacking for isocronous data tranfer.  Brought up to date to use cvs release 
which is at least version 1.0.2 for javax.usb and 1.0.2 for javax.usb.ri.  
Tested against usb-accelerometer (see  http://www.gcdataconcepts.com for details) 
which supports get and set reports as well as interrupt driven in data.

How to compile this code
install cygwin on a Windows computer
use cygwin setup to install the following
	swig
	gcc
	binutils
	make
	cvs
	ssh (if you'd like to work remotely)
	
install a java development kit.  I used jdk1.6.0_07, but other versions
should work as well.

install libusb-win32 see http://libusb-win32.sourceforge.net/

install apache-ant (http://ant.apache.org/)


I commented out part of usb.h (part of libusb-win32) dealing with windows specific components of the
dll, but this is probably unnessisary


To build, start with the swig directory.  You will probably have to adjust
some paths to get it to compile properly.  When it compiles properly you
should hav a LibusbJNK.dll file in the ./lib directory and a number of .java
files in ./src/net/sf/libusb directory.  When you have this, the native
interface is complete and it's time to build the .jar file

I used ant from apache like this to make jars but had to export JAVA_HOME
first

export JAVA_HOME=`cygpath -wp /cygdrive/c/Program\ Files/Java/jdk1.6.0_07/`

 /path/to/ant/apache-ant-1.7.1-bin/apache-ant-1.7.1/bin/ant.bat jars

Once you have the jsr80_windows.jar file you are ready to test the
installation.

As part of testing Kamiz used log4j.jar and commons-loggin.jar for
diagnostics.  It works well, but if you don't use it, that's ok.  The
interface will report that it's missing and continue on without it.

Testing
OK puff, puff puff.  That was alot of pain.  Now comes the hard part,
testing.  I used my target hardware (http://www.gcdataconcepts.com/xlr8r-1.html)
for testing.  You will need to find your own device. 


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





Build Requirements
==================


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


