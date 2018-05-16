# TelegramBotFramework

This is my Telegram Bot Framework.

It can currently run multiple bots and loads modules from a nearby folder.
Modules can be added as seperate jar-files.
With this, you can have multiple people add their own functionality to a bot.
In the future, this framework should make it easier to share a server to run bots around the clock.
I hope this will enable many programmers to run their bots on the same server to save computing resoucres.

### Intended workflow:
 - Clone the [template module](https://github.com/Simon70/telegrambots.framework.TemplateModule)
 - Add you bot's api-key or request access to someone else's bot
 - Test module via the integrated [TestRunner](https://github.com/AnyTimeTraveler/telegrambots.framework.TemplateModule/blob/master/src/test/java/org/simonscode/telegrambots/framework/modules/BotRunner.java)
 - Create account via telegram

    If you are hosting the framework yourself:

    - Download the jar from maven
    - Run the jar

 - Upload your module via Telegram 
 - Add module to bot via Telegram
 - Start the bot via Telegram

##In progress:
 - Managed Menus
 - AdminModule
 - Module upload via Telegram

## TODO:
 - Finalize statefulness system (Currently reliant on JSON Serialisation)
 - Implement database (probably sqlite) backend for larger datasets
 - Implement global permission-system

##Done:
 - Upload to maven central
 - Make modules loadable during runtime (not just when the bot starts)
 - Implement universal help function
 - Implement uploading of new modules through telegram
 - Make modules stateful
 - Implement modules
 - Run multiple bots at the same time

## Contributing

Simple:

1. Fork/Clone this repository
2. Implement your changes
3. Submit a pull request

## Modules that are already available (outdated due to structural changes)

### ~~NanoWriMoTracker~~

Tracks and compares the progress https://nanowrimo.org/

URL: https://github.com/Simon70/telegrambots.framework.NaNoWriMoTracker

### Template Module

A good basis to start your module from

https://github.com/Simon70/telegrambots.framework.TemplateModule
