$(window).on(
		'load',
		function() {

			$("input[value='false']").click(function() {
				$(".showninfo").css("display", "none");
				$(".hiddeninfo").show();
				$("#btn-createdb").val("Connect to database");
			});

			$("input[value='true']").click(function() {
				$(".hiddeninfo").css("display", "none");
				$(".showninfo").show();
				$("#btn-createdb").val("Create database");
			});

			var $console = $("#console");

			$("#btn-createdb").click(
					function() {
						// validate form
						if ($console.text().length > 0) {
							$console.append("<br><br>");
						}
						writeToConsole("Validating form... ");
						var formvalid = true;
						if ($("input[name='dbhost']").val() == "") {
							writeToConsole("<br>ERROR: 'Database host' is empty.");
							formvalid = false;
						}
						if ($("input[name='dbport']").val() == "") {
							writeToConsole("<br>ERROR: 'Database port' is empty.");
							formvalid = false;
						}
						if ($("input[name='dbname']").val() == "") {
							writeToConsole("<br>ERROR: 'Database name' is empty.");
							formvalid = false;
						}
						if ($("input[name='dblogin']").val() == "") {
							writeToConsole("<br>ERROR: 'Database user name' is empty.");
							formvalid = false;
						}
						if ($("input[name='dbpswd']").val() == "") {
							writeToConsole("<br>ERROR: 'Database user password' is empty.");
							formvalid = false;
						}
						if ($("input[name='pinelogin']").val() == "") {
							writeToConsole("<br>ERROR: 'Pine administrator name' is empty.");
							formvalid = false;
						}
						if ($("input[name='pinepswd']").val() == "") {
							writeToConsole("<br>ERROR: 'Pine administrator password' is empty.");
							formvalid = false;
						}

						if (formvalid) {
							writeToConsole("Done.<br>Connect to database host... ");

							// connect to database host
							$.post("DBConnect", {
								dbhost : $("input[name='dbhost']").val(),
								dbport : $("input[name='dbport']").val(),
								dbname : $("input[name='dbname']").val(),
								dblogin : $("input[name='dblogin']").val(),
								dbpswd : $("input[name='dbpswd']").val(),
							}, function(data) {

								writeToConsole(data);

								if (data == "Done.") {
									// create database | validate database
									// structure
									var $message = "<br>Creating database... ";
									if ($("input[value='false']:checked").length > 0) {
										$message = "<br>Validating database structure... ";
									}
									writeToConsole($message);
									$.post("InitDB", {
										createnew : $("input[name='createnewdb']:checked").attr('value')
									}, function(data) {

										writeToConsole(data);

										if (data == "Done.") {
											// create Pine administrator
											writeToConsole("<br>Creating Pine administrator... ");
											$.post("CreateAdmin", {
												pinelogin : $("input[name='pinelogin']").val(),
												pinepswd : $("input[name='pinepswd']").val()
											}, function(data) {

												writeToConsole(data);

												if (data == "Done.") {
													// save database settings
													writeToConsole("<br>Saving database settings... ");
													$.post("SaveDBSettings", function(data) {
														writeToConsole(data);
														if (data == "Done.") {
															$("#success").html(
																	"<img src='img/success-icon.png'> "
																			+ "Database is successfully initialized. "
																			+ "<a href='../pine'>Start the Pine</a>.");
														}
													});
												}
											});
										}
									});
								}
							});

						}
					});

			function writeToConsole(text) {
				$console.append(text);
				$console.animate({
					scrollTop : ($console.offset().top + $console.height())
				}, 'fast');
			}

		});
