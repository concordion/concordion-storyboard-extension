# Customisation

The Storyboard has a number of methods built in to customise the storyboard:


## setTitle()

> Set a custom title for the storyboard.
  

## addCardImage() 

> Add custom card image that will be shared by all tests
  		
> * Must be called before test starts otherwise this will do nothing
> * It is possible to override stock card images if you use the same name


## setAppendMode()

> Set how items are added to the storyboard:
	 
> * Append to end of Storyboard (original behaviour)
> * If item is added within currently executing Example Command append to example element, otherwise append to Storyboard **(default)**
> * If item is added within currently executing Example Command append to new section container in Storyboard, otherwise append to Storyboard
 	 

## setAddCardOnThrowable()

> Sets whether a card will be added to the storyboard when an uncaught exception occurs in the test.  If screenshotTaker is set then it will add a ScreenshotCard, else it will add a NotificationCard.
	
> _Defaults to true_


## setAddCardOnFailure()

> Sets whether a card will be added to the storyboard when a failure occurs in the test.  If screenshotTaker is set then it will add a ScreenshotCard, else it will add a NotificationCard.
	
> _Defaults to true_


## setSupressRepeatingFailures()

> If <code>setAddCardOnFailure(boolean)</code> is true then this will ignore second or subsequent failure.  If using the Example command then each example will be treated separately.


## setScreenshotTaker()

> Set a custom screenshot taker
	
> * By default this is not set to avoid capturing screen shots when not appropriate
> * The project includes <code>RobotScreenshotTaker</code> which will capture the full visible screen<br/>
    As an alternative, custom screenshots can be captured using the `ScreenshotTaker` interface. For example, when using frameworks
    such as Selenium, a custom screenshot taker can capture an image of the entire web page, even if the test is running in the
    background.  See the demo application for an example of a custom <code>SeleniumScreenshotTaker</code>.


## removeScreenshotTaker()
> Removes the screenshot taker so no further screenshots will be taken
	
> * If you are mixing GUI and non GUI actions in your storyboard you may want to remove the screenshot taker to prevent further screenshots being 
    taken for the current example to keep the cards in a logical order.  The need for this is dependent on the setting of <code>setTakeScreenshotOnExampleCompletion()</code>


## setTakeScreenshotOnExampleCompletion()

> A screenshot of the current page will be automatically added to the storyboard (as long as 
the screenshot taker has been set) when:
	
> * an example completes
> * a container is closed (either automatically or by calling closeContainer())

> If not using the example command then final screenshots must be added manually.

> This setting is also obeyed by containers that are configured to auto close such as the section container.
 
> _Defaults to true_


## skipFinalScreenshot()  
	
> If configured to take final screenshot for example, this will override that behaviour until:
	
> * an example completes
> * a container is closed (either automatically or by calling closeContainer())

> This also prevents screenshots being taken if a test fails or an exception is thrown.


## setAcceptCards()

> Prevent cards and containers from being added to the the storyboard. This is useful in situations where might be repeating an action 
  several times and only want to record the first pass through, or performing cleanup steps and don't want the actions recorded.
 	
> * Setting this to false will disable prevent cards and containers being added until either it is set to true, or the current example or specification completes

> _Defaults to true_
 	

# Advanced Customisation

* Create your own cards to capture and display different types of data by inheriting from the Card class
* Create your own containers to group cards by inheriting from the Container class
  