var $changedCells = [];

$(window).on("load", function() {
	var docHeight = $(window).height() - 95;
	var docWidth = $(window).width() - 45;
	var breadcrumbHeight = $("#breadcrumb").height();
	var footerHeight = $("#footer").outerHeight();
	var mainHeight = docHeight - breadcrumbHeight - footerHeight - 27;
	$("#table-container").width(docWidth - $("#delimiter").width() - $(".left-panel").width() - 10);

	if (isChrome()) {
		$("#main .table-cell").addClass("floatleft");
	}

	$("#main").height(mainHeight);
	$("#table-container").height(mainHeight);
	$(".left-panel").height(mainHeight);
	$("#entities-list").height(mainHeight);

});

$().ready(initialize());

function initialize() {

	$(document).ajaxError(
			function(e, xhr, settings, exception) {
				var exrrorText = xhr.responseText.substring(xhr.responseText.indexOf("<h1>"));
				$("body").append(
						'<div id="error-dialog" class="ui-dialog">' + '<div class="ui-dialog-title">Error</div>' + '<div class="ui-dialog-content">' + 'Location: ' + settings.url
								+ '<br><br>' + exrrorText + '<br><br>' + '<div class="right">' + '<button class="ui-button btn-cancel">OK</button>' + '</div></div></div>');
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
		if (isJson()) {
			history.pushState({
				product : productId,
				id : tableId
			}, "", "?product=" + productId + "&id=" + tableId);
		} else {
			history.pushState({
				id : tableId
			}, "", "?id=" + tableId);
		}

		var name = thisDataItem.find("span.tree-item-text").text().trim();
		document.title = name + " - " + $("#section-name").text() + " - Grible";
		$("#section-name").addClass("link-infront");

		var $breadcrumb = $("#breadcrumb");
		if ($("#" + tableType + "-name").length > 0) {
			var $tableName = $("#" + tableType + "-name");
			if (isJson()) {
				$tableName.parent().attr("href", "/" + tableType + "s/?product=" + productId + "&id=" + tableId);
			} else {
				$tableName.parent().attr("href", "/" + tableType + "s/?id=" + tableId);
			}
			$tableName.text(name);
		} else {
			$breadcrumb.append("<span class='extends-symbol'>&nbsp;&gt;&nbsp;</span>");
			if (isJson()) {
				$breadcrumb.append("<a href='/" + tableType + "s/?product=" + productId + "&id=" + tableId + "'><span id='" + tableType + "-name'>" + name + "</span></a>");
			} else {
				$breadcrumb.append("<a href='/" + tableType + "s/?id=" + tableId + "'><span id='" + tableType + "-name'>" + name + "</span></a>");
			}
		}

		$("#table-container").show();
		loadTableValues({
			id : tableId,
			product : productId
		});
		loadTopPanel({
			tabletype : tableType,
			tableid : tableId,
			product : productId
		});
	}

	$
			.contextMenu({
				selector : ".category-item",
				items : {
					add : {
						name : "Add " + tableType,
						icon : "add",
						callback : function() {
							var $id = $(this).attr("id");
							var $args;
							if (isJson()) {
								$args = {
									product : productId,
									tabletype : tableType,
									path : getCategoryPath($(this))
								};
							} else {
								$args = {
									categoryid : $id
								};
							}
							$.post("../GetAddTableDialog", $args, function(data) {
								$("body").append(data);
								initAddDataItemDialog(jQuery);
							});
						}
					},
					import : {
						name : "Import " + tableType,
						icon : "import",
						callback : function() {
							var $id = $(this).attr("id");
							var dialogText = "";
							var servlet = "";
							var fields = "";
							if (tableType == "storage") {
								dialogText = "<br />Only .XLS or .XLSX files are acceptable. Only first sheet will be processed."
										+ "<br />Make sure \"Index\" column or any other help data is absent. File name would be storage name." + "<br /><br />";
								servlet = "../StorageImport";
								fields = '<div class="table"><div class="table-row"><div class="table-cell dialog-cell dialog-label">Class name:</div><div class="table-cell dialog-cell dialog-edit">'
										+ '<input name="class"></div></div></div>';
							} else {
								dialogText = "<br />Only .XLS or .XLSX files are acceptable."
										+ "<br />First sheet will be processed as the General data sheet."
										+ "<br />If \"Preconditions\" sheet is present, it will be processed as Preconditions (1st row - the row of keys, 2nd - the row of values)."
										+ "<br />If \"Postconditions\" sheet is present, it will be processed as Postconditions (1st row - the row of keys, 2nd - the row of values)."
										+ "<br />Make sure \"Index\" column or any other help data is absent. Table name will be taken from the Excel file name." + "<br /><br />";
								servlet = "../TableImport";
							}
							$("body").append(
									'<div id="import-dialog" class="ui-dialog">' + '<div class="ui-dialog-title">Import data ' + tableType + '</div>'
											+ '<div class="ui-dialog-content">' + dialogText + '<form action="' + servlet + '?product=' + productId + '&category=' + $id
											+ '&categorypath=' + getCategoryPath($(this)) + '" method="post" enctype="multipart/form-data">' + fields
											+ '<div class="fileform"><div id="fileformlabel"></div><div class="selectbutton ui-button">Browse...</div>'
											+ '<input id="file" type="file" name="file" size="1"/></div>'
											+ '<div class="dialog-buttons right"><input type="submit" class="ui-button" value="Import">'
											+ '</input> <button class="ui-button btn-cancel">Cancel</button></div></form></div></div>');
							initImportDialog(jQuery);
						}
					},
					sep1 : "---------",
					addCategory : {
						name : "Add subcategory",
						icon : "add",
						callback : function() {
							var $id = $(this).attr("id");
							$("body").append(
									'<div id="add-category-dialog" class="ui-dialog">' + '<div class="ui-dialog-title">Add category</div>' + '<div class="ui-dialog-content">'
											+ '<div class="table">' + '<div class="table-row"><div class="table-cell dialog-cell dialog-label">'
											+ 'Name:</div><div class="table-cell dialog-cell dialog-edit"><input class="category-name dialog-edit"></div>' + '</div>' + '</div>'
											+ '<div class="dialog-buttons right">' + '<button id="dialog-btn-add-category" parentid="' + $id + '" parent-path="'
											+ getCategoryPath($(this)) + '" class="ui-button">Add</button> <button class="ui-button btn-cancel">Cancel</button>'
											+ '</div></div></div>');
							initAddCategoryDialog(jQuery);
						}
					},
					edit : {
						name : "Edit category",
						icon : "edit",
						callback : function() {
							var $id = $(this).attr("id");
							$("body").append(
									'<div id="edit-category-dialog" class="ui-dialog">' + '<div class="ui-dialog-title">Edit category</div>' + '<div class="ui-dialog-content">'
											+ '<div class="table">' + '<div class="table-row"><div class="table-cell dialog-cell dialog-label">'
											+ 'Name:</div><div class="table-cell dialog-cell dialog-edit"><input class="category-name dialog-edit" value="' + $(this).text().trim()
											+ '"></div>' + '</div>' + '</div>' + '<div class="dialog-buttons right">' + '<button id="dialog-btn-edit-category" category-id="' + $id
											+ '" path="' + getCategoryPath($(this)) + '" class="ui-button">Save</button> <button class="ui-button btn-cancel">Cancel</button>'
											+ '</div></div></div>');
							initEditCategoryDialog(jQuery);
						}
					},
					"delete" : {
						name : "Delete category",
						icon : "delete",
						callback : function() {
							var $thisCategory = $(this);
							var $id = $thisCategory.attr("id");
							noty({
								type : "confirm",
								text : "Are you sure you want to delete this category?",
								buttons : [ {
									addClass : 'btn btn-primary ui-button',
									text : 'Delete',
									onClick : function($noty) {
										$noty.close();
										var $args;
										if (isJson()) {
											$args = {
												product : productId,
												tabletype : tableType,
												path : getCategoryPath($thisCategory)
											};
										} else {
											$args = {
												id : $id
											};
										}
										$.post("../DeleteCategory", $args, function(data) {
											if (data == "success") {
												noty({
													type : "success",
													text : "The category was deleted.",
													timeout : 3000
												});
												$thisCategory.remove();
												history.pushState({
													product : productId
												}, "", "?product=" + productId);
											} else {
												noty({
													type : "error",
													text : data
												});
											}
										});
									}
								}, {
									addClass : 'btn btn-danger ui-button',
									text : 'Cancel',
									onClick : function($noty) {
										$noty.close();
									}
								} ]
							});
						}
					},
				},
			});

	$("#btn-add-category").click(
			function() {
				$("body")
						.append(
								'<div id="add-category-dialog" class="ui-dialog">' + '<div class="ui-dialog-title">Add category</div>' + '<div class="ui-dialog-content">'
										+ '<div class="table">' + '<div class="table-row"><div class="table-cell dialog-cell dialog-label">'
										+ 'Name:</div><div class="table-cell dialog-cell dialog-edit"><input class="category-name dialog-edit"></div>' + '</div>' + '</div>'
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

		var name = $(".data-item-selected").find("span.tree-item-text").text().trim();
		document.title = name + " - " + $("#section-name").text() + " - Grible";
		$("#section-name").addClass("link-infront");

		var $breadcrumb = $("#breadcrumb");
		$breadcrumb.append("<span class='extends-symbol'>&nbsp;&gt;&nbsp;</span>");
		$breadcrumb.append("<a href='" + window.location + "'><span id='" + tableType + "-name'>" + name + "</span></a>");

		loadTableValues({
			id : tableId,
			product : productId
		});
		loadTopPanel({
			tabletype : tableType,
			tableid : tableId,
			product : productId
		});
	} else {
		$("#table-container").hide();
	}
}

function getCategoryPath(el) {
	var parentsText = ";" + el.text().trim();
	el.parents(".category-content-holder").each(function() {
		parentsText += ";" + $(this).prev("h3").text().trim();
	});
	return parentsText;
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
		var $args;
		if (isJson()) {
			var $path = $("#dialog-btn-add-data-item").attr("category-path");
			if (tableType == "storage") {
				$args = {
					tabletype : tableType,
					product : productId,
					categorypath : $path,
					name : $("input.data-item-name").val(),
					classname : $("input.data-storage-class-name").val(),
					iscopy : $("input.copy-existing").is(':checked'),
					copytableid : $("select.tables-list").find("option:selected").val(),
					isonlycolumns : $("input.only-columns").is(':checked')
				};
			} else if (tableType == "enumeration") {
				$args = {
					tabletype : tableType,
					product : productId,
					categorypath : $path,
					name : $("input.data-item-name").val(),
					iscopy : $("input.copy-existing").is(':checked'),
					copytableid : $("select.tables-list").find("option:selected").val(),
					isonlycolumns : $("input.only-columns").is(':checked')
				};
			} else {
				$args = {
					tabletype : "table",
					product : productId,
					categorypath : $path,
					name : $("input.data-item-name").val(),
					iscopy : $("input.copy-existing").is(':checked'),
					copytableid : $("select.tables-list").find("option:selected").val(),
					isonlycolumns : $("input.only-columns").is(':checked')
				};
			}
		} else {
			var $id = $("#dialog-btn-add-data-item").attr("category-id");
			if (tableType == "storage") {
				$args = {
					tabletype : tableType,
					categoryid : $id,
					name : $("input.data-item-name").val(),
					classname : $("input.data-storage-class-name").val(),
					iscopy : $("input.copy-existing").is(':checked'),
					copytableid : $("select.tables-list").find("option:selected").val(),
					isonlycolumns : $("input.only-columns").is(':checked')
				};
			} else if (tableType == "enumeration") {
				$args = {
					tabletype : tableType,
					categoryid : $id,
					name : $("input.data-item-name").val(),
					iscopy : $("input.copy-existing").is(':checked'),
					copytableid : $("select.tables-list").find("option:selected").val(),
					isonlycolumns : $("input.only-columns").is(':checked')
				};
			} else {
				$args = {
					tabletype : "table",
					categoryid : $id,
					name : $("input.data-item-name").val(),
					iscopy : $("input.copy-existing").is(':checked'),
					copytableid : $("select.tables-list").find("option:selected").val(),
					isonlycolumns : $("input.only-columns").is(':checked')
				};
			}
		}
		$.post("../AddTable", $args, function(newTableId) {
			if (isNaN(newTableId)) {
				noty({
					type : "error",
					text : newTableId
				});
			} else {
				$("#add-category-dialog").remove();
				if (isJson()) {
					window.location = "?product=" + productId + "&id=" + newTableId;
				} else {
					window.location = "?id=" + newTableId;
				}
			}
		});
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
		var $args;
		if (isNumber($("#dialog-btn-add-category").attr("parentid"))) {
			$args = {
				tabletype : tableType,
				product : productId,
				parent : $("#dialog-btn-add-category").attr("parentid"),
				parentpath : $("#dialog-btn-add-category").attr("parent-path"),
				name : $("input.category-name").val()
			};
		} else {
			$args = {
				tabletype : tableType,
				product : productId,
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
		var $args;
		if (isJson()) {
			$args = {
				product : productId,
				tabletype : tableType,
				name : $("input.category-name").val(),
				path : $("#dialog-btn-edit-category").attr("path")
			};
		} else {
			$args = {
				id : $("#dialog-btn-edit-category").attr("category-id"),
				name : $("input.category-name").val()
			};
		}
		$.post("../UpdateCategory", $args, function(data) {
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

	if ((tableType == "table") || (tableType == "precondition") || (tableType == "postcondition")) {
		$(".sheet-tab-container").click(function() {
			if ($(this).find(".sheet-tab").length > 0) {
				var $tab = $(this).find(".sheet-tab");
				$(".data-item-selected > .changed-sign").remove();
				$(".sheet-tab-selected").removeClass("sheet-tab-selected");
				$tab.addClass("sheet-tab-selected");
				tableId = $tab.attr('id');
				tableType = $tab.attr('label');

				if (isJson()) {
					history.pushState({
						product : productId,
						id : tableId
					}, "", "?product=" + productId + "&id=" + tableId);
				} else {
					history.pushState({
						id : tableId
					}, "", "?id=" + tableId);
				}

				loadTableValues({
					id : tableId,
					product : productId
				});
				loadTopPanel({
					tabletype : tableType,
					tableid : tableId,
					product : productId
				});
			}
		});
	} else {
		if (isChrome()) {
			$("#table-container").css("top", "19px");
		}
	}

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
			tabletype : "precondition",
			product : productId
		}, function(newTableId) {
			if (isJson()) {
				window.location = "?product=" + productId + "&id=" + newTableId;
			} else {
				window.location = "?id=" + newTableId;
			}
		});
	});

	$("#btn-add-postconditions").click(function() {
		$.post("../AddTable", {
			parentid : tableId,
			currTabletype : tableType,
			tabletype : "postcondition",
			product : productId
		}, function(newTableId) {
			if (isJson()) {
				window.location = "?product=" + productId + "&id=" + newTableId;
			} else {
				window.location = "?id=" + newTableId;
			}
		});
	});

	$("#btn-more").mouseenter(function() {
		var optionsTop = Math.floor($("#btn-more").offset().top) + $("#btn-more").height() + 11;
		var optionsLeft = $("#btn-more").offset().left + $("#btn-more").width() - $("#data-item-options").width() + 15;
		$("#data-item-options").css("top", optionsTop + "px");
		$("#data-item-options").css("left", optionsLeft + "px");
		$("#data-item-options").slideDown(150);
	}).mouseleave(function() {
		$("#data-item-options").slideUp(150);
	});

	$("#data-item-options").hover(function() {
		$(this).css("display", "block");
	});

	$("#btn-delete-data-item").click(function() {
		if ($(this).hasClass("button-enabled")) {
			noty({
				type : "confirm",
				text : "Are you sure you want to delete this " + tableType + "?",
				buttons : [ {
					addClass : 'btn btn-primary ui-button',
					text : 'Delete',
					onClick : function($noty) {
						$noty.close();
						$.post("../DeleteTable", {
							id : tableId,
							product : productId
						}, function(data) {
							if (data == "success") {
								noty({
									type : "success",
									text : "The " + tableType + " was deleted.",
									timeout : 3000
								});
								$(".data-item-selected").remove();
								$(".top-panel").find("div").hide();
								$("#table-container").hide();
								if ($("#breadcrumb>a").length > 3) {
									$(".extends-symbol").last().remove();
									$("#breadcrumb>a").last().remove();
									$("#section-name").removeClass("link-infront");
								}
								document.title = $("#section-name").text() + " - Grible";
								history.pushState({
									product : productId
								}, "", "?product=" + productId);
							} else if (isNumber(data)) {
								if (isJson()) {
									window.location = "?product=" + productId + "&id=" + data;
								} else {
									window.location = "?id=" + data;
								}
							} else {
								noty({
									type : "error",
									text : data
								});
							}
						});
					}
				}, {
					addClass : 'btn btn-danger ui-button',
					text : 'Cancel',
					onClick : function($noty) {
						$noty.close();
					}
				} ]
			});
		}
	});

	$("#btn-save-data-item").click(function() {
		if ($(this).hasClass("button-enabled")) {
			disableSaveButton();
			var $tableContainer = $("#table-container").handsontable('getInstance');
			var $keyNames = [];
			$(".handsontable thead th span.colHeader").each(function(i) {
				$keyNames[i] = $(this).text();
			});
			var $keyTypes = [];
			var $keyRefids = [];
			var $values = [];
			var $data = $tableContainer.getData();
			$.each($data, function(index, rowValues) {
				var row = "[\"";
				row += rowValues.join("\",\"");
				row += "\"]";
				$values.push(row);
			});

			$(".handsontable thead th").each(function(i) {
				if (i > 0) {
					$keyTypes.push($(this).attr("type"));
					$keyRefids.push($(this).attr("refid"));
				}
			});
			$.post("../SaveTable", {
				tableid : tableId,
				product : productId,
				keys : $keyNames,
				keyTypes : $keyTypes,
				keyRefids : $keyRefids,
				values : $values
			}, function(data) {
				if (data == "success") {
					$changedCells = [];
					$("td[modified]").removeAttr("modified");
					$("th[modified]").removeAttr("modified");
					$(".current-row").removeClass("current-row");
					$.post("../CheckForDuplicatedRows", {
						id : tableId,
						product : productId
					}, function(data) {
						var message = data.split("|");
						if (message[0] == "true") {
							for (var i = 1; i < message.length; i++) {
								noty({
									type : "warning",
									text : message[i]
								});
							}
						}
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

	$("#btn-edit-data-item").click(function() {
		if ($(this).hasClass("button-enabled")) {
			$.post("../GetEditTableDialog", {
				id : tableId,
				product : productId
			}, function(data) {
				$("body").append(data);
				initEditDataItemDialog(jQuery);
			});
		}
	});

	$("#btn-class-data-item").click(function() {
		$.post("../GetGeneratedClassDialog", {
			id : tableId,
			product : productId
		}, function(data) {
			$("body").append(data);
			initGeneratedClassDialog(jQuery);
		});
	});

	$("#btn-export-data-item").click(function() {
		$("#waiting-bg").addClass("loading");
		$.post("../ExportToExcel", {
			id : tableId,
			product : productId
		}, function(data) {
			$("#waiting-bg").removeClass("loading");
			if (data == "success") {
				document.location.href = "../export/" + $(".data-item-selected").text().trim() + ".xls";
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
		product : productId,
		usage : usage
	}, function(data) {
		$("#waiting-bg").removeClass("loading");
		if (data == "success") {
			loadTableValues({
				id : tableId,
				product : productId
			});
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
		product : productId,
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
		if (isJson()) {
			if (tableType == "storage") {
				$args = {
					id : tableId,
					categorypath : $("select.categories").find("option:selected").text(),
					name : $("input.data-item-name").val(),
					classname : $("input.data-storage-class-name").val(),
					product : productId
				};
			} else {
				$args = {
					id : tableId,
					categorypath : $("select.categories").find("option:selected").text(),
					name : $("input.data-item-name").val(),
					product : productId
				};
			}
		} else {
			if (tableType == "storage") {
				$args = {
					id : tableId,
					categoryid : $categoryid,
					name : $("input.data-item-name").val(),
					classname : $("input.data-storage-class-name").val(),
				};
			} else {
				$args = {
					id : tableId,
					categoryid : $categoryid,
					name : $("input.data-item-name").val(),
				};
			}
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

function loadTableValues(args) {
	$.post("../GetTableValues", args, function(res) {

		var storageCellRenderer = function(instance, td, row, col, prop, value, cellProperties) {
			Handsontable.renderers.TextRenderer.apply(this, arguments);
			$(td).addClass("storage-cell");
			$(td).attr("refid", $(".handsontable thead th:nth-child(" + (col + 2) + ")").attr("refid"));
		};
		var storageValidatorRegExp = function(value, callback) {
			if (/^\d+[;\d+]*$/.test(value)) {
				callback(true);
			} else {
				callback(false);
			}
		};

		var $data = jQuery.parseJSON(res);
		var $colNames = [];
		var $colTypes = [];
		var $colRefids = [];
		for (var i = 0; i < $data.keys.length; i++) {
			$colNames[i] = $data.keys[i].name;
			$colTypes[i] = $data.keys[i].type;
			$colRefids[i] = $data.keys[i].refid;
		}

		var $columns = $data.columns;
		var setColumnTypes = function(row, col, prop) {
			var cellProperties = {};
			if (col <= $columns.length - 1) {
				cellProperties.type = $columns[col].type;
				cellProperties.allowInvalid = $columns[col].allowInvalid;
				if ($columns[col].type === "dropdown") {
					cellProperties.source = $columns[col].source;
				}
				if ($columns[col].type === "text" && $columns[col].allowInvalid == false) {
					cellProperties.renderer = storageCellRenderer;
					cellProperties.validator = storageValidatorRegExp;
				}
			}
			return cellProperties;
		};

		var $isStoragesEmpty = $data.storages == null;
		var $storages = {};
		var $isEnumerationsEmpty = $data.enumerations == null;
		var $enums = {};
		if (!$isStoragesEmpty) {
			for (var i = 0; i < $data.storages.length; i++) {
				$storages[$data.storageIds[i]] = $data.storages[i];
			}
		}
		if (!$isEnumerationsEmpty) {
			for (var i = 0; i < $data.enumerations.length; i++) {
				$enums[$data.enumerationIds[i]] = $data.enumerations[i];
			}
		}

		var $tableContainer = $("#table-container");
		$tableContainer.handsontable({
			data : $data.values,
			manualColumnMove : true,
			manualColumnResize : true,
			contextMenu : true,
			rowHeaders : $data.isIndex,
			colHeaders : $colNames,
			currentRowClassName : 'current-row',
			cells : setColumnTypes,
			// width : $("#table-container").width(),
			// height : $("#table-container").height(),
			autoWrapRow : true,
			afterGetColHeader : function(col, TH) {
				TH.setAttribute("type", $colTypes[col]);
				TH.setAttribute("refid", $colRefids[col]);
			},
			afterChange : function(changes, source) {
				if (changes != null) {
					var isDataChanged = false;
					for (var i = 0; i < changes.length; i++) {
						if (changes[i][2] !== changes[i][3]) {
							isDataChanged = true;
							$changedCells.push({
								row : changes[i][0],
								col : changes[i][1]
							});
						}
					}
					if (isDataChanged) {
						enableSaveButton();
						setModifiedCells();
					}
				}
			},
			afterCreateRow : function(index, amount) {
				enableSaveButton();
				for (var i = 0; i < $changedCells.length; i++) {
					if ($changedCells[i].row >= index) {
						$changedCells[i].row++;
					}
				}
				setModifiedCells();
				for (var i = 0; i < $columns.length; i++) {
					if ($columns[i].type === "dropdown") {
						$tableContainer.handsontable("setDataAtCell", index, i, $columns[i].source[0]);
					}
					if ($columns[i].type === "text" && $columns[i].allowInvalid == false) {
						$tableContainer.handsontable("setDataAtCell", index, i, "0");
					}
				}
			},
			afterCreateCol : function(index, amount) {
				enableSaveButton();
				$colTypes.splice(index, 0, "text");
				$colRefids.splice(index, 0, "0");
				for (var i = 0; i < $changedCells.length; i++) {
					if ($changedCells[i].col >= index) {
						$changedCells[i].col++;
					}
				}
				setModifiedCells();
				$columns.splice(index, 0, {
					type : "text",
					allowInvalid : true
				});
				setColumnTypes();
			},
			afterRemoveRow : function(index, amount) {
				enableSaveButton();
				for (var i = 0; i < $changedCells.length; i++) {
					if ($changedCells[i].row === index) {
						$changedCells.slice(i, 1);
					} else if ($changedCells[i].row > index) {
						$changedCells[i].row--;
					}
				}
				setModifiedCells();
			},
			afterRemoveCol : function(index, amount) {
				enableSaveButton();
				for (var i = 0; i < $changedCells.length; i++) {
					if ($changedCells[i].col === index) {
						$changedCells.slice(i, 1);
					} else if ($changedCells[i].col > index) {
						$changedCells[i].col--;
					}
				}
				setModifiedCells();
			},
			afterRender : function() {
				if ($(".handsontable td.htInvalid").length > 0) {
					disableSaveButton();
				}
				$(".handsontable thead th").each(function(i) {
					if ($(this).find("span.colHeader").length > 0) {
						var $colHeader = $(this);
						var $colName = $colHeader.find("span.colHeader").text();

						var isTextRadioSelected = false;
						var isStorageRadioSelected = false;
						var isEnumRadioSelected = false;

						var storageSelected = 0;
						var enumSelected = 0;
						$.contextMenu({
							selector : ".handsontable thead th:nth-child(" + (i + 1) + ")",
							items : {
								name : {
									name : "Column name",
									type : 'text',
									value : $colName,
									icon : "edit"
								},
								sep1 : "---------",
								textradio : {
									name : "Text",
									type : "radio"
								},
								storageradio : {
									name : "Data storage",
									type : "radio",
									disabled : $isStoragesEmpty
								},
								storageselect : {
									type : "select",
									disabled : $isStoragesEmpty,
									options : $storages
								},
								enumradio : {
									name : "Enumeration",
									type : "radio",
									disabled : $isEnumerationsEmpty
								},
								enumselect : {
									type : "select",
									disabled : $isEnumerationsEmpty,
									options : $enums
								},
								sep2 : "---------",
								save : {
									name : "Save",
									icon : "save",
									callback : function(key, opt) {
										if (opt.inputs["name"].$input.val() !== $colHeader.find("span.colHeader").text()) {
											$colHeader.find("span.colHeader").text(opt.inputs["name"].$input.val());
											$colHeader.attr("modified", true);
											enableSaveButton();
										}
										var isTextTypeChanged = opt.inputs["textradio"].$input.is(":checked") != isTextRadioSelected;
										var isStorageTypeChanged = opt.inputs["storageradio"].$input.is(":checked") != isStorageRadioSelected;
										var isEnumTypeChanged = opt.inputs["enumradio"].$input.is(":checked") != isEnumRadioSelected;
										var isStorageIdChanged = opt.inputs["storageselect"].$input.val() != storageSelected;
										var isEnumIdChanged = opt.inputs["enumselect"].$input.val() != enumSelected;
										if (isTextTypeChanged || isStorageTypeChanged || isEnumTypeChanged || isStorageIdChanged || isEnumIdChanged) {
											var $tableInstance = $tableContainer.handsontable('getInstance');
											if (opt.inputs["textradio"].$input.is(":checked")) {
												$columns[i - 1].type = "text";
												$columns[i - 1].allowInvalid = true;
												$tableInstance.updateSettings({
													cells : setColumnTypes
												});

												$colTypes[i - 1] = "text";
												$colRefids[i - 1] = "0";
												$colHeader.attr("type", "text");
												$colHeader.attr("refid", "0");
											} else if (opt.inputs["storageradio"].$input.is(":checked")) {
												$columns[i - 1].type = "text";
												$columns[i - 1].allowInvalid = false;
												$tableInstance.updateSettings({
													cells : setColumnTypes
												});
												$tableInstance.validateCells(function() {
													$tableInstance.render();
												});

												$colTypes[i - 1] = "storage";
												$colRefids[i - 1] = opt.inputs["storageselect"].$input.val();
												$colHeader.attr("type", "storage");
												$colHeader.attr("refid", opt.inputs["storageselect"].$input.val());
											} else {
												$columns[i - 1].type = "dropdown";
												$columns[i - 1].allowInvalid = false;
												$.post("../GetEnumValues", {
													tableid : opt.inputs["enumselect"].$input.val(),
													product : productId
												}, function(options) {
													var optionsArray = jQuery.parseJSON(options);
													$columns[i - 1].source = optionsArray;
													$tableInstance.updateSettings({
														cells : setColumnTypes
													});
													var changes = [];
													for (var j = 0; j < $tableInstance.countRows(); j++) {
														if (optionsArray.indexOf($tableInstance.getDataAtCell(j, (i - 1))) == -1) {
															changes.push([ j, (i - 1), optionsArray[0] ]);
														}
													}
													$tableInstance.setDataAtCell(changes);
													$tableInstance.validateCells(function() {
														$tableInstance.render();
													});
												});

												$colTypes[i - 1] = "enumeration";
												$colRefids[i - 1] = opt.inputs["enumselect"].$input.val();
												$colHeader.attr("type", "enumeration");
												$colHeader.attr("refid", opt.inputs["enumselect"].$input.val());
											}
											$colHeader.attr("modified", true);
											enableSaveButton();
										}
									}
								}
							},
							events : {
								show : function(opt) {
									$(".context-menu-input").each(function(i) {
										if ($(this).find("span").text() === "") {
											$(this).find("span").remove();
										}
									});
									storageSelected = $colHeader.attr("refid");
									enumSelected = $colHeader.attr("refid");
									if ($colHeader.attr("type") === "text") {
										isTextRadioSelected = true;
										isStorageRadioSelected = false;
										isEnumRadioSelected = false;
										storageSelected = $data.storageIds[0];
										enumSelected = $data.enumerationIds[0];
									} else if ($colHeader.attr("type") === "storage") {
										isTextRadioSelected = false;
										isStorageRadioSelected = true;
										isEnumRadioSelected = false;
										enumSelected = $data.enumerationIds[0];
									} else {
										isTextRadioSelected = false;
										isStorageRadioSelected = false;
										isEnumRadioSelected = true;
										storageSelected = $data.storageIds[0];
									}
									opt.items.textradio.selected = isTextRadioSelected;
									opt.items.storageradio.selected = isStorageRadioSelected;
									opt.items.enumradio.selected = isEnumRadioSelected;
									opt.items.storageselect.selected = storageSelected;
									opt.items.enumselect.selected = enumSelected;
								}
							}
						});
					}
				});

				initTooltipCells($(".storage-cell"));

			},
		});

		function setModifiedCells() {
			$(".handsontable td[modified]").removeAttr("modified");
			for (var i = 0; i < $changedCells.length; i++) {
				var td = $tableContainer.handsontable("getCell", $changedCells[i].row, $changedCells[i].col);
				if (td != null) {
					td.setAttribute("modified", true);
				}
			}
		}

		$("#waiting-bg").removeClass("loading");
		if (isChrome()) {
			$("#main .table-cell").removeClass("floatleft");
		}
	});
}

function isNumber(n) {
	return !isNaN(parseFloat(n)) && isFinite(n);
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
		if (($value.has("span.old-value").length == 0) && ($value.text() != "0") && ($value.text() != "") && (!$value.hasClass("modified-value-cell"))) {
			if ($value.has("div.tooltip").length == 0) {
				var $content = $value.text();
				var $args;
				if (isJson()) {
					$args = {
						product : productId,
						tableid : tableId,
						keyorder : $value.attr('keyid'),
						content : $content
					};
				} else {
					$args = {
						id : $value.attr('id'),
						content : $content
					};
				}
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

	function initTooltipCellsOnHover(td) {
		var $value = td;
		if (($value.text() != "0") && ($value.text() != "") && ($value.has("div.tooltip").length == 0)) {
			var $content = $value.text();
			var $args = {
				product : productId,
				refid : $value.attr("refid"),
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

		var $widthRight = $("#table-container").width() - $value.position().left - 17;
		var $tooltipWidth = $tooltip.width();
		if ($widthRight < $tooltipWidth) {
			if ($tooltipWidth > $("#table-container").width()) {
				$tooltip.css("max-width", ($("#table-container").width() - 20) + "px");
				$tooltip.css("left", "0px");
			} else {
				$tooltip.css("margin-left", "-" + ($tooltipWidth - $widthRight) + "px");
			}
		}

		var $heightToBorder;
		if ($value.position().top < $("#table-container").height() * 0.7) {
			$heightToBorder = $("#table-container").height() - $value.position().top - 17;
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
				$tooltip.css("margin-top", "-" + ($tooltip.height() + 35) + "px");
			}
		}
	}
}

function disableSaveButton() {
	$(".data-item-selected > .changed-sign").remove();
	$("#btn-save-data-item").removeClass("button-enabled");
	$("#btn-save-data-item").addClass("button-disabled");
}

function enableSaveButton() {
	$(".data-item-selected:not(:has(> img.changed-sign))").append(" <img class='changed-sign' src='../img/modified.png'>");
	$("#btn-save-data-item").removeClass("button-disabled");
	$("#btn-save-data-item").addClass("button-enabled");
}

function showImportResult(message) {
	if (message.indexOf("WARNING") > 0) {
		noty({
			type : "success",
			text : message.substring(0, message.indexOf("WARNING")),
			timeout : 3000
		});
		noty({
			type : "warning",
			text : message.substring(message.indexOf("WARNING"))
		});
	} else if (message.indexOf("successfully") > 0) {
		noty({
			type : "success",
			text : message,
			timeout : 3000
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
		noteText = "<strong>Note:</strong> Only General table will change. Preconditions and Postconditions are ignored.<br /><br />";
	}
	var options = '<br/><br/><input type="radio" value="addtoend" name="import-option" checked="checked">Add to the end of the table'
			+ '<br/><br/><input type="radio" value="addfromrow" name="import-option">Replace from row: ' + '<input id="start-row" size="4" disabled="disabled" value="1"/>';
	$("body").append(
			'<div id="advanced-import-dialog" class="ui-dialog">' + '<div class="ui-dialog-title">Import existing data ' + tableType + '</div>' + '<div class="ui-dialog-content">'
					+ '<br />Data ' + tableType + ' with the same name was found.<br /><br /><br />' + '<div class="table"><div class="table-row">'
					+ '<div class="table-cell dialog-cell dialog-label">Rows in the current ' + tableType + ':</div><div class="table-cell dialog-cell">' + currentRowsCount
					+ '</div></div><div class="table-row"><div class="table-cell dialog-cell dialog-label">Rows in the file being imported:</div>'
					+ '<div class="table-cell dialog-cell">' + importedRowsCount + '</div></div></div>' + '<br/><br/>How would you like to apply changes?' + options
					+ '<br /><br />' + noteText + '<div class="dialog-buttons right"><button class="ui-button btn-apply">Apply</button> '
					+ '<button class="ui-button btn-cancel">Cancel</button></div></div></div>');
	initAdvancedImportDialog(jQuery);
}

function initAdvancedImportDialog() {
	initDialog();
	$(".btn-cancel").click(function() {
		$.post("../ClearImportSessionParameters", function() {
			$(".ui-dialog").remove();
		});
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
	for (var i = 0; i < keyIds.length; i++) {
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

function isJson() {
	return appType == "json";
}