package StevenDimDoors.mod_pocketDim;

import StevenDimDoors.mod_pocketDim.blocks.BlockDimWall;
import StevenDimDoors.mod_pocketDim.blocks.BlockDimWallPerm;
import StevenDimDoors.mod_pocketDim.blocks.BlockDoorGold;
import StevenDimDoors.mod_pocketDim.blocks.BlockGoldDimDoor;
import StevenDimDoors.mod_pocketDim.blocks.BlockLimbo;
import StevenDimDoors.mod_pocketDim.blocks.BlockRift;
import StevenDimDoors.mod_pocketDim.blocks.DimensionalDoor;
import StevenDimDoors.mod_pocketDim.blocks.TransTrapdoor;
import StevenDimDoors.mod_pocketDim.blocks.TransientDoor;
import StevenDimDoors.mod_pocketDim.blocks.UnstableDoor;
import StevenDimDoors.mod_pocketDim.blocks.WarpDoor;
import StevenDimDoors.mod_pocketDim.commands.CommandCreateDungeonRift;
import StevenDimDoors.mod_pocketDim.commands.CommandCreatePocket;
import StevenDimDoors.mod_pocketDim.commands.CommandDeleteAllLinks;
import StevenDimDoors.mod_pocketDim.commands.CommandDeleteRifts;
import StevenDimDoors.mod_pocketDim.commands.CommandExportDungeon;
import StevenDimDoors.mod_pocketDim.commands.CommandResetDungeons;
import StevenDimDoors.mod_pocketDim.commands.CommandTeleportPlayer;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.helpers.ChunkLoaderHelper;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;
import StevenDimDoors.mod_pocketDim.items.ItemBlockDimWall;
import StevenDimDoors.mod_pocketDim.items.ItemDimensionalDoor;
import StevenDimDoors.mod_pocketDim.items.ItemGoldDimDoor;
import StevenDimDoors.mod_pocketDim.items.ItemGoldDoor;
import StevenDimDoors.mod_pocketDim.items.ItemRiftBlade;
import StevenDimDoors.mod_pocketDim.items.ItemRiftSignature;
import StevenDimDoors.mod_pocketDim.items.ItemStabilizedRiftSignature;
import StevenDimDoors.mod_pocketDim.items.ItemStableFabric;
import StevenDimDoors.mod_pocketDim.items.ItemUnstableDoor;
import StevenDimDoors.mod_pocketDim.items.ItemWarpDoor;
import StevenDimDoors.mod_pocketDim.items.ItemWorldThread;
import StevenDimDoors.mod_pocketDim.items.itemRiftRemover;
import StevenDimDoors.mod_pocketDim.ticking.CommonTickHandler;
import StevenDimDoors.mod_pocketDim.ticking.LimboDecay;
import StevenDimDoors.mod_pocketDim.ticking.MobMonolith;
import StevenDimDoors.mod_pocketDim.ticking.MonolithSpawner;
import StevenDimDoors.mod_pocketDim.ticking.RiftRegenerator;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityDimDoor;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityDimDoorGold;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityRift;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityTransTrapdoor;
import StevenDimDoors.mod_pocketDim.world.BiomeGenLimbo;
import StevenDimDoors.mod_pocketDim.world.BiomeGenPocket;
import StevenDimDoors.mod_pocketDim.world.GatewayGenerator;
import StevenDimDoors.mod_pocketDim.world.LimboProvider;
import StevenDimDoors.mod_pocketDim.world.PocketProvider;
import StevenDimDoors.mod_pocketDimClient.ClientPacketHandler;
import StevenDimDoors.mod_pocketDimClient.ClientTickHandler;

import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityEggInfo;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = mod_pocketDim.modid, name = "Dimensional Doors", version = mod_pocketDim.version)

@NetworkMod(clientSideRequired = true, serverSideRequired = false, connectionHandler=ConnectionHandler.class,
clientPacketHandlerSpec =
@SidedPacketHandler(channels = {PacketConstants.CHANNEL_NAME}, packetHandler = ClientPacketHandler.class),
serverPacketHandlerSpec =
@SidedPacketHandler(channels = {PacketConstants.CHANNEL_NAME}, packetHandler = ServerPacketHandler.class))
public class mod_pocketDim
{
	public static final String version = "$VERSION$";
	public static final String modid = "dimdoors";

