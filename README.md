# Important Announcement
**I am no longer going to be supporting Pickaxe after this version.** There is now an [official Ore client](https://github.com/SpongePowered/OrePlugin).

Ore is also changing to a new website. It is currently down for migration, so **Pickaxe does not currently work.** After it comes back up at the new location, **I cannot guarantee Pickaxe will begin working again.**

Pickaxe does not store any data, so migration should be seamless. Note, however, that because the Ore website is down, the official plugin cannot currently be downloaded.

Thanks for using Pickaxe, and here's to a great future with a standard, official Ore plugin!

## Old description
# ![](https://github.com/JBYoshi/Pickaxe/raw/master/pickaxe.png) Pickaxe
Pickaxe is a [Sponge](https://www.spongepowered.org) plugin manager and unofficial [Ore](https://ore-staging.spongepowered.org) client.

## Commands
`/pickaxe install <plugin>`: `<plugin>` should be either the ID of a plugin or the Ore name (you can find it in the top-left corner of the project's page on Ore; it looks like `<owner>/<name>`, e.g. `JBYoshi/Pickaxe`). You will need to restart the server to finish installing the plugin.

`/pickaxe update [plugin...]`: `<plugin>` is optional and can be repeated. It should be the same as in `/pickaxe install`. If not present, it will update all plugins. You will need to restart the server to finish updating the plugins.

`/pickaxe uninstall <plugin>`:
`<plugin>` should be the same as in `/pickaxe install`. You will need to restart the server to finish uninstalling the plugin.

To confirm any of the commands above, you will need to type `/pickaxe yes`.

`/pickaxe status`: Displays the changes that will be applied when the server restarts.
