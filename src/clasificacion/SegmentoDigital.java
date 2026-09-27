package clasificacion;

import java.util.ArrayList;
import java.util.List;

/**
 * Un tramo del contorno que forma una recta digital: la forma en que se ve una
 * linea recta cuando se dibuja con pixeles (una escalera con escalones parejos).
 */
public class SegmentoDigital {
    private final int[] inicio;
    private final int[] fin;
    /** Posicion en el contorno del punto final del segmento. */
    private final int indiceFin;

    public SegmentoDigital(int[] inicio, int[] fin, int indiceFin) {
        this.inicio = inicio;
        this.fin = fin;
        this.indiceFin = indiceFin;
    }

    /**
     * Parte un contorno cerrado en segmentos digitales maximos: desde cada punto
     * se avanza lo mas que se pueda mientras el tramo siga siendo una recta digital,
     * y el siguiente segmento empieza donde termino el anterior.
     *
     * Como cualquier pedazo de una recta digital tambien es recta digital, el fin
     * mas lejano se encuentra con busqueda binaria en lugar de avanzar de uno en uno.
     *
     * @param grosor que tan "gruesa" puede ser la recta, en pixeles (menos de 1 es una
     *               recta digital exacta; un poco mas tolera las irregularidades del dibujo)
     */
    public static List<SegmentoDigital> descomponer(List<int[]> contorno, double grosor) {
        List<SegmentoDigital> segmentos = new ArrayList<>();
        int n = contorno.size();
        int inicio = 0;
        while (inicio < n) {
            // se duplica el paso hasta pasarse (o llegar al final del contorno)...
            int valido = inicio + 1;
            int paso = 1;
            int invalido = -1;
            while (invalido == -1) {
                int prueba = Math.min(inicio + 2 * paso, n);
                if (esRectaDigital(contorno, inicio, prueba, grosor)) {
                    valido = prueba;
                    if (prueba == n) {
                        break;
                    }
                    paso *= 2;
                } else {
                    invalido = prueba;
                }
            }
            // ...y luego se busca el limite exacto entre el ultimo valido y el primero invalido
            if (invalido != -1) {
                while (invalido - valido > 1) {
                    int medio = (valido + invalido) / 2;
                    if (esRectaDigital(contorno, inicio, medio, grosor)) {
                        valido = medio;
                    } else {
                        invalido = medio;
                    }
                }
            }
            segmentos.add(new SegmentoDigital(contorno.get(inicio), contorno.get(valido % n), valido % n));
            inicio = valido;
        }
        return segmentos;
    }

    /**
     * Revisa si los puntos del contorno entre 'desde' y 'hasta' (incluidos) forman
     * una recta digital.
     *
     * Definicion aritmetica (Reveilles): los puntos (x, y) de una recta digital de
     * pendiente b/a cumplen  c <= b*x - a*y < c + max(|a|, |b|)  para alguna c.
     * Es decir, todos caben en una franja de grosor menor a 1 pixel, medida sobre el
     * eje dominante (vertical si la recta es mas horizontal y al reves).
     *
     * Para encontrar la mejor inclinacion de la franja se usa la envolvente convexa
     * de los puntos: la franja mas delgada siempre esta apoyada en uno de sus lados
     * (asi se reconocen los "segmentos digitales difusos" de Debled-Rennesson).
     */
    private static boolean esRectaDigital(List<int[]> contorno, int desde, int hasta, double grosor) {
        int n = contorno.size();
        List<int[]> tramo = new ArrayList<>();
        for (int i = desde; i <= hasta; i++) {
            tramo.add(contorno.get(i % n));
        }
        return grosorDeFranja(envolventeConvexa(tramo)) < grosor;
    }

    /**
     * Grosor de la franja mas delgada que contiene a un poligono convexo, medido
     * sobre el eje dominante de cada lado.
     */
    private static double grosorDeFranja(List<int[]> envolvente) {
        int h = envolvente.size();
        if (h <= 2) {
            return 0;
        }
        double menor = Double.MAX_VALUE;
        for (int i = 0; i < h; i++) {
            int[] p = envolvente.get(i);
            int[] q = envolvente.get((i + 1) % h);
            long dx = q[0] - p[0];
            long dy = q[1] - p[1];
            long ejeDominante = Math.max(Math.abs(dx), Math.abs(dy));
            long mayorDistancia = 0;
            for (int[] v : envolvente) {
                mayorDistancia = Math.max(mayorDistancia, Math.abs(dx * (v[1] - p[1]) - dy * (v[0] - p[0])));
            }
            menor = Math.min(menor, (double) mayorDistancia / ejeDominante);
        }
        return menor;
    }

    /** Envolvente convexa con el algoritmo de cadena monotona (Andrew). */
    private static List<int[]> envolventeConvexa(List<int[]> puntos) {
        List<int[]> ordenados = new ArrayList<>(puntos);
        ordenados.sort((a, b) -> a[0] != b[0] ? Integer.compare(a[0], b[0]) : Integer.compare(a[1], b[1]));
        int n = ordenados.size();
        if (n < 3) {
            return ordenados;
        }
        int[][] envolvente = new int[2 * n][];
        int k = 0;
        for (int i = 0; i < n; i++) {
            while (k >= 2 && cruz(envolvente[k - 2], envolvente[k - 1], ordenados.get(i)) <= 0) {
                k--;
            }
            envolvente[k++] = ordenados.get(i);
        }
        int limite = k + 1;
        for (int i = n - 2; i >= 0; i--) {
            while (k >= limite && cruz(envolvente[k - 2], envolvente[k - 1], ordenados.get(i)) <= 0) {
                k--;
            }
            envolvente[k++] = ordenados.get(i);
        }
        List<int[]> resultado = new ArrayList<>();
        for (int i = 0; i < k - 1; i++) {
            resultado.add(envolvente[i]);
        }
        return resultado;
    }

    /** Producto cruz de (a -> b) y (a -> c). */
    private static long cruz(int[] a, int[] b, int[] c) {
        return (long) (b[0] - a[0]) * (c[1] - a[1]) - (long) (b[1] - a[1]) * (c[0] - a[0]);
    }

    public double largo() {
        return Math.hypot(fin[0] - inicio[0], fin[1] - inicio[1]);
    }

    /** Angulo de la direccion del segmento, en radianes. */
    public double angulo() {
        return Math.atan2(fin[1] - inicio[1], fin[0] - inicio[0]);
    }

    public int[] getInicio() {
        return inicio;
    }

    public int[] getFin() {
        return fin;
    }

    public int getIndiceFin() {
        return indiceFin;
    }
}
