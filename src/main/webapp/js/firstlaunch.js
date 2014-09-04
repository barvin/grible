$(window).on(
		'load',
		function() {

			$("input[name='createnewdb'][value='false']").click(function() {
				$(".showninfo").css("display", "none");
				$(".hiddeninfo").show();
				$("#btn-createdb").val(lang.connecttodb);
			});

			$("input[name='createnewdb'][value='true']").click(function() {
				$(".hiddeninfo").css("display", "none");
				$(".showninfo").show();
				$("#btn-createdb").val(lang.createdb);
			});

			$("input[name='exportfromdb'][value='false']").click(function() {
				$("input.dialog-edit").prop("disabled", true);
				$("#btn-export").val(lang.finish);
			});

			$("input[name='exportfromdb'][value='true']").click(function() {
				$("input.dialog-edit").prop("disabled", false);
				$("#btn-export").val(lang.exportdata);
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
						writeToConsole(lang.validatingform + " ");
						var formvalid = true;
						if ($("#postgres-settings input[name='dbhost']").val() == "") {
							writeToConsole("<br>ERROR: '" + lang.dbhost + "' " + lang.empty);
							formvalid = false;
						}
						if ($("#postgres-settings input[name='dbport']").val() == "") {
							writeToConsole("<br>ERROR: '" + lang.dbport + "' " + lang.empty);
							formvalid = false;
						}
						if ($("#postgres-settings input[name='dbname']").val() == "") {
							writeToConsole("<br>ERROR: '" + lang.dbname + "' " + lang.empty);
							formvalid = false;
						}
						if ($("#postgres-settings input[name='dblogin']").val() == "") {
							writeToConsole("<br>ERROR: '" + lang.dblogin + "' " + lang.empty);
							formvalid = false;
						}
						if ($("#postgres-settings input[name='dbpswd']").val() == "") {
							writeToConsole("<br>ERROR: '" + lang.dbpswd + "' " + lang.empty);
							formvalid = false;
						}
						if ($("#postgres-settings input[name='griblelogin']").val() == "") {
							writeToConsole("<br>ERROR: '" + lang.gribleadminname + "' " + lang.empty);
							formvalid = false;
						}
						if ($("#postgres-settings input[name='griblepswd']").val() == "") {
							writeToConsole("<br>ERROR: '" + lang.gribleadminpswd + "' " + lang.empty);
							formvalid = false;
						}

						if (formvalid) {
							writeToConsole(lang.done + "<br>" + lang.connecttodbhost + " ");

							// connect to database host
							$.post("DBConnect", {
								apptype : "POSTGRESQL",
								dbhost : $("#postgres-settings input[name='dbhost']").val(),
								dbport : $("#postgres-settings input[name='dbport']").val(),
								dbname : $("#postgres-settings input[name='dbname']").val(),
								dblogin : $("#postgres-settings input[name='dblogin']").val(),
								dbpswd : $("#postgres-settings input[name='dbpswd']").val(),
							}, function(data) {

								if (data == "success") {
									// create database | validate database
									// structure
									writeToConsole(lang.done);
									var $message = "<br>" + lang.creatingdb + " ";
									if ($("#postgres-settings input[name='createnewdb'][value='false']").is(":checked")) {
										$message = "<br>" + lang.upgradingdb + " ";
									}
									writeToConsole($message);
									$.post("InitDB", {
										createnew : $("#postgres-settings input[name='createnewdb']:checked").attr('value')
									}, function(data) {

										if (data == "success") {
											// create Grible administrator
											writeToConsole(lang.done);
											writeToConsole("<br>" + lang.creatinggribgleadmin + " ");
											$.post("CreateAdmin", {
												griblelogin : $("#postgres-settings input[name='griblelogin']").val(),
												griblepswd : $("#postgres-settings input[name='griblepswd']").val()
											}, function(data) {

												if (data == "success") {
													// save database settings
													writeToConsole(lang.done);
													writeToConsole("<br>" + lang.savingdbsettings + " ");
													$.post("SaveDBSettings",
															function(data) {
																if (data == "success") {
																	writeToConsole(lang.done);
																	$("#success").html(
																			"<img src='img/success-icon.png'> " + lang.dbinitialized + " " + "<a href='../'>" + lang.startgrible
																					+ "</a>.");
																} else {
																	writeToConsole(data);
																}
															});
												} else {
													writeToConsole(data);
												}
											});
										} else {
											writeToConsole(data);
										}
									});
								} else {
									writeToConsole(data);
								}
							});

						}
					});

			$("#btn-export").click(
					function() {
						if ($("#json-settings input[value='false']:checked").length > 0) {
							writeToConsole(lang.settingupgrible + " ");
							$.post("SetJsonAppType", function(data) {
								if (data == "success") {
									writeToConsole(lang.done);
									$("#success").html("<img src='img/success-icon.png'> " + lang.griblesetup + " <a href='../'>" + lang.startgrible + "</a>.");
								} else {
									writeToConsole(data);
								}
							});
						} else {
							// validate form
							if ($console.text().length > 0) {
								$console.append("<br><br>");
							}
							writeToConsole(lang.validatingform + " ");
							var formvalid = true;
							if ($("#json-settings input[name='dbhost']").val() == "") {
								writeToConsole("<br>ERROR: '" + lang.dbhost + "' " + lang.empty);
								formvalid = false;
							}
							if ($("#json-settings input[name='dbport']").val() == "") {
								writeToConsole("<br>ERROR: '" + lang.dbport + "' " + lang.empty);
								formvalid = false;
							}
							if ($("#json-settings input[name='dbname']").val() == "") {
								writeToConsole("<br>ERROR: '" + lang.dbname + "' " + lang.empty);
								formvalid = false;
							}
							if ($("#json-settings input[name='dblogin']").val() == "") {
								writeToConsole("<br>ERROR: '" + lang.dblogin + "' " + lang.empty);
								formvalid = false;
							}
							if ($("#json-settings input[name='dbpswd']").val() == "") {
								writeToConsole("<br>ERROR: '" + lang.dbpswd + "' " + lang.empty);
								formvalid = false;
							}
							if ($("#json-settings input[name='destination']").val() == "") {
								writeToConsole("<br>ERROR: '" + lang.destpath + "' " + lang.empty);
								formvalid = false;
							}

							if (formvalid) {
								writeToConsole(lang.done + "<br>" + lang.connecttodbhost + " ");

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
									if (data == "success") {
										// upgrade database structure if needed
										writeToConsole("<br>" + lang.upgradingdb + " ");
										$.post("UpgradeDb", function(data) {
											writeToConsole(data);
											if (data == "success") {
												$.post("SaveDBSettings", function(data) {
													if (data == "success") {
														writeToConsole("<br>" + lang.exportingdata + " ");
														// export data
														$.post("ExportFromDbToJson", {
															dest : $("#json-settings input[name='destination']").val()
														}, function(data) {
															writeToConsole(data);
															if (data == "success") {
																$("#success").html(
																		"<img src='img/success-icon.png'> " + lang.dataexported + "<br><br>" + lang.nowmove + "<br>" + lang.then
																				+ " <a href='../'>" + lang.startgrible2 + "</a> " + lang.andadd);
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
