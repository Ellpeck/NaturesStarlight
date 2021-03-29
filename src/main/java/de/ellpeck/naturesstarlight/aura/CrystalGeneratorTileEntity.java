package de.ellpeck.naturesstarlight.aura;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityImpl;
import de.ellpeck.naturesstarlight.Registry;

// the tile entity is just here so that we can easily find them in a range
public class CrystalGeneratorTileEntity extends TileEntityImpl {

    public CrystalGeneratorTileEntity() {
        super(Registry.CRYSTAL_GENERATOR_TILE.get());
    }

    @Override
    public boolean wantsLimitRemover() {
        return true;
    }

}
