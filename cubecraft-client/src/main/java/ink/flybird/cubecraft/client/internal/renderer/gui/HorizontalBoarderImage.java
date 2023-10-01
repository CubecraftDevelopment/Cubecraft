package ink.flybird.cubecraft.client.internal.renderer.gui;

import com.google.gson.*;
import ink.flybird.cubecraft.client.ClientRenderContext;
import ink.flybird.cubecraft.client.gui.node.Node;
import ink.flybird.cubecraft.client.render.renderer.IComponentPartRenderer;
import ink.flybird.cubecraft.client.resources.ResourceLocation;
import ink.flybird.cubecraft.client.resources.item.ImageResource;
import ink.flybird.quantum3d_legacy.ShapeRenderer;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import ink.flybird.quantum3d_legacy.draw.VertexBuilderAllocator;
import ink.flybird.quantum3d_legacy.textures.Texture2D;

import java.lang.reflect.Type;
import java.util.Set;

public record HorizontalBoarderImage(double x0,double x1,double y0,double y1,int boarder,String loc) implements IComponentPartRenderer {

    @Override
    public void render(Node node) {
        int x= (int) (node.getLayout().getAbsoluteX() +x0* node.getLayout().getAbsoluteWidth());
        int y= (int) (node.getLayout().getAbsoluteY() +y0* node.getLayout().getAbsoluteHeight());
        int z=node.getLayout().layer;
        int w= (int) (node.getLayout().getAbsoluteWidth() *(x1-x0));
        int h= (int) (node.getLayout().getAbsoluteHeight() *(y1-y0));

        Texture2D tex= ClientRenderContext.TEXTURE.getTexture2DContainer().get(ResourceLocation.uiTexture(this.loc.split(":")[0],this.loc.split(":")[1]).format());
        double tbh=(double) boarder/ tex.getWidth();

        int x0In=x+boarder,x1In=x+w-boarder,x1Out=x+w;
        VertexBuilder builder = VertexBuilderAllocator.createByPrefer(12);
        builder.begin();
        ShapeRenderer.drawRectUV(builder, x,x0In,y,y+h,z, 0,tbh,0,1);
        ShapeRenderer.drawRectUV(builder,x0In,x1In,y,y+h,z, tbh,1-tbh,0,1);
        ShapeRenderer.drawRectUV(builder,x1In,x1Out,y,y+h,z, 1-tbh,1,0,1);
        builder.end();
        builder.uploadPointer();
        builder.free();
    }

    @Override
    public void initializeRenderer(Set<ImageResource> loc) {
        loc.add(new ImageResource(this.loc));
    }

    public static class JDeserializer implements JsonDeserializer<HorizontalBoarderImage>{
        @Override
        public HorizontalBoarderImage deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject root=jsonElement.getAsJsonObject();
            return new HorizontalBoarderImage(
                    root.get("pos").getAsJsonArray().get(0).getAsDouble(),
                    root.get("pos").getAsJsonArray().get(1).getAsDouble(),
                    root.get("pos").getAsJsonArray().get(2).getAsDouble(),
                    root.get("pos").getAsJsonArray().get(3).getAsDouble(),
                    root.get("boarder").getAsInt(),
                    root.get("loc").getAsString());
        }
    }
}
