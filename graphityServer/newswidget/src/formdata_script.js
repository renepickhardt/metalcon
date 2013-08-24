$(document).ready(function() {
	$("#submit_button").click(function(){
		var fd = new FormData();  
	
		var type = "status_update";
		var userid = "1";  
		var localDate = Date();
		var rand = "AB" + Math.round(Math.random() * 1000000);
		var message = document.getElementById("message").value;
		
		fd.append('type' ,type);
		fd.append('user_id' ,userid);
		fd.append('status_update_id' ,rand);
		fd.append('message' ,message);
		fd.append('status_update_type',"Plain");
		$.ajax({
		  url: 'http://localhost:8080/Graphity-Server-0.1/create',
		  data: fd,
		  headers: {'Origin': '*'},
		  processData: false,
		  cache: false,
		  contentType: false,
		  processData: false,
		  type: 'POST',
		  success: function(data){
		    if(data.statusMessage ==="ok"){
		    	renderForm(rand, localDate, userid, message);
		    } //End if
		  }, //End success function
		  error:function(){
		  	renderForm("1","2","3","4");
		  }
		}); //End AJAX

		function renderForm(id_link, date,id_actor, message){
			var stringOfValues = "<li><strong><a href=\""+id_link+"\">"+date+"</a></strong><br><span>"+id_actor+": "+message+"</span></li>";
			$("#newsstream").prepend(stringOfValues);
		} //End renderForm function
		//renderForm(userid, localDate, rand, message);
		//renderForm("1","2","3","4");
	}); //End click function
}); //End ready function
