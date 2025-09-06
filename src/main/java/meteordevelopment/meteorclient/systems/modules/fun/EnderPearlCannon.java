package meteordevelopment.meteorclient.systems.modules.fun;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;

import net.minecraft.entity.EntityType;

public class EnderPearlCannon extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> distance = sgGeneral.add(new DoubleSetting.Builder()
        .name("distance")
        .description("The distance to launch the pearl in blocks.")
        .defaultValue(1000000)
        .min(1)
        .sliderMax(10000000)
        .build()
    );

    private final Setting<Boolean> launch = sgGeneral.add(new BoolSetting.Builder()
        .name("launch")
        .description("Launches the ender pearl.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> chunkByChunk = sgGeneral.add(new BoolSetting.Builder()
        .name("chunk-by-chunk")
        .description("Teleports chunk by chunk to prevent falling through the world.")
        .defaultValue(false)
        .build()
    );

    private EnderPearlEntity fakePearl;

    public EnderPearlCannon() {
        super(Categories.Fun, "ender-pearl-cannon", "Shoots ender pearls very far.");
    }

    @Override
    public void onActivate() {
        if (launch.get()) {
            launchPearl();
            launch.set(false);
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (fakePearl != null) {
            if (fakePearl.age > 80) { // Remove after 4 seconds
                fakePearl.remove(Entity.RemovalReason.DISCARDED);
                fakePearl = null;
            } else {
                fakePearl.setPosition(fakePearl.getX() + fakePearl.getVelocity().x, fakePearl.getY() + fakePearl.getVelocity().y, fakePearl.getZ() + fakePearl.getVelocity().z);
            }
        }
    }

    private void launchPearl() {
        new Thread(() -> {
            double startX = mc.player.getX();
            double startY = mc.player.getY();
            double startZ = mc.player.getZ();

            double yaw = Math.toRadians(mc.player.getYaw());

            double totalDistance = distance.get();

            double endX = startX - Math.sin(yaw) * totalDistance;
            double endZ = startZ + Math.cos(yaw) * totalDistance;

            if (chunkByChunk.get()) {
                // Chunk by chunk teleportation
                int startChunkX = (int) startX >> 4;
                int startChunkZ = (int) startZ >> 4;
                int endChunkX = (int) endX >> 4;
                int endChunkZ = (int) endZ >> 4;

                // Calculate path between chunks (simplified for now, just straight line)
                // This needs to be more robust for non-straight lines
                double deltaX = endX - startX;
                double deltaZ = endZ - startZ;
                double distancePerChunk = Math.sqrt(16 * 16 + 16 * 16); // Diagonal distance of a chunk

                for (double d = 0; d < totalDistance; d += distancePerChunk) {
                    double currentX = startX + (deltaX * (d / totalDistance));
                    double currentZ = startZ + (deltaZ * (d / totalDistance));

                    int chunkX = (int) currentX >> 4;
                    int chunkZ = (int) currentZ >> 4;

                    mc.execute(() -> {
                        mc.player.setPosition(currentX, 300, currentZ);
                    });

                    while (!mc.world.getChunkManager().isChunkLoaded(chunkX, chunkZ)) {
                        mc.execute(() -> {
                            mc.player.setPosition(currentX, 300, currentZ);
                        });
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                // Single teleportation
                mc.execute(() -> {
                    mc.player.setPosition(endX, 300, endZ);
                });

                int chunkX = (int) endX >> 4;
                int chunkZ = (int) endZ >> 4;

                while (!mc.world.getChunkManager().isChunkLoaded(chunkX, chunkZ)) {
                    mc.execute(() -> {
                        mc.player.setPosition(endX, 300, endZ);
                    });
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Final teleport to ground and fake pearl creation
            mc.execute(() -> {
                int endY = mc.world.getChunk((int) endX >> 4, (int) endZ >> 4).sampleHeightmap(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING, (int) endX, (int) endZ);
                mc.player.setPosition(endX, endY + 2, endZ);

                fakePearl = new EnderPearlEntity(EntityType.ENDER_PEARL, mc.world);
                fakePearl.setPosition(startX, startY, startZ);

                double velocityX = endX - startX;
                double velocityY = endY - startY;
                double velocityZ = endZ - startZ;
                double magnitude = Math.sqrt(velocityX * velocityX + velocityY * velocityY + velocityZ * velocityZ);
                velocityX /= magnitude;
                velocityY /= magnitude;
                velocityZ /= magnitude;

                fakePearl.setVelocity(velocityX, velocityY, velocityZ);

                mc.world.addEntity(fakePearl);
            });
        }).start();
    }
}
