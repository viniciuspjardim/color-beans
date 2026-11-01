package com.vpjardim.colorbeans.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IdentityMap;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.views.FocusArrow;

/**
 * Lets the controllers and the keyboard profiles drive a screen's widgets. The screen
 * lists them in rows: up and down move the focus between rows, and left and right inside
 * a row or, on a slider, change its value. The bottom face button (button 3), start or
 * enter press the focused widget, and the right face button (button 2) calls
 * {@link #back}. A {@link FocusArrow} points at the focused widget from its left
 */
public class UiNav {
    /** Seconds a direction is held before it repeats, and then between repeats */
    private static final float REPEAT_DELAY = 0.35f;
    private static final float REPEAT_INTERVAL = 0.08f;

    /**
     * Whether the arrow shows. The first key press shows it and a touch or a click hides
     * it, so it stays out of the way of touch and mouse players. It's kept between screens
     */
    public static boolean arrowShown;

    /** Called on the back button. Null does nothing */
    public Runnable back;
    /** Whether start presses the focused widget. The play screen leaves start for pausing */
    public boolean startConfirms = true;
    /** While paused the keys do nothing, like while a key profile is being edited */
    public boolean paused;

    private final Stage stage;
    private final Array<Array<Actor>> rows = new Array<>();
    /** What the arrow points at for a widget, when not the widget itself, like its label */
    private final IdentityMap<Actor, Actor> anchors = new IdentityMap<>();
    /** Keys each input had down in the last update, to find the new presses */
    private final IdentityMap<InputBase, Integer> lastKeys = new IdentityMap<>();
    private final Vector2 tmp = new Vector2();
    private Actor focused;
    /** Where the focus is, to place it near there when its widget goes away */
    private int focusRow, focusCol;
    private boolean scrollPending;
    private int repeatKey = -1;
    private float repeatTime;
    /** Set while this fires its own touch events, which mustn't hide the arrow */
    private boolean clicking;

