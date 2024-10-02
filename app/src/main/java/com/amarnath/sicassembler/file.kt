package com.amarnath.sicassembler

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import java.io.File

val activeFileDestination = mutableStateOf("")

class GetCustomContents(
    private val isMultiple: Boolean = false,
) : ActivityResultContract<String, List<@JvmSuppressWildcards Uri>>() {

    override fun createIntent(context: Context, input: String): Intent {
        val (inputMime, fileDest) = input.split("||")

        Log.d("GetCustomContents", "inputMime: $inputMime fileDest: $fileDest")
        activeFileDestination.value = fileDest

        return Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = inputMime
            putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, isMultiple)
                .addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): List<Uri> {
        return intent.takeIf {
            resultCode == Activity.RESULT_OK
        }?.getClipDataUris(intent) ?: emptyList()
    }

    internal companion object {
        internal fun Intent.getClipDataUris(intent: Intent?): List<Uri> {
            val resultSet = LinkedHashSet<Uri>()
            data?.let { data ->
                resultSet.add(data)
            }
            val clipData = clipData
            if (clipData == null && resultSet.isEmpty()) {
                return emptyList()
            } else if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    if (uri != null) {
                        resultSet.add(uri)
                    }
                }
            }

            for (uri in resultSet) {
                val dest = activeFileDestination.value
                val newUri = Uri.parse("$uri||$dest")
                resultSet.remove(uri)
                resultSet.add(newUri)
            }

            return ArrayList(resultSet)
        }
    }
}

@Composable
fun ShowFileChooser(
    photoPicker: () -> Unit, text: String
) {
    Button(
        onClick = { photoPicker() },
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
        ),
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.file_present_24dp_e8eaed_fill0_wght400_grad0_opsz24),
            contentDescription = "Dark Mode",
            modifier = Modifier.size(24.dp),
            colorFilter = ColorFilter.tint(
                MaterialTheme.colorScheme.onTertiary
            )
        )
    }
}

fun saveOpcodeToLocalStorage(context: Context, opCode: String) {
    val file2 = File(context.filesDir, "opCode.txt")
    file2.writeText(opCode)
}

fun saveSourceCodeToLocalStorage(context: Context, sourceCode: String) {
    val file = File(context.filesDir, "sourceCode.txt")
    file.writeText(sourceCode)
}

fun removeLocalStorage(context: Context) {
    val file = File(context.filesDir, "sourceCode.txt")
    file.delete()
    val file2 = File(context.filesDir, "opCode.txt")
    file2.delete()
}

fun getFromLocalStorage(context: Context): Pair<String, String> {
    var sourceCode = ""
    var opCode = ""
    try {
        val file = File(context.filesDir, "sourceCode.txt")
        sourceCode = file.readText()
    } catch (_: Exception) {
    }
    try {
        val file2 = File(context.filesDir, "opCode.txt")
        opCode = file2.readText()
    } catch (_: Exception) {
    }

    return Pair(sourceCode, opCode)
}
