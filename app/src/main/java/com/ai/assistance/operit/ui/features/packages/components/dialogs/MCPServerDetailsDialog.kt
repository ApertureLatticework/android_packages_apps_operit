package com.ai.assistance.operit.ui.features.packages.components.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ai.assistance.operit.ui.features.packages.components.dialogs.actions.MCPServerDetailsActions
import com.ai.assistance.operit.ui.features.packages.components.dialogs.content.MCPServerDetailsContent
import com.ai.assistance.operit.ui.features.packages.components.dialogs.header.MCPServerDetailsHeader
import com.ai.assistance.operit.data.mcp.MCPLocalServer

/**
 * A dialog that displays detailed information about a remote MCP server.
 *
 * @param server The MCP server to display details for
 * @param onDismiss Callback to be invoked when the dialog is dismissed
 * @param onUninstall Callback to be invoked when the remove button is clicked
 * @param mdFontSize Markdown内容的字体大小
 */
@Composable
fun MCPServerDetailsDialog(
        server: MCPLocalServer.PluginMetadata,
        onDismiss: () -> Unit,
        onUninstall: (MCPLocalServer.PluginMetadata) -> Unit,
        mdFontSize: Float = 14f
) {
    val isInstalled = server.isInstalled
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Dialog(onDismissRequest = onDismiss) {
        Surface(
                modifier =
                        Modifier.fillMaxWidth(0.95f) // Take 95% of the screen width
                                .fillMaxHeight(
                                        0.7f
                                ) // Take 70% of the screen height (reduced from 0.85f)
                                .heightIn(
                                        min = 400.dp,
                                        max = screenHeight * 0.7f // Reduced maximum height
                                ) // Responsive height
                                .padding(vertical = 8.dp), // Reduced vertical padding
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header (Logo, title, badges, etc.)
                    MCPServerDetailsHeader(server = server, onDismiss = onDismiss)

                    // Content - Note the paddingBottom to make room for actions
                    Box(
                            modifier =
                                    Modifier.fillMaxWidth()
                                            .weight(1f)
                                            .padding(bottom = 56.dp) // Make space for actions
                    ) {
                        MCPServerDetailsContent(
                                server = server,
                                modifier = Modifier.fillMaxSize(), // Fill the available space
                                mdFontSize = mdFontSize.sp // Pass the font size parameter
                        )
                    }
                }

                // Bottom action buttons - Fixed at bottom
                Surface(
                        modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                        tonalElevation = 3.dp, // Slightly elevated
                        shadowElevation = 4.dp // Add shadow for visual separation
                ) {
                    MCPServerDetailsActions(
                            server = server,
                            isInstalled = isInstalled,
                            onUninstall = onUninstall
                    )
                }
            }
        }
    }
}
