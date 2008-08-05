# Copyright (c) 2008 Gulf Coast Data Concepts  http://www.gcdataconcepts.com
# Copyright (c) 2005 m-creations gmbh http://www.m-creations.com
#

ifdef CYGWIN
SWIG          = /cygdrive/c/usr/bin/swigwin-1.3.36/swig.exe
JAVA_HOME     = /cygdrive/c/Program\ Files/Java/jdk1.6.0_07
LIBUSB_PATH   = ../../libusb-win32-device-bin-0.1.12.1
C_OPTIONS      = -mno-cygwin
JNI_DLL       = usbJNI.dll
INCLUDE       = -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/win32 -I$(LIBUSB_PATH)/include
LINK_OPTIONS = -Wl,--kill-at -mno-cygwin
else
# swig doesn't work with version 1.3.31, works with version 1.3.36
SWIG 	      = /usr/local/bin/swig
JAVA_HOME     = /usr/local/jdk1.6.0_04
LIBUSB_PATH   = /usr
C_OPTIONS     =
JNI_DLL       = libusbJNI.so
INCLUDE       = -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -I$(LIBUSB_PATH)/include
LINK_OPTINS   =
endif

IFILE         = libusb
INTERFACE     = $(IFILE).i
WRAPFILE      = $(IFILE)_wrap
CC            = gcc
SWIG_DEST     = ../src/net/sf/libusb

java::
ifdef CYGWIN
	echo "compiling for CYGWIN target"
endif
	$(SWIG) -package net.sf.libusb -I${INCLUDE} -v -Wall -java -o $(WRAPFILE).c $(INTERFACE)
	$(CC) -c $(C_OPTIONS) -shared $(INCLUDE) $(WRAPFILE).c -o $(WRAPFILE).o
	$(CC) -shared $(WRAPFILE).o -o $(JNI_DLL) -L$(LIBUSB_PATH)/lib/gcc -lusb $(LINK_OPTIONS)
	
	rm $(WRAPFILE).c
	chmod 777 $(JNI_DLL)
	mv $(JNI_DLL) ../lib/
	# the reset just makes an moves java files around
	chmod 666 *.java
	rm -f $(SWIG_DEST)/*.java
	mv *.java $(SWIG_DEST)/

clean :
	rm -f $(IFILE)_wrap.*
	rm -f LibusbJNI.*
	rm -f SWIGTYPE_*
	rm -f *.java
	rm -f *.class
	rm -f $(JNI_DLL)
	rm -f *~
	rm -f ../lib/$(JNI_DLL)


