package modelo;

/**
 * Las cuatro categorias en las que se puede clasificar una figura.
 */
public enum Categoria {
    CUADRILATERO('C', "Cuadrilatero"),
    TRIANGULO('T', "Triangulo"),
    CIRCULO('O', "Circulo"),
    OTRO('X', "Otro");

    private final char letra;
    private final String nombre;

    Categoria(char letra, String nombre) {
        this.letra = letra;
        this.nombre = nombre;
    }

    public char getLetra() {
        return letra;
    }

    public String getNombre() {
        return nombre;
    }
}
