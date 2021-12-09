package by.jackraidenph.dragonsurvival.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {

	// Movement
	public final ForgeConfigSpec.ConfigValue<DragonBodyMovementType> firstPersonBodyMovement;
	public final ForgeConfigSpec.ConfigValue<DragonBodyMovementType> thirdPersonBodyMovement;

	public final ForgeConfigSpec.BooleanValue dragonNameTags;
	public final ForgeConfigSpec.BooleanValue renderInFirstPerson;
	public final ForgeConfigSpec.BooleanValue notifyWingStatus;
	public final ForgeConfigSpec.BooleanValue clientDebugMessages;
	
	public final ForgeConfigSpec.BooleanValue dragonInventory;
	public final ForgeConfigSpec.BooleanValue dragonTabs;
	public final ForgeConfigSpec.BooleanValue inventoryToggle;
	
	public final ForgeConfigSpec.IntValue castbarXOffset;
	public final ForgeConfigSpec.IntValue castbarYOffset;
	
	public final ForgeConfigSpec.IntValue skillbarXOffset;
	public final ForgeConfigSpec.IntValue skillbarYOffset;
	
	public final ForgeConfigSpec.IntValue emoteXOffset;
	public final ForgeConfigSpec.IntValue emoteYOffset;
	
	ClientConfig(ForgeConfigSpec.Builder builder) {
		builder.push("client");
		//For people who use first person view mods
		renderInFirstPerson = builder.comment("Render dragon model in first person. If your own tail scares you, write false")
				.define("renderFirstPerson", true);
		notifyWingStatus = builder.comment("Notifies of wing status in chat message").define("notifyWingStatus", true);
		clientDebugMessages = builder.define("Enable client-side debug messages", false);
		
		dragonInventory = builder
				.comment("Should the default inventory be replaced as a dragon?")
				.define("dragonInventory", true);
		
		dragonTabs = builder
				.comment("Should dragon tabs be added to the default player inventory?")
				.define("dragonTabs", true);
		
		inventoryToggle = builder
				.comment("Should the buttons for toggeling between dragon and normaly inventory be added?")
				.define("inventoryToggle", true);
		
		builder.push("ui");
		castbarXOffset = builder
				.comment("Offset the x position of the cast bar in relation to its normal position")
				.defineInRange("casterBarXPos", 0, -1000, 1000);
		
		castbarYOffset = builder
				.comment("Offset the y position of the cast bar in relation to its normal position")
				.defineInRange("casterBarYPos", 0, -1000, 1000);
		
		skillbarXOffset = builder
				.comment("Offset the x position of the magic skill bar in relation to its normal position")
				.defineInRange("skillbarXOffset", 0, -1000, 1000);
		
		skillbarYOffset = builder
				.comment("Offset the y position of the magic skill bar in relation to its normal position")
				.defineInRange("skillbarYOffset", 0, -1000, 1000);
		
		emoteXOffset = builder
				.comment("Offset the x position of the emote button in relation to its normal position")
				.defineInRange("emoteXOffset", 0, -1000, 1000);
		
		emoteYOffset = builder
				.comment("Offset the y position of the emote button in relation to its normal position")
				.defineInRange("emoteYOffset", 0, -1000, 1000);
		
		// Movement
		builder.push("movement");
		firstPersonBodyMovement = builder
				.comment("The type of body movement you use while in first person as a dragon.")
				.defineEnum("firstPersonMovement", DragonBodyMovementType.VANILLA, DragonBodyMovementType.values());
		thirdPersonBodyMovement = builder
				.comment("The type of body movement you use while in third person as a dragon.")
				.defineEnum("thirdPersonMovement", DragonBodyMovementType.DRAGON, DragonBodyMovementType.values());
		builder.pop().push("nametag");
		dragonNameTags = builder
				.comment("Show name tags for dragons.")
				.define("dragonNameTags", false);
		builder.pop();
	}
	
}