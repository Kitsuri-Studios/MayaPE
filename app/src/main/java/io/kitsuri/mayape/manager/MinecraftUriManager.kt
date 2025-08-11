package io.kitsuri.mayape.manager

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import java.util.UUID

/**
 * Utility object for launching Minecraft URI protocol links with individual functions
 */
object MinecraftUriManager {

    /**
     * Shows the how to play screen
     * @param context Application or Activity context
     */
    fun showHowToPlayScreen(context: Context) {
        launchUri(context, "minecraft://?showHowToPlayScreen=1")
    }

    /**
     * Launches Oculus (VR) mode
     * @param context Application or Activity context
     */
    fun launchOculus(context: Context) {
        launchUri(context, "minecraft://oculus_launched")
    }

    /**
     * Adds an external server to the server list
     * @param context Application or Activity context
     * @param serverName Name of the server
     * @param ip Server IP address
     * @param port Server port (default: 19132)
     */
    fun addExternalServer(context: Context, serverName: String, ip: String, port: Int = 19132): Boolean {
        if (port <= 0 || port > 65535) {
            return false
        }
        launchUri(context, "minecraft://?addExternalServer=$serverName|$ip:$port")
        return true
    }

    /**
     * Shows a marketplace item
     * @param context Application or Activity context
     * @param item UUID of the item
     */
    fun showStoreOffer(context: Context, item: String): Boolean {
        if (!isValidUUID(item)) {
            return false
        }
        launchUri(context, "minecraft://?showStoreOffer=$item")
        return true
    }

    /**
     * Shows a marketplace collection or page
     * @param context Application or Activity context
     * @param pageId Document or page ID
     */
    fun showOfferCollection(context: Context, pageId: String) {
        launchUri(context, "minecraft://?showOfferCollection=$pageId")
    }

    /**
     * Shows the marketplace home screen
     * @param context Application or Activity context
     */
    fun showStoreHomeScreen(context: Context) {
        launchUri(context, "minecraft://?showStoreHomeScreen=1")
    }

    /**
     * Opens the marketplace store, optionally with a specific offer
     * @param context Application or Activity context
     * @param item Optional UUID of an item to show
     */
    fun openStore(context: Context, item: String? = null): Boolean {
        item?.let {
            if (!isValidUUID(it)) {
                return false
            }
            launchUri(context, "minecraft://openStore/?showStoreOffer=$it")
        } ?: launchUri(context, "minecraft://openStore")
        return true
    }

    /**
     * Shows the Minecoin purchase modal
     * @param context Application or Activity context
     */
    fun showMineCoinOffers(context: Context) {
        launchUri(context, "minecraft://?showMineCoinOffers=1")
    }

    /**
     * Opens the marketplace inventory
     * @param context Application or Activity context
     * @param tab Tab name (Owned, RealmsPlusCurrent, RealmsPlusRemoved, Subscriptions)
     */
    fun openMarketplaceInventory(context: Context, tab: String): Boolean {
        if (tab !in listOf("Owned", "RealmsPlusCurrent", "RealmsPlusRemoved", "Subscriptions")) {
            return false
        }
        launchUri(context, "minecraft://?openMarketplaceInventory=$tab")
        return true
    }

    /**
     * Opens the Marketplace Pass purchase screen
     * @param context Application or Activity context
     * @param tab Tab name (Home, Content, Faq, Subscribe)
     */
    fun openCsbPDPScreen(context: Context, tab: String): Boolean {
        if (tab !in listOf("Home", "Content", "Faq", "Subscribe")) {
            return false
        }
        launchUri(context, "minecraft://?openCsbPDPScreen=$tab")
        return true
    }

    /**
     * Opens the servers tab in the play menu
     * @param context Application or Activity context
     */
    fun openServersTab(context: Context) {
        launchUri(context, "minecraft://openServersTab")
    }

    /**
     * Shows a cosmetic item in the dressing room
     * @param context Application or Activity context
     * @param offerId UUID of the cosmetic item
     */
    fun showDressingRoomOffer(context: Context, offerId: String): Boolean {
        if (!isValidUUID(offerId)) {
            return false
        }
        launchUri(context, "minecraft://showDressingRoomOffer?offerID=$offerId")
        return true
    }

    /**
     * Opens the dressing room
     * @param context Application or Activity context
     */
    fun showProfileScreen(context: Context) {
        launchUri(context, "minecraft://showProfileScreen")
    }

