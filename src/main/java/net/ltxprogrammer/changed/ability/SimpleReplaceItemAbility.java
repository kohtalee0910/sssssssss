package net.ltxprogrammer.changed.ability;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class SimpleReplaceItemAbility extends SimpleAbility {
    public SimpleReplaceItemAbility(Supplier<ItemStack> itemSupplier, Item itemReplace, float exhaustion, float minimumHunger) {
        this.itemSupplier = itemSupplier;
        this.itemReplace = itemReplace;
        this.exhaustion = exhaustion;
        this.minimumHunger = minimumHunger;
    }

    private final Supplier<ItemStack> itemSupplier;
    private final Item itemReplace;
    private final float exhaustion;
    private final float minimumHunger;

    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        return entity.getFoodLevel() > minimumHunger || entity.isCreative();
    }

    @Override
    public boolean canKeepUsing(IAbstractChangedEntity entity) { return false; }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        if (entity.getEntity().getItemInHand(entity.getEntity().getUsedItemHand()).getItem() == itemReplace) {
            ItemStack stack = entity.getEntity().getItemInHand(entity.getEntity().swingingArm);
            stack.shrink(1);
            var item = itemSupplier.get();
            if (!entity.addItem(item))
                entity.drop(item, false);
            if (!entity.isCreative())
                entity.causeFoodExhaustion(exhaustion);
        }
    }

    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.CHARGE_TIME;
    }

    @Override
    public int getChargeTime(IAbstractChangedEntity entity) {
        return 20;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return 20;
    }
}
