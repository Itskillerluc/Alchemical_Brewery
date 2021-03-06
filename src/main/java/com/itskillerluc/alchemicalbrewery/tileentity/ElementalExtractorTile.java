package com.itskillerluc.alchemicalbrewery.tileentity;

import com.itskillerluc.alchemicalbrewery.block.custom.ElementalExtractorBlock;
import com.itskillerluc.alchemicalbrewery.container.ElementalExtractorContainer;
import com.itskillerluc.alchemicalbrewery.data.recipes.ElementalExtractorRecipe;
import com.itskillerluc.alchemicalbrewery.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
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

import java.util.Optional;

public class ElementalExtractorTile extends BlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private LazyOptional<IItemHandler> handler = LazyOptional.of(()->itemHandler);

    protected final ContainerData data;
    int BurnTime = 0;
    int TotalBurnTime = 1000;
    boolean IsBurning;
    private boolean iscrafting = false;
    private boolean finished = false;

    /**
     * handle the drops
     */
    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }



    public ElementalExtractorTile(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModTileEntities.ELEMENTALEXTRACTORTILE.get(), pWorldPosition, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> (ElementalExtractorTile.this.IsBurning) ? 1 : 0;
                    case 1 -> ElementalExtractorTile.this.BurnTime;
                    case 2 -> ElementalExtractorTile.this.TotalBurnTime;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> ElementalExtractorTile.this.IsBurning = pValue != 0;
                    case 1 -> ElementalExtractorTile.this.BurnTime = pValue;
                    case 2 -> ElementalExtractorTile.this.TotalBurnTime = pValue;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.IsBurning = pTag.getBoolean("isburning");
        this.BurnTime = pTag.getInt("burntime");
        this.iscrafting = pTag.getBoolean("iscrafting");
        this.finished = pTag.getBoolean("finished");
        itemHandler.deserializeNBT(pTag.getCompound("inv"));
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putBoolean("isburning", IsBurning);
        pTag.putInt("burntime", BurnTime);
        pTag.put("inv", itemHandler.serializeNBT());
        pTag.putBoolean("iscrafting",iscrafting);
        pTag.putBoolean("finished",finished);
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

    /**
     * handles all the things that should be done every tick
     * @param pLevel level the tile entity is in
     * @param pPos blockpos of the tile entity
     * @param pState blockstate of the tile entity
     * @param pBlockEntity tile entity
     */
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, ElementalExtractorTile pBlockEntity){

        if(hasRecipe(pBlockEntity)) {
            if(!pBlockEntity.IsBurning) {
                craftItem(pBlockEntity);
                setChanged(pLevel, pPos, pState);
            }
            if(pBlockEntity.iscrafting) {
                pBlockEntity.BurnTime++;
                setChanged(pLevel, pPos, pState);
                if(pBlockEntity.BurnTime > pBlockEntity.TotalBurnTime) {
                    pBlockEntity.finished = true;
                    craftItem(pBlockEntity);
                }
            }
        } else {
            pBlockEntity.resetProgress();
            setChanged(pLevel, pPos, pState);
        }

        pState = pState.setValue(ElementalExtractorBlock.LIT, Boolean.valueOf(pBlockEntity.IsBurning));
        pLevel.setBlock(pPos, pState, 3);
        setChanged(pLevel, pPos, pState);
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
     * @param entity tile entity being targeted
     * @return true if the tile entity has a recipe instance
     */
    private static boolean hasRecipe(ElementalExtractorTile entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<ElementalExtractorRecipe> match = level.getRecipeManager()
                .getRecipeFor(ElementalExtractorRecipe.Type.INSTANCE, inventory, level);

        return match.isPresent() && canInsertAmountIntoOutputSlot(inventory)
                && canInsertItemIntoOutputSlot(inventory, match.get().getResultItem());
    }

    /**
     * craft the item
     * @param entity tile entity being targeted
     */
    private static void craftItem(ElementalExtractorTile entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<ElementalExtractorRecipe> match = level.getRecipeManager()
                .getRecipeFor(ElementalExtractorRecipe.Type.INSTANCE, inventory, level);
        boolean present = false;
        if(match.isPresent()) {
            present = true;
            if(!entity.iscrafting){
                entity.iscrafting = true;
                entity.IsBurning = true;
            }
            if(entity.finished){
                if(!match.get().getIfCapsule()) {
                    entity.itemHandler.extractItem(0, 1, false);
                    entity.itemHandler.extractItem(2,1, false);
                    entity.itemHandler.extractItem(1, 1, false);

                    entity.itemHandler.setStackInSlot(3, new ItemStack(match.get().getResultItem().getItem(),
                            entity.itemHandler.getStackInSlot(3).getCount() + match.get().getOutputcount()));

                    entity.resetProgress();

                    entity.IsBurning = false;
                    entity.finished = false;
                    entity.iscrafting = false;
                }else{
                    if(entity.itemHandler.getStackInSlot(1).is(ModItems.CAPSULE_SMALL.get())){
                        ItemStack result = new ItemStack(match.get().getResultItem().getItem(), entity.itemHandler.getStackInSlot(3).getCount() + 1);
                        if(match.get().getResultItem().is(ModItems.ELEMENT_BASIC.get())){
                            CompoundTag nbt = result.getOrCreateTag();
                            nbt.putInt("ItemColor", match.get().getItemColor());
                            nbt.putInt("SecItemColor",match.get().getSecitemColor());
                            nbt.putString("Element", match.get().getElement());
                        }
                        entity.itemHandler.extractItem(0, 1, false);
                        entity.itemHandler.extractItem(1, 1, false);
                        entity.itemHandler.extractItem(2,1, false);
                        entity.itemHandler.setStackInSlot(3, result);
                        entity.resetProgress();
                        entity.IsBurning = false;
                        entity.finished = false;
                        entity.iscrafting = false;

                    }else if(entity.itemHandler.getStackInSlot(1).is(ModItems.CAPSULE_MEDIUM.get())){
                        ItemStack result = new ItemStack(match.get().getResultItem().getItem(), entity.itemHandler.getStackInSlot(3).getCount() + 4);
                        if(match.get().getResultItem().is(ModItems.ELEMENT_BASIC.get())){
                            CompoundTag nbt = result.getOrCreateTag();
                            nbt.putInt("ItemColor", match.get().getItemColor());
                            nbt.putInt("SecItemColor",match.get().getSecitemColor());
                            nbt.putString("Element", match.get().getElement());
                        }
                        entity.itemHandler.extractItem(0, 1, false);
                        entity.itemHandler.extractItem(1, 1, false);
                        entity.itemHandler.extractItem(2,1, false);
                        entity.itemHandler.setStackInSlot(3, result);
                        entity.resetProgress();
                        entity.IsBurning = false;
                        entity.finished = false;
                        entity.iscrafting = false;

                    }else if(entity.itemHandler.getStackInSlot(1).is(ModItems.CAPSULE_LARGE.get())){
                        ItemStack result = new ItemStack(match.get().getResultItem().getItem(), entity.itemHandler.getStackInSlot(3).getCount() + 9);
                        if(match.get().getResultItem().is(ModItems.ELEMENT_BASIC.get())){
                            CompoundTag nbt = result.getOrCreateTag();
                            nbt.putInt("ItemColor", match.get().getItemColor());
                            nbt.putInt("SecItemColor",match.get().getSecitemColor());
                            nbt.putString("Element", match.get().getElement());
                        }
                        entity.itemHandler.extractItem(0, 1, false);
                        entity.itemHandler.extractItem(1, 1, false);
                        entity.itemHandler.extractItem(2,1, false);
                        entity.itemHandler.setStackInSlot(3, result);
                        entity.resetProgress();
                        entity.IsBurning = false;
                        entity.finished = false;
                        entity.iscrafting = false;
                    }
                }
            }
        }
    }

    private void resetProgress() {
        this.BurnTime = 0;
        this.IsBurning = false;
        this.finished = false;
        this.iscrafting = false;
    }

    /**
     * checks if its the same type
     */
    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack output) {
        return inventory.getItem(3).getItem() == output.getItem() || inventory.getItem(3).isEmpty();
    }

    /**
     * checks if it still fits
     */
    private static boolean canInsertAmountIntoOutputSlot(SimpleContainer inventory) {
        return inventory.getItem(3).getMaxStackSize() > inventory.getItem(3).getCount();
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("Elemental Extractor");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new ElementalExtractorContainer(pContainerId, pInventory, this, this.data);
    }
}