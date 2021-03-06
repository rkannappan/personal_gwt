package com.axioma.reportui.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author rkannappan
 */
public class ServerProxy extends HttpServlet {

  @Override
   protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
      throws ServletException, IOException {
      String restWSUrl = req.getParameter("q");

      restWSUrl = restWSUrl.replaceAll("REPLACE_ME_WITH_AMPERSAND", "&");

      final URL url = new URL(restWSUrl);
      final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

      String str;
      StringBuffer sb = new StringBuffer();
      while ((str = reader.readLine()) != null) {
         sb.append(str);
         sb.append("\n");
      }
      reader.close();

      PrintWriter out = resp.getWriter();
      out.println(sb.toString());
      out.flush();
   }
}