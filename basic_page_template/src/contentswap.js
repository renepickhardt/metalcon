$(document).ready(function() {
		$(".small").click(function() {
			var tempClassForSwitching;
			var bigHTML = $("div.left-side").html();
			switch(this.id) {
				case 'small1': tempClassForSwitching = "div#small1"; break;
				case 'small2': tempClassForSwitching = "div#small2"; break;
				case 'small3': tempClassForSwitching = "div#small3"; break;
				case 'small4': tempClassForSwitching = "div#small4"; break;
				case 'small5': tempClassForSwitching = "div#small5"; break;
				case 'small6': tempClassForSwitching = "div#small6"; break;
				case 'small7': tempClassForSwitching = "div#small7"; break;
			}
			var smallHTML = $(tempClassForSwitching).html();
			$("div.left-side").animate({'left':'25%','height':'50', 'opacity':'0'}, 350, function() {
				$(this).css({'left':'0', 'height':'800', 'opacity':'1'});
			});
			$(tempClassForSwitching).animate({'opacity':'0'}, 350, function() {
				$(this).css({'opacity':'1'});
			});
			$("div.left-side").empty().html(smallHTML);
			$(tempClassForSwitching).empty().html(bigHTML);	
		});
});

//Second try, this time with static positions
/*$(window).load(function() {

	$(".small").click(function(event) {
		var switchHelper;
		switch(this.id) {
			case 'small1' : switchHelper = "div#small1"; break;
			case 'small2' : switchHelper = "div#small2"; break;
			case 'small3' : switchHelper = "div#small3"; break;
			case 'small4' : switchHelper = "div#small4"; break;
			case 'small5' : switchHelper = "div#small5"; break;
			case 'small6' : switchHelper = "div#small6"; break;
			case 'small7' : switchHelper = "div#small7"; break;
		}
	});
});
*/
/*
	$("div.small1").click(function() {
		var bigHTML = $("div.left-side").html();
		var smallHTML = $("div.small1").html();
		$("div.left-side").animate({'left':'25%','height':'50', 'opacity':'0'}, 350, function() {
			$(this).css({'left':'0', 'height':'800', 'opacity':'1'});
			$("div.left-side").empty().html(smallHTML);
			$("div.small1").empty().html(bigHTML);
		});
	});

	$("div.small2").click(function() {
		var bigHTML = $("div.left-side").html();
		var smallHTML = $("div.small2").html();
		$("div.left-side").animate({'left':'25%','height':'50', 'opacity':'0'}, 350, function() {
			$(this).css({'left':'0', 'height':'800', 'opacity':'1'});
			$("div.left-side").empty().html(smallHTML);
			$("div.small2").empty().html(bigHTML);
		});
	});

	$("div.small3").click(function() {
		var bigHTML = $("div.left-side").html();
		var smallHTML = $("div.small3").html();
		$("div.left-side").animate({'left':'25%','height':'50', 'opacity':'0'}, 350, function() {
			$(this).css({'left':'0', 'height':'800', 'opacity':'1'});
			$("div.left-side").empty().html(smallHTML);
			$("div.small3").empty().html(bigHTML);
		});
	});

	$("div.small4").click(function() {
		var bigHTML = $("div.left-side").html();
		var smallHTML = $("div.small4").html();
		$("div.left-side").animate({'left':'25%','height':'50', 'opacity':'0'}, 350, function() {
			$(this).css({'left':'0', 'height':'800', 'opacity':'1'});
			$("div.left-side").empty().html(smallHTML);
			$("div.small4").empty().html(bigHTML);
		});
	});

	$("div.small5").click(function() {
		var bigHTML = $("div.left-side").html();
		var smallHTML = $("div.small5").html();
		$("div.left-side").animate({'left':'25%','height':'50', 'opacity':'0'}, 350, function() {
			$(this).css({'left':'0', 'height':'800', 'opacity':'1'});
			$("div.left-side").empty().html(smallHTML);
			$("div.small5").empty().html(bigHTML);
		});
	});
*/
