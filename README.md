# ![](https://github.com/JBYoshi/Pickaxe/raw/master/pickaxe.png) Pickaxe
Pickaxe is a [Sponge](https://www.spongepowered.org) plugin manager and unofficial [Ore](https://ore-staging.spongepowered.org) client.

## Disclaimer
At the moment, Ore is having some problems with files (see Ore issues [#134](https://github.com/SpongePowered/Ore/issues/134) and [#136](https://github.com/SpongePowered/Ore/issues/136)), so this plugin may not work as expected.

## Downloads
Because of the current file issues with Ore, downloads are currently hosted on [GitHub](https://github.com/JBYoshi/Pickaxe/releases). They will be posted on Ore as soon as the issues are fixed.

## Commands
`/pickaxe install <plugin>`
`<plugin>` should be either the ID of a plugin or the Ore name (you can find it in the top-left corner of the project's page on Ore; it looks like `<owner>/<name>`, e.g. `JBYoshi/Pickaxe`). You will need to restart the server to finish installing the plugin.

`/pickaxe update [plugin...]`
`<plugin>` is optional and can be repeated. It should be the same as in `/pickaxe install`. If not present, it will update all plugins. You will need to restart the server to finish updating the plugins.

`/pickaxe uninstall <plugin>`
`<plugin>` should be the same as in `/pickaxe install`. You will need to restart the server to finish uninstalling the plugin.

To confirm any of the commands above, you will need to type `/pickaxe yes`.

`/pickaxe status`
Displays the changes that will be applied when the server restarts.
