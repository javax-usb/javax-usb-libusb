// $Id$
//
// Author: Kambiz Darabi <darabi at m-creations dot com>
//
// Copyright (c) 2005 Kambiz Darabi
// Copyright (c) 2005 m-creations gmbh (http://www.m-creations.com)
//
// This SWIG interface file defines some typemaps which
// map from C char* to Java byte[].
// It is included by libusb.i 

// typemap taken from $SWIG/Lib/java/various.i
// which maps a char * to a Java byte array

/* 
 * char *BYTE typemaps. 
 * These are input typemaps for mapping a Java byte[] array to a C char array.
 * Note that as a Java array is used and thus passeed by reference, the C routine 
 * can return data to Java via the parameter.
 *
 * Example usage wrapping:
 *   void foo(char *array);
 *  
 * Java usage:
 *   byte b[] = new byte[20];
 *   modulename.foo(b);
 */
%typemap(jni) char *BYTE "jbyteArray"
%typemap(jtype) char *BYTE "byte[]"
%typemap(jstype) char *BYTE "byte[]"
%typemap(in) char *BYTE {
    $1 = (char *) JCALL2(GetByteArrayElements, jenv, $input, 0); 
}

%typemap(argout) char *BYTE {
    JCALL3(ReleaseByteArrayElements, jenv, $input, (jbyte *) $1, 0); 
}

%typemap(javain) char *BYTE "$javainput"

/* Prevent default freearg typemap from being used */
%typemap(freearg) char *BYTE ""


// typemap which maps two arguments:
// char * followed by an unsigned int is translated
// to a byte array (length is derived from
// the length of the byte array)
%typemap(jni)    (char* BUFFER, size_t SIZE) "jbyteArray"
%typemap(jtype)  (char* BUFFER, size_t SIZE) "byte[]"
%typemap(javain) (char* BUFFER, size_t SIZE) "$javainput"
%typemap(jstype) (char* BUFFER, size_t SIZE) "byte[]"
%typemap(in) (char* BUFFER, size_t SIZE) {
    $1 = ($1_ltype) JCALL2(GetByteArrayElements, jenv, $input, NULL);
    $2 = ($2_ltype) JCALL1(GetArrayLength, jenv, $input);
}
%typemap(argout) (char* BUFFER, size_t SIZE) ""
%typemap(freearg) (char* BUFFER, size_t SIZE) {
    JCALL3(ReleaseByteArrayElements, jenv, $input, (jbyte *) $1, 0);
}

// typemap which maps two arguments:
// char * followed by an int is translated
// to a byte array (length is derived from
// the length of the byte array)
%typemap(jni)    (char* BUFFER, int SIZE) "jbyteArray"
%typemap(jtype)  (char* BUFFER, int SIZE) "byte[]"
%typemap(javain) (char* BUFFER, int SIZE) "$javainput"
%typemap(jstype) (char* BUFFER, int SIZE) "byte[]"
%typemap(in) (char* BUFFER, int SIZE) {
    $1 = ($1_ltype) JCALL2(GetByteArrayElements, jenv, $input, NULL);
    $2 = ($2_ltype) JCALL1(GetArrayLength, jenv, $input);
}
%typemap(argout) (char* BUFFER, int SIZE) ""
%typemap(freearg) (char* BUFFER, int SIZE) {
    JCALL3(ReleaseByteArrayElements, jenv, $input, (jbyte *) $1, 0);
}

// typemap which maps two arguments:
// void * followed by an int is translated
// to a byte array (length is derived from
// the length of the byte array)
%typemap(jni)    (void* BUFFER, int SIZE) "jbyteArray"
%typemap(jtype)  (void* BUFFER, int SIZE) "byte[]"
%typemap(javain) (void* BUFFER, int SIZE) "$javainput"
%typemap(jstype) (void* BUFFER, int SIZE) "byte[]"
%typemap(in) (void* BUFFER, int SIZE) {
    $1 = ($1_ltype) JCALL2(GetByteArrayElements, jenv, $input, NULL);
    $2 = ($2_ltype) JCALL1(GetArrayLength, jenv, $input);
}
%typemap(argout) (void* BUFFER, int SIZE) ""
%typemap(freearg) (void* BUFFER, int SIZE) {
    JCALL3(ReleaseByteArrayElements, jenv, $input, (jbyte *) $1, 0);
}

