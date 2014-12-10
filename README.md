[![Build Status](https://travis-ci.org/concordion/concordion-storyboard-extension.svg?branch=master)](https://travis-ci.org/concordion/concordion-storyboard-extension)

This [Concordion](http://www.concordion.org) extension provides the capability to embed a series of 'cards' containing screenshots or data (text, xml, or html) in the output specification.

The [demo project](http://github.com/concordion/concordion-storyboard-extension-demo) demonstrates this extension.

# Introduction

Often its difficult to tell from a specification exactly what is being done to the system under test.  This extension solves that dilema by capturing information (screen shots, soap xml, etc) from the system under test and displaying them as a series of cards on a storyboard at the bottom of the specification while it is being executed.

Supports:
* browser screen shots
* text data (eg soap messages)
* html data (eg email content)
* catches displays exceptions and test failures 
* can group cards into collapsible sections on the storyboard 

This extension is based on the screenshot taker extension and would not be here without it.

# Usage

Further documentation can be found in the [wiki](https://github.com/concordion/concordion-storyboard-extension/wiki).

# Further info

* [Wiki](https://github.com/concordion/concordion-storyboard-extension/wiki)
* [API](http://concordion.github.io/concordion-storyboard-extension/api/index.html)
* [Demo project](http://github.com/concordion/concordion-storyboard-extension-demo)

# Acknowledgements

This extension was partly inspired by Nigel Charman's [Screenshot Extension](https://github.com/concordion/concordion-screenshot-extension).