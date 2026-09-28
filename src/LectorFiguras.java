import clasificacion.Clasificador;
import io.LectorBMP;
import java.io.File;
import java.util.List;
import modelo.Categoria;
import procesamiento.Agrupador;

/**
 * Punto de entrada del programa: coordina la lectura de la imagen, la segmentacion
 * de figuras y su clasificacion, y imprime el reporte final.
 *
 * Uso: java -jar LectorFiguras.jar <ruta_de_la_imagen.bmp>
 */
public class LectorFiguras {

    public static void main(String[] args) {
        // 1. Validaciones de la entrada
        if (args.length != 1) {
            System.out.println("Error: se esperaba exactamente un argumento.");
            System.out.println("Uso: java -jar LectorFiguras.jar <ruta_de_la_imagen.bmp>");
            return;
        }
        String ruta = args[0];
        File archivo = new File(ruta);
        if (!archivo.exists() || !archivo.isFile()) {
            System.out.println("Error: no se encontro el archivo \"" + ruta + "\".");
            return;
        }
        if (!ruta.toLowerCase().endsWith(".bmp")) {
            System.out.println("Error: el archivo debe ser una imagen .bmp.");
            return;
        }

        try {
            // 2. Lectura de la imagen (Integrante 1)
            LectorBMP lector = new LectorBMP();
            lector.leerImagen(ruta);
            int[][] pixeles = lector.getPixeles();
            if (pixeles == null) {
                System.out.println("Error: no se pudo leer la imagen.");
                return;
            }

            // 2b. Segmentacion: separa cada figura del fondo (Integrante 1)
            Agrupador agrupador = new Agrupador();
            agrupador.agruparFiguras(pixeles, lector.getAncho(), lector.getAlto());
            List<List<int[]>> figuras = agrupador.getFigurasEncontradas();
            if (figuras.isEmpty()) {
                System.out.println("No se encontraron figuras en la imagen.");
                return;
            }

            // 3. Clasificacion (Integrante 2) y reporte final
            Clasificador clasificador = new Clasificador();
            for (List<int[]> figura : figuras) {
                int[] primerPixel = figura.get(0);                  // {fila, columna}
                int color = pixeles[primerPixel[0]][primerPixel[1]]; // las figuras son de color solido
                Categoria categoria = clasificador.clasificar(figura);
                System.out.println(categoria.getNombre() + " - Color: " + aHexadecimal(color));
            }
        } catch (Exception e) {
            System.out.println("Error inesperado al procesar la imagen: " + e.getMessage());
        }
    }

    /** Convierte un color entero 0xRRGGBB a texto hexadecimal. */
    static String aHexadecimal(int color) {
        return String.format("#%06X", color & 0xFFFFFF);
    }
}
