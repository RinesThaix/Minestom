package net.minestom.server.world;

import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTList;
import org.jglrxavpok.hephaistos.nbt.NBTTypes;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Allows servers to register custom dimensions. Also used during player login to send the list of all existing dimensions.
 * <p>
 * Contains {@link DimensionType#OVERWORLD} by default but can be removed.
 */
public final class DimensionTypeManager {

    private final List<DimensionType> dimensionTypes = new LinkedList<>();

    public DimensionTypeManager() {
        addDimension(DimensionType.OVERWORLD);
    }

    /**
     * Adds a new dimension type. This does NOT send the new list to players.
     *
     * @param dimensionType the dimension to add
     */
    public void addDimension(@NotNull DimensionType dimensionType) {
        dimensionType.registered = true;
        this.dimensionTypes.add(dimensionType);
    }

    /**
     * Removes a dimension type. This does NOT send the new list to players.
     *
     * @param dimensionType the dimension to remove
     * @return if the dimension type was removed, false if it was not present before
     */
    public boolean removeDimension(@NotNull DimensionType dimensionType) {
        dimensionType.registered = false;
        return dimensionTypes.remove(dimensionType);
    }

    /**
     * Returns an immutable copy of the dimension types already registered.
     *
     * @return an unmodifiable {@link List} containing all the added dimensions
     */
    @NotNull
    public List<DimensionType> unmodifiableList() {
        return Collections.unmodifiableList(dimensionTypes);
    }

    /**
     * Creates the {@link NBTCompound} containing all the registered dimensions.
     * <p>
     * Used when a player connects.
     *
     * @return an nbt compound containing the registered dimensions
     */
    @NotNull
    public NBTCompound toNBT() {
        NBTCompound dimensions = new NBTCompound();
        dimensions.setString("type", "minecraft:dimension_type");
        NBTList<NBTCompound> dimensionList = new NBTList<>(NBTTypes.TAG_Compound);
        for (DimensionType dimensionType : dimensionTypes) {
            dimensionList.add(dimensionType.toIndexedNBT());
        }
        dimensions.set("value", dimensionList);
        return dimensions;
    }
}
