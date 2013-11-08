$(window).on(
		"load",
		function() {
			var docHeight = $(window).height() - 95;
			var docWidth = $(window).width() - 45;
			var breadcrumbHeight = $("#breadcrumb").height();
			var footerHeight = $("#footer").outerHeight();
			var mainHeight = docHeight - breadcrumbHeight - footerHeight - 27;
			$("#table-container").width(
					docWidth - $("#delimiter").width()
							- $(".left-panel").width() - 10);

			if (isChrome()) {
				$("#main .table-cell").addClass("floatleft");
			}

			$("#main").height(mainHeight);
			$("#table-container").height(mainHeight);
			$(".left-panel").height(mainHeight);
			$("#entities-list").height(mainHeight);
		});

$().ready(initialize());

var source = '<div class="table-row key-row">'
		+ '{{#if isIndex}}'
		+ '<div class="table-cell ui-cell index-header-cell">Index</div>'
		+ '{{/if}}'
		+ '{{#each keys}}'
		+ '<div class="table-cell ui-cell key-cell" key-order="{{order}}" id="{{id}}">{{text}}</div>'
		+ '{{/each}}'
		+ '{{#if info}}'
		+ '<div class="table-cell ui-cell info-key-cell">{{tables}}</div>'
		+ '<div class="table-cell ui-cell info-key-cell">{{storages}}</div>'
		+ '{{/if}}'
		+ '</div>'
		+ '{{#each values}}'
		+ '<div class="table-row value-row">'
		+ '{{#if index}}'
		+ '{{#with index}}'
		+ '<div class="table-cell ui-cell index-cell" id="{{id}}">{{order}}</div>'
		+ '{{/with}}'
		+ '{{/if}}'
		+ '{{#each values}}'
		+ '<div class="table-cell ui-cell value-cell'
		+ '{{#if isStorage}} storage-cell {{/if}} {{#if isEnum}} enum-cell {{/if}}"'
		+ 'rowid="{{rowid}}" keyid="{{keyid}}" id="{{id}}">{{text}}</div>'
		+ '{{/each}}' + '{{#if info}}'
		+ '<div class="table-cell ui-cell info-cell">{{tables}}</div>'
		+ '<div class="table-cell ui-cell info-cell">{{storages}}</div>'
		+ '{{/if}}' + '</div>' + '{{/each}}';
var template = Handlebars.compile(source);

function initialize() {

	$(document)
			.ajaxError(
					function(e, xhr, settings, exception) {
						var exrrorText = xhr.responseText
								.substring(xhr.responseText.indexOf("<h1>"));
						$("body")
								.append(
										'<div id="error-dialog" class="ui-dialog">'
												+ '<div class="ui-dialog-title">Error</div>'
												+ '<div class="ui-dialog-content">'
												+ 'Location: '
												+ settings.url
												+ '<br><br>'
												+ exrrorText
												+ '<br><br>'
												+ '<div class="right">'
												+ '<button class="ui-button btn-cancel">OK</button>'
												+ '</div></div></div>');
						initOneButtonDialog(jQuery);
					});

	$.post("../GetCategories", {
		productId : productId,
		tableId : tableId,
		tableType : tableType
	}, function(data) {
		$("#category-container").html(data);
		initLeftPanel(jQuery);
	});
}

