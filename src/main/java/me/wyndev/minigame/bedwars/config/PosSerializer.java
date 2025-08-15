package me.wyndev.minigame.bedwars.config;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class PosSerializer implements TypeSerializer<Pos> {

    @Override
    public Pos deserialize(@NotNull Type type, @NotNull ConfigurationNode node) throws SerializationException {
        String pos = node.getString();
        if (pos == null) throw new SerializationException("Cannot deserialize a null pos!");
        return deserialize(pos);
    }


    @Override
    public void serialize(@NotNull Type type, @Nullable Pos obj, @NotNull ConfigurationNode node) throws SerializationException {
        if (obj == null) throw new SerializationException("Cannot serialize a null pos!");
        node.set(serializeAsTag(obj));
    }

    /**
     * Serializes a {@link Pos} object into a human-readable string format.
     *
     * @param pos The position to be serialized.
     * @return A string representation of the position in the format: "Pos{x=%.2f, y=%.2f, z=%.2f, yaw=%.2f, pitch=%.2f}"
     */
    private String serialize(Pos pos) {
        return String.format("Pos{x=%.2f, y=%.2f, z=%.2f, yaw=%.2f, pitch=%.2f}", pos.x(), pos.y(), pos.z(), pos.yaw(), pos.pitch());
    }

    /**
     * Deserializes a human-readable string format into a {@link Pos} object.
     *
     * @param serializedPos The string representation of the position to be deserialized.
     *                      The string should be in the format: "Pos{x=%.2f, y=%.2f, z=%.2f, yaw=%.2f, pitch=%.2f}"
     * @return A {@link Pos} object representing the deserialized position.
     * If the input string is empty, a new {@link Pos} object with all coordinates set to 0 is returned.
     */
    private Pos deserialize(String serializedPos) {
        if (!serializedPos.isEmpty()) {
            serializedPos = serializedPos.replace("}", "");
            String[] parts = serializedPos.split("[=,\\s]+");
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[3]);
            double z = Double.parseDouble(parts[5]);
            float yaw = Float.parseFloat(parts[7]);
            float pitch = Float.parseFloat(parts[9]);
            return new Pos(x, y, z, yaw, pitch);
        } else {
            return new Pos(0, 0, 0, 180, 0);
        }
    }

    private CompoundBinaryTag serializeAsTag(Pos pos) {
        return CompoundBinaryTag.builder()
                .putDouble("x", pos.x())
                .putDouble("y", pos.y())
                .putDouble("z", pos.z())
                .putFloat("yaw", pos.yaw())
                .putFloat("pitch", pos.pitch())
                .build();
    }

    private Pos deserializeFromTag(CompoundBinaryTag tag) {
        return new Pos(
                tag.getDouble("x"),
                tag.getDouble("y"),
                tag.getDouble("z"),
                tag.getFloat("yaw"),
                tag.getFloat("pitch")
        );
    }
}
