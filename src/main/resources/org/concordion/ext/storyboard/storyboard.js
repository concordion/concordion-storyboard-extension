function showScreenPopup(src) {
	var img = document.getElementById('StoryCardScreenshotPopup');
	img.src = src.src
	
	var scrollTop = Math.max(document.body.scrollTop, document.documentElement.scrollTop);
	var scrollLeft = Math.max(document.body.scrollLeft, document.documentElement.scrollLeft);
	var viewportWidth = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
	var viewportHeight = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;
	var scrollbarWidth = viewportWidth - document.body.offsetWidth;
	
	var srcRect = getSourceImageContainer(src).getBoundingClientRect();
	var imgRect = img.getBoundingClientRect();
	var naturalWidth = (img.naturalWidth || 0);
	var naturalHeight = (img.naturalHeight || 0);

	var above = srcRect.top > (viewportHeight - srcRect.top - srcRect.height);
	var posTop = 0;
	var posLeft = 0;
	
	if (above) {
		var height = Math.min(naturalHeight, srcRect.top - 10)
		posTop = scrollTop + srcRect.top - height;
		
		img.style.height = height + "px";
		img.style.width = 'auto';
		
		if (img.width > Math.min(naturalWidth, viewportWidth)) {
			img.style.height = 'auto';
			img.style.width = Math.min(naturalWidth, viewportWidth) + "px";
			
			posTop = scrollTop + srcRect.top - img.height;
		}
	} else {
		var height = Math.min(naturalHeight, viewportHeight - srcRect.top - srcRect.height - 10)
		posTop = scrollTop + srcRect.top + srcRect.height;
		
		img.style.height = height + "px";
		img.style.width = 'auto';
		
		if (img.width > Math.min(naturalWidth, viewportWidth)) {
			img.style.height = 'auto';
			img.style.width = Math.min(naturalWidth, viewportWidth) + "px";
		}
	}

	var posLeft = srcRect.left + scrollLeft + 1;
	   
	if (posLeft + img.width > scrollLeft + viewportWidth - scrollbarWidth) {
		posLeft = scrollLeft + viewportWidth - img.width - scrollbarWidth;
	}
	if (posLeft < scrollLeft) {
		posLeft = scrollLeft;
	}

	img.style.left = posLeft + "px";
	img.style.top = posTop + "px";
	img.style.visibility = 'visible';	
}

function getSourceImageContainer(src) {
	var node=src;
	
	while (node.offsetParent) {
		node=node.offsetParent;

		if (node.className.indexOf("scimgcontainer") >= 0) {
			return node;
		}
		/*
		var classList = node.classList || node.className;
		
		for (i = 0; i < classList.length; i++) { 
			if (classList[i] === "scimgcontainer") {
				return node;
			}
		}
		*/
	}
	
	return node;
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