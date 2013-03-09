$().ready(initialize());

function initialize() {

	$(document).ajaxError(
			function(e, xhr, settings, exception) {
				var exrrorText = xhr.responseText.substring(xhr.responseText.indexof("<h1>"));
				$("body").append(
						'<div id="error-dialog" class="ui-dialog">' + '<div class="ui-dialog-title">Error</div>'
								+ '<div class="ui-dialog-content">' + 'Location: ' + settings.url + exrrorText
								+ '<br><br>' + '<div class="right">'
								+ '<button class="ui-button btn-cancel">OK</button>' + '</div></div></div>');
				initOneButtonDialog(jQuery);
			});

	$.post("../GetCategories", {
		productId : productId,
		tableId : tableId,
		tableType : tableType
	}, function(data) {
		$("#waiting-bg").remove();
		$("#footer").removeClass("page-bottom");
		$("#category-container").html(data);
		$(".entities-list").append(
				'<span class="top-panel-button button-enabled" id="btn-add-category"><img src="../img/add-icon.png"'
						+ 'class="top-panel-icon">&nbsp;&nbsp;Add category</span>');

		initDataItemsPanel(jQuery);
	});

}

function initDataItemsPanel() {

	$("#category-container").accordion({
		heightStyle : "content"
	});

	$(".category-item-selected").click();

	$(".category-item").click(function() {
		$(".category-item-selected").removeClass("category-item-selected");
		$(this).addClass("category-item-selected");
		$(".data-item-selected").removeClass("data-item-selected");
		$(".top-panel").html("");
		$(".entities-values").html("");
	});

	$(".data-item").click(function() {
		$("#waiting").addClass("loading");
		$(".data-item-selected").find("span.changed-sign").remove();
		$(".data-item-selected").removeClass("data-item-selected");
		$("#btn-edit-data-item").removeClass("button-disabled");
		$("#btn-edit-data-item").addClass("button-enabled");
		$(this).addClass("data-item-selected");
		tableId = $(this).attr('id');
		history.pushState({
			id : tableId
		}, "", "?id=" + tableId);
		loadTopPanel({
			tabletype : tableType,
			tableid : tableId
		});
		loadTableValues(tableId);
	});

	$(".category-item")
			.contextMenu(
					{
						menu : 'categoryMenu'
					},
					function(action, el, pos) {
						var $id = $(el).attr("id");
						var $className = "";
						if (tableType == "storage") {
							$className = '<div class="table-row"><div class="table-cell dialog-cell">'
									+ 'Class name:</div><div class="table-cell dialog-cell"><input class="data-storage-class-name dialog-edit"></div>'
									+ '</div>';
						}
						if (action == "add") {
							$("body")
									.append(
											'<div id="add-data-'
													+ tableType
													+ '-dialog" class="ui-dialog">'
													+ '<div class="ui-dialog-title">Add data '
													+ tableType
													+ '</div>'
													+ '<div class="ui-dialog-content">'
													+ '<div class="table">'
													+ '<div class="table-row"><div class="table-cell dialog-cell">'
													+ 'Name:</div><div class="table-cell dialog-cell"><input class="data-item-name dialog-edit"></div>'
													+ '</div>'
													+ $className
													+ '</div>'
													+ '<br/>The data '
													+ tableType
													+ ' will be added to the category "'
													+ $(el).text()
													+ '".'
													+ '<div class="dialog-buttons right">'
													+ '<button id="dialog-btn-add-data-item" category-id="'
													+ $id
													+ '" class="ui-button">Add</button> <button class="ui-button btn-cancel">Cancel</button>'
													+ '</div></div></div>');
							initAddDataItemDialog(jQuery);
						} else if (action == "edit") {
							$("body")
									.append(
											'<div id="edit-category-dialog" class="ui-dialog">'
													+ '<div class="ui-dialog-title">Edit category</div>'
													+ '<div class="ui-dialog-content">'
													+ '<div class="table">'
													+ '<div class="table-row"><div class="table-cell dialog-cell">'
													+ 'Name:</div><div class="table-cell dialog-cell"><input class="category-name dialog-edit" value="'
													+ $(el).text()
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
							var answer = confirm("Are you sure you want to delete this category?");
							if (answer) {
								$.post("../DeleteCategory", {
									id : $id
								}, function(data) {
									if (data == "success") {
										alert("Category was deleted.");
										window.location = "?product=" + productId;
									} else {
										alert(data);
									}
								});
							}
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
												+ '<div class="table-row"><div class="table-cell dialog-cell">'
												+ 'Name:</div><div class="table-cell dialog-cell"><input class="category-name dialog-edit"></div>'
												+ '</div>'
												+ '</div>'
												+ '<div class="dialog-buttons right">'
												+ '<button id="dialog-btn-add-category" class="ui-button">Add</button> <button class="ui-button btn-cancel">Cancel</button>'
												+ '</div></div></div>');
						initAddCategoryDialog(jQuery);
					});

	if (tableId > 0) {
		$("#waiting").addClass("loading");
		loadTopPanel({
			tabletype : tableType,
			tableid : tableId
		});
		loadTableValues(tableId);
	}
}

function initDialog() {
	var $dialog = $(".ui-dialog");
	var $posTop = ($(window).height() - $dialog.height()) / 2;
	var $posLeft = ($(window).width() - $dialog.width()) / 2;
	$dialog.css("top", $posTop);
	$dialog.css("left", $posLeft);
}

function initAddDataItemDialog() {
	initDialog();
	$("input.data-item-name").focus();

	$("#dialog-btn-add-data-item").click(function() {
		var $id = $(this).attr("category-id");
		var $args;
		if (tableType == "storage") {
			$args = {
				tabletype : tableType,
				categoryid : $id,
				name : $("input.data-item-name").val(),
				classname : $("input.data-storage-class-name").val()
			};
		} else {
			$args = {
				tabletype : "table",
				categoryid : $id,
				name : $("input.data-item-name").val()
			};
		}
		$.post("../AddTable", $args, function(newTableId) {
			$("#add-category-dialog").remove();
			window.location = "?id=" + newTableId;
		});
	});

	$(".btn-cancel").click(function() {
		$(".ui-dialog").remove();
	});
}

function initFillDialog() {
	initDialog();
	$("input.fill-value").focus();

	$("#dialog-btn-fill").click(function() {
		var $columnNumber = $(this).attr("column-number");
		var $value = $("input.fill-value").val();
		var $columnChanged = false;
		$(".value-row").each(function(i) {
			$(this).find(".value-cell").each(function(j) {
				if (j == $columnNumber) {
					if ($(this).text() != $value) {
						$(this).text($value);
						$(this).addClass("modified-value-cell");
						$columnChanged = true;
					}
				}
			});
		});
		if ($columnChanged) {
			enableSaveButton();
		}
		$(".ui-dialog").remove();
	});

	$(".btn-cancel").click(function() {
		$(".ui-dialog").remove();
	});
}

function initAddCategoryDialog() {
	initDialog();
	$("input.category-name").focus();

	$("#dialog-btn-add-category").click(function() {
		$.post("../AddCategory", {
			tabletype : tableType,
			product : productId,
			name : $("input.category-name").val()
		}, function(data) {
			if (data == "success") {
				$("#add-category-dialog").remove();
				location.reload(true);
			} else {
				alert(data);
			}
		});
	});

	$(".btn-cancel").click(function() {
		$(".ui-dialog").remove();
	});
}

function initEditCategoryDialog() {
	initDialog();
	$("input.category-name").focus();

	$("#dialog-btn-edit-category").click(function() {
		var $id = $(this).attr("category-id");
		$.post("../UpdateCategory", {
			id : $id,
			name : $("input.category-name").val()
		}, function(data) {
			if (data == "success") {
				$("#edit-category-dialog").remove();
				location.reload(true);
			} else {
				alert(data);
			}
		});
	});

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
		$(".sheet-tab").click(function() {
			$(".sheet-tab-selected").removeClass("sheet-tab-selected");
			$(this).addClass("sheet-tab-selected");
			tableId = $(this).attr('id');
			tableType = $(this).attr('label');
			history.pushState({
				id : tableId
			}, "", "?id=" + tableId);
			loadTopPanel({
				tabletype : tableType,
				tableid : tableId
			});
			loadTableValues(tableId);
		});
	}

	$("#cbx-sort-keys").click(function() {
		if ($(this).is("input:checked")) {
			$("#btn-sort-keys").removeClass("button-disabled");
			$("#btn-sort-keys").addClass("checkbox-checked");
			$(".key-cell").destroyContextMenu();

			$(".key-row").sortable({
				cursor : "move",
				delay : 50,
				items : "> .key-cell",
				forcePlaceholderSize : true,
				containment : "parent",
				axis : "x",
				update : function(event, ui) {
					$("#btn-sort-keys").removeClass("checkbox-checked");
					$("#btn-sort-keys").addClass("button-disabled");
					$('#cbx-sort-keys').attr("checked", false);
					var modifiedKeyIds = [];
					var modifiedKeyNumbers = [];
					$(".key-cell").each(function(i) {
						modifiedKeyIds[i] = $(this).attr('id');
						modifiedKeyNumbers[i] = i + 1;
					});
					$.post("../SaveTable", {
						modkeyids : modifiedKeyIds,
						modkeynumbers : modifiedKeyNumbers
					}, function(data) {
						if (data == "success") {
							loadTableValues(tableId);
						} else {
							alert(data);
						}
					});
				}

			});

		} else {
			$("#btn-sort-keys").removeClass("checkbox-checked");
			$("#btn-sort-keys").addClass("button-disabled");
			enableKeyContextMenu(jQuery);
		}
	});

	$("#btn-add-preconditions").click(function() {
		$.post("../AddTable", {
			parentid : tableId,
			tabletype : "precondition"
		}, function(newTableId) {
			window.location = "?id=" + newTableId;
		});
	});

	$("#btn-add-postconditions").click(function() {
		$.post("../AddTable", {
			parentid : tableId,
			tabletype : "postcondition"
		}, function(newTableId) {
			window.location = "?id=" + newTableId;
		});
	});

	$("#btn-delete-data-item").click(function() {
		if ($(this).hasClass("button-enabled")) {
			var answer = confirm("Are you sure you want to delete this " + tableType + "?");
			if (answer) {
				$.post("../DeleteTable", {
					id : tableId
				}, function(data) {
					if (data == "success") {
						$("#section-name").click();
					} else {
						window.location = "?id=" + data;
					}
				});
			}
		}
	});

	$("#btn-save-data-item").click(function() {
		if ($(this).hasClass("button-enabled")) {
			$(".data-item-selected > span.changed-sign").remove();
			$(this).removeClass("button-enabled");
			$(this).addClass("button-disabled");
			$("#waiting").addClass("loading");
			var modifiedIds = [];
			var modifiedValues = [];
			$(".modified-value-cell").each(function(i) {
				if ($(this).has("span")) {
					$(this).find("span").remove();
				}
				if ($(this).has("div.tooltip")) {
					$(this).find("div.tooltip").remove();
				}
				modifiedIds[i] = $(this).attr('id');
				modifiedValues[i] = $(this).text();
			});
			var modifiedKeyIds = [];
			var modifiedKeyValues = [];
			$(".modified-key-cell").each(function(i) {
				if ($(this).has("span")) {
					$(this).find("span").remove();
				}
				if ($(this).has("div.tooltip")) {
					$(this).find("div.tooltip").remove();
				}
				modifiedKeyIds[i] = $(this).attr('id');
				modifiedKeyValues[i] = $(this).text();
			});
			$.post("../SaveTable", {
				ids : modifiedIds,
				values : modifiedValues,
				keyids : modifiedKeyIds,
				keyvalues : modifiedKeyValues,
			}, function(data) {
				if (data == "success") {
					loadTableValues(tableId);
				} else {
					alert(data);
				}
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
		if ($(this).hasClass("button-enabled")) {
			$.post("../GetGeneratedClassDialog", {
				id : tableId
			}, function(data) {
				$("body").append(data);
				initGeneratedClassDialog(jQuery);
			});
		}
	});

}

function initEditDataItemDialog() {
	initDialog();
	$("input.data-item-name").focus();

	$("#dialog-btn-edit-data-item").click(function() {

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
				alert(data);
			}
		});
	});

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

function loadTableValues(id) {
	$.post("../GetTableValues", {
		id : id
	}, function(data) {
		$(".entities-values").html(data);
		initTableValues(jQuery);
	});
}

function initTableValues() {

	$("#waiting").removeClass("loading");
	loadFooter();

	$("html").click(function() {
		if ($(".selected-cell").length > 0) {
			$(".selected-cell").removeClass("selected-cell");
		}
	});

	$(".entities-values").sortable({
		cursor : "move",
		delay : 50,
		items : "> .value-row",
		forcePlaceholderSize : true,
		update : function(event, ui) {
			var modifiedRowIds = [];
			var modifiedRowNumbers = [];
			$(".index-cell").each(function(i) {
				modifiedRowIds[i] = $(this).attr('id');
				modifiedRowNumbers[i] = $(this).text();
			});
			$.post("../SaveTable", {
				rowids : modifiedRowIds,
				rownumbers : modifiedRowNumbers
			}, function(data) {
				if (data == "success") {
					loadTableValues(tableId);
				} else {
					alert(data);
				}
			});
		}
	});

	$(".key-cell").dblclick(function() {
		var $key = $(this);
		if ($key.has("span")) {
			$key.find("span").remove();
		}
		if ($key.has("div.tooltip")) {
			$key.find("div.tooltip").remove();
		}
		var $content = $key.text();
		var $args = {
			keyid : $key.attr('id'),
			content : $content
		};
		$.post("../GetParameterTypeDialog", $args, function(data) {
			$key.html(data);
			$key.find("input.changed-value").focus();
			initEditableKeyCell(jQuery);
		});
	});

	$(".storage-cell:not(.modified-value-cell)").hover(function() {
		var $value = $(this);
		if (($value.has("div.tooltip").length == 0) && ($value.has("span.old-value").length == 0)) {
			$("#waiting").addClass("loading");
			$content = $value.text();
			var $args = {
				id : $value.attr('id'),
				content : $content
			};
			$.post("../GetStorageTooltip", $args, function(data) {
				$("#waiting").removeClass("loading");
				var $widthRight = $(document).width() - $value.position().left - 35;
				// 35 - is width of scroll bar.
				$value.html(data);
				var $tooltip = $value.find("div.tooltip");
				var $tooltipWidth = $tooltip.width();
				if ($widthRight < $tooltipWidth) {
					$tooltip.css("margin-left", "-" + ($tooltipWidth - $widthRight) + "px");
				}
			});
		}
	});

	$(".value-cell:not(:has(> input.changed-value))").dblclick(
			function() {
				var $cell = $(this);
				$cell.removeClass("selected-cell");
				if ($cell.has("span")) {
					$cell.find("span").remove();
				}
				if ($cell.has("div.tooltip")) {
					$cell.find("div.tooltip").remove();
				}
				var $content = $cell.text();
				$cell.html("<input class='changed-value' value='" + $content
						+ "' /><span class='old-value' style='display: none;'>" + $content + "</span>");
				$cell.find("input.changed-value").focus();
				initEditableCell(jQuery);
			});

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

	$(".index-cell").contextMenu({
		menu : "rowMenu"
	}, function(action, el, pos) {
		var $rowId = $(el).attr("id");
		if (action == "add") {
			$.post("../InsertRow", {
				rowid : $rowId
			}, function(data) {
				if (data == "success") {
					loadTableValues(tableId);
				} else {
					alert(data);
				}
			});
		} else if (action == "copy") {
			$.post("../CopyRow", {
				rowid : $rowId
			}, function(data) {
				if (data == "success") {
					loadTableValues(tableId);
				} else {
					alert(data);
				}
			});
		} else if (action == "delete") {
			$.post("../DeleteRow", {
				rowid : $rowId
			}, function(data) {
				if (data == "success") {
					loadTableValues(tableId);
				} else {
					alert(data);
				}
			});
		}
	});

	$("#rowMenu").enableContextMenuItems("#add,#copy,#delete");

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
						if (action == "add") {
							$.post("../InsertKey", {
								keyid : $keyId
							}, function(data) {
								if (data == "success") {
									loadTableValues(tableId);
								} else {
									alert(data);
								}
							});
						} else if (action == "copy") {
							$.post("../CopyKey", {
								keyid : $keyId,
							}, function(data) {
								if (data == "success") {
									loadTableValues(tableId);
								} else {
									alert(data);
								}
							});
						} else if (action == "delete") {
							$.post("../DeleteKey", {
								keyid : $keyId,
							}, function(data) {
								if (data == "success") {
									loadTableValues(tableId);
								} else {
									alert(data);
								}
							});
						} else if (action == "fill") {
							var $columnNumber = -1;
							$(".key-cell").each(function(i) {
								if ($(this).attr('id') == $keyId) {
									$columnNumber = i;
								}
							});
							$("body")
									.append(
											'<div id="fill-dialog" class="ui-dialog">'
													+ '<div class="ui-dialog-title">Fill column with value</div>'
													+ '<div class="ui-dialog-content">'
													+ '<div class="table">'
													+ '<div class="table-row"><div class="table-cell dialog-cell">'
													+ 'Value: </div><div class="table-cell dialog-cell"><input class="fill-value dialog-edit"></div>'
													+ '</div>'
													+ '</div>'
													+ '<div class="dialog-buttons right">'
													+ '<button id="dialog-btn-fill" column-number="'
													+ $columnNumber
													+ '" class="ui-button">Fill</button> <button class="ui-button btn-cancel">Cancel</button>'
													+ '</div></div></div>');
							initFillDialog(jQuery);
						}
					});

	if ((tableType == "table") || (tableType == "storage")) {
		$("#keyMenu").enableContextMenuItems("#add,#copy,#fill,#delete");
	} else {
		$("#keyMenu").enableContextMenuItems("#add,#delete");
		$("#keyMenu").disableContextMenuItems("#copy,#fill");
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

	$("input[value='cbx-storage']").click(function() {
		$(".select-storage").prop("disabled", false);
	});

	$("input[value='cbx-text']").click(function() {
		$(".select-storage").prop("disabled", true);
	});

	$('html').click(function() {
		$('.parameter-type-dialog').css("display", "none");
	});

	$('.parameter-type-dialog').click(function(event) {
		event.stopPropagation();
	});

	$(".btn-apply-type").click(function() {
		var $id = $(this).parent().parent().attr('id');
		var $dialog = $(this).parent();
		var $type = $dialog.find("input:checked").val();
		var $storageId = $dialog.find("option:selected").val();
		$.post("../ApplyParameterType", {
			keyId : $id,
			type : $type,
			storageId : $storageId
		}, function(data) {
			if (data == "success") {
				alert("New type was successfully applied.");
				loadTableValues(tableId);
			} else if (data == "not-changed") {
				alert("This parameter is already has that type.");
			} else {
				alert(data);
			}
		});
	});
}

function modifyKeyCell() {
	var $cell = $(".key-cell").has("input");
	var $oldContent = $cell.find("span.old-value").text();
	var $newContent = $cell.find("input.changed-value").val();

	if ($oldContent != $newContent) {
		enableSaveButton();
		$cell.addClass("modified-key-cell");
	}
	$cell.html($newContent);
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

function modifyValueCell() {
	var $cell = $(".value-cell").has("input");
	var $oldContent = $cell.find("span.old-value").text();
	var $newContent = $cell.find("input.changed-value").val();
	if ($oldContent != $newContent) {
		enableSaveButton();
		$cell.removeClass("selected-cell");
		$cell.addClass("modified-value-cell");
	}
	$cell.html($newContent);
}

function enableSaveButton() {
	$(".data-item-selected:not(:has(> span.changed-sign))").append(" <span class='changed-sign'>changed</span>");
	$("#btn-save-data-item").removeClass("button-disabled");
	$("#btn-save-data-item").addClass("button-enabled");
	$("#rowMenu").disableContextMenuItems("#add,#copy,#delete");
	$("#keyMenu").disableContextMenuItems("#add,#copy,#delete");
}
