package com.Myfic.banner.BanEnforcer;

import com.Myfic.banner.MyConfig.config;
import com.Myfic.banner.Utils.FoodState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioChangeEvent;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@EventBusSubscriber
public class BanEvent {
    private static int containerCheckTimer = 0;
    private static int inventoryCheckTimer = 0;
    private static final Map<UUID, FoodState> preEatingStates = new HashMap<>();
    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getSlot().isArmor() || event.getSlot() == EquipmentSlot.OFFHAND) {
                ItemStack newItem = event.getTo();
                if (Mycheck.Check(newItem)) {
                    player.setItemSlot(event.getSlot(), event.getFrom());
                    //player.sendSystemMessage(Component.translatable("你装备了被禁止物品"));
                }
            }
        }
    }
    @SubscribeEvent
    public static void onServerTickPost(ServerTickEvent.Post event) {
        var server = event.getServer();
        for (ServerLevel level : server.getAllLevels()) {
            for (ServerPlayer player : level.players()) {
                if (player.containerMenu != player.inventoryMenu && config.CONTAINERINSPECTIONSTATE.get()) {
                    containerCheckTimer++;
                    if (containerCheckTimer < config.CONTAINERINSPEXCEPTION.get()) continue;
                    containerCheckTimer = 0;
                    AbstractContainerMenu openContainer = player.containerMenu;
                    boolean foundAndRemoved = false;
                    for (Slot slot : openContainer.slots) {
                        ItemStack stack = slot.getItem();
                        if (!stack.isEmpty() && Mycheck.Check(stack)) {
                            slot.set(ItemStack.EMPTY);
                            foundAndRemoved = true;
                        }
                    }
                    //if (foundAndRemoved) {player.sendSystemMessage(Component.literal("你打开的容器中有违禁品。"));}
                }


                if(config.FULLBODYINSPECTIONSTATE.get()){
                    inventoryCheckTimer++;
                    if (inventoryCheckTimer < config.FULLBODYEXCEPTION.get()) continue; // 每2秒检查一次（40 tick）
                    inventoryCheckTimer = 0;



                    // 检查主库存（包括快捷栏）
                    Inventory inventory = player.getInventory();
                    for (int i = 0; i < inventory.getContainerSize(); i++) {
                        ItemStack stack = inventory.getItem(i);
                        if (!stack.isEmpty() && Mycheck.Check(stack)) {
                            inventory.setItem(i, ItemStack.EMPTY);
                        }
                    }
                    int[] armorSlots = {36, 37, 38, 39}; // 头盔、胸甲、护腿、靴子
                    for (int slot : armorSlots) {
                        ItemStack stack = inventory.getItem(slot);
                        if (!stack.isEmpty() && Mycheck.Check(stack)) {
                            inventory.setItem(slot, ItemStack.EMPTY);
                        }
                    }
                    try{
                        Optional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(player);

                        curiosInventory.ifPresent(curios -> {
                            Map<String, ICurioStacksHandler> curiosHandlers = curios.getCurios();
                            for (ICurioStacksHandler stacksHandler : curiosHandlers.values()) {
                                IDynamicStackHandler stackHandler = stacksHandler.getStacks();
                                IDynamicStackHandler cosmeticStackHandler = stacksHandler.getCosmeticStacks();

                                // 检查普通Curios槽位
                                for (int i = 0; i < stackHandler.getSlots(); i++) {
                                    ItemStack stack = stackHandler.getStackInSlot(i);
                                    if (!stack.isEmpty() && Mycheck.Check(stack)) {
                                        stackHandler.setStackInSlot(i, ItemStack.EMPTY);

                                    }
                                }

                                // 检查装饰Curios槽位
                                for (int i = 0; i < cosmeticStackHandler.getSlots(); i++) {
                                    ItemStack stack = cosmeticStackHandler.getStackInSlot(i);
                                    if (!stack.isEmpty() && Mycheck.Check(stack)) {
                                        cosmeticStackHandler.setStackInSlot(i, ItemStack.EMPTY);

                                    }
                                }

                            }
                        });
                        inventory.setChanged();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        }

    }
    @SubscribeEvent
    public static void BreakBlock(BlockEvent.BreakEvent event){
        Player player = event.getPlayer();
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

        if(!player.isCreative() && Mycheck.Check(stack)){
            event.setCanceled(true);
            //player.sendSystemMessage(Component.translatable("你使用了被禁止物品"));
        }

    }
    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.RightClickItem event){
        Player player = event.getEntity();
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if(Mycheck.Check(stack)){
            event.setCanceled(true);
            //player.sendSystemMessage(Component.translatable("该物品被禁止交互"));
        }

    }
    @SubscribeEvent
    public static void onPlayerInteract2(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (Mycheck.Check(stack)) {
            event.setCanceled(true);
            //player.sendSystemMessage(Component.translatable("该物品被禁止与实体交互"));
        }

    }
    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (Mycheck.Check(stack)) {
                event.setCanceled(true);
                //player.sendSystemMessage(Component.translatable("你放置了被禁止物品"));
            }
        }
    }
    @SubscribeEvent
    public static void curseInventory(CurioChangeEvent event){
        LivingEntity livingEntity =  event.getEntity();
        if(livingEntity instanceof Player player){

            ItemStack stack = event.getTo();

            if(Mycheck.Check(stack)){
                CuriosApi.getCuriosInventory(player).ifPresent(curios -> {
                    curios.setEquippedCurio(event.getIdentifier(), event.getSlotIndex(), event.getFrom());
                });
            }
        }
    }
    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event){
        Player target = event.getEntity();
        ItemStack stack = target.getOffhandItem();
        ItemStack stack1 = target.getMainHandItem();
        if(Mycheck.Check(stack) || Mycheck.Check(stack1)){
            event.setCanceled(true);
            //target.sendSystemMessage(Component.translatable("攻击被取消"));
        }
    }
    @SubscribeEvent
    public static void onStartUseItem(LivingEntityUseItemEvent.Start event){
       LivingEntity entity = event.getEntity();

       if(entity instanceof ServerPlayer player){
           ItemStack itemStack = event.getItem();
           if (Mycheck.Check(itemStack)) {
               event.setCanceled(true);
           }
           FoodData foodData = player.getFoodData();
           FoodState flage = FoodState.getInstance();
           flage.setRecord(foodData.getFoodLevel(), foodData.getSaturationLevel());
           preEatingStates.put(player.getUUID(), flage);
           //player.sendSystemMessage(Component.translatable("开始吃了"));


       }

    }

    @SubscribeEvent
    public static void onFinishUseItem(LivingEntityUseItemEvent.Finish event){
        if (event.getEntity() instanceof ServerPlayer player) {
            ItemStack usedItem = event.getItem();
            UUID playerId = player.getUUID();
            if (Mycheck.Check(usedItem)) {
                FoodState preState = preEatingStates.get(playerId);
                if (preState != null) {
                    FoodData foodData = player.getFoodData();
                    foodData.setFoodLevel(preState.beforeFoodLeve);
                    foodData.setSaturation(preState.beforeSaturation);
                    //player.sendSystemMessage(Component.literal("禁止食用违禁食物！效果已移除。"));
                }
            }
        }
    }
    //退出时清理数据，避免占用内存
    @SubscribeEvent
    public static void onPlayerLogout(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            preEatingStates.remove(player.getUUID());
        }
    }


}
