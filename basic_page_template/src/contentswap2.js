$(document).ready(function() {
	$("#small7").css('display', 'none');
	var switchHelper;
	var firstTime = true;
	var brain;
	$(".small").click(function() {
		var offset = $(this).offset();
		var pusher = offset.top - 54;
		if (firstTime) {
			$("div.left-side").children().clone().prependTo('#small7');
			$("#small7").css('display', 'block');
			firstTime = false;
		}

		switch(brain) {
			case 1 : $("div#small1").animate({"height": "129"}, 500); break;
			case 2 : $("div#small2").animate({"height": "129"}, 500); break;
			case 3 : $("div#small3").animate({"height": "129"}, 500); break;
			case 4 : $("div#small4").animate({"height": "129"}, 500); break;
			case 5 : $("div#small5").animate({"height": "129"}, 500); break;
			case 6 : $("div#small6").animate({"height": "129"}, 500); break;
			case 7 : $("div#small7").animate({"height": "129"}, 500); break;
		}	
		switch(this.id) {
			case 'small1': switchHelper = "div#small1"; brain = 1; $(switchHelper).animate({"height": "0"}, 500); break;
			case 'small2': switchHelper = "div#small2"; brain = 2; $(switchHelper).animate({"height": "0"}, 500); break;
			case 'small3': switchHelper = "div#small3"; brain = 3; $(switchHelper).animate({"height": "0"}, 500); break;
			case 'small4': switchHelper = "div#small4"; brain = 4; $(switchHelper).animate({"height": "0"}, 500); break;
			case 'small5': switchHelper = "div#small5"; brain = 5; $(switchHelper).animate({"height": "0"}, 500); break;
			case 'small6': switchHelper = "div#small6"; brain = 6; $(switchHelper).animate({"height": "0"}, 500); break;
			case 'small7': switchHelper = "div#small7"; brain = 7; $(switchHelper).animate({"height": "0"}, 500); break;

		}
		
		//from small to big
		$("div.left-side").css({'transform': 'translate3d(0, 0, 0)','height': '129', 'left': '40%', 'top':  pusher, 'opacity': '0'});
		$("div.left-side").animate({'left': '0%','top': '0', 'height': '800', 'opacity': '1'}, 500);
		$("div.left-side").empty().html($(switchHelper).html());
		
		/*
		//from big to small
		$("div.left-side").animate({'top': pusher,'left':'42%','height':'129', 'opacity':'0'}, 600, function() {
			$(this).css({'top':'0','left':'0', 'height':'800', 'opacity':'1'});
			$("div.left-side").empty().html($(switchHelper).html());
		});
		*/
	});	
});