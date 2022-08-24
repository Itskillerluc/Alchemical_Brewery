package com.itskillerluc.alchemicalbrewery.tileentity;
//TODO
import com.itskillerluc.alchemicalbrewery.data.recipes.ElementalCombinerRecipe;
import com.itskillerluc.alchemicalbrewery.elements.ElementData;
import com.itskillerluc.alchemicalbrewery.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ElementalCombinerTile extends BlockEntity {

    private final ItemStackHandler itemHandler = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private LazyOptional<IItemHandler> handler = LazyOptional.of(()->itemHandler);

    /**
     * move the tags to a spot where it actually works when dropping them
     */
    public SimpleContainer additemtags(){
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            items.add(itemHandler.getStackInSlot(i));
        }
        items.forEach((ele)->{
            ele.serializeNBT().getCompound("ForgeCaps").getAllKeys().forEach((element)->{
                ele.getOrCreateTag().put(element, ele.serializeNBT().getCompound("ForgeCaps").get(element));
            });
        });
        SimpleContainer tagitems = new SimpleContainer(9);
        for(int i=0;i<items.size();i++){
            tagitems.setItem(i, items.get(i));
        }
        return tagitems;
    }

    /**
     * drops that should drop when broken
     */
    public void drops() {
        SimpleContainer tagitems = additemtags();
        Containers.dropContents(this.level, this.worldPosition, tagitems);
    }



    public ElementalCombinerTile(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModTileEntities.ELEMENTALCOMBINER.get(), pWorldPosition, pBlockState);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inv"));
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inv", itemHandler.serializeNBT());
        super.saveAdditional(pTag);
    }


    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return handler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps()  {
        super.invalidateCaps();
        handler.invalidate();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        handler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
    }

    @Override
    public CompoundTag serializeNBT() {
        return super.serializeNBT();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
    }

    /**
     * @param entity tileentity being targeted
     * @return true if a recipe instance is present
     */
    public static boolean hasRecipe(ElementalCombinerTile entity) {
        Level level = entity.getLevel();

        SimpleContainer tagItems = entity.additemtags();
        Optional<ElementalCombinerRecipe> match = level.getRecipeManager()
                .getRecipeFor(ElementalCombinerRecipe.Type.INSTANCE, tagItems, level);

        return match.isPresent();
    }

    /**
     * craft the item
     * @param entity tileentity thats targeted
     */
    public static void craftItem(ElementalCombinerTile entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<ElementalCombinerRecipe> match = level.getRecipeManager()
                .getRecipeFor(ElementalCombinerRecipe.Type.INSTANCE, inventory, level);

        if(match.isPresent()) {
            ItemStack resultItem = match.get().assemble(inventory);

            for (int i = 0; i < match.get().size(); i++) {
                entity.itemHandler.extractItem(i, match.get().getCount(i), false);
            }

            level.addFreshEntity(new ItemEntity(level, entity.getBlockPos().getX(), entity.getBlockPos().getY(),entity.getBlockPos().getZ(), resultItem));
        }
    }

    /**
     * inserts the item that's being held into the tileentity
     * @param item item type that should be inserted
     * @param count item count that should be inserted
     * @param tag tags of the item that should be inserted
     * @param entity tile entity where it should be inserted into
     * @param player player that is inserting it
     */
    public void insertItem(Item item, int count, CompoundTag tag, ElementalCombinerTile entity, Player player) {
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }
        ItemStack itemtoinsert = new ItemStack(item, count, tag);
        if(entity.itemHandler.getStackInSlot(0).isEmpty()||entity.itemHandler.getStackInSlot(1).isEmpty()||entity.itemHandler.getStackInSlot(2).isEmpty()||entity.itemHandler.getStackInSlot(3).isEmpty()||entity.itemHandler.getStackInSlot(4).isEmpty()||entity.itemHandler.getStackInSlot(5).isEmpty()||entity.itemHandler.getStackInSlot(6).isEmpty()||entity.itemHandler.getStackInSlot(7).isEmpty()) {
            entity.itemHandler.insertItem(
                    (entity.itemHandler.getStackInSlot(0).isEmpty()) ? 0 : (entity.itemHandler.getStackInSlot(1).isEmpty()) ? 1 : (entity.itemHandler.getStackInSlot(2).isEmpty()) ? 2 : (entity.itemHandler.getStackInSlot(3).isEmpty()) ? 3 : (entity.itemHandler.getStackInSlot(4).isEmpty()) ? 4 : (entity.itemHandler.getStackInSlot(5).isEmpty()) ? 5 : (entity.itemHandler.getStackInSlot(6).isEmpty()) ? 6 : 7,
                    itemtoinsert, false);
            player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }else{
            if (player.getLevel().isClientSide) {
                player.sendMessage(new TextComponent("There is no empty space left"), player.getUUID());
            }
        }
    }

    /**
     * extracts the item from the tileentity and into the players hand
     * @param pPlayer player thats extracting the item from the tileentity
     * @param entity tileentity thats being targeted
     */
    public void extractItem(Player pPlayer, ElementalCombinerTile entity) {
        int slot = (!entity.itemHandler.getStackInSlot(7).isEmpty()) ? 7 : (!entity.itemHandler.getStackInSlot(6).isEmpty()) ? 6 : (!entity.itemHandler.getStackInSlot(5).isEmpty()) ? 5 : (!entity.itemHandler.getStackInSlot(4).isEmpty()) ? 4 : (!entity.itemHandler.getStackInSlot(3).isEmpty()) ? 3 : (!entity.itemHandler.getStackInSlot(2).isEmpty()) ? 2 : (!entity.itemHandler.getStackInSlot(1).isEmpty()) ? 1 : 0;
        if(!entity.itemHandler.getStackInSlot(0).isEmpty()||!entity.itemHandler.getStackInSlot(1).isEmpty()||!entity.itemHandler.getStackInSlot(2).isEmpty()||!entity.itemHandler.getStackInSlot(3).isEmpty()||!entity.itemHandler.getStackInSlot(4).isEmpty()||!entity.itemHandler.getStackInSlot(5).isEmpty()||!entity.itemHandler.getStackInSlot(6).isEmpty()||!entity.itemHandler.getStackInSlot(7).isEmpty()){
            ItemStack item = entity.additemtags().getItem(slot);
            pPlayer.setItemInHand(InteractionHand.MAIN_HAND, item);

            entity.itemHandler.setStackInSlot(slot, ItemStack.EMPTY);
        }else if (pPlayer.getLevel().isClientSide){
            pPlayer.sendMessage(new TextComponent("There are no items inside"), pPlayer.getUUID());
        }
    }

    /**
     * prints the elements that are in the tileentity
     * @param player player clicking the tileentity
     * @param entity tileentity being clicked
     */
    public void getItems(Player player, ElementalCombinerTile entity) {
        ArrayList<String> items = new ArrayList<String>();
        ArrayList<Integer> counts = new ArrayList<Integer>();
        boolean allEmpty = true;

        for (int i=0; i<=7;i++){
            if(!entity.itemHandler.getStackInSlot(i).isEmpty()&&entity.itemHandler.getStackInSlot(i) != null){
                items.add(entity.itemHandler.getStackInSlot(i).getItem().toString());
                counts.add(entity.itemHandler.getStackInSlot(i).getCount());
                allEmpty = false;
            }
        }

        SimpleContainer tagItems = entity.additemtags();

        TextComponent message = new TextComponent("");
        if (!allEmpty) {
            for (int i = 0; i < items.size(); i++) {
                String Name = null;
                if(entity.serializeNBT().getCompound("inv").getList("Items", 10).getCompound(i).contains("ForgeCaps")){
                    if(entity.serializeNBT().getCompound("inv").getList("Items", 10).getCompound(i).getCompound("ForgeCaps").contains("element")) {
                        ElementData Element = ElementData.of(entity.serializeNBT().getCompound("inv").getList("Items", 10).getCompound(i).getCompound("ForgeCaps").getCompound("element"));
                        Name = Element.displayName;
                    }
                }else if(entity.serializeNBT().getCompound("inv").getList("Items", 10).getCompound(i).contains("tag")) {
                    if (entity.serializeNBT().getCompound("inv").getList("Items", 10).getCompound(i).getCompound("tag").contains("element")) {
                        ElementData Element = ElementData.of(entity.serializeNBT().getCompound("inv").getList("Items", 10).getCompound(i).getCompound("tag").getCompound("element"));
                        Name = Element.displayName;
                    }
                }
                message.append(counts.get(i).toString());
                message.append(" x ");
                if (entity.itemHandler.getStackInSlot(i).is(ModItems.ELEMENT_CRAFTING.get())) {
                    message.append("Element: " + Name);
                } else if(entity.itemHandler.getStackInSlot(i).hasTag()){
                    message.append(items.get(i) + " {"+tagItems.getItem(i).getTag().toString()+"}");
                }else{
                    message.append(items.get(i));
                }
                if(i+1<items.size()) {
                    message.append("\n");
                    message.append("");
                }
            }
        }else{
            message.append("There are no items inside");
        }
        player.sendMessage(message, player.getUUID());
    }
}