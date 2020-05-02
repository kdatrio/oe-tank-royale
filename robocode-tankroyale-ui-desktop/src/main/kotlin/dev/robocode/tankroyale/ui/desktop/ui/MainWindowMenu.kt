package dev.robocode.tankroyale.ui.desktop.ui

import dev.robocode.tankroyale.ui.desktop.extensions.JMenuExt.addNewMenuItem
import dev.robocode.tankroyale.ui.desktop.ui.ResourceBundles.MENU
import dev.robocode.tankroyale.ui.desktop.util.Event
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem

object MainWindowMenu : JMenuBar() {

    // Public events
    val onSelectBots = Event<JMenuItem>()
    val onBootUpBots = Event<JMenuItem>()
    val onSetupRules = Event<JMenuItem>()
    val onShowServerLog = Event<JMenuItem>()
    val onServerConfig = Event<JMenuItem>()
    val onBotDirConfig = Event<JMenuItem>()

    init {
        add(JMenu(MENU.get("menu.battle")).apply {
            addNewMenuItem("item.select_bots", onSelectBots)
            addNewMenuItem("item.boot_up_bots", onBootUpBots)
            addSeparator()
            addNewMenuItem("item.setup_rules", onSetupRules)
        })
        add(JMenu(MENU.get("menu.server")).apply {
            addNewMenuItem("item.show_server_log", onShowServerLog)
            addNewMenuItem("item.select_server", onServerConfig)
        })
        add(JMenu(MENU.get("menu.config")).apply {
            addNewMenuItem("item.bot_dir_config", onBotDirConfig)
        })
    }
}
