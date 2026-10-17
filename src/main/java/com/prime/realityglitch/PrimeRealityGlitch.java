package com.prime.realityglitch;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(modid = PrimeRealityGlitch.MODID, name = PrimeRealityGlitch.NAME, version = PrimeRealityGlitch.VERSION,
     acceptedMinecraftVersions = "[1.12.2]")
public class PrimeRealityGlitch {
    public static final String MODID = "prime_reality_glitch";
    public static final String NAME = "Prime: Reality Glitch";
    public static final String VERSION = "1.0.0";
    private int tickCounter;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntityLiving().world.isRemote) return;

        // Villagers retaliate when attacked by a player.
        if (event.getEntityLiving() instanceof EntityVillager
                && event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityVillager villager = (EntityVillager) event.getEntityLiving();
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
            villager.setAttackTarget(player);
            player.attackEntityFrom(DamageSource.causeMobDamage(villager), 2.0F);
            player.sendMessage(new net.minecraft.util.text.TextComponentString("§cREALITY GLITCH: The villager fights back!"));
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase living = event.getEntityLiving();
        if (living.world.isRemote || living.ticksExisted % 10 != 0) return;

        // Hostile mobs are pushed away from nearby players.
        if (living instanceof EntityMob) {
            EntityPlayer player = living.world.getClosestPlayerToEntity(living, 8.0D);
            if (player != null && living.getDistanceSq(player) < 64.0D) {
                double dx = living.posX - player.posX;
                double dz = living.posZ - player.posZ;
                double len = Math.sqrt(dx * dx + dz * dz);
                if (len > 0.001D) {
                    living.motionX += dx / len * 0.08D;
                    living.motionZ += dz / len * 0.08D;
                    living.velocityChanged = true;
                }
            }
        }

        // Creepers try to patch nearby air gaps rather than destroy buildings.
        if (living instanceof EntityCreeper && living.ticksExisted % 30 == 0) {
            repairNearbyHole(living.world, living.getPosition());
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        EntityPlayer player = event.getPlayer();
        if (player == null || player.world.isRemote || player.capabilities.isCreativeMode) return;
        if (player.world.rand.nextFloat() < 0.30F) {
            IBlockState state = event.getState();
            BlockPos pos = event.getPos();
            player.attackEntityFrom(DamageSource.MAGIC, 2.0F);
            // Restore after the break event has been scheduled by the server tick.
            player.world.scheduleUpdate(pos, state.getBlock(), 1);
            player.world.setBlockState(pos, state, 3);
            player.sendMessage(new net.minecraft.util.text.TextComponentString("§cREALITY GLITCH: The block fought back!"));
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote) return;
        tickCounter++;
        if (tickCounter % 240 == 0) {
            for (EntityPlayer player : event.world.playerEntities) {
                if (event.world.rand.nextFloat() < 0.5F) {
                    player.motionX = -player.motionX * 1.25D;
                    player.motionZ = -player.motionZ * 1.25D;
                    player.motionY = Math.max(player.motionY, 0.15D);
                    player.velocityChanged = true;
                    player.sendMessage(new net.minecraft.util.text.TextComponentString("§dREALITY GLITCH: Your momentum reversed!"));
                }
            }
        }
    }

    private void repairNearbyHole(World world, BlockPos center) {
        for (int i = 0; i < 12; i++) {
            BlockPos pos = center.add(world.rand.nextInt(7) - 3, world.rand.nextInt(5) - 2, world.rand.nextInt(7) - 3);
            if (!world.isAirBlock(pos)) continue;
            BlockPos[] around = {pos.north(), pos.south(), pos.east(), pos.west(), pos.down(), pos.up()};
            for (BlockPos neighbour : around) {
                IBlockState state = world.getBlockState(neighbour);
                if (state.getBlock() != Blocks.AIR && state.isFullBlock()) {
                    world.setBlockState(pos, state, 3);
                    break;
                }
            }
        }
    }
}