    public UiNav(Stage stage) {
        this.stage = stage;

        stage.addCaptureListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!clicking)
                    arrowShown = false;
                return false;
            }
        });

        stage.addActor(new FocusArrow(this));
    }

    /** Adds a row of widgets. Left and right move between them */
    public UiNav row(Actor... actors) {
        rows.add(new Array<>(actors));
        return this;
    }

    /** Makes the arrow point at the anchor, like the widget's label, when the widget has the focus */
    public UiNav anchor(Actor actor, Actor anchor) {
        anchors.put(actor, anchor);
        return this;
    }

    /** Removes the rows, to add them again. If the focused widget is added again, it keeps the focus */
    public void clear() {
        rows.clear();
    }

    public void focus(Actor actor) {
        if (find(actor))
            setFocus(focusRow, focusCol);
    }

    /** What the arrow points at, or null */
    public Actor getAnchor() {
        if (paused || !arrowShown || focused == null || !isValid(focused))
            return null;

        return anchors.get(focused, focused);
    }

    public void update(float delta) {
        if (rows.size == 0)
            return;

        if (focused == null || !isValid(focused) || !find(focused))
            refocus();

        if (scrollPending && focused != null) {
            scrollPending = false;
            scrollToFocus();
        }

        // Typing in a text field, the keyboard's keys are letters, not moves
        boolean typing = stage.getKeyboardFocus() instanceof TextField;
        int pressed = 0;
        int held = 0;

        for (InputBase input : G.game.input.getInputs()) {
            if (!(input instanceof ControllerInput) && !(input instanceof KeyboardInput))
                continue;

            int keys = input.getKeyMap();
            Integer last = lastKeys.get(input);
            lastKeys.put(input, keys);

            // A key held when the screen showed, like the one that opened it, isn't a press
            if (last == null || paused || (typing && input instanceof KeyboardInput))
                continue;

            pressed |= keys & ~last;
            held |= keys;
        }

        if (!typing && Gdx.input.isKeyJustPressed(Keys.ENTER))
            pressed |= 1 << InputBase.BUTTON3_KEY;

        if (paused || focused == null || pressed == 0 && repeatKey == -1)
            return;

        if (isDown(pressed, InputBase.BUTTON2_KEY)) {
            back();
            return;
        }

        if (pressed != 0 && !arrowShown) {
            // The first press only shows where the focus is
            arrowShown = true;
            repeatKey = -1;
            return;
        }

        if (isDown(pressed, InputBase.BUTTON3_KEY) || (startConfirms && isDown(pressed, InputBase.START_KEY))) {
            confirm();
            return;
        }

        // A held direction repeats, so holding right sweeps a slider
        int key = firstDirection(pressed);

        if (key != -1) {
            repeatKey = key;
            repeatTime = -REPEAT_DELAY;
            move(key);
        } else if (repeatKey != -1 && isDown(held, repeatKey)) {
            repeatTime += delta;

            if (repeatTime >= REPEAT_INTERVAL) {
                repeatTime = 0f;
                move(repeatKey);
            }
        } else {
            repeatKey = -1;
        }
    }

    private static boolean isDown(int keys, int key) {
        return ((keys >> key) & 1) == 1;
    }

    /** The first of up, right, down and left in the keys, or -1 */
    private static int firstDirection(int keys) {
        for (int key = InputBase.UP_KEY; key <= InputBase.LEFT_KEY; key++) {
            if (isDown(keys, key))
                return key;
        }

        return -1;
    }

    private void move(int key) {
        if (key == InputBase.UP_KEY || key == InputBase.DOWN_KEY) {
            int step = key == InputBase.UP_KEY ? -1 : 1;

            // A scroll pane scrolls to its end before the focus leaves it
            if (focused instanceof ScrollPane && scroll((ScrollPane) focused, step))
                return;

            moveRow(step);
        } else {
            int step = key == InputBase.LEFT_KEY ? -1 : 1;

            if (focused instanceof Slider) {
                Slider slider = (Slider) focused;
                slider.setValue(slider.getValue() + step * slider.getStepSize());
            } else {
                moveCol(step);
            }
        }
    }

    /** Scrolls the pane a step. Returns false when it's already at that end */
    private boolean scroll(ScrollPane pane, int step) {
        float y = pane.getScrollY();

        if ((step < 0 && y <= 0f) || (step > 0 && y >= pane.getMaxY()))
            return false;

        pane.setScrollY(y + step * pane.getHeight() * 0.25f);
        return true;
    }

    private void moveRow(int step) {
        for (int r = focusRow + step; r >= 0 && r < rows.size; r += step) {
            int c = nearestValid(rows.get(r), focusCol);

            if (c != -1) {
                setFocus(r, c);
                return;
            }
        }
    }

    private void moveCol(int step) {
        Array<Actor> row = rows.get(focusRow);

        for (int c = focusCol + step; c >= 0 && c < row.size; c += step) {
            if (isValid(row.get(c))) {
                setFocus(focusRow, c);
                return;
            }
        }
    }

    /** Index of the valid widget closest to the column, the left one on ties, or -1 */
    private int nearestValid(Array<Actor> row, int col) {
        col = Math.min(col, row.size - 1);

        for (int d = 0; d < row.size; d++) {
            if (col - d >= 0 && isValid(row.get(col - d)))
                return col - d;
            if (col + d < row.size && isValid(row.get(col + d)))
                return col + d;
        }

        return -1;
    }

    /** Puts the focus near where it was, after its widget went away or got hidden */
    private void refocus() {
        int from = Math.min(focusRow, rows.size - 1);

        for (int d = 0; d < rows.size; d++) {
            for (int r = from + d; r >= from - d; r -= 2 * d) {
                if (r >= 0 && r < rows.size) {
                    int c = nearestValid(rows.get(r), focusCol);

                    if (c != -1) {
                        setFocus(r, c);
                        return;
                    }
                }

                if (d == 0)
                    break;
            }
        }

        focused = null;
    }

    /** Finds the widget in the rows and keeps its place. Returns false if it's not there */
    private boolean find(Actor actor) {
        for (int r = 0; r < rows.size; r++) {
            int c = rows.get(r).indexOf(actor, true);

            if (c != -1) {
                focusRow = r;
                focusCol = c;
                return true;
            }
        }

        return false;
    }

    private void setFocus(int row, int col) {
        Actor actor = rows.get(row).get(col);

        // Leaving a text field stops the typing
        if (actor != focused && stage.getKeyboardFocus() instanceof TextField)
            stopTyping();

        focused = actor;
        focusRow = row;
        focusCol = col;
        scrollPending = true;
    }

    private static boolean isValid(Actor actor) {
        if (actor.getStage() == null || !actor.isTouchable() || !actor.ascendantsVisible())
            return false;

        // A scroll pane is only a stop when it has something to scroll
        return !(actor instanceof ScrollPane) || ((ScrollPane) actor).getMaxY() > 0f;
    }

    private void confirm() {
        if (focused instanceof Slider) {
            // Only the on and off sliders toggle
            Slider slider = (Slider) focused;

            if (slider.getMaxValue() - slider.getMinValue() <= slider.getStepSize()) {
                slider.setValue(slider.getValue() == slider.getMaxValue()
                        ? slider.getMinValue()
                        : slider.getMaxValue());
            }
        } else if (focused instanceof TextField) {
            stage.setKeyboardFocus(focused);
            Gdx.input.setOnscreenKeyboardVisible(true);
        } else if (!(focused instanceof ScrollPane)) {
            click(focused);
        }
    }

    private void back() {
        if (stage.getKeyboardFocus() instanceof TextField)
            stopTyping();
        else if (back != null)
            back.run();
    }

    private void stopTyping() {
        stage.setKeyboardFocus(null);
        Gdx.input.setOnscreenKeyboardVisible(false);
    }

    /** Touches the actor's center, so its click listeners run like on a tap */
    private void click(Actor actor) {
        actor.localToStageCoordinates(tmp.set(actor.getWidth() / 2f, actor.getHeight() / 2f));
        clicking = true;
        fireTouch(actor, InputEvent.Type.touchDown);
        fireTouch(actor, InputEvent.Type.touchUp);
        clicking = false;
    }

    private void fireTouch(Actor actor, InputEvent.Type type) {
        InputEvent event = new InputEvent();
        event.setStage(stage);
        event.setType(type);
        event.setStageX(tmp.x);
        event.setStageY(tmp.y);
        event.setPointer(0);
        event.setButton(Buttons.LEFT);
        event.setBubbles(false);
        actor.fire(event);
    }

    /** Scrolls the focused widget and its anchor into view, with a margin for the rows around */
    private void scrollToFocus() {
        ScrollPane pane = scrollPaneOf(focused);

        if (pane == null)
            return;

        Actor content = pane.getActor();
        Actor anchor = anchors.get(focused, focused);

        focused.localToAscendantCoordinates(content, tmp.set(0f, 0f));
        float x0 = tmp.x, y0 = tmp.y;
        float x1 = x0 + focused.getWidth(), y1 = y0 + focused.getHeight();

        if (anchor != focused && anchor.isDescendantOf(content)) {
            anchor.localToAscendantCoordinates(content, tmp.set(0f, 0f));
            x0 = Math.min(x0, tmp.x);
            y0 = Math.min(y0, tmp.y);
            x1 = Math.max(x1, tmp.x + anchor.getWidth());
            y1 = Math.max(y1, tmp.y + anchor.getHeight());
        }

        float margin = G.style.padVBig;
        pane.scrollTo(x0, y0 - margin, x1 - x0, y1 - y0 + 2f * margin);
    }

    /** The scroll pane the actor is in, or null */
    public static ScrollPane scrollPaneOf(Actor actor) {
        for (Actor a = actor.getParent(); a != null; a = a.getParent()) {
            if (a instanceof ScrollPane)
                return (ScrollPane) a;
        }

        return null;
    }
}
