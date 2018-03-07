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

package de.iwes.widgets.html.start;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 *
 * @author tgries
 */
@Deprecated
public class WidgetsHtmlServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
//	private static final String s = File.separator;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
           
        String action = req.getParameter("action");
        
        if(action != null) {
            if(action.equals("getHttpJson")) {
                
                List<String> paths = new ArrayList<String>();
                JSONObject jsonObject = new JSONObject();
                
                final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

                if(jarFile.isFile()) {  // Run with JAR file
                    final JarFile jar = new JarFile(jarFile);
                    final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
                    while(entries.hasMoreElements()) {
                        final String name = entries.nextElement().getName();
                        if (name.endsWith("html")) { //filter according to the path
                            paths.add("/" + name);
                        }
                    }
                    jar.close();
                }

                for(String path : paths) {

                    StringBuffer result = new StringBuffer();
                    try (Scanner scanner = new Scanner(this.getClass().getResourceAsStream(path))) {
                        result = new StringBuffer("");
                        while (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            result.append(line);
                        }
                    }

                    int lastIndex = path.lastIndexOf("/");
                    int secondLastIndex = path.substring(0 , lastIndex).lastIndexOf("/");

                    String shortPath = path.substring(secondLastIndex);
                    //System.out.println(shortPath);
                    jsonObject.put(shortPath, result);
                }
                
                resp.setContentType("application/json");
                jsonObject.write(resp.getWriter());
                resp.setStatus(200);
                
            }
        }
        
        
    }
    
}
