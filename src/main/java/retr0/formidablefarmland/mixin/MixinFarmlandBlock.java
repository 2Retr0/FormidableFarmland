package retr0.formidablefarmland.mixin;

import net.minecraft.block.FarmlandBlock;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FarmlandBlock.class)
public abstract class MixinFarmlandBlock {
    /**
     * Changes the probabilities for the fall distance at which farmland will break as such:
     * <ul>
     *    <li>1.5 block fall distance → 0.0</li>
     *    <li>2.0 block fall distance → 0.5</li>
     *    <li>3.5 block fall distance → 1.0</li>
     * </ul>
     */
    @Redirect(
        method = "onLandedUpon",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/math/random/Random;nextFloat()F"))
    public float redirectNextFloat(Random instance) {
        // Original: Random.nextFloat()                     < fallDistance - 0.5f
        // Want:     2f * square(Random.nextFloat()) + 1.5f < fallDistance
        return 2f * MathHelper.square(instance.nextFloat()) + 2f;
    }
}
