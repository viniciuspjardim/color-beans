package com.vpjardim.colorbeans.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.NativeInputConfiguration;
import com.badlogic.gdx.input.TextInputWrapper;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

/**
 * The text fields' keyboard on a TV. It edits the text in an Android text box, since a TV's
 * keyboard can only move the cursor and clear the text of one. The cursor starts at the end
 * of the text, and done or back closes the keyboard and leaves the field
 */
public class TvKeyboard implements TextField.OnscreenKeyboard {
    @Override
    public void show(TextField field) {
        if (Gdx.input.isTextInputFieldOpened()) {
            Gdx.input.closeTextInputField(false, confirmed -> {
                open(field);
                return true;
            });
            return;
        }

        open(field);
    }

    @Override
    public void close() {
        Gdx.input.closeTextInputField(false);
    }

    private static void open(final TextField field) {
        NativeInputConfiguration config = new NativeInputConfiguration();
        config.setMaxLength(field.getMaxLength() > 0 ? field.getMaxLength() : -1);
        config.setPlaceholder(field.getMessageText() != null ? field.getMessageText() : "");

        config.setCloseCallback(confirmed -> {
            Stage stage = field.getStage();

            if (stage != null && stage.getKeyboardFocus() == field)
                stage.setKeyboardFocus(null);

            return false;
        });

        config.setTextInputWrapper(new TextInputWrapper() {
            @Override
            public String getText() {
                return field.getText();
            }

            @Override
            public int getSelectionStart() {
                return field.getText().length();
            }

            @Override
            public int getSelectionEnd() {
                return field.getText().length();
            }

            @Override
            public void writeResults(String text, int selectionStart, int selectionEnd) {
                field.setText(text);
                field.setCursorPosition(selectionEnd);
            }
        });

        Gdx.input.openTextInputField(config);
    }
}
