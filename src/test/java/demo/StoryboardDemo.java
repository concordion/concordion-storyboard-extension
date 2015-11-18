package demo;

import org.concordion.api.extension.Extension;
import org.concordion.ext.StoryboardExtension;
import org.concordion.ext.StoryboardExtension.AppendMode;
import org.concordion.ext.storyboard.CardResult;
import org.concordion.ext.storyboard.StockCardImage;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class StoryboardDemo {

	@Extension
	public final StoryboardExtension storyboard = new StoryboardExtension().setScreenshotTaker(null)
		.setAppendMode(AppendMode.ItemsToExample);

	public boolean addCard(final String description) {
		if (storyboard.getCurrentExampleTitle().isEmpty()) {
			storyboard.addSectionBreak("Demo");
		}
		
		storyboard.addNotification("Example", description, null, StockCardImage.TEXT, CardResult.SUCCESS);
		return true;
	}

}
