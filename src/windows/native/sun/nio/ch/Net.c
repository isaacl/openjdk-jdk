/*
 * Copyright (c) 2001, 2007, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

// This file is a derivative work resulting from (and including) modifications
// made by Azul Systems, Inc. The date of such changes is 2014.
// These modification are copyright 2014 Azul Systems, Inc., and are made
// available on the same license terms set forth above.
//
// Please contact Azul Systems, Inc., 1173 Borregas Avenue, Sunnyvale, CA 94089
// USA or visit www.azulsystems.com if you need additional information or have
// any questions.

#include <windows.h>
#include <winsock2.h>
#include "jni.h"
#include "jni_util.h"
#include "jvm.h"
#include "jlong.h"

#include "nio.h"
#include "nio_util.h"
#include "net_util.h"

#include "sun_nio_ch_Net.h"


static jclass ia_class;
static jmethodID ia_ctorID;

/**************************************************************
 * static method to store field IDs in initializers
 */

JNIEXPORT void JNICALL
Java_sun_nio_ch_Net_initIDs(JNIEnv *env, jclass clazz)
{
    clazz = (*env)->FindClass(env, "java/net/Inet4Address");
    ia_class = (*env)->NewGlobalRef(env, clazz);
    ia_ctorID = (*env)->GetMethodID(env, clazz, "<init>", "()V");
}


JNIEXPORT jint JNICALL
Java_sun_nio_ch_Net_socket0(JNIEnv *env, jclass cl, jboolean stream,
                            jboolean reuse)
{
    SOCKET s;

    s = socket(AF_INET, (stream ? SOCK_STREAM : SOCK_DGRAM), 0);
    if (s != INVALID_SOCKET) {
        SetHandleInformation((HANDLE)s, HANDLE_FLAG_INHERIT, 0);
    } else {
        NET_ThrowNew(env, WSAGetLastError(), "socket");
    }
    return (jint)s;
}

JNIEXPORT jint JNICALL
Java_sun_nio_ch_Net_isExclusiveBindAvailable(JNIEnv *env, jclass clazz) {
    OSVERSIONINFO ver;
    int version;
    ver.dwOSVersionInfoSize = sizeof(ver);
    GetVersionEx(&ver);
    version = ver.dwMajorVersion * 10 + ver.dwMinorVersion;
    //if os <= xp exclusive binding is off by default
    return version >= 60 ? 1 : 0;
}

JNIEXPORT void JNICALL
Java_sun_nio_ch_Net_bind0(JNIEnv *env, jclass clazz,
                         jobject fdo, jboolean isExclBind, jobject iao, jint port)
{
    SOCKETADDRESS sa;
    int rv;
    int sa_len = sizeof(sa);

   if (NET_InetAddressToSockaddr(env, iao, port, (struct sockaddr *)&sa, &sa_len, JNI_FALSE) != 0) {
      return;
    }

    rv = NET_WinBind(fdval(env, fdo), (struct sockaddr *)&sa, sa_len, isExclBind);
    if (rv == SOCKET_ERROR)
        NET_ThrowNew(env, WSAGetLastError(), "bind");
}

JNIEXPORT jint JNICALL
Java_sun_nio_ch_Net_connect(JNIEnv *env, jclass clazz, jobject fdo, jobject iao,
                            jint port, jint trafficClass)
{
    SOCKETADDRESS sa;
    int rv;
    int sa_len = sizeof(sa);

   if (NET_InetAddressToSockaddr(env, iao, port, (struct sockaddr *)&sa, &sa_len, JNI_FALSE) != 0) {
      return IOS_THROWN;
    }

    rv = connect(fdval(env, fdo), (struct sockaddr *)&sa, sa_len);
    if (rv != 0) {
        int err = WSAGetLastError();
        if (err == WSAEINPROGRESS || err == WSAEWOULDBLOCK) {
            return IOS_UNAVAILABLE;
        }
        NET_ThrowNew(env, err, "connect");
        return IOS_THROWN;
    }
    return 1;
}

