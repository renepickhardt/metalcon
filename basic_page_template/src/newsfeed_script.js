$(function(){ //Here you can define the parameters for the newsfeed
    $("#message-display").metalconReader({
        user    : "user5",
        items   : 100,
        ownUpdates : 1
    });
});


jQuery.fn.metalconReader = function(options){
	return this.each(function(){
		var opts = options || {};
		opts.renderTo = this;
		renderer = new MetalconReader(opts);
	}); //End each function
}; //End Plugin
 
var MetalconReader = function(options){

    jQuery.extend(this,options || {});
    this.url = "http://localhost:8080/Graphity-Server-0.1/read?user_id="+this.user+"&poster_id="+this.user+"&num_items="+this.items+"&own_updates="+this.ownUpdates;
    //this.url = "http://192.168.178.36:8080/Graphity-Server-0.1/read?user_id="+this.user+"&poster_id="+this.user+"&num_items="+this.items+"&own_updates=1";
	if(this.user === "") throw "The 'user' property is required";
	if(this.renderTo === "") throw "The 'renderTo' property is required";
	this.read();
}; //End constructor
var user_name;
MetalconReader.prototype = {
	renderTo: "",
	user	: "",
	items	: 10,
	ownUpdates: 1,
	
	read	: function(){
		this.el = jQuery(this.renderTo);
		//this.el.append("<div class=\"metal-loading\"></div>");
		jQuery.ajax({
			url		: this.url,
			context	: this, 
		    success	: this.parse,
		}); //End AJAX call
	}, //End read function

	parse	: function(json_data){
		//this.el.empty();
		this.data = json_data.items;
		this.render(this.renderTo);
	}, //End parse function

	render	: function(element){
		var html = [];
		var that = this;
		html.push("<ul class=\"comments-holder\">");
		if (typeof this.data != 'undefined'){
			for(var i = 0; i < this.items && i < this.data.length;i++){
				var temp_date = that.createDate(this.data[i].published);
				user_name = this.data[i].actor.displayName;
				html.push("<li id=\""+i+"\" class=\"single-comment-holder\"><div class=\"user-img\"><a href=\""+this.data[i].actor.id+"\"><img src=\"http://www.metalcon.de/images/metal-community.jpg\" class=\"user-img-pic\"></a></div><div class=\"comment-body\"><h3 class=\"username-field\">"+this.data[i].actor.displayName+"</h3><div class=\"comment-date\"><a href=\""+this.data[i].object.id+"\">"+that.format(temp_date)+"</a></div><div class=\"comment-text\">"+this.data[i].object.message+"</div></div></li>");
			} //End for
		}//End if
		html.push("</ul>");
		this.el.append(html.join(""));
		$("div.comment-text").not(".link_box").shorten({"showChars": 400, "moreText" : "Mehr", "lessText" : "Weniger"});
	}, //End render function
	createDate	: function(str){
		str = str.substring(0,19).replace(/[ZT]/," ").replace(/\-/g,"/");
		return new Date(str);
	}, //End createDate function
	format		: function(date){
		var diff   = (((new Date()).getTime() - date.getTime()) / 1000),
			days   = Math.floor(diff / 86400),
			months = Math.floor(days / 31);
		if (isNaN(days) || days < 0)return date.toString();
		
		if(days == 0){ 
			if(diff < 60)return "Just now";
			if(diff < 120)return "1 minute ago";
			if(diff < 3600)return Math.floor( diff / 60 ) + " minutes ago";
			if(diff < 7200)return "1 hour ago";
			if(diff < 86400)return  Math.floor( diff / 3600 ) + " hours ago";
		}else if(days < 31){
			if(days == 1)return "Yesterday";
			if(days < 7)return days + " days ago";
			if(days < 31)return Math.ceil( days / 7 ) + " weeks ago";
		}else{
			if(months == 1)return "A month ago";
			if(months < 12)return Math.ceil( days / 31 ) + " months ago";
			if(months >=12)return Math.floor( days / 365 ) + " years ago";
		}//End else
	} //End format function	
}; //End Prototype




