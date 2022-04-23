package com.itskillerluc.alchemicalbrewery.tileentity;


import com.itskillerluc.alchemicalbrewery.block.custom.ElementalExtractorBlock;
import com.itskillerluc.alchemicalbrewery.container.ElementalInjectorContainer;

import com.itskillerluc.alchemicalbrewery.data.recipes.ElementalInjectorRecipe;
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

public class ElementalInjectorTile extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private LazyOptional<IItemHandler> handler = LazyOptional.of(()->itemHandler);

    protected final ContainerData data;
    int charge = 0;
    int BurnTime = 0;
    int TotalBurnTime = 60;
    boolean IsBurning;
    private boolean iscrafting = false;
    private boolean finished = false;

    /**
     * what it should drop
     */
    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public ElementalInjectorTile(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModTileEntities.ELEMENTALINJECTORTILE.get(), pWorldPosition, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                switch (pIndex){
                    case 0: return (ElementalInjectorTile.this.IsBurning) ? 1 : 0;
                    case 1: return ElementalInjectorTile.this.BurnTime;
                    case 2: return ElementalInjectorTile.this.TotalBurnTime;
                    case 3: return ElementalInjectorTile.this.charge;
                    default: return 0;
                }
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex){
                    case 0: ElementalInjectorTile.this.IsBurning = pValue != 0; break;
                    case 1: ElementalInjectorTile.this.BurnTime = pValue; break;
                    case 2: ElementalInjectorTile.this.TotalBurnTime = pValue; break;
                    case 3: ElementalInjectorTile.this.charge = pValue; break;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.charge = pTag.getInt("charge");
        this.IsBurning = pTag.getBoolean("isburning");
        this.BurnTime = pTag.getInt("burntime");
        this.iscrafting = pTag.getBoolean("iscrafting");
        this.finished = pTag.getBoolean("finished");
        itemHandler.deserializeNBT(pTag.getCompound("inv"));
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putInt("charge", charge);
        pTag.putBoolean("isburning", IsBurning);
        pTag.putInt("burntime", BurnTime);
        pTag.put("inv", itemHandler.serializeNBT());
        pTag.putBoolean("iscrafting",iscrafting);
        pTag.putBoolean("finished",finished);
        super.saveAdditional(pTag);
    }

    /**
     * handles everything that should be done every tick
     * @param pLevel level the tile entity is in
     * @param pPos blockpos of the tile entity
     * @param pState blockstate of the tile entity
     * @param pBlockEntity tile entity
     */
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, ElementalInjectorTile pBlockEntity){

        if(hasRecipe(pBlockEntity)) {
            addCharge(pBlockEntity);
            setChanged(pLevel,pPos,pState);
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
        }

        pState = pState.setValue(ElementalExtractorBlock.LIT, Boolean.valueOf(pBlockEntity.IsBurning));
        pLevel.setBlock(pPos, pState, 3);
        setChanged(pLevel, pPos, pState);
    }

    /**
     * @param entity tile entity targeted
     * @return true if a recipe instance of the type is present
     */
    private static boolean hasRecipe(ElementalInjectorTile entity) {

        Level level = entity.getLevel();
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<ElementalInjectorRecipe> match = level.getRecipeManager()
                .getRecipeFor(ElementalInjectorRecipe.Type.INSTANCE, inventory, level);

        return match.isPresent();
    }

    /**
     * adds the charge of a item to the tile entity
     * @param entity tile entity targeted
     */
    private static void addCharge(ElementalInjectorTile entity){

        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }
        Optional<ElementalInjectorRecipe> match = level.getRecipeManager()
                .getRecipeFor(ElementalInjectorRecipe.Type.INSTANCE, inventory, level);

        if(match.isPresent()){
            if(match.get().getResultItem().is(entity.itemHandler.getStackInSlot(0).getItem())){
                entity.charge = entity.charge + match.get().getCharge();
                entity.itemHandler.extractItem(0, 1, false);
            }
        }
    }

    /**
     * crafts the item
     * @param entity tile enitty targeted
     */
    private static void craftItem(ElementalInjectorTile entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<ElementalInjectorRecipe> match = level.getRecipeManager()
                .getRecipeFor(ElementalInjectorRecipe.Type.INSTANCE, inventory, level);

        if(match.isPresent() && canInsertAmountIntoOutputSlot(inventory)
                && canInsertItemIntoOutputSlot(inventory, match.get().getResultItem()) && match.get().getRecipeItems().getItem() == entity.itemHandler.getStackInSlot(1).getItem() && (entity.itemHandler.getStackInSlot(1).hasTag()) && entity.itemHandler.getStackInSlot(1).getTag().getString("Element").matches(match.get().getElement())) {
            if(match.get().getCharge()<=entity.charge){
                if(!entity.iscrafting){
                    entity.iscrafting = true;
                    entity.IsBurning = true;
                }
                if(entity.finished){
                    entity.charge = entity.charge - match.get().getCharge();


                    entity.itemHandler.extractItem(1, 1, false);
                    ItemStack result = new ItemStack(match.get().getResultItem().getItem(), entity.itemHandler.getStackInSlot(2).getCount() + 1);
                    entity.itemHandler.setStackInSlot(2, new ItemStack(match.get().getResultItem().getItem(),
                            entity.itemHandler.getStackInSlot(2).getCount() + match.get().getOutputcount()));

                    entity.BurnTime = 0;

                    entity.IsBurning = false;
                    entity.finished = false;
                    entity.iscrafting = false;
                }
            }
        }else{
            entity.BurnTime = 0;
            setChanged(entity.getLevel(), entity.getBlockPos(), entity.getBlockState());
        }
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
     * checks if it fits
     */
    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack output) {
        return inventory.getItem(2).getItem() == output.getItem() || inventory.getItem(2).isEmpty();
    }

    /**
     * checks if it fits
     */
    private static boolean canInsertAmountIntoOutputSlot(SimpleContainer inventory) {
        return inventory.getItem(2).getMaxStackSize() > inventory.getItem(2).getCount();
    }
    @Override
    public Component getDisplayName() {
        return new TextComponent("Elemental Injector");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new ElementalInjectorContainer(pContainerId, pInventory, this, this.data);
    }
}
