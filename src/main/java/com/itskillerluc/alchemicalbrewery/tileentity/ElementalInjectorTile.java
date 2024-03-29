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

import java.util.Objects;
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
    private boolean isCrafting = false;
    private boolean finished = false;

    /**
     * what it should drop
     */
    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(Objects.requireNonNull(this.level), this.worldPosition, inventory);
    }

    public ElementalInjectorTile(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModTileEntities.ELEMENTAL_INJECTOR_TILE.get(), pWorldPosition, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> (ElementalInjectorTile.this.IsBurning) ? 1 : 0;
                    case 1 -> ElementalInjectorTile.this.BurnTime;
                    case 2 -> ElementalInjectorTile.this.TotalBurnTime;
                    case 3 -> ElementalInjectorTile.this.charge;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> ElementalInjectorTile.this.IsBurning = pValue != 0;
                    case 1 -> ElementalInjectorTile.this.BurnTime = pValue;
                    case 2 -> ElementalInjectorTile.this.TotalBurnTime = pValue;
                    case 3 -> ElementalInjectorTile.this.charge = pValue;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);
        this.charge = pTag.getInt("charge");
        this.IsBurning = pTag.getBoolean("isBurning");
        this.BurnTime = pTag.getInt("burnTime");
        this.isCrafting = pTag.getBoolean("isCrafting");
        this.finished = pTag.getBoolean("finished");
        itemHandler.deserializeNBT(pTag.getCompound("inv"));
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putInt("charge", charge);
        pTag.putBoolean("isBurning", IsBurning);
        pTag.putInt("burnTime", BurnTime);
        pTag.put("inv", itemHandler.serializeNBT());
        pTag.putBoolean("isCrafting", isCrafting);
        pTag.putBoolean("finished",finished);
        super.saveAdditional(pTag);
    }

    /**
     * handles everything that should be done every tick
     * @param pLevel level the tile entity is in
     * @param pPos blockPos of the tile entity
     * @param pState blockState of the tile entity
     * @param pBlockEntity tile entity
     */
    @SuppressWarnings("DuplicatedCode")
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, ElementalInjectorTile pBlockEntity){

        if(hasRecipe(pBlockEntity)) {
            addCharge(pBlockEntity);
            setChanged(pLevel,pPos,pState);
            if(!pBlockEntity.IsBurning) {
                craftItem(pBlockEntity);
                setChanged(pLevel, pPos, pState);
            }
            if(pBlockEntity.isCrafting) {
                pBlockEntity.BurnTime++;
                setChanged(pLevel, pPos, pState);

                if(pBlockEntity.BurnTime > pBlockEntity.TotalBurnTime) {
                    pBlockEntity.finished = true;
                    craftItem(pBlockEntity);
                }
            }
        }else{
            pBlockEntity.BurnTime = 0;
            pBlockEntity.IsBurning = false;
            pBlockEntity.finished = false;
            pBlockEntity.isCrafting = false;
        }

        pState = pState.setValue(ElementalExtractorBlock.LIT, pBlockEntity.IsBurning);
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
        Optional<ElementalInjectorRecipe> match = getElementalInjectorRecipe(entity, level, inventory);
        return match.isPresent();
    }

    @NotNull
    private static Optional<ElementalInjectorRecipe> getElementalInjectorRecipe(ElementalInjectorTile entity, Level level, SimpleContainer inventory) {
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }
        return level.getRecipeManager()
                .getRecipeFor(ElementalInjectorRecipe.Type.INSTANCE, inventory, level);
    }

    /**
     * adds the charge of an item to the tile entity
     * @param entity tile entity targeted
     */
    private static void addCharge(ElementalInjectorTile entity){

        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }
        Optional<ElementalInjectorRecipe> match = Optional.empty();
        if (level != null) {
            match = level.getRecipeManager()
                    .getRecipeFor(ElementalInjectorRecipe.Type.INSTANCE, inventory, level);
        }

        if (match.isEmpty() || !ElementalInjectorRecipe.chargeMatches(inventory)) {
            return;
        }
        entity.charge = entity.charge + match.get().getCharge(inventory, entity.level);
        if (match.get().getCharge(inventory, entity.level) != 0) {
            entity.itemHandler.extractItem(0, 1, false);
        }
    }

    /**
     * crafts the item
     * @param entity tile entity targeted
     */
    private static void craftItem(ElementalInjectorTile entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        Optional<ElementalInjectorRecipe> match = getElementalInjectorRecipe(entity, level, inventory);

        if(match.isPresent() && canInsertAmountIntoOutputSlot(inventory)
                && canInsertItemIntoOutputSlot(inventory, match.get().assemble(inventory)) && match.get().getRecipeItems().getItem() == entity.itemHandler.getStackInSlot(1).getItem()) {
            if(match.get().getCharge(inventory, entity.level)<=entity.charge){
                if(!entity.isCrafting){
                    entity.isCrafting = true;
                    entity.IsBurning = true;
                }
                if(entity.finished){
                    entity.charge = entity.charge - match.get().getCharge(inventory, entity.level);


                    entity.itemHandler.extractItem(1, 1, false);
                    entity.itemHandler.setStackInSlot(2, new ItemStack(match.get().assemble(inventory).getItem(),
                            entity.itemHandler.getStackInSlot(2).getCount() + match.get().getOutputCount()));

                    entity.BurnTime = 0;

                    entity.IsBurning = false;
                    entity.finished = false;
                    entity.isCrafting = false;
                }
            }
        }else{
            entity.BurnTime = 0;
            entity.IsBurning = false;
            entity.finished = false;
            entity.isCrafting = false;
            setChanged(Objects.requireNonNull(entity.getLevel()), entity.getBlockPos(), entity.getBlockState());
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
    public @NotNull Component getDisplayName() {
        return new TextComponent("Elemental Injector");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pInventory, @NotNull Player pPlayer) {
        return new ElementalInjectorContainer(pContainerId, pInventory, this, this.data);
    }
}
