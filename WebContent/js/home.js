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
																+ '<div class="table-row"><div class="table-cell dialog-cell">'
																+ 'Name:</div><div class="table-cell dialog-cell">'
																+ '<input class="product-name dialog-edit"></div>'
																+ '</div>'
																+ '</div>'
																+ '<div class="dialog-buttons right">'
																+ '<button id="dialog-btn-add-product" class="ui-button">Add</button> <button class="ui-button btn-cancel">Cancel</button>'
																+ '</div></div></div>');
										initAddProductDialog(jQuery);
									});

					function initAddProductDialog() {
						initDialog();
						$("input.product-name").focus();

						$("#dialog-btn-add-product").click(function() {
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
						});

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
				});