    /**
     * Opens an in-game event landing page
     * @param context Application or Activity context
     * @param gatheringId UUID of the gathering
     */
    fun joinGathering(context: Context, gatheringId: String): Boolean {
        if (!isValidUUID(gatheringId)) {
            return false
        }
        launchUri(context, "minecraft://joinGathering?gatheringId=$gatheringId")
        return true
    }

    /**
     * Accepts a Realms invite
     * @param context Application or Activity context
     * @param inviteId Realm invite code
     */
    fun acceptRealmInvite(context: Context, inviteId: String) {
        launchUri(context, "minecraft://acceptRealmInvite?inviteID=$inviteId")
    }

    /**
     * Connects to a Realm using either realmId or inviteID
     * @param context Application or Activity context
     * @param id Realm ID or invite code
     */
    fun connectToRealm(context: Context, id: String) {
        launchUri(context, "minecraft://connectToRealm?${if (isValidUUID(id)) "realmId=$id" else "inviteID=$id"}")
    }

    /**
     * Executes a slash command
     * @param context Application or Activity context
     * @param command The command to execute
     */
    fun executeSlashCommand(context: Context, command: String) {
        launchUri(context, "minecraft://?slashcommand=$command")
    }

    /**
     * Unused URI action
     * @param context Application or Activity context
     */
    fun fromTempFile(context: Context) {
        launchUri(context, "minecraft://fromtempfile")
    }

    /**
     * Handles resource pack path
     * @param context Application or Activity context
     */
    fun originalPath(context: Context) {
        launchUri(context, "minecraft://originalpath")
    }

    /**
     * Imports content
     * @param context Application or Activity context
     * @param path File path to import
     */
    fun importContent(context: Context, path: String) {
        launchUri(context, "minecraft://?import=$path")
    }

    /**
     * Imports unknown content (parameters unclear)
     * @param context Application or Activity context
     */
    fun importLoad(context: Context) {
        launchUri(context, "minecraft://?importload")
    }

    /**
     * Imports a resource or behavior pack
     * @param context Application or Activity context
     * @param path File path ending in .mcpack
     */
    fun importPack(context: Context, path: String): Boolean {
        if (!path.endsWith(".mcpack")) {
            return false
        }
        launchUri(context, "minecraft://?importpack=$path")
        return true
    }

    /**
     * Imports an addon
     * @param context Application or Activity context
     * @param path File path ending in .mcaddon
     */
    fun importAddon(context: Context, path: String): Boolean {
        if (!path.endsWith(".mcaddon")) {
            return false
        }
        launchUri(context, "minecraft://?importaddon=$path")
        return true
    }

    /**
     * Imports a world template
     * @param context Application or Activity context
     * @param path File path ending in .mctemplate
     */
    fun importTemplate(context: Context, path: String): Boolean {
        if (!path.endsWith(".mctemplate")) {
            return false
        }
        launchUri(context, "minecraft://?importtemplate=$path")
        return true
    }

    /**
     * Connects to a local world by ID
     * @param context Application or Activity context
     * @param localLevelId Local level ID
     */
    fun loadLocalWorld(context: Context, localLevelId: String) {
        launchUri(context, "minecraft://?load=$localLevelId")
    }

    /**
     * Connects to a local or online world
     * @param context Application or Activity context
     * @param localLevelId Optional local level ID
     * @param localWorld Optional world name
     * @param serverUrl Optional server IP address
     * @param serverPort Optional server port (default: 19132)
     */
    fun connect(context: Context, localLevelId: String? = null, localWorld: String? = null, serverUrl: String? = null, serverPort: Int? = null): Boolean {
        val query = buildString {
            when {
                localLevelId != null -> append("localLevelId=$localLevelId")
                localWorld != null -> append("localWorld=$localWorld")
                serverUrl != null -> {
                    append("serverUrl=$serverUrl")
                    serverPort?.let { port ->
                        if (port <= 0 || port > 65535) {
                            return false
                        }
                        append("&serverPort=$port")
                    }
                }
                else -> {
                    return false
                }
            }
        }
        launchUri(context, "minecraft://connect/?$query")
        return true
    }

    /**
     * Internal function to launch a URI
     * @param context Application or Activity context
     * @param uriString The Minecraft URI to launch
     */
    private fun launchUri(context: Context, uriString: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString)).apply {
            setClassName(context, "io.kitsuri.mayape.manager.Importer")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
           Log.w("URI",e)
        }
    }

    /**
     * Validates if a string is a proper UUID
     */
    private fun isValidUUID(uuid: String): Boolean {
        return try {
            UUID.fromString(uuid)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}