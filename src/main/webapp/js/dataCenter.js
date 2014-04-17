var $isRowsUsageShown = false;
var $isRowsUsageJustTurnedOn = false;
var $colTypes = [];
var $colRefids = [];
var $draggedRowValues = [];
var $rowNumbers = [];
var $tableGenerationTime = "";
var $tableGenerationTimer;
var $isTimeMessageShown = false;

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
		clearInterval($tableGenerationTimer);
		$(".category-item-selected").removeClass("category-item-selected");
		thisCategoryItem.addClass("category-item-selected");
		$(".data-item-selected").removeClass("data-item-selected");
		$(".data-item-selected").find(".changed-sign").remove();
		$(".top-panel").find("div").hide();
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
		loadTableValues();
		loadTopPanel();
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

		loadTableValues();
		loadTopPanel();
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

function loadTopPanel() {
	var args = {
		tabletype : tableType,
		tableid : tableId,
		product : productId,
		filter : filter
	};
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

				$("#table-container").handsontable("destroy");
				loadTableValues();
				loadTopPanel();
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
			deleteTable();
		}
	});

	$("#btn-save-data-item").click(function() {
		if ($(this).hasClass("button-enabled")) {
			saveTable();
		}
	});

	$("#btn-discard-filter").click(function() {
		if ($(this).hasClass("button-enabled")) {
			if (isJson()) {
				window.location = "?product=" + productId + "&id=" + tableId;
			} else {
				window.location = "?id=" + tableId;
			}
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

	$("#btn-filter").click(function() {
		$.post("../GetFilterDialog", {
			tableid : tableId,
			product : productId
		}, function(data) {
			$("body").append(data);
			initFilterDialog(jQuery);
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

function deleteTable() {
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
						clearInterval($tableGenerationTimer);
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

function saveTable() {
	disableSaveButton();
	$("#waiting-bg").addClass("loading");
	var $tableContainer = $("#table-container").handsontable('getInstance');
	var $keyNames = $tableContainer.getColHeader();
	if ($isRowsUsageShown) {
		$keyNames.splice($keyNames.length - 2, 2);
	}

	$.post("../SaveTableHead", {
		keys : $keyNames,
		keyTypes : $colTypes,
		keyRefids : $colRefids
	}, function(data) {
		if (data == "success") {
			var $rowsCount = $tableContainer.countRows();
			var $counter = 0;

			(function saveTableRow($counter) {
				var $values = $tableContainer.getDataAtRow($counter);
				if ($isRowsUsageShown) {
					$values.splice($values.length - 2, 2);
				}
				$.post("../SaveTableRow", {
					tableid : tableId,
					product : productId,
					row : $counter,
					values : $values,
					islastrow : ($rowsCount - $counter === 1)
				}, function(data) {
					var res = data.split("|");
					if (res[0] == "success") {
						$counter++;
						if ($counter < $rowsCount) {
							saveTableRow($counter);
						} else {
							$(".current-row").removeClass("current-row");
							$tableGenerationTime = res[1];
							$tableContainer.render();
							$("#cbx-show-usage").prop("checked", false);
							if (tableType == "storage") {
								$.post("../UpdateRowsOrder", {
									tableid : tableId,
									product : productId,
									rows : $rowNumbers
								}, function(data) {
									$("#waiting-bg").removeClass("loading");
									if (data == "success") {
										for (var i = 0; i < $rowsCount; i++) {
											$rowNumbers[i] = i;
										}
									} else {
										noty({
											type : "error",
											text : data
										});
									}
								});
							} else {
								$("#waiting-bg").removeClass("loading");
								for (var i = 0; i < $rowsCount; i++) {
									$rowNumbers[i] = i;
								}
							}

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
						}
					} else {
						noty({
							type : "error",
							text : data
						});
					}
				});
			})($counter);

		} else {
			noty({
				type : "error",
				text : data
			});
		}
	});
}

function setRowsUsage(usage) {
	$("#data-item-options").off("mouseleave");
	$("#data-item-options").hide(1);
	$("#data-item-options").on("mouseleave", function() {
		$("#data-item-options").slideUp(150);
	});
	$isRowsUsageShown = usage;
	if (usage) {
		$("#waiting-bg").addClass("loading");
		$.post("../GetRowsUsage", {
			tableid : tableId,
			product : productId
		}, function(res) {
			$("#waiting-bg").removeClass("loading");
			try {
				$isRowsUsageJustTurnedOn = true;
				var $rowsUsage = jQuery.parseJSON(res);
				var $tableInstance = $("#table-container").handsontable('getInstance');
				var countCols = $tableInstance.countCols();
				$tableInstance.alter('insert_col', countCols, 2);
				countCols += 2;

				var changes = [];
				for (var i = 0; i < $tableInstance.countRows(); i++) {
					changes.push([ i, countCols - 2, $rowsUsage.tables[i] ]);
					changes.push([ i, countCols - 1, $rowsUsage.storages[i] ]);
				}
				$tableInstance.setDataAtCell(changes);
				var $newColHeader = $tableInstance.getColHeader();
				$newColHeader[$newColHeader.length - 2] = "Used in tables";
				$newColHeader[$newColHeader.length - 1] = "Used in storages";

				$tableInstance.updateSettings({
					colHeaders : $newColHeader
				});

			} catch (err) {
				noty({
					type : "error",
					text : err.message
				});
			}
		});
	} else {
		var $tableInstance = $("#table-container").handsontable('getInstance');
		var $countCols = $tableInstance.countCols();
		$tableInstance.alter('remove_col', $countCols - 2, 2);
	}
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

function initFilterDialog() {
	initDialog();

	var submitDataItem = function() {
		var $filter = 0;
		if ($("input[name='table-type'][value='table']").is(":checked")) {
			$filter = $("select.tables-list").find("option:selected").val();
		} else if ($("input[name='table-type'][value='storage']").is(":checked")) {
			$filter = $("select.storage-list").find("option:selected").val();
		}
		if (isNumber($filter) && $filter > 0) {
			if (isJson()) {
				window.location = "?product=" + productId + "&id=" + tableId + "&filter=" + $filter;
			} else {
				window.location = "?id=" + tableId + "&filter=" + $filter;
			}
		}
	};

	$("#dialog-btn-filter").click(submitDataItem);

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

function loadTableValues() {
	var args = {
		id : tableId,
		product : productId,
		filter : filter
	};

	$.post("../GetTableValues", args, function(res) {

		var defaultCellRenderer = function(instance, td, row, col, prop, value, cellProperties) {
			Handsontable.renderers.TextRenderer.apply(this, arguments);
			$(td).removeClass("storage-cell");
			$(td).removeAttr("refid");
		};
		var storageCellRenderer = function(instance, td, row, col, prop, value, cellProperties) {
			Handsontable.renderers.TextRenderer.apply(this, arguments);
			$(td).addClass("storage-cell");
			$(td).attr("refid", $colRefids[col]);
		};
		var storageValidatorRegExp = function(value, callback) {
			if (/^\d+[;\d+]*$/.test(value)) {
				callback(true);
			} else {
				callback(false);
			}
		};

		var $data = jQuery.parseJSON(res);

		$tableGenerationTime = $data.time;
		var pingModifiedTime = function() {
			$.post("../PingModifiedTime", {
				time : $tableGenerationTime,
				id : tableId,
				product : productId
			}, function(res) {
				if (res === "logged-out") {
					location.reload(true);
				} else if (res !== "" && !$isTimeMessageShown) {
					$isTimeMessageShown = true;
					noty({
						type : "warning",
						text : res
					});
				}
			});
		};
		$tableGenerationTimer = setInterval(pingModifiedTime, 5000);

		var $colNames = [];
		for (var i = 0; i < $data.keys.length; i++) {
			$colNames[i] = $data.keys[i].name;
			$colTypes[i] = $data.keys[i].type;
			$colRefids[i] = $data.keys[i].refid;
		}

		var $columns = $data.columns;
		var setColumnTypes = function(row, col, prop) {
			var cellProperties = {};
			if (col <= $columns.length - 1) {
				cellProperties.readOnly = false;
				cellProperties.type = $columns[col].type;
				cellProperties.allowInvalid = $columns[col].allowInvalid;
				if ($columns[col].type === "dropdown") {
					cellProperties.source = $columns[col].source;
				}
				if ($columns[col].type === "text" && $columns[col].allowInvalid == false) {
					cellProperties.renderer = storageCellRenderer;
					cellProperties.validator = storageValidatorRegExp;
				} else {
					cellProperties.renderer = defaultCellRenderer;
				}
			} else if (col - $columns.length < 2) {
				cellProperties.readOnly = true;
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

		$("#table-container").handsontable("destroy");
		var $tableContainer = $("#table-container");
		$tableContainer.handsontable({
			data : $data.values,
			manualColumnMove : true,
			manualColumnResize : true,
			contextMenu : true,
			rowHeaders : $data.rowHeaders,
			colHeaders : $colNames,
			currentRowClassName : 'current-row',
			cells : setColumnTypes,
			width : $("#table-container").width(),
			height : $("#table-container").height(),
			autoWrapRow : true,
			afterGetColHeader : function(col, TH) {
				TH.setAttribute("type", $colTypes[col]);
				TH.setAttribute("refid", $colRefids[col]);
			},
			afterChange : function(changes, source) {
				if (changes != null && !$isRowsUsageJustTurnedOn) {
					var isDataChanged = false;
					for (var i = 0; i < changes.length; i++) {
						if (changes[i][2] !== changes[i][3]) {
							isDataChanged = true;
							break;
						}
					}
					if (isDataChanged) {
						enableSaveButton();
					}
				}
				if ($isRowsUsageJustTurnedOn) {
					$isRowsUsageJustTurnedOn = false;
				}
			},
			afterCreateRow : function(index, amount) {
				enableSaveButton();
				if ($draggedRowValues.length === 0) {
					// adding row
					for (var i = 0; i < $columns.length; i++) {
						if ($columns[i].type === "dropdown") {
							$tableContainer.handsontable("setDataAtCell", index, i, $columns[i].source[0]);
						} else if ($columns[i].type === "text" && $columns[i].allowInvalid == false) {
							$tableContainer.handsontable("setDataAtCell", index, i, "0");
						} else {
							$tableContainer.handsontable("setDataAtCell", index, i, "");
						}
					}
					$rowNumbers.splice(index, 0, -1);
				} else {
					// dragging row
					for (var i = 0; i < $columns.length; i++) {
						$tableContainer.handsontable("setDataAtCell", index, i, $draggedRowValues[i]);
					}
					$draggedRowValues = [];
					$("#waiting-bg").removeClass("loading");
				}
			},
			afterCreateCol : function(index, amount) {
				if (amount == 1) {
					enableSaveButton();
					$colTypes.splice(index, 0, "text");
					$colRefids.splice(index, 0, "0");
					$columns.splice(index, 0, {
						type : "text",
						allowInvalid : true
					});
					if ($isRowsUsageShown) {
						$tableContainer.handsontable("updateSettings", {
							cells : setColumnTypes
						});
					}
				}
			},
			beforeRemoveRow : function(index, amount) {
				if ($draggedRowValues.length > 0 || $rowNumbers[index] === -1) {
					return true;
				}
				var res = $.ajax({
					type : "POST",
					url : "../GetRowUsage",
					data : {
						tableid : tableId,
						product : productId,
						row : $rowNumbers[index]
					},
					async : false
				}).responseText;
				if (res !== "") {
					noty({
						type : "error",
						text : res
					});
					return false;
				}
				return true;
			},
			afterRemoveRow : function(index, amount) {
				enableSaveButton();
				$rowNumbers.splice(index, 1);
			},
			afterRemoveCol : function(index, amount) {
				if (amount == 1) {
					enableSaveButton();
				}
			},
			afterInit : function() {

				var $tableInstance = $tableContainer.handsontable('getInstance');
				if (tableType == "enumeration") {
					$tableInstance.updateSettings({
						contextMenu : [ 'row_above', 'row_below', 'hsep1', 'remove_row', 'hsep3', 'undo', 'redo' ]
					});
				} else if (tableType == "precondition" || tableType == "postcondition") {
					$tableInstance.updateSettings({
						contextMenu : [ 'col_left', 'col_right', 'hsep2', 'remove_col', 'hsep3', 'undo', 'redo' ]
					});
				} else {
					$(".handsontable .htCore tbody").sortable({
						update : function(event, ui) {
							$("#waiting-bg").addClass("loading");
							var $draggedRow = ui.item;
							var $rowIndex = $draggedRow.find("th").text() - 1;
							var $unsavedRowNumber = $rowNumbers[$rowIndex];
							$draggedRowValues = $tableInstance.getDataAtRow($rowIndex);
							$tableInstance.alter("remove_row", $rowIndex, 1);

							var $newRowIndex = parseInt($draggedRow.prev().find("th").text());
							$tableInstance.alter("insert_row", $newRowIndex, 1);
							$rowNumbers.splice($newRowIndex, 0, $unsavedRowNumber);
						}
					});
				}
				for (var i = 0; i < $data.values.length; i++) {
					$rowNumbers[i] = i;
				}
			},
			afterRender : function() {
				if ($(".handsontable td.htInvalid").length > 0) {
					disableSaveButton();
				}

				var $tableInstance = $tableContainer.handsontable('getInstance');

				$(".handsontable thead th").each(function(i) {
					if (($(this).find("span.colHeader").length > 0) && (tableType != "enumeration") && ($(this).attr("type") != "undefined")) {
						var $colHeader = $(this);
						var $colName = $colHeader.find("span.colHeader").text();

						var isTextRadioSelected = false;
						var isStorageRadioSelected = false;
						var isEnumRadioSelected = false;

						var storageSelected = 0;
						var enumSelected = 0;

						var runOptions = {};
						function onSaveCoulmnHeaderProperties() {
							if (runOptions.inputs["name"].$input.val() !== $colHeader.find("span.colHeader").text()) {
								var $newColHeader = $tableInstance.getColHeader();
								var $firstVisibleColIndex = $tableInstance.colOffset();
								$newColHeader[$firstVisibleColIndex + i - 1] = runOptions.inputs["name"].$input.val();
								$tableInstance.updateSettings({
									colHeaders : $newColHeader
								});
								enableSaveButton();
							}
							var isTextTypeChanged = runOptions.inputs["textradio"].$input.is(":checked") != isTextRadioSelected;
							var isStorageTypeChanged = runOptions.inputs["storageradio"].$input.is(":checked") != isStorageRadioSelected;
							var isEnumTypeChanged = runOptions.inputs["enumradio"].$input.is(":checked") != isEnumRadioSelected;
							var isStorageIdChanged = runOptions.inputs["storageselect"].$input.val() != storageSelected;
							var isEnumIdChanged = runOptions.inputs["enumselect"].$input.val() != enumSelected;
							if (isTextTypeChanged || isStorageTypeChanged || isEnumTypeChanged || isStorageIdChanged || isEnumIdChanged) {
								if (runOptions.inputs["textradio"].$input.is(":checked")) {
									$columns[i - 1].type = "text";
									$columns[i - 1].allowInvalid = true;
									$tableInstance.updateSettings({
										cells : setColumnTypes
									});

									$colTypes[i - 1] = "text";
									$colRefids[i - 1] = "0";
									$colHeader.attr("type", "text");
									$colHeader.attr("refid", "0");
								} else if (runOptions.inputs["storageradio"].$input.is(":checked")) {
									$columns[i - 1].type = "text";
									$columns[i - 1].allowInvalid = false;
									$tableInstance.updateSettings({
										cells : setColumnTypes
									});
									$tableInstance.validateCells(function() {
										$tableInstance.render();
									});

									$colTypes[i - 1] = "storage";
									$colRefids[i - 1] = runOptions.inputs["storageselect"].$input.val();
									$colHeader.attr("type", "storage");
									$colHeader.attr("refid", runOptions.inputs["storageselect"].$input.val());
								} else {
									$columns[i - 1].type = "dropdown";
									$columns[i - 1].allowInvalid = false;
									$.post("../GetEnumValues", {
										tableid : runOptions.inputs["enumselect"].$input.val(),
										product : productId
									}, function(runOptionsions) {
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
									$colRefids[i - 1] = runOptions.inputs["enumselect"].$input.val();
									$colHeader.attr("type", "enumeration");
									$colHeader.attr("refid", runOptions.inputs["enumselect"].$input.val());
								}
								enableSaveButton();
							}
						}
						;

						$.contextMenu({
							selector : ".handsontable thead th:nth-child(" + (i + 1) + ")",
							items : {
								name : {
									name : "Column name",
									type : 'text',
									value : $colName,
									icon : "edit",
									events : {
										keyup : function(event) {
											if (event.which === 13) {
												onSaveCoulmnHeaderProperties();
												$(".handsontable thead th:nth-child(" + (i + 1) + ")").contextMenu("hide");
											}
										}
									}
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
										runOptions = opt;
										onSaveCoulmnHeaderProperties();
									}
								}
							},
							events : {
								show : function(opt) {
									opt.items.name.value = $colHeader.find("span.colHeader").text();
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
										if ($data.storageIds != null) {
											storageSelected = $data.storageIds[0];
										}
										if ($data.enumerationIds != null) {
											enumSelected = $data.enumerationIds[0];
										}
									} else if ($colHeader.attr("type") === "storage") {
										isTextRadioSelected = false;
										isStorageRadioSelected = true;
										isEnumRadioSelected = false;
										if ($data.enumerationIds != null) {
											enumSelected = $data.enumerationIds[0];
										}
									} else {
										isTextRadioSelected = false;
										isStorageRadioSelected = false;
										isEnumRadioSelected = true;
										if ($data.storageIds != null) {
											storageSelected = $data.storageIds[0];
										}
									}
									opt.items.textradio.selected = isTextRadioSelected;
									opt.items.storageradio.selected = isStorageRadioSelected;
									opt.items.enumradio.selected = isEnumRadioSelected;
									opt.items.storageselect.selected = storageSelected;
									opt.items.enumselect.selected = enumSelected;
									runOptions = opt;
								}
							}
						});
					}
				});
				initTooltipCells($("td"));
			},
		});

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
		elements.each(function(i) {
			if ($(this).is(".storage-cell")) {
				$(this).off("mouseenter mouseleave");
				$(this).click(initTooltipCellsOnClick);
			} else {
				$(this).unbind("click");
			}
		});
	} else {
		elements.each(function(i) {
			if ($(this).is(".storage-cell")) {
				$(this).hover(initTooltipCellsOnHover);
			} else {
				$(this).off("mouseenter mouseleave");
			}
		});
	}

	function initTooltipCellsOnClick() {
		var $value = $(this);
		if (($value.text() != "0") && ($value.text() != "")) {
			if ($value.has("div.tooltip").length == 0) {
				var $content = $value.text();
				var $args = {
					tableid : tableId,
					product : productId,
					refid : $value.attr("refid"),
					content : $content
				};
				$.post("../GetStorageTooltip", $args, function(data) {
					if (data.indexOf("ERROR") === 0) {
						noty({
							type : "error",
							text : data
						});
					} else {
						$value.html(data);
						var $tooltip = $value.find("div.tooltip");
						adjustTooltipPosition($value, $tooltip);
						$value.find("div.tooltip").show();
					}
				});
			} else {
				$value.find("div.tooltip").toggle();
			}
		}
	}

	function initTooltipCellsOnHover() {
		var $value = $(this);
		if (($value.text() != "0") && ($value.text() != "") && ($value.has("div.tooltip").length == 0)) {
			var $content = $value.text();
			var $args = {
				tableid : tableId,
				product : productId,
				refid : $value.attr("refid"),
				content : $content
			};
			$.post("../GetStorageTooltip", $args, function(data) {
				if (data.indexOf("ERROR") === 0) {
					noty({
						type : "error",
						text : data
					});
				} else {
					$value.html(data);
					var $tooltip = $value.find("div.tooltip");
					adjustTooltipPosition($value, $tooltip);
				}
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
