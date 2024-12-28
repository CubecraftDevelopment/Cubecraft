package net.cubecraft.client.gui.node.control;

import me.gb2022.commons.registry.TypeItem;
import me.gb2022.quantum3d.device.Keyboard;
import me.gb2022.quantum3d.device.KeyboardButton;
import me.gb2022.quantum3d.device.Mouse;
import me.gb2022.quantum3d.device.MouseButton;
import net.cubecraft.client.gui.event.component.TextInputSubmitEvent;
import net.cubecraft.client.gui.font.FontAlignment;
import net.cubecraft.client.gui.node.component.Component;
import net.cubecraft.client.util.IMBlocker;
import net.cubecraft.text.TextComponent;
import org.w3c.dom.Element;

import java.util.Objects;

@TypeItem("textbar")
public final class TextInput extends Component {
    private final StringBuilder text = new StringBuilder();
    private final TextComponent component = TextComponent.text("");
    private final TextComponent cursor = TextComponent.text("");
    private final TextComponent hint = TextComponent.text("");
    private int limit;
    private boolean focus;
    private int cursorPos;


    @Override
    public void init(Element element) {
        super.init(element);
        int lim = 50;
        if (element.hasAttribute("limit")) {
            lim = Integer.parseInt(element.getAttribute("limit"));
        }
        this.limit = lim;
    }

    @Override
    public TextComponent queryText(String query) {
        this.component.content(this.text.toString());

        String cursor = (System.currentTimeMillis() % 1000 < 500) ? "▎" : "";
        if (Objects.equals(query, "cursor") && this.focus) {
            return this.cursor.content(text.substring(0, cursorPos) + cursor);
        }
        if (!this.text.isEmpty()) {
            return this.component;
        }
        if (!this.focus) {
            return this.hint;
        }
        return TextComponent.EMPTY;
    }

    @Override
    public FontAlignment queryTextAlignment(String query) {
        return FontAlignment.LEFT;
    }

    @Override
    public void onKeyboardPressed(Keyboard keyboard, KeyboardButton button) {
        if (!this.focus) {
            return;
        }
        if (button == KeyboardButton.KEY_BACKSPACE && !this.text.isEmpty() && this.cursorPos > 0) {
            this.text.deleteCharAt(this.cursorPos - 1);
            this.cursorPos--;
        }
        if (button == KeyboardButton.KEY_ENTER) {
            this.context.getEventBus().callEvent(new TextInputSubmitEvent(this, this.text.toString()), this.screen.getId());
        }
        if (button == KeyboardButton.KEY_RIGHT && this.cursorPos < text.length()) {
            this.cursorPos++;
        }
        if (button == KeyboardButton.KEY_LEFT && this.cursorPos > 0) {
            this.cursorPos--;
        }
        if (button == KeyboardButton.KEY_DELETE && this.cursorPos >= 0 && this.cursorPos < text.length()) {
            text.deleteCharAt(this.cursorPos);
        }
    }

    @Override
    public void onKeyboardChar(Keyboard keyboard, char c) {
        if (this.focus && this.text.length() < this.limit) {
            this.text.insert(this.cursorPos, c);
            this.cursorPos++;
        }
    }

    @Override
    public void onMouseClicked(Mouse m, int fx, int fy, MouseButton button) {
        this.focus = isMouseInbound(fx, fy);
        IMBlocker.set(this.focus);
        if (!this.focus) {
            this.context.getEventBus().callEvent(new TextInputSubmitEvent(this, this.text.toString()), this.screen.getId());
        }
    }

    public String getText() {
        return this.text.toString();
    }
}
