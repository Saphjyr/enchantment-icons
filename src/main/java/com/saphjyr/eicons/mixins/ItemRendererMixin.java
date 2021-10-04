package com.saphjyr.eicons.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.block.TransparentBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.BlockItem;

@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    
    @Inject(method = "renderItem", at = @At(value = "HEAD"))
	private void onRenderItem(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo callbackInfo) {
        if (!stack.isEmpty()) {

            boolean bl3;
            if (renderMode != ModelTransformation.Mode.GUI && !renderMode.isFirstPerson() && stack.getItem() instanceof BlockItem) {
               Block block = ((BlockItem)stack.getItem()).getBlock();
               bl3 = !(block instanceof TransparentBlock) && !(block instanceof StainedGlassPaneBlock);
            } else {
               bl3 = true;
            }

            RenderLayer renderLayer = RenderLayers.getItemLayer(stack, bl3);
            VertexConsumer vertexConsumer4;

            if (stack.getItem() == Items.DIAMOND_CHESTPLATE) {
                matrices.push();
                model = ((ItemRendererInvoker)this).getModels().getModelManager().getModel(new ModelIdentifier("minecraft:trident#inventory"));
                MatrixStack.Entry entry = matrices.peek();
                if (renderMode == ModelTransformation.Mode.GUI) {
                   entry.getModel().multiply(0.5F);
                   entry.getModel().translate(1.0F, 1.0F, 1F);
                } else if (renderMode.isFirstPerson()) {
                   entry.getModel().multiply(0.75F);
                }
                

                if (bl3) {
                    vertexConsumer4 = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, renderLayer, true, stack.hasGlint());
                    } else {
                    vertexConsumer4 = ItemRenderer.getItemGlintConsumer(vertexConsumers, renderLayer, true, stack.hasGlint());
                    }
                 
                matrices.pop();
                ((ItemRendererInvoker)this).renderBakedItemModelInvoker(model, stack, light, overlay, matrices, vertexConsumer4);
             
            }

        }
    }
}
