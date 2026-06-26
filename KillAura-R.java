// Пример реализации KillAura для Minecraft на Java

// Импорты для работы с Minecraft Modding API (Forge/Fabric)
// Предполагается, что у вас настроена среда разработки для модов Minecraft.

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand; // Для указания руки, которой будет атаковать

public class KillAuraR {

    private Minecraft mc = Minecraft.getMinecraft(); // Получение экземпляра Minecraft
    private boolean isEnabled = false; // Флаг, включающий/выключающий ауру
    private float range = 4.5f; // Радиус действия ауры
    private int attackDelay = 10; // Задержка между атаками (в тиках)
    private int currentDelay = 0; // Текущий счетчик задержки

    // Метод для включения/выключения ауры
    public void toggleAura() {
        this.isEnabled = !this.isEnabled;
        if (this.isEnabled) {
            System.out.println("KillAura включена!");
        } else {
            System.out.println("KillAura выключена.");
        }
    }

    // Метод, который будет вызываться каждый игровой тик (например, в событии TickEvent)
    public void onGameTick() {
        if (mc.player == null || !isEnabled) {
            return; // Выход, если игрок не существует или аура выключена
        }

        // Уменьшаем счетчик задержки
        if (currentDelay > 0) {
            currentDelay--;
            return;
        }

        // Ищем ближайших врагов в радиусе
        for (Entity entity : mc.world.loadedEntityList) {
            // Проверяем, что это не игрок, не животное (можно добавить фильтры)
            // и что он находится в пределах досягаемости
            if (entity != null && entity != mc.player && !entity.isDead && entity.getDistanceSq(mc.player) <= range * range) {
                // Дополнительная проверка на принадлежность (например, только враждебные мобы)
                // if (entity instanceof EntityLivingBase && !((EntityLivingBase) entity).isFriend(mc.player)) { // Пример
                    attackEntity(entity);
                    currentDelay = attackDelay; // Сбрасываем задержку после атаки
                    return; // Атакуем только одну сущность за тик
                // }
            }
        }
    }

    // Метод для атаки сущности
    private void attackEntity(Entity entity) {
        // Смотрим на сущность
        mc.player.rotationYaw = (float) Math.atan2(entity.posZ - mc.player.posZ, entity.posX - mc.player.posX) * (180 / (float) Math.PI) - 90.0F;
        mc.player.rotationPitch = (float) -Math.atan2(entity.posY + entity.getEyeHeight() - mc.player.posY, mc.player.getDistance(entity)) * (180 / (float) Math.PI);
        mc.player.rotationPitch = Math.max(-90.0F, Math.min(90.0F, mc.player.rotationPitch)); // Ограничиваем угол наклона

        // Атакуем
        mc.playerController.attackEntity(mc.player, entity);
        mc.player.swingArm(EnumHand.MAIN_HAND); // Взмахиваем рукой
    }

    // Геттеры для настроек
    public boolean isEnabled() {
        return isEnabled;
    }

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
    }

    public int getAttackDelay() {
        return attackDelay;
    }

    public void setAttackDelay(int attackDelay) {
        this.attackDelay = attackDelay;
    }
}