	//need to clean up 
	@SidedProxy(clientSide = "StevenDimDoors.mod_pocketDimClient.ClientProxy", serverSide = "StevenDimDoors.mod_pocketDim.CommonProxy")
	public static CommonProxy proxy;

	@Instance("PocketDimensions")
	public static mod_pocketDim instance = new mod_pocketDim();

	public static Block transientDoor;
	public static Block warpDoor;
	public static Block goldDoor;
	public static Block goldDimDoor;
	public static Block unstableDoor;
	public static Block blockLimbo;
	public static DimensionalDoor dimensionalDoor;    
	public static Block blockDimWall;   
	public static TransTrapdoor transTrapdoor;
	public static Block blockDimWallPerm;
	public static BlockRift blockRift;

	public static Item itemGoldDimDoor;
	public static Item itemGoldDoor;
	public static Item itemWorldThread;

	public static Item itemRiftBlade;
	public static Item itemDimDoor;
	public static Item itemExitDoor;
	public static Item itemRiftRemover;
	public static Item itemLinkSignature;
	public static Item itemStableFabric;
	public static Item itemChaosDoor;
	public static Item itemStabilizedLinkSignature;

	public static BiomeGenBase limboBiome;
	public static BiomeGenBase pocketBiome;

	public static boolean isPlayerWearingGoogles = false;

	public static DDProperties properties;
	public static MonolithSpawner spawner; //Added this field temporarily. Will be refactored out later.
	public static GatewayGenerator riftGen;
	public static PlayerTracker tracker;

	public static CreativeTabs dimDoorsCreativeTab = new CreativeTabs("dimDoorsCreativeTab") 
	{
		@Override
		public ItemStack getIconItemStack() 
		{
			return new ItemStack(mod_pocketDim.itemDimDoor, 1, 0);
		}

		@Override
		public String getTranslatedTabLabel()
		{
			return "Dimensional Doors";
		}
	};



	@EventHandler
	public void onPreInitialization(FMLPreInitializationEvent event)
	{
		instance = this;
		//This should be the FIRST thing that gets done.
		String path = event.getSuggestedConfigurationFile().getAbsolutePath().replace(modid, "DimDoors");

		properties = DDProperties.initialize(new File(path));

		//Now do other stuff
		MinecraftForge.EVENT_BUS.register(new EventHookContainer(properties));

		riftGen = new GatewayGenerator(properties);
	}

