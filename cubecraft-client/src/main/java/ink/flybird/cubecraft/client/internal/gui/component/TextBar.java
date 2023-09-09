package ink.flybird.cubecraft.client.internal.gui.component;

import ink.flybird.cubecraft.client.gui.base.Text;
import ink.flybird.cubecraft.client.gui.font.FontAlignment;
import ink.flybird.cubecraft.client.gui.layout.Layout;
import ink.flybird.cubecraft.client.gui.node.Component;
import ink.flybird.cubecraft.client.internal.gui.event.TextBarSubmitEvent;
import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.fcommon.file.FAMLDeserializer;
import ink.flybird.fcommon.file.XmlReader;
import ink.flybird.quantum3d.device.KeyboardButton;
import ink.flybird.quantum3d.device.event.KeyboardCharEvent;
import ink.flybird.quantum3d.device.event.KeyboardHoldEvent;
import ink.flybird.quantum3d.device.event.KeyboardPressEvent;
import ink.flybird.quantum3d.device.event.MouseClickEvent;
import org.w3c.dom.Element;

import java.util.Objects;

public class TextBar extends Component {
    private final StringBuilder text = new StringBuilder();
    private final int limit;
    private final Text hint;
    private boolean focus;
    private int cursorPos;

    public TextBar(int limit, Text hint) {
        this.limit = limit;
        this.hint = hint;
        String s = " " + this.hint.getText();
        this.hint.setText(s);
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
            if (k == KeyboardButton.KEY_BACKSPACE && this.text.length() > 0 && this.cursorPos > 0) {
                this.text.deleteCharAt(this.cursorPos - 1);
                this.cursorPos--;
            }
            if (k == KeyboardButton.KEY_ENTER) {
                this.context.getEventBus().callEvent(new TextBarSubmitEvent(this, this.screen, this.context, this.text.toString()), this.screen.getID());
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
        int xm = this.context.getFixedMouseX();
        int ym = this.context.getFixedMouseY();
        int x0 = this.getLayout().getAbsoluteX();
        int x1 = x0 + this.getLayout().getAbsoluteWidth();
        int y0 = this.getLayout().getAbsoluteY();
        int y1 = y0 + this.getLayout().getAbsoluteHeight();
        this.focus = xm > x0 && xm < x1 && ym > y0 && ym < y1;
        if (!this.focus) {
            this.context.getEventBus().callEvent(new TextBarSubmitEvent(this, this.screen, this.context, this.text.toString()), this.screen.getID());
        }
    }

    public static class XMLDeserializer implements FAMLDeserializer<TextBar> {
        @Override
        public TextBar deserialize(Element element, XmlReader reader) {
            TextBar topBar = new TextBar(
                    Integer.parseInt(element.getAttribute("limit")),
                    reader.deserialize((Element) element.getElementsByTagName("text").item(0), Text.class)
            );
            topBar.setLayout(reader.deserialize((Element) element.getElementsByTagName("layout").item(0), Layout.class));
            return topBar;
        }
    }
}
