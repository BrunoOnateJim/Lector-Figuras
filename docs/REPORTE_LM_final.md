# REPORTE_LM: Uso de modelos de lenguaje

**Equipo:** Cyberdowns\
**Integrantes:** Bruno Alonso Oñate Jimenez, Diego Avalos, Paulino
Perez, Santiago Morales Bofill\
**Herramienta:** Modelo de lenguaje\

## 1. Por qué decidimos usar este tipo de herramienta

Durante el proyecto utilizamos un modelo de lenguaje como
herramienta de apoyo en algunas tareas específicas. La mayor parte del
trabajo de programación, integración y pruebas fue realizada y revisada
por los integrantes del equipo.

El modelo se utilizó principalmente para facilitar tareas auxiliares y
de documentación, así como para proponer alternativas que posteriormente
fueron revisadas por nosotros. Entre los usos principales estuvieron la
elaboración del pseudocódigo y del diagrama de flujo, algunas
partes del archivo `build.xml`, la generación de un banco de imágenes
BMP para realizar pruebas, la búsqueda de alternativas para ciertos
procedimientos del código y algunas busquedas de información auxiliar
relacionadas con el proyecto.

No se tomó el código generado por el modelo como una solución
definitiva. Las respuestas por parte del modelo fueron revisadas dentro
del proyecto antes de conservarlas.

## 2. Resumen de usos

   1) Para que se utilizo el modelo
   
      1.1. Elaboracion del pseudocódigo
      1.2. Elabolarcion del diagrama de flujo
      1.3. Apoyo con `build.xml`
      1.4. Creacion del banco de imagenes
      1.5. Busquedas alternativas para la solución
      1.6. Informacion auxiliar
      1.7. Esctructura de los reportes.

   2) Que se aprobechó:
   
      2.1. La propuesta de los pasos pruncupales del algoritmo
      2.2. Propuestas en como compilar y ejecutar el codigo
      2.3. Generacion de imagenes con diferentes figuras, tamaños y colores
      2.4. Ideas de otros metodos para resolver el problema
      2.5. Explicaciones sobre .BMP, pruebas, funcionamiento de algunos procedimientos

   3) Qué se revisó o modificó:
   
      3.1. Se revisó que la elaboracion del pseudocódigo coincidiera con la implementacion real
      3.2. Se ajustaron algunos procesos para reflejar el funcionamiento del diagrama
      3.3. Se revisaron los resultados y las pruebas hechas
      3.4. Se comparo la respuesta al problema con otras alternativas
      

## 3. Detalle de los usos

### Uso 1: Pseudocódigo

**Para qué se utilizó:**\
Se pidió ayuda para convertir el funcionamiento general del programa en
una representación en pseudocódigo.

**Qué respondió el modelo:**\
Propuso una estructura con las etapas principales: validar la entrada,
leer la imagen BMP, obtener el color de fondo, agrupar los píxeles que
pertenecen a cada figura y clasificar cada figura.

**Qué se aprovechó:**\
La estructura sirvió como punto de partida para organizar el
pseudocódigo.

**Qué se revisó:**\
El equipo comparó la propuesta con el código realmente implementado y
modificó los pasos para que el pseudocódigo representara el
comportamiento real del programa.

El pseudocódigo final refleja procesos como la lectura del BMP, la
agrupación mediante vecinos y la clasificación de cada figura.

### Uso 2: Diagrama de flujo

**Para qué se utilizó:**\
Se utilizó el modelo para obtener una propuesta inicial del diagrama de
flujo del algoritmo.

**Qué se aprovechó:**\
La organización de las etapas principales del programa y las decisiones
que se realizan durante la clasificación.

**Qué se revisó o modificó:**\
El diagrama se revisó para que coincidiera con el comportamiento
implementado, se verificaron las decisiones relacionadas
con la detección de huecos, la identificación de círculos y el conteo de
esquinas.

El funcionamiento general representado en el diagrama coincide con la
descripción del algoritmo del reporte técnico: primero se agrupan las
figuras y después se analiza su contorno para clasificarlas.

### Uso 3: `build.xml`

**Para qué se utilizó:**\
Se utilizo para estructurar o revisar el archivo `build.xml`,
principalmente para facilitar la compilación, generación del `.jar` y
ejecución del programa con una imagen BMP como argumento.

**Qué se aprovechó:**\
La propuesta de tareas de Ant y la forma de pasar la ruta de la imagen
al programa.

**Qué se revisó o modificó:**\
El archivo se probó en el entorno del equipo y se realizaron los ajustes
necesarios para que las tareas correspondieran con la estructura del
proyecto.

El uso final de Apache Ant quedó documentado en el reporte técnico
mediante comandos como `ant jar` y `ant run -Dimg=ruta.bmp`.

### Uso 4: Banco de imágenes

**Para qué se utilizó:**\
Se utilizó para generar ejemplos de imágenes BMP que permitieran probar
el programa con diferentes figuras y casos límite.

**Qué se aprovechó:**\
La generación de imágenes de prueba y la organización de casos
esperados.

**Qué se revisó:**\
Las imágenes fueron utilizadas como banco de pruebas y sus resultados se
compararon con las salidas esperadas del programa. Se revisaron
especialmente casos como diferentes tamaños, rotaciones, fondos y
características particulares.

El proyecto terminó contando con un banco de 17 imágenes y resultados
esperados, utilizado para verificar el comportamiento del programa.

### Uso 5: Alternativas al código

**Para qué se utilizó:**\
Se consultó al modelo para conocer otras formas de resolver algunas
partes del problema, principalmente relacionadas con la clasificación de
las figuras.

**Qué se aprovechó:**\
Las propuestas sirvieron para comparar diferentes enfoques antes de
mantener la solución implementada.

**Qué se decidió:**\
Las alternativas no fueron incorporadas automáticamente. Se compararon
sus ventajas y limitaciones con el método utilizado en el proyecto. La
solución final se basa en el análisis del contorno, detección de huecos,
revisión del radio para círculos, segmentos de rectas digitales y conteo
de esquinas.

### Uso 6: Información auxiliar

Además de las tareas anteriores, se realizaron algunas consultas
puntuales para aclarar conceptos relacionados con el proyecto, por
ejemplo el funcionamiento de determinados casos de archivos BMP, formas
de realizar pruebas y posibles problemas que podían aparecer durante la
ejecución.

Estas consultas tuvieron un carácter auxiliar y no sustituyeron la
revisión del código ni las pruebas realizadas por el equipo.

### Uso 7: Estructura de los reportes

Como no sabiamos bien como hacer archivos.md en emacs, utilizamos el modelo
para mandarle la informacion hecha sin estructura y le pedimos que la organizara.

## 4. Revisión de las respuestas del modelo

Una parte importante del uso del modelo consistió en revisar sus
propuestas antes de incorporarlas al proyecto.

No todas las respuestas fueron tomadas como definitivas. Cuando una
propuesta no coincidía con el comportamiento deseado o con los
requisitos del proyecto, se modificó o se descartó.

Un ejemplo fue la revisión de los casos de prueba relacionados con
figuras pequeñas y píxeles conectados diagonalmente. El proyecto utiliza
finalmente vecindad de 8 direcciones para el agrupamiento, ya que la
vecindad de 4 podía producir una figura adicional en ciertos vértices
agudos.

## 5. Límites que establecimos para el uso del modelo

El modelo se utilizó como herramienta de apoyo y no como sustituto del
trabajo del equipo. Toda informacion recibida por el modelo fue revisada
y ajustada para el proyecto  y no toda respuesta del modelo fue aceptada.

------------------------------------------------------------------------