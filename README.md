# Infraestructura

### PARTE A
Utilizando el emulador Logic.ly, en su versión gratuita, se desarrollarán los siguientes circuitos.
1) Contador de 0 a 5 asincrónico, de uno en uno, ascendente, circular.
2) Decodificador con 8 entradas y 3 salidas. Código a elección.
3) Codificador que sea la inversa del anterior.
Contemplando las bases anteriores, cada grupo delineará a su elección el resto
de los detalles.
Se entregará un informe completo de lo realizado.
### PARTE B 

Se desarrollará, en Lenguaje Java, un simulador de un sistema operativo
multitarea multiusuario.
Sobre el tal sistema, ejecutarán un conjunto de programas, concurrentemente, y se deberá simular su ejecución compartiendo un recurso procesador, además del resto de los recursos.
Habrá un conjunto de recursos, los que deberán ser arbitrados, en cuanto a permisos, entre un conjunto de usuarios.

Concretamente:

- Habrá al menos 3 usuarios u1, u2, u3.
- Habrá al menos hasta 3 procesos concurrentes, p1, p2, p3.
- Habrá al menos 3 recursos, r1, r2 y r3.
- Cada proceso consistirá de la ejecución de un código, línea a línea, y
dicho código puede incluir la solicitud, devolución o uso de los recursos.
- Los recursos se accederán bajo mutua exclusión.

El simulador deberá simular:
- El reparto de procesador.
- La ejecución concurrente.
- La política de permisos con respecto a usuarios y recursos.
- El pedido y devolución de recursos.
- La existencia eventual de deadlocks.
- La política de schedulling.
- El alojamiento del código en memoria

Contemplando las bases anteriores, cada grupo delineará a su elección el resto de los detalles.

Respecto a la salida, podrá usarse una interfaz que muestre un estado de las cosas, o un log (basado en texto) de la ejecución.
Se entregará un informe completo de lo realizado, incluyendo código, pruebas, decisiones de diseño, conclusiones, etc.