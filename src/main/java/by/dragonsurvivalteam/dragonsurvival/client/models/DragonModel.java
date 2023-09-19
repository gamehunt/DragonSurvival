package by.dragonsurvivalteam.dragonsurvival.client.models;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.DragonEditorHandler;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.SkinPreset.SkinAgeGroup;
import by.dragonsurvivalteam.dragonsurvival.client.util.FakeClientPlayer;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.config.ClientConfig;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import software.bernie.geckolib.core.molang.MolangParser;
import software.bernie.geckolib.model.GeoModel;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public class DragonModel extends GeoModel<DragonEntity> {
	private final ResourceLocation defaultTexture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/dragon/cave_newborn.png");
	private final ResourceLocation model = new ResourceLocation(DragonSurvivalMod.MODID, "geo/dragon_model.geo.json");
	private final ResourceLocation animation = new ResourceLocation(DragonSurvivalMod.MODID, "animations/dragon.animations.json");

	private ResourceLocation currentTexture = defaultTexture;

	private final ConcurrentHashMap<String, ResourceLocation> cache = new ConcurrentHashMap<>();

	@Override
	public void applyMolangQueries(final DragonEntity dragon, double currentTick) {
		super.applyMolangQueries(dragon, currentTick);

		// In case the Integer (id of the player) is null
		if (dragon.playerId == null || dragon.getPlayer() == null) {
			return;
		}

		MolangParser parser = MolangParser.INSTANCE;

		Player player = dragon.getPlayer();
		Vec3 deltaMovement = dragon.getPseudoDeltaMovement();
		DragonStateHandler handler = DragonUtils.getHandler(player);

		parser.setValue("query.delta_y", () -> deltaMovement.y);
		parser.setValue("query.head_yaw", () -> handler.getMovementData().headYaw);
		parser.setValue("query.head_pitch", () -> handler.getMovementData().headPitch);

		double bodyYawChange = Functions.angleDifference((float)handler.getMovementData().bodyYawLastTick, (float)handler.getMovementData().bodyYaw);
		double headYawChange = Functions.angleDifference((float)handler.getMovementData().headYawLastTick, (float)handler.getMovementData().headYaw);
		double headPitchChange = Functions.angleDifference((float)handler.getMovementData().headPitchLastTick, (float)handler.getMovementData().headPitch);

		double gravity = player.getAttribute(ForgeMod.ENTITY_GRAVITY.get()).getValue();

		dragon.tailMotionUp = Mth.clamp(Mth.lerp(0.25, dragon.tailMotionUp, ServerFlightHandler.isFlying(player) ? 0 : (deltaMovement.y + gravity) * 50), -10, 10);
		dragon.tailMotionSide = Mth.lerp(0.1, Mth.clamp(dragon.tailMotionSide + (ServerFlightHandler.isGliding(player) ? 0 : bodyYawChange), -50, 50), 0);

		dragon.bodyYawAverage.add(bodyYawChange);
		while (dragon.bodyYawAverage.size() > 10) {
			dragon.bodyYawAverage.remove(0);
		}

		dragon.headYawAverage.add(headYawChange);
		while (dragon.headYawAverage.size() > 10) {
			dragon.headYawAverage.remove(0);
		}

		dragon.headPitchAverage.add(headPitchChange);
		while (dragon.headPitchAverage.size() > 10) {
			dragon.headPitchAverage.remove(0);
		}

		dragon.tailSideAverage.add(dragon.tailMotionSide);
		while (dragon.tailSideAverage.size() > 10) {
			dragon.tailSideAverage.remove(0);
		}

		dragon.tailUpAverage.add(dragon.tailMotionUp * -1);
		while (dragon.tailUpAverage.size() > 10) {
			dragon.tailUpAverage.remove(0);
		}

		double bodyYawAvg = dragon.bodyYawAverage.stream().mapToDouble(a -> a).sum() / dragon.bodyYawAverage.size();
		double headYawAvg = dragon.headYawAverage.stream().mapToDouble(a -> a).sum() / dragon.headYawAverage.size();
		double headPitchAvg = dragon.headPitchAverage.stream().mapToDouble(a -> a).sum() / dragon.headPitchAverage.size();
		double tailSideAvg = dragon.tailSideAverage.stream().mapToDouble(a -> a).sum() / dragon.tailSideAverage.size();
		double tailUpAvg = dragon.tailUpAverage.stream().mapToDouble(a -> a).sum() / dragon.tailUpAverage.size();

		double query_body_yaw_change = Mth.lerp(0.1, dragon.body_yaw_change, bodyYawAvg);
		double query_head_yaw_change = Mth.lerp(0.1, dragon.head_yaw_change, headYawAvg);
		double query_head_pitch_change = Mth.lerp(0.1, dragon.head_pitch_change, headPitchAvg);
		double query_tail_motion_up = Mth.lerp(0.1, dragon.tail_motion_up, tailUpAvg);
		double query_tail_motion_side = Mth.lerp(0.1, dragon.tail_motion_side, tailSideAvg);

		if (dragon.tailLocked || !ClientConfig.enableTailPhysics) {
			dragon.tailMotionUp = 0;
			dragon.tailMotionSide = 0;

			dragon.tail_motion_up = 0;
			dragon.tail_motion_side = 0;

			query_tail_motion_up = 0;
			query_tail_motion_side = 0;
		}

		parser.setValue("query.body_yaw_change", () -> query_body_yaw_change);
		parser.setValue("query.head_yaw_change", () -> query_head_yaw_change);
		parser.setValue("query.head_pitch_change", () -> query_head_pitch_change);

		double finalQuery_tail_motion_up = query_tail_motion_up;
		parser.setValue("query.tail_motion_up", () -> finalQuery_tail_motion_up);

		double finalQuery_tail_motion_side = query_tail_motion_side;
		parser.setValue("query.tail_motion_side", () -> finalQuery_tail_motion_side);

		dragon.body_yaw_change = query_body_yaw_change;
		dragon.head_yaw_change = query_head_yaw_change;
		dragon.head_pitch_change = query_head_pitch_change;
		dragon.tail_motion_up = query_tail_motion_up;
		dragon.tail_motion_side = query_tail_motion_side;
	}

	public void setCurrentTexture(final ResourceLocation currentTexture) {
		this.currentTexture = currentTexture;
	}

	@Override
	public ResourceLocation getModelResource(final DragonEntity dragon) {
		return model;
	}

	@Override
	public ResourceLocation getTextureResource(final DragonEntity dragon) {
		String id = null;

		if (dragon.playerId != null || dragon.getPlayer() != null) {
			DragonStateHandler handler = DragonUtils.getHandler(dragon.getPlayer());
			SkinAgeGroup ageGroup = handler.getSkinData().skinPreset.skinAges.get(handler.getLevel()).get();

			if (handler.getSkinData().recompileSkin) {
				DragonEditorHandler.generateSkinTextures(dragon);
			}

			if (handler.getSkinData().blankSkin) {
				id = "textures/dragon/blank_skin_" + handler.getTypeName().toLowerCase(Locale.ROOT) + ".png";
			} else if (ageGroup.defaultSkin) {
				if (currentTexture != null) {
					id = currentTexture.getPath();
				} else {
					id = "textures/dragon/" + handler.getTypeName().toLowerCase(Locale.ROOT) + "_" + handler.getLevel().name.toLowerCase(Locale.ROOT) + ".png";
				}
			} else if (handler.getSkinData().isCompiled && currentTexture == null) {
				id = "dynamic_normal_" + dragon.getPlayer().getStringUUID() + "_" + handler.getLevel().name;
			}
		}

		if (id == null && dragon.getPlayer() instanceof FakeClientPlayer) {
			LocalPlayer localPlayer = Minecraft.getInstance().player;

			if (localPlayer != null) {
				id = "dynamic_normal_" + localPlayer.getStringUUID() + "_" + DragonUtils.getHandler(dragon.getPlayer()).getLevel().name;
			}
		}

		if (id != null) {
			// Dragon editor skins
			return cache.computeIfAbsent(id, key -> new ResourceLocation(DragonSurvivalMod.MODID, key));
		}

		// Layers (e.g. armor) or player skins
        return currentTexture == null ? defaultTexture : currentTexture;
    }

	@Override
	public ResourceLocation getAnimationResource(final DragonEntity dragon) {
		return animation;
	}
}