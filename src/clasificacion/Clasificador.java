package clasificacion;

import java.util.ArrayList;
import java.util.List;
import modelo.Categoria;

/**
 * Clasifica una figura contando sus lados rectos con el metodo de rectas digitales.
 *
 * Pasos:
 * 1. Se sigue el contorno exterior de la figura pixel por pixel.
 * 2. Si la figura tiene menos pixeles de los que encierra su contorno, tiene
 *    huecos (un anillo, por ejemplo) y es X.
 * 3. Si el borde esta siempre a la misma distancia del centro, es circulo.
 * 4. El contorno se parte en segmentos rectos digitales.
 * 5. Se mide cuanto gira la direccion del borde en tramos cortos del contorno.
 *    Donde gira mucho en poco espacio hay una esquina; una curva gira poco a poco.
 * 6. Con 3 o 4 esquinas, se revisa que los lados entre ellas sean rectos
 *    (en una elipse las "esquinas" son puntas curvas y los lados no son rectos).
 * 7. 3 esquinas es triangulo, 4 es cuadrilatero. Cualquier otra cosa es X.
 */
public class Clasificador {
    /** Grosor permitido de una recta digital (1 = recta digital exacta). */
    private static final double GROSOR_RECTA = 1.5;
    /** Largo de la ventana donde se mide el giro, como fraccion del perimetro. */
    private static final double VENTANA_RELATIVA = 0.02;
    /** Largo minimo de la ventana en pixeles. */
    private static final double VENTANA_MINIMA_PIXELES = 4.0;
    /** Giro minimo, en grados, dentro de la ventana para contar una esquina. */
    private static final double GIRO_ESQUINA = 30;
    /** Distancia maxima (en pixeles) que se puede alejar un lado de la recta entre sus esquinas. */
    private static final double DESVIACION_LADO_PIXELES = 3.0;
    /** Lo mismo pero como fraccion del largo del lado, para lados grandes. */
    private static final double DESVIACION_LADO_RELATIVA = 0.04;
    /** Maxima variacion del radio que se permite a un circulo. */
    private static final double VARIACION_CIRCULO = 0.02;
    /** Fraccion de pixeles que puede faltar antes de decir que hay un hueco. */
    private static final double FALTANTE_HUECO = 0.02;

    public Categoria clasificar(List<int[]> pixeles) {
        Contorno contorno = Contorno.seguir(pixeles);
        if (contorno.tamano() < 4) {
            // es un punto o una rayita: no tiene forma
            return Categoria.OTRO;
        }

        double faltantes = contorno.pixelesSinHuecos() - pixeles.size();
        if (faltantes > FALTANTE_HUECO * contorno.pixelesSinHuecos()) {
            return Categoria.OTRO;
        }

        // Un circulo se digitaliza como un poligono de muchos lados cortos, asi que se
        // reconoce antes de contar esquinas: su borde esta siempre a la misma distancia del centro
        if (contorno.variacionDelRadio() < VARIACION_CIRCULO) {
            return Categoria.CIRCULO;
        }

        List<Integer> esquinas = buscarEsquinas(contorno);
        if (esquinas.size() != 3 && esquinas.size() != 4) {
            return Categoria.OTRO;
        }
        // las puntas de una elipse o de un semicirculo pueden parecer esquinas,
        // pero entonces los "lados" entre ellas son curvos
        if (!ladosRectos(contorno, esquinas)) {
            return Categoria.OTRO;
        }
        return esquinas.size() == 3 ? Categoria.TRIANGULO : Categoria.CUADRILATERO;
    }

