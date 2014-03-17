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
						if ($("input[name='griblelogin']").val() == "") {
							writeToConsole("<br>ERROR: 'Grible administrator name' is empty.");
							formvalid = false;
						}
						if ($("input[name='griblepswd']").val() == "") {
							writeToConsole("<br>ERROR: 'Grible administrator password' is empty.");
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
									if ($("input[name='createnewdb'][value='false']").is(":checked")) {
										$message = "<br>Validating database structure... ";
									}
									writeToConsole($message);
									$.post("InitDB", {
										createnew : $("input[name='createnewdb']:checked").attr('value')
									}, function(data) {

										writeToConsole(data);

										if (data == "Done.") {
											// create Grible administrator
											writeToConsole("<br>Creating Grible administrator... ");
											$.post("CreateAdmin", {
												griblelogin : $("input[name='griblelogin']").val(),
												griblepswd : $("input[name='griblepswd']").val()
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
																			+ "<a href='../'>Start the Grible</a>.");
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

			$("#btn-export").click(function() {
				if ($("input[value='false']:checked").length > 0) {
					writeToConsole("Setting up Grible... ");
					$.post("SetJsonAppType", function(data) {
						if (data == "success") {
							writeToConsole("Done.");
							$("#success").html("<img src='img/success-icon.png'> Grible was successfully set up. <a href='../'>Start the Grible</a>.");
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
					if ($("input[name='destination']").val() == "") {
						writeToConsole("<br>ERROR: 'Destination path' is empty.");
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
								// upgrade database structure if needed
								writeToConsole("<br>Upgrading database structure if needed... ");
								$.post("UpgradeDb", function(data) {

									writeToConsole(data);
									if (data == "Done.") {
										// export data
										writeToConsole("<br>Exporting data... ");
										$.post("ExportDataFromDb", {
											dest : $("input[name='destination']").val()
										}, function(data) {
											writeToConsole(data);
											if (data == "Done.") {
												$("#success").html("<img src='img/success-icon.png'> Data was successfully exported. <a href='../'>Start the Grible</a>.");
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
