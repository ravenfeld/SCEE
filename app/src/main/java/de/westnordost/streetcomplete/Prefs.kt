package de.westnordost.streetcomplete

/** Constant class to have all the identifiers for SCEE shared preferences in one place  */
object Prefs {
    const val VOLUME_ZOOM = "volume_button_zoom"
    const val SHOW_3D_BUILDINGS = "3d_buildings"
    const val QUEST_GEOMETRIES = "quest_geometries"
    const val AUTO_DOWNLOAD = "auto_download"
    const val GPS_INTERVAL = "gps_interval"
    const val NETWORK_INTERVAL = "network_interval"
    const val HIDE_NOTES_BY_USERS = "hide_notes_by_users2"
    const val MANUAL_DOWNLOAD_OVERRIDE_CACHE = "manual_download_override_cache"
    const val QUICK_SETTINGS = "quick_settings"
    const val ALLOWED_LEVEL = "allowed_level"
    const val ALLOWED_LEVEL_TAGS = "allowed_level_tags"
    const val RESURVEY_KEYS = "resurvey_keys"
    const val RESURVEY_DATE = "resurvey_date"
    const val GPX_BUTTON = "gpx_button"
    const val SWAP_GPX_NOTE_BUTTONS = "swap_gpx_note_buttons"
    const val HIDE_KEYBOARD_FOR_NOTE = "hide_keyboard_for_note"
    const val OFFSET_FIX = "offset_fix"
    const val DAY_NIGHT_BEHAVIOR = "day_night_behavior"
    const val QUEST_SETTINGS_PER_PRESET = "quest_settings_per_preset"
    const val SHOW_HIDE_BUTTON = "show_hide_button"
    const val SELECT_FIRST_EDIT = "select_first_edit"
    const val BAN_CHECK_ERROR_COUNT = "ban_check_error_count"
    const val DATA_RETAIN_TIME = "data_retain_time"
    const val FAVS_FIRST_MIN_LINES = "favs_first_min_lines"
    const val SHOW_NEARBY_QUESTS = "show_nearby_quests"
    const val SHOW_NEARBY_QUESTS_DISTANCE = "show_nearby_quests_distance"
    const val CUSTOM_OVERLAY_IDX_FILTER = "custom_overlay_idx_filter"
    const val CUSTOM_OVERLAY_IDX_COLOR_KEY = "custom_overlay_idx_color_key"
    const val CUSTOM_OVERLAY_IDX_NAME = "custom_overlay_idx_name"
    const val CUSTOM_OVERLAY_IDX_ICON = "custom_overlay_idx_icon"
    const val CUSTOM_OVERLAY_IDX_DASH_FILTER = "custom_overlay_idx_dash_filter"
    const val CUSTOM_OVERLAY_IDX_HIGHLIGHT_MISSING_DATA = "custom_overlay_idx_highlight_missing_data"
    const val CUSTOM_OVERLAY_INDICES = "custom_overlay_indices"
    const val CUSTOM_OVERLAY_SELECTED_INDEX = "custom_overlay_selected_index"
    const val SHOW_SOLVED_ANIMATION = "show_solved_animation"
    const val SHOW_NEXT_QUEST_IMMEDIATELY = "show_next_quest_immediately"
    const val MAIN_MENU_FULL_GRID = "main_menu_full_grid"
    const val CREATE_POI_RECENT_FEATURE_IDS = "create_poi_recent_feature_ids"
    const val DYNAMIC_QUEST_CREATION = "dynamic_quest_creation"
    const val QUEST_MONITOR = "quest_monitor"
    const val QUEST_MONITOR_GPS = "quest_monitor_gps"
    const val QUEST_MONITOR_NET = "quest_monitor_net"
    const val QUEST_MONITOR_RADIUS = "quest_monitor_radius"
    const val QUEST_MONITOR_DOWNLOAD = "quest_monitor_download"
    const val SHOW_GPX_TRACK = "show_gpx_track"
    const val RASTER_TILE_URL = "raster_tile_url"
    const val RASTER_TILE_MAXZOOM = "raster_tile_maxzoom"
    const val CREATE_EXTERNAL_QUESTS = "create_external_quests"
    const val SAVE_PHOTOS = "save_photos"
    const val EXPERT_MODE = "expert_mode"
    const val SHOW_WAY_DIRECTION = "show_way_direction"
    const val SEARCH_MORE_LANGUAGES = "search_more_languages"
    const val NO_SATELLITE_LABEL = "no_satellite_label"
    const val CAPS_WORD_NAME_INPUT = "caps_word_name_input"
    const val INSERT_NODE_RECENT_FEATURE_IDS = "insert_node_recent_feature_ids"
    const val OVERLAY_QUICK_SELECTOR = "overlay_quick_selector"
    const val CREATE_NODE_LAST_TAGS_FOR_FEATURE = "create_node_last_tags_for_"
    const val CREATE_NODE_SHOW_KEYBOARD = "create_node_show_keyboard"
    const val UPDATE_LOCAL_STATISTICS = "update_local_statistics"
    const val HIDE_OVERLAY_QUESTS = "hide_overlay_quests"
    const val MAIN_MENU_SWITCH_PRESETS = "main_menu_switch_presets"
    const val DISABLE_NAVIGATION_MODE = "disable_navigation_mode"
    const val TEMP_LOGGER = "temp_logger"
    const val SHOW_CUSTOM_GEOMETRY = "show_custom_geometry"
    const val THEME_BACKGROUND = "theme.background_type"
    const val REALLY_ALL_NOTES = "really_all_notes"
    const val ROTATE_WHILE_ZOOMING = "rotate_while_zooming"
    const val ROTATE_ANGLE_THRESHOLD = "rotate_angle_threshold"

    enum class DayNightBehavior(val titleResId: Int) {
        IGNORE(R.string.day_night_ignore),
        PRIORITY(R.string.day_night_priority),
        VISIBILITY(R.string.day_night_visibility)
    }
}
