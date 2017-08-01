
<!--  @author Haris Asif - haris.asif@ihsinformatics.com -->

<!--  

Copyright(C) 2016 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html
Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.

-->


<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%!String selection;
	String duplicates;
	String checkDigit;
	String prefix1;
	String appendDate;
	String alphanumeric;
	String caseSen;
	String dateFormat;
	String date;
	String length;
	String from;
	String to;
	String range;
	String layout;
	String copies;
	String pagetypes;
	String pageorientations;%>

<%
	prefix1 = request.getParameter("prefix");
	selection = request.getParameter("typeSelection");
	duplicates = request.getParameter("duplicates");
	checkDigit = request.getParameter("checkdigitBox");
	appendDate = request.getParameter("appendDate");
	dateFormat = request.getParameter("dateFormatList");
	date = request.getParameter("date");
	length = request.getParameter("serialNumberList");
	alphanumeric = request.getParameter("alphanumeric");
	caseSen = request.getParameter("casesensitive");
	range = request.getParameter("rangeForRandom");
	layout = request.getParameter("column");
	copies = request.getParameter("copiesList");
	pagetypes = request.getParameter("pagetype");
	from = request.getParameter("from");
	to = request.getParameter("to");
	pageorientations = request.getParameter("pageorientation");
	if (prefix1 == null) {
		prefix1 = "";
	}

	if (range == null) {
		range = "";
	}

	if (from == null) {
		from = "";
	}

	if (to == null) {
		to = "";
	}

	if (date == null) {
		date = "";
	}
%>

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
$( document ).ready(function() { 
enableFormat(); 
changeForm(); 
enableSensitive(); 
updateColumns();
setNumberOfCopies();
}); 
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
	
	function updateColumns() {
		
		var columnList = document.getElementById("columnBox");
		
		if(document.getElementById("portraitBtn").checked == true){
			for (var i=0; i<columnList.length; i++){
				  if (columnList.options[i].value == '7' || columnList.options[i].value == '8'){
					  columnList.remove(i);
				       i = 0;
				  }
			}
			setNumberOfCopies();
		}
		else{
			if(columnList.length == 4){
			 	for(i = 7 ; i <= 8 ; i++){
					 var objOption = document.createElement("option");
					 objOption.text = i;
					 objOption.value = i;
					 columnList.options.add(objOption);
				 }
			}
			
			setNumberOfCopies();
		}
	}
	
	</script>
	
<script type="text/javascript"> 
	
	function setNumberOfCopies(){
		var colList = document.getElementById("columnBox");
		var columnSelectedValue = colList.options[colList.selectedIndex].value;
		var columnSelectedVal = parseInt(columnSelectedValue);
		var copiesBox = document.getElementById("copyBox");
		var numberArr = [];
		var k = columnSelectedVal;
		
		for(i = copiesBox.options.length - 1 ; i >= 1  ; i--){
			copiesBox.remove(i);
		}
		
		if(columnSelectedVal % 2 == 0){
			
			while (k > 1){
				numberArr.push(k);
				if( k % 2 == 0){
					k/=2;
				}
				else{
					break;
				}
			}
			
			for(j = numberArr.length - 1 ; j >= 0 ; j--){
				 var objOption = document.createElement("option");
				 objOption.value = numberArr[j];
				 objOption.text = numberArr[j];
				 copiesBox.options.add(objOption);
			}
		}
		
		else{
			 var objOption = document.createElement("option");
			 objOption.text = columnSelectedVal;
			 objOption.value = columnSelectedVal;
			 copiesBox.options.add(objOption);
		}
		
	} 
	
</script>	
	
<script type="text/javascript">
function isNumber(evt) {
    evt = (evt) ? evt : window.event;
    var charCode = (evt.which) ? evt.which : evt.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
        return false;
    }
    return true;
}
</script>


