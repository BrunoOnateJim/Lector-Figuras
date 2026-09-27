package clasificacion;

import java.util.ArrayList;
import java.util.List;

/**
 * Contorno exterior de una figura: la lista ordenada de los pixeles del borde,
 * recorrida en el sentido de las manecillas del reloj.
 * Los puntos son arreglos {x, y}: x es la columna y y es la fila.
 */
public class Contorno {
    /**
     * Las 8 direcciones vecinas en el sentido de las manecillas del reloj,
     * empezando por el oeste (en la imagen y crece hacia abajo).
     */
    private static final int[][] DIRECCIONES = {
        {-1, 0}, {-1, -1}, {0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}
    };

    private final List<int[]> puntos;

    private Contorno(List<int[]> puntos) {
        this.puntos = puntos;
    }

    /**
     * Sigue el borde exterior de la figura con el algoritmo de vecindad de Moore.
     * Se empieza en el pixel mas arriba a la izquierda y se va girando alrededor
     * de cada pixel del borde hasta encontrar al siguiente, hasta volver al inicio.
     */
    public static Contorno seguir(List<int[]> pixeles) {
        // Se copia la figura a una mascara del tamano de su caja, con un pixel de margen
        int filaMin = Integer.MAX_VALUE;
        int filaMax = Integer.MIN_VALUE;
        int columnaMin = Integer.MAX_VALUE;
        int columnaMax = Integer.MIN_VALUE;
        for (int[] pixel : pixeles) {
            filaMin = Math.min(filaMin, pixel[0]);
            filaMax = Math.max(filaMax, pixel[0]);
            columnaMin = Math.min(columnaMin, pixel[1]);
            columnaMax = Math.max(columnaMax, pixel[1]);
        }
        int ancho = columnaMax - columnaMin + 3;
        int alto = filaMax - filaMin + 3;
        boolean[][] mascara = new boolean[alto][ancho];
        for (int[] pixel : pixeles) {
            mascara[pixel[0] - filaMin + 1][pixel[1] - columnaMin + 1] = true;
        }

        // Pixel inicial: el primero que aparece recorriendo por filas
        int[] inicio = null;
        for (int y = 0; y < alto && inicio == null; y++) {
            for (int x = 0; x < ancho && inicio == null; x++) {
                if (mascara[y][x]) {
                    inicio = new int[]{x, y};
                }
            }
        }

        List<int[]> puntos = new ArrayList<>();
        puntos.add(new int[]{inicio[0] + columnaMin - 1, inicio[1] + filaMin - 1});

        // Como es el primero de su fila, su vecino del oeste es fondo: de ahi "venimos"
        int[] actual = inicio;
        int direccionAtras = 0;
        int[] segundo = null;
        while (true) {
            int[] siguiente = null;
            int direccionSiguiente = -1;
            for (int k = 1; k <= 8; k++) {
                int d = (direccionAtras + k) % 8;
                int x = actual[0] + DIRECCIONES[d][0];
                int y = actual[1] + DIRECCIONES[d][1];
                if (mascara[y][x]) {
                    siguiente = new int[]{x, y};
                    direccionSiguiente = d;
                    break;
                }
            }
            if (siguiente == null) {
                // pixel aislado, el contorno es solo el
                break;
            }
            if (segundo == null) {
                segundo = siguiente;
            } else if (mismoPunto(actual, inicio) && mismoPunto(siguiente, segundo)) {
                // se volvio a dar el primer paso: ya se dio la vuelta completa
                break;
            }
            // el ultimo vecino revisado antes de encontrar al siguiente era fondo;
            // se calcula en que direccion queda visto desde el nuevo pixel
            int[] fondo = {
                actual[0] + DIRECCIONES[(direccionSiguiente + 7) % 8][0],
                actual[1] + DIRECCIONES[(direccionSiguiente + 7) % 8][1]
            };
            direccionAtras = direccionHacia(siguiente, fondo);
            actual = siguiente;
            puntos.add(new int[]{actual[0] + columnaMin - 1, actual[1] + filaMin - 1});
        }
        // el inicio quedo repetido al final al cerrar la vuelta
        if (puntos.size() > 1 && mismoPunto(puntos.get(0), puntos.get(puntos.size() - 1))) {
            puntos.remove(puntos.size() - 1);
        }
        return new Contorno(puntos);
    }

    private static boolean mismoPunto(int[] a, int[] b) {
        return a[0] == b[0] && a[1] == b[1];
    }

    private static int direccionHacia(int[] desde, int[] hacia) {
        int dx = hacia[0] - desde[0];
        int dy = hacia[1] - desde[1];
        for (int d = 0; d < 8; d++) {
            if (DIRECCIONES[d][0] == dx && DIRECCIONES[d][1] == dy) {
                return d;
            }
        }
        throw new IllegalStateException("Los puntos no son vecinos");
    }

    public List<int[]> getPuntos() {
        return puntos;
    }

    public int tamano() {
        return puntos.size();
    }

    /** Area encerrada por el contorno (formula del zapato sobre los centros de los pixeles). */
    public double area() {
        long doble = 0;
        int n = puntos.size();
        for (int i = 0; i < n; i++) {
            int[] a = puntos.get(i);
            int[] b = puntos.get((i + 1) % n);
            doble += (long) a[0] * b[1] - (long) b[0] * a[1];
        }
        return Math.abs(doble) / 2.0;
    }

    /**
     * Cuantos pixeles tendria la figura si no tuviera huecos. Por el teorema de Pick,
     * un poligono con vertices en la cuadricula encierra area + borde/2 + 1 puntos,
     * contando los del borde. Aqui el borde son los pixeles del contorno.
     */
    public double pixelesSinHuecos() {
        return area() + puntos.size() / 2.0 + 1;
    }

    /**
     * Que tanto varia la distancia de los puntos del contorno a su centro,
     * como fraccion del radio promedio (desviacion estandar / promedio).
     * En un circulo es casi 0.
     */
    public double variacionDelRadio() {
        double centroX = 0;
        double centroY = 0;
        for (int[] p : puntos) {
            centroX += p[0];
            centroY += p[1];
        }
        centroX /= puntos.size();
        centroY /= puntos.size();
        double suma = 0;
        double sumaCuadrados = 0;
        for (int[] p : puntos) {
            double radio = Math.hypot(p[0] - centroX, p[1] - centroY);
            suma += radio;
            sumaCuadrados += radio * radio;
        }
        double promedio = suma / puntos.size();
        double varianza = sumaCuadrados / puntos.size() - promedio * promedio;
        return Math.sqrt(Math.max(0, varianza)) / promedio;
    }
}
