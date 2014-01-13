package org.grible.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.settings.AppTypes;
import org.grible.settings.GlobalSettings;

public class Security {
	public static boolean anyServletEntryCheckFailed(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (!GlobalSettings.getInstance().init(request.getServletContext().getRealPath(""))) {
			response.sendRedirect("/firstlaunch");
			return true;
		}
		if (GlobalSettings.getInstance().getAppType() == AppTypes.JSON) {
			return false;
		}
		if (request.getSession(false) == null) {
			response.sendRedirect("/?url=" + request.getRequestURI() + "?" + request.getQueryString());
			return true;
		}
		if (request.getSession(false).getAttribute("userName") == null) {
			response.sendRedirect("/?url=" + request.getRequestURI() + "?" + request.getQueryString());
			return true;
		}
		return false;
	}
}
