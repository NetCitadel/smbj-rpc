/*
 * Copyright 2017, Rapid7, Inc.
 *
 * License: BSD-3-clause
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 *  Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 *
 */

package com.rapid7.client.dcerpc.msvcctl.messages;

import com.rapid7.client.dcerpc.io.PacketOutput;
import com.rapid7.client.dcerpc.io.ndr.Alignment;
import com.rapid7.client.dcerpc.messages.RequestCall;
import com.rapid7.client.dcerpc.objects.WChar;
import java.io.IOException;

public class RCreateServiceWRequest extends RequestCall<RCreateServiceWResponse> {
    private final static short OP_NUM = 12;
    private final byte[] hSCManager;
    private final WChar.NullTerminated lpServiceName;
    private final WChar.NullTerminated lpDisplayName;
    private final int dwDesiredAccess;
    private final int dwServiceType;
    private final int dwStartType;
    private final int dwErrorControl;
    private final WChar.NullTerminated lpBinaryPathName;
    private final WChar.NullTerminated lpLoadOrderGroup;
    private final Integer lpDwTagId;
    private final String[] lpDependencies;
    private final WChar.NullTerminated lpServiceStartName;
    private final String lpPassword;

    public RCreateServiceWRequest(final byte[] hSCManager, final WChar.NullTerminated lpServiceName, final WChar.NullTerminated lpDisplayName,
            final int dwDesiredAccess, final int dwServiceType, final int dwStartType, final int dwErrorControl,
            final WChar.NullTerminated lpBinaryPathName, final WChar.NullTerminated lpLoadOrderGroup, final Integer lpDwTagId,
            final String[] lpDependencies, final WChar.NullTerminated lpServiceStartName, final String lpPassword) {
        super(OP_NUM);
        this.hSCManager = hSCManager;
        this.lpServiceName = lpServiceName;
        this.lpDisplayName = lpDisplayName;
        this.dwDesiredAccess = dwDesiredAccess;
        this.dwServiceType = dwServiceType;
        this.dwStartType = dwStartType;
        this.dwErrorControl = dwErrorControl;
        this.lpBinaryPathName = lpBinaryPathName;
        this.lpLoadOrderGroup = lpLoadOrderGroup;
        this.lpDwTagId = lpDwTagId;
        this.lpDependencies = lpDependencies;
        this.lpServiceStartName = lpServiceStartName;
        this.lpPassword = lpPassword;
    }

