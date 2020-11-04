package ruukas.infinityeditor.gui.action;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ChatAllowedCharacters;
import ruukas.infinityeditor.data.InfinityConfig;

public class GuiActionTextField extends GuiTextField {
    public Runnable action;


    public GuiActionTextField(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height) {
        super( componentId, fontrendererObj, x, y, par5Width, par6Height );

        setDisabledTextColour( InfinityConfig.CONTRAST_COLOR );
        setTextColor( InfinityConfig.MAIN_COLOR );
    }


    @Override
    public void setResponderEntryValue( int idIn, String textIn ) {
        super.setResponderEntryValue( idIn, textIn );

        if (action != null) {
            action.run();
        }
    }


    @Override
    public void setText( String textIn ) {
        super.setText( textIn );

        if (action != null) {
            action.run();
        }
    }
    
    /**
     * Filter a string, keeping only characters for which {@link #isAllowedCharacter(char)} returns true.
     *  
     * Note that this method strips line breaks, as {@link #isAllowedCharacter(char)} returns false for those.
     * @return A filtered version of the input string
     */
    public static String filterAllowedCharacters(String input)
    {
        StringBuilder stringbuilder = new StringBuilder();

        for (char c0 : input.toCharArray())
        {
            if (ChatAllowedCharacters.isAllowedCharacter(c0) || c0 == 167)
            {
                stringbuilder.append(c0);
            }
        }

        return stringbuilder.toString();
    }



    /**
     * Adds the given text after the cursor, or replaces the currently selected text
     * if there is a selection.
     */
    public void writeText( String textToWrite ) {
        int c = getCursorPosition();
        int e = getSelectionEnd();
        String t = getText();
        String s = "";
        String s1 = filterAllowedCharacters( textToWrite );
        int i = c < e ? c : e;
        int j = c < e ? e : c;
        int k = getMaxStringLength() - t.length() - (i - j);

        if (!t.isEmpty()) {
            s = s + t.substring( 0, i );
        }

        int l;

        if (k < s1.length()) {
            s = s + s1.substring( 0, k );
            l = k;
        }
        else {
            s = s + s1;
            l = s1.length();
        }

        if (!t.isEmpty() && j < t.length()) {
            s = s + t.substring( j );
        }

        setText( s );
        moveCursorBy( i - getSelectionEnd() + l );
        setResponderEntryValue( getId(), t );
    }


    public void setTextNoAction( String textIn ) {
        super.setText( textIn );
    }
}
