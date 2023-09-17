package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.DropDownButton;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class DropdownValueEntry extends DropdownEntry{
	private final int num;
	private final String value;
	private final Consumer<String> setter;
	private final DropDownButton source;
	private final Component message;
	private ExtendedButton button;

	public DropdownValueEntry(DropDownButton source, int num, String value, Consumer<String> setter){
		this.num = num;
		this.value = value;
		this.setter = setter;
		this.source = source;
		message = Component.empty().append(value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1).toLowerCase(Locale.ROOT));
	}

	@Override
	public List<? extends GuiEventListener> children(){
		return ImmutableList.of(button);
	}

	@Override
	public void render(@NotNull final GuiGraphics guiGraphics, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTicks){
		if(button == null){
			if(list != null)
				button = new ExtendedButton(list.getLeft() + 3, 0, list.getWidth() - 12, pHeight + 1, null, null){
					@Override
					public Component getMessage(){
						return message;
					}

					@Override
					public void onPress(){
						source.current = value;
						source.onPress();
						setter.accept(value);
					}
				};
		}else{
			button.setY(pTop);
			button.visible = source.visible;
			button.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
		}
	}

	@Override
	public List<? extends NarratableEntry> narratables(){
		return Collections.emptyList();
	}
}