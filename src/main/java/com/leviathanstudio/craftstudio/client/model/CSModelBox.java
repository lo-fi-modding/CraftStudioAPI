package com.leviathanstudio.craftstudio.client.model;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.math.Vector3d;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.model.TexturedQuad;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Class used to render a box in a {@link ModelPart} or a
 * {@link ModelPart}.</br>
 * Partially based on {@link ModelPart.Cube ModelBox}.
 *
 * @author Timmypote
 * @since 0.3.0
 */
@OnlyIn(Dist.CLIENT)
public class CSModelBox {
    private final static double NORM_PREC = 0.0001;
    /**
     * An array of 6 TexturedQuads, one for each face of a cube.
     */
    private final ModelPart.Polygon[] quadList;
    /**
     * The box name.
     **/
    public String boxName;

    /**
     * Create a textured rectangular box.
     *
     * @param renderer The (CS)RendererModel to which the box will be add.
     * @param texU     The X coordinate of the texture.
     * @param texV     The Y coordinate of the texture.
     * @param x        The X coordinate of the starting point of the box.
     * @param y        The Y coordinate of the starting point of the box.
     * @param z        The Z coordinate of the starting point of the box.
     * @param dx       The length of the box on the X axis.
     * @param dy       The length of the box on the Y axis.
     * @param dz       The length of the box on the Z axis.
     */
    public CSModelBox(ModelPart renderer, int texU, int texV, float x, float y, float z, float dx, float dy, float dz) {
        this(renderer, getVerticesForRect(x, y, z, dx, dy, dz), getTextureUVsForRect(texU, texV, dx, dy, dz));
    }

    /**
     * Create a box from PositionTextureVertex and texture it with textUVs
     * without textures mirror precision.<br>
     * See {@link #setVertex(ModelPart.Vertex[]) setVertex()} and
     * {@link #setTexture(ModelPart, int[][]) setTexture()} for orders.
     *
     * @param renderer              The (CS)RendererModel to which the box will be add.
     * @param positionTextureVertex The 8 vertices used to create the box.
     * @param textUVs               The 6 pairs of points used to set the textures' UVs for each
     *                              faces.
     */
    public CSModelBox(ModelPart renderer, ModelPart.Vertex positionTextureVertex[], int[][] textUVs) {
        this(positionTextureVertex);
        this.setTexture(renderer, textUVs);
        this.checkBlockForShadow();
    }

    /**
     * Create a box from PositionTextureVertex.<br>
     * See {@link #setVertex(ModelPart.Vertex[]) setVertex()} for order.
     *
     * @param positionTextureVertex The 8 vertices used to create the box.
     */
    public CSModelBox(ModelPart.Vertex positionTextureVertex[]) {
        this(6);
        this.setVertex(positionTextureVertex);
    }

    /**
     * Just create a box with a list of facesNumber unset TexturedQuad.
     *
     * @param facesNumber The number of faces the box will have.
     */
    public CSModelBox(int facesNumber) {
        this.quadList = new ModelPart.Polygon[facesNumber];
    }

    /**
     * Calculate the PositionTextureVertex from a rectangular box.
     *
     * @param x      The X coordinate of the starting point of the box.
     * @param y      The Y coordinate of the starting point of the box.
     * @param z      The Z coordinate of the starting point of the box.
     * @param dx     The length of the box on the X axis.
     * @param dy     The length of the box on the Y axis.
     * @param dz     The length of the box on the Z axis.
     * @return An 8 long array of PositionTextureVertex that can be used to
     * create a rectangular box.
     */
    public static ModelPart.Vertex[] getVerticesForRect(float x, float y, float z, float dx, float dy, float dz) {
      ModelPart.Vertex[] positionTextureVertex = new ModelPart.Vertex[8];
        float endX = x + dx;
        float endY = y + dy;
        float endZ = z + dz;

        positionTextureVertex[0] = new ModelPart.Vertex(x, y, z, 0.0F, 0.0F);
        positionTextureVertex[1] = new ModelPart.Vertex(endX, y, z, 0.0F, 0.0F);
        positionTextureVertex[2] = new ModelPart.Vertex(endX, endY, z, 0.0F, 0.0F);
        positionTextureVertex[3] = new ModelPart.Vertex(x, endY, z, 0.0F, 0.0F);
        positionTextureVertex[4] = new ModelPart.Vertex(x, y, endZ, 0.0F, 0.0F);
        positionTextureVertex[5] = new ModelPart.Vertex(endX, y, endZ, 0.0F, 0.0F);
        positionTextureVertex[6] = new ModelPart.Vertex(endX, endY, endZ, 0.0F, 0.0F);
        positionTextureVertex[7] = new ModelPart.Vertex(x, endY, endZ, 0.0F, 0.0F);

        return positionTextureVertex;
    }

    /**
     * Calculate the textures' UVs for a rectangular box.
     *
     * @param texU The X coordinate of the texture.
     * @param texV The Y coordinate of the texture.
     * @param dx   The length of the box on the X axis.
     * @param dy   The length of the box on the Y axis.
     * @param dz   The length of the box on the Z axis.
     * @return A 6 long array of pairs of UV that can be used to texture a
     * rectangular box.
     */
    public static int[][] getTextureUVsForRect(int texU, int texV, float dx, float dy, float dz) {
        dy = -dy;
        dz = -dz;
        int[][] tab = new int[][]{{(int) (texU + dz + dx + dz), (int) (texV + dz + dy), (int) (texU + dz + dx), (int) (texV + dz)},
                {(int) (texU + dz), (int) (texV + dz + dy), texU, (int) (texV + dz)},
                {(int) (texU + dz + dx), texV, (int) (texU + dz + dx + dx), (int) (texV + dz)},
                {(int) (texU + dz), texV, (int) (texU + dz + dx), (int) (texV + dz)},
                {(int) (texU + dz + dx + dz + dx), (int) (texV + dz + dy), (int) (texU + dz + dx + dz), (int) (texV + dz)},
                {(int) (texU + dz + dx), (int) (texV + dz + dy), (int) (texU + dz), (int) (texV + dz)}};
        return tab;
    }

