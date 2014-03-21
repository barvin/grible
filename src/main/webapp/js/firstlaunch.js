$(window).on(
		'load',
		function() {

			$("input[name='createnewdb'][value='false']").click(function() {
				$(".showninfo").css("display", "none");
				$(".hiddeninfo").show();
				$("#btn-createdb").val("Connect to database");
			});

			$("input[name='createnewdb'][value='true']").click(function() {
				$(".hiddeninfo").css("display", "none");
				$(".showninfo").show();
				$("#btn-createdb").val("Create database");
			});

			$("input[name='exportfromdb'][value='false']").click(function() {
				$("input.dialog-edit").prop("disabled", true);
				$("#btn-export").val("Finish");
			});

			$("input[name='exportfromdb'][value='true']").click(function() {
				$("input.dialog-edit").prop("disabled", false);
				$("#btn-export").val("Export data");
			});

			$("#btn-select-json").click(function() {
				$("#type-selection").slideUp(function() {
					$("#json-settings").slideDown();
					$("#console").slideDown();
				});
			});

			$("#btn-select-postgresql").click(function() {
				$("#type-selection").slideUp(function() {
					$("#postgres-settings").slideDown();
					$("#console").slideDown();
				});
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
						if ($("#postgres-settings input[name='dbhost']").val() == "") {
							writeToConsole("<br>ERROR: 'Database host' is empty.");
							formvalid = false;
						}
						if ($("#postgres-settings input[name='dbport']").val() == "") {
							writeToConsole("<br>ERROR: 'Database port' is empty.");
							formvalid = false;
						}
						if ($("#postgres-settings input[name='dbname']").val() == "") {
							writeToConsole("<br>ERROR: 'Database name' is empty.");
							formvalid = false;
						}
						if ($("#postgres-settings input[name='dblogin']").val() == "") {
							writeToConsole("<br>ERROR: 'Database user name' is empty.");
							formvalid = false;
						}
						if ($("#postgres-settings input[name='dbpswd']").val() == "") {
							writeToConsole("<br>ERROR: 'Database user password' is empty.");
							formvalid = false;
						}
						if ($("#postgres-settings input[name='griblelogin']").val() == "") {
							writeToConsole("<br>ERROR: 'Grible administrator name' is empty.");
							formvalid = false;
						}
						if ($("#postgres-settings input[name='griblepswd']").val() == "") {
							writeToConsole("<br>ERROR: 'Grible administrator password' is empty.");
							formvalid = false;
						}

						if (formvalid) {
							writeToConsole("Done.<br>Connect to database host... ");

							// connect to database host
							$.post("DBConnect", {
								apptype : "POSTGRESQL",
								dbhost : $("#postgres-settings input[name='dbhost']").val(),
								dbport : $("#postgres-settings input[name='dbport']").val(),
								dbname : $("#postgres-settings input[name='dbname']").val(),
								dblogin : $("#postgres-settings input[name='dblogin']").val(),
								dbpswd : $("#postgres-settings input[name='dbpswd']").val(),
							}, function(data) {

								writeToConsole(data);

								if (data == "Done.") {
									// create database | validate database
									// structure
									var $message = "<br>Creating database... ";
									if ($("#postgres-settings input[name='createnewdb'][value='false']").is(":checked")) {
										$message = "<br>Upgrading database structure if needed... ";
									}
									writeToConsole($message);
									$.post("InitDB", {
										createnew : $("#postgres-settings input[name='createnewdb']:checked").attr('value')
									}, function(data) {

										writeToConsole(data);

										if (data == "Done.") {
											// create Grible administrator
											writeToConsole("<br>Creating Grible administrator... ");
											$.post("CreateAdmin", {
												griblelogin : $("#postgres-settings input[name='griblelogin']").val(),
												griblepswd : $("#postgres-settings input[name='griblepswd']").val()
											}, function(data) {

												writeToConsole(data);

												if (data == "Done.") {
													// save database settings
													writeToConsole("<br>Saving database settings... ");
													$.post("SaveDBSettings", function(data) {
														writeToConsole(data);
														if (data == "Done.") {
															$("#success").html(
																	"<img src='img/success-icon.png'> " + "Database is successfully initialized. "
																			+ "<a href='../'>Start Grible</a>.");
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

			$("#btn-export").click(
					function() {
						if ($("#json-settings input[value='false']:checked").length > 0) {
							writeToConsole("Setting up Grible... ");
							$.post("SetJsonAppType", function(data) {
								if (data == "success") {
									writeToConsole("Done.");
									$("#success").html("<img src='img/success-icon.png'> Grible was successfully set up. <a href='../'>Start Grible</a>.");
								} else {
									writeToConsole(data);
								}
							});
						} else {
							// validate form
							if ($console.text().length > 0) {
								$console.append("<br><br>");
							}
							writeToConsole("Validating form... ");
							var formvalid = true;
							if ($("#json-settings input[name='dbhost']").val() == "") {
								writeToConsole("<br>ERROR: 'Database host' is empty.");
								formvalid = false;
							}
							if ($("#json-settings input[name='dbport']").val() == "") {
								writeToConsole("<br>ERROR: 'Database port' is empty.");
								formvalid = false;
							}
							if ($("#json-settings input[name='dbname']").val() == "") {
								writeToConsole("<br>ERROR: 'Database name' is empty.");
								formvalid = false;
							}
							if ($("#json-settings input[name='dblogin']").val() == "") {
								writeToConsole("<br>ERROR: 'Database user name' is empty.");
								formvalid = false;
							}
							if ($("#json-settings input[name='dbpswd']").val() == "") {
								writeToConsole("<br>ERROR: 'Database user password' is empty.");
								formvalid = false;
							}
							if ($("#json-settings input[name='destination']").val() == "") {
								writeToConsole("<br>ERROR: 'Destination path' is empty.");
								formvalid = false;
							}

							if (formvalid) {
								writeToConsole("Done.<br>Connect to database host... ");

								// connect to database host
								$.post("DBConnect", {
									apptype : "JSON",
									dbhost : $("#json-settings input[name='dbhost']").val(),
									dbport : $("#json-settings input[name='dbport']").val(),
									dbname : $("#json-settings input[name='dbname']").val(),
									dblogin : $("#json-settings input[name='dblogin']").val(),
									dbpswd : $("#json-settings input[name='dbpswd']").val(),
								}, function(data) {
									writeToConsole(data);
									if (data == "Done.") {
										// upgrade database structure if needed
										writeToConsole("<br>Upgrading database structure if needed... ");
										$.post("UpgradeDb", function(data) {
											writeToConsole(data);
											if (data == "Done.") {
												$.post("SaveDBSettings", function(data) {
													if (data == "Done.") {
														writeToConsole("<br>Exporting data... ");
														// export data
														$.post("ExportFromDbToJson", {
															dest : $("#json-settings input[name='destination']").val()
														}, function(data) {
															writeToConsole(data);
															if (data == "Done.") {
																$("#success").html(
																		"<img src='img/success-icon.png'> Data was successfully exported."
																				+ "<br><br>Now move exported files to your frameworks "
																				+ "(e.g. from 'C:\\Tools\\Grible\\Temp\MyProduct' to 'D:\\PathToFramework\\MyProduct\\data')."
																				+ "<br>Then <a href='../'>start Grible</a> and add all these products.");
															}
														});
													}
												});
											}
										});
									}
								});
							}
						}
					});

			function writeToConsole(text) {
				$console.append(text);
				$console.animate({
					scrollTop : ($console.offset().top + $console.height())
				}, 'fast');
			}

		});
