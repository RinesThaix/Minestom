package net.minestom.server.item.meta;

import net.minestom.server.MinecraftServer;
import net.minestom.server.chat.ChatColor;
import net.minestom.server.color.Color;
import net.minestom.server.item.ItemMeta;
import net.minestom.server.item.ItemMetaBuilder;
import net.minestom.server.utils.clone.PublicCloneable;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTList;
import org.jglrxavpok.hephaistos.nbt.NBTTypes;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

public class MapMeta extends ItemMeta {

    private final int mapId;
    private final int mapScaleDirection;
    private final List<MapDecoration> decorations;
    private final Color mapColor;

    protected MapMeta(ItemMetaBuilder metaBuilder,
                      int mapId,
                      int mapScaleDirection,
                      @NotNull List<MapDecoration> decorations,
                      @NotNull Color mapColor) {
        super(metaBuilder);
        this.mapId = mapId;
        this.mapScaleDirection = mapScaleDirection;
        this.decorations = decorations;
        this.mapColor = mapColor;
    }

    /**
     * Gets the map id.
     *
     * @return the map id
     */
    public int getMapId() {
        return mapId;
    }

    /**
     * Gets the map scale direction.
     *
     * @return the map scale direction
     */
    public int getMapScaleDirection() {
        return mapScaleDirection;
    }

    /**
     * Gets the map decorations.
     *
     * @return a modifiable list containing all the map decorations
     */
    public List<MapDecoration> getDecorations() {
        return decorations;
    }

    /**
     * Gets the map color.
     *
     * @return the map color
     * @deprecated Use {@link #getMapColor()}
     */
    @Deprecated
    public ChatColor getLegacyMapColor() {
        return this.mapColor.asLegacyChatColor();
    }

    /**
     * Gets the map color.
     *
     * @return the map color
     */
    public @NotNull Color getMapColor() {
        return this.mapColor;
    }

    public static class Builder extends ItemMetaBuilder {

        private int mapId;
        private int mapScaleDirection = 1;
        private List<MapDecoration> decorations = new CopyOnWriteArrayList<>();
        private Color mapColor = new Color(0, 0, 0);

        public Builder mapId(int value) {
            this.mapId = value;
            return this;
        }

        public Builder mapScaleDirection(int value) {
            this.mapScaleDirection = value;
            return this;
        }

        public Builder decorations(List<MapDecoration> value) {
            this.decorations = value;
            return this;
        }

        public Builder mapColor(Color value) {
            this.mapColor = value;
            return this;
        }

        @Override
        public @NotNull ItemMeta build() {
            return new MapMeta(this, mapId, mapScaleDirection, decorations, mapColor);
        }

        @Override
        public void read(@NotNull NBTCompound compound) {
            if (compound.containsKey("map")) {
                this.mapId = compound.getAsInt("map");
            }

            if (compound.containsKey("map_scale_direction")) {
                this.mapScaleDirection = compound.getAsInt("map_scale_direction");
            }

            if (compound.containsKey("Decorations")) {
                final NBTList<NBTCompound> decorationsList = compound.getList("Decorations");
                for (NBTCompound decorationCompound : decorationsList) {
                    final String id = decorationCompound.getString("id");
                    final byte type = decorationCompound.getAsByte("type");
                    byte x = 0;

                    if (decorationCompound.containsKey("x")) {
                        x = decorationCompound.getAsByte("x");
                    }

                    byte z = 0;
                    if (decorationCompound.containsKey("z")) {
                        z = decorationCompound.getAsByte("z");
                    }

                    double rotation = 0.0;
                    if (decorationCompound.containsKey("rot")) {
                        rotation = decorationCompound.getAsDouble("rot");
                    }

                    this.decorations.add(new MapDecoration(id, type, x, z, rotation));
                }
            }

            if (compound.containsKey("display")) {
                final NBTCompound displayCompound = compound.getCompound("display");
                if (displayCompound.containsKey("MapColor")) {
                    this.mapColor = new Color(displayCompound.getAsInt("MapColor"));
                }
            }
        }

        @Override
        public void write(@NotNull NBTCompound compound) {
            compound.setInt("map", mapId);

            compound.setInt("map_scale_direction", mapScaleDirection);

            if (!decorations.isEmpty()) {
                NBTList<NBTCompound> decorationsList = new NBTList<>(NBTTypes.TAG_Compound);
                for (MapDecoration decoration : decorations) {
                    NBTCompound decorationCompound = new NBTCompound();
                    decorationCompound.setString("id", decoration.getId());
                    decorationCompound.setByte("type", decoration.getType());
                    decorationCompound.setByte("x", decoration.getX());
                    decorationCompound.setByte("z", decoration.getZ());
                    decorationCompound.setDouble("rot", decoration.getRotation());

                    decorationsList.add(decorationCompound);
                }
                compound.set("Decorations", decorationsList);
            }

            {
                NBTCompound displayCompound;
                if (compound.containsKey("display")) {
                    displayCompound = compound.getCompound("display");
                } else {
                    displayCompound = new NBTCompound();
                }
                displayCompound.setInt("MapColor", mapColor.asRGB());
            }
        }

        @Override
        protected void deepClone(@NotNull ItemMetaBuilder metaBuilder) {
            var mapBuilder = (MapMeta.Builder) metaBuilder;
            mapBuilder.mapId = mapId;
            mapBuilder.mapScaleDirection = mapScaleDirection;
            mapBuilder.decorations = decorations;
            mapBuilder.mapColor = mapColor;
        }

        @Override
        protected @NotNull Supplier<@NotNull ItemMetaBuilder> getSupplier() {
            return Builder::new;
        }
    }

    public static class MapDecoration implements PublicCloneable<MapDecoration> {
        private final String id;
        private final byte type;
        private final byte x, z;
        private final double rotation;

        public MapDecoration(@NotNull String id, byte type, byte x, byte z, double rotation) {
            this.id = id;
            this.type = type;
            this.x = x;
            this.z = z;
            this.rotation = rotation;
        }

        /**
         * Gets the arbitrary decoration id.
         *
         * @return the decoration id
         */
        public String getId() {
            return id;
        }

        /**
         * Gets the decoration type.
         *
         * @return the decoration type
         * @see <a href="https://minecraft.gamepedia.com/Map#Map_icons">Map icons</a>
         */
        public byte getType() {
            return type;
        }

        /**
         * Gets the X position of the decoration.
         *
         * @return the X position
         */
        public byte getX() {
            return x;
        }

        /**
         * Gets the Z position of the decoration.
         *
         * @return the Z position
         */
        public byte getZ() {
            return z;
        }

        /**
         * Gets the rotation of the symbol (0;360).
         *
         * @return the rotation of the symbol
         */
        public double getRotation() {
            return rotation;
        }

        @NotNull
        @Override
        public MapDecoration clone() {
            try {
                return (MapDecoration) super.clone();
            } catch (CloneNotSupportedException e) {
                MinecraftServer.getExceptionManager().handleException(e);
                throw new IllegalStateException("Something weird happened");
            }
        }
    }

}