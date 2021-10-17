package eu.siacs.conversations.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.text.style.StyleSpan;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import java.util.regex.Pattern;

import eu.siacs.conversations.R;
import eu.siacs.conversations.entities.Message;
import eu.siacs.conversations.ui.util.ColorUtil;

/**
 * Getters for theme attributes.
 */
public class Theme {

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
     * @return received message bubble tinted with provided color integer.
     */
    private static Drawable getMessageBubbleReceived(Context context, int color){
        Drawable bubble = ContextCompat.getDrawable(context, getThemeResource(context, R.attr.message_bubble_received_monochrome));
        DrawableCompat.setTint(bubble, color);
        return bubble;
    }

    /**
     * @return decision whether message bubble should be colored. Only received bubbles are colored,
     * and only if such user preference is set.
     */
    private static boolean isMessageBubbleColored(Context context, Message message){
        if (message.getStatus() == Message.STATUS_RECEIVED){
            SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
            return p.getBoolean("use_green_background", context.getResources().getBoolean(R.bool.use_green_background));
        }
        return false;
    }

    /**
     * Decides between theme attributes based on the "colored message bubble" user preference.
     * @return Theme attribute integer.
     */
    private static int decideColor(Context context, int attrDefault, int attrColored, Message message) {
        int colorAttr = attrDefault;
        if (isMessageBubbleColored(context, message)) {
            colorAttr = attrColored;
        }
        return colorAttr;
    }

    /**
     * Decides between color integers based on the "colored message bubble" user preference.
     * @return Color integer.
     */
    private static int decideColorInt (Context context, int colorDefault, int colorColored, Message message) {
        int colorAttr = colorDefault;
        if (isMessageBubbleColored(context, message)) {
            colorAttr = colorColored;
        }
        return colorAttr;
    }

    private static Drawable getMessageIcon(Context context, int drawableResource, Message message){
        Drawable icon = ContextCompat.getDrawable(context, getThemeResource(context, drawableResource));
        int iconColorAttr = decideColor(context, R.attr.message_icons_tint, R.attr.message_icons_tint_on_colored, message);
        DrawableCompat.setTint(icon, getThemedColor(context, iconColorAttr));
        return icon;
    }

    private static int getMessageSecondaryTextColor(Context context, Message message) {
        return getThemedColor(context, decideColor(context, R.attr.message_secondary_text_color, R.attr.message_secondary_text_color_on_colored, message));
    }

    private static int getPrimaryColor(Context context){
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
        String primaryColorStr = p.getString("custom_colorPrimary",  String.format("#%06X", (0xFFFFFF & getThemedColor(context, R.attr.colorPrimary))));
        if (!isHexColorString(primaryColorStr)) {
            return Color.parseColor(String.format("#%06X", (0xFFFFFF & getThemedColor(context, R.attr.colorPrimary))));
        }
        return Color.parseColor(primaryColorStr);
    }

    private static boolean isHexColorString(String string) {
        return Pattern.matches("^#(?:[0-9a-fA-F]{3}){1,2}$", string);
    }

    private static int getPrimaryDarkColor(Context context){
        // according to https://stackoverflow.com/questions/30870167/convert-colorprimary-to-colorprimarydark-how-much-darker/40964456#40964456
        // PrimaryDark must be darkened by 12
        return ColorUtil.darken(getPrimaryColor(context), 12);
    }

    public static ColorDrawable getActionBarColor(Context context){
        return new ColorDrawable(getPrimaryColor(context));
    }

    public static int getStatusBarColor(Context context){
        return getPrimaryDarkColor(context);
    }

    public static Drawable getReceivedMessageBubble(Context context, Message message){
        int bubbleColor = decideColorInt(context, getThemedColor(context, R.attr.message_bubble_received_color), getPrimaryDarkColor(context), message);
        return getMessageBubbleReceived(context, bubbleColor);
    }

