package com.itskillerluc.alchemicalbrewery.item.custom.elements;
//TODO
import com.itskillerluc.alchemicalbrewery.entity.custom.ElementProjectileEntity;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ElementInit {
    public interface ElementFunction{
        /**
         * @param dir direction of the side thats being hit
         * @param pos blockpos of the block thats being hit
         * @param level level that the block is in
         * @param user user that is hitting it (or owner)
         * @param hand Hand that its being hit with
         * @param consume true if the item should be consumed
         * @param args any other arguments that are needed
         */
        void run(Direction dir, BlockPos pos, Level level, LivingEntity user, InteractionHand hand, boolean consume, String[] args);
    }
    public static Map<String, ElementFunction> functions = new HashMap<>();
    public static Map<String, Function<UseOnContext, String[]>> arguments = new HashMap<>();
    public static Map<String, Function<ElementProjectileEntity, String[]>> entityargs = new HashMap<>();

    public static final ElementFunction ENDER = register("Ender", elementfunctions::Ender);
    public static final Function<UseOnContext, String[]> ENDERARG = registerargs("Ender", useOnContext -> new String[]{""});
    public static final Function<ElementProjectileEntity, String[]> EENDERARG = registereargs("Ender", entity -> new String[]{""});
    public static final ElementFunction LAVA = register("Lava", elementfunctions::lava);
    public static final Function<UseOnContext, String[]> LAVAARG = registerargs("Lava", (UseOnContext pContext)-> new String[]{""});
    public static final Function<ElementProjectileEntity, String[]> ELAVAARG = registereargs("Lava", (entity)-> new String[]{""});
    public static final ElementFunction WATER = register("Water", elementfunctions::water);
    public static final Function<UseOnContext, String[]>  WATERARG = registerargs("Water", (UseOnContext pContext)-> new String[]{""});
    public static final Function<ElementProjectileEntity, String[]> EWATERARG = registereargs("Water", (entity)-> new String[]{""});
    public static final Function<UseOnContext, String[]>  BLOCKARG = registerargs("Block", (UseOnContext pContext)-> {
        String ElementRaw = pContext.getItemInHand().hasTag() ? pContext.getItemInHand().getTag().getString("Element") : null;
        String Element = pContext.getItemInHand().getTag().getString("Element");
        if (ElementRaw != null)
            if (ElementRaw.contains("-")) {
                if(ElementRaw.substring(ElementRaw.indexOf('-')).length() < 1){
                    try {
                        throw new ResourceLocationException("found - sign without Element value behind it. Correct syntax should be: Displayname-RealElement or Realelement. Found in:" + ElementRaw);

                    }catch (ResourceLocationException exception){
                        exception.printStackTrace();
                    }
                }
                Element = ElementRaw.substring(ElementRaw.indexOf('-')+1);
            }
        return new String[]{Element};
    });

    //public static final Function<ElementProjectileEntity, String[]> EBLOCKARG = registereargs("Block", (entity)-> new String[]{entity.getElement()});


    public static ElementFunction register(String key, ElementFunction function){
        return functions.put(key, function);
    }
    public static Function<UseOnContext, String[]> registerargs(String key, Function<UseOnContext, String[]> args){
        return arguments.put(key, args);
    }
    public static Function<ElementProjectileEntity, String[]> registereargs(String key, Function<ElementProjectileEntity, String[]> args){
        return entityargs.put(key, args);
    }
}
