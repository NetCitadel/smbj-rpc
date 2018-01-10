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

import com.rapid7.client.dcerpc.mserref.SystemErrorCode;
import com.rapid7.client.dcerpc.msvcctl.dto.ServiceManagerHandle;
import com.rapid7.client.dcerpc.msvcctl.dto.enums.ServiceError;
import com.rapid7.client.dcerpc.msvcctl.dto.enums.ServiceManagerAccessLevel;
import com.rapid7.client.dcerpc.msvcctl.dto.enums.ServiceStartType;
import com.rapid7.client.dcerpc.msvcctl.dto.enums.ServiceType;
import com.rapid7.client.dcerpc.objects.WChar;
import javax.xml.bind.DatatypeConverter;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Test_RCreateService {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @SuppressWarnings("unchecked")
    @Test
    public void readRCreateServiceResponse() throws IOException {
        final RCreateServiceWResponse response = new RCreateServiceWResponse();
        response.fromHexString("123456780000000000000000856EAAB53335904AA1B43E4670603AF100000000");
        assertEquals(0, response.getTagId().intValue());
        assertTrue(Arrays.equals(Hex.decode("00000000856EAAB53335904AA1B43E4670603AF1"), response.getHandle()));
        assertTrue(SystemErrorCode.ERROR_SUCCESS.is(response.getReturnValue()));
    }

    @Test
    public void encodeCreateServiceRequest() throws IOException {
        byte[] testHandle = Hex.decode("00000000306786C75D720E489E4D20FF45B9D8A5");

        RCreateServiceWRequest request = new RCreateServiceWRequest(testHandle, WChar.NullTerminated.of("smbtest"),
                                                                    WChar.NullTerminated.of("SMB Test"),
                                                                    ServiceManagerAccessLevel.ALL_ACCESS.getValue(),
                                                                    ServiceType.WIN32_OWN_PROCESS.getValue(),
                                                                    ServiceStartType.DEMAND_START.getValue(),
                                                                    ServiceError.NORMAL.getValue(),
                                                                    WChar.NullTerminated.of("\"C:\\Windows\\test\\test.exe\""),
                                                                    null,
                                                                    0,
                                                                    null,
                                                                    null,
                                                                    null );
        assertEquals("00000000306786c75d720e489e4d20ff45b9d8a508000000000000000800000073006d006200740065007300740000000000020009000000000000000900000053004d00420020005400650073007400000000003f000f001000000003000000010000001b000000000000001b000000220043003a005c00570069006e0064006f00770073005c0074006500730074005c0074006500730074002e0065007800650022000000000000000000000000000000000000000000000000000000000000000000", request.toHexString());
    }

    @Test
    public void encodeCreateServiceRequestComplete() throws IOException {
        byte[] testHandle = Hex.decode("00000000306786C75D720E489E4D20FF45B9D8A5");

        RCreateServiceWRequest request = new RCreateServiceWRequest(testHandle, WChar.NullTerminated.of("smbtest"),
                                                                    WChar.NullTerminated.of("SMB Test"),
                                                                    ServiceManagerAccessLevel.ALL_ACCESS.getValue(),
                                                                    ServiceType.WIN32_OWN_PROCESS.getValue(),
                                                                    ServiceStartType.DEMAND_START.getValue(),
                                                                    ServiceError.NORMAL.getValue(),
                                                                    WChar.NullTerminated.of("\"C:\\Windows\\test\\test.exe\""),
                                                                    WChar.NullTerminated.of("group"),
                                                                    1,
                                                                    new String[]{"dependency1","dependency2"},
                                                                    WChar.NullTerminated.of(".\\user"),
                                                                    "password" );
        assertEquals("00000000306786c75d720e489e4d20ff45b9d8a508000000000000000800000073006d006200740065007300740000000000020009000000000000000900000053004d00420020005400650073007400000000003f000f001000000003000000010000001b000000000000001b000000220043003a005c00570069006e0064006f00770073005c0074006500730074005c0074006500730074002e0065007800650022000000000004000200060000000000000006000000670072006f0075007000000008000200010000000c0002003200000064006500700065006e00640065006e00630079003100000064006500700065006e00640065006e0063007900320000000000000032000000100002000700000000000000070000002e005c007500730065007200000000001400020012000000700061007300730077006f00720064000000000012000000", request.toHexString());
    }
}
