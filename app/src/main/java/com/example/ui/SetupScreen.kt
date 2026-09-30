package com.example.ui
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MetroCertViewModel
import com.example.data.RulesConfig
import com.example.ui.theme.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import com.example.data.InstrumentCatalogItem
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.ui.Alignment

@Composable
fun SetupScreen(viewModel: MetroCertViewModel, onNext: () -> Unit) {
    val report by viewModel.currentReport.collectAsState()
    val catalog by viewModel.instrumentCatalog.collectAsStateWithLifecycle()
    
    var manufacturer by remember { mutableStateOf(report.manufacturer) }
    var model by remember { mutableStateOf(report.modelNumber) }
    var serial by remember { mutableStateOf(report.serialNumber) }
    var certNo by remember { mutableStateOf(report.certificateNo) }
    var accuracyClass by remember { mutableStateOf(report.accuracyClass) }
    var isInService by remember { mutableStateOf(report.isInService) }
    var minCapacity by remember { mutableStateOf(report.minCapacity.toString().let { if (it == "0.0") "" else it }) }
    var maxCapacity by remember { mutableStateOf(report.maxCapacity.toString().let { if (it == "0.0") "" else it }) }
    var e by remember { mutableStateOf(report.e.toString().let { if (it == "0.0") "" else it }) }
    
    var temp by remember { mutableStateOf(report.ambientTemp.toString()) }
    var humidity by remember { mutableStateOf(report.relativeHumidity.toString()) }
    var pressure by remember { mutableStateOf(report.atmosphericPressure.toString()) }
    var standardId by remember { mutableStateOf(report.standardWeightId) }
    
    var nValidationError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(report) {
        if (report.manufacturer.isNotEmpty()) manufacturer = report.manufacturer
        if (report.modelNumber.isNotEmpty()) model = report.modelNumber
        if (report.serialNumber.isNotEmpty()) serial = report.serialNumber
        if (report.certificateNo.isNotEmpty()) certNo = report.certificateNo
        if (report.accuracyClass.isNotEmpty()) accuracyClass = report.accuracyClass
        isInService = report.isInService
        if (report.minCapacity > 0.0) minCapacity = if (report.minCapacity == report.minCapacity.toLong().toDouble()) report.minCapacity.toLong().toString() else report.minCapacity.toString()
        if (report.maxCapacity > 0.0) maxCapacity = if (report.maxCapacity == report.maxCapacity.toLong().toDouble()) report.maxCapacity.toLong().toString() else report.maxCapacity.toString()
        if (report.e > 0.0) e = if (report.e == report.e.toLong().toDouble()) report.e.toLong().toString() else report.e.toString()
        if (report.ambientTemp > 0.0) temp = report.ambientTemp.toString()
        if (report.relativeHumidity > 0.0) humidity = report.relativeHumidity.toString()
        if (report.atmosphericPressure > 0.0) pressure = report.atmosphericPressure.toString()
        if (report.standardWeightId.isNotEmpty()) standardId = report.standardWeightId
    }

    fun applyDemoData() {
        manufacturer = "Mettler Toledo"
        model = "ICS689 Precision"
        serial = "MT-2026-XPR984"
        certNo = "CERT-OIML-2026-088"
        accuracyClass = "III"
        minCapacity = "0.1"
        maxCapacity = "30"
        e = "0.005"
        temp = "20.0"
        humidity = "50.0"
        pressure = "1013.25"
        standardId = "OIML-E2-STD-2026"
        nValidationError = null
        viewModel.autoPopulateDemoSetup()
        viewModel.autoPopulateDemoTests()
    }

    val textFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = NeoBackground,
        unfocusedContainerColor = NeoBackground,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        focusedTextColor = TextDark,
        unfocusedTextColor = TextDark,
        focusedLabelColor = NeoAccent,
        unfocusedLabelColor = TextMuted
    )
    val fieldShape = RoundedCornerShape(16.dp)
    
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        
        Text("Manual Details Entry", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextDark)
        Spacer(modifier = Modifier.height(24.dp))
        
        NeoTextField(value = manufacturer, onValueChange = { manufacturer = it }, label = "Manufacturer Name", modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        NeoTextField(value = model, onValueChange = { model = it }, label = "Model Number", modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        NeoTextField(value = serial, onValueChange = { serial = it }, label = "Serial Number", modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        NeoTextField(value = certNo, onValueChange = { certNo = it }, label = "Certificate No.", modifier = Modifier.fillMaxWidth())
        
        Spacer(modifier = Modifier.height(16.dp))
        SetupDropdownField(value = accuracyClass, onValueChange = { accuracyClass = it }, label = "Accuracy Class", options = listOf("I", "II", "III", "IIII", "N/A (non-verified)"), modifier = Modifier.fillMaxWidth(), shape = fieldShape, colors = textFieldColors)
        
        val classInfo = when (accuracyClass) {
            "I" -> "Class I (Special): n >= 50,000 | e >= 1 mg"
            "II" -> "Class II (Fine): 100 <= n <= 100,000 | e >= 1 mg"
            "III" -> "Class III (Medium): 100 <= n <= 10,000 | e >= 0.1 g"
            "IIII" -> "Class IIII (Ordinary): 100 <= n <= 1,000 | e >= 5 g"
            else -> ""
        }
        
        if (classInfo.isNotEmpty()) {
            Text(classInfo, color = NeoAccent, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            NeoTextField(value = maxCapacity, onValueChange = { maxCapacity = it }, label = "Max (kg)", modifier = Modifier.weight(1f))
            NeoTextField(value = minCapacity, onValueChange = { minCapacity = it }, label = "Min (kg)", modifier = Modifier.weight(1f))
            NeoTextField(value = e, onValueChange = { e = it }, label = "e (kg)", modifier = Modifier.weight(1f))
        }
        
        if (nValidationError != null) {
            Text(nValidationError!!, color = FailText, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text("Environmental Conditions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextDark)
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            NeoTextField(value = temp, onValueChange = { temp = it }, label = "Temp (°C)", modifier = Modifier.weight(1f))
            NeoTextField(value = humidity, onValueChange = { humidity = it }, label = "Hum (%)", modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        NeoTextField(value = pressure, onValueChange = { pressure = it }, label = "Pressure (hPa)", modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(32.dp))
        Text("Reference Standards", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextDark)
        Spacer(modifier = Modifier.height(16.dp))
        NeoTextField(value = standardId, onValueChange = { standardId = it }, label = "Standard Weight ID / Set", modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = {
                val eVal = e.toDoubleOrNull() ?: 0.0
                val maxVal = maxCapacity.toDoubleOrNull() ?: 0.0
                
                var isValid = true
                if (eVal > 0 && maxVal > 0 && accuracyClass in RulesConfig.rules.keys) {
                    val n = maxVal / eVal
                    val rules = RulesConfig.rules[accuracyClass]
                    if (rules != null && (n < rules.nRange.min || n > rules.nRange.max)) {
                        nValidationError = "Validation Error: 'n' ($n) is out of bounds for Class $accuracyClass."
                        isValid = false
                    } else {
                        nValidationError = null
                    }
                }
                
                if (isValid) {
                    viewModel.updateInstrumentDetails(
                        manufacturer = manufacturer,
                        model = model,
                        serial = serial,
                        certNo = certNo,
                        accuracyClass = accuracyClass,
                        minCapacity = minCapacity.toDoubleOrNull() ?: 0.0,
                        maxCapacity = maxVal,
                        e = eVal,
                        isInService = isInService
                    )
                    viewModel.updateEnvironmentalConditions(
                        temp = temp.toDoubleOrNull() ?: 20.0,
                        humidity = humidity.toDoubleOrNull() ?: 50.0,
                        pressure = pressure.toDoubleOrNull() ?: 1013.25,
                        standardId = standardId
                    )
                    onNext()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp).neoShadow(cornerRadius = 28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeoAccent),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("Proceed to Testing", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(
            onClick = { applyDemoData() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NeoAccent, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Auto-fill Demo Data", color = NeoAccent, fontWeight = FontWeight.SemiBold)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun NeoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = if (label.isNotEmpty()) { { Text(label) } } else null,
        modifier = modifier.neoShadow(cornerRadius = 16.dp, isPressed = true),
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = NeoBackground,
            unfocusedContainerColor = NeoBackground,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = TextDark,
            unfocusedTextColor = TextDark,
            focusedLabelColor = NeoAccent,
            unfocusedLabelColor = TextMuted
        ),
        textStyle = LocalTextStyle.current.copy(fontSize = androidx.compose.ui.unit.TextUnit(16f, androidx.compose.ui.unit.TextUnitType.Sp)),
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupDropdownField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    options: List<String>,
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape,
    colors: TextFieldColors
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = value,
            onValueChange = {},
            label = if (label.isNotEmpty()) { { Text(label) } } else null,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable).fillMaxWidth().neoShadow(cornerRadius = 16.dp, isPressed = true),
            shape = shape,
            colors = colors,
            readOnly = true,
            textStyle = LocalTextStyle.current.copy(fontSize = androidx.compose.ui.unit.TextUnit(16f, androidx.compose.ui.unit.TextUnitType.Sp))
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(NeoSurface)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = TextDark) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
