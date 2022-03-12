package eu.siacs.conversations.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;

import androidx.annotation.ColorInt;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.drawable.DrawableCompat;

import java.util.ArrayList;
import java.util.List;

import eu.siacs.conversations.R;
import eu.siacs.conversations.entities.Message;
import eu.siacs.conversations.entities.Presence;
import eu.siacs.conversations.ui.util.ColorUtil;
import eu.siacs.conversations.utils.ThemeHelper;

/**
 * Getters for theme attributes.
 */
public class Theme {

    //
    // GENERAL | General Helpers
    //

    private static boolean isThemeDark(Context context) {
        return ThemeHelper.isDark(ThemeHelper.find(context));
    }

    private static boolean isThemeBlack(Context context) {
        return ThemeHelper.isBlack(ThemeHelper.find(context));
    }

    /**
     * Get a resource of the current theme.
     *
     * @param r_attr_name A human readable resource attribute name,
     * @return The id of the requested resource.
     */
    private static int getThemeResource(Context context, int r_attr_name) {
        TypedValue tvattr = new TypedValue();
        context.getTheme().resolveAttribute(r_attr_name, tvattr, true);

        return tvattr.resourceId;
    }

    private static TypedValue getThemeResourceAttr(Context context, int r_attr_name) {
        TypedValue tvattr = new TypedValue();
        context.getTheme().resolveAttribute(r_attr_name, tvattr, true);

        return tvattr;
    }

    private static int getThemedColor(Context context, int attr){
        return ContextCompat.getColor(context, getThemeResource(context, attr));
    }

    /**
     * @return decison whether a color is too bright (and thus should be contrasted by darker colors)
     *
     * Calculates difference between message bubble color and black / white for its decision. Thus
     * it does not decide against the real, not purely black / white font colors, but this should
     * make little difference.
     *
     * Based on W3C standard and logic thankfully taken from https://stackoverflow.com/a/3943023,
     * but decreased sensitivity so that normal Conversations green would not be considered too bright.
     */
    private static boolean isColorTooBright(@ColorInt int color) {
        List<Float> colorList = new ArrayList<>();
        colorList.add((float) Color.red(color));
        colorList.add((float) Color.green(color));
        colorList.add((float) Color.blue(color));

        for (int i = 0; i < colorList.size(); i++) {
            float c = colorList.get(i);
            colorList.set(i, (float) (c / 255.0));
            c = colorList.get(i);
            if (colorList.get(i) <= 0.03928) {
                colorList.set(i, (float) (c / 12.92));
            } else {
                colorList.set(i, (float) Math.pow((c + 0.055) / 1.055, 2.4));
            }
        }

        float L = (float) (0.2126 * colorList.get(0) + 0.7152 * colorList.get(1) + 0.0722 * colorList.get(2));

       return L > (Math.sqrt(1.05 * 0.05) + 0.05); // W3C correct formula: L > (Math.sqrt(1.05 * 0.05) -0.05)
    }

    //
    // GENERAL | Theme
    //

    private static int getPrimaryColor(Context context){
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
        return p.getInt("custom_colorPrimaryInt", getThemedColor(context, R.attr.colorPrimary));
    }

    private static boolean isPrimaryColorCustomised(Context context) {
        return getPrimaryColor(context) != getThemedColor(context, R.attr.colorPrimary);
    }

    private static int getPrimaryDarkColor(Context context){
        // according to https://stackoverflow.com/questions/30870167/convert-colorprimary-to-colorprimarydark-how-much-darker/40964456#40964456
        // PrimaryDark must be darkened by 12
        return ColorUtil.safeDarken(getPrimaryColor(context), 12);
    }

    /**
     * Get contrast color for dark background.
     */
    private static int getContrastLight(Context context) {
        return ContextCompat.getColor(context, R.color.white);
    }

    /**
     * Get contrast color for light background.
     */
    private static int getContrastDark(Context context) {
        return ContextCompat.getColor(context, R.color.black87);
    }

    /**
     * Get less intense contrast color for dark background.
     */
    private static int getSecondaryContrastLight(Context context) {
        return ContextCompat.getColor(context, R.color.white70);
    }

    /**
     * Get less intense contrast color for light background.
     */
    private static int getSecondaryContrastDark(Context context) {
        return ContextCompat.getColor(context, R.color.black54);
    }

    //
    // GENERAL | Theme | Action and status bars
    //

    @ColorInt
    private static int getActionBarColor(Context context){
        return getPrimaryColor(context);
    }

