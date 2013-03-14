$(window)
		.on(
				'load',
				function() {
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

					$("#btn-add-product")
							.click(
									function() {
										$("body")
												.append(
														'<div id="add-product-dialog" class="ui-dialog">'
																+ '<div class="ui-dialog-title">Add product</div>'
																+ '<div class="ui-dialog-content">'
																+ '<div class="table">'
																+ '<div class="table-row"><div class="table-cell dialog-cell dialog-label">'
																+ 'Name:</div><div class="table-cell dialog-cell dialog-edit">'
																+ '<input class="product-name dialog-edit"></div>'
																+ '</div>'
																+ '</div>'
																+ '<div class="dialog-buttons right">'
																+ '<button id="dialog-btn-add-product" class="ui-button">Add</button> <button class="ui-button btn-cancel">Cancel</button>'
																+ '</div></div></div>');
										initAddProductDialog(jQuery);
									});

					$(".product-item")
							.contextMenu(
									{
										menu : 'productMenu'
									},
									function(action, el, pos) {
										var $id = $(el).attr("id");
										if (action == "edit") {
											$("body")
													.append(
															'<div id="edit-product-dialog" class="ui-dialog">'
																	+ '<div class="ui-dialog-title">Edit product</div>'
																	+ '<div class="ui-dialog-content">'
																	+ '<div class="table">'
																	+ '<div class="table-row"><div class="table-cell dialog-cell dialog-label">'
																	+ 'Name:</div><div class="table-cell dialog-cell dialog-edit"><input class="product-name dialog-edit" value="'
																	+ $(el).text()
																	+ '"></div>'
																	+ '</div>'
																	+ '</div>'
																	+ '<div class="dialog-buttons right">'
																	+ '<button id="dialog-btn-edit-product" product-id="'
																	+ $id
																	+ '" class="ui-button">Save</button> <button class="ui-button btn-cancel">Cancel</button>'
																	+ '</div></div></div>');
											initEditProductDialog(jQuery);
										} else if (action == "delete") {
											var answer = confirm("Are you sure you want to delete this product?");
											if (answer) {
												$.post("DeleteProduct", {
													id : $id
												}, function(data) {
													if (data == "success") {
														alert("Product was deleted.");
														location.reload(true);
													} else {
														alert(data);
													}
												});
											}
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

						function submitAddProduct() {
							$.post("AddProduct", {
								name : $("input.product-name").val()
							}, function(data) {
								if (data == "success") {
									$("#add-product-dialog").remove();
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

					function initDialog() {
						var $dialog = $(".ui-dialog");
						var $posTop = ($(window).height() - $dialog.height()) / 2;
						var $posLeft = ($(window).width() - $dialog.width()) / 2;
						$dialog.css("top", $posTop);
						$dialog.css("left", $posLeft);
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

						function submitEditProduct() {
							var $id = $("#dialog-btn-edit-product").attr("product-id");
							$.post("UpdateProduct", {
								id : $id,
								name : $("input.product-name").val()
							}, function(data) {
								if (data == "success") {
									$("#edit-product-dialog").remove();
									location.reload(true);
								} else {
									alert(data);
								}
							});
						}

						$(".btn-cancel").click(function() {
							$("#edit-product-dialog").remove();
						});
					}
				});
