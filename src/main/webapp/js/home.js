$(window).on(
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

			$("#btn-add-product").click(
					function() {
						var productPath = "";
						if ($("#lbl-user").length == 0) {
							productPath = '<div class="table-row"><div class="table-cell dialog-cell dialog-label">' + lang.path
									+ ':</div><div class="table-cell dialog-cell dialog-edit">' + '<input class="product-path dialog-edit" size="50"></div></div>';
						}
						$("body").append(
								'<div id="add-product-dialog" class="ui-dialog">' + '<div class="ui-dialog-title">' + lang.addproduct + '</div>'
										+ '<div class="ui-dialog-content">' + '<div class="table">' + '<div class="table-row"><div class="table-cell dialog-cell dialog-label">'
										+ lang.name + ':</div><div class="table-cell dialog-cell dialog-edit">' + '<input class="product-name dialog-edit" size="50"></div>'
										+ '</div>' + productPath + '</div>' + '<div class="dialog-buttons right">' + '<button id="dialog-btn-add-product" class="ui-button">'
										+ lang.add + '</button> <button class="ui-button btn-cancel">' + lang.cancel + '</button>' + '</div></div></div>');
						initAddProductDialog(jQuery);
					});

			$("#lnk-product-info").click(
					function(e) {
						e.preventDefault();
						$("#product-info-dialog").remove();
						$("body").append(
								'<div id="product-info-dialog" class="ui-dialog"><div class="ui-dialog-title">' + lang.product + '</div><div class="ui-dialog-content scrollable">'
										+ '<p><strong>' + lang.product + '</strong> ' + lang.productdefinition + '</p>' + '<p><strong>' + lang.product + '</strong> '
										+ lang.properties + ':' + '<ul><li><strong>' + lang.name + '</strong> - ' + lang.namepropdesr + '</li><br>' + '<li><strong>' + lang.injson
										+ ' ' + lang.path + '</strong> - '+lang.pathpropdescr + '</li>' + '</ul>' + '</p>' + '<div class="dialog-buttons right">'
										+ '<button class="ui-button btn-cancel">'+lang.close+'</button>' + '</div></div></div>');
						initOneButtonDialog(jQuery);
					});

			$.contextMenu({
				selector : ".product-item",
				items : {
					edit : {
						name : lang.editproduct,
						icon : "edit",
						callback : function() {
							var $id = $(this).attr("id");
							$.post("GetEditProductDialog", {
								id : $id
							}, function(data) {
								$("body").append(data);
								initEditProductDialog(jQuery);
							});
						}
					},
					"delete" : {
						name : lang.deleteproduct,
						icon : "delete",
						callback : function() {
							var $productItem = $(this);
							var $id = $productItem.attr("id");
							var message;
							if (isJson()) {
								message = lang.suredelproduct + "<br>" + lang.prodnotdeleted;
							} else {
								message = lang.suredelproduct;
							}
							noty({
								type : "confirm",
								text : message,
								buttons : [ {
									addClass : 'btn btn-primary ui-button',
									text : lang.del,
									onClick : function($noty) {
										$noty.close();
										$.post("DeleteProduct", {
											id : $id
										}, function(data) {
											if (data == "success") {
												noty({
													type : "success",
													text : lang.proddeleted,
													timeout : 3000
												});
												$productItem.parents(".table-row").remove();
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
									text : lang.cancel,
									onClick : function($noty) {
										$noty.close();
									}
								} ]
							});
						}
					}
				}
			});

			$(".section").each(function(i) {
				if ($(this).width() < 250) {
					$(this).css("padding-right", (250 - $(this).width()) + "px");
				}
			});

			for (var i = 0; i < productsWhosePathsNotExist.length; i++) {
				noty({
					type : "warning",
					text : lang.dirnotexist1 + " '" + productsWhosePathsNotExist[i] + "' " + lang.dirnotexist2
				});
			}

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
					if (isJson()) {
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
						} else if (data == "folder-not-exists") {
							noty({
								type : "confirm",
								text : lang.dir + " '" + $("input.product-path").val() + "' " + lang.confcreatedir,
								buttons : [ {
									addClass : 'btn btn-primary ui-button',
									text : lang.createdir,
									onClick : function($noty) {
										$noty.close();
										$.post("CreateDirectory", {
											path : $("input.product-path").val()
										}, function(data) {
											if (data == "success") {
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
									text : lang.cancel,
									onClick : function($noty) {
										$noty.close();
									}
								} ]
							});
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

			function initOneButtonDialog() {
				initDialog();
				$(".btn-cancel").click(function() {
					$(".ui-dialog").remove();
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
					if (isJson()) {
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

function isJson() {
	return appType == "json";
}
