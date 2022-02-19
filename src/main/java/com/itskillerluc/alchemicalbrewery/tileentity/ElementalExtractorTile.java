package com.itskillerluc.alchemicalbrewery.tileentity;

import com.itskillerluc.alchemicalbrewery.block.custom.ElementalExtractorBlock;
import com.itskillerluc.alchemicalbrewery.container.ElementalExtractorContainer;
import com.itskillerluc.alchemicalbrewery.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.TickTask;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jline.utils.Log;

public class ElementalExtractorTile extends BaseContainerBlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = createHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(()->itemHandler);

    protected final ContainerData data;
    int BurnTime;
    final int TotalBurnTime = 1000;
    boolean IsBurning;

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
                switch (pIndex){
                    case 0: return (ElementalExtractorTile.this.IsBurning) ? 1 : 0;
                    default: return 0;
                }
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex){
                    case 0: ElementalExtractorTile.this.IsBurning = pValue != 0;
                }
            }

            @Override
            public int getCount() {
                return 1;
            }
        };
    }


    @Override
    public void load(CompoundTag pTag) {
        this.IsBurning = pTag.getBoolean("IsBurning");
        this.BurnTime = pTag.getInt("BurnTime");
        itemHandler.deserializeNBT(serializeNBT().getCompound("inv"));
        super.load(pTag);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putBoolean("IsBurning", IsBurning);
        pTag.putInt("BurnTime", BurnTime);
        pTag.put("inv", itemHandler.serializeNBT());
        super.saveAdditional(pTag);
    }

    private ItemStackHandler createHandler(){
        return new ItemStackHandler(4){
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                switch (slot){
                    //TODO: Change iron ingot to capsule
                    case 1: return stack.getItem() == Items.IRON_INGOT;
                    case 2: return stack.getItem() == ModItems.FUELMIX.get();
                    default: return false;
                }
            }

            @NotNull
            @Override
            public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                if (!isItemValid(slot, stack)){
                    return stack;
                }

                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return handler.cast();
        }

        return super.getCapability(cap, side);
    }



    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        return super.getCapability(cap);
    }

    public void ElementCreate(){
        //TODO: change these to correct items
        boolean ElementInFirstSlot = this.itemHandler.getStackInSlot(0).getCount() > 0 && this.itemHandler.getStackInSlot(0).getItem() == Items.DIAMOND;
        boolean CapsuleInSecondSlot = this.itemHandler.getStackInSlot(1).getCount() > 0 && this.itemHandler.getStackInSlot(1).getItem() == Items.IRON_INGOT;
        boolean FuelInThirdSlot = this.itemHandler.getStackInSlot(2).getCount() > 0 && this.itemHandler.getStackInSlot(2).getItem() == ModItems.FUELMIX.get();
        if (ElementInFirstSlot){
            Log.info("element");
        }
        if (CapsuleInSecondSlot){
            Log.info("Capsule");
        }
        if (FuelInThirdSlot){
            Log.info("fuel");
        }
        if (ElementInFirstSlot && CapsuleInSecondSlot && FuelInThirdSlot && !IsBurning){
            Log.info("craft");
            this.itemHandler.getStackInSlot(0).shrink(1);
            this.itemHandler.getStackInSlot(1).shrink(1);
            this.itemHandler.getStackInSlot(2).shrink(1);
            this.BurnTime = TotalBurnTime;
            this.IsBurning = true;
        }
    }

    public void OutputItem(){
        //TODO: change to correct item
        this.itemHandler.insertItem(4, new ItemStack(Items.NETHER_STAR), false);
        IsBurning = false;

    }
    /*
    @Override
    public Component getDisplayName() {
        return new TextComponent("Elemental Extractor");
    }*/

    @Override
    protected Component getDefaultName() {
        return new TextComponent("Elemental Extractor");
    }
    /*
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new ElementalExtractorContainer(pContainerId, pInventory,this, this.data);
    }*/

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new ElementalExtractorContainer(pContainerId, pInventory,this, this.data);
    }


    //FIXME
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, ElementalExtractorTile pBlockEntity){
        Log.info("tick");
        if(!pBlockEntity.IsBurning){
            pBlockEntity.ElementCreate();
        }
        if(pBlockEntity.IsBurning){
            pBlockEntity.BurnTime --;
        }
        if(pBlockEntity.BurnTime <= 0 && pBlockEntity.IsBurning){
            pBlockEntity.OutputItem();
        }
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

    @Override
    public void onLoad() {
        super.onLoad();
    }


    @Override
    public int getContainerSize() {
        return 4;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getItem(int pIndex) {
        return null;
    }

    @Override
    public ItemStack removeItem(int pIndex, int pCount) {
        return null;
    }

    @Override
    public ItemStack removeItemNoUpdate(int pIndex) {
        return null;
    }

    @Override
    public void setItem(int pIndex, ItemStack pStack) {

    }

    @Override
    public boolean stillValid(Player pPlayer) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return pPlayer.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void clearContent() {

    }
}