    /**
     * Busca las esquinas del contorno y regresa su posicion en el.
     *
     * Para cada union entre dos segmentos se mide cuanto gira el borde dentro de una
     * ventana corta de contorno alrededor de ella (la direccion un poco despues menos
     * la direccion un poco antes). En un lado recto o en una curva suave el giro es
     * pequeno; en una esquina es grande. Las uniones con giro grande que estan a menos
     * de una ventana entre si son la misma esquina (por los pedacitos de segmento que
     * quedan en las puntas); de cada grupo se toma la union que mas gira.
     */
    public List<Integer> buscarEsquinas(Contorno contorno) {
        List<Integer> esquinas = new ArrayList<>();
        List<SegmentoDigital> segmentos = SegmentoDigital.descomponer(contorno.getPuntos(), GROSOR_RECTA);
        int n = segmentos.size();
        if (n < 2) {
            return esquinas;
        }

        // posicion (largo recorrido del contorno) donde termina cada segmento
        double[] finEn = new double[n];
        double recorrido = 0;
        for (int i = 0; i < n; i++) {
            recorrido += segmentos.get(i).largo();
            finEn[i] = recorrido;
        }
        double perimetro = recorrido;
        double ventana = Math.max(VENTANA_MINIMA_PIXELES, VENTANA_RELATIVA * perimetro);

        double[] giro = new double[n];
        int ultimaMarcada = -1;
        for (int i = 0; i < n; i++) {
            double antes = segmentoEn(segmentos, finEn, perimetro, finEn[i] - ventana / 2).angulo();
            double despues = segmentoEn(segmentos, finEn, perimetro, finEn[i] + ventana / 2).angulo();
            giro[i] = Math.abs(giroEnGrados(antes, despues));
            if (giro[i] >= GIRO_ESQUINA) {
                ultimaMarcada = i;
            }
        }
        if (ultimaMarcada == -1) {
            return esquinas;
        }

        // Se recorren las uniones marcadas; un hueco mayor a la ventana empieza una esquina nueva
        double posicionAnterior = finEn[ultimaMarcada] - perimetro;
        int mejorDelGrupo = -1;
        for (int i = 0; i < n; i++) {
            if (giro[i] < GIRO_ESQUINA) {
                continue;
            }
            if (finEn[i] - posicionAnterior > ventana) {
                if (mejorDelGrupo != -1) {
                    esquinas.add(segmentos.get(mejorDelGrupo).getIndiceFin());
                }
                mejorDelGrupo = i;
            } else if (mejorDelGrupo == -1 || giro[i] > giro[mejorDelGrupo]) {
                mejorDelGrupo = i;
            }
            posicionAnterior = finEn[i];
        }
        if (mejorDelGrupo != -1) {
            esquinas.add(segmentos.get(mejorDelGrupo).getIndiceFin());
        }
        // El primer grupo pudo haber empezado al final del contorno (da la vuelta):
        // si la ultima esquina quedo pegada a la primera, son la misma
        if (esquinas.size() > 1) {
            int primera = esquinas.get(0);
            int ultima = esquinas.get(esquinas.size() - 1);
            if (distanciaEnContorno(contorno, ultima, primera) <= ventana) {
                esquinas.remove(esquinas.size() - 1);
            }
        }
        return esquinas;
    }

    /**
     * Revisa que el contorno entre cada par de esquinas seguidas sea (casi) una recta:
     * ningun punto se puede alejar mucho de la recta que une a las dos esquinas.
     */
    private boolean ladosRectos(Contorno contorno, List<Integer> esquinas) {
        List<int[]> puntos = contorno.getPuntos();
        int total = puntos.size();
        for (int e = 0; e < esquinas.size(); e++) {
            int desde = esquinas.get(e);
            int hasta = esquinas.get((e + 1) % esquinas.size());
            int[] a = puntos.get(desde);
            int[] b = puntos.get(hasta);
            double largo = Math.hypot(b[0] - a[0], b[1] - a[1]);
            if (largo == 0) {
                return false;
            }
            double tolerancia = Math.max(DESVIACION_LADO_PIXELES, DESVIACION_LADO_RELATIVA * largo);
            for (int i = desde; i != hasta; i = (i + 1) % total) {
                int[] p = puntos.get(i);
                double distancia = Math.abs((b[0] - a[0]) * (p[1] - a[1]) - (b[1] - a[1]) * (p[0] - a[0])) / largo;
                if (distancia > tolerancia) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Cuantos pasos hay en el contorno para ir del punto 'desde' al punto 'hasta'. */
    private int distanciaEnContorno(Contorno contorno, int desde, int hasta) {
        return ((hasta - desde) % contorno.tamano() + contorno.tamano()) % contorno.tamano();
    }

    /** El segmento que contiene la posicion dada del contorno (la posicion da la vuelta). */
    private SegmentoDigital segmentoEn(List<SegmentoDigital> segmentos, double[] finEn, double perimetro, double posicion) {
        posicion = ((posicion % perimetro) + perimetro) % perimetro;
        for (int i = 0; i < finEn.length; i++) {
            if (posicion < finEn[i]) {
                return segmentos.get(i);
            }
        }
        return segmentos.get(segmentos.size() - 1);
    }

    /** Cuanto gira la direccion al pasar de un segmento al otro, entre -180 y 180 grados. */
    private double giroEnGrados(double anguloAntes, double anguloDespues) {
        double giro = Math.toDegrees(anguloDespues - anguloAntes);
        while (giro > 180) {
            giro -= 360;
        }
        while (giro < -180) {
            giro += 360;
        }
        return giro;
    }
}
