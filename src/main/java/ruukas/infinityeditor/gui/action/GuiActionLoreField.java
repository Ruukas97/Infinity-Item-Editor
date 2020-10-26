package ruukas.infinity.gui.action;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.TextFormatting;
import ruukas.infinity.data.InfinityConfig;

public class GuiActionLoreField extends GuiActionTextField {
    public GuiActionLoreField(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height) {
        super( componentId, fontrendererObj, x, y, par5Width, par6Height );

        setDisabledTextColour( InfinityConfig.CONTRAST_COLOR );
        setTextColor( 0xFFAA00AA );
    }


    @Override
    public void drawTextBox() {
        String before = getText();
        int c = getCursorPosition();
        int e = getSelectionEnd();
        setTextNoAction( TextFormatting.ITALIC + getText() );
        setCursorPosition( c + 2 );
        setSelectionPos( e + 2 );
        super.drawTextBox();
        setTextNoAction( before );
        setCursorPosition( c );
        setSelectionPos( e );
    }
}
