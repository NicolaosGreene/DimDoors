package StevenDimDoors.mod_pocketDim.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDimWall extends Block
{
	private static final float SUPER_HIGH_HARDNESS = 10000000000000F;
	private static final float SUPER_EXPLOSION_RESISTANCE = 18000000F;
	private Icon[] blockIcon = new Icon[2];
	
	public BlockDimWall(int blockID, int j, Material par2Material) 
	{
		super(blockID, Material.ground);
		this.setCreativeTab(mod_pocketDim.dimDoorsCreativeTab);      
	}
	
	@Override
	public float getBlockHardness(World world, int x, int y, int z)
	{
		if (world.getBlockMetadata(x, y, z) == 0)
		{
			return this.blockHardness;
		}
		else
		{
			return SUPER_HIGH_HARDNESS;
		}
	}
	
	@Override
    public float getExplosionResistance(Entity entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ)
    {
		if (world.getBlockMetadata(x, y, z) == 0)
		{
			return super.getExplosionResistance(entity, world, x, y, z, explosionX, explosionY, explosionZ);
		}
		else
		{
			return SUPER_EXPLOSION_RESISTANCE;
		}
    }
	
	@Override
	public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon[0] = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName());
        this.blockIcon[1] = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName() + "Perm");
    }
	
	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIcon(int par1, int par2)
	{
		return (par2 != 1) ? blockIcon[0] : blockIcon[1];
	}
	
	@Override
	public int damageDropped(int metadata) 
	{
		//Return 0 to avoid dropping Ancient Fabric even if the player somehow manages to break it
		return 0;
	}
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(int unknown, CreativeTabs tab, List subItems) 
	{
		for (int ix = 0; ix < 2; ix++) 
		{
			subItems.add(new ItemStack(this, 1, ix));
		}
	}
    @Override
	public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5) {}
    
    @Override
	protected boolean canSilkHarvest()
    {
        return true;
    }
    
    @Override
	public int quantityDropped(Random par1Random)
    {
        return 0;
    }
   
    /**
     * replaces the block clicked with the held block, instead of placing the block on top of it. Shift click to disable. 
     */
    @Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9)
    {
    	//Check if the metadata value is 0 -- we don't want the user to replace Ancient Fabric
        if (entityPlayer.getCurrentEquippedItem() != null && world.getBlockMetadata(x, y, z) == 0)
        {
        	Item playerEquip = entityPlayer.getCurrentEquippedItem().getItem();
        	
        	if (playerEquip instanceof ItemBlock)
        	{
        		// SenseiKiwi: Using getBlockID() rather than the raw itemID is critical.
        		// Some mods may override that function and use item IDs outside the range
        		// of the block list.
        		
        		int blockID = ((ItemBlock) playerEquip).getBlockID();
        		Block block = Block.blocksList[blockID];
        		if (!Block.isNormalCube(blockID) || block instanceof BlockContainer || blockID == this.blockID)
        		{
        			return false;
        		}
        		if (!world.isRemote)
        		{
            		if (!entityPlayer.capabilities.isCreativeMode)
            		{
            			entityPlayer.getCurrentEquippedItem().stackSize--;
            		}
            		world.setBlock(x, y, z, entityPlayer.getCurrentEquippedItem().itemID, entityPlayer.getCurrentEquippedItem().getItemDamage(), 0);
        		}
        		return true;
        	}
        }
        return false;
    }
}