    /**
     * Set the vertices of the box ! A
     * {@link #setTexture(ModelPart, int[][]) setTexture()} is necessary
     * after that.<br>
     * <p>
     * Order of the vertices:<br>
     * vertices[0] = (0, 0, 0) (bloc's origin)<br>
     * vertices[1] = (x, 0, 0)<br>
     * vertices[2] = (x, y, 0)<br>
     * vertices[3] = (0, y, 0)<br>
     * vertices[4] = (0, 0, z)<br>
     * vertices[5] = (x, 0, z)<br>
     * vertices[6] = (x, y, z) (bloc's end)<br>
     * vertices[7] = (0, y, z)<br>
     *
     * @param positionTextureVertex The 8 vertices that will replace the old ones.
     */
    public void setVertex(ModelPart.Vertex positionTextureVertex[]) {
        if (positionTextureVertex.length == 8) {
            this.quadList[0] = new TexturedQuad(new ModelPart.Vertex[]{positionTextureVertex[5], positionTextureVertex[1],
                    positionTextureVertex[2], positionTextureVertex[6]});
            this.quadList[1] = new TexturedQuad(new ModelPart.Vertex[]{positionTextureVertex[0], positionTextureVertex[4],
                    positionTextureVertex[7], positionTextureVertex[3]});
            this.quadList[2] = new TexturedQuad(new ModelPart.Vertex[]{positionTextureVertex[5], positionTextureVertex[4],
                    positionTextureVertex[0], positionTextureVertex[1]});
            this.quadList[3] = new TexturedQuad(new ModelPart.Vertex[]{positionTextureVertex[2], positionTextureVertex[3],
                    positionTextureVertex[7], positionTextureVertex[6]});
            this.quadList[4] = new TexturedQuad(new ModelPart.Vertex[]{positionTextureVertex[1], positionTextureVertex[0],
                    positionTextureVertex[3], positionTextureVertex[2]});
            this.quadList[5] = new TexturedQuad(new ModelPart.Vertex[]{positionTextureVertex[4], positionTextureVertex[5],
                    positionTextureVertex[6], positionTextureVertex[7]});
        }
    }

    /**
     * Check and correct the problem of dark texture.
     */
    private void checkBlockForShadow() {
        Vector3d or = this.quadList[1].field_78239_a[0].vector3D;
        double x = this.quadList[0].field_78239_a[1].vector3D.x, y = this.quadList[1].field_78239_a[3].vector3D.y,
                z = this.quadList[1].field_78239_a[1].vector3D.z;
        if (x - or.x < 0)
            this.flipFaces();
        if (y - or.y > 0)
            this.flipFaces();
        if (z - or.z > 0)
            this.flipFaces();
    }

    /**
     * Flip all the vertices of all the faces.
     */
    private void flipFaces() {
        for (int i = 0; i < this.quadList.length; i++)
            this.quadList[i].flipFace();
    }

    /**
     * Set the textures' UVs for each faces.<br>
     * <p>
     * Faces order:<br>
     * faces[0] = X1<br>
     * faces[1] = X0<br>
     * faces[2] = Y0<br>
     * faces[3] = Y1<br>
     * faces[4] = Z0<br>
     * faces[5] = Z1<br>
     * Textures' UVs order :<br>
     * coord[0] = U0 (x0)<br>
     * coord[1] = V0 (y0) (Top/Left)<br>
     * coord[2] = U1 (x1)<br>
     * coord[3] = V1 (y1) (Bottom/Right)<br>
     *
     * @param renderer The (CS)RendererModel to which the box will be add.
     * @param textUVs  The 6 pairs of points used to set the textures' UVs for each
     *                 faces.
     */
    public void setTexture(ModelPart renderer, int[][] textUVs) {
        int[] textUV;
        if (textUVs.length == 6)
            for (int i = 0; i < 6; i++) {
                textUV = textUVs[i];
                if (textUV.length == 4)
                    this.quadList[i] = new TexturedQuad(this.quadList[i].field_78239_a, textUV[0], textUV[1], textUV[2], textUV[3],
                            renderer.textureWidth, renderer.textureHeight);
            }
    }

    /**
     * Function used to prepare the rendering of the bloc.
     *
     * @param renderer VertexBuffer from the Tesselator.
     * @param scale    Scale factor.
     */
    public void render(BufferBuilder renderer, float scale) {
        for (ModelPart.Polygon texturedquad : this.quadList)
            texturedquad.draw(renderer, scale);
    }

    /**
     * Set the box name.
     *
     * @param name The name given to the box.
     * @return The CSModelBox.
     */
    public CSModelBox setBoxName(String name) {
        this.boxName = name;
        return this;
    }

    /**
     * Get the TexturedQuad array so it can be modified.
     *
     * @return The TexturedQuad array.
     */
    public ModelPart.Polygon[] getQuadList() {
        return this.quadList;
    }
}
