/**
 * Copyright 2017, Rapid7, Inc.
 *
 * License: BSD-3-clause
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 */
package com.rapid7.client.dcerpc.mssamr.messages;

import static com.rapid7.client.dcerpc.mssamr.objects.DisplayInformationClass.DomainDisplayGroup;
import static org.junit.Assert.assertEquals;
import java.io.IOException;
import org.junit.Test;
import com.rapid7.client.dcerpc.mssamr.objects.SAMPRDomainDisplayGroup;

public class Test_SamrQueryDisplayInformation2Response {

    @Test
    public void unmarshallGroupDisplayInfo() throws IOException {
        SamrQueryDisplayInformation2Response<SAMPRDomainDisplayGroup> response = new SamrQueryDisplayInformation2Response(DomainDisplayGroup);
        response.fromHexString(
            "54000000540000000300000001000000000002000100000001000000010200000700000008000800040002001c001c00080002000400000000000000040000004e006f006e0065000e000000000000000e0000004f007200640069006e0061007200790020007500730065007200730000000000");
        assertEquals(84, response.getTotalAvailableBytes());
        assertEquals(84, response.getTotalReturnedBytes());
        assertEquals(1, response.getList().size());
    }

}
