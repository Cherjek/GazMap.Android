var TABLE_NAME = "#points";
var PATH = "points/editor/";
var table = null;
var changesTable = null;

$(document).ready(function() {
	makeTable();
	
	function updateData() {
		var entry = {"0": $("#editTable").find("#isAccept").prop("checked") ? "Да" : "Нет", 
		"1": $("#editTable").find("#isWork").prop("checked") ? "Работает" : "Не работает",
		"2": $("#editTable").find("#fuelType").val(),
		"3": notEmpty($("#editTable").find("#title").val()),
		"4": notEmpty($("textarea[name=address]").val()),
		"5": notEmpty($("textarea[name=worktime]").val()),
		"6": $("#editTable").find("#isCard").prop("checked") ? "Да" : "Нет",
		"7": notEmpty($("#editTable").find("#lat").val()),
		"8": notEmpty($("#editTable").find("#lon").val()),
		"9": $("#editTable").find("#id").val(),
		"10": $("#editTable").find("#dateAdd").val(),
		"11": $("#editTable").find("#rating").val(),
		"12": $("#editTable").find("#pricePropan").val(),
		"13": $("#editTable").find("#priceMetan").val(),
		"14": $("#editTable").find("#isOnMap").prop("checked") ? "Да" : "Нет",
		"DT_RowId": $("#editTable").find("#id").val(),
		"changes": 0
		};
		
		$.ajax({
			type: "post",
			url : PATH + "save",
		    data: JSON.stringify(entry),
		    dataType: "json",
		    contentType: "application/json;charset=utf-8",
			success : function(data) {
				dialog.dialog( "close" );
				changesTable.fnClearTable();
				table.fnDestroy();
				makeTable();
			},
			error : function(){
				alert("Ошибка соединения с сервером");
			}
		});
		
	}
	
	function notEmpty(str) {
		if (str) {
			return str;
		}
	    return null;
	}
	
	var dialog = $( "#dialog-form" ).dialog({
	    autoOpen: false,
	    height: 800,
	    width: 900,
	    modal: true,
	    buttons: {
	      "Сохранить": function() {
	    	  updateData();
	      },
	      "Отмена": function() {
	    	  dialog.dialog( "close" );
	    	  changesTable.fnClearTable();
	      }
	    },
	    close: function() {
	    	changesTable.fnClearTable();
	      form[ 0 ].reset();
	    }
	  });

	  var form = dialog.find( "form" ).on( "submit", function( event ) {
	    event.preventDefault();
	    updateData();
	  });
	  
	  
	  $(TABLE_NAME).on( 'click', 'tr', function ( event ) {
		  event.preventDefault();
		  var data = table.fnGetData(this);
		  if (data == null) {
			  return;
		  }
		  
		  $.ajax({
				url : PATH + "get/point?id=" + data.DT_RowId,
				dataType: 'json',
				success : function(data) {
					updateFormData(data);
					updateChangesData(data.DT_RowId);
					dialog.dialog( "open" );
				},
				error : function(){
					alert("Ошибка соединения с сервером");
					return;
				}
			});		  
	    } );
});

function updateFormData(data) {
	$("#editTable").find("#isWork").prop('checked', data[1] == 'Работает' ? true : false);
	$("#editTable").find("#fuelType").val(data[2]);
	$("#editTable").find("#title").val(data[3]);
	$("#editTable").find("#pricePropan").val(data[12]);
	$("#editTable").find("#priceMetan").val(data[13]);
	$("#editTable").find("#isCard").prop('checked', data[6] == 'Да' ? true : false);
	$("textarea[name=worktime]").val(data[5]);
	$("textarea[name=address]").val(data[4]);
	$("#editTable").find("#id").val(data[9]);
	$("#editTable").find("#rating").val(data[11]);
	$("#editTable").find("#isAccept").prop('checked', data[0] == 'Да' ? true : false);
	$("#editTable").find("#lat").val(data[7]);
	$("#editTable").find("#lon").val(data[8]);
	$("#editTable").find("#dateAdd").val(data[10]);
	$("#editTable").find("#isOnMap").prop('checked', data[14] == 'Да' ? true : false);
	
	$('.two-digits').keypress( function(e) {
		var val = $(this).val();
	    var chr = String.fromCharCode(e.which);	    
	    return /^\d*\.?\d{0,2}$/.test(val + chr);
	});
}

function updateChangesData(id) {
	changesTable = $('#changes').dataTable();
	
	$.ajax({
		url : PATH + "get/changes?id=" + id,
		dataType: 'json',
		success : function(data) {
			for(var key in data) {
				var obj = data[key];
				var date = dateToString(new Date(obj["created"]));
				var buttons = "<button style='button-changes' onclick='changeAccepted(" + id + ", " + obj["id"] + ")'>+</button><button style='button-changes' onclick='changeCancel(" + id + ", " + obj["id"] + ")'>-</button>";
				changesTable.fnAddData([date, column(obj["fieldId"]), obj["text"], buttons]);
			}
		},
		error : function(){
			$('#changes').prop("visibility", "hidden");
		}
	});	  
}

