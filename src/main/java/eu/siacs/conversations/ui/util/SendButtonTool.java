/*
 * Copyright (c) 2018, Daniel Gultsch All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package eu.siacs.conversations.ui.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import eu.siacs.conversations.R;
import eu.siacs.conversations.entities.Conversation;
import eu.siacs.conversations.entities.Presence;
import eu.siacs.conversations.ui.ConversationFragment;
import eu.siacs.conversations.ui.Theme;
import eu.siacs.conversations.utils.UIHelper;

public class SendButtonTool {

	public static SendButtonAction getAction(final Activity activity, final Conversation c, final String text) {
		if (activity == null) {
			return SendButtonAction.TEXT;
		}
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
		final boolean empty = text.length() == 0;
		final boolean conference = c.getMode() == Conversation.MODE_MULTI;
		if (c.getCorrectingMessage() != null && (empty || text.equals(c.getCorrectingMessage().getBody()))) {
			return SendButtonAction.CANCEL;
		} else if (conference && !c.getAccount().httpUploadAvailable()) {
			if (empty && c.getNextCounterpart() != null) {
				return SendButtonAction.CANCEL;
			} else {
				return SendButtonAction.TEXT;
			}
		} else {
			if (empty) {
				if (conference && c.getNextCounterpart() != null) {
					return SendButtonAction.CANCEL;
				} else {
					String setting = preferences.getString("quick_action", activity.getResources().getString(R.string.quick_action));
					if (!"none".equals(setting) && UIHelper.receivedLocationQuestion(c.getLatestMessage())) {
						return SendButtonAction.SEND_LOCATION;
					} else {
						if ("recent".equals(setting)) {
							setting = preferences.getString(ConversationFragment.RECENTLY_USED_QUICK_ACTION, SendButtonAction.TEXT.toString());
							return SendButtonAction.valueOfOrDefault(setting);
						} else {
							return SendButtonAction.valueOfOrDefault(setting);
						}
					}
				}
			} else {
				return SendButtonAction.TEXT;
			}
		}
	}

	public static Drawable getSendButtonImageResource(Activity activity, SendButtonAction action, Presence.Status status) {
		Drawable button = Theme.getDefaultSendButton(activity);
		switch (action) {
			case TEXT:
				return Theme.getSendTextButton(activity, status);
			case RECORD_VIDEO:
				return Theme.getSendVideoButton(activity, status);
			case TAKE_PHOTO:
				return Theme.getSendPhotoButton(activity, status);
			case RECORD_VOICE:
				return Theme.getSendVoiceButton(activity, status);
			case SEND_LOCATION:
				return Theme.getSendLocationButton(activity, status);
			case CANCEL:
				return Theme.getCancelSending(activity, status);
			case CHOOSE_PICTURE:
				return Theme.getSendPictureButton(activity, status);
		}
		return button;
	}
}
