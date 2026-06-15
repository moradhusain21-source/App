package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// Custom visual colors
val PremiumDarkBg = Color(0xFF0F172A)
val CardDarkBg = Color(0xFF1E293B)
val GlassBg = Color(0xAA1E293B)
val LightAccentColor = Color(0xFF3B82F6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioAppMainScreen(viewModel: PortfolioViewModel) {
    val profile by viewModel.profileState.collectAsStateWithLifecycle()
    val htmlCode by viewModel.htmlCodeState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    var currentTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        TabItem("তথ্য দিন", Icons.Default.Edit, "details_tab"),
        TabItem("টেম্পলেট", Icons.Default.Palette, "templates_tab"),
        TabItem("লাইভ প্রিভিউ", Icons.Default.Language, "preview_tab"),
        TabItem("কোড ভিউয়ার", Icons.Default.Code, "code_tab")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "পোর্টিফোলিও মেকার",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "HTML Portfolio App & Editor",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.resetToDefault()
                            Toast.makeText(context, "ডিফল্ট তথ্যে রিসেট করা হয়েছে", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("reset_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Form Data",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.testTag("main_top_bar")
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("bottom_nav_bar")
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = currentTab == index,
                        onClick = { currentTab = index },
                        label = { Text(tab.title, fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    slideInHorizontally { width -> if (targetState > initialState) width else -width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> if (targetState > initialState) -width else width } + fadeOut()
                },
                label = "TabTransition"
            ) { targetTab ->
                when (targetTab) {
                    0 -> DetailsFormScreen(viewModel = viewModel, profile = profile)
                    1 -> TemplateSelectorScreen(viewModel = viewModel, profile = profile)
                    2 -> LivePreviewScreen(htmlCode = htmlCode)
                    3 -> CodeViewerScreen(htmlCode = htmlCode)
                }
            }
        }
    }
}

data class TabItem(val title: String, val icon: ImageVector, val testTag: String)

// --- 1. DETAILS FORM SCREEN ---
@Composable
fun DetailsFormScreen(viewModel: PortfolioViewModel, profile: PortfolioProfile) {
    var skillInput by remember { mutableStateOf("") }
    var showAddProjectDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("details_scroll_area"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Core Info Card
        item {
            DetailsCardHeader(title = "ব্যক্তিগত তথ্য (Personal Info)", icon = Icons.Default.Person) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = profile.name,
                        onValueChange = { name -> viewModel.updateProfile { it.copy(name = name) } },
                        label = { Text("আপনার নাম (Name)") },
                        modifier = Modifier.fillMaxWidth().testTag("input_name"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                    )
                    OutlinedTextField(
                        value = profile.title,
                        onValueChange = { title -> viewModel.updateProfile { it.copy(title = title) } },
                        label = { Text("পেশাগত পদবী (Designation)") },
                        modifier = Modifier.fillMaxWidth().testTag("input_title"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.WorkspacePremium, contentDescription = null) }
                    )
                    OutlinedTextField(
                        value = profile.bio,
                        onValueChange = { bio -> viewModel.updateProfile { it.copy(bio = bio) } },
                        label = { Text("সংক্ষিপ্ত পরিচিতি (Short Bio/Summary)") },
                        modifier = Modifier.fillMaxWidth().height(100.dp).testTag("input_bio"),
                        minLines = 3,
                        leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) }
                    )
                }
            }
        }

        // Contact details Card
        item {
            DetailsCardHeader(title = "যোগাযোগ (Contact)", icon = Icons.Default.ContactPage) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = profile.email,
                        onValueChange = { email -> viewModel.updateProfile { it.copy(email = email) } },
                        label = { Text("ইমেইল ঠিকানা (Email)") },
                        modifier = Modifier.fillMaxWidth().testTag("input_email"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
                    )
                    OutlinedTextField(
                        value = profile.phone,
                        onValueChange = { phone -> viewModel.updateProfile { it.copy(phone = phone) } },
                        label = { Text("ফোন নম্বর (Phone)") },
                        modifier = Modifier.fillMaxWidth().testTag("input_phone"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }
                    )
                    OutlinedTextField(
                        value = profile.location,
                        onValueChange = { loc -> viewModel.updateProfile { it.copy(location = loc) } },
                        label = { Text("ঠিকানা বা লোকেশন (Location)") },
                        modifier = Modifier.fillMaxWidth().testTag("input_location"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) }
                    )
                }
            }
        }

        // Social Media details Card
        item {
            DetailsCardHeader(title = "অনলাইন লিংক (Social Connections)", icon = Icons.Default.Share) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = profile.github,
                        onValueChange = { github -> viewModel.updateProfile { it.copy(github = github) } },
                        label = { Text("GitHub প্রোফাইল লিংক") },
                        modifier = Modifier.fillMaxWidth().testTag("input_github"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Code, contentDescription = null) }
                    )
                    OutlinedTextField(
                        value = profile.linkedin,
                        onValueChange = { linkedin -> viewModel.updateProfile { it.copy(linkedin = linkedin) } },
                        label = { Text("LinkedIn প্রোফাইল লিংক") },
                        modifier = Modifier.fillMaxWidth().testTag("input_linkedin"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) }
                    )
                }
            }
        }

        // Skills List Card
        item {
            DetailsCardHeader(title = "দক্ষতসমূহ (Skills List)", icon = Icons.Default.MilitaryTech) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = skillInput,
                            onValueChange = { skillInput = it },
                            label = { Text("নতুন দক্ষতা (যেমন: HTML5)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_skill_term"),
                            singleLine = true
                        )
                        Button(
                            onClick = {
                                if (skillInput.trim().isNotEmpty()) {
                                    viewModel.addSkill(skillInput.trim())
                                    skillInput = ""
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(56.dp)
                                .testTag("btn_add_skill")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Skill")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (profile.skills.isEmpty()) {
                        Text(
                            "কোন দক্ষতা যোগ করা হয়নি। উপরে টাইপ করে কুইক অ্যাড করুন।",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        // Flow row alternative
                        Column {
                            Text(
                                "মোট এড করা দক্ষতা (${profile.skills.size} টি):",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Box(modifier = Modifier.fillMaxWidth()) {
                                @OptIn(ExperimentalLayoutApi::class)
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    profile.skills.forEach { skill ->
                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                            modifier = Modifier.testTag("skill_${skill.replace(" ", "_")}")
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = skill,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Delete Skill",
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .clickable { viewModel.removeSkill(skill) },
                                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
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

        // Projects Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("projects_section_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.FolderOpen, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(
                                text = "প্রজেক্টসমূহ (${profile.projects.size} টি)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Button(
                            onClick = { showAddProjectDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_trigger_add_project_dialog")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Project Dialog",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("প্রজেক্ট যোগ করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (profile.projects.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Terminal,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.outline
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "আপনার কোনো প্রজেক্ট যুক্ত করা নেই।",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            profile.projects.forEach { proj ->
                                ProjectItemRow(project = proj, onDelete = {
                                    viewModel.removeProject(proj)
                                })
                            }
                        }
                    }
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    if (showAddProjectDialog) {
        AddProjectDialog(
            onDismiss = { showAddProjectDialog = false },
            onSave = { newProj ->
                viewModel.addProject(newProj)
                showAddProjectDialog = false
            }
        )
    }
}

@Composable
fun DetailsCardHeader(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            content()
        }
    }
}

@Composable
fun ProjectItemRow(project: Project, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("project_item_${project.title.replace(" ", "_")}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = project.description,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "টেকনোলজি: ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = project.techStack,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("delete_proj_${project.title.replace(" ", "_")}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Project",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun AddProjectDialog(onDismiss: () -> Unit, onSave: (Project) -> Unit) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var tech by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("নতুন প্রজেক্ট যোগ করুন", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("প্রজেক্টের নাম (Project Title)") },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_input_title"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("বর্ণনা (Short Description)") },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_input_desc"),
                    minLines = 2
                )
                OutlinedTextField(
                    value = tech,
                    onValueChange = { tech = it },
                    label = { Text("টেকনোলজি (যেমন: HTML5, React, JS)") },
                    placeholder = { Text("কমা দিয়ে আলাদা করুন") },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_input_tech"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = link,
                    onValueChange = { link = it },
                    label = { Text("GitHub/ওয়েবসাইট লিংক (ঐচ্ছিক)") },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_input_link"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.trim().isNotEmpty()) {
                        onSave(Project(title.trim(), desc.trim(), tech.trim(), link.trim()))
                    }
                },
                enabled = title.trim().isNotEmpty(),
                modifier = Modifier.testTag("dialog_btn_save")
            ) {
                Text("সেভ করুন")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dialog_btn_cancel")
            ) {
                Text("বাতিল")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

// --- 2. TEMPLATE SELECTOR SCREEN ---
@Composable
fun TemplateSelectorScreen(viewModel: PortfolioViewModel, profile: PortfolioProfile) {
    val context = LocalContext.current
    val colorOptions = listOf(
        ColorOption("Emerald Web", "#10B981", Color(0xFF10B981)),
        ColorOption("Sunset Orange", "#F97316", Color(0xFFF97316)),
        ColorOption("Cyber Blue", "#3B82F6", Color(0xFF3B82F6)),
        ColorOption("Imperial Indigo", "#6366F1", Color(0xFF6366F1)),
        ColorOption("Vibrant Rose", "#ED4C99", Color(0xFFED4C99)),
        ColorOption("Retro Green", "#00FF33", Color(0xFF00FF33))
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("templates_scroll_area"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                    Column {
                        Text(
                            "ডিজাইন মেকার ও টেমপ্লেট",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "নিচের যেকোনো একটি পোর্টফোলিও ডিজাইন লেআউট পছন্দ করুন এবং এর মূল হাইলাইট কালার বেছে নিন।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Text(
                "টেমপ্লেট লেআউটসমূহ:",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Templates rendering grid
        items(PortfolioTemplate.values().toList()) { template ->
            val isSelected = profile.selectedTemplate == template
            val borderWidth = if (isSelected) 3.dp else 1.dp
            val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
            val shadowElevation = if (isSelected) 6.dp else 1.dp

            Card(
                onClick = { viewModel.setTemplate(template) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("template_card_${template.name}"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(borderWidth, borderColor),
                elevation = CardDefaults.cardElevation(defaultElevation = shadowElevation)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Accent preview visual circular design
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = when (template) {
                                        PortfolioTemplate.CLASSIC_IVORY -> listOf(Color(0xFFFDFBFA), Color(0xFF64748B))
                                        PortfolioTemplate.RETRO_TERMINAL -> listOf(Color(0xFF0C0F12), Color(0xFF00FF33))
                                        PortfolioTemplate.NEON_CYBER -> listOf(Color(0xFF05050A), Color(0xFFEC4899))
                                        PortfolioTemplate.MODERN_GLASS -> listOf(Color(0xFF0F172A), Color(0xFF10B981))
                                    }
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (template) {
                                PortfolioTemplate.CLASSIC_IVORY -> Icons.Default.Smartphone
                                PortfolioTemplate.RETRO_TERMINAL -> Icons.Default.Terminal
                                PortfolioTemplate.NEON_CYBER -> Icons.Default.Bolt
                                PortfolioTemplate.MODERN_GLASS -> Icons.Default.AutoAwesome
                            },
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = template.label,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        "সিলেক্টেড",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = template.description,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Color selector
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "অ্যাকসেন্ট থিম কালার (Accent Hue):",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "পোর্টফোলিওর বাটন, বর্ডার এবং হাইলাইটসমূহ এই কালারে প্রদর্শিত হবে।",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        colorOptions.forEach { opt ->
                            val isColorSelected = profile.accentColorHex.lowercase() == opt.hex.lowercase()
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(opt.composeColor)
                                    .clickable {
                                        viewModel.setAccentColor(opt.hex)
                                        Toast.makeText(context, "${opt.name} কালার সিলেক্ট করা হয়েছে", Toast.LENGTH_SHORT).show()
                                    }
                                    .border(
                                        BorderStroke(
                                            if (isColorSelected) 3.dp else 0.dp,
                                            if (isColorSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent
                                        ),
                                        CircleShape
                                    )
                                    .testTag("color_circle_${opt.name.replace(" ", "_")}"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isColorSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = if (opt.hex == "#00FF33") Color.Black else Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(android.graphics.Color.parseColor(profile.accentColorHex)).copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "বর্তমান কোড টোন: ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(profile.accentColorHex)))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = profile.accentColorHex,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(android.graphics.Color.parseColor(profile.accentColorHex))
                        )
                    }
                }
            }
        }
    }
}

data class ColorOption(val name: String, val hex: String, val composeColor: Color)

// --- 3. LIVE WEBVIEW PREVIEW ---
@Composable
fun LivePreviewScreen(htmlCode: String) {
    var isDesktopView by remember { mutableStateOf(false) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .testTag("preview_container")
    ) {
        // Controls Row
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Leftside Toggle Desktop vs Mobile
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = { isDesktopView = false },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (!isDesktopView) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                        ),
                        modifier = Modifier.testTag("btn_toggle_mobile_view")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Smartphone,
                            contentDescription = "Switch to Mobile View",
                            tint = if (!isDesktopView) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = { isDesktopView = true },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (isDesktopView) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                        ),
                        modifier = Modifier.testTag("btn_toggle_desktop_view")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Computer,
                            contentDescription = "Switch to Desktop/Tablet View",
                            tint = if (isDesktopView) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = if (isDesktopView) "ওয়াইডস্ক্রিন প্রিভিউ (Responsive)" else "মোবাইল লেআউট প্রিভিউ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                // Refresh control
                IconButton(
                    onClick = {
                        webViewRef?.loadDataWithBaseURL("https://localhost", htmlCode, "text/html", "utf-8", null)
                    },
                    modifier = Modifier.testTag("btn_refresh_preview")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reload Web View",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Live WebView Rendering frame
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            val widthModifier = if (isDesktopView) {
                Modifier.fillMaxSize()
            } else {
                Modifier
                    .fillMaxHeight()
                    .widthIn(max = 380.dp)
                    .border(BorderStroke(4.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)), RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(12.dp))
            }

            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.useWideViewPort = true
                        settings.loadWithOverviewMode = true
                        webViewRef = this
                        loadDataWithBaseURL("https://localhost", htmlCode, "text/html", "utf-8", null)
                    }
                },
                update = { webView ->
                    webView.loadDataWithBaseURL("https://localhost", htmlCode, "text/html", "utf-8", null)
                },
                modifier = widthModifier.fillMaxSize().testTag("preview_web_view")
            )
        }
    }
}

// --- 4. HTML CODE VIEW SCREEN ---
@Composable
fun CodeViewerScreen(htmlCode: String) {
    val context = LocalContext.current
    var showGuide by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("code_viewer_screen")
    ) {
        // Quick Action Operations Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "কোড জেনারেটর সফল! 🎉",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("HTML Portfolio", htmlCode)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "এইচটিএমএল কোড সফলভাবে কপি করা হয়েছে! 📋", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.testTag("btn_copy_code")
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("কপি করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, htmlCode)
                                putExtra(Intent.EXTRA_SUBJECT, "My HTML Portfolio Website")
                            }
                            context.startActivity(Intent.createChooser(intent, "কোড শেয়ার করুন:"))
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier.testTag("btn_share_code")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share HTML File",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Code Scrolling View Box
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PremiumDarkBg),
            border = BorderStroke(2.dp, Color(0xFF1E293B))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .testTag("code_scroll_area")
                ) {
                    item {
                        Text(
                            text = htmlCode,
                            color = Color(0xFF34D399), // Emerald Neon Green
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Info Guide Section on free hosting
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showGuide = !showGuide }
                .testTag("btn_toggle_hosting_guide")
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            "এই কোডটি কিভাবে ইন্টারনেটে ফ্রিতে আপলোড করবেন?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Icon(
                        imageVector = if (showGuide) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                }

                if (showGuide) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = """
                            খুব সহজেই আপনার এই পোর্টফোলিও সাইটটি ফ্রিতে লাইভ করতে পারেন:
                            
                            ১. "কপি করুন" বাটনে ক্লিক করে কোডটি কপি করুন।
                            ২. যেকোনো কোড এডিটর বা কম্পিউটারে "index.html" নামে একটি ফাইল তৈরি করে কোডটি সেখানে পেস্ট (Paste) করে সেভ করুন।
                            ৩. GitHub এ গিয়ে একটি নতুন রিপোজিটরি (Repository) তৈরি করুন এবং ফাইলটি আপলোড করুন।
                            ৪. Settings > Pages এ গিয়ে Source হিসেবে "main segment branch" সিলেক্ট করে সেভ করলেই কয়েক মিনিটে আপনার ওয়েবসাইট লাইভ হয়ে যাবে!
                            ৫. এছাড়া Netlify.com অথবা Vercel.com এ ফ্রি অ্যাকাউন্ট তৈরি করে সরাসরি "index.html" ফাইলটি ড্র্যাগ অ্যান্ড ড্রপ করলেই পেয়ে যাবেন নিজস্ব ফ্রি লিংক!
                        """.trimIndent(),
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://pages.github.com/"))
                            context.startActivity(browserIntent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier
                            .align(Alignment.End)
                            .testTag("btn_visit_github_pages")
                    ) {
                        Text("GitHub Pages দেখুন ▸", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
