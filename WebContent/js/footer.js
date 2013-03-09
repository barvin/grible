$(window).on('load', function() {
	loadFooter();
});

function loadFooter() {
	$("#footer").css("margin-top", "25px");
	var $docHeight = $(window).height();
	var $footerTop = $("#footer").offset().top;
	var $footerBottom = $footerTop + $("#footer").height();
	if ($docHeight > $footerBottom) {
		$("#footer").css("margin-top", ($docHeight - $footerBottom) + "px");
	}
}