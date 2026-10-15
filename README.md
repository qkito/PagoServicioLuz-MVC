# Pago de Servicio de Luz (CU-04) — Arquitectura MVC en Java

Examen Unidad 1 · Arquitectura de Software · ITSON
Alumno: Ramses López Murrieta (00000269854)

Aplicación de escritorio en **Java Swing** que implementa el caso de uso
**Pagar servicio de luz** con el patrón **Modelo–Vista–Controlador**,
coherente con el modelo de diseño en StarUML (`Examen_1_00000269854.mdj`).

## Estructura

```
src/main/java/pagoservicioluz/
├── PagoServicioLuzApp.java          # main: crea y conecta modelo, vista y controlador
├── modelo/
│   ├── IPagoServicioModelo.java     # contrato del modelo (desacopla al controlador)
│   ├── PagoServicioModelo.java      # datos en memoria + lógica del pago
│   ├── PasarelaPagos.java           # sistema de pagos externo (simulado)
│   ├── Cliente.java, Servicio.java, Consumo.java
│   ├── Tarjeta.java                 # validación: Luhn, vencimiento MM/AA, CVV
│   ├── Recibo.java
│   └── PagoException.java
├── vista/
│   └── FrmPagoServicio.java         # JFrame (txt*, lst*, btn*, pnl*)
└── controlador/
    └── PagoServicioControlador.java # atiende eventos de la vista y usa el modelo
```

## Cómo ejecutar

- **NetBeans / IntelliJ:** abrir la carpeta como proyecto Maven y ejecutar `PagoServicioLuzApp`.
- **Consola:** `mvn package` y luego `java -jar target/PagoServicioLuz-1.0.jar`.

Requiere JDK 17 o superior.

## Datos de prueba

| Número de servicio | Titular | Monto |
|---|---|---|
| 482019003521 | Ana López García | $684.00 |
| 482019008147 | José Martínez Ruiz | $512.30 |
| 482019009630 | María Pérez Soto | $948.75 |
| 573120004410 | Luis Herrera Campos | $298.40 |
| 573120007752 | Carmen Ruiz Valdez | $341.90 |

Escriba `482019` para ver tres coincidencias.

| Tarjeta | Vencimiento | CVV | Resultado |
|---|---|---|---|
| 4111111111111111 | 12/28 | 123 | Pago autorizado (recibo) |
| 4000000000000002 | 12/28 | 123 | Pago rechazado (FA-5) |
| 4111111111111112 | 12/28 | 123 | Número de tarjeta inválido (FA-4) |
| 4111111111111111 | 01/20 | 123 | Tarjeta vencida (FA-4) |

Si se vuelve a seleccionar un servicio ya pagado, se muestra "Este servicio no tiene adeudo" (FA-3).
