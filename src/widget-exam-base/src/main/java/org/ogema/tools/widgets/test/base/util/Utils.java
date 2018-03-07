/**
 * This file is part of the OGEMA widgets framework.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2014 - 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
 */

package org.ogema.tools.widgets.test.base.util;

import java.io.Closeable;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.Charsets;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Utils {

	
	public static final JSONObject getDataFromResponse(HttpResponse resp) throws ParseException, IOException {
		StatusLine sl = resp.getStatusLine();
		if (sl.getStatusCode() != HttpServletResponse.SC_OK)
			throw new IOException("Status code not ok: " + sl.getStatusCode() + ": " + sl.getReasonPhrase());
		String json = EntityUtils.toString(resp.getEntity(),Charsets.UTF_8);
		// System.out.println("     zzz json answer " + json);
		return new JSONObject(json);
	}
	
	public static final void closeSmoothly(Closeable stream) {
		if (stream == null)
			return;
		try {
			stream.close();
		} catch (Exception e) {}
		
	}
	
	public final static String[] extractUserAndPw(String html) {
		int idx = html.indexOf("<script");
		while (idx >= 0 && idx != html.length()-7) {
			final int idxClosed = html.indexOf('>', idx);
			if (idxClosed < 0 || idxClosed == html.length()-1)
				return null;
			final int idxEnd = html.indexOf("</script>", idxClosed);
			if (idxEnd < 0)
				return null;
			final String javascript = html.substring(idxClosed+1, idxEnd);
			final String user = extractVar(javascript, "otusr");
			if (user != null) {
				final String pw = extractVar(javascript, "otpwd");
				if (pw != null)
					return new String[] {user,pw};
			}
			html = html.substring(idxEnd + 8);
			idx = html.indexOf("<script");
		}
		return null;
	}
	
	private final static String extractVar(String javascript, final String varName) {
		final int idx = javascript.indexOf(varName + "=");
		if (idx < 0)
			return null;
		final int idx0 = javascript.indexOf('\'',idx+ varName.length());
		final int idx1 = javascript.indexOf('\"',idx+ varName.length());
		if (idx0 <0 && idx1 <0)
			return null;
		final boolean swtch = idx0 > 0 && (idx1 >0 && idx0 < idx1 || idx1<0);
		final int idxEnd = javascript.indexOf(swtch? '\'' : '\"', swtch ? idx0+1 : idx1+1);
		if (idxEnd < 0)
			return null;
		return javascript.substring( swtch ? idx0+1 : idx1+1, idxEnd);
	}
	
}