function initLeftPanel() {

	$("#waiting-bg").removeClass("loading");

	$(".categories").accordion({
		collapsible : true,
		animate : 200,
		active : false,
		heightStyle : "content"
	});

	$(".category-item-selected").click();

	$(".category-item").click(function() {
		var thisCategoryItem = $(this);
		if ($("#btn-save-data-item").hasClass("button-enabled")) {
			noty({
				type : "confirm",
				text : "Leave unsaved table?",
				buttons : [ {
					addClass : 'btn btn-primary ui-button',
					text : 'Yes',
					onClick : function($noty) {
						$noty.close();
						$("#btn-save-data-item").removeClass("button-enabled");
						onCategoryItemClick(thisCategoryItem);
					}
				}, {
					addClass : 'btn btn-danger ui-button',
					text : 'No',
					onClick : function($noty) {
						$noty.close();
					}
				} ]
			});
		} else {
			onCategoryItemClick(thisCategoryItem);
		}
	});

	function onCategoryItemClick(thisCategoryItem) {
		$(".category-item-selected").removeClass("category-item-selected");
		thisCategoryItem.addClass("category-item-selected");
		$(".data-item-selected").removeClass("data-item-selected");
		$(".data-item-selected").find(".changed-sign").remove();
		$(".top-panel").find("div").hide();
		$("#table-container > div.entities-values > div").remove();
		$("#table-container").hide();
		if ($("#breadcrumb>a").length > 3) {
			$(".extends-symbol").last().remove();
			$("#breadcrumb>a").last().remove();
			$("#section-name").removeClass("link-infront");
		}
		history.pushState({
			product : productId
		}, "", "?product=" + productId);
		document.title = $("#section-name").text() + " - Grible";
	}

	$(".category-item").mousedown(function(event) {
		if (event.which === 3) {
			$(".category-item-selected").removeClass("category-item-selected");
			$(this).addClass("category-item-selected");
		}
	});

	$(".data-item").click(function() {
		var thisDataItem = $(this);
		if ($("#btn-save-data-item").hasClass("button-enabled")) {
			noty({
				type : "confirm",
				text : "Leave unsaved table?",
				buttons : [ {
					addClass : 'btn btn-primary ui-button',
					text : 'Yes',
					onClick : function($noty) {
						$noty.close();
						$("#btn-save-data-item").removeClass("button-enabled");
						onDataItemClick(thisDataItem);
					}
				}, {
					addClass : 'btn btn-danger ui-button',
					text : 'No',
					onClick : function($noty) {
						$noty.close();
					}
				} ]
			});
		} else {
			onDataItemClick(thisDataItem);
		}
	});

	function onDataItemClick(thisDataItem) {
		if (isChrome()) {
			$("#main .table-cell").addClass("floatleft");
		}
		$("#waiting-bg").addClass("loading");
		$(".data-item-selected").find(".changed-sign").remove();
		$(".data-item-selected").removeClass("data-item-selected");
		$("#btn-edit-data-item").removeClass("button-disabled");
		$("#btn-edit-data-item").addClass("button-enabled");
		thisDataItem.addClass("data-item-selected");
		tableId = thisDataItem.attr('id');
		if ((tableType == "precondition") || (tableType == "postcondition")) {
			tableType = "table";
		}
		history.pushState({
			id : tableId
		}, "", "?id=" + tableId);

		var name = thisDataItem.find("span.tree-item-text").text().trim();
		document.title = name + " - " + $("#section-name").text() + " - Grible";
		$("#section-name").addClass("link-infront");

		var $breadcrumb = $("#breadcrumb");
		if ($("#" + tableType + "-name").length > 0) {
			var $tableName = $("#" + tableType + "-name");
			$tableName.parent().attr("href", "/grible/" + tableType + "s/?id=" + tableId);			$tableName.text(name);
		} else {
			$breadcrumb.append("<span class='extends-symbol'>&nbsp;&gt;&nbsp;</span>");
			$breadcrumb.append("<a href='/grible/" + tableType + "s/?id=" + tableId + "'><span id='" + tableType + "-name'>" + name + "</span></a>");}

		$("#table-container").show();
		loadTableValues(tableId);
		loadTopPanel({
			tabletype : tableType,
			tableid : tableId
		});
	}

	$(".category-item")
			.contextMenu(
					{
						menu : 'categoryMenu'
					},
					function(action, el, pos) {
						var $id = $(el).attr("id");
						if (action == "add") {
							$.post("../GetAddTableDialog", {
								categoryid : $id
							}, function(data) {
								$("body").append(data);
								initAddDataItemDialog(jQuery);
							});
						} else if (action == "import") {
							var dialogText = "";
							var servlet = "";
							var fields = "";
							if (tableType == "storage") {
								dialogText = "<br />Only .XLS or .XLSX files are acceptable. Only first sheet will be processed."
										+ "<br />Make sure \"Index\" column or any other help data is absent. File name would be storage name."
										+ "<br /><br />";
								servlet = "../StorageImport";
								fields = '<div class="table"><div class="table-row"><div class="table-cell dialog-cell dialog-label">Class name:</div><div class="table-cell dialog-cell dialog-edit">'
										+ '<input name="class"></div></div></div>';
							} else {
								dialogText = "<br />Only .XLS or .XLSX files are acceptable."
										+ "<br />First sheet will be processed as the General data sheet."
										+ "<br />If \"Preconditions\" sheet is present, it will be processed as Preconditions (1st row - the row of keys, 2nd - the row of values)."
										+ "<br />If \"Postconditions\" sheet is present, it will be processed as Postconditions (1st row - the row of keys, 2nd - the row of values)."
										+ "<br />Make sure \"Index\" column or any other help data is absent. Table name will be taken from the Excel file name."
										+ "<br /><br />";
								servlet = "../TableImport";
							}
							$("body")
									.append(
											'<div id="import-dialog" class="ui-dialog">'
													+ '<div class="ui-dialog-title">Import data '
													+ tableType
													+ '</div>'
													+ '<div class="ui-dialog-content">'
													+ dialogText
													+ '<form action="'
													+ servlet
													+ '?product='
													+ productId
													+ '&category='
													+ $id
													+ '" method="post" enctype="multipart/form-data">'
													+ fields
													+ '<div class="fileform"><div id="fileformlabel"></div><div class="selectbutton ui-button">Browse...</div>'
													+ '<input id="file" type="file" name="file" size="1"/></div>'
													+ '<div class="dialog-buttons right"><input type="submit" class="ui-button" value="Import">'
													+ '</input> <button class="ui-button btn-cancel">Cancel</button></div></form></div></div>');
							initImportDialog(jQuery);
						} else if (action == "add-category") {
							$("body")
									.append(
											'<div id="add-category-dialog" class="ui-dialog">'
													+ '<div class="ui-dialog-title">Add category</div>'
													+ '<div class="ui-dialog-content">'
													+ '<div class="table">'
													+ '<div class="table-row"><div class="table-cell dialog-cell dialog-label">'
													+ 'Name:</div><div class="table-cell dialog-cell dialog-edit"><input class="category-name dialog-edit"></div>'
													+ '</div>'
													+ '</div>'
													+ '<div class="dialog-buttons right">'
													+ '<button id="dialog-btn-add-category" parentid="'
													+ $id
													+ '" class="ui-button">Add</button> <button class="ui-button btn-cancel">Cancel</button>'
													+ '</div></div></div>');
							initAddCategoryDialog(jQuery);
						} else if (action == "edit") {
							$("body")
									.append(
											'<div id="edit-category-dialog" class="ui-dialog">'
													+ '<div class="ui-dialog-title">Edit category</div>'
													+ '<div class="ui-dialog-content">'
													+ '<div class="table">'
													+ '<div class="table-row"><div class="table-cell dialog-cell dialog-label">'
													+ 'Name:</div><div class="table-cell dialog-cell dialog-edit"><input class="category-name dialog-edit" value="'
													+ $(el).text().trim()
													+ '"></div>'
													+ '</div>'
													+ '</div>'
													+ '<div class="dialog-buttons right">'
													+ '<button id="dialog-btn-edit-category" category-id="'
													+ $id
													+ '" class="ui-button">Save</button> <button class="ui-button btn-cancel">Cancel</button>'
													+ '</div></div></div>');
							initEditCategoryDialog(jQuery);
						} else if (action == "delete") {
							noty({
								type : "confirm",
								text : "Are you sure you want to delete this category?",
								buttons : [
										{
											addClass : 'btn btn-primary ui-button',
											text : 'Delete',
											onClick : function($noty) {
												$noty.close();
												$
														.post(
																"../DeleteCategory",
																{
																	id : $id
																},
																function(data) {
																	if (data == "success") {
																		noty({
																			type : "success",
																			text : "The category was deleted.",
																			timeout : 5000
																		});
																		$(el)
																				.remove();
																		history
																				.pushState(
																						{
																							product : productId
																						},
																						"",
																						"?product="
																								+ productId);
																	} else {
																		noty({
																			type : "error",
																			text : data
																		});
																	}
																});
											}
										},
										{
											addClass : 'btn btn-danger ui-button',
											text : 'Cancel',
											onClick : function($noty) {
												$noty.close();
											}
										} ]
							});
						}
					});

	$("#btn-add-category")
			.click(
					function() {
						$("body")
								.append(
										'<div id="add-category-dialog" class="ui-dialog">'
												+ '<div class="ui-dialog-title">Add category</div>'
												+ '<div class="ui-dialog-content">'
												+ '<div class="table">'
												+ '<div class="table-row"><div class="table-cell dialog-cell dialog-label">'
												+ 'Name:</div><div class="table-cell dialog-cell dialog-edit"><input class="category-name dialog-edit"></div>'
												+ '</div>'
												+ '</div>'
												+ '<div class="dialog-buttons right">'
												+ '<button id="dialog-btn-add-category" class="ui-button">Add</button> <button class="ui-button btn-cancel">Cancel</button>'
												+ '</div></div></div>');
						initAddCategoryDialog(jQuery);
					});

	initDelimiter();

	$("a.confirm-needed").click(function(event) {
		event.preventDefault();
		confirmLeavingUnsavedTable($(this).attr("href"));
	});

	if (tableId > 0) {
		$("#waiting-bg").addClass("loading");

		var name = $(".data-item-selected").find("span.tree-item-text").text()
				.trim();
		document.title = name + " - " + $("#section-name").text() + " - Grible";
		$("#section-name").addClass("link-infront");

		var $breadcrumb = $("#breadcrumb");
		$breadcrumb
				.append("<span class='extends-symbol'>&nbsp;&gt;&nbsp;</span>");
		$breadcrumb.append("<a href='" + window.location + "'><span id='"
				+ tableType + "-name'>" + name + "</span></a>");

		loadTableValues(tableId);
		loadTopPanel({
			tabletype : tableType,
			tableid : tableId
		});
	} else {
		$("#table-container").hide();
	}
}

function initImportDialog() {
	initDialog();
	$(".fileform>div").click(function() {
		$("#file").click();
	});

	$(".btn-cancel").click(function(e) {
		e.preventDefault();
		$(".ui-dialog").remove();
	});

	$("#file").change(function() {
		var fullPath = $(this).val();
		var i = 0;
		if (fullPath.lastIndexOf('\\')) {
			i = fullPath.lastIndexOf('\\') + 1;
		} else {
			i = fullPath.lastIndexOf('/') + 1;
		}
		var filename = fullPath.slice(i);
		$("#fileformlabel").text(filename);
	});
}

function initDelimiter() {

	var originPosX = 0;

	$("#delimiter").mousedown(function(e) {
		originPosX = e.pageX;
		$(this).addClass("moving");
	});

	$("#main").mouseup(function() {
		$("#delimiter").removeClass("moving");
	});

	$("#main").mousemove(function(e) {
		if ($("#delimiter.moving").length > 0) {
			var diff = originPosX - e.pageX;
			var leftWidth = $(".left-panel").width();
			$(".left-panel").width(leftWidth - diff);
			$("#entities-list").width(leftWidth - diff);
			var rightWidth = $("#table-container").width();
			$("#table-container").width(rightWidth + diff);
			originPosX = e.pageX;
		}
	});

}

