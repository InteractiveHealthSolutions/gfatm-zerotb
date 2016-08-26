<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>QR Generator</title>

<script
	src="http://ajax.googleapis.com/ajax/libs/jquery/2.0.0/jquery.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.13.0/moment.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.37/js/bootstrap-datetimepicker.min.js"></script>


<script type="text/javascript"
	src="<c:url value="/resources/src/js/bootstrap.min.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/resources/src/js/scripts.js"/>"></script>

<link href="resources/src/css/bootstrap.min.css" rel="stylesheet">
<link href="resources/src/css/bootstrap-theme.min.css" rel="stylesheet">
<link href="resources/src/css/bootstrap-theme.css" rel="stylesheet">
<link href="resources/src/css/bootstrap.css" rel="stylesheet">
<link href="resources/src/css/style.css" rel="stylesheet">

<script type="text/javascript">
	$(function() {
		$('#datetimepicker1').datetimepicker({
			format : 'YYYY-MM-DD'
		});
	});
</script>

<script type="text/javascript">
	function enableFormat() {
		var checkedValue = document.getElementById('chkBox');
		if (checkedValue.checked) {
			document.getElementById('dFormatList').disabled = false;
			document.getElementById('datePicker').disabled = false;

		} else {
			document.getElementById('dFormatList').disabled = true;
			document.getElementById('datePicker').disabled = true;
		}
	}
</script>

<script type="text/javascript">
	function enableSensitive() {
		var checkedValue = document.getElementById('alphanumericBox');
		if (checkedValue.checked) {
			document.getElementById('caseSenstiveBox').disabled = false;
		} else {
			document.getElementById('caseSenstiveBox').checked = false;
			document.getElementById('caseSenstiveBox').disabled = true;
		}
	}
</script>



<script type="text/javascript">
	function submitForm() {
		var serialNumList = document.getElementById("serialNum");
		var serialValue = serialNumList.options[serialNumList.selectedIndex].value;
		var toVal = document.getElementById("toBox");
		var fromVal = document.getElementById("fromBox");
		var form = document.getElementById("qrGenForm");
		var date = document.getElementById("datePicker");
		var prefix = document.getElementById("prefixId");
		var error = document.getElementById('errorLbl');
		var from = document.getElementById("fromBox");
		var appendDate = document.getElementById("chkBox");
		var to = document.getElementById("toBox");
		var layout = document.getElementById("columnBox");
		var serial = document.getElementById("serialBtn");
		var random = document.getElementById("randomBtn");
		var randomRange = document.getElementById("rangeBox");

		var defaultRange = "9";
		var val = serialValue - 1;

		if (serial.checked == true) {

			if (prefix.value == "") {
				error.innerHTML = "Prefix Missing!!";

			}

			else if (!prefix.value.replace(/\s/g, '').length) {
				error.innerHTML = "Prefix Missing!!";
			}

			else if (appendDate.checked && date.value == "") {
				error.innerHTML = "Date Missing!!";

			}

			else if (appendDate.checked
					&& !date.value.replace(/\s/g, '').length) {
				error.innerHTML = "Date Missing!!";
			}

			else if (from.value == "") {
				error.innerHTML = "Serial No Range From Missing!!";

			}

			else if (to.value == "") {
				error.innerHTML = "Serial No Range To Missing!!";

			} else if (layout.value == "") {
				error.innerHTML = "QR Code Layout Missing!!";
			}

			else {

				for (var i = 0; i < val; i++) {
					defaultRange += "9";
				}

				var defaultRangeNumber = parseInt(defaultRange);
				var incomingValue = parseInt(toVal.value);
				var fromValue = parseInt(fromVal.value);

				if (incomingValue > defaultRangeNumber) {
					error.innerHTML = "Serial number range cannot be greater than "
							+ defaultRange;
				}

				else if (fromValue > incomingValue) {
					error.innerHTML = "Serial number range is not in order. Start value is greater than End value";
				}

				else {
					error.innerHTML = "";
					form.submit();
				}
			}
		}

		else {
			if (prefix.value == "") {
				error.innerHTML = "Prefix Missing!!";

			}

			else if (!prefix.value.replace(/\s/g, '').length) {
				error.innerHTML = "Prefix Missing!!";
			}

			else if (appendDate.checked && date.value == "") {
				error.innerHTML = "Date Missing!!";

			}

			else if (appendDate.checked
					&& !date.value.replace(/\s/g, '').length) {
				error.innerHTML = "Date Missing!!";
			}

			else if (randomRange.value == "") {
				error.innerHTML = "Range Missing!!";
			}

			else if (layout.value == "") {
				error.innerHTML = "QR Code Layout Missing!!";
			}

			else {
				error.innerHTML = "";
				form.submit();
			}
		}
	}
