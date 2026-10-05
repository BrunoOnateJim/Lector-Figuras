package io;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class LectorBMP {
    private int ancho;
    private int alto;
    private int[][] pixeles;

    public void leerImagen(String ruta) throws IOException {
        byte[] d;
        try (DataInputStream in = new DataInputStream(new FileInputStream(ruta))) {
            d = in.readAllBytes();
        }
        if (d.length < 54 || d[0] != 'B' || d[1] != 'M') {
            throw new IOException("el archivo no es un BMP valido");
        }
        int offset = entero(d, 10);
        ancho = entero(d, 18);
        int altoRaw = entero(d, 22);
        int bits = (d[28] & 0xff) | ((d[29] & 0xff) << 8);
        int compresion = entero(d, 30);
        if (compresion != 0 || (bits != 24 && bits != 32)) {
            throw new IOException("solo se soportan BMP sin compresion de 24 o 32 bits (este: "
                    + bits + " bits, compresion " + compresion + ")");
        }
        boolean arribaAbajo = altoRaw < 0;
        alto = Math.abs(altoRaw);
        if (ancho <= 0 || alto == 0) {
            throw new IOException("dimensiones invalidas");
        }
        int bytesPx = bits / 8;
        int bytesFila = ((ancho * bytesPx + 3) / 4) * 4;   // cada fila se rellena a multiplo de 4
        if ((long) offset + (long) bytesFila * alto > d.length) {
            throw new IOException("el archivo esta incompleto o corrupto");
        }
        pixeles = new int[alto][ancho];
        for (int f = 0; f < alto; f++) {
            int filaDestino = arribaAbajo ? f : alto - 1 - f;   // BMP normal viene de abajo hacia arriba
            int base = offset + f * bytesFila;
            for (int c = 0; c < ancho; c++) {
                int i = base + c * bytesPx;
                pixeles[filaDestino][c] = (d[i] & 0xff) | ((d[i + 1] & 0xff) << 8) | ((d[i + 2] & 0xff) << 16);
            }
        }
    }

    private static int entero(byte[] d, int i) {
        return (d[i] & 0xff) | ((d[i + 1] & 0xff) << 8) | ((d[i + 2] & 0xff) << 16) | ((d[i + 3] & 0xff) << 24);
    }

    public int[][] getPixeles() {
	return pixeles;
    }
    public int getAncho() {
	return ancho;
    }
    public int getAlto() {
	return alto;
    }
}
