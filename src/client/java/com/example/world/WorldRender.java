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
                    otimizarChunk(chunk);
                }
            }
        }, 20L, 20L);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        otimizarChunk(event.getChunk());
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

        // Gerar árvores aleatoriamente
        for (int i = 0; i < 10; i++) {
            int x = random.nextInt(16);
            int z = random.nextInt(16);
            int y = chunk.getHighestBlockYAt(x, z);

            if (y > 0 && chunk.getBlock(x, y - 1, z).getType() == Material.DIRT) {
                Bukkit.getServer().getScheduler().runTaskLater(this, () -> {
                    World world = chunk.getWorld();
                    world.generateTree(chunk.getBlock(x, y, z).getLocation(), TreeType.BIG_TREE);
                }, 1L);
            }
        }
    }
}
