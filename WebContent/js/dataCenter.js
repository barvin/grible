$().ready(initialize());

function initialize() {

	$(document).ajaxError(
			function(e, xhr, settings, exception) {
				$("body").append(
						'<div id="error-dialog" class="ui-dialog">' + '<div class="ui-dialog-title">Error</div>'
								+ '<div class="ui-dialog-content">' + 'Location: ' + settings.url + '<br><br>'
								+ xhr.responseText + '<br><br>' + '<div class="right">'
								+ '<button class="ui-button btn-cancel">OK</button>' + '</div></div></div>');
				initOneButtonDialog(jQuery);
			});

	$.post("../GetCategories", {
		productId : productId,
		dataTypeId : dataTypeId,
		dataType : dataType
	}, function(data) {
		$("#waiting-bg").remove();
		$("#footer").removeClass("page-bottom");
		$("#category-container").html(data);
		$(".entities-list").append(
				'<span class="top-panel-button button-enabled" id="btn-add-category"><img src="../img/add-icon.png"'
						+ 'class="top-panel-icon"> Add category</span>');

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
		$(this).addClass("data-item-selected");
		var $id = $(this).attr('id');
		history.pushState({
			id : $id
		}, "", "?id=" + $id);
		if (dataType == "storage") {
			loadTopPanel({});
			loadDataStorageTable($id);
		} else {
			loadTopPanel({
				datafileid : $id
			});
			loadDataFileTable($id, "general");
		}
	});

	$(".category-item")
			.contextMenu(
					{
						menu : 'categoryMenu'
					},
					function(action, el, pos) {
						var $id = $(el).attr("id");
						var $className = "";
						if (dataType == "storage") {
							$className = '<div class="table-row"><div class="table-cell dialog-cell">'
									+ 'Class name:</div><div class="table-cell dialog-cell"><input class="data-storage-class-name dialog-edit"></div>'
									+ '</div>';
						}
						if (action == "add") {
							$("body")
									.append(
											'<div id="add-data-'
													+ dataType
													+ '-dialog" class="ui-dialog">'
													+ '<div class="ui-dialog-title">Add data '
													+ dataType
													+ '</div>'
													+ '<div class="ui-dialog-content">'
													+ '<div class="table">'
													+ '<div class="table-row"><div class="table-cell dialog-cell">'
													+ 'Name:</div><div class="table-cell dialog-cell"><input class="data-item-name dialog-edit"></div>'
													+ '</div>'
													+ $className
													+ '</div>'
													+ '<br/>The data '
													+ dataType
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
								var $servlet;
								if (dataType == "storage") {
									$servlet = "../DeleteStorageCategory";
								} else {
									$servlet = "../DeleteCategory";
								}
								$.post($servlet, {
									id : $id
								}, function(data) {
									if (data == "success") {
										alert("Category was deleted.");
										window.location = "../product=" + productId;
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

	if (dataTypeId > 0) {
		$("#waiting").addClass("loading");
		if (dataType == "storage") {
			loadTopPanel({});
			loadDataStorageTable(dataTypeId);
		} else {
			loadTopPanel({
				datafileid : dataTypeId
			});
			loadDataFileTable(dataTypeId, "general");
		}
	}

}

function initAddDataItemDialog() {

	$("input.data-item-name").focus();

	$("#dialog-btn-add-data-item").click(function() {
		var $id = $(this).attr("category-id");
		var $servlet;
		var $args;
		if (dataType == "storage") {
			$servlet = "../AddDataStorage";
			$args = {
				categoryid : $id,
				name : $("input.data-item-name").val(),
				classname : $("input.data-storage-class-name").val()
			};
		} else {
			$servlet = "../AddDataFile";
			$args = {
				categoryid : $id,
				name : $("input.data-item-name").val()
			};
		}
		$.post($servlet, $args, function() {
			$("#add-category-dialog").remove();
			location.reload(true);
		});
	});

	$(".btn-cancel").click(function() {
		$(".ui-dialog").remove();
	});
}

function initFillDialog() {

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

	$("input.category-name").focus();

	$("#dialog-btn-add-category").click(function() {
		var $servlet;
		if (dataType == "storage") {
			$servlet = "../InsertStorageCategory";
		} else {
			$servlet = "../InsertCategory";
		}
		$.post($servlet, {
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

	$("input.category-name").focus();

	$("#dialog-btn-edit-category").click(function() {
		var $id = $(this).attr("category-id");
		var $servlet;
		if (dataType == "storage") {
			$servlet = "../UpdateStorageCategory";
		} else {
			$servlet = "../UpdateCategory";
		}
		$.post($servlet, {
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

	var $id;
	if ($(".data-item-selected").length > 0) {
		$id = $(".data-item-selected").attr("id");
	} else {
		$id = dataTypeId;
	}

	if (dataType == "table") {
		$(".sheet-tab").click(function() {
			$(".sheet-tab-selected").removeClass("sheet-tab-selected");
			$(this).addClass("sheet-tab-selected");
			var $sheet = $(this).attr("label");
			loadDataFileTable($id, $sheet);
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
					if (dataType == "storage") {
						$.post("../SaveStorage", {
							modkeyids : modifiedKeyIds,
							modkeynumbers : modifiedKeyNumbers
						}, function(data) {
							if (data == "success") {
								loadDataStorageTable($id);
							} else {
								alert(data);
							}
						});
					} else {
						$.post("../SaveDataFile", {
							sheet : $(".sheet-tab-selected").attr("label"),
							modkeyids : modifiedKeyIds,
							modkeynumbers : modifiedKeyNumbers
						}, function(data) {
							if (data == "success") {
								loadDataFileTable($id, $(".sheet-tab-selected").attr("label"));
							} else {
								alert(data);
							}
						});
					}
				}

			});

		} else {
			$("#btn-sort-keys").removeClass("checkbox-checked");
			$("#btn-sort-keys").addClass("button-disabled");
			enableKeyContextMenu(jQuery);
		}
	});

	$("#btn-add-preconditions").click(function() {
		$.post("../AddPreconditions", {
			id : $id
		}, function() {
			loadTopPanel({
				datafileid : $id
			});
			$(".sheet-tab[label='preconditions']").click();
		});
	});

	$("#btn-add-postconditions").click(function() {
		$.post("../AddPostconditions", {
			id : $id
		}, function() {
			loadTopPanel({
				datafileid : $id
			});
			$(".sheet-tab[label='postconditions']").click();
		});
	});

	$("#btn-delete-data-item").click(function() {
		if ($(this).hasClass("button-enabled")) {
			var answer = confirm("Are you sure you want to delete this data " + dataType + "?");
			if (answer) {
				var $servlet;
				if (dataType == "storage") {
					$servlet = "../DeleteStorage";
				} else {
					$servlet = "../DeleteDataFile";
				}
				$.post($servlet, {
					id : $id
				}, function(data) {
					if (data == "success") {
						$("#section-name").click();
					} else {
						alert(data);
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
			if (dataType == "storage") {
				$.post("../SaveStorage", {
					ids : modifiedIds,
					values : modifiedValues,
					keyids : modifiedKeyIds,
					keyvalues : modifiedKeyValues,
				}, function(data) {
					if (data == "success") {
						loadDataStorageTable($id);
					} else {
						alert(data);
					}
				});
			} else {
				$.post("../SaveDataFile", {
					sheet : $(".sheet-tab-selected").attr("label"),
					ids : modifiedIds,
					values : modifiedValues,
					keyids : modifiedKeyIds,
					keyvalues : modifiedKeyValues,
				}, function(data) {
					if (data == "success") {
						loadDataFileTable($id, $(".sheet-tab-selected").attr("label"));
					} else {
						alert(data);
					}
				});
			}
		}
	});

	$("#btn-edit-data-item").click(function() {
		if ($(this).hasClass("button-enabled")) {
			var $servlet;
			if (dataType == "storage") {
				$servlet = "../GetEditDataStorageDialog";
			} else {
				$servlet = "../GetEditDataFileDialog";
			}
			$.post($servlet, {
				id : $id
			}, function(data) {
				$("body").append(data);
				initEditDataItemDialog(jQuery);
			});
		}
	});

	$("#btn-class-data-item").click(function() {
		if ($(this).hasClass("button-enabled")) {
			$.post("../GetGeneratedClassDialog", {
				id : $id
			}, function(data) {
				$("body").append(data);
				initGeneratedClassDialog(jQuery);
			});
		}
	});

}

function initEditDataItemDialog() {
	$("input.data-item-name").focus();

	$("#dialog-btn-edit-data-item").click(function() {

		var $id;
		if ($(".data-item-selected").length > 0) {
			$id = $(".data-item-selected").attr("id");
		} else {
			$id = dataTypeId;
		}
		var $categoryid = $("select.categories").find("option:selected").attr('value');
		var $servlet;
		var $args;
		if (dataType == "storage") {
			$servlet = "../EditDataStorage";
			$args = {
				id : $id,
				categoryid : $categoryid,
				usage : $("input.usage").is("input:checked"),
				name : $("input.data-item-name").val(),
				classname : $("input.data-storage-class-name").attr("value")
			};
		} else {
			$servlet = "../EditDataFile";
			$args = {
				id : $id,
				categoryid : $categoryid,
				name : $("input.data-item-name").val()
			};
		}
		$.post($servlet, $args, function(data) {
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

	$(".btn-cancel").click(function() {
		$(".ui-dialog").remove();
	});
}

function loadDataFileTable(id, sheet) {
	if (sheet == "general") {
		$.post("../GetDataFileValues?id=" + id, function(data) {
			$(".entities-values").html(data);
			initValuesTable(jQuery);
		});
	} else if (sheet == "preconditions") {
		$.post("../GetPreconditions?id=" + id, function(data) {
			$(".entities-values").html(data);
			initValuesTable(jQuery);
		});
	} else {
		$.post("../GetPostconditions?id=" + id, function(data) {
			$(".entities-values").html(data);
			initValuesTable(jQuery);
		});
	}
}

function loadDataStorageTable(id) {
	$.post("../GetStorageValues?id=" + id, function(data) {
		$(".entities-values").html(data);
		initValuesTable(jQuery);
	});
}

function initValuesTable() {

	$("#waiting").removeClass("loading");

	var $id;
	if ($(".data-item-selected").length > 0) {
		$id = $(".data-item-selected").attr("id");
	} else {
		$id = dataTypeId;
	}

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
			if (dataType == "storage") {
				$.post("../SaveStorage", {
					rowids : modifiedRowIds,
					rownumbers : modifiedRowNumbers
				}, function(data) {
					if (data == "success") {
						loadDataStorageTable($id);
					} else {
						alert(data);
					}
				});
			} else {
				$.post("../SaveDataFile", {
					sheet : $(".sheet-tab-selected").attr("label"),
					rowids : modifiedRowIds,
					rownumbers : modifiedRowNumbers
				}, function(data) {
					if (data == "success") {
						loadDataFileTable($id, $(".sheet-tab-selected").attr("label"));
					} else {
						alert(data);
					}
				});
			}
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
		var $args;
		if (dataType == "storage") {
			$args = {
				product : productId,
				storageid : $key.attr('id'),
				content : $content
			};
		} else {
			$args = {
				product : productId,
				datafileid : $(this).attr('id'),
				sheet : $(".sheet-tab-selected").attr('label'),
				content : $content
			};
		}
		$.post("../GetParameterTypeDialog", $args, function(data) {
			$key.html(data);
			$key.find("input.changed-value").focus();
			initEditableKeyCell(jQuery);
		});
	});

	$(".storage-cell").hover(function() {
		var $value = $(this);
		if (($value.has("div.tooltip").length == 0) && ($value.has("span.old-value").length == 0)) {
			$("#waiting").addClass("loading");
			$content = $value.text();
			var $args;
			if (dataType == "storage") {
				$args = {
					storagevalueid : $value.attr('id'),
					content : $content
				};
			} else {
				$args = {
					datafilevalueid : $value.attr('id'),
					sheet : $(".sheet-tab-selected").attr('label'),
					content : $content
				};
			}
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
		var $id;
		if ($(".data-item-selected").length > 0) {
			$id = $(".data-item-selected").attr("id");
		} else {
			$id = dataTypeId;
		}
		var $rowId = $(el).attr("id");
		if (action == "add") {
			if (dataType == "storage") {
				$.post("../InsertDataStorageRow", {
					rowid : $rowId
				}, function(data) {
					if (data == "success") {
						loadDataStorageTable($id);
					} else {
						alert(data);
					}
				});
			} else {
				$.post("../InsertDataFileRow", {
					rowid : $rowId
				}, function(data) {
					if (data == "success") {
						loadDataFileTable($id, "general");
					} else {
						alert(data);
					}
				});
			}
		} else if (action == "copy") {
			if (dataType == "storage") {
				$.post("../CopyDataStorageRow", {
					rowid : $rowId
				}, function(data) {
					if (data == "success") {
						loadDataStorageTable($id);
					} else {
						alert(data);
					}
				});
			} else {
				$.post("../CopyDataFileRow", {
					rowid : $rowId
				}, function(data) {
					if (data == "success") {
						loadDataFileTable($id, "general");
					} else {
						alert(data);
					}
				});
			}
		} else if (action == "delete") {
			if (dataType == "storage") {
				$.post("../DeleteDataStorageRow", {
					rowid : $rowId
				}, function(data) {
					if (data == "success") {
						loadDataStorageTable($id);
					} else {
						alert(data);
					}
				});
			} else {
				$.post("../DeleteDataFileRow", {
					rowid : $rowId
				}, function(data) {
					if (data == "success") {
						loadDataFileTable($id, "general");
					} else {
						alert(data);
					}
				});
			}
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
						var $id;
						if ($(".data-item-selected").length > 0) {
							$id = $(".data-item-selected").attr("id");
						} else {
							$id = dataTypeId;
						}
						if (action == "add") {
							if (dataType == "storage") {
								$.post("../InsertDataStorageKey", {
									keyid : $keyId
								}, function(data) {
									if (data == "success") {
										loadDataStorageTable($id);
									} else {
										alert(data);
									}
								});
							} else {
								$.post("../InsertDataFileKey", {
									keyid : $keyId,
									sheet : $(".sheet-tab-selected").attr("label")
								}, function(data) {
									if (data == "success") {
										loadDataFileTable($id, $(".sheet-tab-selected").attr("label"));
									} else {
										alert(data);
									}
								});
							}
						} else if (action == "copy") {
							if (dataType == "storage") {
								$.post("../CopyDataStorageKey", {
									keyid : $keyId,
								}, function(data) {
									if (data == "success") {
										loadDataStorageTable($id);
									} else {
										alert(data);
									}
								});
							} else {
								$.post("../CopyDataFileKey", {
									keyid : $keyId,
									sheet : $(".sheet-tab-selected").attr("label")
								}, function(data) {
									if (data == "success") {
										loadDataFileTable($id, $(".sheet-tab-selected").attr("label"));
									} else {
										alert(data);
									}
								});
							}
						} else if (action == "delete") {
							if (dataType == "storage") {
								$.post("../DeleteDataStorageKey", {
									keyid : $keyId,
								}, function(data) {
									if (data == "success") {
										loadDataStorageTable($id);
									} else {
										alert(data);
									}
								});
							} else {
								$.post("../DeleteDataFileKey", {
									keyid : $keyId,
									sheet : $(".sheet-tab-selected").attr("label")
								}, function(data) {
									if (data == "success") {
										loadDataFileTable($id, $(".sheet-tab-selected").attr("label"));
									} else {
										alert(data);
									}
								});
							}
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

	if (dataType == "table") {
		if (($(".sheet-tab-selected").attr("label") == "preconditions")
				|| ($(".sheet-tab-selected").attr("label") == "postconditions")) {
			$("#keyMenu").enableContextMenuItems("#add,#delete");
			$("#keyMenu").disableContextMenuItems("#copy,#fill");
		} else {
			$("#keyMenu").enableContextMenuItems("#add,#copy,#fill,#delete");
		}
	} else {
		$("#keyMenu").enableContextMenuItems("#add,#copy,#fill,#delete");
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
		var $type = $dialog.find("input:checked").attr('value');
		var $storageId = $dialog.find("option:selected").attr('value');
		var $dataTypeId;
		if ($(".data-item-selected").length > 0) {
			$dataTypeId = $(".data-item-selected").attr("id");
		} else {
			$dataTypeId = dataTypeId;
		}
		if (dataType == "storage") {
			$.post("../ApplyStorageParameterType", {
				keyId : $id,
				type : $type,
				storageId : $storageId
			}, function(data) {
				if (data == "success") {
					alert("New type was successfully applied.");
					loadDataStorageTable($dataTypeId);
				} else if (data == "not-changed") {
					alert("This parameter is already has that type.");
				} else {
					alert(data);
				}
			});
		} else {
			$.post("../ApplyDataFileParameterType", {
				keyId : $id,
				type : $type,
				storageId : $storageId,
				sheet : $(".sheet-tab-selected").attr("label")
			}, function(data) {
				if (data == "success") {
					alert("New type was successfully applied.");
					loadDataFileTable($dataTypeId, $(".sheet-tab-selected").attr('label'));
				} else if (data == "not-changed") {
					alert("This parameter is already has that type.");
				} else {
					alert(data);
				}
			});
		}
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