    @ColorInt
    private static int getActionBarTextandItemColor(Context context) {
        return isActionBarColorTooBright(context) ? getSecondaryContrastDark(context) : getContrastLight(context);
    }

    private static boolean isActionBarColorTooBright(Context context) {
        return isColorTooBright(getActionBarColor(context));
    }

    public static Drawable getTintedActionBarIconDrawable(Context context, Drawable icon) {
        DrawableCompat.setTint(icon, getActionBarTextandItemColor(context));
        return icon;
    }

    public static SpannableString getActionBarTitleSpan(Context context, String title) {
        SpannableString s = new SpannableString(title);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getActionBarTextandItemColor(context));
        s.setSpan(colorSpan, 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return s;
    }

    public static Toolbar getThemedActionBar(Toolbar bar, Context context) {
        bar.setBackground(new ColorDrawable(getActionBarColor(context)));
        SpannableString s = getActionBarTitleSpan(context, context.getResources().getString(R.string.app_name));
        bar.setTitle(s);
        bar.setNavigationIcon(getBackUpArrowIcon(context));
        bar.setOverflowIcon(getMoreOptionsIcon(context));
        return bar;
    }

    public static int getStatusBarColor(Context context){
        return getPrimaryDarkColor(context);
    }

    public static Drawable getBackUpArrowIcon(Context context) {
        Drawable icon = ContextCompat.getDrawable(context, androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        return getTintedActionBarIconDrawable(context, icon);
    }

    public static Drawable getMoreOptionsIcon(Context context) {
        Drawable icon = ContextCompat.getDrawable(context, androidx.appcompat.R.drawable.abc_ic_menu_overflow_material);
        return getTintedActionBarIconDrawable(context, icon);
    }

    public static Drawable getQrCodeScanIcon(Context context) {
        Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_qr_code_scan_white_24dp);
        return getTintedActionBarIconDrawable(context, icon);
    }

