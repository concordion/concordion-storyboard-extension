package demo;

import org.concordion.api.Scope;
import org.concordion.api.extension.Extension;
import org.concordion.ext.StoryboardExtension;
import org.concordion.ext.StoryboardExtension.AppendMode;
import org.concordion.ext.storyboard.CardResult;
import org.concordion.ext.storyboard.StockCardImage;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.ext.storyboard.DummyScreenshotTaker;

@RunWith(ConcordionRunner.class)
public class StoryboardDemo {

	@Extension
	public final StoryboardExtension storyboard = new StoryboardExtension()
						.setScreenshotTaker(new DummyScreenshotTaker(), Scope.SPECIFICATION)
						.setAppendMode(AppendMode.ItemsToExample);

	public boolean addCard(final String description) {
		String title = storyboard.getCurrentExampleTitle();
		
		if (title.isEmpty()) title = "Demo";
		
		//storyboard.addScreenshot("Try this", "hey");
		
		storyboard.addSectionBreak(title);
		storyboard.addNotification("Example", description, null, StockCardImage.TEXT, CardResult.SUCCESS);
//		storyboard.addSectionBreak("Inner");
		storyboard.addNotification("Example", description, null, StockCardImage.TEXT, CardResult.FAILURE);
//		storyboard.addSectionBreak("");
		storyboard.addSectionBreak("");
		
		return true;
	}

}
