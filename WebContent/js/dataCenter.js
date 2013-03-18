$().ready(initialize());

function initialize() {

	$(document).ajaxError(
			function(e, xhr, settings, exception) {
				var exrrorText = xhr.responseText.substring(xhr.responseText.indexOf("<h1>"));
				$("body").append(
						'<div id="error-dialog" class="ui-dialog">' + '<div class="ui-dialog-title">Error</div>'
								+ '<div class="ui-dialog-content">' + 'Location: ' + settings.url + '<br><br>'
								+ exrrorText + '<br><br>' + '<div class="right">'
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
				'<div class="under-sections"><span class="top-panel-button button-enabled"'
						+ 'id="btn-add-category"><img src="../img/add-icon.png"'
						+ 'class="top-panel-icon">&nbsp;&nbsp;Add category</span></div>');

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
		history.pushState({
			product : productId
		}, "", "?product=" + productId);
	});

	$(".data-item").click(function() {
		$("#waiting").addClass("loading");
		$(".data-item-selected").find(".changed-sign").remove();
		$(".data-item-selected").removeClass("data-item-selected");
		$("#btn-edit-data-item").removeClass("button-disabled");
		$("#btn-edit-data-item").addClass("button-enabled");
		$(this).addClass("data-item-selected");
		tableId = $(this).attr('id');
		if ((tableType == "precondition") || (tableType == "postcondition")) {
			tableType = "table";
		}
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
							$className = '<div class="table-row"><div class="table-cell dialog-cell dialog-label">'
									+ 'Class name:</div><div class="table-cell dialog-cell dialog-edit"><input class="data-storage-class-name dialog-edit"></div>'
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
													+ '<div class="table-row"><div class="table-cell dialog-cell dialog-label">'
													+ 'Name:</div><div class="table-cell dialog-cell dialog-edit"><input class="data-item-name dialog-edit"></div>'
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
													+ '<div class="table-row"><div class="table-cell dialog-cell dialog-label">'
													+ 'Name:</div><div class="table-cell dialog-cell dialog-edit"><input class="category-name dialog-edit" value="'
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
												+ '<div class="table-row"><div class="table-cell dialog-cell dialog-label">'
												+ 'Name:</div><div class="table-cell dialog-cell dialog-edit"><input class="category-name dialog-edit"></div>'
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
	loadFooter();
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
				alert(data);
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
		$(".sheet-tab").click(function() {
			$(".data-item-selected > .changed-sign").remove();
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

	$("#cbx-sort-keys").click(
			function() {
				if ($(this).is("input:checked")) {
					$("#btn-sort-keys").removeClass("button-disabled");
					$("#btn-sort-keys").addClass("checkbox-checked");
					$(".key-cell").destroyContextMenu();

					$(".key-row").sortable(
							{
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
									var keyIds = [];
									var newOrder = [];
									var oldOrder = [];
									var modifiedStart = -1;
									$(".ui-cell.key-cell").each(function(i) {
										if ($(this).attr('key-order') != (i + 1)) {
											if (modifiedStart == -1) {
												modifiedStart = i;
											}
											keyIds[i - modifiedStart] = $(this).attr('id');
											newOrder[i - modifiedStart] = i + 1;
											oldOrder[i - modifiedStart] = $(this).attr('key-order');
										}
									});
									$.post("../UpdateKeysOrder", {
										modkeyids : keyIds,
										modkeynumbers : newOrder
									}, function(data) {
										if (data == "success") {
											$(".key-cell").each(function(i) {
												$(this).attr("key-order", (i + 1));
											});
											$(".value-row").each(
													function(i) {
														var sortedCells = $(this).find(".ui-cell.value-cell").sort(
																function(a, b) {
																	var contentA = parseInt($(
																			".key-cell[id='" + $(a).attr('keyid')
																					+ "']").attr('key-order'));
																	var contentB = parseInt($(
																			".key-cell[id='" + $(b).attr('keyid')
																					+ "']").attr('key-order'));
																	return (contentA < contentB) ? -1
																			: (contentA > contentB) ? 1 : 0;
																});
														$(this).find(".value-cell").remove();
														$(this).append(sortedCells);
													});
											initTableValues(jQuery);
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
			$(".data-item-selected > .changed-sign").remove();
			$(this).removeClass("button-enabled");
			$(this).addClass("button-disabled");
			$(".modified-value-cell").each(function(i) {
				var $cell = $(this);
				if ($cell.has("span")) {
					$cell.find("span").remove();
				}
				if ($cell.has("div.tooltip")) {
					$cell.find("div.tooltip").remove();
				}
				$.post("../SaveCellValue", {
					id : $cell.attr('id'),
					value : $cell.text()
				}, function(data) {
					if (data == "success") {
						$cell.removeClass("modified-value-cell");
					} else {
						alert(data);
					}
				});
			});
			$(".modified-key-cell").each(function(i) {
				var $key = $(this);
				if ($key.has("span")) {
					$key.find("span").remove();
				}
				if ($key.has("div.tooltip")) {
					$key.find("div.tooltip").remove();
				}
				$.post("../SaveKeyValue", {
					id : $key.attr('id'),
					value : $key.text()
				}, function(data) {
					if (data == "success") {
						$key.removeClass("modified-key-cell");
					} else {
						alert(data);
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
				alert(data);
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
	}, 1000);
}

function loadTableValues(id) {
	$.post("../GetTableValues", {
		id : id
	}, function(data) {
		$(".entities-values").html(data);
		initTableValues(jQuery);
		initKeysAndIndexes(jQuery);
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
			var rowIds = [];
			var oldOrder = [];
			var newOrder = [];
			var modifiedStart = -1;
			$(".ui-cell.index-cell").each(function(i) {
				if ($(this).text() != (i + 1)) {
					if (modifiedStart == -1) {
						modifiedStart = i;
					}
					rowIds[i - modifiedStart] = $(this).attr('id');
					oldOrder[i - modifiedStart] = $(this).text();
					newOrder[i - modifiedStart] = i + 1;
				}
			});
			$.post("../UpdateRowsOrder", {
				rowids : rowIds,
				oldorder : oldOrder,
				neworder : newOrder
			}, function(data) {
				if (data == "success") {
					for ( var j = 0; j < rowIds.length; j++) {
						var modifiedIndexCell = $(".ui-cell.index-cell[id='" + rowIds[j] + "']");
						highlight(modifiedIndexCell);
						modifiedIndexCell.text(j + modifiedStart + 1);
					}
				} else {
					alert(data);
				}
			});
		}
	});

	initTooltipCells($(".storage-cell"));

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
				var $width = $cell.width();
				$cell.html("<input class='changed-value' value='" + $content
						+ "' /><span class='old-value' style='display: none;'>" + $content + "</span>");
				$cell.find("input.changed-value").css("width", $width + "px");
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
}

function isNumber(n) {
	return !isNaN(parseFloat(n)) && isFinite(n);
}

function initKeysAndIndexes() {
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
			var $width = $key.width();
			$key.html(data);
			$key.find("input.changed-value").css("width", $width + "px");
			$key.find("input.changed-value").focus();
			initEditableKeyCell(jQuery);
		});
	});

	$(".ui-cell.index-cell").contextMenu({
		menu : "rowMenu"
	}, function(action, el, pos) {
		var $rowId = $(el).attr("id");
		var $rowOrder = parseInt($(el).text());
		var $row = $(el).parent();
		if (action == "add") {
			$.post("../InsertRow", {
				rowid : $rowId
			}, function(data) {
				var newIds = data.split(";");
				if (newIds.length > 1) {
					$newRow = $row.clone(true);
					$newRow.find(".ui-cell.index-cell").attr("id", newIds[0]);
					$newRow.find(".ui-cell.modified-value-cell").removeClass("modified-value-cell");
					$newRow.find(".ui-cell.value-cell").text("");
					$newRow.find(".ui-cell.storage-cell").text("0");
					$newRow.find(".ui-cell.value-cell").each(function(i) {
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
					alert(data);
				}
			});
		} else if (action == "copy") {
			$.post("../CopyRow", {
				rowid : $rowId
			}, function(data) {
				var newIds = data.split(";");
				if (newIds.length > 1) {
					$newRow = $row.clone(true);
					$newRow.find(".ui-cell.index-cell").attr("id", newIds[0]);
					$newRow.find(".ui-cell.value-cell").each(function(i) {
						$(this).attr("rowid", newIds[0]);
						$(this).attr("id", newIds[i + 1]);
					});
					$newRow.insertAfter($row);
					highlight($newRow);
					$(".ui-cell.index-cell").each(function(i) {
						if ((i + 1) > $rowOrder) {
							$(this).text(i + 1);
						}
					});
				} else {
					alert(data);
				}
			});
		} else if (action == "delete") {
			$.post("../DeleteRow", {
				rowid : $rowId
			}, function(data) {
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
						var $keyOrder = $(el).attr("key-order");
						var $column = $("div[keyid='" + $keyId + "']");
						if (action == "add") {
							$.post("../InsertKey", {
								keyid : $keyId
							}, function(data) {
								var newIds = data.split(";");
								if (newIds.length > 1) {
									$newKey = $(el).clone(true);
									$newKey.attr("id", newIds[0]);
									$newKey.text("editme");
									$newKey.insertBefore($(el));
									highlight($newKey);

									$column.each(function(i) {
										$newCell = $(this).clone(true);
										$newCell.removeClass("modified-value-cell");
										if ($newCell.hasClass("storage-cell")) {
											$newCell.text("0");
										} else {
											$newCell.text("");
										}
										$newCell.attr("keyid", newIds[0]);
										$newCell.attr("id", newIds[i + 1]);
										$newCell.insertBefore($(this));
										highlight($newCell);
									});
									$(".ui-cell.key-cell").each(function(i) {
										if ((i + 1) >= $keyOrder) {
											$(this).attr("key-order", (i + 1));
										}
									});
								} else {
									alert(data);
								}
							});
						} else if (action == "copy") {
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
										$newCell.insertAfter($(this));
										highlight($newCell);
									});
									$(".ui-cell.key-cell").each(function(i) {
										if ((i + 1) > $keyOrder) {
											$(this).attr("key-order", (i + 1));
										}
									});
								} else {
									alert(data);
								}
							});
						} else if (action == "delete") {
							$.post("../DeleteKey", {
								keyid : $keyId,
							}, function(data) {
								if (data == "success") {
									$(el).hide(400);
									$column.hide(400, function() {
										$(el).remove();
										$column.remove();
										$(".ui-cell.key-cell").each(function(i) {
											if ((i + 1) >= $keyOrder) {
												$(this).attr("key-order", (i + 1));
											}
										});
									});
								} else {
									alert(data);
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
						}
					});

	if ((tableType == "table") || (tableType == "storage")) {
		$("#keyMenu").enableContextMenuItems("#add,#copy,#fill,#delete");
	} else {
		$("#keyMenu").enableContextMenuItems("#add,#delete");
		$("#keyMenu").disableContextMenuItems("#copy,#fill");
	}
}

function initTooltipCells(elements) {
	elements.hover(function() {
		var $value = $(this);
		if (($value.has("div.tooltip").length == 0) && ($value.has("span.old-value").length == 0)
				&& ($value.text() != "0") && (!$value.hasClass("modified-value-cell"))) {
			$("#waiting").addClass("loading");
			var $content = $value.text();
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
				var $column = $("div[keyid='" + $id + "']");
				if ($type == "cbx-text") {
					$column.find("div.tooltip").remove();
					$column.off();
					$column.removeClass("storage-cell");
					modifyKeyCell();
				} else {
					$column.addClass("storage-cell");
					initTooltipCells($column);
					modifyKeyCell();
				}
			} else if (data == "not-changed") {
				alert("This parameter is already has this type.");
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
	$(".data-item-selected:not(:has(> img.changed-sign))").append(
			" <img class='changed-sign' src='../img/modified.png'>");
	$("#btn-save-data-item").removeClass("button-disabled");
	$("#btn-save-data-item").addClass("button-enabled");
}