function initDialog() {
	var $dialog = $(".ui-dialog");
	var $posTop = ($(window).height() - $dialog.height()) * 0.3;
	var $posLeft = ($(window).width() - $dialog.width()) / 2;
	$dialog.css("top", $posTop);
	$dialog.css("left", $posLeft);
	$(".ui-dialog").draggable({
		handle : ".ui-dialog-title",
		cursor : "move"
	});
}

function initAddDataItemDialog() {
	initDialog();
	$("input.data-item-name").focus();

	$("input.copy-existing").click(function() {
		if ($(this).is(':checked')) {
			$("select.tables-list").prop("disabled", false);
			$("input.only-columns").prop("disabled", false);
		} else {
			$("select.tables-list").prop("disabled", true);
			$("input.only-columns").prop("disabled", true);
		}
	});

	$("input.data-item-name").keypress(function(event) {
		if (event.which === 13) {
			submitAddDataItem();
		}
	});

	$("input.data-storage-class-name").keypress(function(event) {
		if (event.which === 13) {
			submitAddDataItem();
		}
	});

	$("#dialog-btn-add-data-item").click(function() {
		submitAddDataItem();
	});

	function submitAddDataItem() {
		var $id = $("#dialog-btn-add-data-item").attr("category-id");
		var $args;
		if (tableType == "storage") {
			$args = {
				tabletype : tableType,
				categoryid : $id,
				name : $("input.data-item-name").val(),
				classname : $("input.data-storage-class-name").val(),
				iscopy : $("input.copy-existing").is(':checked'),
				copytableid : $("select.tables-list").find("option:selected")
						.val(),
				isonlycolumns : $("input.only-columns").is(':checked')
			};
		} else if (tableType == "enumeration") {
			$args = {
				tabletype : tableType,
				categoryid : $id,
				name : $("input.data-item-name").val(),
				iscopy : $("input.copy-existing").is(':checked'),
				copytableid : $("select.tables-list").find("option:selected")
						.val(),
				isonlycolumns : $("input.only-columns").is(':checked')
			};
		} else {
			$args = {
				tabletype : "table",
				categoryid : $id,
				name : $("input.data-item-name").val(),
				iscopy : $("input.copy-existing").is(':checked'),
				copytableid : $("select.tables-list").find("option:selected")
						.val(),
				isonlycolumns : $("input.only-columns").is(':checked')
			};
		}
		$.post("../AddTable", $args, function(newTableId) {
			if (isNaN(newTableId)) {
				noty({
					type : "error",
					text : newTableId
				});
			} else {
				$("#add-category-dialog").remove();
				window.location = "?id=" + newTableId;
			}
		});
	}

	$(".btn-cancel").click(function() {
		$(".ui-dialog").remove();
	});
}

function initFillDialog() {
	initDialog();
	$("input.fill-value").focus();

	$("input.fill-value").keypress(function(event) {
		if (event.which === 13) {
			submitFill();
		}
	});

	$("#dialog-btn-fill").click(function() {
		submitFill();
	});

	function submitFill() {
		var $keyId = $("#dialog-btn-fill").attr("keyid");
		var $value = $("input.fill-value").val();
		var $columnChanged = false;
		$("div[keyid='" + $keyId + "']").each(function(i) {
			if ($(this).text() != $value) {
				$(this).text($value);
				$(this).addClass("modified-value-cell");
				$columnChanged = true;
			}
		});
		if ($columnChanged) {
			enableSaveButton();
		}
		$(".ui-dialog").remove();
	}

	$(".btn-cancel").click(function() {
		$(".ui-dialog").remove();
	});
}

function initAddCategoryDialog() {
	initDialog();
	$("input.category-name").focus();

	$("#dialog-btn-add-category").click(function() {
		submitAddCategory();
	});

	$("input.category-name").keypress(function(event) {
		if (event.which === 13) {
			submitAddCategory();
		}
	});

	function submitAddCategory() {
		var $args = {
			tabletype : tableType,
			product : productId,
			name : $("input.category-name").val()
		};
		if (isNumber($("#dialog-btn-add-category").attr("parentid"))) {
			$args = {
				tabletype : tableType,
				product : productId,
				parent : $("#dialog-btn-add-category").attr("parentid"),
				name : $("input.category-name").val()
			};
		}
		$.post("../AddCategory", $args, function(data) {
			if (data == "success") {
				$("#add-category-dialog").remove();
				location.reload(true);
			} else {
				noty({
					type : "error",
					text : data
				});
			}
		});
	}

	$(".btn-cancel").click(function() {
		$(".ui-dialog").remove();
	});
}

function initEditCategoryDialog() {
	initDialog();
	$("input.category-name").focus();

	$("input.category-name").keypress(function(event) {
		if (event.which === 13) {
			submitEditCategory();
		}
	});

	$("#dialog-btn-edit-category").click(function() {
		submitEditCategory();
	});

	function submitEditCategory() {
		var $id = $("#dialog-btn-edit-category").attr("category-id");
		$.post("../UpdateCategory", {
			id : $id,
			name : $("input.category-name").val()
		}, function(data) {
			if (data == "success") {
				$("#edit-category-dialog").remove();
				location.reload(true);
			} else {
				noty({
					type : "error",
					text : data
				});
			}
		});
	}

	$(".btn-cancel").click(function() {
		$("#edit-category-dialog").remove();
	});
}

function initOneButtonDialog() {
	initDialog();
	$(".btn-cancel").click(function() {
		$(".ui-dialog").remove();
	});
}

function loadTopPanel(args) {
	$.post("../GetTopPanel", args, function(data) {
		$(".top-panel").html(data);
		initTopPanel(jQuery);
	});
}

