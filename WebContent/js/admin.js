$()
		.ready(
				function() {

					$(document).ajaxError(
							function(e, xhr, settings, exception) {
								$("body").append(
										'<div id="error-dialog" class="ui-dialog">'
												+ '<div class="ui-dialog-title">Error</div>'
												+ '<div class="ui-dialog-content">' + 'Location: ' + settings.url
												+ '<br><br>' + xhr.responseText + '<br><br>' + '<div class="right">'
												+ '<button class="ui-button btn-cancel">OK</button>'
												+ '</div></div></div>');
								initOneButtonDialog(jQuery);
							});

					$("#add-user").click(function() {
						var isFormCorrect = true;

						if ($("input.username").val() == "") {
							alert("ERROR: User name cannot be empty.");
							isFormCorrect = false;
						}

						if ($("input.pass").val() == "") {
							alert("ERROR: Password cannot be empty.");
							isFormCorrect = false;
						}

						if ($("input.pass").val() != $("input.retype-pass").val()) {
							alert("ERROR: Passwords are different.");
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
									alert(data);
								}
							});
						}
					});

					$(".btn-delete-user").click(function() {
						var $userid = $(this).attr("userid");
						var answer = confirm("Are you sure you want to delete this user?");
						if (answer) {
							$.post("../DeleteUser", {
								userid : $userid
							}, function(data) {
								if (data == "success") {
									alert("User was deleted.");									
									location.reload(true);
								} else if (data == "gohome") {
									alert("User was deleted.");
									window.location = "../";
								} else {
									alert(data);
								}
							});
						}
					});

					$(".btn-edit-user")
							.click(
									function() {
										var $userid = $(this).attr("userid");
										var $userName = $("div[userid=\"" + $userid + "\"]").text();
										$("body")
												.append(
														'<div id="edit-user-dialog" class="ui-dialog">'
																+ '<div class="ui-dialog-title">Edit User</div>'
																+ '<div class="ui-dialog-content">'

																+ '<div class="table">'
																+ '<div class="table-row">'
																+ '<div class="table-cell">User Name:</div>'
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
																+ '<button class="ui-button btn-cancel">Cancel</button>'
																+ '</div></div></div>');
										initEditUserDialog(jQuery);
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
			alert("ERROR: User name cannot be empty.");
			isFormCorrect = false;
		}

		if ($("div#edit-user-dialog input.pass").val() != $("div#edit-user-dialog input.retype-pass").val()) {
			alert("ERROR: Passwords are different.");
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
					alert(data);
				}
			});
		}
	});

	$(".btn-cancel").click(function() {
		$(".ui-dialog").remove();
	});
}
