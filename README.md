# ![](https://github.com/JBYoshi/Pickaxe/raw/master/pickaxe.png) Pickaxe
Pickaxe is a [Sponge](https://www.spongepowered.org) plugin manager and unofficial [Ore](https://ore-staging.spongepowered.org) client.

## Disclaimer
At the moment, Ore is having some problems with files (see [SpongePowered/Ore#136](https://github.com/SpongePowered/Ore/issues/136)), so this plugin may not work as expected with certain plugins.

## Downloads
First-time download: [Download on Ore](https://ore-staging.spongepowered.org/JBYoshi/Pickaxe)

To update: `/pickaxe update pickaxe` or `/pickaxe update JBYoshi/Pickaxe`

[Backup downloads](https://github.com/JBYoshi/Pickaxe/releases)

## Commands
`/pickaxe install <plugin>`: `<plugin>` should be either the ID of a plugin or the Ore name (you can find it in the top-left corner of the project's page on Ore; it looks like `<owner>/<name>`, e.g. `JBYoshi/Pickaxe`). You will need to restart the server to finish installing the plugin.

`/pickaxe update [plugin...]`: `<plugin>` is optional and can be repeated. It should be the same as in `/pickaxe install`. If not present, it will update all plugins. You will need to restart the server to finish updating the plugins.

`/pickaxe uninstall <plugin>`:
`<plugin>` should be the same as in `/pickaxe install`. You will need to restart the server to finish uninstalling the plugin.

To confirm any of the commands above, you will need to type `/pickaxe yes`.

`/pickaxe status`: Displays the changes that will be applied when the server restarts.
