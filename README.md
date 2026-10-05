Proyecto 1: Reconocimiento de Figuras
Equipo: Cyberdowns
Integrantes: Bruno Alonso Oñate Jimenez, Diego Avalos, Paulino Perez, Santiago Morales Bofill

¿Qué hace?
-Dada una imagen .bmp con una o más figuras geométricas de color sólido sobre un fondo uniforme, el programa identifica cada figura y reporta su categoría y su color en hexadecimal:
  1) C (Cuadrilatero): cuadrados, rectángulos, rombos, trapezoides.
  2) T (Triangulo): equilateros, isósceles, escalenos, rectángulos.
  3) O (Circulo): círculos.
  4) X (Otros): cualquier otra figura.

Requisitos
-Java 21(JDK, no solo JRE).
-Apache Ant 1.10 en adelante.

Como compilar y ejecutar:
 1. Compilar: "ant clean jar" en terminal
 2. Ejecutar: "java -jar build/jar/LectorFiguras.jar ruta.bmp"
 ejemplo: java -jar build/jar/LectorFiguras.jar tests/banco_imagenes/10_mezcla_fondo_oscuro.bmp

  Si la imagen solo tiene fondo, el programa responde No se encontraron figuras en la imagen.

Entradas no válidas

  El programa avisa con un mensaje de error claro cuando:

  1. No se le pasa exactamente un argumento (y muestra cómo usarlo).
  2. El archivo no existe o no termina en .bmp.
  3. El archivo no es un BMP válido, está incompleto o está corrupto.
  4. El BMP no es de 24 o 32 bits sin compresión.

Formato de imagen soportado

	BMP sin compresión de 24 o 32 bits, con las filas en cualquier orientación (de abajo hacia arriba, que es lo normal, o	de arriba hacia abajo) y con cualquier ancho. Se supone que:

 1. El fondo es de un solo color y es el color más frecuente de la imagen,
 2. Cada figura tiene un color sólido, distinto del fondo y de las demás,
 3. O hay anti-aliasing ni colores intermedios en los bordes,
 4. Las figuras no se traslapan.