function changeAccepted(rowId, id) {
	$("button").attr('disabled','disabled');
	
	var obj = {"id": id};
	
	$.ajax({
		type: "POST",
		url : PATH + "apply",
	    data: JSON.stringify(obj),
	    contentType: "application/json; charset=utf-8",
		success : function(data) {
			$("button").removeAttr('disabled');
			updateFormData(data);
			changesTable.fnClearTable();
			updateChangesData(rowId);
		},
		error : function(){
			$("button").removeAttr('disabled');
			alert("Ошибка соединения с сервером");
		}
	});
}

function changeCancel(rowId, id) {
	$("button").attr('disabled','disabled');
	
	var obj = {"id": id};
	
	$.ajax({
		type: "POST",
		url : PATH + "cancel",
	    data: JSON.stringify(obj),
	    contentType: "application/json; charset=utf-8",
		success : function(data) {
			$("button").removeAttr('disabled');
			//updateFormData(data);
			changesTable.fnClearTable();
			updateChangesData(rowId);
		},
		error : function(){
			$("button").removeAttr('disabled');
			alert("Ошибка соединения с сервером");
		}
	});
}

function dateToString(date) {
	return two(date.getDate()) + "." + two(date.getMonth()) + "." + (date.getYear() + 1900) + " " + two(date.getHours()) + ":" + two(date.getMinutes());
}
function two(a) {
	return ("0" + a).slice(-2);
}
function column(index) {	
	 if (index == 0) return "Название";
	 if (index == 1) return "Адрес";
	 if (index == 2) return "Широта";
	 if (index == 3) return "Долгота";
	 if (index == 4) return "Цена пропан";
	 if (index == 5) return "Цена метан";
	 if (index == 6) return "График работы";
	 if (index == 7) return "Тип топлива";
	 if (index == 8) return "Статус";
	 if (index == 9) return "Прием банковских карт";
	 return "";
}

function makeTable(){
	table = $(TABLE_NAME).dataTable({
		"aoColumns": [
		          { "sWidth": "3%", "sClass": "icon" },					//has changes Icon
	              { "sWidth": "5%", "sClass": "regular" },				//id
	              { "sWidth": "7%", "sClass": "point-state" },			//state
	              { "sWidth": "10%", "sClass": "gas-type" },			//gas type
	              { "sWidth": "30%", "sClass": "regular"},          	//name
	            //  { "sWidth": "500px", "sClass": "regular"},          //address
	            //  { "sWidth": "200px", "sClass": "regular"},          //worktime
	            //  { "sWidth": "120px", "sClass": "logical" },         //accept cards
	            //  { "sWidth": "120px", "sClass": "regular"},          //latitude
	            //  { "sWidth": "120px", "sClass": "regular"},          //longitude

	            //  { "sWidth": "200px", "sClass": "not-edited" },		//creation date
	              { "sWidth": "3%", "sClass": "regular" },				//price propan
	              { "sWidth": "3%", "sClass": "regular" },				//price metan
	              { "sWidth": "7%", "sClass": "not-edited" },			//rating
	              { "sWidth": "7%", "sClass": "not-edited" },			//modified
	              { "sWidth": "5%", "sClass": "logical" },				//accepted
	              { "sWidth": "7%", "sClass": "logical" }				//deleted
	          ],
		"bAutoWidth" : true,
		"bProcessing": true,
        "sAjaxSource": PATH + 'get/list',
        "fnDrawCallback": function(){
        	if (table != null) {
				table.find('tr').each(function (rowIndex, tr) {
        	        var data = table.fnGetData(this);
        	        if (data != null && data["changes"] > 0) {
        	        	tr.firstChild.innerHTML = "<img src='/gazstation/resources/images/new.png'/>";
        	        }
        	    });
        	}
         }
	});	
}

function addRow() {
	$.ajax({
		url : PATH + "add",
		dataType: 'json',
		success : function(data) {
			var row = $(TABLE_NAME).dataTable().fnAddData( data);
			var tr = table.fnSettings().aoData[ row[0] ].nTr;
			
			tr.setAttribute("id", data["DT_RowId"]);
		},
		error : function(){
			alert("Не удалось добавить новую точку");
		}
	});
}

function showMap() {
	var lat = $("#editTable").find("#lat").val();
	var lon = $("#editTable").find("#lon").val();
	var address = $("textarea[name=address]").val();
	if (lat && lon) {
		window.open("http://maps.yandex.ru/?l=sat&text=" + lat + "%20" + lon);
		return;
	}
	if (address) {
		window.open("http://maps.yandex.ru/?l=sat&text=" + address);
		return;
	}
	window.open("http://maps.yandex.ru?l=sat");
}
