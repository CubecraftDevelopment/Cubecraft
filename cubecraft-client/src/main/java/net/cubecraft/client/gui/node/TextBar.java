package net.cubecraft.client.gui.node;

import net.cubecraft.client.event.gui.component.TextBarSubmitEvent;
import net.cubecraft.client.gui.base.Text;
import net.cubecraft.client.gui.font.FontAlignment;
import me.gb2022.commons.event.EventHandler;
import me.gb2022.commons.registry.TypeItem;
import me.gb2022.quantum3d.device.KeyboardButton;
import me.gb2022.quantum3d.device.event.KeyboardCharEvent;
import me.gb2022.quantum3d.device.event.KeyboardHoldEvent;
import me.gb2022.quantum3d.device.event.KeyboardPressEvent;
import me.gb2022.quantum3d.device.event.MouseClickEvent;
import net.cubecraft.client.util.IMBlocker;
import org.w3c.dom.Element;

import java.util.Objects;

@TypeItem("textbar")
public class TextBar extends Component {
    private final StringBuilder text = new StringBuilder();
    private int limit;
    private final Text hint=new Text(" ", 0xFFFFFF, FontAlignment.LEFT, false);
    private boolean focus;
    private int cursorPos;

    @Override
    public void init(Element element) {
        super.init(element);
        int lim=50;
        if(element.hasAttribute("limit")){
            lim=Integer.parseInt(element.getAttribute("limit"));
        }
        this.limit = lim;
    }

    @EventHandler
    public void onChar(KeyboardCharEvent e) {
        if (this.focus && this.text.length() < this.limit) {
            this.text.insert(this.cursorPos, e.getCharacter());
            this.cursorPos++;
        }
    }

    private void processPress(KeyboardButton k) {
        if (this.focus) {
            if (k == KeyboardButton.KEY_BACKSPACE && !this.text.isEmpty() && this.cursorPos > 0) {
                this.text.deleteCharAt(this.cursorPos - 1);
                this.cursorPos--;
            }
            if (k == KeyboardButton.KEY_ENTER) {
                this.context.getEventBus().callEvent(new TextBarSubmitEvent(this, this.screen, this.context, this.text.toString()), this.screen.getId());
            }
            if (k == KeyboardButton.KEY_RIGHT && this.cursorPos < text.length()) {
                this.cursorPos++;
            }
            if (k == KeyboardButton.KEY_LEFT && this.cursorPos > 0) {
                this.cursorPos--;
            }
            if (k == KeyboardButton.KEY_DELETE && this.cursorPos >= 0 && this.cursorPos < text.length()) {
                text.deleteCharAt(this.cursorPos);
            }
        }
    }

    @Override
    public Text queryText(String query) {
        String cursor = (System.currentTimeMillis() % 1000 < 500) ? "▎" : "";
        if (Objects.equals(query, "cursor") && focus) {
            return new Text(" " + text.substring(0, cursorPos) + cursor, 0xFFFFFF, FontAlignment.LEFT);
        }
        if (!text.isEmpty()) {
            return new Text(" " + text, 0xFFFFFF, FontAlignment.LEFT);
        }
        if (!focus) {
            return this.hint;
        }
        return new Text("", 0x000000, FontAlignment.LEFT);
    }

    @EventHandler
    public void onKey(KeyboardHoldEvent e) {
        this.processPress(e.getKey());
    }

    @EventHandler
    public void onKey(KeyboardPressEvent e) {
        this.processPress(e.getKey());
    }

    @EventHandler
    public void onClick_t(MouseClickEvent e) {
        this.focus = isMouseInbound();
        IMBlocker.set(this.focus);
        if (!this.focus) {
            this.context.getEventBus().callEvent(new TextBarSubmitEvent(this, this.screen, this.context, this.text.toString()), this.screen.getId());
        }
    }
}
