package com.example

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PortfolioViewModel : ViewModel() {

    private val _profileState = MutableStateFlow(createDefaultProfile())
    val profileState: StateFlow<PortfolioProfile> = _profileState.asStateFlow()

    private val _htmlCodeState = MutableStateFlow("")
    val htmlCodeState: StateFlow<String> = _htmlCodeState.asStateFlow()

    init {
        regenerateHtml()
    }

    private fun createDefaultProfile(): PortfolioProfile {
        return PortfolioProfile(
            name = "Md. Morad Husain",
            title = "Software Engineer & Web Developer",
            bio = "আমি একজন প্রফেশনাল ওয়েব ও এন্ড্রয়েড অ্যাপ্লিকেশন ডেভেলপার। আধুনিক ওয়েব ফ্রেমওয়ার্ক (HTML5, Tailwind, React, Node.js) এবং এন্ড্রয়েড অ্যাপ্লিকেশন (Kotlin, Jetpack Compose) ডেভেলপমেন্টে আমার দীর্ঘদিনের অভিজ্ঞতা রয়েছে। আমি ক্লিন কোড এবং চমৎকার ইউজার ফ্রেন্ডলি ইন্টারফেস তৈরি করতে ভালোবাসি।",
            email = "moradhusain21@gmail.com",
            phone = "+880 1712-34bd90",
            location = "উত্তরা, ঢাকা, বাংলাদেশ",
            github = "https://github.com/moradhusain",
            linkedin = "https://linkedin.com/in/moradhusain",
            skills = listOf(
                "HTML5 / CSS3", "JavaScript ES6+", "React.js", "Tailwind CSS", 
                "Bootstrap", "Git & GitHub", "Kotlin", "Jetpack Compose",
                "Node.js & Express", "SQLite & Room"
            ),
            projects = listOf(
                Project(
                    title = "E-Commerce Web Portal",
                    description = "একটি রিয়্যাক্ট-ভিত্তিক সম্পূর্ণ রেসপনসিভ ই-কমার্স সাইট, যাতে রয়েছে ডাইনামিক প্রোডাক্ট ফিল্টারিং, ইন্টারেক্টিভ কন্ট্যাক্ট ফর্ম এবং চমৎকার শপিং কার্ট সিস্টেম।",
                    techStack = "React, TailwindCSS, HTML5",
                    link = "https://github.com/moradhusain/shop-portal"
                ),
                Project(
                    title = "Portfolio Maker Android App",
                    description = "একটি প্রিমিয়াম অ্যান্ড্রয়েড অ্যাপ্লিকেশন যা রিয়েল-টাইমে ব্যবহারকারীকে একাধিক টেমপ্লেট থেকে ইনপুট নিয়ে রেসপনসিভ এবং আধুনিক এইচটিএমএল পোর্টফোলিও ওয়েবসাইট তৈরি করে দেয়।",
                    techStack = "Kotlin, Jetpack Compose, HTML5, WebView",
                    link = "https://github.com/moradhusain/portfolio-maker"
                ),
                Project(
                    title = "TaskFlow HTML Web Panel",
                    description = "একটি অফলাইন টু-ডু এবং টাস্ক ট্র্যাকিং ফ্রন্টএন্ড ওয়েব অ্যাপ, যার সাথে চমৎকার এনিমেটেড ডাটা গ্রাফ এবং ডার্ক মোড সাপোর্ট রয়েছে।",
                    techStack = "HTML5, CSS3, JS, Chart.js",
                    link = "https://github.com/moradhusain/task-flow"
                )
            ),
            selectedTemplate = PortfolioTemplate.MODERN_GLASS,
            accentColorHex = "#10B981"
        )
    }

    fun updateProfile(builder: (PortfolioProfile) -> PortfolioProfile) {
        _profileState.update { builder(it) }
        regenerateHtml()
    }

    fun addSkill(skill: String) {
        if (skill.trim().isNotEmpty()) {
            _profileState.update { current ->
                if (!current.skills.contains(skill.trim())) {
                    current.copy(skills = current.skills + skill.trim())
                } else {
                    current
                }
            }
            regenerateHtml()
        }
    }

    fun removeSkill(skill: String) {
        _profileState.update { current ->
            current.copy(skills = current.skills.filter { it != skill })
        }
        regenerateHtml()
    }

    fun addProject(project: Project) {
        _profileState.update { current ->
            current.copy(projects = current.projects + project)
        }
        regenerateHtml()
    }

    fun removeProject(project: Project) {
        _profileState.update { current ->
            current.copy(projects = current.projects.filter { it != project })
        }
        regenerateHtml()
    }

    fun setTemplate(template: PortfolioTemplate) {
        _profileState.update { current ->
            val defaultColor = when (template) {
                PortfolioTemplate.CLASSIC_IVORY -> "#4F46E5" // Royal Indigo
                PortfolioTemplate.RETRO_TERMINAL -> "#00FF33" // Classic Matrix Green
                PortfolioTemplate.NEON_CYBER -> "#F43F5E" // Rose Red
                PortfolioTemplate.MODERN_GLASS -> "#10B981" // Emerald
            }
            current.copy(selectedTemplate = template, accentColorHex = defaultColor)
        }
        regenerateHtml()
    }

    fun setAccentColor(hex: String) {
        _profileState.update { current ->
            current.copy(accentColorHex = hex)
        }
        regenerateHtml()
    }

    fun resetToDefault() {
        _profileState.value = createDefaultProfile()
        regenerateHtml()
    }

    private fun regenerateHtml() {
        _htmlCodeState.value = HtmlPortfolioGenerator.generateHtml(_profileState.value)
    }
}
