<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<script src="<c:url value="/resources/js/jquery-2.1.3.min.js" />"></script>
<script src="<c:url value="/resources/js/jquery.jeditable.js" />"></script>
<script src="<c:url value="/resources/js/jquery.dataTables.js" />"></script>
<script src="<c:url value="/resources/js/main.js" />"></script>
<script src="<c:url value="/resources/js/jquery-ui.min.js" />"></script>

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/jquery.dataTables.css" />">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/jquery-ui.css" />">

<html>
	<head>
		<meta charset="utf-8">
		<title>Gaz Station</title>
		<style>
			html,body { 
				height:100%;
				overflow: auto; 
			}
			.scroller{
				overflow: auto;
				width:100%;
				height:90%;
			}
		</style>
	</head> 
	<body>
	
	<div id="dialog-form" title="Редактирование" style="height:auto !important; max-height:800px !important">
		<div id="container-form">
		  <form>
		    <fieldset>
		      <table id="editTable">
		      	<tr>
		      		<td><label for="isWork">Работает</label></td>
		      		<td><input type="checkbox" name="isWork" id="isWork"></td>
		      	</tr>
		      	<tr>
		      		<td><label for="fuelType">Тип топлива</label></td>
		      		<td><select name="fuelType" id="fuelType">
						<option value="Пропан">Пропан</option>
						<option value="Метан">Метан</option>
						<option value="Пропан + Метан">Пропан + Метан</option>
						<option value="Сервис">Сервис</option>
						</select>
					</td>
		      	</tr>
		      	<tr>
		      		<td><label for="title">Название</label></td>
		      		<td><input type="text" name="title" id="title"></td>
		      	</tr>
		      	<tr>
		      		<td><label for="pricePropan">Цена пропан</label></td>
		      		<td><input type="number" step="0.1" class="two-digits" name="pricePropan" id="pricePropan"></td>
		      	</tr>
		      	<tr>
		      		<td><label for="priceMetan">Цена метан</label></td>
		      		<td><input type="number" step="0.1" class="two-digits" name="priceMetan" id="priceMetan"></td>
		      	</tr>
		      	<tr>
		      		<td><label for="isCard">Прием банковских карт</label></td>
		      		<td><input type="checkbox" name="isCard" id="isCard"></td>
		      	</tr>
		      	<tr>
		      		<td><label for="worktime">График работы</label></td>
		      		<td><textarea rows="3" name="worktime"></textarea></td>
		      	</tr>
		      	<tr>
		      		<td><label for="address">Адрес</label></td>
		      		<td><textarea rows="3" name="address"></textarea></td>
		      	</tr>
		      	<tr>
		      		<td colspan=2><button type="button" onclick="showMap()">Показать на карте</button></td>
		      	</tr>
		      	<tr>
		      		<td><label for="id">ID</label></td>
		      		<td><input type="text" name="id" id="id" disabled="disabled"></td>
		      	</tr>
		      	<tr>
		      		<td><label for="rating">Рейтинг</label></td>
		      		<td><input type="text" name="rating" id="rating" disabled="disabled"></td>
		      	</tr>
		      	<tr>
		      		<td><label for="isAccept">Точка подверждена</label></td>
		      		<td><input type="checkbox" name="isAccept" id="isAccept"></td>
		      	</tr>
		      	<tr>
		      		<td><label for="lat">Широта</label></td>
		      		<td><input type="text" name="lat" id="lat"></td>
		      	</tr>
		      	<tr>
		      		<td><label for="lon">Долгота</label></td>
		      		<td><input type="text" name="lon" id="lon"></td>
		      	</tr>
		      	<tr>
		      		<td><label for="dateAdd">Дата добавления</label></td>
		      		<td><input type="text" name="dateAdd" id="dateAdd" disabled="disabled"></td>
		      	</tr>
		      	<tr>
		      		<td><label for="isOnMap">Отображается на карте</label></td>
		      		<td><input type="checkbox" name="isOnMap" id="isOnMap"></td>
		      	</tr>
		      </table>
		      	      
		      <!-- Allow form submission with keyboard without duplicating the dialog button -->
		      <input type="submit" tabindex="-1" style="position:absolute; top:-1000px">
		    </fieldset>
		  </form>
	  </div> <!-- container-form -->
	  <div id="container-changes">
		<table cellpadding="0" cellspacing="0" border="0" class="display" id="changes" width="100%" style="float:left;">
			<thead>
				<tr>
					<th>Дата</th>
					<th>Колонка</th>
					<th>Значение</th>
					<th style="min-width:60px"> </th>
				</tr>
			</thead>
			<tbody>
			</tbody>
		</table>
	</div> <!-- container-changes -->
</div>
	
		<p>
			<button onclick="addRow()">Добавить запись</button>
		</p>
		<div id="container" >
				<table cellpadding="0" cellspacing="0" border="0" class="display" id="points" width="100%">
					<thead>
						<tr>
							<th>Есть изменения</th>
							<th>ID</th>
							<th>Статус</th>
							<th>Тип топлива </th>
							<th>Название АГЗС</th>
							<th>Цена пропан</th>
							<th>Цена метан</th>
							<th>Рейтинг</th>
							<th>Дата последнего изменения</th>
							<th>Точка подтвер ждена</th>
							<th>Отображается на карте</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
					<tfoot>
						<tr>
							<th>Есть изменения</th>
							<th>ID</th>
							<th>Статус</th>
							<th>Тип топлива </th>
							<th>Название АГЗС</th>
							<th>Цена пропан</th>
							<th>Цена метан</th>
							<th>Рейтинг</th>
							<th>Дата последнего изменения</th>
							<th>Точка подтвер ждена</th>
							<th>Отображается на карте</th>
						</tr>
					</tfoot>
				</table>
			</div>
		</div>
	</body>
</html>