	@EventHandler
	public void onInitialization(FMLInitializationEvent event)
	{
		CommonTickHandler commonTickHandler = new CommonTickHandler();
		TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
		TickRegistry.registerTickHandler(commonTickHandler, Side.SERVER);

		//MonolithSpawner should be initialized before any provider instances are created
		//Register the other regular tick receivers as well
		spawner = new MonolithSpawner(commonTickHandler, properties);
		new RiftRegenerator(commonTickHandler); //No need to store the reference
		LimboDecay decay = new LimboDecay(commonTickHandler, properties);

		transientDoor = new TransientDoor(properties.TransientDoorID, Material.iron, properties).setHardness(1.0F) .setUnlocalizedName("transientDoor");
		goldDimDoor = new BlockGoldDimDoor(properties.GoldDimDoorID, Material.iron, properties).setHardness(1.0F) .setUnlocalizedName("dimDoorGold");

		goldDoor = new BlockDoorGold(properties.GoldDoorID, Material.iron, properties).setHardness(0.1F).setUnlocalizedName("doorGold");
		blockDimWall = new BlockDimWall(properties.FabricBlockID, 0, Material.iron).setLightValue(1.0F).setHardness(0.1F).setUnlocalizedName("blockDimWall");
		blockDimWallPerm = (new BlockDimWallPerm(properties.PermaFabricBlockID, 0, Material.iron)).setLightValue(1.0F).setBlockUnbreakable().setResistance(6000000.0F).setUnlocalizedName("blockDimWallPerm");
		warpDoor = new WarpDoor(properties.WarpDoorID, Material.wood, properties).setHardness(1.0F) .setUnlocalizedName("dimDoorWarp");
		blockRift = (BlockRift) (new BlockRift(properties.RiftBlockID, 0, Material.air, properties).setHardness(1.0F) .setUnlocalizedName("rift"));
		blockLimbo = new BlockLimbo(properties.LimboBlockID, 15, Material.iron, properties.LimboDimensionID, decay).setHardness(.2F).setUnlocalizedName("BlockLimbo").setLightValue(.0F);
		unstableDoor = (new UnstableDoor(properties.UnstableDoorID, Material.iron, properties).setHardness(.2F).setUnlocalizedName("chaosDoor").setLightValue(.0F) );
		dimensionalDoor = (DimensionalDoor) (new DimensionalDoor(properties.DimensionalDoorID, Material.iron, properties).setHardness(1.0F).setResistance(2000.0F) .setUnlocalizedName("dimDoor"));
		transTrapdoor = (TransTrapdoor) (new TransTrapdoor(properties.TransTrapdoorID, Material.wood).setHardness(1.0F) .setUnlocalizedName("dimHatch"));

		itemGoldDimDoor = (new ItemGoldDimDoor(properties.GoldDimDoorItemID, Material.iron)).setUnlocalizedName("itemGoldDimDoor");
		itemGoldDoor = (new ItemGoldDoor(properties.GoldDoorID, Material.wood)).setUnlocalizedName("itemGoldDoor");
		itemDimDoor = (new ItemDimensionalDoor(properties.DimensionalDoorItemID, Material.iron)).setUnlocalizedName("itemDimDoor");
		itemExitDoor = (new ItemWarpDoor(properties.WarpDoorItemID, Material.wood)).setUnlocalizedName("itemDimDoorWarp");
		itemLinkSignature = (new ItemRiftSignature(properties.RiftSignatureItemID)).setUnlocalizedName("itemLinkSignature");
		itemRiftRemover = (new itemRiftRemover(properties.RiftRemoverItemID, Material.wood)).setUnlocalizedName("itemRiftRemover");
		itemStableFabric = (new ItemStableFabric(properties.StableFabricItemID, 0)).setUnlocalizedName("itemStableFabric");
		itemChaosDoor = (new ItemUnstableDoor(properties.UnstableDoorItemID, Material.iron)).setUnlocalizedName("itemChaosDoor");
		itemRiftBlade = (new ItemRiftBlade(properties.RiftBladeItemID, EnumToolMaterial.GOLD, properties)).setUnlocalizedName("ItemRiftBlade");
		itemStabilizedLinkSignature = (new ItemStabilizedRiftSignature(properties.StabilizedRiftSignatureItemID)).setUnlocalizedName("itemStabilizedRiftSig");
		itemWorldThread = (new ItemWorldThread(properties.ItemWorldThreadID)).setUnlocalizedName("itemWorldThread");


		mod_pocketDim.limboBiome = (new BiomeGenLimbo(properties.LimboBiomeID));
		mod_pocketDim.pocketBiome = (new BiomeGenPocket(properties.PocketBiomeID));

		GameRegistry.registerWorldGenerator(mod_pocketDim.riftGen);
		tracker = new PlayerTracker();
		GameRegistry.registerPlayerTracker(tracker);

		GameRegistry.registerBlock(goldDoor, "Golden Door");
		GameRegistry.registerBlock(goldDimDoor, "Golden Dimensional Door");
		GameRegistry.registerBlock(unstableDoor, "Unstable Door");
		GameRegistry.registerBlock(warpDoor, "Warp Door");
		GameRegistry.registerBlock(blockRift, "Rift");
		GameRegistry.registerBlock(blockLimbo, "Unraveled Fabric");
		GameRegistry.registerBlock(dimensionalDoor, "Dimensional Door");
		GameRegistry.registerBlock(transTrapdoor,"Transdimensional Trapdoor");
		GameRegistry.registerBlock(blockDimWallPerm, "Fabric of RealityPerm");
		GameRegistry.registerBlock(transientDoor, "transientDoor");

		GameRegistry.registerBlock(blockDimWall, ItemBlockDimWall.class, "Fabric of Reality");

		DimensionManager.registerProviderType(properties.PocketProviderID, PocketProvider.class, false);
		DimensionManager.registerProviderType(properties.LimboProviderID, LimboProvider.class, false);
		DimensionManager.registerDimension(properties.LimboDimensionID, properties.LimboProviderID);

		LanguageRegistry.addName(goldDoor, "Golden Door");
		LanguageRegistry.addName(goldDimDoor, "Golden Dimensional Door");
		LanguageRegistry.addName(transientDoor	, "transientDoor");
		LanguageRegistry.addName(blockRift	, "Rift");
		LanguageRegistry.addName(blockLimbo	, "Unraveled Fabric");
		LanguageRegistry.addName(warpDoor	, "Warp Door");
		LanguageRegistry.addName(unstableDoor	, "Unstable Door");
		LanguageRegistry.addName(blockDimWall	, "Fabric of Reality");
		LanguageRegistry.addName(blockDimWallPerm	, "Eternal Fabric");
		LanguageRegistry.addName(dimensionalDoor, "Dimensional Door");
		LanguageRegistry.addName(transTrapdoor, "Transdimensional Trapdoor");

		LanguageRegistry.addName(itemExitDoor, "Warp Door");
		LanguageRegistry.addName(itemLinkSignature	, "Rift Signature");
		LanguageRegistry.addName(itemGoldDoor, "Golden Door");
		LanguageRegistry.addName(itemGoldDimDoor	, "Golden Dimensional Door");
		LanguageRegistry.addName(itemStabilizedLinkSignature, "Stabilized Rift Signature");
		LanguageRegistry.addName(itemRiftRemover	, "Rift Remover");
		LanguageRegistry.addName(itemStableFabric	, "Stable Fabric");
		LanguageRegistry.addName(itemChaosDoor	, "Unstable Door");
		LanguageRegistry.addName(itemDimDoor, "Dimensional Door");
		LanguageRegistry.addName(itemRiftBlade	, "Rift Blade");
		LanguageRegistry.addName(itemWorldThread, "World Thread");


		/**
		 * Add names for multiblock inventory item
		 */
		LanguageRegistry.addName(new ItemStack(blockDimWall, 1, 0), "Fabric of Reality");
		LanguageRegistry.addName(new ItemStack(blockDimWall, 1, 1), "Ancient Fabric");


		LanguageRegistry.instance().addStringLocalization("itemGroup.dimDoorsCustomTab", "en_US", "Dimensional Doors Items");

		GameRegistry.registerTileEntity(TileEntityDimDoor.class, "TileEntityDimDoor");
		GameRegistry.registerTileEntity(TileEntityRift.class, "TileEntityRift");
		GameRegistry.registerTileEntity(TileEntityTransTrapdoor.class, "TileEntityDimHatch");
		GameRegistry.registerTileEntity(TileEntityDimDoorGold.class, "TileEntityDimDoorGold");

		EntityRegistry.registerModEntity(MobMonolith.class, "Monolith", properties.MonolithEntityID, this, 70, 1, true);
		EntityList.IDtoClassMapping.put(properties.MonolithEntityID, MobMonolith.class);
		EntityList.entityEggs.put(properties.MonolithEntityID, new EntityEggInfo(properties.MonolithEntityID, 0, 0xffffff));
		LanguageRegistry.instance().addStringLocalization("entity.DimDoors.Obelisk.name", "Monolith");


		CraftingManager.registerRecipies();
		DungeonHelper.initialize();

		// Register loot chests
		DDLoot.registerInfo(properties);

		proxy.loadTextures();
		proxy.registerRenderers();
	}


