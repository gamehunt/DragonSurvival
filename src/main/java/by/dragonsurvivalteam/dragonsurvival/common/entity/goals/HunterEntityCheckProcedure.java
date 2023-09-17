package by.dragonsurvivalteam.dragonsurvival.common.entity.goals;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.registries.ForgeRegistries;

public class HunterEntityCheckProcedure {
    public static boolean execute(Entity entity) {
        if (entity == null)
            return false;
        if (entity.getType().is(TagKey.create(ForgeRegistries.Keys.ENTITY_TYPES, new ResourceLocation("dragonsurvival:hunters_goal")))) {
            return false;
        }
        return true;
    }
}
