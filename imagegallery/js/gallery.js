var ImageGallery = (function($) {

	/* Returns a value or the defaultvalue if the value is null */
	var $nz = function(value, defaultvalue) {
		if( typeof (value) === undefined || value == null) {
			return defaultvalue;
		}
		return value;
	};
	
	/*
	 * Distribute a delta (integer value) to n items based on
	 * the size (width) of the items thumbnails.
	 */
	var calculateCutOff = function(len, delta, items) {
		// resulting distribution
		var cutoff = [];
		var cutsum = 0;

		// distribute the delta based on the proportion of
		// thumbnail size to length of all thumbnails.
		for(var i in items) {
			var item = items[i];
			var fractOfLen = item.twidth / len;
			cutoff[i] = Math.floor(fractOfLen * delta);
			cutsum += cutoff[i];
		}

		// still more pixel to distribute because of decimal
		// fractions that were omitted.
		var stillToCutOff = delta - cutsum;
		while(stillToCutOff > 0) {
			for(i in cutoff) {
				// distribute pixels evenly until done
				cutoff[i]++;
				stillToCutOff--;
				if (stillToCutOff == 0) break;
			}
		}
		return cutoff;
	};

	var calculateMiddle = function(item) {
		/**
		 * calculate the center in width of the image to place user portrait
		 * distracts the half size of the user image to center it
		 */
		var imageMargin = 12;
		var widthCenter = item / 2 - imageMargin;
		return widthCenter;
	}
	
	/**
	 * Takes images from the items array (removes them) as 
	 * long as they fit into a width of maxwidth pixels.
	 */
	var buildImageRow = function(maxwidth, items) {
		var row = [], len = 0;
		
		// each image a has a 1px margin, i.e. it takes 2px additional space
		var marginsOfImage = 2;

		// Build a row of images until longer than maxwidth
		while(items.length > 0 && len < maxwidth) {
			var item = items.shift();
			row.push(item);
			len += (item.twidth + marginsOfImage);
		}

		// calculate by how many pixels too long?
		var delta = len - maxwidth;

		// if the line is too long, make images smaller
		if(row.length > 0 && delta > 0) {

			// calculate the distribution to each image in the row
			var cutoff = calculateCutOff(len, delta, row);

			for(var i in row) {
				var pixelsToRemove = cutoff[i];
				item = row[i];

				// move the left border inwards by half the pixels
				item.vx = Math.floor(pixelsToRemove / 2);

				// shrink the width of the image by pixelsToRemove
				item.vwidth = item.twidth - pixelsToRemove;
			}
		} else {
			// all images fit in the row, set vx and vwidth
			for(var i in row) {
				item = row[i];
				item.vx = 0;
				item.vwidth = item.twidth;
			}
		}

		return row;
	};
	
	/**
	 * Creates a new thumbail in the image area. An attaches a fade in animation
	 * to the image. 
	 */
	var createImageElement = function(parent, item) {
		var imageContainer = $('<div class="imageContainer"/>');

		var overflow = $("<div/>");
		overflow.css("width", ""+$nz(item.vwidth, 120)+"px");
		overflow.css("height", "" + "190px");
		overflow.css("overflow", "hidden");

		var link = $('<a class="viewImageAction" href="' + item.previewURL + '"/>');
		
		var img = $("<img/>");
		img.attr("src" , ""+ item.previewURL);
		img.attr("title", item.title);
		img.css("width", "" + $nz(item.twidth, 120) + "px");
		img.css("height", "" + "190px");
		img.css("margin-left", "" + (item.vx ? (-item.vx) : 0) + "px");
		img.css("margin-top", "" + 0 + "px");
		img.hide();

		var userImage = $('<img class="userimg"/>');
		userImage.attr("src" , "" + item.uploader.profilePicture);
		userImage.css("width", "" + "30px");
		userImage.css("height", "" + "50px");
		userImage.css("margin-left", "" + calculateMiddle(item.twidth) + "px");

		img.append(userImage);
		link.append(img);
		overflow.append(link);
		imageContainer.append(overflow);

		// fade in the image after load
		img.bind("load", function () { 
			$(this).fadeIn(150); 
		});

		parent.find(".clearfix").before(imageContainer);
		item.el = imageContainer;
		return imageContainer;
	};
	
	/*
	 * Updates an exisiting thumbnail in the image area. 
	 */
	var updateImageElement = function(item) {
		var overflow = item.el.find("div:first");
		var img = overflow.find("img:first");

		overflow.css("width", "" + $nz(item.vwidth, 120) + "px");
		overflow.css("height", ""+ "190px");

		img.css("margin-left", "" + (item.vx ? (-item.vx) : 0) + "px");
		img.css("margin-top", "" + 0 + "px");

		userImage.css("margin-left", "" + calculateMiddle(item.twidth) + "px");
	};	
		
	return {
		
		showImages : function(imageContainer, realItems) {

			// reduce width by 1px due to layout problem in IE
			var containerWidth = imageContainer.width() - 1;
			
			// Make a copy of the array
			var items = realItems.slice();
		
			// calculate rows of images which each row fitting into
			// the specified windowWidth.
			var rows = [];
			while(items.length > 0) {
				rows.push(buildImageRow(containerWidth, items));
			}  

			for(var r in rows) {
				for(var i in rows[r]) {
					var item = rows[r][i];
					if(item.el) {
						// this image is already on the screen, update it
						updateImageElement(item);
					} else {
						// create this image
						createImageElement(imageContainer, item);
					}
				}
			}
		}

	}
})(jQuery);

