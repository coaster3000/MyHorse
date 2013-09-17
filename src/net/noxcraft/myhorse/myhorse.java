package net.noxcraft.myhorse;

import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;


@SuppressWarnings("unused")
public class myhorse extends JavaPlugin implements Listener
{
	
	private String noxglobalprefix = ChatColor.WHITE + "[" + ChatColor.AQUA + "G" + ChatColor.WHITE + "] " + ChatColor.GOLD + "[" + ChatColor.RED + "N" + ChatColor.GOLD + "]" + ChatColor.WHITE + ": ";
    private Entity hanging;
    private Entity remover;
	private boolean canRack;
    
    
	@Override
    public void onEnable()
    {
        getLogger().info("MyHorse Enabled!");
        getServer().getPluginManager().registerEvents(this, this);
    }
	
	
	
    
    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void PlayerInteractEntityEvent(PlayerInteractEntityEvent event)
    {
    	
    	ApplicableRegionSet set = WGBukkit.getRegionManager(event.getPlayer().getWorld()).getApplicableRegions(event.getPlayer().getLocation());
    	if (set == null)
    	{
    		return;
    	}
    	
    	//event.getPlayer().sendMessage(noxglobalprefix + ChatColor.GREEN + "Player interact entity event called!");
    	
    	if(!set.allows(DefaultFlag.USE))
    	{
    		if (event.getRightClicked().getType() == EntityType.LEASH_HITCH)
    		{

        		if (event.getPlayer().getItemInHand().getType() != Material.LEASH)
        		{
        			for (Entity nearby : event.getPlayer().getNearbyEntities(10, 10, 10))
        			{
        			
        				if (nearby.getType() == EntityType.HORSE)
        				{
        					
        					if (((LivingEntity) nearby).isLeashed())
        					{
        						
        						if (((LivingEntity) nearby).getLeashHolder() != event.getPlayer())
        						{
        							if (((Tameable) nearby).getOwner() != event.getPlayer())
        							{
        								if (((LivingEntity) nearby).getLeashHolder() == event.getRightClicked())
        								{
        									
        									event.getPlayer().sendMessage(this.noxglobalprefix + ChatColor.RED + "That horse is not yours!");
        									event.setCancelled(true);
        								}
        							}
        						}
        					}
        				}
        			}
        		}
    			else if (event.getPlayer().getItemInHand().getType() == Material.LEASH)
    			{
    				
    				this.canRack = true;

        			for (Entity it : event.getPlayer().getNearbyEntities(10, 10, 10))
        			{
        			
        				if (it.getType() == EntityType.HORSE)
        				{
        					
        					if (((LivingEntity) it).isLeashed())
        					{
        						
        						if (((LivingEntity) it).getLeashHolder() != event.getPlayer())
        						{
        							
        							if (((LivingEntity) it).getLeashHolder() == event.getRightClicked())
        							{
        								this.canRack = false;
        							}
        						}
        					}
        				}
        			}
        			
        			if (this.canRack == true)
        			{
        				event.getPlayer().sendMessage(this.noxglobalprefix + ChatColor.GREEN + "Your horse has been racked");
        			}
        			
        			else if(this.canRack == false)
        			{
	        			event.getPlayer().sendMessage(this.noxglobalprefix + ChatColor.RED + "That rack is being used");
	        			event.setCancelled(true);
        			}
    			}
    		}
    	}
    }
	
    
	
	
	/*@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void PlayerInteractEvent(PlayerInteractEvent event)
    {
		
    }*/
	
    
    
	
    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void HangingBreakByEntityEvent(HangingBreakByEntityEvent event)
    {
    	
    	ApplicableRegionSet set = WGBukkit.getRegionManager(event.getEntity().getWorld()).getApplicableRegions(event.getEntity().getLocation());
    	if (set == null)
    	{
    		return;
    	}
    	
    	if (!set.allows(DefaultFlag.USE))
    	{
	        //((Player)event.getRemover()).sendMessage("running entity break event");
	    	remover = event.getRemover();
	    	hanging = event.getEntity();
	    	if (hanging.getType() == EntityType.LEASH_HITCH)
	    	{
	    		//((Player)event.getRemover()).sendMessage("breaking leash");
	    		LivingEntity owners = null;
	    		for (Entity it : hanging.getNearbyEntities(10, 10, 10))
	    		{
	    			
	    			if (it.getType() == EntityType.HORSE && ((LivingEntity)it).isLeashed())
	    			{
	    				//((Player)event.getRemover()).sendMessage("nearby horse found");
	
						if (((LivingEntity)it).getLeashHolder() == hanging)
						{
			    			//((Player)event.getRemover()).sendMessage("horse on rack");
							
							if (((Tameable)it).getOwner() != ((AnimalTamer)remover))
							{
		    					event.setCancelled(true);
							}
							else if (((Tameable)it).getOwner() == ((AnimalTamer)remover))
							{
								owners = ((LivingEntity)it);
							}
	    				}
	    			}
	    		}
	    		if (event.isCancelled() && owners != null)
	    		{
    				owners.setLeashHolder(remover);
    				//((Player)event.getRemover()).sendMessage("nothing to say here");
    			}
	    	
    			else if (event.isCancelled() && owners == null)
    			{
    			((Player)event.getRemover()).sendMessage(this.noxglobalprefix + ChatColor.RED + "That horse is not yours!");
    			}
	    	}
	    }
    }
}
