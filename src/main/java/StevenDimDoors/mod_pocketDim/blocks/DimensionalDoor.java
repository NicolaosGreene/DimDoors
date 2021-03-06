package StevenDimDoors.mod_pocketDim.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.LinkTypes;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;

@SuppressWarnings("deprecation")
public class DimensionalDoor extends BaseDimDoor
{

	public DimensionalDoor(int blockID, Material material, DDProperties properties) 
	{
		super(blockID, material, properties);
	}

	@Override
	public void placeLink(World world, int x, int y, int z) 
	{
		if (!world.isRemote && world.getBlockId(x, y - 1, z) == this.blockID)
		{
			NewDimData dimension = PocketManager.getDimensionData(world);
			DimLink link = dimension.getLink(x, y, z);
			if (link == null)
			{
				dimension.createLink(x, y, z, LinkTypes.POCKET,world.getBlockMetadata(x, y - 1, z));
			}
		}
		
	}
	@Override
	public int getDrops()
	{
		return Item.doorIron.itemID;
	}
}