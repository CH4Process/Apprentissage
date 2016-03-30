<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="jstl_core" %>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="style.css" />
<title>Test de google charts</title>
</head>
<body>
	<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
  <div id="chart_div"></div>
  <div id="table_div"></div>
  <script>
  
  google.charts.load('current', {packages: ['corechart', 'line','table']});
  google.charts.setOnLoadCallback(drawLineChart);
  
  function drawLineChart() 
  {
	var data = new google.visualization.DataTable();
	data.addColumn('datetime', 'Date');
	data.addColumn('number', 'Temperature');
				
	data.addRows([<jstl_core:forEach items="${bddrequest.requestResultArray}" var="entry">[new Date(${entry[0]}),${entry[1]}],</jstl_core:forEach>]);
				
	var line_options = 
	{
		hAxis : {
		title : 'Date',
	},
		vAxis : {
		title : 'Temperature',
	},
		backgroundColor : '#f1f8e9'
	};

	var chart = new google.visualization.LineChart(document.getElementById('chart_div'));
	chart.draw(data, line_options);
	var table = new google.visualization.Table(document.getElementById('table_div'));
    table.draw(data, {showRowNumber: true, width: '100%', height: '100%'});
	}
  
	</script>

</body>
</html>