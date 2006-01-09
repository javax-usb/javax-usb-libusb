# NMAKE (VC++ 6.0) Makefile for using SWIG and Java for C code
#
# Copyright (c) 2005 m-creations gmbh http://www.m-creations.com
#

# - JAVA_HOME should point to your jdk
# - location of SWIG should be corrected
# - location of libusb

SWIG          = C:\Programme\swig\swig.exe

LIBUSB        = M:\Programme\libusb-win32
LIBUSB_LIBPATH= $(LIBUSB)\lib\msvc

VC_PATH       = "C:\Programme\Microsoft Visual Studio\VC98"
IFILE         = Libusb
INTERFACE     = $(IFILE).i
WRAPFILE      = $(IFILE)_wrap.c

# Location of the Visual C++ tools (32 bit assumed)

TARGET        = $(IFILE).dll
CC            = cl.exe
LINK          = link.exe
MACHINE       = I386

# C Library needed to build a DLL

DLLIBC        = msvcrt.lib oldnames.lib  

# Windows libraries that are apparently needed
WINLIB        = kernel32.lib advapi32.lib user32.lib gdi32.lib comdlg32.lib winspool.lib

# Libraries common to all DLLs
LIBS          = libusb.lib $(DLLIBC) $(WINLIB)  

# Linker options
LOPT      = -debug:full -debugtype:cv /NODEFAULTLIB /RELEASE /NOLOGO \
             /libpath:$(LIBUSB_LIBPATH) /MACHINE:$(MACHINE) \
             -entry:_DllMainCRTStartup@12 -dll


# C compiler flags

CFLAGS        = /Z7 /Od /c /nologo
JAVA_INCLUDE    = -I$(JAVA_HOME)\include -I$(JAVA_HOME)\include\win32 -I$(LIBUSB)\src\src

java::
	$(SWIG) -package net.sf.libusb -v -Wall -java -o $(WRAPFILE) $(INTERFACE)
	$(CC) $(CFLAGS) $(JAVA_INCLUDE) $(WRAPFILE)
	set LIB=$(VC_PATH)\lib
	$(LINK) $(LOPT) -out:$(IFILE)JNI.dll $(LIBS) $(IFILE)_wrap.obj
	javac *.java

clean :
	-@erase .\$(IFILE)_wrap.*
	-@erase .\LibusbJNI.*
	-@erase .\SWIGTYPE_*
	-@erase .\*~