function initTopPanel() {

	if ((tableType == "table") || (tableType == "precondition")
			|| (tableType == "postcondition")) {
		$(".sheet-tab-container").click(function() {
			if ($(this).find(".sheet-tab").length > 0) {
				var $tab = $(this).find(".sheet-tab");
				$(".data-item-selected > .changed-sign").remove();
				$(".sheet-tab-selected").removeClass("sheet-tab-selected");
				$tab.addClass("sheet-tab-selected");
				tableId = $tab.attr('id');
				tableType = $tab.attr('label');
				history.pushState({
					id : tableId
				}, "", "?id=" + tableId);
				loadTableValues(tableId);
				loadTopPanel({
					tabletype : tableType,
					tableid : tableId
				});
			}
		});
	} else {
		if (isChrome()) {
			$("#table-container").css("top", "19px");
		}
	}

	$("#btn-sort-keys").click(function() {
		if ($("#cbx-sort-keys").is("input:checked")) {
			$("#cbx-sort-keys").prop("checked", false);
			enableKeyContextMenu(jQuery);
		} else {
			$("#cbx-sort-keys").prop("checked", true);
			enableCoulumnsMoving();
		}
	});

	$("#cbx-sort-keys").click(function(event) {
		event.stopPropagation();
		if ($(this).is("input:checked")) {
			enableCoulumnsMoving();
		} else {
			enableKeyContextMenu(jQuery);
		}
	});

	$("#btn-show-usage").click(function() {
		if ($("#cbx-show-usage").is("input:checked")) {
			$("#cbx-show-usage").prop("checked", false);
			setRowsUsage(false);
		} else {
			$("#cbx-show-usage").prop("checked", true);
			setRowsUsage(true);
		}
	});

	$("#cbx-show-usage").click(function(event) {
		event.stopPropagation();
		if ($(this).is("input:checked")) {
			setRowsUsage(true);
		} else {
			setRowsUsage(false);
		}
	});

	$("#btn-show-warning").click(function() {
		if ($("#cbx-show-warning").is("input:checked")) {
			$("#cbx-show-warning").prop("checked", false);
			setDuplicateWarning(false);
		} else {
			$("#cbx-show-warning").prop("checked", true);
			setDuplicateWarning(true);
		}
	});

	$("#cbx-show-warning").click(function(event) {
		event.stopPropagation();
		if ($(this).is("input:checked")) {
			setDuplicateWarning(false);
		} else {
			setDuplicateWarning(true);
		}
	});

	$("#btn-add-preconditions").click(function() {
		$.post("../AddTable", {
			parentid : tableId,
			currTabletype : tableType,
			tabletype : "precondition"
		}, function(newTableId) {
			window.location = "?id=" + newTableId;
		});
	});

	$("#btn-add-postconditions").click(function() {
		$.post("../AddTable", {
			parentid : tableId,
			currTabletype : tableType,
			tabletype : "postcondition"
		}, function(newTableId) {
			window.location = "?id=" + newTableId;
		});
	});

	$("#btn-more").mouseenter(
			function() {
				var optionsTop = Math.floor($("#btn-more").offset().top)
						+ $("#btn-more").height() + 11;
				var optionsLeft = $("#btn-more").offset().left
						+ $("#btn-more").width()
						- $("#data-item-options").width() + 15;
				$("#data-item-options").css("top", optionsTop + "px");
				$("#data-item-options").css("left", optionsLeft + "px");
				$("#data-item-options").slideDown(150);
			}).mouseleave(function() {
		$("#data-item-options").slideUp(150);
	});

	$("#data-item-options").hover(function() {
		$(this).css("display", "block");
	});

	$("#btn-delete-data-item")
			.click(
					function() {
						if ($(this).hasClass("button-enabled")) {
							noty({
								type : "confirm",
								text : "Are you sure you want to delete this "
										+ tableType + "?",
								buttons : [
										{
											addClass : 'btn btn-primary ui-button',
											text : 'Delete',
											onClick : function($noty) {
												$noty.close();
												$
														.post(
																"../DeleteTable",
																{
																	id : tableId
																},
																function(data) {
																	if (data == "success") {
																		noty({
																			type : "success",
																			text : "The "
																					+ tableType
																					+ " was deleted.",
																			timeout : 5000
																		});
																		$(
																				".data-item-selected")
																				.remove();
																		$(
																				".top-panel")
																				.find(
																						"div")
																				.hide();
																		$(
																				"#table-container")
																				.hide();
																		if ($("#breadcrumb>a").length > 3) {
																			$(
																					".extends-symbol")
																					.last()
																					.remove();
																			$(
																					"#breadcrumb>a")
																					.last()
																					.remove();
																			$(
																					"#section-name")
																					.removeClass(
																							"link-infront");
																		}
								document.title = $("#section-name").text() + " - Grible";
								history.pushState({																							product : productId
																						},
																						"",
																						"?product="
																								+ productId);
																	} else if (isNumber(data)) {
																		window.location = "?id="
																				+ data;
																	} else {
																		noty({
																			type : "error",
																			text : data
																		});
																	}
																});
											}
										},
										{
											addClass : 'btn btn-danger ui-button',
											text : 'Cancel',
											onClick : function($noty) {
												$noty.close();
											}
										} ]
							});
						}
					});

	$("#btn-save-data-item")
			.click(
					function() {
						if ($(this).hasClass("button-enabled")) {
							$(".data-item-selected > .changed-sign").remove();
							$(this).removeClass("button-enabled");
							$(this).addClass("button-disabled");
							var valuesWaiting = $(".modified-value-cell").length;
							$(".modified-value-cell")
									.each(
											function(i) {
												var $cell = $(this);
												if ($cell.has("span")) {
													$cell.find("span").remove();
												}
												if ($cell.has("div.tooltip")) {
													$cell.find("div.tooltip")
															.remove();
												}
												$
														.post(
																"../SaveCellValue",
																{
																	id : $cell
																			.attr('id'),
																	value : $cell
																			.text()
																},
																function(data) {
																	if (data == "success") {
																		$cell
																				.removeClass("modified-value-cell");
																	} else {
																		noty({
																			type : "error",
																			text : data
																		});
																	}
																	valuesWaiting--;
																	if (valuesWaiting == 0) {
																		$
																				.post(
																						"../CheckForDuplicatedRows",
																						{
																							id : tableId
																						},
																						function(
																								data) {
																							var message = data
																									.split("|");
																							if (message[0] == "true") {
																								for ( var i = 1; i < message.length; i++) {
																									noty({
																										type : "warning",
																										text : message[i]
																									});
																								}
																							}
																						});
																	}
																});
											});
							var keysWaiting = $(".modified-key-cell").length;
							$(".modified-key-cell")
									.each(
											function(i) {
												var $key = $(this);
												if ($key.has("span")) {
													$key.find("span").remove();
												}
												if ($key.has("div.tooltip")) {
													$key.find("div.tooltip")
															.remove();
												}
												$
														.post(
																"../SaveKeyValue",
																{
																	id : $key
																			.attr('id'),
																	value : $key
																			.text()
																},
																function(data) {
																	if (data == "success") {
																		$key
																				.removeClass("modified-key-cell");
																	} else {
																		noty({
																			type : "error",
																			text : data
																		});
																	}
																	keysWaiting--;
																	if (keysWaiting == 0) {
																		var keyNames = $(
																				".key-cell.ui-cell")
																				.map(
																						function() {
																							return $(
																									this)
																									.text();
																						})
																				.get();
																		var usedNames = new Array();
																		for ( var i = 0; i < keyNames.length - 1; i++) {
																			if ($
																					.inArray(
																							keyNames[i],
																							usedNames) == -1) {
																				for ( var j = i + 1; j < keyNames.length; j++) {
																					if (keyNames[i] === keyNames[j]) {
																						usedNames
																								.push(keyNames[i]);
																						noty({
																							type : "warning",
																							text : "More than one parameter name '"
																									+ keyNames[i]
																									+ "'."
																						});
																					}
																				}
																			}
																		}
																	}
																});
											});
						}
					});

	$("#btn-edit-data-item").click(function() {
		if ($(this).hasClass("button-enabled")) {
			$.post("../GetEditTableDialog", {
				id : tableId
			}, function(data) {
				$("body").append(data);
				initEditDataItemDialog(jQuery);
			});
		}
	});

	$("#btn-class-data-item").click(function() {
		$.post("../GetGeneratedClassDialog", {
			id : tableId
		}, function(data) {
			$("body").append(data);
			initGeneratedClassDialog(jQuery);
		});
	});

	$("#btn-export-data-item").click(
			function() {
				$("#waiting-bg").addClass("loading");
				$.post("../ExportToExcel", {
					id : tableId
				}, function(data) {
					$("#waiting-bg").removeClass("loading");
					if (data == "success") {
						document.location.href = "../export/"
								+ $(".data-item-selected").text().trim()
								+ ".xls";
					} else {
						noty({
							type : "error",
							text : data
						});
					}
				});
			});
}

function setRowsUsage(usage) {
	$("#data-item-options").off("mouseleave");
	$("#data-item-options").hide(1);
	$("#waiting-bg").addClass("loading");
	$("#data-item-options").on("mouseleave", function() {
		$("#data-item-options").slideUp(150);
	});
	$.post("../SetRowsUsage", {
		id : tableId,
		usage : usage
	}, function(data) {
		$("#waiting-bg").removeClass("loading");
		if (data == "success") {
			loadTableValues(tableId);
		} else {
			noty({
				type : "error",
				text : data
			});
		}
	});
}

function setDuplicateWarning(show) {
	$.post("../SetDuplicateWarning", {
		id : tableId,
		show : show
	}, function(data) {
		if (data != "success") {
			noty({
				type : "error",
				text : data
			});
		}
	});
}

