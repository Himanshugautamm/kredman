package com.example.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WidgetConfigEntity
import com.example.data.model.WidgetSize
import com.example.data.model.WidgetStyle
import com.example.ui.theme.*

@Composable
fun WidgetContainer(
    widget: WidgetConfigEntity,
    isCustomizeMode: Boolean = false,
    onResizeToggle: () -> Unit = {},
    onStyleChange: (WidgetStyle) -> Unit = {},
    onToggleHide: () -> Unit = {},
    onMoveUp: () -> Unit = {},
    onMoveDown: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    var showStyleMenu by remember { mutableStateOf(false) }

    val heightDp = when (widget.size) {
        WidgetSize.SMALL -> 140.dp
        WidgetSize.MEDIUM -> 180.dp
        WidgetSize.LARGE -> 280.dp
    }

    val shape = when (widget.style) {
        WidgetStyle.ROUNDED -> RoundedCornerShape(24.dp)
        WidgetStyle.GLASS -> RoundedCornerShape(20.dp)
        WidgetStyle.LUMIA_TILE -> RoundedCornerShape(4.dp)
        WidgetStyle.SOLID -> RoundedCornerShape(18.dp)
        WidgetStyle.TRANSPARENT -> RoundedCornerShape(16.dp)
    }

    val modifierStyle = when (widget.style) {
        WidgetStyle.LUMIA_TILE -> Modifier.background(
            Brush.linearGradient(
                listOf(LumiaBlue.copy(alpha = 0.85f), LumiaPurple.copy(alpha = 0.95f))
            )
        )
        WidgetStyle.GLASS -> Modifier
            .background(GlassSurface)
            .border(1.dp, GlassBorder, shape)
        WidgetStyle.ROUNDED -> Modifier
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), shape)
        WidgetStyle.SOLID -> Modifier
            .background(NothingDarkCard)
            .border(1.dp, NothingDotGray, shape)
        WidgetStyle.TRANSPARENT -> Modifier
            .background(Color.Transparent)
            .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f), shape)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = heightDp)
            .animateContentSize()
            .padding(vertical = 6.dp),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(modifierStyle)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header row inside widget
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = widget.widgetType.defaultTitle.uppercase(),
                        fontSize = 11.sp,
                        letterSpacing = 1.2.sp,
                        color = if (widget.style == WidgetStyle.LUMIA_TILE) Color.White.copy(alpha = 0.9f)
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    if (isCustomizeMode) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(onClick = onMoveUp, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = "Up", tint = NothingRed, modifier = Modifier.size(16.dp))
                            }
                            IconButton(onClick = onMoveDown, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.ArrowDownward, contentDescription = "Down", tint = NothingRed, modifier = Modifier.size(16.dp))
                            }
                            IconButton(onClick = onResizeToggle, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.AspectRatio, contentDescription = "Resize", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            }
                            IconButton(onClick = { showStyleMenu = true }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Palette, contentDescription = "Style", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            }
                            IconButton(onClick = onToggleHide, modifier = Modifier.size(28.dp)) {
                                Icon(if (widget.isHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = "Hide", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            }

                            DropdownMenu(
                                expanded = showStyleMenu,
                                onDismissRequest = { showStyleMenu = false }
                            ) {
                                WidgetStyle.values().forEach { styleOption ->
                                    DropdownMenuItem(
                                        text = { Text(styleOption.name) },
                                        onClick = {
                                            onStyleChange(styleOption)
                                            showStyleMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Content slot
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    content()
                }
            }
        }
    }
}
