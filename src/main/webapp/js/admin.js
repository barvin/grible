$().ready(
		function() {

			$("input.isadmin").click(function() {
				if ($(this).is("input:checked")) {
					$("input.access-product").attr("disabled", "disabled");
				} else {
					$("input.access-product").removeAttr("disabled");
				}
			});

			$("#add-user").click(function() {
				var isFormCorrect = true;

				if ($("input.username").val() == "") {
					noty({
						type : "error",
						text : "ERROR: User name cannot be empty."
					});
					isFormCorrect = false;
				}

				if ($("input.pass").val() == "") {
					noty({
						type : "error",
						text : "ERROR: Password cannot be empty."
					});
					isFormCorrect = false;
				}

				if ($("input.pass").val() != $("input.retype-pass").val()) {
					noty({
						type : "error",
						text : "ERROR: Passwords are different."
					});
					isFormCorrect = false;
				}

				if (isFormCorrect) {

					var productIds = [];
					$(".access-product:checked").each(function(i) {
						productIds[i] = $(this).attr("id");
					});

					$args = {
						username : $("input.username").val(),
						pass : $("input.pass").val(),
						isadmin : $("input.isadmin").is(":checked"),
						productIds : productIds
					};
					$.post("../AddUser", $args, function(data) {
						if (data == "success") {
							location.reload(true);
						} else {
							noty({
								type : "error",
								text : data
							});
						}
					});
				}
			});

			$(".btn-delete-user").click(function() {
				var $userid = $(this).attr("userid");
				var $parentRow = $(this).parents(".table-row");
				noty({
					type : "confirm",
					text : "Are you sure you want to delete this user?",
					buttons : [ {
						addClass : 'btn btn-primary ui-button',
						text : 'Delete',
						onClick : function($noty) {
							$noty.close();
							$.post("../DeleteUser", {
								userid : $userid
							}, function(data) {
								if (data == "success") {
									noty({
										type : "success",
										text : "The user was deleted.",
										timeout : 5000
									});
									$parentRow.remove();
								} else if (data == "gohome") {
									noty({
										type : "success",
										text : "The user was deleted.",
										timeout : 5000,
										modal : true,
										callback : {
											afterClose : function() {
												window.location = "../";
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
					}, {
						addClass : 'btn btn-danger ui-button',
						text : 'Cancel',
						onClick : function($noty) {
							$noty.close();
						}
					} ]
				});
			});

			$(".btn-edit-user").click(
					function() {
						var $userid = $(this).attr("userid");
						var $userName = $("div[userid=\"" + $userid + "\"]").text();
						$("body").append(
								'<div id="edit-user-dialog" class="ui-dialog">' + '<div class="ui-dialog-title">Edit User</div>' + '<div class="ui-dialog-content">'

								+ '<div class="table">' + '<div class="table-row">' + '<div class="table-cell">User Name:</div>'
										+ '<div class="table-cell"><input class="username" value="'
										+ $userName
										+ '"></div>'
										+ '</div>'
										+ '<div class="table-row">'
										+ '<div class="table-cell">New password:</div>'
										+ '<div class="table-cell"><input class="pass" type="password" ></div>'
										+ '</div>'
										+ '<div class="table-row">'
										+ '<div class="table-cell">Retype it:</div>'
										+ '<div class="table-cell"><input class="retype-pass" type="password" ></div>'
										+ '</div>'
										+ '<div class="table-row">'
										+ '<div class="table-cell">Is Admin:</div>'
										+ '<div class="table-cell"><input class="isadmin" type="checkbox" ></div>'
										+ '</div>'
										+ '<div class="table-row">'
										+ '<div class="table-cell">Products:</div>'
										+ '<div class="table-cell">'

										+ getProductsCheckboxes()

										+ '</div>'
										+ '</div>'
										+ '</div>'

										+ '<br><div class="right">'
										+ '<button id="'
										+ $userid
										+ '" class="ui-button btn-update-user">Update</button> '
										+ '<button class="ui-button btn-cancel">Cancel</button>' + '</div></div></div>');
						initEditUserDialog(jQuery);
					});

			$("#savedbsettings").click(function() {
				// validate form
				var formvalid = true;
				var errors = "";
				if ($("input[name='dbhost']").val() == "") {
					errors += "<br>ERROR: 'Database host' is empty.";
					formvalid = false;
				}
				if ($("input[name='dbport']").val() == "") {
					errors += "<br>ERROR: 'Database port' is empty.";
					formvalid = false;
				}
				if ($("input[name='dbname']").val() == "") {
					errors += "<br>ERROR: 'Database name' is empty.";
					formvalid = false;
				}
				if ($("input[name='dblogin']").val() == "") {
					errors += "<br>ERROR: 'Database user name' is empty.";
					formvalid = false;
				}
				if ($("input[name='dbpswd']").val() == "") {
					errors += "<br>ERROR: 'Database user password' is empty.";
					formvalid = false;
				}

				if (formvalid) {
					$.post("../DBConnect", {
						dbhost : $("input[name='dbhost']").val(),
						dbport : $("input[name='dbport']").val(),
						dbname : $("input[name='dbname']").val(),
						dblogin : $("input[name='dblogin']").val(),
						dbpswd : $("input[name='dbpswd']").val(),
					}, function(data) {
						if (data == "Done.") {
							$.post("../SaveDBSettings", function(data) {
								if (data == "Done.") {
									noty({
										type : "success",
										text : "Database settings were successfully saved.",
										timeout : 5000
									});
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

				} else {
					noty({
						type : "error",
						text : errors.substring(4)
					});

				}
			});

			$("#btn-apply-updates").click(function() {
				$("#update-result").html("<img src='../img/ajax-loader-small.gif'> Downloading...");
				$.post("../ApplyUpdates", function(data) {
					if (data == "success") {
						$("#update-result").html("<img src='../img/ajax-loader-small.gif'> Installing...");
						(function updater() {
							$.ajax({
								url : '/../',
								success : function(data) {
									window.location = "/../";
								},
								complete : function() {
									// Schedule the next request when the current one's complete
									setTimeout(updater, 5000);
								}
							});
						})();
					} else {
						$("#update-result").html(data);
					}
				});
			});
		});

function initOneButtonDialog() {
	initDialog();
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

function initEditUserDialog() {
	initDialog();
	$(".btn-update-user").click(function() {
		var isFormCorrect = true;

		if ($("div#edit-user-dialog input.username").val() == "") {
			noty({
				type : "error",
				text : "ERROR: User name cannot be empty."
			});
			isFormCorrect = false;
		}

		if ($("div#edit-user-dialog input.pass").val() != $("div#edit-user-dialog input.retype-pass").val()) {
			noty({
				type : "error",
				text : "ERROR: Passwords are different."
			});
			isFormCorrect = false;
		}

		if (isFormCorrect) {

			var productIds = [];
			$("div#edit-user-dialog .access-product:checked").each(function(i) {
				productIds[i] = $(this).attr("id");
			});

			$args = {
				userid : $(".btn-update-user").attr("id"),
				username : $("div#edit-user-dialog input.username").val(),
				pass : $("div#edit-user-dialog input.pass").val(),
				isadmin : $("div#edit-user-dialog input.isadmin").is(":checked"),
				productIds : productIds
			};
			$.post("../UpdateUser", $args, function(data) {
				if (data == "success") {
					location.reload(true);
				} else {
					noty({
						type : "error",
						text : data
					});
				}
			});
		}
	});

	$(".btn-cancel").click(function() {
		$(".ui-dialog").remove();
	});
}