</script>

<script type="text/javascript">
	function changeForm() {
		var serial = document.getElementById("serialBtn");
		var random = document.getElementById("randomBtn");
		var prefix = document.getElementById("prefixId");
		var appendDate = document.getElementById("chkBox");
		var dateFormat = document.getElementById("dFormatList");
		var date = document.getElementById("datePicker");
		var sno = document.getElementById("serialNum");
		var from = document.getElementById("fromBox");
		var to = document.getElementById("toBox");
		var layout = document.getElementById("columnBox");
		var copies = document.getElementById("copyBox");
		var alpha = document.getElementById("alphanumericBox");
		var sensitive = document.getElementById('caseSenstiveBox');
		var row = document.getElementById('serialNoRange');
		var rangeRow = document.getElementById('randomRange');
		var textTypeRow = document.getElementById('textType');
		var alphaLbl = document.getElementById('alphaLbl');
		var senseLbl = document.getElementById('senseLbl');

		if (serial.checked == true) {
			// 			prefix.disabled = false;
			// 			appendDate.disabled = false;
			// 			sno.disabled = false;
			// 			from.disabled = false;
			// 			to.disabled = false;
			// 			layout.disabled = false;
			// 			copies.disabled = false;
			alpha.style.display = 'none';
			sensitive.style.display = 'none';
			alphaLbl.style.display = 'none';
			senseLbl.style.display = 'none';
			alpha.checked = false;
			sensitive.checked = false;
			row.style.display = 'table-row';
			rangeRow.style.display = 'none';
			textTypeRow.style.display = 'none';
		}

		else if (random.checked == true) {
			//	prefix.disabled = true;
			//	appendDate.checked = false;
			//	appendDate.disabled = true;
			//	dateFormat.disabled = true;
			//	date.disabled = true;
			//	sno.disabled = true;
			//	from.disabled = true;
			//	to.disabled = true;
			//	layout.disabled = true;
			//	copies.disabled = true;
			alpha.style.display = 'inline-block';
			sensitive.style.display = 'inline-block';
			alphaLbl.style.display = 'inline-block';
			senseLbl.style.display = 'inline-block';
			alpha.disabled = false;
			row.style.display = 'none';
			rangeRow.style.display = 'table-row';
			textTypeRow.style.display = 'table-row';
		}

	}
</script>