function enableCoulumnsMoving() {
	$(".key-cell").destroyContextMenu();
	$(".key-row")
			.sortable(
					{
						cursor : "move",
						delay : 50,
						items : "> .key-cell",
						forcePlaceholderSize : false,
						containment : "parent",
						axis : "x",
						update : function(event, ui) {
							$('#cbx-sort-keys').attr("checked", false);
							var keyIds = [];
							var newOrder = [];
							var oldOrder = [];
							var modifiedStart = -1;
							$(".ui-cell.key-cell")
									.each(
											function(i) {
												if ($(this).attr('key-order') != (i + 1)) {
													if (modifiedStart == -1) {
														modifiedStart = i;
													}
													keyIds[i - modifiedStart] = $(
															this).attr('id');
													newOrder[i - modifiedStart] = i + 1;
													oldOrder[i - modifiedStart] = $(
															this).attr(
															'key-order');
												}
											});
							$
									.post(
											"../UpdateKeysOrder",
											{
												modkeyids : keyIds,
												modkeynumbers : newOrder
											},
											function(data) {
												if (data == "success") {
													$(".key-cell")
															.each(
																	function(i) {
																		$(this)
																				.attr(
																						"key-order",
																						(i + 1));
																	});
													$(".value-row")
															.each(
																	function(i) {
																		var sortedCells = $(
																				this)
																				.find(
																						".ui-cell.value-cell")
																				.sort(
																						function(
																								a,
																								b) {
																							var contentA = parseInt($(
																									".key-cell[id='"
																											+ $(
																													a)
																													.attr(
																															'keyid')
																											+ "']")
																									.attr(
																											'key-order'));
																							var contentB = parseInt($(
																									".key-cell[id='"
																											+ $(
																													b)
																													.attr(
																															'keyid')
																											+ "']")
																									.attr(
																											'key-order'));
																							return (contentA < contentB) ? -1
																									: (contentA > contentB) ? 1
																											: 0;
																						});
																		$(this)
																				.find(
																						".value-cell")
																				.remove();
																		if ($(
																				this)
																				.find(
																						".info-cell").length > 0) {
																			sortedCells
																					.insertBefore($(
																							this)
																							.find(
																									".info-cell")
																							.first());
																		} else {
																			$(
																					this)
																					.append(
																							sortedCells);
																		}
																	});
													initTableValues(jQuery);
												} else {
													noty({
														type : "error",
														text : data
													});
												}
												enableKeyContextMenu(jQuery);
											});
						}
					});
}

function initEditDataItemDialog() {
	initDialog();
	$("input.data-item-name").focus();

	$("input.data-item-name").keypress(function(event) {
		if (event.which === 13) {
			submitDataItem();
		}
	});

	$("input.data-storage-class-name").keypress(function(event) {
		if (event.which === 13) {
			submitDataItem();
		}
	});

	$("#dialog-btn-edit-data-item").click(function() {
		submitDataItem();
	});

	function submitDataItem() {
		var $categoryid = $("select.categories").find("option:selected").val();
		var $args;
		if (tableType == "storage") {
			$args = {
				id : tableId,
				categoryid : $categoryid,
				usage : $("input.usage").is("input:checked"),
				name : $("input.data-item-name").val(),
				classname : $("input.data-storage-class-name").val()
			};
		} else {
			$args = {
				id : tableId,
				categoryid : $categoryid,
				name : $("input.data-item-name").val()
			};
		}
		$.post("../EditTable", $args, function(data) {
			if (data == "success") {
				$(".ui-dialog").remove();
				location.reload(true);
			} else {
				noty({
					type : "error",
					text : data
				});
			}
		});
	}

	$(".btn-cancel").click(function() {
		$(".ui-dialog").remove();
	});
}

function initGeneratedClassDialog() {
	$("#tabs").tabs();

	var $dialog = $("#generated-class-dialog");
	var $dilogMaxWidth = 700;
	if ($(window).width() < $dilogMaxWidth) {
		$dialog.width($(window).width());
	} else {
		$dialog.width($dilogMaxWidth);
	}
	$dialog.height($(window).height() * 0.8);
	$dialog.find(".tab-content").height($dialog.height() - 154);
	var $posTop = ($(window).height() - $dialog.height()) / 2;
	var $posLeft = ($(window).width() - $dialog.width()) / 2;
	$dialog.css("top", $posTop);
	$dialog.css("left", $posLeft);

	$(".btn-cancel").click(function() {
		$(".ui-dialog").remove();
	});
}

function highlight(element) {
	element.effect("highlight", {
		color : "#FFF3B3"
	}, 600);
}

function loadTableValues(id) {
	$.post("../GetTableValues", {
		id : id
	}, function(data) {
		$(".entities-values").html(template(jQuery.parseJSON(data)));
		initTableValues(jQuery);
		initKeysAndIndexes(jQuery);
		if (isChrome()) {
			$("#main .table-cell").removeClass("floatleft");
		}
	});
}

function initTableValues() {

	$("#waiting-bg").removeClass("loading");

	$("html").click(function() {
		if ($(".selected-cell").length > 0) {
			$(".selected-cell").removeClass("selected-cell");
		}
	});

	$(".entities-values")
			.sortable(
					{
						cursor : "move",
						delay : 50,
						items : "> .value-row",
						forcePlaceholderSize : true,
						update : function(event, ui) {
							var rowIds = [];
							var oldOrder = [];
							var newOrder = [];
							var modifiedStart = -1;
							$(".ui-cell.index-cell")
									.each(
											function(i) {
												if ($(this).text() != (i + 1)) {
													if (modifiedStart == -1) {
														modifiedStart = i;
													}
													rowIds[i - modifiedStart] = $(
															this).attr('id');
													oldOrder[i - modifiedStart] = $(
															this).text();
													newOrder[i - modifiedStart] = i + 1;
												}
											});
							$
									.post(
											"../UpdateRowsOrder",
											{
												rowids : rowIds,
												oldorder : oldOrder,
												neworder : newOrder
											},
											function(data) {
												if (data == "success") {
													for ( var j = 0; j < rowIds.length; j++) {
														var modifiedIndexCell = $(".ui-cell.index-cell[id='"
																+ rowIds[j]
																+ "']");
														highlight(modifiedIndexCell);
														modifiedIndexCell
																.text(j
																		+ modifiedStart
																		+ 1);
													}
												} else {
													noty({
														type : "error",
														text : data
													});
												}
											});
						}
					});

	initValueCells($(".value-cell:not(:has(> input.changed-value))"));
	initTooltipCells($(".storage-cell"));
	initEnumCells($(".enum-cell"));

	$(".value-cell").click(function(event) {
		event.stopPropagation();
		$(".selected-cell").removeClass("selected-cell");
		$(this).addClass("selected-cell");
		if ($(".value-cell").has("input").length > 0) {
			modifyValueCell();
		}
	});

	$(".selected-cell").keypress("c", function(event) {
		if (event.ctrlKey) {
			// TODO: Copy to clipboard.
		}
	});
}

