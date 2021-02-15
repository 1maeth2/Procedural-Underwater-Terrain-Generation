package Textures;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class Texture {
	private int texture;
	private int width,height;
	public Texture(String filename){
		texture = load(filename);
	}
	public int load(String path){
		BufferedImage bi; //creates image
		int id = 0;
		try{
			bi = ImageIO.read(new File(path));//Loads image
			width = bi.getWidth();
			height = bi.getHeight();
			
			int[] pixels_raw = new int[width*height*4];
			pixels_raw = bi.getRGB(0, 0, width, height, null, 0, width);//puts pixel rgb value into array

			ByteBuffer pixels = BufferUtils.createByteBuffer(width*height*4);

			for(int i = 0;i<width;i++){
				for(int j = 0; j<height;j++){
					int pixel = pixels_raw[i*width+j];//transforms rgb pixel array into byte buffer
					pixels.put((byte)((pixel >> 16) & 0xFF)); //RED
					pixels.put((byte)((pixel >> 8) & 0xFF));//GREEN
					pixels.put((byte)(pixel & 0xFF));//BLUE
					pixels.put((byte)((pixel >> 24) & 0xFF));//ALPHA
				}
			}
			
			pixels.flip();
			
			id = GL11.glGenTextures();//Creates a new Texture and saves it in video memory for shaders to access
			
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);//Binds texture to a GL_TEXTURE_2D in order to edit
			
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
			
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			
		}catch (IOException e){
			e.printStackTrace();
		}
		return id;
	}
	public void bind(){
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
	}

	public void unbind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	public int getTextureID() {
		return texture;
	}

}
