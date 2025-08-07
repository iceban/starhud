package fin.starhud.helper;

import fin.starhud.Main;
import fin.starhud.config.GeneralSettings;

public enum HUDDisplayMode {
    ICON_ONLY,
    INFO_ONLY,
    BOTH;

    public int calculateWidth(int iconWidth, int infoWidth) {
        final GeneralSettings.HUDSettings SETTINGS = Main.settings.generalSettings.hudSettings;
        int padding = SETTINGS.textPadding;
        int gap = SETTINGS.iconInfoGap;

        return switch (this) {
            case ICON_ONLY -> iconWidth;
            case INFO_ONLY -> padding + infoWidth + padding;
            case BOTH -> iconWidth + gap + padding + infoWidth + padding;
        };
    }
}
