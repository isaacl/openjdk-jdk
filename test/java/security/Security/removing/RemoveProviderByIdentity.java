/*
 * Copyright 1999 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
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
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

/*
 * @test
 * @bug 4208414
 * @summary Providers should be removed "by-identity" - not "by-value"
 */

import java.security.*;

public class RemoveProviderByIdentity {

    public static void main(String[] args) throws Exception {
	String PROVIDER_NAME = "myprovider";

	Security.addProvider(new MyProvider(PROVIDER_NAME, 1, "test"));
	if (Security.getProvider(PROVIDER_NAME) == null)
	    throw new Exception("provider not registered");

	Security.removeProvider(PROVIDER_NAME);
	if (Security.getProvider(PROVIDER_NAME) != null)
	    throw new Exception("provider not removed");
    }
}

class MyProvider extends Provider {
    public MyProvider(String name, double version, String info) {
        super(name, version, info);
        put("Signature.sigalg", "sigimpl");
    }
}
