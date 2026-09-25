package io;
import java.io.FileInputStream;
import java.io.IOException; 
public class LectorBMP{
    int ancho;
    int alto;
    int[][] pixeles;

    public void leerImagen(String ruta) {
        try{
            FileInputStream lectorBMP =  new FileInputStream(ruta);
            byte[] matrizBytes = new byte[54];
            lectorBMP.read(matrizBytes, 0, 54);
            ancho = (matrizBytes[18] & 0xff) | ((matrizBytes[19] & 0xff) << 8) | ((matrizBytes[20] & 0xff) << 16) | ((matrizBytes[21] & 0xff) << 24);
            alto = (matrizBytes[22] & 0xff) | ((matrizBytes[23] & 0xff) << 8) | ((matrizBytes[24] & 0xff) << 16) | ((matrizBytes[25] & 0xff) << 24);
            System.out.println("Ancho: " + ancho);
            System.out.println("Alto: " + alto);
            pixeles = new int[alto][ancho];
            byte[] color = new byte[3];
            for(int fila = 0; fila < alto; fila++){
                for(int columna = 0; columna < ancho; columna ++){
                    lectorBMP.read(color);
                    int valorColor = (color[0] & 0xff) | ((color[1] & 0xff) << 8) | ((color[2] & 0xff) << 16);
                    pixeles[fila][columna] = valorColor;
                }
            }
            
            lectorBMP.close();
        }catch(Exception e){
            System.out.println("Error al leer la imagen: " + e.getMessage());
        }
    }

    public int[][] getPixeles(){
        return pixeles;
    }
    public int getAncho(){
        return ancho;
    }
    public int getAlto(){
        return alto;
    }
}