<script type="text/javascript">
function isAlphaNumeric(e) {
	var charCode = e.which;
 	   if ((e.keyCode >= 48 && e.keyCode <= 57) ||
    	   (e.keyCode >= 65 && e.keyCode <= 90) ||
     	   (e.keyCode >= 97 && e.keyCode <= 122) || (charCode >= 48 && charCode <= 57) ||
      		(charCode >= 65 && charCode <= 90) || (charCode >= 97 && charCode <= 122) || 
      		(charCode == 8) || (charCode == 127)){
     		   return true;
 	   } else {
    		return false;
 	   }
}
</script>


<script type="text/javascript">
	function submitForm(btnText) {
		document.getElementById('linkRow').innerHTML = "";
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
		var btnType = document.getElementById("hiddenBtn");
		
	    btnType.value = btnText;
		
		var reg = new RegExp('^[0-9]+$');
		var reg1 = new RegExp('[a-zA-Z0-9]*[a-zA-Z]*');
		
		var splitter = date.value.split("-");
		
		var defaultRange = "9";
		var val = serialValue - 1;

		
		 if (serial.checked == true) {

			if(prefix.value != "" && prefix.value.trim() == ""){
					error.innerHTML = "Incorrect Prefix Value!!";
			}
					
			else if(prefix.value != "" &&  /[^a-zA-Z0-9]/.test(prefix.value)){
						error.innerHTML = "Incorrect Prefix Value!!";
			}
			 
			else if(appendDate.checked && date.value == "") {
				error.innerHTML = "Date Missing!!";

			}

			else if (appendDate.checked
					&& !date.value.replace(/\s/g, '').length) {
				error.innerHTML = "Date Missing!!";
			}
			
			else if(appendDate.checked && splitter[0] < 1970){
				error.innerHTML = "Incorrect Date Year. Minimum year can be 1970";
			}
			
			else if(appendDate.checked && splitter[1] < 1 || appendDate.checked && splitter[1] > 12){
				error.innerHTML = "Incorrect Date Month";
			}
			
			else if(appendDate.checked && splitter[2] < 1 || appendDate.checked && splitter[2] > 31){
				error.innerHTML = "Incorrect Date Day";
			}
			
			else if (from.value == "") {
				error.innerHTML = "Serial No Range From Missing or Incorrect!!";

			}
			
			else if(!reg.test(from.value)){
				error.innerHTML = "Serial No Range Incorrect From Value!!";
			}
			
			else if(from.value == 0){
				error.innerHTML = "Serial No Range From Cant Be 0!!";
			}

			else if (to.value == "") {
				error.innerHTML = "Serial No Range To Missing or Incorrect!!";

			} 
			else if(!reg.test(to.value)){
				error.innerHTML = "Serial No Range Incorrect To Value!!";
			}

			else if(to.value == 0){
				error.innerHTML = "Serial No Range To Cant Be 0!!";
			}

			
			else if (layout.value == "") {
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
			var alphaNum = document.getElementById('alphanumericBox');
			var caseSensitive = document.getElementById('caseSenstiveBox');
			var totalSum = 0;
			var mulNumber = 0;

			if (!alphaNum.checked && !caseSensitive.checked) {
				totalSum = 10;
				mulNumber = 10;
				
			}

			if (alphaNum.checked && !caseSensitive.checked) {
				totalSum = 36;
				mulNumber = 36;
			}

			if (alphaNum.checked && caseSensitive.checked) {
				totalSum = 62;
				mulNumber = 62;
			}

			if (!randomRange.value == "") {
				for (var j = 1; j < serialValue ; j++) {
					totalSum *= mulNumber;
				}
			}

			
			if(prefix.value != "" && prefix.value.trim() == ""){
				error.innerHTML = "Incorrect Prefix Value!!";
			}
				
			else if(prefix.value != "" &&  /[^a-zA-Z0-9]/.test(prefix.value)){
					error.innerHTML = "Incorrect Prefix Value!!";
			}
			
			else if (appendDate.checked && date.value == "") {
				error.innerHTML = "Date Missing!!";

			}

			else if (appendDate.checked
					&& !date.value.replace(/\s/g, '').length) {
				error.innerHTML = "Date Missing!!";
			}
			
			else if(appendDate.checked && splitter[0] < 1970){
				error.innerHTML = "Incorrect Date Year. Minimum year can be 1970";
			}
			
			else if(appendDate.checked && splitter[1] < 1 || appendDate.checked && splitter[1] > 12){
				error.innerHTML = "Incorrect Date Month";
			}
			
			else if(appendDate.checked && splitter[2] < 1 || appendDate.checked && splitter[2] > 31){
				error.innerHTML = "Incorrect Date Day";
			}

			else if (randomRange.value == "") {
				error.innerHTML = "Range Missing or Incorrect Range!!";
			}
			
			else if(!reg.test(randomRange.value)){
				error.innerHTML = "Incorrect Range Value!!";
			}
			
			else if(randomRange.value == 0){
				error.innerHTML = "Range Cant Be 0!!";
			}


			else if (layout.value == "") {
				error.innerHTML = "QR Code Layout Missing!!";
			}
			
			else if(randomRange.value > 1000000 && totalSum > 1000000){
				error.innerHTML = "Maximum length can be 1000000";
			}
			
			else if(randomRange.value > totalSum){
				error.innerHTML = "Maximum length can be " + totalSum;
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
		var duplicates = document.getElementById('duplicateChk');
		var link = document.getElementById('linkRow');
		var duplicateCheckBox = document.getElementById('duplicateBox');

		if (serial.checked == true) {
			alpha.style.display = 'none';
			//document.getElementById('errorLbl').innerHTML = "";
			sensitive.style.display = 'none';
			alphaLbl.style.display = 'none';
			link.style.display = 'none';
			senseLbl.style.display = 'none';
			alpha.checked = false;
			sensitive.checked = false;
			row.style.display = 'table-row';
			rangeRow.style.display = 'none';
			textTypeRow.style.display = 'none';
			duplicates.style.display = 'table-row';
		}

		else if (random.checked == true) {
			enableSensitive();
			//document.getElementById('errorLbl').innerHTML = "";
			alpha.style.display = 'inline-block';
			sensitive.style.display = 'inline-block';
			link.style.display = 'table-cell';
			duplicateCheckBox.checked = false;
			duplicates.style.display = 'none';
			alphaLbl.style.display = 'inline-block';
			senseLbl.style.display = 'inline-block';
			alpha.disabled = false;
			row.style.display = 'none';
			rangeRow.style.display = 'table-row';
			textTypeRow.style.display = 'table-row';
		}

	}
	
	</script>
	
	<script type="text/javascript">
	function removeError(){
			document.getElementById('errorLbl').innerHTML = "";
			document.getElementById('linkRow').innerHTML = "";
	}
	
	</script>
</head>
<body>
	<div class="container">
		<div class="row clearfix">
			<div class="col-md-12 column"></div>
		</div>

		<div class="row clearfix">


			<div class="col-md-12 column">
				<h3 align="center">
					QR Code Generator <font size="1">Version 1.2.0</font>
				</h3>
				<form method="POST" action="/gf-qrgen-web/qrgenerator"
					id="qrGenForm">

					<table border="1px" class="table table-bordered"
						style="width: 50%;" align="center">

						<tr>
							<th style="width: 1.3in">Type</th>
							<td style="padding-left: 23px; width: 3.5in"><input
								type="radio" id="serialBtn" value="serial" checked
								onclick="changeForm();removeError();" name="typeSelection"
								<%=("serial".equals(selection) ? "checked" : "")%>>Serial
								<input type="radio" id="randomBtn" value="random"
								onclick="changeForm();removeError();" name="typeSelection"
								<%=("random".equals(selection) ? "checked" : "")%>
								style="margin-left: 25px">Random</td>
						</tr>

						<tr id="textType" style="display: none">
							<th>Text Type</th>
							<td style="padding-left: 23px"><input id="alphanumericBox"
								type="checkbox" name="alphanumeric" style="display: none"
								onclick="enableSensitive()"
								<%=("on".equals(alphanumeric) ? "checked" : "")%> disabled>
								<label id="alphaLbl" style="font-weight: normal; display: none">Alphanumeric</label>
								<br> <input id="caseSenstiveBox" type="checkbox"
								style="display: none" name="casesensitive"
								<%=("on".equals(caseSen) ? "checked" : "")%> disabled> <label
								id="senseLbl" style="font-weight: normal; display: none">Case
									Sensitive</label></td>
						</tr>

						<tr id="duplicateChk">
							<th>Allow Duplicates</th>
							<td style="padding-left: 23px"><input id="duplicateBox"
								type="checkbox" name="duplicates"
								<%=("on".equals(duplicates) ? "checked" : "")%>></td>
						</tr>

						<tr>
							<th>Allow Check Digit</th>
							<td style="padding-left: 23px"><input id="checkdigitBox"
								type="checkbox" name="checkdigitBox"
								<%=("on".equals(checkDigit) ? "checked" : "")%>></td>
						</tr>

						<tr>
							<th>Prefix</th>
							<td style="padding-left: 23px"><input name="prefix"
								size="50" maxlength="10" class="form-control input"
								id="prefixId" required="true" value="<%=prefix1%>"
								onkeypress="return isAlphaNumeric(event)" /></td>
						</tr>
						<tr>
							<th>Append Date</th>
							<td style="padding-left: 23px"><input id="chkBox"
								type="checkbox" name="appendDate" onclick="enableFormat()"
								<%=("on".equals(appendDate) ? "checked" : "")%>></td>
						</tr>

						<tr>
							<th>Date Format</th>
							<td style="padding-left: 23px"><select id="dFormatList"
								name="dateFormatList" class="form-control input" required="true"
								disabled>
									<option value="yyMMdd"
										<%=("yyMMdd".equals(dateFormat) ? "selected = 'selected'"
					: "")%>>yyMMdd</option>
									<option value="yyMM"
										<%=("yyMM".equals(dateFormat) ? "selected = 'selected'" : "")%>>yyMM</option>
									<option value="yy"
										<%=("yy".equals(dateFormat) ? "selected = 'selected'" : "")%>>yy</option>
									<option value="yyyyMMdd"
										<%=("yyyyMMdd".equals(dateFormat) ? "selected = 'selected'"
					: "")%>>yyyyMMdd</option>
									<option value="MMyyyy"
										<%=("MMyyyy".equals(dateFormat) ? "selected = 'selected'"
					: "")%>>MMyyyy</option>
									<option value="ddMMyy"
										<%=("ddMMyy".equals(dateFormat) ? "selected = 'selected'"
					: "")%>>ddMMyy</option>
									<option value="MMyy"
										<%=("MMyy".equals(dateFormat) ? "selected = 'selected'" : "")%>>MMyy</option>
							</select></td>
						</tr>
						<tr>
							<th>Date</th>
							<td style="padding-left: 23px">
								<div class="form-group">
									<div class='input-group date' id='datetimepicker1'>
										<input type='text' id="datePicker" class="form-control"
											name="date" required="true" disabled value="<%=date%>" /> <span
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
									<option value="2"
										<%=("2".equals(length) ? "selected = 'selected'" : "")%>>2</option>
									<option value="3"
										<%=("3".equals(length) ? "selected = 'selected'" : "")%>>3</option>
									<option value="4"
										<%=("4".equals(length) ? "selected = 'selected'" : "")%>>4</option>
									<option value="5"
										<%=("5".equals(length) ? "selected = 'selected'" : "")%>>5</option>
									<option value="6"
										<%=("6".equals(length) ? "selected = 'selected'" : "")%>>6</option>
							</select></td>
						</tr>

						<tr id="serialNoRange">
							<th>Serial No Range</th>
							<td>
								<div class="control-group">

									<div class="col-md-6">
										From<input name="from" type="number" min="1"
											class="form-control input" value="<%=from%>" required="true"
											id="fromBox" onkeypress="return isNumber(event)" />
									</div>

									<div class="col-md-6">
										To<input id="toBox" name="to" type="number" min="1"
											class="form-control input" value="<%=to%>" required="true"
											onkeypress="return isNumber(event)" />
									</div>

								</div>

							</td>
						</tr>


						<tr id="randomRange" style="display: none">
							<th>Range</th>
							<td style="padding-left: 23px"><input name="rangeForRandom"
								type="number" id="rangeBox" class="form-control input"
								required="true" min="1" value="<%=range%>"
								onkeypress="return isNumber(event)" /></td>
						</tr>

						<tr>
							<th>Page Type</th>
							<td style="padding-left: 23px"><select name="pagetype"
								class="form-control input" required="true" id="pageType">
									<option value="A4"
										<%=("A4".equals(pagetypes) ? "selected = 'selected'" : "")%>>A4</option>
							</select></td>
						</tr>


						<tr>
							<th style="width: 1.3in">Page Orientation</th>
							<td style="padding-left: 23px; width: 3.5in"><input
								type="radio" id="portraitBtn" value="portrait" checked
								name="pageorientation" onclick="updateColumns()"
								<%=("portrait".equals(pageorientations) ? "checked" : "")%>>Portrait
								<input type="radio" id="landscapeBtn" value="landscape"
								name="pageorientation" onclick="updateColumns()"
								<%=("landscape".equals(pageorientations) ? "checked" : "")%>
								style="margin-left: 25px">Landscape</td>
						</tr>


						<tr>
							<th>QR Code Layout</th>
							<td style="padding-left: 23px">column(s)<select
								name="column" class="form-control input" required="true"
								id="columnBox" onchange="setNumberOfCopies()">
									<option value="3"
										<%=("3".equals(layout) ? "selected = 'selected'" : "")%>>3</option>
									<option value="4"
										<%=("4".equals(layout) ? "selected = 'selected'" : "")%>>4</option>
									<option value="5"
										<%=("5".equals(layout) ? "selected = 'selected'" : "")%>>5</option>
									<option value="6"
										<%=("6".equals(layout) ? "selected = 'selected'" : "")%>>6</option>
									<option value="7"
										<%=("7".equals(layout) ? "selected = 'selected'" : "")%>>7</option>
									<option value="8"
										<%=("8".equals(layout) ? "selected = 'selected'" : "")%>>8</option>
							</select></td>
						</tr>

						<tr>
							<th>Copies per Code</th>
							<td style="padding-left: 23px"><select name="copiesList"
								class="form-control input" required="true" id="copyBox">
									<option value="1"
										<%=("1".equals(copies) ? "selected = 'selected'" : "")%>>1</option>
									<option value="2"
										<%=("2".equals(copies) ? "selected = 'selected'" : "")%>>2</option>
									<option value="3"
										<%=("3".equals(copies) ? "selected = 'selected'" : "")%>>3</option>
									<option value="4"
										<%=("4".equals(copies) ? "selected = 'selected'" : "")%>>4</option>
									<option value="5"
										<%=("5".equals(copies) ? "selected = 'selected'" : "")%>>5</option>
									<option value="6"
										<%=("6".equals(copies) ? "selected = 'selected'" : "")%>>6</option>
									<option value="7"
										<%=("7".equals(copies) ? "selected = 'selected'" : "")%>>7</option>
									<option value="8"
										<%=("8".equals(copies) ? "selected = 'selected'" : "")%>>8</option>
							</select></td>
						<tr>
							<td colspan="3" align="center" id="errorLbl"
								style="color: red; font-weight: bold">${errorMsg}</td>
						</tr>

						<tr>
							<td colspan="3" align="center" id="linkRow"><a
								href="${linkDownload}">${linkTitle}</a></td>
						</tr>

						<tr>
							<td colspan="3">
								<div class="control-group">

									<div class="col-md 6">
										<div class="text-center">
										<input value="Generate PDF" id="pdfButton" type="button"
												onclick="submitForm(this.id);" class="btn btn-success" />
											 	&nbsp;&nbsp;
										<input value="Generate ZIP" id="zipButton" type="button"
												onclick="submitForm(this.id)" class="btn btn-success" />
												
										<input type="hidden" id="hiddenBtn" name = "fileType"/>
										
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