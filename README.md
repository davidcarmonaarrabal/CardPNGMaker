# 🖼️ CardPNG Maker - Procesador de Imágenes en ZIP

Aplicación de escritorio desarrollada en **JavaFX** que permite procesar imágenes dentro de un archivo `.zip`, eliminando esquinas blancas automáticamente y generando un nuevo archivo con las imágenes corregidas en formato `.png`.

---

## 🚀 ¿Qué hace esta app?

1. 📦 Seleccionas un archivo `.zip` que contenga imágenes (`.jpg`, `.jpeg` o `.png`).
2. 🎯 Define el **margen** en píxeles donde se buscarán esquinas blancas.
3. 🎨 Ajusta el **umbral RGB** para decidir qué se considera "blanco".
4. 🧼 La app procesa cada imagen y convierte las esquinas blancas en **transparente**.
5. 📁 Se genera un nuevo archivo `.zip` con todas las imágenes convertidas a `.png`.

---

## 🛠️ Requisitos

- Java 17 o superior
- JavaFX (si lo corres fuera de un entorno IDE que ya lo incluya)

---

## 📸 Interfaz

- Campo para elegir margen (por defecto: `20 px`)
- Campos para umbral RGB (por defecto: `180 180 180`)
- Barra de progreso con porcentaje
- Botón grande y visual para seleccionar el archivo ZIP
- Alertas automáticas al terminar o si ocurre algún error

---