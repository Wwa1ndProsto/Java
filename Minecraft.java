// Main mod file for Minecraft 1.16.5

package com.example.killauramod; // Укажите свой пакет

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(KillAuraMod.MOD_ID)
public class KillAuraMod {
    public static final String MOD_ID = "killauramod";

    private Minecraft mc = Minecraft.getInstance();
    private boolean auraEnabled = false;
    private float range = 4.5f;
    private int attackSpeed = 10; // Тиков между атаками
    private int currentAttackTimer = 0;

    public KillAuraMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Здесь можно добавить код, который выполняется при инициализации мода.
    }

    // Метод для переключения состояния ауры
    public void toggleAura() {
        this.auraEnabled = !this.auraEnabled;
        if (auraEnabled) {
            System.out.println("KillAura включена.");
        } else {
            System.out.println("KillAura выключена.");
        }
    }

    // Обработчик тиков клиента
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ClientPlayerEntity player = mc.player;
            if (player == null || !auraEnabled) {
                return;
            }

            // Уменьшаем таймер атаки
            if (currentAttackTimer > 0) {
                currentAttackTimer--;
                return;
            }

            // Ищем ближайших врагов
            for (Entity entity : mc.world.getLoadedEntities()) {
                // Проверяем, что сущность не является игроком, другим мобом, которому мы не враждебны, и не мертва
                if (entity != null && entity != player && entity.isAlive() &&
                    !(entity.getType().equals(EntityType.PLAYER)) && // Можно добавить более сложную логику враждебности
                    player.getDistanceSq(entity) <= range * range) {

                    attackTarget(entity);
                    currentAttackTimer = attackSpeed; // Сбрасываем таймер
                    return; // Атакуем только одну сущность за тик
                }
            }
        }
    }

    // Метод для атаки выбранной цели
    private void attackTarget(Entity target) {
        // Направляем взгляд на цель
        mc.player.lookAt(target); // Простая функция взгляда, можно улучшить

        // Атакуем
        mc.playerController.attackEntity(mc.player, target);
        mc.player.swingArm(Hand.MAIN_HAND); // Взмах рукой
    }

    // Геттеры для настроек
    public boolean isAuraEnabled() {
        return auraEnabled;
    }

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
    }

    public int getAttackSpeed() {
        return attackSpeed;
    }

    public void setAttackSpeed(int attackSpeed) {
        this.attackSpeed = attackSpeed;
    }
}