    @Override
    public void marshal(PacketOutput packetOut) throws IOException {
        // <NDR: fixed array> [in] SC_RPC_HANDLE hSCManager
        packetOut.write(this.hSCManager);
        // <NDR: struct> [in, string, range(0, SC_MAX_NAME_LENGTH)] wchar_t* lpServiceName
        // Despite what the documentation states, this is not a pointer
        packetOut.writeMarshallable(this.lpServiceName);
        packetOut.align(Alignment.FOUR);

        // <NDR: pointer[struct]> [in, string, unique, range(0, SC_MAX_NAME_LENGTH)] wchar_t* lpDisplayName
        if (packetOut.writeReferentID(this.lpDisplayName)) {
            packetOut.writeMarshallable(this.lpDisplayName);
            packetOut.align(Alignment.FOUR);
        }

        // <NDR: unsigned long> [in] DWORD dwDesiredAccess
        // Alignment: 4 - Already aligned
        packetOut.writeInt(this.dwDesiredAccess);
        // <NDR: unsigned long> [in] DWORD dwServiceType
        // Alignment: 4 - Already aligned
        packetOut.writeInt(this.dwServiceType);
        // <NDR: unsigned long> [in] DWORD dwStartType
        // Alignment: 4 - Already aligned
        packetOut.writeInt(this.dwStartType);
        // <NDR: unsigned long> [in] DWORD dwErrorControl
        // Alignment: 4 - Already aligned
        packetOut.writeInt(this.dwErrorControl);
        // <NDR: pointer[struct]> [in, string, unique, range(0, SC_MAX_PATH_LENGTH)] wchar_t* lpBinaryPathName
        // Alignment: 4 - Already aligned
        // Despite what the documentation states, this is not a pointer
        packetOut.writeMarshallable(this.lpBinaryPathName);
        // Alignment for lpLoadOrderGroup
        packetOut.align(Alignment.FOUR);

        // <NDR: pointer[struct]> [in, string, unique, range(0, SC_MAX_NAME_LENGTH)] wchar_t* lpLoadOrderGroup
        if (packetOut.writeReferentID(this.lpLoadOrderGroup)) {
            packetOut.writeMarshallable(this.lpLoadOrderGroup);
            // Alignment for lpdwTagId
            packetOut.align(Alignment.FOUR);
        }

        // <NDR: pointer[unsigned long]> [in, out, unique] LPDWORD lpdwTagId
        if (this.lpDwTagId != null && this.lpDwTagId != 0) {
            packetOut.writeReferentID();
            packetOut.writeInt(this.lpDwTagId);
        } else {
            packetOut.writeNull();
        }

        // <NDR: pointer[conformant array]> [in, unique, size_is(dwDependSize)] LPBYTE lpDependencies
        if (packetOut.writeReferentID(this.lpDependencies)) {
            // Count the number of bytes required for the array of dependencies
            // This is better than allocating a new byte[] to hold everything.
            // At the very least we have a null terminator at the end
            int byteCount = 2;
            for (final String dependency : this.lpDependencies) {
                // Number of UTF-16 bytes including null terminator
                byteCount += ((dependency == null) ? 0 : dependency.length() * 2) + 2;
            }
            // MaximumCount for Conformant Array
            packetOut.writeInt(byteCount);
            // Entries
            for (final String dependency : this.lpDependencies) {
                if (dependency != null) {
                    // This is better than allocating a new char[]
                    for (int i = 0; i < dependency.length(); i++) {
                        // UTF-16 little endian
                        packetOut.writeChar(dependency.charAt(i));
                    }
                }
                // Each entry is null terminated
                packetOut.writeChar(0);
            }
            // Array is doubly null terminated
            packetOut.writeChar(0);
            // <NDR: unsigned long> [in, range(0, SC_MAX_DEPEND_SIZE)] DWORD dwDependSize
            packetOut.align(Alignment.FOUR);
            packetOut.writeInt(byteCount);
        } else {
            // <NDR: unsigned long> [in, range(0, SC_MAX_DEPEND_SIZE)] DWORD dwDependSize
            packetOut.writeInt(0);
        }

        // <NDR: pointer[struct]> [in, string, unique, range(0, SC_MAX_ACCOUNT_NAME_LENGTH)] wchar_t* lpServiceStartName
        if (packetOut.writeReferentID(this.lpServiceStartName)) {
            packetOut.writeMarshallable(this.lpServiceStartName);
            // Alignment for lpPassword
            packetOut.align(Alignment.FOUR);
        }

        // <NDR: conformant array> [in, unique, size_is(dwPwSize)] LPBYTE lpPassword
        if (packetOut.writeReferentID(this.lpPassword)) {
            final int byteCount = (this.lpPassword.length() * 2) + 2;
            packetOut.writeInt(byteCount);
            for (int i = 0; i < this.lpPassword.length(); i++) {
                // UTF-16 little endian
                packetOut.writeChar(this.lpPassword.charAt(i));
            }
            // Null terminated
            packetOut.writeChar(0);
            // <NDR: unsigned long> [in, range(0, SC_MAX_PWD_SIZE)] DWORD dwPwSize
            packetOut.align(Alignment.FOUR);
            packetOut.writeInt(byteCount);
        } else {
            // <NDR: unsigned long> [in, range(0, SC_MAX_PWD_SIZE)] DWORD dwPwSize
            packetOut.writeInt(0);
        }
    }

    @Override
    public RCreateServiceWResponse getResponseObject() {
        return new RCreateServiceWResponse();
    }

}
