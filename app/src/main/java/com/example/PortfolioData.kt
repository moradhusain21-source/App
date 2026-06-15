package com.example

import java.io.Serializable

data class Project(
    val title: String,
    val description: String,
    val techStack: String,
    val link: String
) : Serializable

data class PortfolioProfile(
    val name: String,
    val title: String,
    val bio: String,
    val email: String,
    val phone: String,
    val location: String,
    val github: String,
    val linkedin: String,
    val skills: List<String>,
    val projects: List<Project>,
    val selectedTemplate: PortfolioTemplate = PortfolioTemplate.MODERN_GLASS,
    val accentColorHex: String = "#10B981" // Emerald
) : Serializable

enum class PortfolioTemplate(val label: String, val description: String) {
    MODERN_GLASS("Modern Glassmorphism", "আধুনিক কাঁচের মত স্বচ্ছ ডার্ক মোড ডিজাইন, নিয়ন গ্লো ইফেক্ট"),
    CLASSIC_IVORY("Classic Ivory", "মার্জিত ও পরিষ্কার লাইট মোড ডিজাইন, সুন্দর সেরিফ টাইপোগ্রাফি"),
    RETRO_TERMINAL("Retro Terminal", "ক্লাসিক সবুজ এবং কালো রঙের রেট্রো কম্পিউটার টার্মিনাল স্টাইল"),
    NEON_CYBER("Cyberpunk Pulse", "উজ্জ্বল গোলাপী ও বেগুনী অ্যাকসেন্ট সহ ডার্ক ফিউচারিস্টিক ভাইব")
}

object HtmlPortfolioGenerator {
    
