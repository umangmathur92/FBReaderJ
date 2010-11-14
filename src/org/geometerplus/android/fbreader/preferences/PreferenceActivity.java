/*
 * Copyright (C) 2009-2010 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader.preferences;

import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceScreen;

import org.geometerplus.zlibrary.core.options.ZLIntegerRangeOption;
import org.geometerplus.zlibrary.core.dialogs.ZLOptionsDialog;

import org.geometerplus.zlibrary.text.view.style.*;

import org.geometerplus.zlibrary.ui.android.library.ZLAndroidApplication;

import org.geometerplus.fbreader.optionsDialog.OptionsDialog;
import org.geometerplus.fbreader.fbreader.*;
import org.geometerplus.fbreader.Paths;
import org.geometerplus.fbreader.bookmodel.FBTextKind;

public class PreferenceActivity extends ZLPreferenceActivity {
	public PreferenceActivity() {
		super("Preferences");
	}

	/*private static final class ColorProfilePreference extends ZLSimplePreference {
		private final FBReaderApp myFBReader;
		private final Screen myScreen;
		private final String myKey;

		static final String createTitle(ZLResource resource, String resourceKey) {
			final ZLResource r = resource.getResource(resourceKey);
			return r.hasValue() ? r.getValue() : resourceKey;
		}

		ColorProfilePreference(Context context, FBReaderApp fbreader, Screen screen, String key, String title) {
			super(context);
			myFBReader = fbreader;
			myScreen = screen;
			myKey = key;
			setTitle(title);
		}

		@Override
		public void onAccept() {
		}

		@Override
		public void onClick() {
			myScreen.setSummary(getTitle());
			myFBReaderApp.setColorProfileName(myKey);
			myScreen.close();
		}
	}*/

	@Override
	protected void init(Intent intent) {
		final FBReaderApp fbReader = (FBReaderApp)FBReaderApp.Instance();
		final ZLAndroidApplication androidApp = ZLAndroidApplication.Instance();

		final Category optionsCategory = createCategory(null);

		final Screen directoriesScreen = optionsCategory.createPreferenceScreen("directories");
		final Category directoriesCategory = directoriesScreen.createCategory(null);
		directoriesCategory.addPreference(new ZLStringOptionPreference(
			this, Paths.BooksDirectoryOption(),
			directoriesCategory.Resource, "books"
		));
		directoriesCategory.addPreference(new ZLStringOptionPreference(
			this, Paths.FontsDirectoryOption(),
			directoriesCategory.Resource, "fonts"
		));

		final Screen appearanceScreen = optionsCategory.createPreferenceScreen("appearance");
		final Category appearanceCategory = appearanceScreen.createCategory(null);
		appearanceCategory.addOption(androidApp.AutoOrientationOption, "autoOrientation");
		if (!androidApp.isAlwaysShowStatusBar()) {
			appearanceCategory.addOption(androidApp.ShowStatusBarOption, "showStatusBar");
		}

		final Screen textScreen = optionsCategory.createPreferenceScreen("text");
		final Category textCategory = textScreen.createCategory(null);
		final ZLTextStyleCollection collection = ZLTextStyleCollection.Instance();
		final ZLTextBaseStyle baseStyle = collection.getBaseStyle();
		textCategory.addPreference(new FontOption(
			this, textCategory.Resource, "font",
			baseStyle.FontFamilyOption, false
		));
		textCategory.addPreference(new ZLIntegerRangePreference(
			this, textCategory.Resource.getResource("fontSize"),
			baseStyle.FontSizeOption
		));
		textCategory.addPreference(new FontStylePreference(
			this, textCategory.Resource, "fontStyle",
			baseStyle.BoldOption, baseStyle.ItalicOption
		));
		final ZLIntegerRangeOption spaceOption = baseStyle.LineSpaceOption;
		final String[] spacings = new String[spaceOption.MaxValue - spaceOption.MinValue + 1];
		for (int i = 0; i < spacings.length; ++i) {
			final int val = spaceOption.MinValue + i;
			spacings[i] = (char)(val / 10 + '0') + "." + (char)(val % 10 + '0');
		}
		textCategory.addPreference(new ZLChoicePreference(
			this, textCategory.Resource, "lineSpacing",
			baseStyle.LineSpaceOption, spacings
		));
		final String[] alignments = { "left", "right", "center", "justify" };
		textCategory.addPreference(new ZLChoicePreference(
			this, textCategory.Resource, "alignment",
			baseStyle.AlignmentOption, alignments
		));
		textCategory.addPreference(new ZLBooleanPreference(
			this, baseStyle.AutoHyphenationOption,
			textCategory.Resource, "autoHyphenations"
		));

		final ZLOptionsDialog dlg = new OptionsDialog(fbReader).getDialog();
		final Screen moreStylesScreen = textCategory.createPreferenceScreen("more");
		final Category moreStylesCategory = moreStylesScreen.createCategory(null);

		byte styles[] = {
			FBTextKind.REGULAR,
			FBTextKind.TITLE,
			FBTextKind.SECTION_TITLE,
			FBTextKind.SUBTITLE,
			FBTextKind.H1,
			FBTextKind.H2,
			FBTextKind.H3,
			FBTextKind.H4,
			FBTextKind.H5,
			FBTextKind.H6,
			FBTextKind.ANNOTATION,
			FBTextKind.EPIGRAPH,
			FBTextKind.AUTHOR,
			FBTextKind.POEM_TITLE,
			FBTextKind.STANZA,
			FBTextKind.VERSE,
			FBTextKind.CITE,
			FBTextKind.INTERNAL_HYPERLINK,
			FBTextKind.EXTERNAL_HYPERLINK,
			FBTextKind.FOOTNOTE,
			FBTextKind.ITALIC,
			FBTextKind.EMPHASIS,
			FBTextKind.BOLD,
			FBTextKind.STRONG,
			FBTextKind.DEFINITION,
			FBTextKind.DEFINITION_DESCRIPTION,
			FBTextKind.PREFORMATTED,
			FBTextKind.CODE
		};
		for (int i = 0; i < styles.length; ++i) {
			final ZLTextStyleDecoration decoration = collection.getDecoration(styles[i]);
			if (decoration == null) {
				continue;
			}
			ZLTextFullStyleDecoration fullDecoration =
				decoration instanceof ZLTextFullStyleDecoration ?
					(ZLTextFullStyleDecoration)decoration : null;
			final Screen formatScreen = moreStylesCategory.createPreferenceScreen(decoration.getName());
			final Category formatCategory = formatScreen.createCategory(null);
			formatCategory.addPreference(new FontOption(
				this, textCategory.Resource, "font",
				decoration.FontFamilyOption, true
			));
			formatCategory.addPreference(new ZLIntegerRangePreference(
				this, textCategory.Resource.getResource("fontSizeDifference"),
				decoration.FontSizeDeltaOption
			));
			formatCategory.addPreference(new ZLBoolean3Preference(
				this, textCategory.Resource, "bold",
				decoration.BoldOption
			));
			formatCategory.addPreference(new ZLBoolean3Preference(
				this, textCategory.Resource, "italic",
				decoration.ItalicOption
			));
			if (fullDecoration != null) {
				final String[] allAlignments = { "unchanged", "left", "right", "center", "justify" };
				formatCategory.addPreference(new ZLChoicePreference(
					this, textCategory.Resource, "alignment",
					fullDecoration.AlignmentOption, allAlignments
				));
			}
			formatCategory.addPreference(new ZLBoolean3Preference(
				this, textCategory.Resource, "allowHyphenations",
				decoration.AllowHyphenationsOption
			));
		}

		final Screen formatScreen = moreStylesCategory.createPreferenceScreen("format");
		final Screen colorsScreen = textCategory.createPreferenceScreen("colors");
		formatScreen.setOnPreferenceClickListener(
				new PreferenceScreen.OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						dlg.run(0);
						return true;
					}
				}
		);
		colorsScreen.setOnPreferenceClickListener(
				new PreferenceScreen.OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						dlg.run(1);
						return true;
					}
				}
		);

		final Screen marginsScreen = optionsCategory.createPreferenceScreen("margins");
		final Category marginsCategory = marginsScreen.createCategory(null);
		marginsCategory.addPreference(new ZLIntegerRangePreference(
			this, marginsCategory.Resource.getResource("left"),
			fbReader.LeftMarginOption
		));
		marginsCategory.addPreference(new ZLIntegerRangePreference(
			this, marginsCategory.Resource.getResource("right"),
			fbReader.RightMarginOption
		));
		marginsCategory.addPreference(new ZLIntegerRangePreference(
			this, marginsCategory.Resource.getResource("top"),
			fbReader.TopMarginOption
		));
		marginsCategory.addPreference(new ZLIntegerRangePreference(
			this, marginsCategory.Resource.getResource("bottom"),
			fbReader.BottomMarginOption
		));

		final Screen statusLineScreen = optionsCategory.createPreferenceScreen("scrollBar");
		final Category statusLineCategory = statusLineScreen.createCategory(null);

		String[] scrollBarTypes = {"hide", "show", "showAsProgress", "showAsFooter"};
		statusLineCategory.addPreference(new ZLChoicePreference(
			this, statusLineCategory.Resource, "scrollbarType",
			fbReader.ScrollbarTypeOption, scrollBarTypes
		));

		statusLineCategory.addPreference(new ZLIntegerRangePreference(
			this, statusLineCategory.Resource.getResource("footerHeight"),
			fbReader.FooterHeightOption
		));

		/*
		String[] footerLongTaps = {"longTapRevert", "longTapNavigate"};
		statusLineCategory.addPreference(new ZLChoicePreference(
			this, statusLineCategory.Resource, "footerLongTap",
			fbReader.FooterLongTapOption, footerLongTaps
		));
		*/

		statusLineCategory.addOption(fbReader.FooterShowClockOption, "showClock");
		statusLineCategory.addOption(fbReader.FooterShowBatteryOption, "showBattery");
		statusLineCategory.addOption(fbReader.FooterShowProgressOption, "showProgress");
		statusLineCategory.addOption(fbReader.FooterIsSensitiveOption, "isSensitive");
		statusLineCategory.addPreference(new FontOption(
			this, statusLineCategory.Resource, "font",
			fbReader.FooterFontOption, false
		));

		final Screen displayScreen = optionsCategory.createPreferenceScreen("display");
		final Category displayCategory = displayScreen.createCategory(null);
		displayCategory.addPreference(new ZLBooleanPreference(
			this,
			fbReader.AllowScreenBrightnessAdjustmentOption,
			displayCategory.Resource,
			"allowScreenBrightnessAdjustment"
		) {
			public void onAccept() {
				super.onAccept();
				if (!isChecked()) {
					androidApp.ScreenBrightnessLevelOption.setValue(0);
				}
			}
		});
		displayCategory.addPreference(new BatteryLevelToTurnScreenOffPreference(
			this,
			androidApp.BatteryLevelToTurnScreenOffOption,
			displayCategory.Resource,
			"dontTurnScreenOff"
		));

		/*
		final Screen colorProfileScreen = optionsCategory.createPreferenceScreen("colorProfile");
		final Category colorProfileCategory = colorProfileScreen.createCategory(null);
		final ZLResource resource = colorProfileCategory.Resource;
		colorProfileScreen.setSummary(ColorProfilePreference.createTitle(resource, fbreader.getColorProfileName()));
		for (String key : ColorProfile.names()) {
			colorProfileCategory.addPreference(new ColorProfilePreference(
				this, fbreader, colorProfileScreen, key, ColorProfilePreference.createTitle(resource, key)
			));
		}
		*/

		final Screen scrollingScreen = optionsCategory.createPreferenceScreen("scrolling");
		final Category scrollingCategory = scrollingScreen.createCategory(null);
		final ScrollingPreferences scrollingPreferences = ScrollingPreferences.Instance();
		scrollingCategory.addOption(scrollingPreferences.FlickOption, "flick");
		scrollingCategory.addOption(scrollingPreferences.VolumeKeysOption, "volumeKeys");
		scrollingCategory.addOption(scrollingPreferences.InvertVolumeKeysOption, "invertVolumeKeys");
		scrollingCategory.addOption(scrollingPreferences.AnimateOption, "animated");
		scrollingCategory.addOption(scrollingPreferences.HorizontalOption, "horizontal");
	}
}
