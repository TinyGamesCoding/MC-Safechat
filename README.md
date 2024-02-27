# Roblox Safechat

This is a Minecraft mod that adds Roblox's old safechat feature, from 2007-2014, as a GUI in the chat menu!

### Features
- A safechat button, which opens the GUI, with a bunch of premade messages.
- A settings icon, which allows you to customize the mod to your liking!

### Settings
This is a list of all the settings that you are able to change. These are:
- Scale - Sets the size of the message buttons, from 0.4 to 1.2.
- Instantly send - Toggles if clicking the message button will instantly send the message.
- Flip groups - Flips the order of the message groups, in the config.
- Show arrows next to groups - Shows < as an indicator for message buttons that are groups.
- Groups are also texts - Toggles being able to select message button groups as actual messages.
- Close after sending - Closes the safechat UI after selecting a button.
- Tooltip text scale - Sets the size that the tooltip can show for small message texts.

### Config
There are some config options included with the settings. These can be accessed in the settings.

- Open config folder - Opens the config folder for the mod.
- Open messages file - Opens the messages file, and allows you to add, delete, and edit messages!
- Reload config & messages - Reloads the messages and config settings.

### Message Editing
Normal messages are written like this:

{
"type": "chat",
"chat": ""
}
- Do not change the type, it will mess stuff up.
- Write whatever you want in the "" in chat to write your message.
- Add a comma if there is another chat message in the messages array, at the }.

Group messages are written like this:

{
"type": "group",
"name": "Questions",
"array": []
}

- Again, do not change the type, it will mess stuff up.
- Write whatever you want in the "" in name to be the name of the group.
- Put the list of messages inside the [], inside the array, to put the messages inside that group.
- Add a comma if there is another chat message or group after it, at the }.
