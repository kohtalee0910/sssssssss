package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.LockToPlayerMover;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class GrabEntityPacket implements ChangedPacket {
    public enum GrabType {
        /**
         * Target will be release by the latex
         */
        RELEASE,
        /**
         * Target is grabbed by latex by their arms
         */
        ARMS,
        /**
         * Target is fully encased by latex entity
         */
        SUIT
    }

    public final UUID sourceEntity;
    public final UUID targetEntity;
    public final GrabType type;

    public GrabEntityPacket(LivingEntity source, LivingEntity target, GrabType type) {
        this.sourceEntity = source.getUUID();
        this.targetEntity = target.getUUID();
        this.type = type;
    }

    public GrabEntityPacket(FriendlyByteBuf buffer) {
        this.sourceEntity = buffer.readUUID();
        this.targetEntity = buffer.readUUID();
        this.type = GrabType.values()[buffer.readInt()];
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(sourceEntity);
        buffer.writeUUID(targetEntity);
        buffer.writeInt(type.ordinal());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        var sender = context.getSender();
        if (context.getDirection().getReceptionSide().isServer() && sender != null) {
            var level = sender.getLevel();
            var target = level.getEntity(targetEntity);
            if (!(target instanceof LivingEntity livingTarget))
                return;
            if (!target.getType().is(ChangedTags.EntityTypes.HUMANOIDS) && !(target instanceof Player))
                return;
            context.setPacketHandled(true);
            if (sender.getUUID().equals(sourceEntity)) {
                if (ProcessTransfur.isPlayerOrganic(sender))
                    return; // Invalid, sender has to be latex
            } else {
                return; // Invalid, sender cannot dictate other entities grab action
            }

            ProcessTransfur.ifPlayerLatex(sender, variant -> {
                var ability = variant.getAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get());
                if (ability == null)
                    return;

                switch (type) {
                    case RELEASE -> {
                        ability.grabbedEntity = null;
                        ChangedSounds.broadcastSound(sender, ChangedSounds.BLOW1, 1.0f, 1.0f);
                        if (target instanceof Player targetPlayer)
                            LockToPlayerMover.releaseHuman(sender, targetPlayer, type);
                    }
                    case SUIT -> {
                        ability.grabbedEntity = livingTarget;
                        ChangedSounds.broadcastSound(sender, ChangedSounds.POISON, 1.0f, 1.0f);
                        if (target instanceof Player targetPlayer)
                            LockToPlayerMover.setupLatexHoldHuman(sender, targetPlayer, type);
                    }
                    case ARMS -> {
                        ability.grabbedEntity = livingTarget;
                        ChangedSounds.broadcastSound(sender, ChangedSounds.BLOW1, 1.0f, 1.0f);
                        if (target instanceof Player targetPlayer)
                            LockToPlayerMover.setupLatexHoldHuman(sender, targetPlayer, type);
                    }
                }
            });
        }

        else {

        }
    }

    public static GrabEntityPacket release(Player latexPlayer, LivingEntity entity) {
        return new GrabEntityPacket(latexPlayer, entity, GrabType.RELEASE);
    }

    public static GrabEntityPacket initialGrab(Player latexPlayer, LivingEntity entity) {
        return new GrabEntityPacket(latexPlayer, entity, GrabType.ARMS);
    }
}