</head>
<body>
	<div class="container">
		<div class="row clearfix">
			<div class="col-md-12 column"></div>
		</div>

		<div class="row clearfix" style="margin-left: 3in">


			<div class="col-md-9 column">
				<h3 align="center">QR Code Generator</h3>
				<form method="POST" action="/gf-qrgen-web/qrgenerator"
					id="qrGenForm">

					<table border="1px" class="table table-bordered"
						style="width: 4.8in; margin-left: 0.8in">

						<tr>
							<th style="width: 1.3in">Type</th>
							<td style="padding-left: 23px; width: 3.5in">
							<input type="radio" id="serialBtn" value="serial" checked
								onclick="changeForm()" name="typeSelection">Serial
							<input
								type="radio" id="randomBtn" value="random"
								onclick="changeForm()" name="typeSelection" style="margin-left: 25px">Random
						  </td>
						</tr>

						<tr id="textType" style="display: none">
							<th>Text Type</th>
							<td style="padding-left: 23px"><input id="alphanumericBox"
								type="checkbox" name="alphanumeric" style="display: none"
								onclick="enableSensitive()" disabled> <label
								id="alphaLbl" style="font-weight: normal; display: none">Alphanumeric</label>
								<br> <input id="caseSenstiveBox" type="checkbox"
								style="display: none" name="casesensitive" disabled> <label
								id="senseLbl" style="font-weight: normal; display: none">Case
									Sensitive</label></td>
						</tr>
						<tr>
							<th>Prefix</th>
							<td style="padding-left: 23px"><input name="prefix"
								size="50" maxlength="20" class="form-control input"
								id="prefixId" required="true" /></td>
						</tr>
						<tr>
							<th>Append Date</th>
							<td style="padding-left: 23px"><input id="chkBox"
								type="checkbox" name="appendDate" onclick="enableFormat()"></td>
						</tr>

						<tr>
							<th>Date Format</th>
							<td style="padding-left: 23px"><select id="dFormatList"
								name="dateFormatList" class="form-control input" required="true"
								disabled>
									<option value="yyMMdd">yyMMdd</option>
									<option value="yyMM">yyMM</option>
									<option value="yy">yy</option>
									<option value="yyyyMMdd">yyyyMMdd</option>
									<option value="MMyyyy">MMyyyy</option>
									<option value="ddMMyy">ddMMyy</option>
									<option value="MMyy">MMyy</option>
							</select></td>
						</tr>
						<tr>
							<th>Date</th>
							<td style="padding-left: 23px">
								<div class="form-group">
									<div class='input-group date' id='datetimepicker1'>
										<input type='text' id="datePicker" class="form-control"
											name="date" required="true" disabled /> <span
											class="input-group-addon"> <span
											class="glyphicon glyphicon-calendar"></span>
										</span>

									</div>
								</div>
							</td>
						</tr>
						<tr>
							<th>Length</th>
							<td style="padding-left: 23px"><select
								name="serialNumberList" class="form-control input"
								required="true" id="serialNum">
									<option value="2">2</option>
									<option value="3">3</option>
									<option value="4">4</option>
									<option value="5">5</option>
									<option value="6">6</option>
									<option value="7">7</option>
									<option value="8">8</option>
							</select></td>
						</tr>

						<tr id="serialNoRange">
							<th>Serial No Range</th>
							<td>
								<div class="control-group">

									<div class="col-md-6">
										From<input name="from" type="number" min="1"
											class="form-control input" required="true" id="fromBox" />
									</div>

									<div class="col-md-6">
										To<input id="toBox" name="to" type="number" min="1"
											class="form-control input" required="true" />
									</div>

								</div>

							</td>
						</tr>


						<tr id="randomRange" style="display: none">
							<th>Range</th>
							<td style="padding-left: 23px"><input name="rangeForRandom"
								type="number" id="rangeBox" class="form-control input"
								required="true" min="1" /></td>
						</tr>

						<tr>
							<th>QR Code Layout</th>
							<td style="padding-left: 23px">column(s)<select
								name="column" class="form-control input" required="true"
								id="columnBox">
									<option value="3">3</option>
									<option value="4">4</option>
									<option value="5">5</option>
									<option value="6">6</option>
							</select></td>
						</tr>

						<tr>
							<th>Copies per Code</th>
							<td style="padding-left: 23px"><select name="copiesList"
								class="form-control input" required="true" id="copyBox">
									<option value="1">1</option>
									<option value="2">2</option>
									<option value="3">3</option>
									<option value="4">4</option>
							</select></td>
						</tr>

						<tr>
							<td colspan="3" align="center" id="errorLbl"
								style="color: red; font-weight: bold"></td>
						</tr>

						<tr>
							<td colspan="3">
								<div class="control-group">

									<div class="col-md 4">
										<div class="text-center">
											<input value="Generate" id="singlebutton" type="button"
												onclick="submitForm()" class="btn btn-success" />
										</div>

									</div>
								</div>
							</td>
						</tr>
					</table>
				</form>
			</div>
		</div>

	</div>
</body>
</html>