JNIEXPORT jint JNICALL
Java_sun_nio_ch_Net_localPort(JNIEnv *env, jclass clazz, jobject fdo)
{
    struct sockaddr_in sa;
    int sa_len = sizeof(sa);

    if (getsockname(fdval(env, fdo), (struct sockaddr *)&sa, &sa_len) < 0) {
        int error = WSAGetLastError();
        if (error == WSAEINVAL) {
            return 0;
        }
        NET_ThrowNew(env, error, "getsockname");
        return IOS_THROWN;
    }
    return (jint)ntohs(sa.sin_port);
}

JNIEXPORT jobject JNICALL
Java_sun_nio_ch_Net_localInetAddress(JNIEnv *env, jclass clazz, jobject fdo)
{
    struct sockaddr_in sa;
    int sa_len = sizeof(sa);
    jobject iao;

    if (getsockname(fdval(env, fdo), (struct sockaddr *)&sa, &sa_len) < 0) {
        NET_ThrowNew(env, WSAGetLastError(), "getsockname");
        return NULL;
    }

    iao = (*env)->NewObject(env, ia_class, ia_ctorID);
    if (iao == NULL) {
        JNU_ThrowOutOfMemoryError(env, "heap allocation failure");
    } else {
        setInetAddress_addr(env, iao, ntohl(sa.sin_addr.s_addr));
    }

    return iao;
}

JNIEXPORT jint JNICALL
Java_sun_nio_ch_Net_getIntOption0(JNIEnv *env, jclass clazz,
                                  jobject fdo, jint opt)
{
    int klevel, kopt;
    int result;
    struct linger linger;
    char *arg;
    int arglen;

    if (NET_MapSocketOption(opt, &klevel, &kopt) < 0) {
        JNU_ThrowByNameWithLastError(env,
                                     JNU_JAVANETPKG "SocketException",
                                     "Unsupported socket option");
        return IOS_THROWN;
    }

    if (opt == java_net_SocketOptions_SO_LINGER) {
        arg = (char *)&linger;
        arglen = sizeof(linger);
    } else {
        arg = (char *)&result;
        arglen = sizeof(result);
    }

    if (NET_GetSockOpt(fdval(env, fdo), klevel, kopt, arg, &arglen) < 0) {
        NET_ThrowNew(env, WSAGetLastError(), "sun.nio.ch.Net.setIntOption");
        return IOS_THROWN;
    }

    if (opt == java_net_SocketOptions_SO_LINGER)
        return linger.l_onoff ? linger.l_linger : -1;
    else
        return result;
}

JNIEXPORT void JNICALL
Java_sun_nio_ch_Net_setIntOption0(JNIEnv *env, jclass clazz,
                                  jobject fdo, jint opt, jint arg)
{
    int klevel, kopt;
    struct linger linger;
    char *parg;
    int arglen;

    if (NET_MapSocketOption(opt, &klevel, &kopt) < 0) {
        JNU_ThrowByNameWithLastError(env,
                                     JNU_JAVANETPKG "SocketException",
                                     "Unsupported socket option");
        return;
    }

    if (opt == java_net_SocketOptions_SO_LINGER) {
        parg = (char *)&linger;
        arglen = sizeof(linger);
        if (arg >= 0) {
            linger.l_onoff = 1;
            linger.l_linger = (unsigned short)arg;
        } else {
            linger.l_onoff = 0;
            linger.l_linger = 0;
        }
    } else {
        parg = (char *)&arg;
        arglen = sizeof(arg);
    }

    if (NET_SetSockOpt(fdval(env, fdo), klevel, kopt, parg, arglen) < 0) {
        NET_ThrowNew(env, WSAGetLastError(), "sun.nio.ch.Net.setIntOption");
    }
}
