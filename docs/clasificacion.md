# Clasificación geométrica (rectas digitales)

Paquete `clasificacion/` y `modelo/Categoria`. Recibe cada figura tal como la entrega
`Agrupador.getFigurasEncontradas()` (lista de pixeles `{fila, columna}`) y regresa su
categoría: **C** cuadrilátero, **T** triángulo, **O** círculo, **X** otro.

```java
Clasificador clasificador = new Clasificador();
Categoria categoria = clasificador.clasificar(figura);   // figura: List<int[]>
categoria.getLetra();    // 'C', 'T', 'O' o 'X'
categoria.getNombre();   // "Cuadrilatero", "Triangulo", "Circulo", "Otro"
```

## Idea

Una figura con lados rectos, dibujada con pixeles, tiene un borde formado por
**rectas digitales**: escaleras con escalones parejos. Si partimos el borde en esas
rectas y contamos dónde cambia bruscamente de dirección, sabemos cuántas esquinas
(y por lo tanto cuántos lados) tiene. Todo se mide sobre el borde, así que no importa
la rotación, la posición ni el tamaño.

## Pasos

1. **Seguir el contorno** (`Contorno.seguir`). Algoritmo de vecindad de Moore: se empieza
   en el pixel más arriba a la izquierda y se gira alrededor de cada pixel del borde
   (en sentido horario) hasta encontrar el siguiente, hasta dar la vuelta completa.

2. **Detectar huecos** (`Contorno.pixelesSinHuecos`). Por el teorema de Pick, un polígono
   con vértices en la cuadrícula encierra `área + borde/2 + 1` puntos. Si la figura tiene
   menos pixeles que eso, tiene un hueco (un anillo, por ejemplo) → **X**.

3. **Círculo** (`Contorno.variacionDelRadio`). Se mide la distancia de cada punto del borde
   al centro. En un círculo casi no varía (menos de 2%); en un octágono varía 2.2% o más, en un
   cuadrado 11%. Se revisa antes que las esquinas porque un círculo pixelado parece un
   polígono de muchos lados cortos → **O**.

4. **Partir el borde en rectas digitales** (`SegmentoDigital.descomponer`). Desde un punto
   se avanza lo más posible mientras el tramo siga siendo una recta digital; ahí empieza
   el siguiente segmento.
   - Un tramo es recta digital si todos sus puntos caben en una franja de grosor menor a
     1 pixel (definición aritmética de Reveillès: `c <= b·x − a·y < c + max(|a|,|b|)`).
     Se permite hasta 1.5 pixeles para tolerar irregularidades
     (*segmentos digitales difusos*, Debled-Rennesson).
   - La franja más delgada siempre se apoya en un lado de la envolvente convexa del tramo,
     así que basta revisar esos lados.
   - Como un pedazo de recta digital también es recta digital, el final más lejano se
     busca con búsqueda binaria.

5. **Contar esquinas** (`Clasificador.buscarEsquinas`). En cada unión entre segmentos se
   mide cuánto gira la dirección del borde dentro de una ventana corta (2% del perímetro,
   mínimo 4 pixeles). Si gira 30° o más es esquina. Varias uniones pegadas (los pedacitos
   de segmento que quedan en las puntas) cuentan como una sola esquina.

6. **Lados rectos** (`Clasificador.ladosRectos`). Con 3 o 4 esquinas se revisa que el borde
   entre cada par de esquinas no se aleje más de 3 pixeles (o 4% del lado) de la recta que
   las une. Así, las puntas de una elipse o de un semicírculo no pasan por esquinas.

7. **Decisión**: 3 esquinas → **T**, 4 → **C**, cualquier otra cosa → **X**
   (pentágonos, hexágonos, estrellas, cruces, elipses, etc.).

## Constantes (en `Clasificador`)

| Constante | Valor | Para qué |
|---|---|---|
| `GROSOR_RECTA` | 1.5 px | grosor máximo de una recta digital |
| `VENTANA_RELATIVA` / `VENTANA_MINIMA_PIXELES` | 2% / 4 px | tramo donde se mide el giro |
| `GIRO_ESQUINA` | 30° | giro mínimo para que sea esquina |
| `DESVIACION_LADO_PIXELES` / `_RELATIVA` | 3 px / 4% | qué tan recto debe ser un lado |
| `VARIACION_CIRCULO` | 0.02 | variación máxima del radio de un círculo |
| `FALTANTE_HUECO` | 2% | pixeles faltantes para decir que hay hueco |

## Pruebas y límites

Se probaron 19 tipos de figura (cuadrado, rectángulo, rombo, trapecio, 4 tipos de
triángulo, cuadrilátero cóncavo, círculo, elipse, semicírculo, pentágono, hexágono,
octágono, estrella, cruz, L y anillo) en 7 tamaños y 24 rotaciones cada una.

- Figuras de **30 pixeles o más**: 97–100% de aciertos en todos los tipos.
- Figuras de **menos de ~25 pixeles**: hay errores. Con tan pocos pixeles un círculo de
  13 px es un octágono y los lados de una cruz miden 3 px, así que la forma ya no se
  distingue.
- Un cuadrilátero cóncavo (forma de flecha) se reporta como **C**, porque tiene 4 lados.
- Los ángulos interiores de más de 150° (giro menor a 30°) no se detectan como esquina.
