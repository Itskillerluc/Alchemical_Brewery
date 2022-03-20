package com.itskillerluc.alchemicalbrewery.tileentity;

import com.itskillerluc.alchemicalbrewery.data.recipes.ElementalCombinerRecipe;
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
import java.util.concurrent.atomic.AtomicInteger;

public class ElementalCombinerTile extends BlockEntity {

    private final ItemStackHandler itemHandler = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private LazyOptional<IItemHandler> handler = LazyOptional.of(()->itemHandler);

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        List<ItemStack> items = new ArrayList<ItemStack>();
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            items.add(itemHandler.getStackInSlot(i));
        }
        AtomicInteger increment = new AtomicInteger(0);
        items.forEach((ele)->{
            if(ele.is(ModItems.ELEMENT_BASIC.get())) {
                ele.getOrCreateTag().putString("Element", serializeNBT().getCompound("inv").getList("Items", 10).getCompound(increment.get()).getCompound("ForgeCaps").getString("Element"));
                ele.getOrCreateTag().putInt("ItemColor", serializeNBT().getCompound("inv").getList("Items", 10).getCompound(increment.get()).getCompound("ForgeCaps").getInt("ItemColor"));
                increment.getAndIncrement();
            }
        });
        for(int i=0;i<items.size();i++){
            itemHandler.setStackInSlot(i,items.get(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
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

    public static boolean hasRecipe(ElementalCombinerTile entity) {
        Level level = entity.level;

        List<ItemStack> items = new ArrayList<ItemStack>();
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            items.add(entity.itemHandler.getStackInSlot(i));
        }
        AtomicInteger increment = new AtomicInteger(0);
        items.forEach((ele)->{
            if(ele.is(ModItems.ELEMENT_BASIC.get())) {
                ele.getOrCreateTag().putString("Element", entity.serializeNBT().getCompound("inv").getList("Items", 10).getCompound(increment.get()).getCompound("ForgeCaps").getString("Element"));
                increment.getAndIncrement();
            }
        });
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<ElementalCombinerRecipe> match = level.getRecipeManager()
                .getRecipeFor(ElementalCombinerRecipe.Type.INSTANCE, inventory, level);

        return match.isPresent();
    }

    public static void craftItem(ElementalCombinerTile entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<ElementalCombinerRecipe> match = level.getRecipeManager()
                .getRecipeFor(ElementalCombinerRecipe.Type.INSTANCE, inventory, level);

        if(match.isPresent()) {
            ItemStack resultItem = match.get().getResultItem();
            CompoundTag nbt = resultItem.getOrCreateTag();
            if(match.get().isHaselement()) {
                nbt.putInt("ItemColor", match.get().getItemcolor());
                nbt.putString("Element", match.get().getelement());
            }
            for (int i = 0; i < match.get().size(); i++) {
                entity.itemHandler.extractItem(i, match.get().getCount(i), false);
            }
            level.addFreshEntity(new ItemEntity(level, entity.getBlockPos().getX(), entity.getBlockPos().getY(),entity.getBlockPos().getZ(), resultItem));


        }
    }

    public void insertItem(Item item, int count, CompoundTag tag, ElementalCombinerTile entity, Player player) {
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }
        int slot = (!entity.itemHandler.getStackInSlot(7).isEmpty()) ? 7 : (!entity.itemHandler.getStackInSlot(6).isEmpty()) ? 6 : (!entity.itemHandler.getStackInSlot(5).isEmpty()) ? 5 : (!entity.itemHandler.getStackInSlot(4).isEmpty()) ? 4 : (!entity.itemHandler.getStackInSlot(3).isEmpty()) ? 3 : (!entity.itemHandler.getStackInSlot(2).isEmpty()) ? 2 : (!entity.itemHandler.getStackInSlot(1).isEmpty()) ? 1 : 0;
        ItemStack itemtoinsert = new ItemStack(item, count, tag);
        if(entity.itemHandler.getStackInSlot(0).isEmpty()||entity.itemHandler.getStackInSlot(1).isEmpty()||entity.itemHandler.getStackInSlot(2).isEmpty()||entity.itemHandler.getStackInSlot(3).isEmpty()||entity.itemHandler.getStackInSlot(4).isEmpty()||entity.itemHandler.getStackInSlot(5).isEmpty()||entity.itemHandler.getStackInSlot(6).isEmpty()||entity.itemHandler.getStackInSlot(7).isEmpty()) {
            entity.itemHandler.insertItem(
                    (entity.itemHandler.getStackInSlot(0).isEmpty()) ? 0 : (entity.itemHandler.getStackInSlot(1).isEmpty()) ? 1 : (entity.itemHandler.getStackInSlot(2).isEmpty()) ? 2 : (entity.itemHandler.getStackInSlot(3).isEmpty()) ? 3 : (entity.itemHandler.getStackInSlot(4).isEmpty()) ? 4 : (entity.itemHandler.getStackInSlot(5).isEmpty()) ? 5 : (entity.itemHandler.getStackInSlot(6).isEmpty()) ? 6 : 7,
                    itemtoinsert, false);
            player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }else{
            player.sendMessage(new TextComponent("There is no empty space left"), player.getUUID());
        }
    }

    public void extractItem(Player pPlayer, ElementalCombinerTile entity) {
        /*
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }*/
        int slot = (!entity.itemHandler.getStackInSlot(7).isEmpty()) ? 7 : (!entity.itemHandler.getStackInSlot(6).isEmpty()) ? 6 : (!entity.itemHandler.getStackInSlot(5).isEmpty()) ? 5 : (!entity.itemHandler.getStackInSlot(4).isEmpty()) ? 4 : (!entity.itemHandler.getStackInSlot(3).isEmpty()) ? 3 : (!entity.itemHandler.getStackInSlot(2).isEmpty()) ? 2 : (!entity.itemHandler.getStackInSlot(1).isEmpty()) ? 1 : 0;
        if(!entity.itemHandler.getStackInSlot(0).isEmpty()||!entity.itemHandler.getStackInSlot(1).isEmpty()||!entity.itemHandler.getStackInSlot(2).isEmpty()||!entity.itemHandler.getStackInSlot(3).isEmpty()||!entity.itemHandler.getStackInSlot(4).isEmpty()||!entity.itemHandler.getStackInSlot(5).isEmpty()||!entity.itemHandler.getStackInSlot(6).isEmpty()||!entity.itemHandler.getStackInSlot(7).isEmpty()){
            ItemStack item = entity.itemHandler.getStackInSlot(slot).copy();
            if(item.is(ModItems.ELEMENT_BASIC.get())){
                item.getOrCreateTag().putString("Element", entity.serializeNBT().getCompound("inv").getList("Items", 10).getCompound(slot).getCompound("ForgeCaps").getString("Element"));
                item.getOrCreateTag().putInt("ItemColor", entity.serializeNBT().getCompound("inv").getList("Items", 10).getCompound(slot).getCompound("ForgeCaps").getInt("ItemColor"));
            }
            pPlayer.setItemInHand(InteractionHand.MAIN_HAND, item);

            entity.itemHandler.setStackInSlot(slot, ItemStack.EMPTY);
        }else{
            pPlayer.sendMessage(new TextComponent("There are no items inside"), pPlayer.getUUID());
        }
    }

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
        TextComponent message = new TextComponent("");
        if (!allEmpty) {
            for (int i = 0; i < items.size(); i++) {
                message.append(counts.get(i).toString());
                message.append(" x ");
                if(entity.itemHandler.getStackInSlot(i).is(ModItems.ELEMENT_BASIC.get())){
                        message.append("Element: "+ entity.serializeNBT().getCompound("inv").getList("Items", 10).getCompound(i).getCompound("ForgeCaps").getString("Element"));
                }else {
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