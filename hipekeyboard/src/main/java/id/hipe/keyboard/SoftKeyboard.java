/*
 * Copyright (C) 2008-2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package id.hipe.keyboard;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.method.MetaKeyKeyListener;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.zuragan.shopkeepr.data.api.model.AutoTextWord;
import com.zuragan.shopkeepr.utility.AnalyticUtils;
import com.zuragan.shopkeepr.utility.PreferenceUtils;
import com.zuragan.shopkeepr.view.keyboard.autotext.AutoTextAddView;
import com.zuragan.shopkeepr.view.keyboard.autotext.AutoTextSettingView;
import com.zuragan.shopkeepr.view.keyboard.autotext.AutoTextSuggestionView;
import com.zuragan.shopkeepr.view.keyboard.calculator.CalculatorView;
import com.zuragan.shopkeepr.view.keyboard.ceckOngkir.CekOngkirView;
import timber.log.Timber;

import java.util.List;

/**
 * Example of writing an input method for a soft keyboard.  This code is
 * focused on simplicity over completeness, so it should in no way be considered
 * to be a complete soft keyboard implementation.  Its purpose is to provide
 * a basic example for how you would get started writing an input method, to
 * be fleshed out as appropriate.
 */
public class SoftKeyboard extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener, CalculatorView.CalculatorListener,
        CekOngkirView.checkOngkirCallback, AutoTextAddView.Callback,
        AutoTextSettingView.Callback, AutoTextSuggestionView.Callback {

    /**
     * This boolean indicates the optional example code for performing
     * processing of hard keys in addition to regular text generation
     * from on-screen interaction.  It would be used for input methods that
     * perform language translations (such as converting text entered on
     * a QWERTY keyboard to Chinese), but may not be used for input methods
     * that are primarily intended to be used for on-screen text entry.
     */
    static final boolean PROCESS_HARD_KEYS = true;
    public static String mActiveKeyboard;
    SharedPreferences sharedPreferences;
    long spacePast;
    boolean haltKeyboard = false;
    private CalculatorView calculatorView;
    private CekOngkirView cekOngkirView;
    private AutoTextSettingView autoTextSettingView;
    private AutoTextAddView autoTextAddView;
    private AutoTextSuggestionView autoTextSuggestionView;
    private LinearLayout toolbarHomeKeyboard;
    private InputMethodManager mInputMethodManager;
    private LatinKeyboardView mInputView;
    private boolean mPredictionOn;
    private boolean mSound;
    private int mLastDisplayWidth;
    private boolean mCapsLock;
    private long mLastShiftTime;
    private long mMetaState;
    private String mWordSeparators;
    // Keyboards (not subtypes)
    private LatinKeyboard mQwertyKeyboard;
    private LatinKeyboard mNumbersKeyboard;
    private LatinKeyboard mSymbolsKeyboard;
    private LatinKeyboard mSymbolsShiftedKeyboard;
    private LatinKeyboard mPhoneKeyboard;
    private LatinKeyboard mCurKeyboard;
    // In the onCreate method
    private AudioManager am;
    private Vibrator vb;
    private StringBuilder suggestionTemp = new StringBuilder();

    /**
     * This is the point where you can do all of your UI initialization.  It
     * is called after creation and any configuration change.
     */
    @Override
    public void onInitializeInterface() {
        if (mQwertyKeyboard != null) {
            // Configuration changes can happen after the keyboard gets recreated,
            // so we need to be able to re-build the keyboards if the available
            // space has changed.
            int displayWidth = getMaxWidth();
            if (displayWidth == mLastDisplayWidth) return;
            mLastDisplayWidth = displayWidth;
        }

        mQwertyKeyboard = new LatinKeyboard(this, R.xml.qwerty);
        mSymbolsKeyboard = new LatinKeyboard(this, R.xml.symbols);
        mSymbolsShiftedKeyboard = new LatinKeyboard(this, R.xml.symbols_shift);
        mNumbersKeyboard = new LatinKeyboard(this, R.xml.numbers);
        mPhoneKeyboard = new LatinKeyboard(this, R.xml.phone);
        cekOngkirView = new CekOngkirView(SoftKeyboard.this);
        cekOngkirView.clearText();
    }

    /*
     * param String
     *
     * return show toast with message
     */
    public void showMessage(String msg) {
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Use the right subtype based on language selected.
     *
     * @return mCurKeyboard
     */
    private LatinKeyboard getSelectedSubtype() {
        return mQwertyKeyboard;
    }

    private void setLatinKeyboard(LatinKeyboard nextKeyboard) {
        //boolean shouldSupportLanguageSwitchKey = mInputMethodManager.shouldOfferSwitchingToNextInputMethod(getToken());
        //// TODO: 22/05/18 uncomment to enable language switch button
        //nextKeyboard.setLanguageSwitchKeyVisibility(true);
        mInputView.setKeyboard(nextKeyboard);
    }

    /**
     * Called by the framework when your view for showing candidates needs to
     * be generated, like {@link #onCreateInputView}.
     */
    @Override
    public View onCreateCandidatesView() {
        return super.onCreateCandidatesView();
    }

    /**
     * This is the main point where we do our initialization of the input method
     * to begin operating on an application.  At this point we have been
     * bound to the client, and are now receiving all of the detailed information
     * about the target of our edits.
     */
    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        Timber.d("onStartInput, restart:" + restarting);
        super.onStartInput(attribute, restarting);

        // Restart the InputView to apply right theme selected.
        setInputView(onCreateInputView());

        if (!restarting) {
            // Clear shift states.
            mMetaState = 0;
        }

        mPredictionOn = false;

        // We are now going to initialize our state based on the type of
        // text being edited.
        switch (attribute.inputType & InputType.TYPE_MASK_CLASS) {
            case InputType.TYPE_CLASS_NUMBER:
                mCurKeyboard = mNumbersKeyboard;
                break;
            case InputType.TYPE_CLASS_DATETIME:
                // Numbers and dates default to the symbols keyboard, with
                // no extra features.
                mCurKeyboard = mSymbolsKeyboard;
                break;

            case InputType.TYPE_CLASS_PHONE:
                // Phones will also default to the symbols keyboard, though
                // often you will want to have a dedicated phone keyboard.
                mCurKeyboard = mPhoneKeyboard;
                break;

            case InputType.TYPE_CLASS_TEXT:
                // This is general text editing.  We will default to the
                // normal alphabetic keyboard, and assume that we should
                // be doing predictive text (showing candidates as the
                // user types).
                mCurKeyboard = getSelectedSubtype();
                mPredictionOn = sharedPreferences.getBoolean("suggestion", true);

                // We now look for a few special variations of text that will
                // modify our behavior.
                int variation = attribute.inputType & InputType.TYPE_MASK_VARIATION;
                if (variation == InputType.TYPE_TEXT_VARIATION_PASSWORD
                        || variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    // Do not display predictions / what the user is typing
                    // when they are entering a password.
                    mPredictionOn = false;
                }

                if (variation == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        || variation == InputType.TYPE_TEXT_VARIATION_URI
                        || variation == InputType.TYPE_TEXT_VARIATION_FILTER) {
                    // Our predictions are not useful for e-mail addresses
                    // or URIs.
                    mPredictionOn = false;
                    mActiveKeyboard = "en_US";
                    mCurKeyboard = mQwertyKeyboard;
                }

                if ((attribute.inputType & InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE) != 0) {
                    // If this is an auto-complete text view, then our predictions
                    // will not be shown and instead we will allow the editor
                    // to supply their own.  We only show the editor's
                    // candidates when in fullscreen mode, otherwise relying
                    // own it displaying its own UI.
                    mPredictionOn = false;
                }

                // We also want to look at the current state of the editor
                // to decide whether our alphabetic keyboard should start out
                // shifted.
                updateShiftKeyState(attribute);
                break;

            default:
                // For all unknown input types, default to the alphabetic
                // keyboard with no special features.
                mCurKeyboard = getSelectedSubtype();
                updateShiftKeyState(attribute);
        }

        // Update the label on the enter key, depending on what the application
        // says it will do.
        mCurKeyboard.setImeOptions(getResources(), attribute.imeOptions);

        mSound = sharedPreferences.getBoolean("sound", true);

        // Apply the selected keyboard to the input view.
        setLatinKeyboard(mCurKeyboard);
    }

    /**
     * This is called when the user is done editing a field.  We can use
     * this to reset our state.
     */
    @Override
    public void onFinishInput() {
        Timber.d("hasil finish input");
        super.onFinishInput();

        // We only hide the candidates window when finishing input on
        // a particular editor, to avoid popping the underlying application
        // up and down if the user is entering text into the bottom of
        // its window.
        setCandidatesViewShown(false);
        suggestionTemp.setLength(0);

        mCurKeyboard = mQwertyKeyboard;
        if (mInputView != null) {
            mInputView.closing();
        }
    }

    @Override
    public void onStartInputView(EditorInfo attribute, boolean restarting) {
        Timber.d("hasil start input view: " + restarting);
        super.onStartInputView(attribute, restarting);

        mInputView.closing();
        final InputMethodSubtype subtype = mInputMethodManager.getCurrentInputMethodSubtype();
        mInputView.setSubtypeOnSpaceKey(subtype);
    }

    /**
     * Switch to language when it is changed from Choose Input Method.
     */
    @Override
    public void onCurrentInputMethodSubtypeChanged(InputMethodSubtype subtype) {
        setLatinKeyboard(mQwertyKeyboard);
    }

    /**
     * Deal with the editor reporting movement of its cursor.
     */
    @Override
    public void onUpdateSelection(int oldSelStart, int oldSelEnd, int newSelStart,
                                  int newSelEnd, int candidatesStart, int candidatesEnd) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart,
                candidatesEnd);
    }

    /**
     * This tells us about completions that the editor has determined based
     * on the current text in it.  We want to use this in fullscreen mode
     * to show the completions ourselves, since the editor can not be seen
     * in that situation.
     */
    @Override
    public void onDisplayCompletions(CompletionInfo[] completions) {
    }

    /**
     * This translates incoming hard key events in to edit operations on an
     * InputConnection.  It is only needed when using the
     * PROCESS_HARD_KEYS option.
     */
    private boolean translateKeyDown(int keyCode, KeyEvent event) {
        mMetaState = MetaKeyKeyListener.handleKeyDown(mMetaState, keyCode, event);
        int c = event.getUnicodeChar(MetaKeyKeyListener.getMetaState(mMetaState));
        mMetaState = MetaKeyKeyListener.adjustMetaAfterKeypress(mMetaState);
        InputConnection ic = getCurrentInputConnection();
        if (c == 0 || ic == null) {
            return false;
        }

        boolean dead = false;
        if ((c & KeyCharacterMap.COMBINING_ACCENT) != 0) {
            dead = true;
            c = c & KeyCharacterMap.COMBINING_ACCENT_MASK;
        }
//
//        if (mComposing.length() > 0) {
//            char accent = mComposing.charAt(mComposing.length() - 1);
//            int composed = KeyEvent.getDeadChar(accent, c);
//            if (composed != 0) {
//                c = composed;
//                mComposing.setLength(mComposing.length() - 1);
//            }
//        }

        onKey(c, null);

        return true;
    }

    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Timber.d("hasil on key down: " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                // The InputMethodService already takes care of the back
                // key for us, to dismiss the input method if it is shown.
                // However, our keyboard could be showing a pop-up window
                // that back should dismiss, so we first allow it to do that.
                if (event.getRepeatCount() == 0 && mInputView != null) {
                    if (mInputView.handleBack()) {
                        return true;
                    }
                }
                break;

            case KeyEvent.KEYCODE_DEL:
                // Special handling of the delete key: if we currently are
                // composing text for the user, we want to modify that instead
                // of let the application to the delete itself.
                if (getCurrentText() != null && getCurrentText().length() > 0) {
                    onKey(Keyboard.KEYCODE_DELETE, null);
                    return true;
                }
                break;

            case KeyEvent.KEYCODE_ENTER:
                // Let the underlying text editor always handle these.
                return false;

            default:
                // For all other keys, if we want to do transformations on
                // text being entered with a hard keyboard, we need to process
                // it and do the appropriate action.
                if (PROCESS_HARD_KEYS) {
                    if (keyCode == KeyEvent.KEYCODE_SPACE
                            && (event.getMetaState() & KeyEvent.META_ALT_ON) != 0) {
                        // A silly example: in our input method, Alt+Space
                        // is a shortcut for 'android' in lower case.
                        InputConnection ic = getCurrentInputConnection();
                        if (ic != null) {
                            // First, tell the editor that it is no longer in the
                            // shift state, since we are consuming this.
                            ic.clearMetaKeyStates(KeyEvent.META_ALT_ON);
                            keyDownUp(KeyEvent.KEYCODE_A);
                            keyDownUp(KeyEvent.KEYCODE_N);
                            keyDownUp(KeyEvent.KEYCODE_D);
                            keyDownUp(KeyEvent.KEYCODE_R);
                            keyDownUp(KeyEvent.KEYCODE_O);
                            keyDownUp(KeyEvent.KEYCODE_I);
                            keyDownUp(KeyEvent.KEYCODE_D);
                            // And we consume this event.
                            return true;
                        }
                    }
                }
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Helper to update the shift state of our keyboard based on the initial
     * editor state.
     */
    private void updateShiftKeyState(EditorInfo attr) {
        if (attr != null && mInputView != null && mQwertyKeyboard == mInputView.getKeyboard()) {
            int caps = 0;
            EditorInfo ei = getCurrentInputEditorInfo();
            if (ei != null && ei.inputType != InputType.TYPE_NULL) {
                if (autoTextAddView.isFocused()) {
                    caps = autoTextAddView.getCapsMode(attr);
                } else if (cekOngkirView.isFocused()) {
                    caps = cekOngkirView.getCapsMode(attr);
                } else {
                    caps = getCurrentInputConnection().getCursorCapsMode(attr.inputType);
                }
            }
            mInputView.setShifted(mCapsLock || caps != 0);

            // Change Shift key icon - 2
            updateShiftIcon();
        }
    }

    /**
     * Helper to determine if a given character code is alphabetic.
     */
    private boolean isAlphabet(int code) {
        return Character.isLetter(code);
    }

    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private void keyDownUp(int keyEventCode) {
        Timber.d("hasil key down up: " + keyEventCode);
        getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    // Main initialization of the input method component.
    // Be sure to call to super class.
    @Override
    public void onCreate() {
        super.onCreate();
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mWordSeparators = getResources().getString(R.string.word_separators);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        vb = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    /**
     * Called by the framework when your view for creating input needs to
     * be generated.  This will be called the first time your input method
     * is displayed, and every time it needs to be re-created such as due to
     * a configuration change.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateInputView() {

        // Set custom theme to input view.
        View view = getLayoutInflater().inflate(R.layout.keyboard_layout_home_view, null);
        mInputView = view.findViewById(R.id.keyboard);
        mInputView.setOnKeyboardActionListener(this);

        cekOngkirView = new CekOngkirView(SoftKeyboard.this);
        cekOngkirView = view.findViewById(R.id.check_ongkir);
        cekOngkirView.setListener(SoftKeyboard.this);
        cekOngkirView.clearText();

        // call view cek ongkir
        ImageView btnCekOngkir = view.findViewById(R.id.btn_cek_ongkir);
        btnCekOngkir.setOnClickListener(view15 -> {
            toolbarHomeKeyboard.setVisibility(View.GONE);
            cekOngkirView.show();
            cekOngkirView.setHeight(CekOngkirView.MIN_HEIGHT);
            cekOngkirView.setDefaultOriginLocation();
        });

        //initial view calculator
        calculatorView = new CalculatorView(this);
        calculatorView = view.findViewById(R.id.zuragan_calculator_view);
        toolbarHomeKeyboard = view.findViewById(R.id.view_toolbar_keyboard_home_view);

        autoTextSettingView = new AutoTextSettingView(this);
        autoTextSettingView = view.findViewById(R.id.zuragan_autotext_setting);
        autoTextSettingView.setCallback(this);

        autoTextAddView = new AutoTextAddView(this);
        autoTextAddView = view.findViewById(R.id.zuragan_autotext_add);
        autoTextAddView.setCallback(this);

        autoTextSuggestionView = new AutoTextSuggestionView(this);
        autoTextSuggestionView = view.findViewById(R.id.zuragan_autotext_suggestion);
        autoTextSuggestionView.setCallback(this);

        //initial view dasboard
        ImageView btnDasboard = view.findViewById(R.id.btn_dasboard);
        btnDasboard.setOnClickListener(viewDashboard -> {
            showMessage("Coming Soon");
            AnalyticUtils.getInstance(SoftKeyboard.this).onLogoClicked();
        });

        ImageView btnInventory = view.findViewById(R.id.btn_inventory);
        btnInventory.setOnClickListener(view1 -> {
            AnalyticUtils.getInstance(SoftKeyboard.this)
                    .shareItemAccessMenu(
                            PreferenceUtils.getDataStringFromSP(SoftKeyboard.this, "imei", ""));
            showMessage("Coming Soon");
            // FIXME: 22/05/18 if inventory is ready
        });

        //call view autotext
        ImageView btnAutoText = view.findViewById(R.id.btn_autotext);
        btnAutoText.setOnClickListener(view12 -> {
            autoTextSettingView.show();
            toolbarHomeKeyboard.setVisibility(View.GONE);
            mInputView.setVisibility(View.GONE);
        });

        //call view calculator
        ImageView btnCalculator = view.findViewById(R.id.btn_calculator);
        btnCalculator.setOnClickListener(view13 -> {
            AnalyticUtils.getInstance(SoftKeyboard.this)
                    .calculatorAccessMenu(
                            PreferenceUtils.getDataStringFromSP(SoftKeyboard.this, "imei", ""));

            if (calculatorView != null) {
                calculatorView.setVisibility(View.VISIBLE);
                toolbarHomeKeyboard.setVisibility(View.GONE);
                mInputView.setVisibility(View.GONE);
            }
        });

        calculatorView.setListener(this);

        //// Close popup keyboard when screen is touched, if it's showing
        mInputView.setOnTouchListener((view14, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                haltKeyboard = false;
                mInputView.closing();
            }
            return false;
        });

        setLatinKeyboard(getSelectedSubtype());

        return view;
    }

    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // If we want to do transformations on text being entered with a hard
        // keyboard, we need to process the up events to update the meta key
        // state we are tracking.

        Timber.d("hasil on key up: " + keyCode);

        if (PROCESS_HARD_KEYS) {
            if (mPredictionOn) {
                mMetaState = MetaKeyKeyListener.handleKeyUp(mMetaState, keyCode, event);
            }
        }

        return super.onKeyUp(keyCode, event);
    }

    /**
     * Implementation of KeyboardViewListener
     */
    public void onKey(int primaryCode, int[] keyCodes) {
        if (haltKeyboard) {
            return;
        }
        Timber.d("hasil " + primaryCode);
        if (isWordSeparator(primaryCode)) {//input other  edittext App
            Timber.d("hasil is separator");
            boolean isSpaceLongPress = false;
            boolean isImeiEnter = true;
            if (primaryCode == 32) {
                long now = System.currentTimeMillis();
                long diff = Math.abs(now - spacePast);
                if (diff > 60) {
                    isSpaceLongPress = false;
                } else {
                    isSpaceLongPress = true;
                }
                spacePast = now;
            } else if (primaryCode == 10) {
                final int options = this.getCurrentInputEditorInfo().imeOptions;
                final int actionId = options & EditorInfo.IME_MASK_ACTION;
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                    case EditorInfo.IME_ACTION_GO:
                    case EditorInfo.IME_ACTION_SEND:
                        sendDefaultEditorAction(true);
                        isImeiEnter = false;
                        break;
                    default:
                        isImeiEnter = true;
                }
            }

            if (!isSpaceLongPress) {
                if (isImeiEnter) {
                    handleCharacter(primaryCode, keyCodes);
                    updateShiftKeyState(getCurrentInputEditorInfo());
                }
            } else {
                if (vb != null) {
                    vb.vibrate(50);
                }
                backspace();
                backspace();
                mInputMethodManager.showInputMethodPicker();
                haltKeyboard = true;
                String imei = PreferenceUtils.getDataStringFromSP(this, "imei", "");
                AnalyticUtils.getInstance(this).keyboardChange(imei);
            }
        } else if (primaryCode == Keyboard.KEYCODE_DELETE) {
            if (autoTextAddView.isFocused()) {
                handleBackspaceEdit(autoTextAddView.getEditText());
            } else if (cekOngkirView.isFocused()) {
                handleBackspaceEdit(cekOngkirView.getEditText());
            } else {
                handleBackspace();
            }
        } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
            handleShift();
        } else if (primaryCode == Keyboard.KEYCODE_CANCEL) {
            handleClose();
            return;
        } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE && mInputView != null) {
            Keyboard current = mInputView.getKeyboard();
            if (current == mSymbolsKeyboard || current == mSymbolsShiftedKeyboard) {
                setLatinKeyboard(mQwertyKeyboard);
                updateShiftIcon();
            } else {
                String imei = PreferenceUtils.getDataStringFromSP(this, "imei", "");
                AnalyticUtils.getInstance(this).keyboardToNumeric(imei);
                setLatinKeyboard(mSymbolsKeyboard);
                mSymbolsKeyboard.setShifted(false);
            }
        } else {
            handleCharacter(primaryCode, keyCodes);
        }

        if (mSound) playClick(primaryCode); // Play sound with button click.
    }

    /**
     * Play sound when key is pressed.
     */
    private void playClick(int keyCode) {
        if (am != null) {
            switch (keyCode) {
                case 32:
                    am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                    break;
                case Keyboard.KEYCODE_DONE:
                case 10:
                    am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                    break;
                case Keyboard.KEYCODE_DELETE:
                    am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                    break;
                default:
                    am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
            }
        }
    }

    public void onText(CharSequence text) {
//        Timber.d("Hasil on text: " + text);
//        InputConnection ic = getCurrentInputConnection();
//        if (ic == null) return;
//        ic.beginBatchEdit();
//        if (mComposing.length() > 0) {
//            commitTyped(ic);
//        }
//        ic.commitText(text, 0);
//        ic.endBatchEdit();
//        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    private void handleBackspace() {
        final int length = suggestionTemp.length();
        if (length > 1) {
            suggestionTemp.delete(length - 1, length);
            if (suggestionTemp.charAt(0) == '\\') {
                autoTextSuggestionView.refreshData(suggestionTemp.toString(),
                        AutoTextSuggestionView.DEFAULT_LIMIT);
            }
        } else if (length > 0) {
            suggestionTemp.setLength(0);
            if (autoTextSuggestionView.isShowing()) {
                autoTextSuggestionView.hide();
            }
        }
        keyDownUp(KeyEvent.KEYCODE_DEL);
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    private void handleBackspaceEdit(EditText editText) {
        if (editText != null) {
            final int length = editText.getSelectionEnd();
            if (length > 1) {
                editText.getText().delete(length - 1, length);
            } else if (length > 0) {
                suggestionTemp.setLength(0);
                editText.getText().delete(0, 1);
                editText.getText().insert(0, "");
            }
            updateShiftKeyState(getCurrentInputEditorInfo());
        }
    }

    private void handleShift() {
        if (mInputView == null) {
            return;
        }

        Keyboard currentKeyboard = mInputView.getKeyboard();
        if (mQwertyKeyboard == currentKeyboard) {
            // Alphabet keyboard
            checkToggleCapsLock();
            mInputView.setShifted(mCapsLock || !mInputView.isShifted());
        } else if (currentKeyboard == mSymbolsKeyboard) {
            mSymbolsKeyboard.setShifted(true);
            setLatinKeyboard(mSymbolsShiftedKeyboard);
            mSymbolsShiftedKeyboard.setShifted(true);
        } else if (currentKeyboard == mSymbolsShiftedKeyboard) {
            mSymbolsShiftedKeyboard.setShifted(false);
            setLatinKeyboard(mSymbolsKeyboard);
            mSymbolsKeyboard.setShifted(false);
        }

        updateShiftIcon();
    }

    /**
     * Change shift icon
     */
    private void updateShiftIcon() {
        List<Keyboard.Key> keys = mQwertyKeyboard.getKeys();
        Keyboard.Key currentKey;
        for (int i = 0; i < keys.size() - 1; i++) {
            currentKey = keys.get(i);
            mInputView.invalidateAllKeys();
            if (currentKey.codes[0] == -1) {
                currentKey.label = null;
                if (mInputView.isShifted() || mCapsLock) {
                    currentKey.icon = getResources().getDrawable(R.drawable.ic_shift_toggle_white);
                } else {
                    currentKey.icon = getResources().getDrawable(R.drawable.ic_shift_white);
                }
                break;
            }
        }
    }


    private void handleCharacter(int primaryCode, int[] keyCodes) {
        if (isInputViewShown()) {
            if (mInputView.isShifted()) {
                primaryCode = Character.toUpperCase(primaryCode);
            }
        }

        if (autoTextAddView.isFocused()) {
            autoTextAddView.insert(primaryCode);
        } else if (cekOngkirView.isFocused()) {
            cekOngkirView.insert(primaryCode);
        } else {
            StringBuilder text = new StringBuilder();
            text.append((char) primaryCode);
            suggestionTemp.append((char) primaryCode);
            if (suggestionTemp.charAt(0) == '\\' && suggestionTemp.length() > 0) {
                autoTextSuggestionView.refreshData(suggestionTemp.toString(),
                        AutoTextSuggestionView.DEFAULT_LIMIT);
            }

            getCurrentInputConnection().commitText(text, 1);
        }

        if (isAlphabet(primaryCode) && mPredictionOn) {
            updateShiftKeyState(getCurrentInputEditorInfo());
        }
    }

    private void handleClose() {
        suggestionTemp.setLength(0);
        requestHideSelf(0);
        mInputView.closing();
    }

    private IBinder getToken() {
        final Dialog dialog = getWindow();
        if (dialog == null) {
            return null;
        }
        final Window window = dialog.getWindow();
        if (window == null) {
            return null;
        }
        return window.getAttributes().token;
    }

    private void checkToggleCapsLock() {
        long now = System.currentTimeMillis();
        if (mLastShiftTime + 800 > now) {
            mCapsLock = !mCapsLock;
            mLastShiftTime = 0;
        } else {
            mLastShiftTime = now;
            mCapsLock = false;
        }
    }

    private String getWordSeparators() {
        return mWordSeparators;
    }

    public boolean isWordSeparator(int code) {
        String separators = getWordSeparators();
        return separators.contains(String.valueOf((char) code));
    }

    public void swipeRight() {

    }

    public void swipeLeft() {
    }

    public void swipeDown() {
    }

    public void swipeUp() {
    }

    public void onPress(int primaryCode) {
        mInputView.setPreviewEnabled(true);

        // Disable preview key on Shift, Delete, Space, Language, Symbol and Emoticon.
        if (primaryCode == -1
                || primaryCode == -5
                || primaryCode == -2
                || primaryCode == -10000
                //// TODO: 22/05/18 tombol browser inactive replace with slash
                || primaryCode == -7
                || primaryCode == 32) {
            mInputView.setPreviewEnabled(false);
        }
    }

    public void onRelease(int primaryCode) {

    }

    private void clearText() {
        CharSequence currentText =
                getCurrentInputConnection().getExtractedText(new ExtractedTextRequest(), 0).text;
        CharSequence beforCursorText =
                getCurrentInputConnection().getTextBeforeCursor(currentText.length(), 0);
        CharSequence afterCursorText =
                getCurrentInputConnection().getTextAfterCursor(currentText.length(), 0);
        getCurrentInputConnection().deleteSurroundingText(beforCursorText.length(),
                afterCursorText.length());
    }

    private CharSequence getCurrentText() {
        ExtractedText et = getCurrentInputConnection().getExtractedText(new ExtractedTextRequest(), 0);
        if (et != null)
            return et.text;
        return null;
    }

    private void clearComposing() {
        suggestionTemp.delete(0, suggestionTemp.length());
    }

    private void backspace() {
        getCurrentInputConnection().deleteSurroundingText(1, 0);
    }

    private void pasteText(String text, boolean isCommit) {
        clearComposing();
        StringBuilder input = new StringBuilder();
        input.append(text);
        if (isCommit) {
            getCurrentInputConnection().commitText(input, input.length());
        } else {
            getCurrentInputConnection().setComposingText(input, input.length());
        }
    }

    @Override
    public void onClickBackFromCalculator() {
        calculatorView.setVisibility(View.GONE);
        mInputView.setVisibility(View.VISIBLE);
        toolbarHomeKeyboard.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCopyClicked(String result) {
        StringBuilder input = new StringBuilder();
        input.append(result);
        getCurrentInputConnection().commitText(input, input.length());
        onClickBackFromCalculator();
    }

    @Override
    public void onShowKeyboard() {
        mCurKeyboard = mQwertyKeyboard;
        setLatinKeyboard(mCurKeyboard);
        mInputView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onShowKeyboardNumber() {
        mCurKeyboard = mNumbersKeyboard;
        setLatinKeyboard(mCurKeyboard);
        mInputView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onShowResult() {
        mInputView.setVisibility(View.GONE);
        int limit = 10;
        int count = PreferenceUtils.getDataIntFromSP(this, PreferenceUtils.COUNT_CEK_ONGKIR, 0) + 1;
        if (count != limit && count < 12) {
            PreferenceUtils.setDataIntTOSP(this, PreferenceUtils.COUNT_CEK_ONGKIR, count);
        } else if (count == limit) {
            RatePopup.showRating(SoftKeyboard.this, mInputView);
        }
    }

    @Override
    public void onPasteClipBoard(String clipboard) {
        Timber.d("paste clipeboard %s", clipboard);
        pasteText(clipboard, false);
    }

    @Override
    public void onBackFromCheckOngkir() {
        clearComposing();
        cekOngkirView.setVisibility(View.GONE);
        toolbarHomeKeyboard.setVisibility(View.VISIBLE);
        if (mInputView.getVisibility() == View.GONE) {
            mInputView.setVisibility(View.VISIBLE);
        } else {
            mCurKeyboard = mQwertyKeyboard;
            setLatinKeyboard(mCurKeyboard);
            mInputView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAutoTextAddBackPress() {
        autoTextAddView.hide();
        autoTextSettingView.show();
        mInputView.setVisibility(View.GONE);
    }

    @Override
    public void onAutoTextDonePress() {
        clearComposing();
        autoTextAddView.hide();
        mInputView.setVisibility(View.GONE);
        autoTextSettingView.show();
        autoTextSettingView.refreshData();
    }

    @Override
    public void onShortcutFocus(boolean focus) {
        mInputView.setShifted(!focus);
    }

    @Override
    public void onAddShortcut() {
        autoTextAddView.show();
        autoTextSettingView.hide();
        mInputView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackSetting() {
        clearComposing();
        autoTextSettingView.hide();
        mInputView.setVisibility(View.VISIBLE);
        toolbarHomeKeyboard.setVisibility(View.VISIBLE);
    }

    @Override
    public void onEditShortcut(AutoTextWord item) {
        autoTextAddView.show(item);
        autoTextSettingView.hide();
        mInputView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAutoTextClick(AutoTextWord text) {
        if (text != null) {
            pasteText(text.getContent(), true);
        }
    }

    @Override
    public void onAutoTextSuggestionClick(String text) {
        //handle buat autotext di awal bukan ditengah text lain
//        if (getCurrentInputConnection().getTextBeforeCursor(0, 0)
//                == null) {
//            clearComposing();
//            clearText();
//        }
        clearComposing();
        clearText();
        pasteText(text, false);
        autoTextSuggestionView.hide();
        toolbarHomeKeyboard.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAutoTextSuggestionShow(boolean isShow) {
        toolbarHomeKeyboard.setVisibility(isShow ? View.GONE : View.VISIBLE);
    }
}