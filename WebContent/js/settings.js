$().ready(
		function() {

			$(document).ajaxError(
					function(e, xhr, settings, exception) {
						$("body").append(
								'<div id="error-dialog" class="ui-dialog">' + '<div class="ui-dialog-title">Error</div>' + '<div class="ui-dialog-content">' + 'Location: ' + settings.url + '<br><br>'
										+ xhr.responseText + '<br><br>' + '<div class="right">' + '<button class="ui-button btn-cancel">OK</button>' + '</div></div></div>');
						initOneButtonDialog(jQuery);
					});

			$("#btn-save-settings").click(function() {
				$args = {
					tooltiponclick : $("#cbx-tooltiponclick").is(":checked")
				};
				$.post("../SaveSettings", $args, function(data) {
					if (data == "success") {
						noty({
							type : "success",
							text : "Settings were saved successfully.",
							timeout : 3000
						});
					} else {
						noty({
							type : "error",
							text : data
						});
					}
				});
			});

		});