$(document).ready(function() {
		
    var images = [{ "title": "Image 1",
	"previewURL": "http://farm9.staticflickr.com/8317/8044757536_4605611993_z.jpg",
    "twidth": 150,
    "imageURL": "", 
    "numComments": "", 
    "uploader": {
    	"name": "",
    	"profilePicture": "http://www.kulfoto.com/pic/0001/0018/t1/ElXFr17943.jpg",
    	"url": "", 
    	"imageURL": ""
    	}
    },
    { "title": "Image 2",
	"previewURL": "http://farm9.staticflickr.com/8309/8044747063_fcff2dae29_z.jpg",
    "twidth": 150,
    "imageURL": "", 
    "numComments": "", 
    "uploader": {
    	"name": "",
    	"profilePicture": "http://www.kulfoto.com/pic/0001/0018/t1/ElXFr17943.jpg",
    	"url": "", 
    	"imageURL": ""
    	}
    },
    { "title": "Image 3",
	"previewURL": "http://farm9.staticflickr.com/8040/8044746243_4e24814426_z.jpg",
    "twidth": 150,
    "imageURL": "", 
    "numComments": "", 
    "uploader": {
    	"name": "",
    	"profilePicture": "http://www.kulfoto.com/pic/0001/0018/t1/ElXFr17943.jpg",
    	"url": "", 
    	"imageURL": ""
    	}
    },
  	{ "title": "Image 4",
	"previewURL": "http://farm9.staticflickr.com/8036/8044746069_c4e5bdb162_z.jpg",
    "twidth": 150,
    "imageURL": "", 
    "numComments": "", 
    "uploader": {
    	"name": "",
    	"profilePicture": "http://www.kulfoto.com/pic/0001/0018/t1/ElXFr17943.jpg",
    	"url": "", 
    	"imageURL": ""
    	}
    },
    { "title": "Image 5",
	"previewURL": "http://farm9.staticflickr.com/8031/8044758106_a1da1c29e3_z.jpg",
    "twidth": 150,
    "imageURL": "", 
    "numComments": "", 
    "uploader": {
    	"name": "",
    	"profilePicture": "http://www.kulfoto.com/pic/0001/0018/t1/ElXFr17943.jpg",
    	"url": "", 
    	"imageURL": ""
    	}
    },
    { "title": "Image 6",
	"previewURL": "http://farm9.staticflickr.com/8451/8044754594_0ce2eb9f6c_c.jpg",
    "twidth": 180,
    "imageURL": "", 
    "numComments": "", 
    "uploader": {
    	"name": "",
    	"profilePicture": "http://www.kulfoto.com/pic/0001/0018/t1/ElXFr17943.jpg",
    	"url": "", 
    	"imageURL": ""
    	}
    },
    { "title": "Image 7",
	"previewURL": "http://farm9.staticflickr.com/8178/8044745529_2879f1cce0_z.jpg",
    "twidth": 150,
    "imageURL": "", 
    "numComments": "", 
    "uploader": {
    	"name": "",
    	"profilePicture": "http://www.kulfoto.com/pic/0001/0018/t1/ElXFr17943.jpg",
    	"url": "", 
    	"imageURL": ""
    	}
    },
    { "title": "Image 8",
	"previewURL": "http://farm9.staticflickr.com/8309/8044746703_a9b92ffba0_z.jpg",
    "twidth": 300,
    "imageURL": "", 
    "numComments": "", 
    "uploader": {
    	"name": "",
    	"profilePicture": "http://www.kulfoto.com/pic/0001/0018/t1/ElXFr17943.jpg",
    	"url": "", 
    	"imageURL": ""
    	}
    },
    { "title": "Image 9",
	"previewURL": "http://farm9.staticflickr.com/8042/8044746879_78722ae593_z.jpg",
    "twidth": 300,
    "imageURL": "", 
    "numComments": "", 
    "uploader": {
    	"name": "",
    	"profilePicture": "http://www.kulfoto.com/pic/0001/0018/t1/ElXFr17943.jpg",
    	"url": "", 
    	"imageURL": ""
    	}
    },
    { "title": "Image 10",
	"previewURL": "http://farm9.staticflickr.com/8458/8044756210_7ed3a91e6e_z.jpg",
    "twidth": 200,
    "imageURL": "", 
    "numComments": "", 
    "uploader": {
    	"name": "",
    	"profilePicture": "http://www.kulfoto.com/pic/0001/0018/t1/ElXFr17943.jpg",
    	"url": "", 
    	"imageURL": ""
    	}
    }, 
    { "title": "Image 11",
	"previewURL": "http://farm9.staticflickr.com/8450/8044750247_7ea348c6b7_z.jpg",
    "twidth": 200,
    "imageURL": "", 
    "numComments": "", 
    "uploader": {
    	"name": "",
    	"profilePicture": "http://www.kulfoto.com/pic/0001/0018/t1/ElXFr17943.jpg",
    	"url": "", 
    	"imageURL": ""
    	}
    },  
    { "title": "Image 12",
	"previewURL": "http://farm9.staticflickr.com/8450/8044754082_2b04ddf0e8_z.jpg",
    "twidth": 200,
    "imageURL": "", 
    "numComments": "", 
    "uploader": {
    	"name": "",
    	"profilePicture": "http://www.kulfoto.com/pic/0001/0018/t1/ElXFr17943.jpg",
    	"url": "", 
    	"imageURL": ""
    	}
    }, 
    { "title": "Image 13",
	"previewURL": "http://farm9.staticflickr.com/8459/8044755866_dfcfeaef7f_z.jpg",
    "twidth": 170,
    "imageURL": "", 
    "numComments": "", 
    "uploader": {
    	"name": "",
    	"profilePicture": "http://www.kulfoto.com/pic/0001/0018/t1/ElXFr17943.jpg",
    	"url": "", 
    	"imageURL": ""
    	}
    },
    { "title": "Image 14",
	"previewURL": "http://farm9.staticflickr.com/8037/8044747879_9476e4ef9f_z.jpg",
    "twidth": 170,
    "imageURL": "", 
    "numComments": "", 
    "uploader": {
    	"name": "",
    	"profilePicture": "http://www.kulfoto.com/pic/0001/0018/t1/ElXFr17943.jpg",
    	"url": "", 
    	"imageURL": ""
    	}
    },
   	{ "title": "Image 15",
	"previewURL": "http://farm9.staticflickr.com/8170/8044756868_6a93dd87d7_z.jpg",
    "twidth": 100,
    "imageURL": "", 
    "numComments": "", 
    "uploader": {
    	"name": "",
    	"profilePicture": "http://www.kulfoto.com/pic/0001/0018/t1/ElXFr17943.jpg",
    	"url": "", 
    	"imageURL": ""
    	}
    },
    { "title": "Image 16",
	"previewURL": "http://farm9.staticflickr.com/8032/8044757710_3ef3a8dfb7_z.jpg",
    "twidth": 150,
    "imageURL": "", 
    "numComments": "", 
    "uploader": {
    	"name": "",
    	"profilePicture": "http://www.kulfoto.com/pic/0001/0018/t1/ElXFr17943.jpg",
    	"url": "", 
    	"imageURL": ""
    	}
    },
    { "title": "Image 17",
	"previewURL": "http://farm9.staticflickr.com/8037/8044749175_a899cce585_z.jpg",
    "twidth": 160,
    "imageURL": "", 
    "numComments": "", 
    "uploader": {
    	"name": "",
    	"profilePicture": "http://www.kulfoto.com/pic/0001/0018/t1/ElXFr17943.jpg",
    	"url": "", 
    	"imageURL": ""
    	}
    },
    { "title": "Image 18",
	"previewURL": "http://farm9.staticflickr.com/8322/8044755704_9bea88f6d1_z.jpg",
    "twidth": 170,
    "imageURL": "", 
    "numComments": "", 
    "uploader": {
    	"name": "",
    	"profilePicture": "http://www.kulfoto.com/pic/0001/0018/t1/ElXFr17943.jpg",
    	"url": "", 
    	"imageURL": ""
    	}
    },
    { "title": "Image 19",
	"previewURL": "http://farm9.staticflickr.com/8175/8044756664_12422918a5_z.jpg",
    "twidth": 200,
    "imageURL": "", 
    "numComments": "", 
    "uploader": {
    	"name": "",
    	"profilePicture": "http://www.kulfoto.com/pic/0001/0018/t1/ElXFr17943.jpg",
    	"url": "", 
    	"imageURL": ""
    	}
    },
    { "title": "Image 20",
	"previewURL": "http://farm9.staticflickr.com/8450/8044750089_8ebcefb25c_z.jpg",
    "twidth": 300,
    "imageURL": "", 
    "numComments": "", 
    "uploader": {
    	"name": "",
    	"profilePicture": "http://www.kulfoto.com/pic/0001/0018/t1/ElXFr17943.jpg",
    	"url": "", 
    	"imageURL": ""
    	}
    }
     ];

     ImageGallery.showImages($("#imagearea"), images);
				
		$(window).resize(function() {
			// layout the images with new width
			ImageGallery.showImages($("#imagearea"), images);
	}); 

});

    /* $.getJSON('http-request', function(data) {
        var items = data.thumbs;
        ImageGallery.showImages($("#imagearea"), items);
        $(window).resize(function() {
            // layout the images with new width
            ImageGallery.showImages($("#imagearea"), items);
        });  
*/

//  could be used for loading aditional images on scrolling
//      $(window).scroll(function() {
//          if  ($(window).scrollTop() + $(window).height() > $(document).height() - 200){
//              
//          }
//      });
//});


