package com.example.world;
    
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldRender extends JavaPlugin implements Listener {

    private Random random = new Random();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);

        // Otimização de chunks ao carregar
        Bukkit.getServer().getScheduler().runTaskTimer(this, () -> {
            for (World world : Bukkit.getWorlds()) {
                for (Chunk chunk : world.getLoadedChunks()) {
                    WorldRender(chunk);
                }
            }
        }, 20L, 20L);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        WorldRender(event.getChunk());
    }

    private void WorldRender(Chunk chunk) {
        // Remover entidades desnecessárias
        List<Entity> entities = chunk.getEntities();
        for (Entity entity : entities) {
            if (entity.isDead() || entity.getType().isStationary()) {
                entity.remove();
            }
        }

        // Substituir blocos de ar por pedra
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 256; y++) {
                for (int z = 0; z < 16; z++) {
                    Block block = chunk.getBlock(x, y, z);
                    if (block.getType() == Material.AIR) {
                        block.setType(Material.STONE);
                    }
                }
            }
        }
    }
