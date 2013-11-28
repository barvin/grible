$().ready(function() {

	$("#option-tooltiponclick").click(function() {
		$("#cbx-tooltiponclick").click();
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
