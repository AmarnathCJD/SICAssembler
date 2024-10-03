package com.amarnath.sicassembler

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amarnath.sicassembler.ui.theme.SICAssemblerTheme
import com.amarnath.sicassembler.ui.theme.fedFont
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import java.io.StringWriter

val shouldBeInDarkMode = mutableStateOf(false)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            if (isSystemInDarkTheme()) {
                shouldBeInDarkMode.value = true
            }
            SICAssemblerTheme(
                darkTheme = shouldBeInDarkMode
            ) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    println(innerPadding)
                    AssemblerScreen(innerPadding)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssemblerScreen(p: PaddingValues) {
    val srcCode = remember { mutableStateOf("") }
    val optabContent = remember { mutableStateOf("") }
    var symtab by remember { mutableStateOf("") }
    var intermediateFile by remember { mutableStateOf("") }
    var passValue by remember { mutableIntStateOf(2) }
    val errString = remember { mutableStateOf("Success!") }
    val objectCode = remember { mutableStateOf("") }

    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("0") }
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(Unit) {
        val (src, optab) = getFromLocalStorage(context)
        srcCode.value = src
        optabContent.value = optab
    }

    val contentResolver = LocalContext.current.contentResolver
    val textPicker = rememberLauncherForActivityResult(
        contract = GetCustomContents(isMultiple = true),
        onResult = { uris ->
            uris.forEach { uri ->
                val uriString = uri.toString()
                val fileDest = uriString.substringAfter("||")
                val uriPath = uriString.substringBefore("||")

                val uriPathURI = Uri.parse(uriPath)
                val inputStream = contentResolver.openInputStream(uriPathURI)
                val writer = StringWriter()
                inputStream?.bufferedReader()?.useLines { lines ->
                    lines.forEach {
                        writer.write(it)
                        writer.write("\n")
                    }
                }

                when (fileDest) {
                    "src" -> srcCode.value = writer.toString()
                    "optab" -> optabContent.value = writer.toString()
                }
            }
        })

    Column(
        modifier = Modifier
            .padding(p)
            .padding(bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "SIC Assembler",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 12.dp),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary,
                            letterSpacing = 1.2.sp,
                            fontFamily = fedFont,
                            fontSize = 26.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Column {
                        Text(
                            "Developed by @Amarnath",
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(bottom = 12.dp),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 0.8.sp,
                                fontFamily = fedFont
                            )
                        )
                        Text(
                            "The SIC assembler converts symbolic assembly code into machine code for the Simplified Instructional Computer (SIC) architecture, facilitating efficient instruction execution.",
                            modifier = Modifier.padding(bottom = 8.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.inverseSurface,
                                letterSpacing = 0.8.sp,
                                fontFamily = fedFont
                            )
                        )
                    }
                }
            }


            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(6.dp),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            passValue = 1
                        },
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (passValue == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "Pass 2",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = TextStyle(
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = fedFont
                                )
                            )
                            if (passValue == 1) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Image(
                                    painter = painterResource(id = R.drawable.check_circle_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                                    contentDescription = "Pass 1 Selected",
                                    modifier = Modifier.size(24.dp),
                                    colorFilter = ColorFilter.tint(
                                        MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            passValue = 2
                        },
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (passValue == 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "Pass 2",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = TextStyle(
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = fedFont
                                )
                            )
                            if (passValue == 2) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Image(
                                    painter = painterResource(id = R.drawable.check_circle_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                                    contentDescription = "Pass 2 Selected",
                                    modifier = Modifier.size(24.dp),
                                    colorFilter = ColorFilter.tint(
                                        MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { shouldBeInDarkMode.value = !shouldBeInDarkMode.value },
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        if (shouldBeInDarkMode.value) {
                            Image(
                                painter = painterResource(id = R.drawable.dark_mode_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                                contentDescription = "Dark Mode",
                                modifier = Modifier.size(24.dp),
                                colorFilter = ColorFilter.tint(
                                    MaterialTheme.colorScheme.onSurface
                                )
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.light_mode_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                                contentDescription = "Light Mode",
                                modifier = Modifier.size(24.dp),
                                colorFilter = ColorFilter.tint(
                                    MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            }

            ElevatedCard(
                modifier = Modifier
                    .padding(8.dp),
                shape = RoundedCornerShape(6.dp),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = {
                                expanded = !expanded
                            },
                        ) {
                            TextField(
                                value = if (selectedText == "0") "Select Preset" else "Selected > $selectedText",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .menuAnchor(
                                        MenuAnchorType.PrimaryEditable, true
                                    )
                                    .clip(RoundedCornerShape(12.dp))
                                    .padding(top = 2.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = TextFieldDefaults.colors(
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                textStyle = MaterialTheme.typography.bodyMedium
                                    .copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    ),
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                presets.forEachIndexed { i, item ->
                                    DropdownMenuItem(
                                        text = { Text(text = "Preset S$i") },
                                        onClick = {
                                            srcCode.value = item.srcCode
                                            optabContent.value = item.optabContent
                                            expanded = false
                                            selectedText = "S$i"
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            srcCode.value = ""
                            optabContent.value = ""
                            DynamicToast.makeSuccess(
                                context,
                                "Cleared the source code and optab",
                                Toast.LENGTH_SHORT
                            ).show()
                            selectedText = "0"
                            removeLocalStorage(context)
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .padding(vertical = 0.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                        ),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.cancel_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                            contentDescription = "Clear",
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.error)
                        )
                    }
                }
            }

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(6.dp),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp
                )
            ) {
                TextField(
                    value = srcCode.value,
                    onValueChange = { srcCode.value = it },
                    label = {
                        Text(
                            "Source Code",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    minLines = 5,
                    maxLines = 10,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        if (srcCode.value.isEmpty()) {
                            DynamicToast.makeWarning(
                                context,
                                "Source code is empty",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }
                        optabContent.value = generateOpCodeFromSourceCode(srcCode.value)
                        DynamicToast.makeSuccess(
                            context,
                            "Generated the optab",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    Text(
                        text = "Gen Op",
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontFamily = fedFont
                        )
                    )
                }
                Button(
                    onClick = {
                        saveSourceCodeToLocalStorage(context, srcCode.value)
                        DynamicToast.makeSuccess(
                            context,
                            "Saved the source code",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    Text(
                        text = "Save",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontFamily = fedFont
                        )
                    )
                }
                ShowFileChooser(
                    photoPicker = {
                        textPicker.launch("text/*||src")
                    },
                    text = "Source File",
                )
            }

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(6.dp),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp
                )
            ) {
                TextField(
                    value = optabContent.value,
                    onValueChange = { optabContent.value = it },
                    label = {
                        Text(
                            "OpTab",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    minLines = 4,
                    maxLines = 10,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        saveOpcodeToLocalStorage(context, optabContent.value)
                        DynamicToast.makeSuccess(
                            context,
                            "Saved the optab",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    Text(
                        text = "Save",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontFamily = fedFont
                        )
                    )
                }
                ShowFileChooser(
                    photoPicker = {
                        textPicker.launch("text/*||optab")
                    },
                    text = " OpTab File",
                )
            }

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(6.dp),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp
                )
            ) {
                Button(
                    onClick = {
                        try {
                            val result = processAssemblerPass1(srcCode.value, optabContent.value)
                            symtab = result.first
                            intermediateFile = result.second
                            errString.value = result.third.ifEmpty { "Success!" }
                            if (errString.value != "Success!") {
                                DynamicToast.makeError(
                                    context,
                                    errString.value,
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }
                        } catch (e: Exception) {
                            DynamicToast.makeError(
                                context,
                                "${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            errString.value = e.message ?: e.toString()
                            return@Button
                        }
                        if (passValue == 2 && errString.value == "Success!") {
                            val passCode =
                                generateObjectCode(intermediateFile, symtab, optabContent.value)
                            if (passCode.first.isNotEmpty()) {
                                objectCode.value = passCode.first
                            } else {
                                DynamicToast.makeError(
                                    context,
                                    passCode.second,
                                    Toast.LENGTH_SHORT
                                ).show()
                                errString.value = passCode.second
                                return@Button
                            }
                        }

                        DynamicToast.make(
                            context,
                            "Assembling completed!!!",
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    )
                ) {
                    Text(
                        "Assemble",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            fontFamily = fedFont
                        ),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        painter = painterResource(id = R.drawable.code_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                        contentDescription = "Dark Mode",
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(
                            MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(4.dp),
                        elevation = CardDefaults.cardElevation(6.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary)
                    ) {
                        BasicTextField(
                            value = errString.value,
                            onValueChange = {},
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (errString.value == "Success!") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(0.dp))
                                .background(MaterialTheme.colorScheme.onPrimary)
                                .padding(8.dp)
                                .align(Alignment.CenterHorizontally),
                            readOnly = true
                        )
                    }
                    if (passValue == 2 && objectCode.value.isNotEmpty()) {
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    "Object Code",
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
                                    color = MaterialTheme.colorScheme.secondary,
                                    style = TextStyle(
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = fedFont
                                    ),
                                )

                                val objectCodeLines = objectCode.value.split("\n")
                                Column {
                                    objectCodeLines.forEach { line ->
                                        val lineParts = line.split("\t")
                                        Row {
                                            lineParts.forEach { part ->
                                                Text(
                                                    text = part,
                                                    modifier = Modifier.padding(
                                                        horizontal = 8.dp,
                                                        vertical = 2.dp
                                                    ),
                                                    style = TextStyle(
                                                        fontSize = 14.sp,
                                                        fontFamily = FontFamily.Monospace,
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    color = MaterialTheme.colorScheme.onBackground
                                                )
                                            }
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Button(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(objectCode.value))
                                            DynamicToast.make(
                                                context, "Copied!",
                                            )
                                        },
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        ),
                                        modifier = Modifier.padding(horizontal = 0.dp),
                                        contentPadding = PaddingValues(4.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.content_copy_24dp_e8eaed_fill0_wght400_grad0_opsz24),
                                            contentDescription = "Copy",
                                            modifier = Modifier.size(24.dp),
                                            tint = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }
                            }
                        }
                    } else if (passValue == 2 && objectCode.value.isEmpty()) {
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Text(
                                "Object Code",
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
                                style = TextStyle(
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = fedFont
                                ),
                                color = MaterialTheme.colorScheme.secondary
                            )

                            BasicTextField(
                                value = "Object code will be generated after Pass 2",
                                onValueChange = {},
                                textStyle = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = fedFont,
                                    fontWeight = FontWeight.Light,
                                    color = MaterialTheme.colorScheme.onBackground
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .padding(8.dp),
                                readOnly = true
                            )
                        }
                    }

                    if (symtab.isNotEmpty()) {
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    "SymTab",
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
                                    style = TextStyle(
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = fedFont
                                    ),
                                    color = MaterialTheme.colorScheme.secondary
                                )

                                val symtabLines = symtab.split("\n")
                                Column {
                                    symtabLines.forEach { line ->
                                        val lineParts = line.split("\t")
                                        Row {
                                            lineParts.forEach { part ->
                                                Text(
                                                    text = fillSpace(part, 6),
                                                    modifier = Modifier.padding(horizontal = 8.dp),
                                                    style = TextStyle(
                                                        fontSize = 14.sp,
                                                        fontFamily = FontFamily.Monospace,
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    color = MaterialTheme.colorScheme.onBackground
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (intermediateFile.isEmpty()) {
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Text(
                                "Intermediate File",
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
                                style = TextStyle(
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = fedFont
                                ),
                                color = MaterialTheme.colorScheme.secondary
                            )

                            BasicTextField(
                                value = "Intermediate file will be generated after Pass 1",
                                onValueChange = {},
                                textStyle = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = fedFont,
                                    fontWeight = FontWeight.Light,
                                    color = MaterialTheme.colorScheme.onBackground
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .padding(8.dp),
                                readOnly = true
                            )
                        }
                    } else {
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    "Intermediate File",
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
                                    style = TextStyle(
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = fedFont
                                    ),
                                    color = MaterialTheme.colorScheme.secondary
                                )

                                val intermediateLines = intermediateFile.split("\n")
                                Column {
                                    intermediateLines.forEach { line ->
                                        val lineParts = line.split("\t")
                                        Row {
                                            lineParts.forEach { part ->
                                                Text(
                                                    text = fillSpace(part, 6),
                                                    modifier = Modifier.padding(horizontal = 8.dp),
                                                    style = TextStyle(
                                                        fontSize = 14.sp,
                                                        fontFamily = FontFamily.Monospace,
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    color = MaterialTheme.colorScheme.onBackground
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun fillSpace(s: String, n: Int): String {
    if (s.length >= n) return s
    return s + " ".repeat(n - s.length)
}