package org.pine.servlets.pages.dialogs;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.pine.model.storages.DataStorage;
import org.pine.model.storages.DataStorageKey;
import org.pine.model.storages.DataStorageValue;
import org.pine.sql.SQLHelper;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/GetGeneratedClassDialog")
public class GetGeneratedClassDialog extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private SQLHelper sqlHelper;
	private DataStorage storage;
	private String className;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetGeneratedClassDialog() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		sqlHelper = new SQLHelper();

		int id = Integer.parseInt(request.getParameter("id"));
		storage = sqlHelper.getDataStorage(id);
		className = storage.getClassName();

		out.println("<div id=\"generated-class-dialog\" class=\"ui-dialog\">");
		out.println("<div class=\"ui-dialog-title\">Generated class</div>");
		out.println("<div class=\"ui-dialog-content\">");
		out.println("<div id=\"tabs\">");
		out.println("<ul>");
		out.println("<li><a href=\"#tabs-1\"><span class=\"dialog-tab\">Java</span></a></li>");
		out.println("<li><a href=\"#tabs-2\"><span class=\"dialog-tab\">C#</span></a></li>");
		out.println("<li><a href=\"#tabs-3\"><span class=\"dialog-tab\">Objective-C</span></a></li>");
		out.println("</ul>");
		out.println("<div id=\"tabs-1\" class=\"tab-content\">");
		out.println(getJavaClass());
		out.println("</div>");
		out.println("<div id=\"tabs-2\" class=\"tab-content\">");
		out.println(getCSharpClass());
		out.println("</div>");
		out.println("<div id=\"tabs-3\" class=\"tab-content\">");
		out.println("<br>In progress.");
		out.println("</div>");
		out.println("</div>");
		out.println("<div class=\"dialog-buttons right\">");
		out.println("<button class=\"ui-button btn-cancel\">Close</button> ");
		out.println("</div></div></div>");

		out.flush();
		out.close();

	}

	private String getJavaClass() {
		StringBuilder pack = new StringBuilder();
		pack.append("<br>package com.company.descriptors;");

		StringBuilder imp = new StringBuilder();
		imp.append("<br><br>import java.util.HashMap;");
		imp.append("<br>import com.globallogic.zed.descriptors.BaseDescriptor;");

		boolean isListIncluded = false;
		boolean isDataHelperIncluded = false;

		StringBuilder header = new StringBuilder();
		header.append("<br><br>public class ");
		header.append(className);
		header.append(" extends BaseDescriptor {");

		StringBuilder fields = new StringBuilder("<br>");
		StringBuilder methods = new StringBuilder();
		StringBuilder constructor = new StringBuilder("<br><br>public ");
		constructor.append(className);
		constructor.append("(HashMap&lt;String, String&gt; data) {");
		constructor.append("<br>super(data);");

		List<DataStorageKey> keys = sqlHelper.getDataStorageKeys(storage.getId());
		for (DataStorageKey key : keys) {
			String keyName = key.getName();
			String fieldName = StringUtils.uncapitalize(keyName);
			String type = "String";
			String method = "getString(\"" + keyName + "\");";

			List<DataStorageValue> values = sqlHelper.getDataStorageValues(key);
			if (key.getReferenceStorageId() == 0) {
				if (isBoolean(values)) {
					type = "boolean";
					method = "getBoolean(\"" + keyName + "\");";
				} else if (isInteger(values)) {
					type = "int";
					method = "getInt(\"" + keyName + "\");";
				}
			} else {
				isDataHelperIncluded = true;
				String refClassName = sqlHelper.getDataStorage(key.getReferenceStorageId()).getClassName();
				if (semicoulumExists(values)) {
					isListIncluded = true;
					type = "List&lt;" + refClassName + "&gt;";
					method = "DataHelper.getDescriptorsFromDC(" + refClassName + ".class, getString(\"" + keyName
							+ "\"));";
				} else {
					type = refClassName;
					method = "DataHelper.getDescriptorFromDC(" + refClassName + ".class, getString(\"" + keyName
							+ "\"));";
				}
			}
			fields.append("<br>private ").append(type).append(" ").append(fieldName).append(";");
			constructor.append("<br>this.").append(fieldName).append(" = ").append(method);

			methods.append("<br><br>public ").append(type).append(" get").append(keyName).append("() {<br>return ")
					.append(fieldName).append(";<br>}");
		}

		if (isListIncluded) {
			imp.append("<br>import java.util.List;");
		}

		if (isDataHelperIncluded) {
			imp.append("<br>import com.globallogic.zed.data.DataHelper;");
		}

		constructor.append("<br>}");
		pack.append(imp).append(header).append(fields).append(constructor).append(methods).append("<br>}");

		return pack.toString();
	}

	private boolean semicoulumExists(List<DataStorageValue> values) {
		List<String> strValues = new ArrayList<>();
		for (DataStorageValue value : values) {
			strValues.add(value.getValue());
		}
		String allValues = StringUtils.join(strValues, "");
		return allValues.contains(";");
	}

	private boolean isBoolean(List<DataStorageValue> values) {
		List<String> strValues = new ArrayList<>();
		for (DataStorageValue value : values) {
			strValues.add(value.getValue());
		}
		String allValues = StringUtils.join(strValues, "");
		return allValues.matches("[true|false]+");
	}

	private boolean isInteger(List<DataStorageValue> values) {
		List<String> strValues = new ArrayList<>();
		for (DataStorageValue value : values) {
			strValues.add(value.getValue());
		}
		String allValues = StringUtils.join(strValues, "");
		return allValues.matches("\\d+");
	}

	private String getCSharpClass() {
		StringBuilder imp = new StringBuilder();
		imp.append("<br>using System.Collections.Generic;");
		imp.append("<br>using Zed.Framework.Descriptors;");

		StringBuilder namespace = new StringBuilder();
		namespace.append("<br><br>namespace Your.Namespase.For.Descriptors<br>{");

		boolean isListIncluded = false;
		boolean isDataHelperIncluded = false;

		StringBuilder header = new StringBuilder();
		header.append("<br>public class ");
		header.append(className);
		header.append(" : BaseDescriptor<br>{");

		StringBuilder properties = new StringBuilder();
		StringBuilder constructor = new StringBuilder("<br><br>public ");
		constructor.append(className);
		constructor.append("(Dictionary&lt;string, string&gt; data) : base(data)<br>{");

		List<DataStorageKey> keys = sqlHelper.getDataStorageKeys(storage.getId());
		for (DataStorageKey key : keys) {
			String keyName = key.getName();
			String type = "string";
			String method = "GetString(\"" + keyName + "\");";

			List<DataStorageValue> values = sqlHelper.getDataStorageValues(key);
			if (key.getReferenceStorageId() == 0) {
				if (isBoolean(values)) {
					type = "bool";
					method = "GetBoolean(\"" + keyName + "\");";
				} else if (isInteger(values)) {
					type = "int";
					method = "GetInt(\"" + keyName + "\");";
				}
			} else {
				isDataHelperIncluded = true;
				String refClassName = sqlHelper.getDataStorage(key.getReferenceStorageId()).getClassName();
				if (semicoulumExists(values)) {
					isListIncluded = true;
					type = "List&lt;" + refClassName + "&gt;";
					method = "DataHelper.GetDescriptorsFromDC&lt;" + refClassName + "&gt;(GetString(\"" + keyName
							+ "\"));";
				} else {
					type = refClassName;
					method = "DataHelper.GetDescriptorFromDC&lt;" + refClassName + "&gt;(GetString(\"" + keyName
							+ "\"));";
				}
			}
			properties.append("<br>public ").append(type).append(" ").append(keyName).append(" { get; private set; }");
			constructor.append("<br>").append(keyName).append(" = ").append(method);
		}

		if (isListIncluded) {
			//imp.append("<br>using Zed.Framework.Descriptors;");
		}

		if (isDataHelperIncluded) {
			imp.append("<br>using Zed.Framework.Data;");
		}

		constructor.append("<br>}");
		imp.append(namespace).append(header).append(properties).append(constructor).append("<br>}").append("<br>}");

		return imp.toString();
	}

}