function initValueCells(cells) {
	cells.dblclick(function() {
		discardEnumCellChanges();
		var $cell = $(this);
		$cell.removeClass("selected-cell");
		if ($cell.has("span")) {
			$cell.find("span").remove();
		}
		if ($cell.has("div.tooltip")) {
			$cell.find("div.tooltip").remove();
		}
		var $content = $cell.text().replace(/'/g, "&#39;");
		var $width = $cell.width();
		$cell.html("<input class='changed-value' value='" + $content
				+ "' /><span class='old-value' style='display: none;'>"
				+ $content + "</span>");
		$cell.find("input.changed-value").css("width", $width + "px");
		$cell.find("input.changed-value").focus();
		$cell.find("input.changed-value").select();
		initEditableCell(jQuery);
	});
}

function isNumber(n) {
	return !isNaN(parseFloat(n)) && isFinite(n);
}

function initKeysAndIndexes() {
	if (tableType != "enumeration") {
		$(".key-cell")
				.dblclick(
						function() {
							var $key = $(this);
							if ($key.has("span")) {
								$key.find("span").remove();
							}
							var $content = $key.text().replace(/'/g, "&#39;");
							var $width = $key.width();
							$key
									.html("<input class='changed-value' value='"
											+ $content
											+ "' /><span class='old-value' style='display: none;'>"
											+ $content + "</span>");
							$key.find("input.changed-value").css("width",
									$width + "px");
							$key.find("input.changed-value").focus();
							$key.find("input.changed-value").select();
							initEditableKeyCell(jQuery);
						});
	}

	$(".ui-cell.index-cell").contextMenu(
			{
				menu : "rowMenu"
			},
			function(action, el, pos) {
				var $rowId = $(el).attr("id");
				var $rowOrder = parseInt($(el).text());
				var $row = $(el).parent();
				if (action == "add") {
					if ($(".index-cell.ui-cell").length == 1) {
						$("#rowMenu").enableContextMenuItems("#delete");
					}
					$("#waiting-bg").addClass("loading");
					$.post("../InsertRow", {
						rowid : $rowId
					}, function(data) {
						$("#waiting-bg").removeClass("loading");
						var newIds = data.split(";");
						if (newIds.length > 1) {
							$newRow = $row.clone(true);
							$newRow.find(".ui-cell.selected-cell").removeClass(
									"selected-cell");
							$newRow.find(".ui-cell.index-cell").attr("id",
									newIds[0]);
							$newRow.find(".ui-cell.modified-value-cell")
									.removeClass("modified-value-cell");
							$newRow.find(".ui-cell.value-cell:not(.enum-cell)")
									.text("");
							$newRow.find(".ui-cell.storage-cell").text("0");
							$newRow.find(".ui-cell.value-cell").each(
									function(i) {
										$(this).attr("rowid", newIds[0]);
										$(this).attr("id", newIds[i + 1]);
									});
							$newRow.insertBefore($row);
							highlight($newRow);
							$(".ui-cell.index-cell").each(function(i) {
								if ((i + 1) >= $rowOrder) {
									$(this).text(i + 1);
								}
							});

						} else {
							noty({
								type : "error",
								text : data
							});
						}
					});
				} else if (action == "copy") {
					if ($(".index-cell.ui-cell").length == 1) {
						$("#rowMenu").enableContextMenuItems("#delete");
					}
					$("#waiting-bg").addClass("loading");
					$.post("../CopyRow", {
						rowid : $rowId
					}, function(data) {
						$("#waiting-bg").removeClass("loading");
						var newIds = data.split(";");
						if (newIds.length > 1) {
							$newRow = $row.clone(true);
							$newRow.find(".ui-cell.index-cell").attr("id",
									newIds[0]);
							$newRow.find(".ui-cell.value-cell").each(
									function(i) {
										$(this).attr("rowid", newIds[0]);
										$(this).attr("id", newIds[i + 1]);
									});
							$newRow.find(".ui-cell.value-cell").removeClass(
									"selected-cell");
							$newRow.insertAfter($row);
							highlight($newRow);
							$(".ui-cell.index-cell").each(function(i) {
								if ((i + 1) > $rowOrder) {
									$(this).text(i + 1);
								}
							});

						} else {
							noty({
								type : "error",
								text : data
							});
						}
					});
				} else if (action == "delete") {
					if ($(".index-cell.ui-cell").length == 2) {
						$("#rowMenu").disableContextMenuItems("#delete");
					}
					$("#waiting-bg").addClass("loading");
					$.post("../DeleteRow", {
						rowid : $rowId
					}, function(data) {
						$("#waiting-bg").removeClass("loading");
						if (data == "success") {
							$row.hide(400, function() {
								$row.remove();
								$(".ui-cell.index-cell").each(function(i) {
									if ((i + 1) >= $rowOrder) {
										highlight($(this));
										$(this).text(i + 1);
									}
								});

							});
						} else {
							noty({
								type : "error",
								text : data
							});
						}
					});
				}
			});

	$(".ui-cell.index-cell").mousedown(function(event) {
		if (event.which === 3) {
			selectRow($(this).attr("id"));
		}
	});

	$("#rowMenu").enableContextMenuItems("#add,#copy,#delete");

	if ($(".index-cell.ui-cell").length == 1) {
		$("#rowMenu").disableContextMenuItems("#delete");
	}

	enableKeyContextMenu(jQuery);
}

function enableKeyContextMenu() {
	$(".key-cell")
			.contextMenu(
					{
						menu : "keyMenu"
					},
					function(action, el, pos) {
						var $keyId = $(el).attr("id");
						var $keyOrder = $(el).attr("key-order");
						var $column = $("div[keyid='" + $keyId + "']");
						if (action == "add") {
							if ($(".key-cell.ui-cell").length == 1) {
								$("#keyMenu").enableContextMenuItems("#delete");
							}
							$
									.post(
											"../InsertKey",
											{
												keyid : $keyId
											},
											function(data) {
												var newIds = data.split(";");
												if (newIds.length > 1) {
													$newKey = $(el).clone(true);
													$newKey.attr("id",
															newIds[0]);
													$newKey.text("editme");
													$newKey.insertBefore($(el));
													highlight($newKey);

													$column
															.each(function(i) {
																$newCell = $(
																		this)
																		.clone(
																				true);
																$newCell
																		.removeClass("modified-value-cell");
																$newCell
																		.removeClass("storage-cell");
																$newCell
																		.removeClass("selected-cell");
																$newCell
																		.text("");
																$newCell
																		.attr(
																				"keyid",
																				newIds[0]);
																$newCell
																		.attr(
																				"id",
																				newIds[i + 1]);
																$newCell
																		.insertBefore($(this));
																highlight($newCell);
															});
													$(".ui-cell.key-cell")
															.each(
																	function(i) {
																		if ((i + 1) >= $keyOrder) {
																			$(
																					this)
																					.attr(
																							"key-order",
																							(i + 1));
																		}
																	});
												} else {
													noty({
														type : "error",
														text : data
													});
												}
											});
						} else if (action == "copy") {
							if ($(".key-cell.ui-cell").length == 1) {
								$("#keyMenu").enableContextMenuItems("#delete");
							}
							$.post("../CopyKey", {
								keyid : $keyId,
							}, function(data) {
								var newIds = data.split(";");
								if (newIds.length > 1) {
									$newKey = $(el).clone(true);
									$newKey.attr("id", newIds[0]);
									$newKey.insertAfter($(el));
									highlight($newKey);

									$column.each(function(i) {
										$newCell = $(this).clone(true);
										$newCell.attr("keyid", newIds[0]);
										$newCell.attr("id", newIds[i + 1]);
										$newCell.removeClass("selected-cell");
										$newCell.insertAfter($(this));
										highlight($newCell);
									});
									$(".ui-cell.key-cell").each(function(i) {
										if ((i + 1) > $keyOrder) {
											$(this).attr("key-order", (i + 1));
										}
									});
								} else {
									noty({
										type : "error",
										text : data
									});
								}
							});
						} else if (action == "delete") {
							if ($(".key-cell.ui-cell").length == 2) {
								$("#keyMenu")
										.disableContextMenuItems("#delete");
							}
							$.post("../DeleteKey", {
								keyid : $keyId,
							}, function(data) {
								if (data == "success") {
									$(el).hide(400);
									$column.hide(400, function() {
										$(el).remove();
										$column.remove();
										$(".ui-cell.key-cell").each(
												function(i) {
													if ((i + 1) >= $keyOrder) {
														$(this).attr(
																"key-order",
																(i + 1));
													}
												});
									});
								} else {
									noty({
										type : "error",
										text : data
									});
								}
							});
						} else if (action == "fill") {
							$("body")
									.append(
											'<div id="fill-dialog" class="ui-dialog">'
													+ '<div class="ui-dialog-title">Fill column with value</div>'
													+ '<div class="ui-dialog-content">'
													+ '<div class="table">'
													+ '<div class="table-row"><div class="table-cell dialog-cell dialog-label">'
													+ 'Value: </div><div class="table-cell dialog-cell dialog-edit"><input class="fill-value dialog-edit"></div>'
													+ '</div>'
													+ '</div>'
													+ '<div class="dialog-buttons right">'
													+ '<button id="dialog-btn-fill" keyid="'
													+ $keyId
													+ '" class="ui-button">Fill</button> <button class="ui-button btn-cancel">Cancel</button>'
													+ '</div></div></div>');
							initFillDialog(jQuery);
						} else if (action == "parameter") {
							$.post("../GetParameterTypeDialog", {
								keyid : $keyId,
							}, function(data) {
								$("body").append(data);
								initParameterTypeDialog(jQuery);
							});

						}
					});

	$(".key-cell").mousedown(function(event) {
		if (event.which === 3) {
			selectColumn($(this).attr("id"));
		}
	});

	if ((tableType == "table") || (tableType == "storage")) {
		$("#keyMenu").enableContextMenuItems("#add,#copy,#fill,#delete");
	} else {
		$("#keyMenu").enableContextMenuItems("#add,#copy,#delete");
		$("#keyMenu").disableContextMenuItems("#fill");
	}

	if ($(".key-cell.ui-cell").length == 1) {
		$("#keyMenu").disableContextMenuItems("#delete");
	}
}

function initTooltipCells(elements) {
	if (isTooltipOnClick) {
		elements.click(function() {
			$(this).off("mouseover");
			initTooltipCellsOnClick($(this));
		});
	} else {
		elements.hover(function() {
			initTooltipCellsOnHover($(this));
		});
	}

	function initTooltipCellsOnClick(value) {
		var $value = value;
		if (($value.has("span.old-value").length == 0)
				&& ($value.text() != "0") && ($value.text() != "")
				&& (!$value.hasClass("modified-value-cell"))) {
			if ($value.has("div.tooltip").length == 0) {
				var $content = $value.text();
				var $args = {
					id : $value.attr('id'),
					content : $content
				};
				$.post("../GetStorageTooltip", $args, function(data) {
					$value.html(data);
					var $tooltip = $value.find("div.tooltip");
					adjustTooltipPosition($value, $tooltip);
					$value.find("div.tooltip").show();
				});
			} else {
				$value.find("div.tooltip").toggle();
			}
		}
	}

	function initTooltipCellsOnHover(value) {
		var $value = value;
		if (($value.has("span.old-value").length == 0)
				&& ($value.text() != "0") && ($value.text() != "")
				&& (!$value.hasClass("modified-value-cell"))
				&& ($value.has("div.tooltip").length == 0)) {
			var $content = $value.text();
			var $args = {
				id : $value.attr('id'),
				content : $content
			};
			$.post("../GetStorageTooltip", $args, function(data) {
				$value.html(data);
				var $tooltip = $value.find("div.tooltip");
				adjustTooltipPosition($value, $tooltip);
			});
		}
	}

	function adjustTooltipPosition($value, $tooltip) {
		$tooltip.mousedown(function(event) {
			event.stopPropagation();
		});

		var $widthRight = $("#table-container").width()
				- $value.position().left - 17;
		var $tooltipWidth = $tooltip.width();
		if ($widthRight < $tooltipWidth) {
			if ($tooltipWidth > $("#table-container").width()) {
				$tooltip.css("max-width", ($("#table-container").width() - 20)
						+ "px");
				$tooltip.css("left", "0px");
			} else {
				$tooltip.css("margin-left", "-" + ($tooltipWidth - $widthRight)
						+ "px");
			}
		}

		var $heightToBorder;
		if ($value.position().top < $("#table-container").height() * 0.7) {
			$heightToBorder = $("#table-container").height()
					- $value.position().top - 17;
			$tooltip.addClass("down");
		} else {
			$heightToBorder = $value.position().top;
			$tooltip.addClass("up");
		}
		if ($heightToBorder < $tooltip.height()) {
			if ($tooltip.hasClass("up")) {
				$tooltip.css("margin-top", "-" + ($heightToBorder + 15) + "px");
				$tooltip.css("max-height", ($heightToBorder - 20) + "px");
			} else {
				$tooltip.css("max-height", ($heightToBorder - 35) + "px");
			}
			$tooltip.css("padding-right", "15px");
		} else {
			if ($tooltip.hasClass("up")) {
				$tooltip.css("margin-top", "-" + ($tooltip.height() + 35)
						+ "px");
			}
		}
	}
}

function initEditableKeyCell() {
	$("html").click(function() {
		if ($(".key-cell").has("input").length > 0) {
			modifyKeyCell();
		}
	});

	$(".key-cell > input").keypress(function(event) {
		if (event.which === 13) {
			modifyKeyCell();
		}
	});

	$("input.changed-value").click(function(event) {
		event.stopPropagation();
	});

	$("input.changed-value").dblclick(function(event) {
		event.stopPropagation();
	});
}

function initParameterTypeDialog() {
	initDialog();
	$("input[value='storage']").click(function() {
		onStorageClick();
	});

	$("#label-option-storage").click(function() {
		$("input[value='storage']").click();
		onStorageClick();
	});

	function onStorageClick() {
		$(".select-storage").prop("disabled", false);
		$(".select-enum").prop("disabled", true);
	}

	$("input[value='text']").click(function() {
		onTextClick();
	});

	$("#label-option-text").click(function() {
		$("input[value='text']").click();
		onTextClick();
	});

	function onTextClick() {
		$(".select-storage").prop("disabled", true);
		$(".select-enum").prop("disabled", true);
	}

	$("input[value='enumeration']").click(function() {
		onEnumClick();
	});

	$("#label-option-enumeration").click(function() {
		$("input[value='enumeration']").click();
		onEnumClick();
	});

	function onEnumClick() {
		$(".select-storage").prop("disabled", true);
		$(".select-enum").prop("disabled", false);
	}

	$("#btn-apply-type").click(function() {
		var $id = $(this).attr("keyid");
		var $dialog = $("#parameter-type-dialog");
		var $type = $dialog.find("input:checked").val();
		var $select;
		if ($type == "storage") {
			$select = $dialog.find("select.select-storage");
		} else {
			$select = $dialog.find("select.select-enum");
		}
		var $refId = $select.find("option:selected").val();
		$.post("../ApplyParameterType", {
			keyId : $id,
			type : $type,
			refId : $refId
		}, function(data) {
			if (data.indexOf("success") == 0) {
				noty({
					type : "success",
					text : "New type was successfully applied.",
					timeout : 5000
				});
				$(".ui-dialog").remove();
				var $column = $("div[keyid='" + $id + "']");
				if ($type == "text") {
					$column.find("div.tooltip").remove();
					$column.off();
					$column.removeClass("storage-cell");
					initValueCells($column);
					modifyKeyCell();
				} else if ($type == "storage") {
					$column.addClass("storage-cell");
					initTooltipCells($column);
					modifyKeyCell();
				} else {
					$column.addClass("enum-cell");
					initEnumCells($column);
					modifyKeyCell();
				}
			} else if (data == "not-changed") {
				noty({
					text : "This parameter is already has this type.",
					timeout : 5000
				});
			} else {
				noty({
					type : "error",
					text : data
				});
			}
		});
	});

	$(".btn-cancel").click(function() {
		$(".ui-dialog").remove();
	});
}

function modifyKeyCell() {
	if ($(".key-cell").has("input").length > 0) {
		var $cell = $(".key-cell").has("input");
		var $oldContent = $cell.find("span.old-value").text();
		var $newContent = $cell.find("input.changed-value").val();

		if ($oldContent != $newContent) {
			enableSaveButton();
			$cell.addClass("modified-key-cell");
		}
		$cell.html($newContent);
	}
}

function initEnumCells(elements) {
	elements.off("dblclick");
	elements.dblclick(function() {
		discardEnumCellChanges();
		var $cell = $(this);
		var $content = $cell.text().replace(/'/g, "&#39;");
		var $args = {
			keyid : $cell.attr('keyid'),
			content : $content
		};
		$cell.removeClass("selected-cell");
		if ($cell.has("span")) {
			$cell.find("span").remove();
		}
		if ($cell.has("div.tooltip")) {
			$cell.find("div.tooltip").remove();
		}

		$.post("../GetEnumValues", $args, function(data) {
			$cell.html(data);
			$cell.addClass("no-padding");
			initEditableEnumCell(jQuery);
		});
	});
}

function initEditableCell() {
	$("html").click(function() {
		if ($(".value-cell").has("input").length > 0) {
			modifyValueCell();
		}
	});

	$(".value-cell > input").keypress(function(event) {
		if (event.which === 13) {
			modifyValueCell();
		}
	});

	$("input.changed-value").click(function(event) {
		event.stopPropagation();
	});

	$("input.changed-value").dblclick(function(event) {
		event.stopPropagation();
	});
}

function initEditableEnumCell() {
	$("html").click(function() {
		discardEnumCellChanges();
	});

	$(".enum-cell > select").change(function() {
		enableSaveButton();
		var $cell = $(this).parent();
		$cell.removeClass("selected-cell");
		$cell.addClass("modified-value-cell");
		var $newContent = $(this).find("option:selected").text();
		$cell.removeClass("no-padding");
		$cell.html($newContent);
	});

	$("select.enum-values").click(function(event) {
		event.stopPropagation();
	});

	$("select.enum-values").dblclick(function(event) {
		event.stopPropagation();
	});
}

function discardEnumCellChanges() {
	if ($(".enum-cell").has("select").length > 0) {
		var $cell = $(".enum-cell > select").parent();
		$cell.removeClass("selected-cell");
		var $content = $(".enum-cell > select > option:selected").text();
		$cell.html($content);
		$cell.removeClass("no-padding");
	}
}

function modifyValueCell() {
	var $cell = $(".value-cell").has("input");
	var $oldContent = $cell.find("span.old-value").text();
	var $newContent = $cell.find("input.changed-value").val();
	if ($oldContent != $newContent) {
		enableSaveButton();
		$cell.removeClass("selected-cell");
		$cell.addClass("modified-value-cell");
	}
	if ($cell.hasClass("storage-cell") && $newContent.match(/\d+-\d+/)) {
		var start = parseInt($newContent.substring(0, $newContent.indexOf("-")));
		var end = parseInt($newContent.substring($newContent.indexOf("-") + 1));
		$newContent = start;
		for ( var i = start + 1; i < end + 1; i++) {
			$newContent += ";" + i;
		}
	}
	$cell.html($newContent);
}

function enableSaveButton() {
	$(".data-item-selected:not(:has(> img.changed-sign))").append(
			" <img class='changed-sign' src='../img/modified.png'>");
	$("#btn-save-data-item").removeClass("button-disabled");
	$("#btn-save-data-item").addClass("button-enabled");
}

function showImportResult(message) {
	if (message.indexOf("WARNING") > 0) {
		noty({
			type : "success",
			text : message.substring(0, message.indexOf("WARNING")),
			timeout : 5000
		});
		noty({
			type : "warning",
			text : message.substring(message.indexOf("WARNING"))
		});
	} else if (message.indexOf("successfully") > 0) {
		noty({
			type : "success",
			text : message,
			timeout : 5000
		});
	} else {
		noty({
			type : "error",
			text : message
		});
	}
}

function showAdvancedImportDialog(currentRowsCount, importedRowsCount) {
	var noteText = "";
	if (tableType == "storage") {
	} else {
		noteText = "Note: Only General table will change. Preconditions and Postconditions are ignored.<br /><br />";
	}
	var options = '<br/><br/><input type="radio" value="addtoend" name="import-option" checked="checked">Add to the end of the table'
			+ '<br/><br/><input type="radio" value="addfromrow" name="import-option">Replace from row: '
			+ '<input id="start-row" size="4" disabled="disabled" value="1"/>';
	$("body")
			.append(
					'<div id="advanced-import-dialog" class="ui-dialog">'
							+ '<div class="ui-dialog-title">Import existing data '
							+ tableType
							+ '</div>'
							+ '<div class="ui-dialog-content">'
							+ '<br />Data '
							+ tableType
							+ ' with the same name was found.<br /><br /><br />'
							+ '<div class="table"><div class="table-row">'
							+ '<div class="table-cell dialog-cell dialog-label">Rows in the current '
							+ tableType
							+ ':</div><div class="table-cell dialog-cell">'
							+ currentRowsCount
							+ '</div></div><div class="table-row"><div class="table-cell dialog-cell dialog-label">Rows in the file being imported:</div>'
							+ '<div class="table-cell dialog-cell">'
							+ importedRowsCount
							+ '</div></div></div>'
							+ '<br/><br/>How would you like to apply changes?'
							+ options
							+ '<br /><br />'
							+ noteText
							+ '<div class="dialog-buttons right"><button class="ui-button btn-apply">Apply</button> '
							+ '<button class="ui-button btn-cancel">Cancel</button></div></div></div>');
	initAdvancedImportDialog(jQuery);
}

function initAdvancedImportDialog() {
	initDialog();
	$(".btn-cancel").click(function() {
		$(".ui-dialog").remove();
	});

	$("input[value='addfromrow']").click(function() {
		$("#start-row").prop("disabled", false);
	});

	$("input[value='addtoend']").click(function() {
		$("#start-row").prop("disabled", true);
	});

	$("#advanced-import-dialog .btn-apply").click(function() {
		var $dialog = $("#advanced-import-dialog");
		var $option = $dialog.find("input:checked").val();
		var $startrow = $dialog.find("#start-row").val();
		$.post("../AnvancedImport", {
			option : $option,
			startrow : $startrow
		}, function(data) {
			if (data == "success") {
				location.reload(true);
			} else {
				noty({
					type : "error",
					text : data
				});
			}
		});
	});
}

function applyParameterTypesAfterImport(keyIds, refIds, types) {
	for ( var i = 0; i < keyIds.length; i++) {
		var $id = keyIds[i];
		var $type = types[i];
		var $refId = refIds[i];
		$.post("../ApplyParameterType", {
			keyId : $id,
			type : $type,
			refId : $refId
		}, function(data) {
			if (data.indexOf("success") == 0) {
				var result = data.split("|");
				var $column = $("div[keyid='" + result[1] + "']");
				$column.addClass(result[2] + "-cell");
				if (result[2] == "storage") {
					initTooltipCells($column);
				} else if (result[2] == "enum") {
					initEnumCells($column);
				}
			} else if (data != "not-changed") {
				noty({
					type : "error",
					text : data
				});
			}
		});
	}
}

function selectRow(id) {
	$(".selected-cell").removeClass("selected-cell");
	$(".value-cell[rowid=" + id + "]").addClass("selected-cell");
}

function selectColumn(id) {
	$(".selected-cell").removeClass("selected-cell");
	$(".value-cell[keyid=" + id + "]").addClass("selected-cell");
}

function confirmLeavingUnsavedTable(url) {
	if ($("#btn-save-data-item").hasClass("button-enabled")) {
		noty({
			type : "confirm",
			text : "Leave unsaved table?",
			buttons : [ {
				addClass : 'btn btn-primary ui-button',
				text : 'Yes',
				onClick : function($noty) {
					$noty.close();
					window.location = url;
				}
			}, {
				addClass : 'btn btn-danger ui-button',
				text : 'No',
				onClick : function($noty) {
					$noty.close();
				}
			} ]
		});
	} else {
		window.location = url;
	}
}

function isChrome() {
	return /chrome/.test(navigator.userAgent.toLowerCase());
}