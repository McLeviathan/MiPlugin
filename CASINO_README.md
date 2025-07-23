# 🎰 Sistema de Casino - MiPlugin

## Descripción
Sistema de tragamonedas completamente funcional para Minecraft 1.8.8 con animaciones fluidas y sistema de recompensas.

## Características Implementadas

### ✅ Compatibilidad
- **Minecraft**: 1.8.8
- **Java**: 8
- **API**: Spigot/Bukkit 1.8.8
- **Sin dependencias externas**

### ✅ Funcionalidades
- **Comando `/casino`**: Abre la GUI de la tragamonedas
- **Animación de giro**: 2-3 segundos de duración con efectos visuales y sonoros
- **5 símbolos diferentes**: Diamante, Oro, Esmeralda, Hierro, Redstone
- **Sistema de recompensas**: XP + Pepitas de oro según el símbolo ganador
- **Efectos de sonido**: Sonidos durante la animación y al ganar/perder
- **Mensajes broadcast**: Anuncia las victorias a todos los jugadores

### ✅ Arquitectura
```
mp/casino/
├── CasinoCommand.java      # Comando principal /casino
├── SlotMachineManager.java # Lógica de la tragamonedas y animaciones
├── CasinoListener.java     # Manejo de eventos de inventario
└── RewardManager.java      # Sistema de recompensas
```

## Recompensas por Símbolo

| Símbolo | XP | Pepitas de Oro |
|---------|----|----|
| 💎 Diamante | 50 | 5 |
| 💚 Esmeralda | 40 | 4 |
| 🥇 Oro | 30 | 3 |
| ⚙ Hierro | 20 | 2 |
| 🔴 Redstone | 10 | 1 |

## Características Técnicas

### Prevención de Fugas de Memoria
- Las tareas de animación se cancelan automáticamente si el jugador cierra el inventario
- Verificación constante del estado del jugador durante la animación
- Limpieza automática de recursos

### Animación Fluida
- **Duración**: 40 ticks * 3 = 120 ticks del servidor (2 segundos)
- **Frecuencia**: Cada 3 ticks (0.15 segundos)
- **Efectos de sonido**: Progresivos durante la animación
- **Resultado predeterminado**: Se calcula al inicio para evitar manipulación

### Seguridad
- Validación de inventarios
- Prevención de clics durante la animación
- Manejo de errores y desconexiones
- Verificación de permisos (preparado para futuras expansiones)

## Uso

1. **Abrir casino**: `/casino`
2. **Girar**: Hacer clic en el botón verde "GIRAR"
3. **Esperar**: La animación dura 2-3 segundos
4. **Resultado**: Si los 3 símbolos coinciden, ¡ganas!

## Integración con el Plugin

El sistema está completamente integrado con:
- **Sistema de comandos** del plugin principal
- **Utilidades de mensajes** (MessageUtils)
- **Gestión de eventos** centralizada
- **Arquitectura modular** del proyecto

## Notas de Desarrollo

- **Patrón Observer**: Para eventos de inventario
- **Patrón Strategy**: Para diferentes tipos de recompensas
- **BukkitRunnable**: Para animaciones asíncronas
- **Encapsulación**: Cada clase tiene responsabilidades específicas
- **Compatibilidad**: Solo APIs de Bukkit 1.8.8, sin reflexión ni APIs modernas