    public static Drawable getCallIcon(Context context) {
        Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_call_white_24dp);
        return getTintedActionBarIconDrawable(context, icon);
    }

    public static Drawable getOngoingCallIcon(Context context) {
        Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_phone_in_talk_white_24dp);
        return getTintedActionBarIconDrawable(context, icon);
    }

    public static Drawable getMucDetailsIcon(Context context) {
        Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_group_white_24dp);
        return getTintedActionBarIconDrawable(context, icon);
    }

    public static Drawable getAddAccountIcon(Context context) {
        Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_person_add_white_24dp);
        return getTintedActionBarIconDrawable(context, icon);
    }

    public static Drawable getShareIcon(Context context) {
        Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_share_white_24dp);
        return getTintedActionBarIconDrawable(context, icon);
    }

    public static Drawable getChangePresenceIcon(Context context) {
        Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_announcement_white_24dp);
        return getTintedActionBarIconDrawable(context, icon);
    }

    public static Drawable getSearchActionIcon(Context context) {
        Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_search_white_24dp);
        return getTintedActionBarIconDrawable(context, icon);
    }

    public static Drawable getEditIcon(Context context) {
        Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_edit_white_24dp);
        return getTintedActionBarIconDrawable(context, icon);
    }

    public static Drawable getAddIcon(Context context) {
        Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_add_white_24dp);
        return getTintedActionBarIconDrawable(context, icon);
    }

    public static Drawable getImportBackupIcon(Context context) {
        Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_cloud_download_white_24dp);
        return getTintedActionBarIconDrawable(context, icon);
    }

    //
    // GENERAL | Theme | Action and status bars | Search
    //

    public static int getSearchActionTextColor(Context context) {
        return getActionBarTextandItemColor(context);
    }

    public static int getSearchActionHintTextColor(Context context) {
        return getActionBarTextandItemColor(context);
    }

    public static Drawable getSearchActionCursor(Context context) {
        Drawable icon = ContextCompat.getDrawable(context, R.drawable.white_cursor);
        return getTintedActionBarIconDrawable(context, icon);
    }

    //
    // MESSAGES | Bubbles
    //

    /**
     * @return received message bubble tinted with provided color integer.
     */
    private static Drawable getMessageBubbleReceived(Context context, int color){
        Drawable bubble = ContextCompat.getDrawable(context, getThemeResource(context, R.attr.message_bubble_received_monochrome));
        DrawableCompat.setTint(bubble, color);
        return bubble;
    }

    /**
     * @return decision whether message bubble should be colored. Only received bubbles are colored,
     * only if such user preference is set.
     */
    private static boolean isMessageBubbleColored(Context context, Message message){
        return message.getStatus() == Message.STATUS_RECEIVED
                && PreferenceManager.getDefaultSharedPreferences(context).getBoolean("use_green_background", context.getResources().getBoolean(R.bool.use_green_background));
    }

    /**
     * @return decision whether a message bubble is too bright for bright font.
     * If theme is black, it always returns false, since black theme message bubbles are dark inside.
     *
     */
    private static boolean isMessageBubbleColorTooBright(Context context, Message message) {
        if (isThemeBlack(context)) {
            return false;
        }
        int bubbleColor = getThemedColor(context, R.attr.message_bubble_sent_color);

        if (message.getStatus() == Message.STATUS_RECEIVED) {
            bubbleColor = getReceivedMessageBubbleColor(context, message);
        }

        return isColorTooBright(bubbleColor);
    }

    /**
     * Decides between color integers based on the "colored message bubble" user preference.
     * @return Color integer.
     */
    private static int decideColorInt (Context context, int colorDefault, int colorOnColored, Message message) {
        int color = colorDefault;
        if (isMessageBubbleColored(context, message)) {
            color = colorOnColored;
        }
        return color;
    }

    private static int getReceivedMessageBubbleColor(Context context, Message message) {
        return decideColorInt(context, getThemedColor(context, R.attr.message_bubble_received_color), getPrimaryDarkColor(context), message);
    }

    public static Drawable getReceivedMessageBubble(Context context, Message message){
        return getMessageBubbleReceived(context, getReceivedMessageBubbleColor(context, message));
    }

    public static Drawable getReceivedWarningMessageBubble(Context context){
        return getMessageBubbleReceived(context, getThemedColor(context, R.attr.message_bubble_received_warning));
    }

    //
    // MESSAGES | Icons
    //
    private static Drawable getMessageIcon(Context context, int drawableResource, Message message){
        Drawable icon = ContextCompat.getDrawable(context, getThemeResource(context, drawableResource));
        int iconColor = getMessageStatusTextColor(context, message);
        DrawableCompat.setTint(icon, iconColor);
        return icon;
    }

    public static Drawable getDoneMessageIcon(Context context, Message message){
        return getMessageIcon(context, R.attr.ic_done, message);
    }

    public static Drawable getEditedMessageIcon(Context context, Message message){
        return getMessageIcon(context, R.attr.ic_mode_edit, message);
    }

    public static Drawable getUserVerifiedMessageIcon(Context context, Message message){
        return getMessageIcon(context, R.attr.ic_verified_user, message);
    }

    public static Drawable getUserUnverifiedMessageIcon(Context context, Message message){
        return getMessageIcon(context, R.attr.ic_lock, message);
    }

    public static float getMessageIconsAlpha(Context context){
        return getThemeResourceAttr(context, R.attr.message_indicator_alpha).getFloat();
    }

    //
    // MESSAGES | Texts
    //

    private static int getMessageSecondaryTextColor(Context context, Message message) {
        return isMessageBubbleColorTooBright(context, message) ? getSecondaryContrastDark(context) : getSecondaryContrastLight(context);
    }

    public static int getMessageStatusTextColor(Context context, Message message) {
        return getMessageSecondaryTextColor(context, message);
    }

    public static int getMessageBodyTextColor(Context context, Message message) {
        return isMessageBubbleColorTooBright(context, message) ? getContrastDark(context) : getContrastLight(context);
    }

    public static int getMessageBodyTextAppearance(Context context, Message message){
        return isMessageBubbleColorTooBright(context, message) ?  R.style.TextAppearance_Conversations_Body1 : R.style.TextAppearance_Conversations_Body1_OnDark;
    }

    public static int getMessageBodySecondaryTextAppearance(Context context, Message message){
        return isMessageBubbleColorTooBright(context, message) ? R.style.TextAppearance_Conversations_Body1_Secondary : R.style.TextAppearance_Conversations_Body1_Secondary_OnDark;
    }

    public static int getMessageEmojiBodyTextAppearance(Context context, Message message){
        return isMessageBubbleColorTooBright(context, message) ? R.style.TextAppearance_Conversations_Body1_Emoji : R.style.TextAppearance_Conversations_Body1_Emoji_OnDark;
    }

    public static StyleSpan getMessageHighlightSpan(){
        return new StyleSpan(Typeface.BOLD);
    }

    public static StyleSpan getMessageBodyMeSpan(){
        return new StyleSpan(Typeface.BOLD_ITALIC);
    }

    public static int getSentMessageHighlightColor(Context context) {
        return getThemedColor(context, R.attr.message_highlight_color_sent);
    }

    public static int getReceivedMessageHighlightColor(Context context, Message message){
        return getThemedColor(context, isMessageBubbleColored(context, message) && !isMessageBubbleColorTooBright(context, message) ? R.attr.message_bubble_received_colored_color : R.attr.message_bubble_received_color);
    }

    /** This is NOT the color of the text in a message bubble. */
    public static int getMessageTextColor(Context context, Message message) {
        return getMessageSecondaryTextColor(context, message);
    }

    public static int getWarningCaptionTextAppearance(Context context, Message message){
        return isMessageBubbleColored(context, message) && !isMessageBubbleColorTooBright(context, message) ? R.style.TextAppearance_Conversations_Caption_Warning_OnDark : R.style.TextAppearance_Conversations_Caption_Warning;
    }

    public static int getCaptionTextAppearance(Context context, Message message){
        return isMessageBubbleColored(context, message) && !isMessageBubbleColorTooBright(context, message) ? R.style.TextAppearance_Conversations_Caption_OnDark : R.style.TextAppearance_Conversations_Caption;
    }

    //
    // MESSAGES | Texts | Quotes
    //

    /**
     * @return If primaryColor were R.color.green600, R.color.green700_desaturated would be returned.
     */
    private static int getMessageQuoteTextColorForLightBackground(Context context) {
        // darken
        int darkened = ColorUtil.safeDarken(getPrimaryColor(context), 6);
        // desaturate
        return ColorUtil.safeDesaturate(darkened, 15);
    }

    /**
     * Gets the color for quote blocks. If light theme is used and message bubble not colored,
     * it defaults to getMessageQuoteTextColorForLightBackground, otherwise uses the default
     * provided by the theme.
     */
    public static int getMessageQuoteTextColor(Context context, Message message){
        if (!isThemeDark(context) && !isMessageBubbleColored(context, message)) {
            return getMessageQuoteTextColorForLightBackground(context);
        } else {
            if (isMessageBubbleColorTooBright(context, message)) {
                int col = getThemedColor(context, R.attr.message_primary_text_color);
                int alpha = Color.alpha(col) - 76;
                return ColorUtils.setAlphaComponent(col, alpha);
            } else {
                return getThemedColor(context, R.attr.quote_text_color_on_colored);
            }
        }
    }

    //
    // MESSAGES | Audio Player
    //

    public static Drawable getAudioPlayerPauseButton(Context context, Message message){
        return getMessageIcon(context, R.attr.ic_pause, message);
    }
    public static Drawable getAudioPlayerPlayButton(Context context, Message message){
        return getMessageIcon(context, R.attr.ic_play_arrow, message);
    }
    public static int getAudioPlayerRuleColor(Context context){
        return ColorUtil.safeDesaturate(getPrimaryColor(context), 15);
    }

    //
    // CONVERSATIONS OVERVIEW
    //

    public static Drawable getStartConversationFabIcon(Context context) {
        Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_chat_white_24dp);
        return getTintedActionBarIconDrawable(context, icon);
    }

    public static int getStartConversationFabColor(Context context) {
        return getPrimaryColor(context);
    }

    public static int getUnreadCountTextColor(Context context) {
        return getActionBarTextandItemColor(context);
    }

    public static int getUnreadCountBackgroundColor(Context context) {
        return getPrimaryColor(context);
    }

    /** Darken primaryDark by 6, so that it would return green800 if primaryDark were green700
     * (original light theme values). Then desaturate by 15 to reach green800_desaturated */
    public static int getSwipeBackgroundColor(Context context) {
        int color = ColorUtil.safeDarken(getPrimaryDarkColor(context), 6);
        return ColorUtil.safeDesaturate(color, 15);
    }

    //
    // CONVERSATIONS OVERVIEW | StartConversationActivity
    //

    public static int getTabLayoutSelectedColor(Context context) {
        return isActionBarColorTooBright(context) ? getContrastDark(context) : getContrastLight(context);
    }

    public static int getTabLayoutNormalColor(Context context) {
        return isActionBarColorTooBright(context) ? getSecondaryContrastDark(context) : getSecondaryContrastLight(context);
    }

    public static int getTabLayoutBackgroundColor(Context context) {
        return getPrimaryColor(context);
    }

    public static Drawable getSpeedDialButtonIconClosed(Context context) {
        return getAddIcon(context);
    }

    public static Drawable getSpeedDialButtonIconOpened(Context context) {
        Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_action_cancel_launchersize);
        return getTintedActionBarIconDrawable(context, icon);
    }

    public static int getSpeedDialButtonColorClosed(Context context) {
        return getPrimaryColor(context);
    }

    public static int getSpeedDialButtonColorOpened(Context context) {
        return getPrimaryDarkColor(context);
    }

    public static int getSpeedDialItemImageColor(Context context) {
        return isActionBarColorTooBright(context) ? getContrastDark(context) : getContrastLight(context);
    }

    public static int getSpeedDialItemBackgroundColor(Context context) {
        return getSpeedDialButtonColorClosed(context);
    }

    //
    // SCROLLBARS
    //

    public static int getVerticalScrollbarColor(Context context) {
        return getPrimaryDarkColor(context);
    }
    public static Drawable getVerticalScrollbarColorDrawable(Context context) {
        return new ColorDrawable(getStartConversationFabColor(context));
    }

    //
    // BLOCKLIST
    //
    public static int getAddBlockedJidButtonColor(Context context) {
        return getStartConversationFabColor(context);
    }

    public static Drawable getAddBlockedJidButtonIcon(Context context) {
        return getAddAccountIcon(context);
    }

    //
    // MESSAGING | SendButtons (the following methods allow forks to overwrite colors send colors in theme.xml, but falls back
    // to default colors if user enters a custom primary color (as not every possible color like
    // Bordeaux would be an appropriate 'online' color)

    public static Drawable getDefaultSendButton(Context context) {
        Drawable button = ContextCompat.getDrawable(context, getThemeResource(context, R.attr.ic_send_text_icon));
        DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_text_offline)));
        return button;
    }

    private static Drawable getSendTextButton(Context context) {
        return ContextCompat.getDrawable(context, getThemeResource(context, R.attr.ic_send_text_icon));
    }

    public static Drawable getSendTextButton(Context context, Presence.Status status) {
        Drawable button = getSendTextButton(context);
        if (isPrimaryColorCustomised(context)) {
            return getDefaultTintedButton(context, button, status);
        } else {
            switch (status) {
                case CHAT:
                case ONLINE:
                    DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_text_online)));
                    return button;
                case AWAY:
                    DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_text_away)));
                    return button;
                case XA:
                case DND:
                    DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_text_dnd)));
                    return button;
                default:
                    DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_text_offline)));
                    return button;
            }
        }
    }

    private static Drawable getSendVideoButton(Context context) {
        return ContextCompat.getDrawable(context, getThemeResource(context, R.attr.ic_send_videocam_icon));
    }

    public static Drawable getSendVideoButton(Context context, Presence.Status status) {
        Drawable button = getSendVideoButton(context);
        if (isPrimaryColorCustomised(context)) {
            return getDefaultTintedButton(context, button, status);
        } else {
            switch (status) {
                case CHAT:
                case ONLINE:
                    DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_videocam_online)));
                    return button;
                case AWAY:
                    DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_videocam_away)));
                    return button;
                case XA:
                case DND:
                    DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_videocam_dnd)));
                    return button;
                default:
                    DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_videocam_offline)));
                    return button;
            }
        }
    }

    private static Drawable getSendPhotoButton(Context context) {
        return ContextCompat.getDrawable(context, getThemeResource(context, R.attr.ic_send_photo_icon));
    }

    public static Drawable getSendPhotoButton(Context context, Presence.Status status) {
        Drawable button = getSendPhotoButton(context);
        if (isPrimaryColorCustomised(context)) {
            return getDefaultTintedButton(context, button, status);
        } else {
            switch (status) {
                case CHAT:
                case ONLINE:
                    DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_photo_online)));
                    return button;
                case AWAY:
                    DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_photo_away)));
                    return button;
                case XA:
                case DND:
                    DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_photo_dnd)));
                    return button;
                default:
                    DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_photo_offline)));
                    return button;
            }
        }
    }

    private static Drawable getSendPictureButton(Context context) {
        return ContextCompat.getDrawable(context, getThemeResource(context, R.attr.ic_send_picture_icon));
    }

    public static Drawable getSendPictureButton(Context context, Presence.Status status) {
        Drawable button = getSendPictureButton(context);
        if (isPrimaryColorCustomised(context)) {
            return getDefaultTintedButton(context, button, status);
        } else {
            switch (status) {
                case CHAT:
                case ONLINE:
                    DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_picture_online)));
                    return button;
                case AWAY:
                    DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_picture_away)));
                    return button;
                case XA:
                case DND:
                    DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_picture_dnd)));
                    return button;
                default:
                    DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_picture_offline)));
                    return button;
            }
        }
    }

    private static Drawable getSendVoiceButton(Context context) {
        return ContextCompat.getDrawable(context, getThemeResource(context, R.attr.ic_send_voice_icon));
    }

    public static Drawable getSendVoiceButton(Context context, Presence.Status status) {
        Drawable button = getSendVoiceButton(context);
        if (isPrimaryColorCustomised(context)) {
            return getDefaultTintedButton(context, button, status);
        } else {
            switch (status) {
                case CHAT:
                case ONLINE:
                    DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_voice_online)));
                    return button;
                case AWAY:
                    DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_voice_away)));
                    return button;
                case XA:
                case DND:
                    DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_voice_dnd)));
                    return button;
                default:
                    DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_voice_offline)));
                    return button;
            }
        }
    }

    private static Drawable getSendLocationButton(Context context) {
        return ContextCompat.getDrawable(context, getThemeResource(context, R.attr.ic_send_location_icon));
    }

    public static Drawable getSendLocationButton(Context context, Presence.Status status) {
        Drawable button = getSendLocationButton(context);
        if (isPrimaryColorCustomised(context)) {
            return getDefaultTintedButton(context, button, status);
        } else {
            switch (status) {
                case CHAT:
                case ONLINE:
                    DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_location_online)));
                    return button;
                case AWAY:
                    DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_location_away)));
                    return button;
                case XA:
                case DND:
                    DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_location_dnd)));
                    return button;
                default:
                    DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_location_offline)));
                    return button;
            }
        }
    }

    private static Drawable getCancelSendingButton(Context context) {
        return ContextCompat.getDrawable(context, getThemeResource(context, R.attr.ic_send_cancel_icon));
    }

    public static Drawable getCancelSending(Context context, Presence.Status status) {
        Drawable button = getCancelSendingButton(context);
        if (isPrimaryColorCustomised(context)) {
            return getDefaultTintedButton(context, button, status);
        } else {
            switch (status) {
                case CHAT:
                case ONLINE:
                    DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_cancel_online)));
                    return button;
                case AWAY:
                    DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_cancel_away)));
                    return button;
                case XA:
                case DND:
                    DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_cancel_dnd)));
                    return button;
                default:
                    DrawableCompat.setTint(button, ContextCompat.getColor(context, getThemeResource(context, R.attr.ic_send_cancel_offline)));
                    return button;
            }
        }
    }

    private static Drawable getDefaultTintedButton(Context context, Drawable button, Presence.Status status){
        switch (status) {
            case CHAT:
            case ONLINE:
                DrawableCompat.setTint(button, getDefaultOnlineColor(context));
                return button;
            case AWAY:
                DrawableCompat.setTint(button, getDefaultAwayColor(context));
                return button;
            case XA:
            case DND:
                DrawableCompat.setTint(button, getDefaultDndColor(context));
                return button;
            default:
                DrawableCompat.setTint(button, getDefaultOfflineColor(context));
                return button;
        }
    }

    private static int getDefaultOnlineColor(Context context){
        return ContextCompat.getColor(context, R.color.green600);
    }

    private static int getDefaultAwayColor(Context context) {
        return ContextCompat.getColor(context, R.color.orange500);
    }

    private static int getDefaultDndColor(Context context) {
        return ContextCompat.getColor(context, R.color.red500);
    }

    private static int getDefaultOfflineColor(Context context) {
        return ContextCompat.getColor(context, R.color.white70);
    }

    //
    // ChooseContactActivity aka invite Contact
    //

    public static Drawable getChooseContactAddFabIcon(Context context){
        Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_person_add_white_24dp);
        return getTintedActionBarIconDrawable(context, icon);
    }

    public static Drawable getChooseContactForwardFabIcon(Context context){
        Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_forward_white_24dp);
        return getTintedActionBarIconDrawable(context, icon);
    }

    public static int getChooseContactAddFabBackgroundColor(Context context){
        return getStartConversationFabColor(context);
    }

    //
    // NOTIFICATIONS are handled in NotificationService
    //
}