	@EventHandler
	public void onPostInitialization(FMLPostInitializationEvent event)
	{	
		ForgeChunkManager.setForcedChunkLoadingCallback(instance, new ChunkLoaderHelper());
	}
	
	@EventHandler
	public void onServerStopping(FMLServerStoppingEvent event)
	{
		try
		{
			PocketManager.unload();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event)
	{

		//TODO- load dims with forced chunks on server startup here  

		CommandResetDungeons.instance().register(event);
		CommandCreateDungeonRift.instance().register(event);
		CommandDeleteAllLinks.instance().register(event);
		//CommandDeleteDimensionData.instance().register(event);
		CommandDeleteRifts.instance().register(event);
		CommandExportDungeon.instance().register(event);
		//CommandPrintDimensionData.instance().register(event);
		//CommandPruneDimensions.instance().register(event);
		CommandCreatePocket.instance().register(event);
		CommandTeleportPlayer.instance().register(event);

		try
		{
			ChunkLoaderHelper.loadChunkForcedWorlds(event);
		}
		catch (Exception e)
		{
			System.out.println("Loading chunkloaders failed");
		}
	}
	
	public static void sendChat(EntityPlayer player, String message)
	{
		ChatMessageComponent cmp = new ChatMessageComponent();
		cmp.addText(message);
		player.sendChatToPlayer(cmp);
	}
}
