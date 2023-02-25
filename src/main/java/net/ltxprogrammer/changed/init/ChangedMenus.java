package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.world.inventory.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.IContainerFactory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChangedMenus {
    private static final List<MenuType<?>> REGISTRY = new ArrayList<>();
    public static final MenuType<AbilityRadialMenu> ABILITY_RADIAL = register("ability_radial", AbilityRadialMenu::new);
    public static final MenuType<HairStyleRadialMenu> HAIRSTYLE_RADIAL = register("hairstyle_radial", HairStyleRadialMenu::new);
    public static final MenuType<SpecialStateRadialMenu> SPECIAL_RADIAL = register("special_radial", SpecialStateRadialMenu::new);
    public static final MenuType<ComputerMenu> COMPUTER = register("computer", ComputerMenu::new);
    public static final MenuType<ExtraHandsMenu> EXTRA_HANDS = register("extra_hands", ExtraHandsMenu::new);
    public static final MenuType<CentaurSaddleMenu> CENTAUR_SADDLE = register("centaur_saddle", CentaurSaddleMenu::new);
    public static final MenuType<InfuserMenu> INFUSER = register("infuser", InfuserMenu::new);
    public static final MenuType<PurifierMenu> PURIFIER = register("purifier", PurifierMenu::new);
    public static final MenuType<KeypadMenu> KEYPAD = register("keypad", KeypadMenu::new);

    private static <T extends AbstractContainerMenu> MenuType<T> register(String name, IContainerFactory<T> containerFactory) {
        MenuType<T> menuType = new MenuType<T>(containerFactory);
        menuType.setRegistryName(name);
        REGISTRY.add(menuType);
        return menuType;
    }

    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<MenuType<?>> event) {
        event.getRegistry().registerAll(REGISTRY.toArray(new MenuType[0]));
    }
}
