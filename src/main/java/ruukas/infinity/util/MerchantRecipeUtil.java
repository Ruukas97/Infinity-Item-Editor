package ruukas.infinity.util;

import net.minecraft.village.MerchantRecipe;

public class MerchantRecipeUtil
{
    public static String getMerchantRecipeDisplayString( MerchantRecipe rec )
    {
        return rec.getItemToBuy().getDisplayName() + (rec.getSecondItemToBuy().isEmpty() ? "" : " + " + rec.getSecondItemToBuy().getDisplayName()) + " -> " + rec.getItemToSell().getDisplayName();
    }
}
