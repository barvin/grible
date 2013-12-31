$(window)
		.on(
				'load',
				function() {
					$("#waiting-bg").removeClass("loading");

					var $loginForm = $("#login-form");
					if ($loginForm.height() < $(window).height()) {
						var $posTop = $(window).height() * 0.2;
						$loginForm.css("top", $posTop);
					}
					if ($loginForm.width() < $(window).width()) {
						var $posLeft = ($(window).width() - $loginForm.width()) / 2;
						$loginForm.css("left", $posLeft);
					}

					$(".dialog-error-message").fadeIn(400);

					$("#video-tutorial-msg").css("bottom", $("#footer").height() + 25);
					$("#video-tutorial-msg").slideDown(600);

					$("#btn-add-product")
							.click(
									function() {
										var productPath = "";
										if ($("#lbl-user").length == 0) {
											productPath = '<div class="table-row"><div class="table-cell dialog-cell dialog-label">'
													+ 'Path:</div><div class="table-cell dialog-cell dialog-edit">'
													+ '<input class="product-path dialog-edit" size="50"></div></div>';
										}
										$("body")
												.append(
														'<div id="add-product-dialog" class="ui-dialog">'
																+ '<div class="ui-dialog-title">Add product</div>'
																+ '<div class="ui-dialog-content">'
																+ '<div class="table">'
																+ '<div class="table-row"><div class="table-cell dialog-cell dialog-label">'
																+ 'Name:</div><div class="table-cell dialog-cell dialog-edit">'
																+ '<input class="product-name dialog-edit" size="50"></div>'
																+ '</div>'
																+ productPath
																+ '</div>'
																+ '<div class="dialog-buttons right">'
																+ '<button id="dialog-btn-add-product" class="ui-button">Add</button> <button class="ui-button btn-cancel">Cancel</button>'
																+ '</div></div></div>');
										initAddProductDialog(jQuery);
									});

					$(".product-item").contextMenu({
						menu : 'productMenu'
					}, function(action, el, pos) {
						var $id = $(el).attr("id");
						if (action == "edit") {
							$.post("GetEditProductDialog", {
								id : $id
							}, function(data) {
								$("body").append(data);
								initEditProductDialog(jQuery);
							});
						} else if (action == "delete") {
							noty({
								type : "confirm",
								text : "Are you sure you want to delete this product?",
								buttons : [ {
									addClass : 'btn btn-primary ui-button',
									text : 'Delete',
									onClick : function($noty) {
										$noty.close();
										$.post("DeleteProduct", {
											id : $id
										}, function(data) {
											if (data == "success") {
												noty({
													type : "success",
													text : "The product was deleted.",
													timeout : 5000
												});
												$(el).parents(".table-row").remove();
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

					$(".section").each(function(i) {
						if ($(this).width() < 250) {
							$(this).css("padding-right", (250 - $(this).width()) + "px");
						}
					});

					function initAddProductDialog() {
						initDialog();
						$("input.product-name").focus();

						$("input.product-name").keypress(function(event) {
							if (event.which === 13) {
								submitAddProduct();
							}
						});

						$("#dialog-btn-add-product").click(function() {
							submitAddProduct();
						});

						if ($("#lbl-user").length == 0) {
							$("input.product-path").keypress(function(event) {
								if (event.which === 13) {
									submitAddProduct();
								}
							});
						}

						function submitAddProduct() {
							var args;
							if ($("#lbl-user").length == 0) {
								args = {
									name : $("input.product-name").val(),
									path : $("input.product-path").val()
								};
							} else {
								args = {
									name : $("input.product-name").val()
								};
							}
							$.post("AddProduct", args, function(data) {
								if (data == "success") {
									$("#add-product-dialog").remove();
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

					function initEditProductDialog() {
						initDialog();
						$("input.product-name").focus();

						$("input.product-name").keypress(function(event) {
							if (event.which === 13) {
								submitEditProduct();
							}
						});

						$("#dialog-btn-edit-product").click(function() {
							submitEditProduct();
						});

						if ($("#lbl-user").length == 0) {
							$("input.product-path").keypress(function(event) {
								if (event.which === 13) {
									submitEditProduct();
								}
							});
						}

						function submitEditProduct() {
							var $id = $("#dialog-btn-edit-product").attr("product-id");
							var args;
							if ($("#lbl-user").length == 0) {
								args = {
									id : $id,
									name : $("input.product-name").val(),
									path : $("input.product-path").val()
								};
							} else {
								args = {
									id : $id,
									name : $("input.product-name").val()
								};
							}
							$.post("UpdateProduct", args, function(data) {
								if (data == "success") {
									$("#edit-product-dialog").remove();
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
							$("#edit-product-dialog").remove();
						});
					}
				});
