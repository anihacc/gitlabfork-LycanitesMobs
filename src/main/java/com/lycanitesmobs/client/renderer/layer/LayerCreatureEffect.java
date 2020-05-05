package com.lycanitesmobs.client.renderer.layer;

import com.lycanitesmobs.client.renderer.CreatureRenderer;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class LayerCreatureEffect extends LayerCreatureBase {
	public String textureSuffix;
	public boolean subspecies = true;
	public Vec2f scrollSpeed;


    // ==================================================
    //                   Constructor
    // ==================================================
    public LayerCreatureEffect(CreatureRenderer renderer, String textureSuffix) {
        super(renderer);
        this.name = textureSuffix;
        this.textureSuffix = textureSuffix;
    }

	public LayerCreatureEffect(CreatureRenderer renderer, String textureSuffix, boolean glow, int blending, boolean subspecies) {
		super(renderer);
		this.name = textureSuffix;
		this.textureSuffix = textureSuffix;
		this.glow = glow;
		this.blending = blending;
		this.subspecies = subspecies;
	}

	public LayerCreatureEffect(CreatureRenderer renderer, String name, String textureSuffix, boolean glow, int blending, boolean subspecies) {
		super(renderer);
		this.name = name;
		this.textureSuffix = textureSuffix;
		this.glow = glow;
		this.blending = blending;
		this.subspecies = subspecies;
	}

    @Override
    public Vector4f getPartColor(String partName, BaseCreatureEntity entity, boolean trophy) {
        return new Vector4f(1, 1, 1, 1);
    }

    @Override
    public Identifier getLayerTexture(BaseCreatureEntity entity) {
		return entity.getTexture(this.textureSuffix);
    }

	@Override
	public Vec2f getTextureOffset(String partName, BaseCreatureEntity entity, boolean trophy, float loop) {
    	if(this.scrollSpeed == null) {
			this.scrollSpeed = new Vec2f(0, 0);
		}
		return new Vec2f(loop * this.scrollSpeed.x, loop * this.scrollSpeed.y);
	}

	@Override
	public int getBrightness(String partName, BaseCreatureEntity entity, int brightness) {
    	if(this.glow) {
    		return 240;
		}
		return brightness;
	}
}
