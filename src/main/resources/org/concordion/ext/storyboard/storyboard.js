function showScreenPopup(src) {
	var img = document.getElementById('StoryCardScreenshotPopup');
	img.src = src.src

	var width = document.body.clientWidth * .60;
	if (width < 500) width = 550;
	if (width > 1000) width = 900;
	img.style.width = width + "px";
	img.style.height = 'auto';
	
	var srcRect = src.getBoundingClientRect();
	var imgRect = img.getBoundingClientRect();
	
	var scrollTop = Math.max(document.body.scrollTop, document.documentElement.scrollTop);
	var scrollHeight = Math.max(document.body.scrollHeight, document.documentElement.scrollHeight);
	var scrollLeft = Math.max(document.body.scrollLeft, document.documentElement.scrollLeft);
	
	 var posTop = srcRect.top - imgRect.height + scrollTop + 10;
     
	if (posTop < scrollTop) {
		posTop = scrollTop + srcRect.top + srcRect.height;

		if (posTop > scrollTop + (scrollHeight / 3)) {
	    	posTop = scrollTop;
			img.style.height = srcRect.top + "px";
			img.style.width = 'auto';
			imgRect = img.getBoundingClientRect();
		}		
	}

	var posLeft = srcRect.left + scrollLeft + 10;
	   
	if (posLeft + imgRect.width > scrollLeft + document.body.clientWidth) {
		posLeft = scrollLeft + document.body.clientWidth - imgRect.width;
	}
	if (posLeft < scrollLeft) {
		posLeft = scrollLeft;
	}

	img.style.left = posLeft + "px";
	img.style.top = posTop + "px";
	img.style.visibility = 'visible';	
}

function hideScreenPopup() {
	document.getElementById('StoryCardScreenshotPopup').style.visibility = 'hidden';
}

function showStoryboardSection(img, group, collapse, expand) {
	var display;
	
	if (getImageName(img.src) == getImageName(collapse)) {		
		img.src = expand;
		display = "none";
	} else {
		img.src = collapse;
		display = "inline-block";
	}
	
	var cards = document.getElementsByClassName(group);

	for (var i = 0; i < cards.length; i ++) {
		cards[i].style.display = display;
	}
} 

function getImageName(path) {
	var pos = path.lastIndexOf("/");

	if (pos > 0) {
		path = path.substring(pos + 1);
	}
	
	return path;	
}