# Logging Extension Integration

Assuming that the StoryboardLogListener has been registered with the logging extension 

    @Extension
    public StoryboardExtension storyboard = new StoryboardExtension();
    
    @Extension
    public LoggingFormatterExtension logging = new LoggingFormatterExtension()
    		.registerListener(new StoryboardLogListener(storyboard));

Then the storyboard is able to reuse the following items from the log file:

* [Screenshots](- "c:assertTrue=screenshot()")  
* [Attachments](- "c:assertTrue=attachment()") 
* Screenshot taken for [Exceptions](- "c:assertTrue=exception()")
* Screenshot taken for [Failures](- "c:assertTrue=failure()")