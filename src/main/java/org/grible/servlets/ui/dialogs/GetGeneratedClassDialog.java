/*******************************************************************************
 * Copyright (c) 2013 - 2014 Maksym Barvinskyi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Maksym Barvinskyi - initial API and implementation
 ******************************************************************************/
package org.grible.servlets.ui.dialogs;

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
import org.grible.dao.JsonDao;
import org.grible.dao.PostgresDao;
import org.grible.dbmigrate.oldmodel.Key;
import org.grible.dbmigrate.oldmodel.Value;
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.model.json.KeyJson;
import org.grible.model.json.KeyType;
import org.grible.security.Security;
import org.grible.servlets.ServletHelper;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/GetGeneratedClassDialog")
public class GetGeneratedClassDialog extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Table table;
	private String className;
	private PostgresDao pDao;
	private JsonDao jDao;
	private int productId;

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
		try {
			if (Security.anyServletEntryCheckFailed(request, response)) {
				return;
			}

			int id = Integer.parseInt(request.getParameter("id"));
			productId = Integer.parseInt(request.getParameter("product"));

			if (ServletHelper.isJson()) {
				jDao = new JsonDao();
				table = jDao.getTable(id, productId);
			} else {
				pDao = new PostgresDao();
				table = pDao.getTable(id);
			}

			StringBuilder result = new StringBuilder();
			if (table.getType() == TableType.STORAGE) {
				className = table.getClassName();
				result.append("<div id=\"generated-class-dialog\" class=\"ui-dialog\">");
				result.append("<div class=\"ui-dialog-title\">Generated class</div>");
				result.append("<div class=\"ui-dialog-content\">");
				result.append("<div id=\"tabs\">");
				result.append("<ul>");
				result.append("<li><a href=\"#tabs-1\"><span class=\"dialog-tab\">Java</span></a></li>");
				result.append("<li><a href=\"#tabs-2\"><span class=\"dialog-tab\">C#</span></a></li>");
				result.append("</ul>");
				result.append("<div id=\"tabs-1\" class=\"tab-content\">");
				result.append(getJavaClass());
				result.append("</div>");
				result.append("<div id=\"tabs-2\" class=\"tab-content\">");
				result.append(getCSharpClass());
				result.append("</div>");
				result.append("</div>");
				result.append("<div class=\"dialog-buttons right\">");
				result.append("<button class=\"ui-button btn-cancel\">Close</button> ");
				result.append("</div></div></div>");
			} else if (table.getType() == TableType.ENUMERATION) {
				result.append("<div id=\"generated-class-dialog\" class=\"ui-dialog\">");
				result.append("<div class=\"ui-dialog-title\">Generated enum</div>");
				result.append("<div class=\"ui-dialog-content\">");
				result.append("<div id=\"tabs\">");
				result.append("<ul>");
				result.append("<li><a href=\"#tabs-1\"><span class=\"dialog-tab\">Java</span></a></li>");
				result.append("<li><a href=\"#tabs-2\"><span class=\"dialog-tab\">C#</span></a></li>");
				result.append("</ul>");
				result.append("<div id=\"tabs-1\" class=\"tab-content\">");
				result.append(getJavaEnum());
				result.append("</div>");
				result.append("<div id=\"tabs-2\" class=\"tab-content\">");
				result.append(getCSharpEnum());
				result.append("</div>");
				result.append("</div>");
				result.append("<div class=\"dialog-buttons right\">");
				result.append("<button class=\"ui-button btn-cancel\">Close</button> ");
				result.append("</div></div></div>");
			}
			out.print(result.toString());
		} catch (Exception e) {
			e.printStackTrace(out);
		} finally {
			out.flush();
			out.close();
		}
	}

	private String getJavaEnum() {
		StringBuilder pack = new StringBuilder();
		try {
			pack.append("<br>package com.company.enums;");

			StringBuilder header = new StringBuilder();
			header.append("<br><br>public enum ");
			header.append(table.getName());
			header.append(" {");

			StringBuilder fields = new StringBuilder("<br>");

			List<String> strValues = null;
			if (ServletHelper.isJson()) {
				strValues = jDao.getValuesByKeyOrder(table, 0);
			} else {
				Key key = pDao.getKeys(table.getId()).get(0);
				List<Value> values = pDao.getValues(key);
				strValues = getStringValues(values);
			}
			fields.append(StringUtils.join(strValues, ", "));
			fields.append(";<br>}");
			pack.append(header).append(fields);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return pack.toString();
	}

	private List<String> getStringValues(List<Value> values) {
		List<String> result = new ArrayList<String>();
		for (Value value : values) {
			result.add(value.getValue());
		}
		return result;
	}

	private String getCSharpEnum() {
		StringBuilder pack = new StringBuilder();
		try {
			StringBuilder header = new StringBuilder();
			header.append("<br>public enum ");
			header.append(table.getName());
			header.append("<br>{");

			StringBuilder fields = new StringBuilder("<br>");

			List<String> strValues = null;
			if (ServletHelper.isJson()) {
				strValues = jDao.getValuesByKeyOrder(table, 0);
			} else {
				Key key = pDao.getKeys(table.getId()).get(0);
				List<Value> values = pDao.getValues(key);
				strValues = getStringValues(values);
			}
			fields.append(StringUtils.join(strValues, ", "));
			fields.append("<br>}");
			pack.append(header).append(fields);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return pack.toString();
	}

	private String getJavaClass() {
		StringBuilder pack = new StringBuilder();
		try {
			pack.append("<br>package com.company.descriptors;");

			StringBuilder imp = new StringBuilder();
			imp.append("<br><br>import java.util.HashMap;");
			imp.append("<br>import org.grible.adaptor.BaseDescriptor;");

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

			if (ServletHelper.isJson()) {
				KeyJson[] keys = table.getTableJson().getKeys();
				for (int i = 0; i < keys.length; i++) {
					String keyName = keys[i].getName();
					String fieldName = StringUtils.uncapitalize(keyName).replace(" ", "");
					String type = "String";
					String method = "getString(\"" + keyName + "\");";

					List<String> values = jDao.getValuesByKeyOrder(table, i);
					if (keys[i].getType() == KeyType.TEXT) {
						if (StringUtils.join(values, "").matches("[true|false]+")) {
							type = "boolean";
							method = "getBoolean(\"" + keyName + "\");";
						}
					} else {
						Table refTable = jDao.getTable(keys[i].getRefid(), productId);
						if (keys[i].getType() == KeyType.STORAGE) {
							isDataHelperIncluded = true;
							String refClassName = refTable.getClassName();
							if (StringUtils.join(values, "").contains(";")) {
								isListIncluded = true;
								type = "List&lt;" + refClassName + "&gt;";
								method = "DataStorage.getDescriptors(" + refClassName + ".class, getString(\""
										+ keyName + "\"));";
							} else {
								type = refClassName;
								method = "DataStorage.getDescriptor(" + refClassName + ".class, getString(\"" + keyName
										+ "\"));";
							}
						} else {
							type = refTable.getName();
							method = "(getString(\"" + keyName + "\") != null) ? " + refTable.getName()
									+ ".valueOf(getString(\"" + keyName + "\")) : null;";
						}
					}
					fields.append("<br>private ").append(type).append(" ").append(fieldName).append(";");
					constructor.append("<br>this.").append(fieldName).append(" = ").append(method);

					methods.append("<br><br>public ").append(type).append(" get").append(keyName)
							.append("() {<br>return ").append(fieldName).append(";<br>}");
				}
			} else {
				List<Key> keys = pDao.getKeys(table.getId());
				for (Key key : keys) {
					String keyName = key.getName();
					String fieldName = StringUtils.uncapitalize(keyName).replace(" ", "");
					String type = "String";
					String method = "getString(\"" + keyName + "\");";

					List<Value> values = pDao.getValues(key);
					if (key.getReferenceTableId() == 0) {
						if (isBoolean(values)) {
							type = "boolean";
							method = "getBoolean(\"" + keyName + "\");";
						}
					} else {
						Table refTable = pDao.getTable(key.getReferenceTableId());
						if (refTable.getType() == TableType.STORAGE) {
							isDataHelperIncluded = true;
							String refClassName = refTable.getClassName();
							if (semicolumnExists(values)) {
								isListIncluded = true;
								type = "List&lt;" + refClassName + "&gt;";
								method = "DataStorage.getDescriptors(" + refClassName + ".class, getString(\""
										+ keyName + "\"));";
							} else {
								type = refClassName;
								method = "DataStorage.getDescriptor(" + refClassName + ".class, getString(\"" + keyName
										+ "\"));";
							}
						} else {
							type = refTable.getName();
							method = "(getString(\"" + keyName + "\") != null) ? " + refTable.getName()
									+ ".valueOf(getString(\"" + keyName + "\")) : null;";
						}
					}
					fields.append("<br>private ").append(type).append(" ").append(fieldName).append(";");
					constructor.append("<br>this.").append(fieldName).append(" = ").append(method);

					methods.append("<br><br>public ").append(type).append(" get").append(keyName)
							.append("() {<br>return ").append(fieldName).append(";<br>}");
				}
			}

			if (isListIncluded) {
				imp.append("<br>import java.util.List;");
			}

			if (isDataHelperIncluded) {
				imp.append("<br>import org.grible.adaptor.DataStorage;");
			}

			constructor.append("<br>}");
			pack.append(imp).append(header).append(fields).append(constructor).append(methods).append("<br>}");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return pack.toString();
	}

	private boolean semicolumnExists(List<Value> values) {
		List<String> strValues = new ArrayList<String>();
		for (Value value : values) {
			strValues.add(value.getValue());
		}
		String allValues = StringUtils.join(strValues, "");
		return allValues.contains(";");
	}

	private boolean isBoolean(List<Value> values) {
		List<String> strValues = new ArrayList<String>();
		for (Value value : values) {
			strValues.add(value.getValue());
		}
		String allValues = StringUtils.join(strValues, "");
		return allValues.matches("[true|false]+");
	}

	private String getCSharpClass() {
		StringBuilder imp = new StringBuilder();
		try {
			imp.append("<br>using System.Collections.Generic;");
			imp.append("<br>using GribleAdaptor;");

			StringBuilder namespace = new StringBuilder();
			namespace.append("<br><br>namespace Your.Namespase.For.Descriptors<br>{");

			StringBuilder header = new StringBuilder();
			header.append("<br>public class ");
			header.append(className);
			header.append(" : BaseDescriptor<br>{");

			StringBuilder properties = new StringBuilder();
			StringBuilder constructor = new StringBuilder("<br><br>public ");
			constructor.append(className);
			constructor.append("(Dictionary&lt;string, string&gt; data) : base(data)<br>{");

			if (ServletHelper.isJson()) {
				KeyJson[] keys = table.getTableJson().getKeys();
				for (int i = 0; i < keys.length; i++) {
					String keyName = keys[i].getName().replace(" ", "");
					String type = "string";
					String method = "GetString(\"" + keyName + "\");";

					List<String> values = jDao.getValuesByKeyOrder(table, i);
					if (keys[i].getType() == KeyType.TEXT) {
						if (StringUtils.join(values, "").matches("[true|false]+")) {
							type = "bool";
							method = "GetBoolean(\"" + keyName + "\");";
						}
					} else {
						Table refTable = jDao.getTable(keys[i].getRefid(), productId);
						if (keys[i].getType() == KeyType.STORAGE) {
							String refClassName = refTable.getClassName();
							if (StringUtils.join(values, "").contains(";")) {
								type = "List&lt;" + refClassName + "&gt;";
								method = "DataStorage.GetDescriptors&lt;" + refClassName + "&gt;(GetString(\""
										+ keyName + "\"));";
							} else {
								type = refClassName;
								method = "DataStorage.GetDescriptor&lt;" + refClassName + "&gt;(GetString(\"" + keyName
										+ "\"));";
							}
						} else {
							type = refTable.getName();
							method = "(" + refTable.getName() + ") Enum.Parse(typeof(" + refTable.getName()
									+ "), GetString(\"" + keyName + "\"));";
						}
					}
					properties.append("<br>public ").append(type).append(" ").append(keyName)
							.append(" { get; private set; }");
					constructor.append("<br>").append(keyName).append(" = ").append(method);
				}
			} else {
				List<Key> keys = pDao.getKeys(table.getId());
				for (Key key : keys) {
					String keyName = key.getName().replace(" ", "");
					String type = "string";
					String method = "GetString(\"" + keyName + "\");";

					List<Value> values = pDao.getValues(key);
					if (key.getReferenceTableId() == 0) {
						if (isBoolean(values)) {
							type = "bool";
							method = "GetBoolean(\"" + keyName + "\");";
						}
					} else {
						PostgresDao dao = pDao;
						Table refTable = dao.getTable(key.getReferenceTableId());
						if (refTable.getType() == TableType.STORAGE) {
							String refClassName = dao.getTable(key.getReferenceTableId()).getClassName();
							if (semicolumnExists(values)) {
								type = "List&lt;" + refClassName + "&gt;";
								method = "DataStorage.GetDescriptors&lt;" + refClassName + "&gt;(GetString(\""
										+ keyName + "\"));";
							} else {
								type = refClassName;
								method = "DataStorage.GetDescriptor&lt;" + refClassName + "&gt;(GetString(\"" + keyName
										+ "\"));";
							}
						} else {
							type = refTable.getName();
							method = "(" + refTable.getName() + ") Enum.Parse(typeof(" + refTable.getName()
									+ "), GetString(\"" + keyName + "\"));";
						}
					}
					properties.append("<br>public ").append(type).append(" ").append(keyName)
							.append(" { get; private set; }");
					constructor.append("<br>").append(keyName).append(" = ").append(method);
				}
			}

			constructor.append("<br>}");
			imp.append(namespace).append(header).append(properties).append(constructor).append("<br>}").append("<br>}");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return imp.toString();
	}

}
