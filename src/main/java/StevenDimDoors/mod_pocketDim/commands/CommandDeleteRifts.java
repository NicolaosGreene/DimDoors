package StevenDimDoors.mod_pocketDim.commands;

import java.util.ArrayList;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;

@SuppressWarnings("deprecation")
public class CommandDeleteRifts extends DDCommandBase
{
	private static CommandDeleteRifts instance = null;

	private CommandDeleteRifts()
	{
		super("dd-???", "???");
	}

	public static CommandDeleteRifts instance()
	{
		if (instance == null)
			instance = new CommandDeleteRifts();

		return instance;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "Usage: /dd-??? <dimension ID>";
	}

	@Override
	protected DDCommandResult processCommand(EntityPlayer sender, String[] command)
	{
		int linksRemoved=0;
		int targetDim;
		boolean shouldGo= true;

		if(command.length==1)
		{
			targetDim = parseInt(sender, command[0]);
		}
		else
		{
			targetDim=0;
			shouldGo=false;
			sendChat(sender,("Error-Invalid argument, delete_all_links <targetDimID>"));
		}

		if(shouldGo)
		{
			
				NewDimData dim = PocketManager.getDimensionData(targetDim);
				ArrayList<DimLink> linksInDim = dim.getAllLinks();

				for (DimLink link : linksInDim)
				{
					World targetWorld = PocketManager.loadDimension(targetDim);
					
					if(!mod_pocketDim.blockRift.isBlockImmune(sender.worldObj,link.source().getX(), link.source().getY(), link.source().getZ())||
							(targetWorld.getBlockId(link.source().getX(), link.source().getY(), link.source().getZ())==mod_pocketDim.blockRift.blockID))
					{
						linksRemoved++;
						targetWorld.setBlock(link.source().getX(), link.source().getY(), link.source().getZ(), 0);
						dim.deleteLink(link);
					
						
					}
					//TODO Probably should check what the block is, but thats annoying so Ill do it later.

				
				}
				sendChat(sender,("Removed " + linksRemoved + " links."));
			
		}
		return DDCommandResult.SUCCESS; //TEMPORARY HACK
	}
}