    public static Drawable getReceivedWarningMessageBubble(Context context){
        return getMessageBubbleReceived(context, getThemedColor(context, R.attr.message_bubble_received_warning));
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

    public static StyleSpan getMessageBodyMeSpan(){
        return new StyleSpan(Typeface.BOLD_ITALIC);
    }

    public static float getMessageIconsAlpha(Context context){
        return getThemeResourceAttr(context, R.attr.message_indicator_alpha).getFloat();
    }

    public static int getMessageQuoteTextColor(Context context, Message message){
        return getThemedColor(context, decideColor(context, R.attr.quote_text_color, R.attr.quote_text_color_on_colored, message));
    }

    public static int getMessageStatusTextColor(Context context, Message message) {
        return getMessageSecondaryTextColor(context, message);
    }

    public static int getMessageBodyTextAppearance(Context context, Message message){
        return isMessageBubbleColored(context, message) ? R.style.TextAppearance_Conversations_Body1_OnDark : R.style.TextAppearance_Conversations_Body1;
    }

    public static int getMessageBodySecondaryTextAppearance(Context context, Message message){
        return isMessageBubbleColored(context, message) ? R.style.TextAppearance_Conversations_Body1_Secondary_OnDark : R.style.TextAppearance_Conversations_Body1_Secondary;
    }

    public static int getMessageEmojiBodyTextAppearance(Context context, Message message){
        return isMessageBubbleColored(context, message) ? R.style.TextAppearance_Conversations_Body1_Emoji_OnDark : R.style.TextAppearance_Conversations_Body1_Emoji;
    }

    public static StyleSpan getMessageHighlightSpan(){
        return new StyleSpan(Typeface.BOLD);
    }

    public static int getSentMessageHighlightColor(Context context) {
        return getThemedColor(context, R.attr.message_highlight_color_sent);
    }

    public static int getReceivedMessageHighlightColor(Context context, Message message){
        return getThemedColor(context, isMessageBubbleColored(context, message) ? R.attr.message_bubble_received_colored_color : R.attr.message_bubble_received_color);
    }

    public static int getMessageTextColor(Context context, Message message) {
        return getMessageSecondaryTextColor(context, message);
    }

    public static int getWarningCaptionTextAppearance(Context context, Message message){
        return isMessageBubbleColored(context, message) ? R.style.TextAppearance_Conversations_Caption_Warning_OnDark : R.style.TextAppearance_Conversations_Caption_Warning;
    }

    public static int getCaptionTextAppearance(Context context, Message message){
        return isMessageBubbleColored(context, message) ? R.style.TextAppearance_Conversations_Caption_OnDark : R.style.TextAppearance_Conversations_Caption;
    }

    // Message / Audio Player
    public static Drawable getAudioPlayerPauseButton(Context context, Message message){
        return getMessageIcon(context, R.attr.ic_pause, message);
    }
    public static Drawable getAudioPlayerPlayButton(Context context, Message message){
        return getMessageIcon(context, R.attr.ic_play_arrow, message);
    }
    public static int getAudioPlayerRuleColor(Context context){
        return ColorUtil.desaturate(getPrimaryColor(context), 15);
    }

    // Conversations overview
    public static int getFloatingActionButtonColor(Context context) {
        return getPrimaryColor(context);
    }

    public static int getUnreadCountBackgroundColor(Context context) {
        return getPrimaryColor(context);
    }

    /** Darken primaryDark by 6, so that it would return green800 if primaryDark were green700
     * (original light theme values). Then desaturate by 15 to reach green800_desaturated */
    public static int getSwipeBackgroundColor(Context context) {
        int color = ColorUtil.safeDarken(getPrimaryDarkColor(context), 6);
        return ColorUtil.desaturate(color, 15);
    }

    // Scrollbars
    public static int getVerticalScrollbarColor(Context context) {
        return getPrimaryDarkColor(context);
    }
    public static Drawable getVerticalScrollbarColorDrawable(Context context) {
        return new ColorDrawable(getFloatingActionButtonColor(context));
    }

    // StartConversationsActivity
    public static int getTabLayoutBackgroundColor(Context context) {
        return getPrimaryColor(context);
    }

    public static int getSpeedDialButtonColorClosed(Context context) {
        return getPrimaryColor(context);
    }

    public static int getSpeedDialButtonColorOpened(Context context) {
        return getPrimaryDarkColor(context);
    }

    public static int getSpeedDialItemImageColor(Context context) {
        return ContextCompat.getColor(context, R.color.white);
    }

    public static int getSpeedDialItemBackgroundColor(Context context) {
        return getSpeedDialButtonColorClosed(context);
    }

    // Blocklist
    public static int getAddBlockedJidButtonColor(Context context) {
        return getFloatingActionButtonColor(context);
    }

    //
    // NOTIFICATIONS are handled in NotificationService
    //
}
