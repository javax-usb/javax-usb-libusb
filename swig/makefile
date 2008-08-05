# Copyright (c) 2005 m-creations gmbh http://www.m-creations.com
#
# - JAVA_HOME should point to your jdk
# - location of SWIG should be corrected

SWIG          = /cygdrive/c/usr/bin/swigwin-1.3.36/swig.exe

JAVA_HOME     = /cygdrive/c/Program\ Files/Java/jdk1.6.0_07

IFILE         = Libusb
INTERFACE     = $(IFILE).i
WRAPFILE      = $(IFILE)_wrap

CC            = gcc

SWIG_DEST     = ../src/net/sf/libusb

LIBUSB_PATH   = ../../libusb-win32-device-bin-0.1.12.1
INCLUDE       = -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/win32 -I$(LIBUSB_PATH)/include
JNI_DLL       = LibusbJNI.dll
java::
	$(SWIG) -package net.sf.libusb -I${INCLUDE} -v -Wall -java -o $(WRAPFILE).c $(INTERFACE)
	$(CC) -c -mno-cygwin -shared $(INCLUDE) $(WRAPFILE).c -o $(WRAPFILE).o
	$(CC) -shared $(WRAPFILE).o -o $(JNI_DLL) -L$(LIBUSB_PATH)/lib/gcc -lusb -Wl,--kill-at -mno-cygwin
	
	chmod 666 $(WRAPFILE).c
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


