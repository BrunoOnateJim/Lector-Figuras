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
        System.out.println("El color de fondo es: " + colorFond);

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
                        int filaActual = actual[0];
                        int columnaActual = actual[1];
                        if(filaActual - 1 >= 0){
                            if(pixeles[filaActual-1][columnaActual] == colorFigura && visitado[filaActual-1][columnaActual] == false){
                                cola.add(new int[]{filaActual-1, columnaActual});
                                visitado[filaActual-1][columnaActual] = true;
                            }
                        }
                        if(filaActual +1 < alto){
                            if(pixeles[filaActual+1][columnaActual] == colorFigura && visitado[filaActual + 1][columnaActual] == false){
                                cola.add(new int[]{filaActual+1, columnaActual});
                                visitado[filaActual+1][columnaActual] = true;
                            }
                        }
                        if(columnaActual - 1 >= 0){
                            if(pixeles[filaActual][columnaActual-1] == colorFigura && visitado[filaActual][columnaActual-1] == false){
                                cola.add(new int[]{filaActual, columnaActual - 1});
                                visitado[filaActual][columnaActual-1] = true;
                            }
                        }
                        if(columnaActual + 1 < ancho){
                            if(pixeles[filaActual][columnaActual+1] == colorFigura  && visitado[filaActual][columnaActual+1] == false){
                                cola.add(new int[]{filaActual, columnaActual+1});
                                visitado[filaActual][columnaActual+1] = true;
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
