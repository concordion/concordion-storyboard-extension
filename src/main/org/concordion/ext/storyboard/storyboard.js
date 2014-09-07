function showScreenPopup(src, id) {
	var img = document.getElementById(id);

	var width = document.body.clientWidth * .60;
	if (width < 500) width = 550;
	if (width > 1000) width = 900;
	img.style.width = width;
	
	var srcRect = src.getBoundingClientRect();
	var imgRect = img.getBoundingClientRect();
	
	var posLeft = srcRect.left + 10 + document.body.scrollLeft;
    var posTop = srcRect.top - imgRect.height + document.body.scrollTop + 10;
	
	if (posLeft + imgRect.width > document.body.clientWidth) {
		posLeft = document.body.clientWidth - imgRect.width;
	}
	if (posLeft < 0) {
		posLeft = 0;
	}

	img.style.left = posLeft;
	img.style.top = posTop;
	img.style.visibility = 'visible';	
}

function hideScreenPopup(id) {
	document.getElementById(id).style.visibility = 'hidden';
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