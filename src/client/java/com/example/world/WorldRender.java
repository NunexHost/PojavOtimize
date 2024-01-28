package com.example.world;
    
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.level.block.Blocks;

public class WorldOptimizer {

    private static final Minecraft mc = Minecraft.getInstance();
    private static final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static void main(String[] args) {
        // Define a área a ser otimizada
        int radius = 100;

        // Obtém a posição do jogador
        BlockPos playerPos = mc.player.getPosition();

        // Cria um callable para cada chunk na área
        Callable<Void>[] tasks = new Callable[radius * radius * 2];
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                int index = (i + radius) * radius * 2 + (j + radius);
                tasks[index] = () -> {
                    optimizeChunk(playerPos.getX() + i, playerPos.getZ() + j);
                    return null;
                };
            }
        }

        // Executa as tarefas em paralelo
        try {
            Future<?>[] futures = executorService.invokeAll(Arrays.asList(tasks));
            for (Future<?> future : futures) {
                future.get();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Informa o jogador que a otimização foi concluída
        mc.player.sendMessage("Otimização do mundo concluída!");
    }

    private static void optimizeChunk(int x, int z) {
        // Obtém o chunk
        Chunk chunk = mc.world.getChunk(x, z);

        // Apaga blocos
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 256; k++) {
                    chunk.setBlockState(new BlockPos(i, j, k), Blocks.AIR.getDefaultState());
                }
            }
        }

        // Atualiza a luz
        chunk.relight(null, false);

        // Salva o chunk
        chunk.markDirty();
    }
}
