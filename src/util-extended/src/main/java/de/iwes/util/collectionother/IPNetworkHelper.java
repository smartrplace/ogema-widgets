/**
 * ﻿Copyright 2014-2018 Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.iwes.util.collectionother;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/** Static methods for processing IP addresses*/
public class IPNetworkHelper {
	/** Get IP address of OGEMA system. The method tries to identify an address
	 * that does not represent localhost, but that can be reached at least from
	 * the local network
	 * @return standard String representation of the IP address
	 */
	public static String getLocalIPAddress() {
		String ipList = null;
		try {
			Enumeration<?> e;
			e = NetworkInterface.getNetworkInterfaces();
			while(e.hasMoreElements())
			{
			    NetworkInterface n = (NetworkInterface) e.nextElement();
			    Enumeration<?> ee = n.getInetAddresses();
			    while (ee.hasMoreElements())
			    {
			        InetAddress i = (InetAddress) ee.nextElement();
			        String s = i.getHostAddress();
			        if(s.startsWith("127")) continue;
			        if(s.contains(":")) continue;
			        if(ipList == null) {
				        ipList = s;			        	
			        } else {
			        	ipList += ", "+s;
			        }
			    }
			}
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		if(ipList == null) return "none";
		return ipList;
	}
}
