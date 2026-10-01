package procesamiento;
import java.util.HashMap;
import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
public class Agrupador {
    //te dejo estas dos variables para que las uses en lo que te toca Pauulino para que te ayuden para sacar la clasificacion 
    private int colorFond;
    private List<List<int[]>> figurasEncontradas = new ArrayList<>();
    
    public void agruparFiguras(int[][] pixeles, int ancho, int alto){
        HashMap<Integer, Integer> cantColores = new HashMap<>();
        for(int fila = 0 ; fila < alto; fila++){
            for(int columna = 0; columna < ancho; columna++){
                int colorActual = pixeles[fila][columna];
                if(cantColores.containsKey(colorActual)){
                    int contador = cantColores.get(colorActual);
                    cantColores.put(colorActual, contador + 1);
                } else {
                    cantColores.put(colorActual, 1);
                }
            }
        }
        int valorMax = 0;
        colorFond = 0;
        for(int color: cantColores.keySet()){
            int valorActual = cantColores.get(color);
            if(valorActual > valorMax){
                valorMax = valorActual;
                colorFond = color;
            }
        }

        boolean[][] visitado = new boolean[alto][ancho];
        for(int fila = 0; fila < alto; fila++){
            for(int columna = 0; columna < ancho; columna++){
                if(pixeles[fila][columna] != colorFond && visitado[fila][columna] == false){
                    int colorFigura = pixeles[fila][columna];
                    Queue<int[]> cola = new LinkedList<>();
                    List<int[]> pixelesDeEstaFigura = new ArrayList<>();
                    cola.add(new int[]{fila, columna});
                    visitado[fila][columna] = true;
                    while(cola.isEmpty() == false){
                        int[] actual = cola.poll();
                        pixelesDeEstaFigura.add(actual);
			for(int df = -1; df <= 1; df++){
                            for(int dc = -1; dc <= 1; dc++){
                                int nf = actual[0] + df;
                                int nc = actual[1] + dc;
                                if(nf >= 0 && nf < alto && nc >= 0 && nc < ancho
				   && !visitado[nf][nc] && pixeles[nf][nc] == colorFigura){
                                    visitado[nf][nc] = true;
                                    cola.add(new int[]{nf, nc});
                                }
                            }
                        }
                    }
                    figurasEncontradas.add(pixelesDeEstaFigura);
                }
            }
        }
    }
    
    public List<List<int[]>> getFigurasEncontradas() { 
        return figurasEncontradas; 
    }

    public int getColorFondo() { 
        return colorFond; 
    }
}