    fun generateHtml(profile: PortfolioProfile): String {
        val accentColor = profile.accentColorHex
        val secondaryColor = when (profile.selectedTemplate) {
            PortfolioTemplate.MODERN_GLASS -> "#3B82F6" // Blue
            PortfolioTemplate.CLASSIC_IVORY -> "#1E293B" // Slate
            PortfolioTemplate.RETRO_TERMINAL -> "#00FF00" // Bright neon green
            PortfolioTemplate.NEON_CYBER -> "#EC4899" // Pink magenta
        }

        val fontImports = when (profile.selectedTemplate) {
            PortfolioTemplate.CLASSIC_IVORY -> "@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400..700;1,400..700&family=Plus+Jakarta+Sans:wght@300..800&display=swap');"
            PortfolioTemplate.RETRO_TERMINAL -> "@import url('https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@300..700&display=swap');"
            else -> "@import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@300..800&display=swap');"
        }

        val primaryFont = when (profile.selectedTemplate) {
            PortfolioTemplate.CLASSIC_IVORY -> "'Plus Jakarta Sans', sans-serif"
            PortfolioTemplate.RETRO_TERMINAL -> "'JetBrains Mono', monospace"
            else -> "'Plus Jakarta Sans', sans-serif"
        }

        val headingFont = when (profile.selectedTemplate) {
            PortfolioTemplate.CLASSIC_IVORY -> "'Playfair Display', serif"
            PortfolioTemplate.RETRO_TERMINAL -> "'JetBrains Mono', monospace"
            else -> "'Plus Jakarta Sans', sans-serif"
        }

        val cssStyles = when (profile.selectedTemplate) {
            PortfolioTemplate.CLASSIC_IVORY -> """
                :root {
                    --bg-primary: #FDFBFA;
                    --bg-secondary: #F5F1EE;
                    --text-primary: #1E293B;
                    --text-secondary: #64748B;
                    --accent: $accentColor;
                    --card-bg: #FFFFFF;
                    --card-border: rgba(226, 232, 240, 0.8);
                    --shadow: 0 4px 20px -2px rgba(120, 110, 100, 0.08);
                    --radius: 12px;
                }
                body {
                    background: var(--bg-primary);
                }
                .hero {
                    text-align: center;
                    padding: 80px 20px;
                    border-bottom: 1.5px solid var(--bg-secondary);
                }
                .section-title {
                    font-family: $headingFont;
                    font-weight: 700;
                    font-size: 2.2rem;
                    text-align: center;
                    margin-bottom: 40px;
                    color: var(--text-primary);
                    position: relative;
                }
                .section-title::after {
                    content: '';
                    display: block;
                    width: 50px;
                    height: 3px;
                    background: var(--accent);
                    margin: 12px auto 0 auto;
                    border-radius: 4px;
                }
                .project-card {
                    transition: transform 0.3s ease, box-shadow 0.3s ease;
                }
                .project-card:hover {
                    transform: translateY(-5px);
                    box-shadow: 0 10px 30px -5px rgba(120, 110, 100, 0.15);
                }
                .skill-badge {
                    background: var(--bg-secondary);
                    color: var(--text-primary);
                    border: 1px solid rgba(0,0,0,0.04);
                    font-weight: 500;
                }
                .skill-badge:hover {
                    background: var(--accent);
                    color: #FFFFFF;
                }
            """.trimIndent()
            
            PortfolioTemplate.RETRO_TERMINAL -> """
                :root {
                    --bg-primary: #0C0F12;
                    --bg-secondary: #161B22;
                    --text-primary: #00FF33;
                    --text-secondary: #88FF88;
                    --accent: $accentColor;
                    --card-bg: #101419;
                    --card-border: var(--text-primary);
                    --shadow: 0 0 10px rgba(0, 255, 51, 0.15);
                    --radius: 4px;
                }
                body {
                    background: var(--bg-primary);
                    background-image: radial-gradient(#00FF33 0.5px, transparent 0.5px);
                    background-size: 24px 24px;
                }
                .hero {
                    border: 2px solid var(--text-primary);
                    border-radius: var(--radius);
                    padding: 40px 20px;
                    margin: 40px 0;
                    background: var(--card-bg);
                    position: relative;
                    box-shadow: var(--shadow);
                }
                .hero::before {
                    content: ' [ terminal_session: active ] ';
                    position: absolute;
                    top: -12px;
                    left: 20px;
                    background: var(--bg-primary);
                    padding: 0 8px;
                    font-size: 0.8rem;
                    color: var(--text-primary);
                }
                .section-title {
                    font-family: $headingFont;
                    font-size: 1.8rem;
                    margin-bottom: 30px;
                    color: var(--text-primary);
                    text-transform: uppercase;
                }
                .section-title::before {
                    content: '> ';
                }
                .project-card {
                    border: 1px dashed var(--text-primary);
                    background: var(--card-bg);
                }
                .project-card:hover {
                    border-style: solid;
                    background: #152219;
                    box-shadow: 0 0 15px rgba(0, 255, 51, 0.3);
                }
                .skill-badge {
                    background: transparent;
                    color: var(--text-primary);
                    border: 1px solid var(--text-primary);
                }
                .skill-badge:hover {
                    background: var(--text-primary);
                    color: #000;
                }
                .contact-btn {
                    border: 1px solid var(--text-primary);
                }
                .contact-btn:hover {
                    background: var(--text-primary);
                    color: #000;
                }
            """.trimIndent()

            PortfolioTemplate.NEON_CYBER -> """
                :root {
                    --bg-primary: #05050A;
                    --bg-secondary: #0F0F23;
                    --text-primary: #FFFFFF;
                    --text-secondary: #CDCDDD;
                    --accent: $accentColor;
                    --card-bg: #0C0C1E;
                    --card-border: rgba(236, 72, 153, 0.3);
                    --shadow: 0 0 15px rgba(236, 72, 153, 0.15);
                    --radius: 16px;
                }
                body {
                    background: var(--bg-primary);
                    overflow-x: hidden;
                }
                body::before {
                    content: '';
                    position: fixed;
                    top: -20%;
                    left: -20%;
                    width: 70%;
                    height: 70%;
                    background: radial-gradient(circle, rgba(236, 72, 153, 0.08) 0%, transparent 70%);
                    z-index: -1;
                    pointer-events: none;
                }
                body::after {
                    content: '';
                    position: fixed;
                    bottom: -20%;
                    right: -20%;
                    width: 70%;
                    height: 70%;
                    background: radial-gradient(circle, rgba(59, 130, 246, 0.08) 0%, transparent 70%);
                    z-index: -1;
                    pointer-events: none;
                }
                .hero {
                    text-align: center;
                    padding: 100px 20px;
                    background: linear-gradient(135deg, var(--card-bg) 0%, var(--bg-secondary) 100%);
                    border: 1px solid rgba(255, 255, 255, 0.05);
                    border-radius: var(--radius);
                    box-shadow: var(--shadow);
                    position: relative;
                }
                .section-title {
                    font-family: $headingFont;
                    font-weight: 800;
                    font-size: 2.2rem;
                    text-align: center;
                    margin-bottom: 40px;
                    letter-spacing: -1px;
                }
                .section-title span {
                    background: linear-gradient(90deg, var(--accent) 0%, $secondaryColor 100%);
                    -webkit-background-clip: text;
                    -webkit-text-fill-color: transparent;
                }
                .project-card {
                    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
                    border: 1px solid rgba(255,255,255,0.03);
                    background: linear-gradient(145deg, rgba(12,12,30,0.8) 0%, rgba(5,5,10,0.8) 100%);
                    box-shadow: 0 4px 30px rgba(0, 0, 0, 0.4);
                }
                .project-card:hover {
                    transform: translateY(-8px) scale(1.01);
                    border-color: var(--accent);
                    box-shadow: 0 0 25px rgba(236, 72, 153, 0.25);
                }
                .skill-badge {
                    background: rgba(255, 255, 255, 0.03);
                    border: 1px solid rgba(255, 255, 255, 0.08);
                }
                .skill-badge:hover {
                    background: linear-gradient(135deg, var(--accent) 0%, $secondaryColor 100%);
                    color: #fff;
                    border-color: transparent;
                    box-shadow: 0 0 12px var(--accent);
                }
            """.trimIndent()

            PortfolioTemplate.MODERN_GLASS -> """
                :root {
                    --bg-primary: #0F172A;
                    --bg-secondary: #1E293B;
                    --text-primary: #F8FAFC;
                    --text-secondary: #94A3B8;
                    --accent: $accentColor;
                    --card-bg: rgba(30, 41, 59, 0.45);
                    --card-border: rgba(255, 255, 255, 0.08);
                    --shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.2);
                    --radius: 16px;
                }
                body {
                    background: radial-gradient(circle at 10% 20%, rgba(15, 23, 42, 1) 0%, rgba(20, 20, 48, 1) 90%);
                }
                .hero {
                    backdrop-filter: blur(12px);
                    -webkit-backdrop-filter: blur(12px);
                    background: rgba(30, 41, 59, 0.4);
                    border: 1px solid var(--card-border);
                    border-radius: var(--radius);
                    padding: 80px 20px;
                    box-shadow: var(--shadow);
                }
                .section-title {
                    font-family: $headingFont;
                    font-weight: 700;
                    font-size: 2.3rem;
                    text-align: center;
                    margin-bottom: 40px;
                }
                .section-title span {
                    background: linear-gradient(135deg, var(--accent) 0%, $secondaryColor 100%);
                    -webkit-background-clip: text;
                    -webkit-text-fill-color: transparent;
                }
                .project-card {
                    backdrop-filter: blur(10px);
                    -webkit-backdrop-filter: blur(10px);
                    background: var(--card-bg);
                    border: 1px solid var(--card-border);
                    transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
                }
                .project-card:hover {
                    background: rgba(30, 41, 59, 0.7);
                    border-color: rgba(255, 255, 255, 0.18);
                    transform: translateY(-6px);
                    box-shadow: 0 12px 30px rgba(0, 0, 0, 0.3);
                }
                .skill-badge {
                    background: rgba(255, 255, 255, 0.04);
                    color: var(--text-primary);
                    border: 1px solid rgba(255,255,255,0.06);
                    backdrop-filter: blur(4px);
                }
                .skill-badge:hover {
                    background: var(--accent);
                    border-color: var(--accent);
                    color: #FFFFFF;
                }
            """.trimIndent()
        }

        // Generate skills dynamic HTML
        val skillsHtml = profile.skills.joinToString("") { skill ->
            "<div class=\"skill-badge\">$skill</div>"
        }

        // Generate projects dynamic HTML
        val projectsHtml = if (profile.projects.isEmpty()) {
            "<p style=\"text-align:center; color:var(--text-secondary); grid-column: 1/-1;\">কোন প্রজেক্ট যুক্ত করা হয়নি।</p>"
        } else {
            profile.projects.joinToString("") { proj ->
                """
                <div class="project-card">
                    <h3 class="project-title">${proj.title}</h3>
                    <p class="project-desc">${proj.description}</p>
                    <div class="project-tech-label">Technology:</div>
                    <div style="flex-direction: row; display: flex; flex-wrap: wrap; gap: 6px; margin: 8px 0 15px 0;">
                        ${proj.techStack.split(",").joinToString("") { tag -> 
                            if (tag.trim().isNotEmpty()) "<span class=\"tag-pill\">${tag.trim()}</span>" else "" 
                        }}
                    </div>
                    ${if (proj.link.isNotEmpty()) """
                        <a href="${proj.link}" target="_blank" class="project-link">
                            ওয়েবসাইট দেখুন 
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="margin-left: 4px; vertical-align: middle;"><line x1="5" y1="12" x2="19" y2="12"></line><polyline points="12 5 19 12 12 19"></polyline></svg>
                        </a>
                    """.trimIndent() else ""}
                </div>
                """.trimIndent()
            }
        }

        // Generate contact methods check
        val emailHtml = if (profile.email.isNotEmpty()) """
            <div class="contact-item">
                <span class="contact-icon">📧</span>
                <div>
                    <div class="contact-label">ইমেইল</div>
                    <a href="mailto:${profile.email}" class="contact-value">${profile.email}</a>
                </div>
            </div>
        """.trimIndent() else ""

        val phoneHtml = if (profile.phone.isNotEmpty()) """
            <div class="contact-item">
                <span class="contact-icon">📞</span>
                <div>
                    <div class="contact-label">ফোন</div>
                    <a href="tel:${profile.phone}" class="contact-value">${profile.phone}</a>
                </div>
            </div>
        """.trimIndent() else ""

        val locationHtml = if (profile.location.isNotEmpty()) """
            <div class="contact-item">
                <span class="contact-icon">📍</span>
                <div>
                    <div class="contact-label">ঠিকানা</div>
                    <span class="contact-value" style="color:var(--text-primary); text-decoration: none;">${profile.location}</span>
                </div>
            </div>
        """.trimIndent() else ""

        // Social handles bottom
        val socialSection = if (profile.github.isNotEmpty() || profile.linkedin.isNotEmpty()) {
            """
            <div class="social-links">
                ${if (profile.github.isNotEmpty()) """
                    <a href="${profile.github}" target="_blank" class="social-icon-btn" title="GitHub">
                        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 19c-5 1.5-5-2.5-7-3m14 6v-3.87a3.37 3.37 0 0 0-.94-2.61c3.14-.35 6.44-1.54 6.44-7A5.44 5.44 0 0 0 20 4.77 5.07 5.07 0 0 0 19.91 1S18.73.65 16 2.48a13.38 13.38 0 0 0-7 0C6.27.65 5.09 1 5.09 1A5.07 5.07 0 0 0 5 4.77a5.44 5.44 0 0 0-1.5 3.78c0 5.42 3.3 6.61 6.44 7A3.37 3.37 0 0 0 9 18.13V22"></path></svg>
                        GitHub
                    </a>
                """.trimIndent() else ""}
                ${if (profile.linkedin.isNotEmpty()) """
                    <a href="${profile.linkedin}" target="_blank" class="social-icon-btn" title="LinkedIn">
                        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M16 8a6 6 0 0 1 6 6v7h-4v-7a2 2 0 0 0-2-2 2 2 0 0 0-2 2v7h-4v-7a6 6 0 0 1 6-6z"></path><rect x="2" y="9" width="4" height="12"></rect><circle cx="4" cy="4" r="2"></circle></svg>
                        LinkedIn
                    </a>
                """.trimIndent() else ""}
            </div>
            """.trimIndent()
        } else ""

        // Compile HTML
        return """
        <!DOCTYPE html>
        <html lang="bn">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>${profile.name} | Portfolio</title>
            <style>
                $fontImports
                
                $cssStyles
                
                /* Reset and Core Layout */
                * {
                    margin: 0;
                    padding: 0;
                    box-sizing: border-box;
                    font-family: $primaryFont;
                }
                body {
                    color: var(--text-primary);
                    line-height: 1.6;
                    padding: 0;
                    font-size: 16px;
                }
                .container {
                    max-width: 1000px;
                    margin: 0 auto;
                    padding: 0 24px;
                }
                a {
                    color: var(--accent);
                    text-decoration: none;
                    transition: color 0.2s ease;
                }
                a:hover {
                    opacity: 0.85;
                }
                
                /* Hero Section Details */
                .avatar-placeholder {
                    width: 90px;
                    height: 90px;
                    border-radius: 50%;
                    background: linear-gradient(135deg, var(--accent) 0%, $secondaryColor 100%);
                    margin: 0 auto 20px auto;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 2.2rem;
                    color: #fff;
                    font-weight: bold;
                    box-shadow: 0 4px 15px rgba(0,0,0,0.15);
                }
                .hero-name {
                    font-family: $headingFont;
                    font-size: 2.6rem;
                    font-weight: 800;
                    margin-bottom: 8px;
                    color: var(--text-primary);
                    letter-spacing: -0.5px;
                }
                .hero-title {
                    font-size: 1.3rem;
                    color: var(--accent);
                    font-weight: 600;
                    margin-bottom: 24px;
                    text-transform: uppercase;
                    letter-spacing: 1px;
                }
                .hero-bio {
                    max-width: 650px;
                    margin: 0 auto;
                    color: var(--text-secondary);
                    font-size: 1.05rem;
                }
                
                /* Main Sections Layout */
                .main-section {
                    padding: 60px 0;
                }
                
                /* Skills Section */
                .skills-grid {
                    display: flex;
                    flex-wrap: wrap;
                    justify-content: center;
                    gap: 12px;
                    max-width: 800px;
                    margin: 0 auto;
                }
                .skill-badge {
                    padding: 8px 18px;
                    border-radius: 30px;
                    font-size: 0.95rem;
                    transition: all 0.2s ease;
                }
                
                /* Projects Section Grid */
                .projects-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
                    gap: 24px;
                }
                .project-card {
                    padding: 28px;
                    border-radius: var(--radius);
                    display: flex;
                    flex-direction: column;
                    height: 100%;
                }
                .project-title {
                    font-family: $headingFont;
                    font-size: 1.35rem;
                    font-weight: 700;
                    margin-bottom: 12px;
                    color: var(--text-primary);
                }
                .project-desc {
                    color: var(--text-secondary);
                    font-size: 0.95rem;
                    margin-bottom: 16px;
                    flex-grow: 1;
                }
                .project-tech-label {
                    font-size: 0.8rem;
                    color: var(--text-secondary);
                    text-transform: uppercase;
                    letter-spacing: 0.5px;
                    font-weight: bold;
                }
                .tag-pill {
                    font-size: 0.8rem;
                    padding: 4px 10px;
                    background: rgba(120, 120, 120, 0.1);
                    color: var(--text-secondary);
                    border-radius: 4px;
                    font-weight: 500;
                }
                .project-link {
                    display: inline-flex;
                    align-items: center;
                    font-size: 0.9rem;
                    font-weight: 600;
                    margin-top: auto;
                    color: var(--accent);
                }
                
                /* Contact Grid layout */
                .contact-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                    gap: 20px;
                    max-width: 900px;
                    margin: 0 auto 40px auto;
                }
                .contact-item {
                    border-radius: var(--radius);
                    border: 1.5px solid var(--card-border);
                    background: var(--card-bg);
                    padding: 20px;
                    display: flex;
                    align-items: center;
                    gap: 16px;
                }
                .contact-icon {
                    font-size: 1.6rem;
                    opacity: 0.9;
                }
                .contact-label {
                    font-size: 0.8rem;
                    text-transform: uppercase;
                    color: var(--text-secondary);
                    letter-spacing: 0.5px;
                    font-weight: bold;
                }
                .contact-value {
                    font-size: 0.95rem;
                    font-weight: 600;
                    color: var(--accent);
                }
                
                /* Footer details */
                .social-links {
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    gap: 20px;
                    margin-top: 30px;
                }
                .social-icon-btn {
                    display: inline-flex;
                    align-items: center;
                    gap: 8px;
                    padding: 8px 16px;
                    border: 1.5px solid var(--card-border);
                    border-radius: 30px;
                    color: var(--text-primary);
                    background: var(--card-bg);
                    font-size: 0.9rem;
                    font-weight: 500;
                    transition: all 0.2s ease;
                }
                .social-icon-btn:hover {
                    border-color: var(--accent);
                    color: var(--accent);
                    transform: translateY(-2px);
                }
                
                .footer {
                    text-align: center;
                    padding: 40px 20px;
                    color: var(--text-secondary);
                    font-size: 0.85rem;
                    border-top: 1.5px solid var(--card-border);
                }

                @media (max-width: 768px) {
                    .hero-name {
                        font-size: 2.1rem;
                    }
                    .hero-title {
                        font-size: 1.15rem;
                    }
                    .section-title {
                        font-size: 1.8rem;
                    }
                    .projects-grid {
                        grid-template-columns: 1fr;
                    }
                }
            </style>
        </head>
        <body>
            <div class="container" style="padding-top: 40px;">
                <!-- Hero Section -->
                <header class="hero">
                    <div class="avatar-placeholder">
                        ${profile.name.firstNotNullCharacter()}
                    </div>
                    <h1 class="hero-name">${profile.name}</h1>
                    <div class="hero-title">${profile.title}</div>
                    <p class="hero-bio">${profile.bio}</p>
                </header>

                <!-- Skills Section -->
                <section class="main-section" id="skills">
                    <h2 class="section-title">আমার <span>দক্ষতাসমূহ</span></h2>
                    <div class="skills-grid">
                        $skillsHtml
                    </div>
                </section>

                <!-- Projects Section -->
                <section class="main-section" id="projects">
                    <h2 class="section-title">আমার <span>প্রজেক্টসমূহ</span></h2>
                    <div class="projects-grid">
                        $projectsHtml
                    </div>
                </section>

                <!-- Contact Section -->
                <section class="main-section" id="contact" style="padding-bottom: 20px;">
                    <h2 class="section-title">আমার সাথে <span>যোগাযোগ করুন</span></h2>
                    <div class="contact-grid">
                        $emailHtml
                        $phoneHtml
                        $locationHtml
                    </div>
                    $socialSection
                </section>

                <!-- Footer Section -->
                <footer class="footer">
                    <p>© ${java.time.LocalDate.now().year} ${profile.name}. সর্বস্বত্ব সংরক্ষিত।</p>
                    <p style="margin-top: 5px; font-size: 0.75rem; opacity: 0.7;">Powered by Portfolio Maker Android App</p>
                </footer>
            </div>
        </body>
        </html>
        """.trimIndent()
    }

    private fun String.firstNotNullCharacter(): String {
        val trimmed = this.trim()
        if (trimmed.isEmpty()) return "M"
        return trimmed.substring(0, 1).uppercase()
    }
}
