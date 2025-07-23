# 🐰 Minijuego del Conejo - MiPlugin

## Descripción
Minijuego de persecución donde el jugador debe golpear a un conejo que se teletransporta cada segundo dentro de un área de 10x10 bloques.

## Características Implementadas

### ✅ Compatibilidad Total
- **Minecraft**: 1.8.8
- **Java**: 8
- **API**: Spigot/Bukkit 1.8.8
- **Sin dependencias externas**
- **Sin APIs modernas**: Solo BukkitRunnable, EntityDamageByEntityEvent, Location, etc.

### ✅ Funcionalidades Principales

#### **Comando `/conejo`**
- Inicia el minijuego para el jugador
- Comando de ayuda: `/conejo help` o `/conejo ayuda`
- Solo disponible para jugadores (no consola)

#### **Mecánicas del Juego**
- **Duración**: 10 segundos exactos
- **Área de juego**: 10x10 bloques alrededor del jugador
- **Objetivo**: Golpear al conejo antes de que termine el tiempo
- **Teletransporte**: El conejo se mueve cada segundo (20 ticks)

#### **Sistema de Posicionamiento Inteligente**
- **Búsqueda de área segura**: Hasta 20 intentos para encontrar terreno adecuado
- **Uso de `world.getHighestBlockYAt()`**: Para posicionamiento automático
- **Validación de seguridad**: Verifica bloques sólidos y espacios libres
- **Fallback**: Si no encuentra área segura, usa la posición actual del jugador

### ✅ Arquitectura Técnica

#### **Estructura Modular**
```
mp/game/RabbitChaseGame.java     # Lógica principal del juego
mp/commands/RabbitChaseCommand.java  # Comando /conejo
mp/listeners/RabbitChaseListener.java # Eventos de golpe y desconexión
```

#### **Prevención de Memory Leaks**
- **Cancelación automática**: BukkitRunnable se cancela si el jugador se desconecta
- **Limpieza en onDisable**: Método `cleanup()` elimina todas las sesiones activas
- **Verificaciones constantes**: Estado del jugador y entidad verificados cada tick
- **Eliminación de entidades**: Conejos removidos automáticamente al finalizar

#### **Gestión de Sesiones**
- **Map de sesiones activas**: `Map<UUID, GameSession>` para tracking por jugador
- **Una sesión por jugador**: Previene múltiples juegos simultáneos
- **Clase interna GameSession**: Encapsula toda la lógica de una partida individual

### ✅ Experiencia de Usuario

#### **Flujo del Juego**
1. **Inicio**: `/conejo` → Búsqueda de área segura → Teletransporte
2. **Spawn del conejo**: Entidad con nombre visible "¡Atrápame!"
3. **Animación**: Conejo se teletransporta cada segundo con efectos de sonido
4. **Cuenta regresiva**: Últimos 5 segundos mostrados al jugador
5. **Finalización**: Victoria o derrota con efectos correspondientes

#### **Efectos Audiovisuales**
- **Sonidos de inicio**: NOTE_PLING al comenzar
- **Teletransporte**: ENDERMAN_TELEPORT cada movimiento del conejo
- **Cuenta regresiva**: NOTE_STICKS en los últimos 5 segundos
- **Victoria**: LEVEL_UP + mensaje broadcast
- **Derrota**: NOTE_BASS + mensaje de ánimo

#### **Sistema de Recompensas**
- **Victoria**: 25 XP + mensaje broadcast a todos los jugadores
- **Derrota**: Mensaje motivacional para intentar de nuevo

### ✅ Características de Seguridad

#### **Validaciones Robustas**
- **Estado del jugador**: Verificación constante de conexión
- **Estado del conejo**: Verificación de vida y validez de la entidad
- **Área de juego**: Validación de terreno seguro antes del spawn
- **Prevención de spam**: Un juego por jugador a la vez

#### **Manejo de Eventos**
- **EntityDamageByEntityEvent**: Detecta golpes al conejo específico del juego
- **PlayerQuitEvent**: Limpia automáticamente el juego del jugador desconectado
- **Cancelación de daño**: Evita que el conejo muera por daño normal

#### **Limpieza Automática**
- **Al finalizar el juego**: Conejo eliminado, tareas canceladas, sesión removida
- **Al deshabilitar plugin**: Todas las sesiones activas limpiadas
- **Al desconectarse**: Juego del jugador terminado automáticamente

### ✅ Integración con el Plugin

#### **Registro Correcto**
- **Comando registrado** en `plugin.yml` y `MiPlugin.java`
- **Listener registrado** sin duplicados
- **Información en ayuda** agregada al comando principal
- **Mensaje de inicio** actualizado en consola

#### **Compatibilidad con Sistema Existente**
- **Sigue patrones establecidos**: Misma estructura que otros minijuegos
- **Usa utilidades existentes**: MessageUtils para mensajes coloreados
- **Integración con ayuda**: Información en `/miPlugin` help

## Uso del Minijuego

### **Comandos Disponibles**
- `/conejo` - Inicia el minijuego
- `/conejo help` - Muestra ayuda detallada
- `/conejo ayuda` - Alias en español para ayuda

### **Reglas del Juego**
1. El jugador es teletransportado a un área segura cercana
2. Aparece un conejo con nombre "¡Atrápame!"
3. El conejo se teletransporta cada segundo dentro del área de 10x10
4. El jugador tiene 10 segundos para golpearlo
5. Si lo golpea: gana 25 XP y se anuncia la victoria
6. Si no lo golpea: el conejo escapa y puede intentar de nuevo

### **Estrategias**
- **Posicionamiento**: Mantenerse en el centro del área
- **Predicción**: Anticipar las posiciones del conejo
- **Velocidad**: Reaccionar rápido a cada teletransporte
- **Paciencia**: El conejo aparece en posiciones aleatorias

## Notas Técnicas

### **Compatibilidad Garantizada**
- Solo usa APIs disponibles en Spigot 1.8.8
- Compilación con Java 8
- Sin reflexión ni APIs modernas
- Materiales y sonidos compatibles con la versión

### **Rendimiento Optimizado**
- Tareas ejecutadas cada 20 ticks (1 segundo)
- Verificaciones mínimas necesarias
- Limpieza inmediata de recursos
- Sin operaciones costosas en el hilo principal

### **Mantenibilidad**
- Código modular y bien documentado
- Separación clara de responsabilidades
- Nombres descriptivos y métodos pequeños
- Fácil extensión para futuras mejoras