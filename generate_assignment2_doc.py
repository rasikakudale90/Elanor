import os
import docx
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml import parse_xml
from docx.oxml.ns import nsdecls

def generate_assignment2_document(filename="Assignment_2_User_Research_and_Competitive_Analysis.docx"):
    doc = docx.Document()

    # Academic & Brand Styling Palette
    C_PRIMARY = RGBColor(27, 26, 23)      # #1B1A17 - Dark Charcoal / Obsidian
    C_SECONDARY = RGBColor(70, 85, 65)    # #465541 - Deep Sage / Olive
    C_ACCENT = RGBColor(160, 110, 40)     # #A06E28 - Warm Bronze / Gold
    C_BODY = RGBColor(40, 40, 40)         # Clean dark readable text
    C_MUTED = RGBColor(95, 90, 85)        # Muted labels

    # 1-inch margins
    for section in doc.sections:
        section.top_margin = Inches(1.0)
        section.bottom_margin = Inches(1.0)
        section.left_margin = Inches(1.0)
        section.right_margin = Inches(1.0)

    # Shading and borders helper
    def set_cell_background(cell, fill_hex):
        shd = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{fill_hex}"/>')
        cell._tc.get_or_add_tcPr().append(shd)

    def set_cell_padding(cell, top=80, bottom=80, left=110, right=110):
        tcPr = cell._tc.get_or_add_tcPr()
        tcMar = parse_xml(
            f'<w:tcMar {nsdecls("w")}>'
            f'<w:top w:w="{top}" w:type="dxa"/>'
            f'<w:bottom w:w="{bottom}" w:type="dxa"/>'
            f'<w:left w:w="{left}" w:type="dxa"/>'
            f'<w:right w:w="{right}" w:type="dxa"/>'
            f'</w:tcMar>'
        )
        tcPr.append(tcMar)

    # Callout Box
    def add_callout(text, title="KEY RESEARCH TAKEAWAY:"):
        tbl = doc.add_table(rows=1, cols=1)
        tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
        tbl.autofit = False
        c = tbl.cell(0, 0)
        c.width = Inches(6.5)
        set_cell_background(c, "FBF9F5")
        set_cell_padding(c, top=100, bottom=100, left=140, right=140)

        tcPr = c._tc.get_or_add_tcPr()
        borders = parse_xml(
            f'<w:tcBorders {nsdecls("w")}>'
            f'<w:top w:val="none"/>'
            f'<w:left w:val="single" w:sz="24" w:space="0" w:color="A06E28"/>'
            f'<w:bottom w:val="none"/>'
            f'<w:right w:val="none"/>'
            f'</w:tcBorders>'
        )
        tcPr.append(borders)

        p = c.paragraphs[0]
        p.paragraph_format.space_before = Pt(2)
        p.paragraph_format.space_after = Pt(2)
        p.paragraph_format.line_spacing = 1.15

        r_lbl = p.add_run(f"{title} ")
        r_lbl.bold = True
        r_lbl.font.name = 'Arial'
        r_lbl.font.size = Pt(10)
        r_lbl.font.color.rgb = C_ACCENT

        r_txt = p.add_run(text)
        r_txt.font.name = 'Calibri'
        r_txt.font.size = Pt(10.5)
        r_txt.font.color.rgb = C_PRIMARY
        doc.add_paragraph().paragraph_format.space_after = Pt(4)

    # Headings
    def add_h1(text):
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(14)
        p.paragraph_format.space_after = Pt(4)
        p.paragraph_format.keep_with_next = True
        run = p.add_run(text)
        run.bold = True
        run.font.name = 'Arial'
        run.font.size = Pt(14)
        run.font.color.rgb = C_PRIMARY
        return p

    def add_h2(text):
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(10)
        p.paragraph_format.space_after = Pt(3)
        p.paragraph_format.keep_with_next = True
        run = p.add_run(text)
        run.bold = True
        run.font.name = 'Arial'
        run.font.size = Pt(11.5)
        run.font.color.rgb = C_SECONDARY
        return p

    def add_body(text, space_after=5):
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(0)
        p.paragraph_format.space_after = Pt(space_after)
        p.paragraph_format.line_spacing = 1.15
        run = p.add_run(text)
        run.font.name = 'Calibri'
        run.font.size = Pt(11)
        run.font.color.rgb = C_BODY
        return p

    def add_bullet(bold_prefix, text):
        p = doc.add_paragraph(style='List Bullet')
        p.paragraph_format.space_before = Pt(1)
        p.paragraph_format.space_after = Pt(3)
        p.paragraph_format.line_spacing = 1.15
        r_bold = p.add_run(bold_prefix)
        r_bold.bold = True
        r_bold.font.name = 'Calibri'
        r_bold.font.size = Pt(10.5)
        r_bold.font.color.rgb = C_PRIMARY

        r_text = p.add_run(text)
        r_text.font.name = 'Calibri'
        r_text.font.size = Pt(10.5)
        r_text.font.color.rgb = C_BODY
        return p

    # Table helper
    def create_table(headers, rows_data, widths=None):
        tbl = doc.add_table(rows=len(rows_data) + 1, cols=len(headers))
        tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
        tbl.autofit = False

        # Header Row
        hdr = tbl.rows[0].cells
        for i, h in enumerate(headers):
            hdr[i].text = h
            set_cell_background(hdr[i], "1B1A17")
            set_cell_padding(hdr[i], top=70, bottom=70, left=90, right=90)
            p = hdr[i].paragraphs[0]
            p.alignment = WD_ALIGN_PARAGRAPH.LEFT
            for r in p.runs:
                r.bold = True
                r.font.name = 'Arial'
                r.font.size = Pt(9.5)
                r.font.color.rgb = RGBColor(255, 255, 255)

        # Body Rows
        for r_i, row in enumerate(rows_data):
            cells = tbl.rows[r_i + 1].cells
            bg = "FAF7F2" if r_i % 2 == 1 else "FFFFFF"
            for c_i, val in enumerate(row):
                cells[c_i].text = str(val)
                set_cell_background(cells[c_i], bg)
                set_cell_padding(cells[c_i], top=65, bottom=65, left=90, right=90)
                p = cells[c_i].paragraphs[0]
                for r in p.runs:
                    r.font.name = 'Calibri'
                    r.font.size = Pt(9.5)
                    r.font.color.rgb = C_BODY

        if widths:
            for row in tbl.rows:
                for c_i, w in enumerate(widths):
                    row.cells[c_i].width = Inches(w)

        tblPr = tbl._tbl.tblPr
        b = parse_xml(
            f'<w:tblBorders {nsdecls("w")}>'
            f'<w:top w:val="single" w:sz="4" w:space="0" w:color="D3C5B4"/>'
            f'<w:left w:val="none"/>'
            f'<w:bottom w:val="single" w:sz="4" w:space="0" w:color="D3C5B4"/>'
            f'<w:right w:val="none"/>'
            f'<w:insideH w:val="single" w:sz="4" w:space="0" w:color="EFE7DD"/>'
            f'<w:insideV w:val="none"/>'
            f'</w:tblBorders>'
        )
        tblPr.append(b)

        doc.add_paragraph().paragraph_format.space_after = Pt(4)
        return tbl

    # ==========================================
    # ACADEMIC COVER & METADATA
    # ==========================================
    p_meta = doc.add_paragraph()
    p_meta.paragraph_format.space_before = Pt(0)
    p_meta.paragraph_format.space_after = Pt(2)
    p_meta.alignment = WD_ALIGN_PARAGRAPH.RIGHT
    r_sub = p_meta.add_run("MCA DEGREE PROGRAM | UI/UX DESIGN & INTERACTION COURSEWORK")
    r_sub.font.name = 'Arial'
    r_sub.font.size = Pt(8.5)
    r_sub.bold = True
    r_sub.font.color.rgb = C_ACCENT

    p_title = doc.add_paragraph()
    p_title.paragraph_format.space_before = Pt(4)
    p_title.paragraph_format.space_after = Pt(2)
    r_t1 = p_title.add_run("ASSIGNMENT 2: USER RESEARCH & COMPETITIVE ANALYSIS\n")
    r_t1.bold = True
    r_t1.font.name = 'Arial'
    r_t1.font.size = Pt(16)
    r_t1.font.color.rgb = C_PRIMARY

    r_t2 = p_title.add_run("Qualitative User Interviews, Competitor Benchmarking, Empathy & Journey Mapping, and Agile Requirements for Élanor Skincare")
    r_t2.italic = True
    r_t2.font.name = 'Georgia'
    r_t2.font.size = Pt(11)
    r_t2.font.color.rgb = C_SECONDARY

    # Metadata Block
    info_headers = ["Project Parameter", "Details"]
    info_rows = [
        ["Course & Department", "Master of Computer Applications (MCA) — Department of Computer Science / Applications"],
        ["Subject", "User Interface & User Experience Design (UI/UX) / HCI"],
        ["Student Name", "Rasika Kudale"],
        ["Roll Number / PRN", "[Your Roll Number / PRN]"],
        ["Project Case Study", "Élanor — Luxury Skincare & Clinical Cellular Longevity Web Application"],
        ["Research Scope", "5-User Qualitative Testing, Competitive Audit (Aesop vs Kiehl's), Persona Modeling, HMW Framing, and Agile User Stories"],
        ["Submission Unit", "Assignment 2 Deliverable: User Research & Competitive Strategy Document"]
    ]
    create_table(info_headers, info_rows, widths=[2.3, 4.2])

    # ==========================================
    # 1. EXECUTIVE SUMMARY & RESEARCH OBJECTIVES
    # ==========================================
    add_h1("1. Research Objectives & Methodology")
    add_body(
        "Following the initial heuristic usability evaluation of Élanor in Assignment 1, Assignment 2 executes an in-depth User Research and Competitive Benchmarking study. While heuristic evaluations identify compliance with interface design heuristics, qualitative user research reveals the genuine emotional drivers, mental models, and frustrations of real people who purchase skincare online."
    )
    add_body(
        "The primary objectives of this study were: (1) to observe how five target users navigate luxury skincare e-commerce on mobile and desktop, (2) to benchmark Élanor against two industry leaders (Aesop and Kiehl's), and (3) to translate user insights into structured UX deliverables: User Personas, Empathy Maps, Customer Journey Maps, Problem Statements, 'How Might We' (HMW) frameworks, and Agile User Stories with formal Acceptance Criteria."
    )

    add_callout(
        "User research bridges the gap between assumptions and evidence. In luxury beauty commerce, user trust is determined not by decorative visuals alone, but by scientific ingredient clarity and zero-friction purchase flows.",
        title="USER RESEARCH PRINCIPLE:"
    )

    # ==========================================
    # 2. USER RESEARCH WITH FIVE PARTICIPANTS
    # ==========================================
    add_h1("2. Qualitative User Research with Five Participants")
    add_body(
        "I conducted qualitative, semi-structured interviews paired with task-based prototype usability walk-throughs with five diverse participants representing different ages, skincare routines, and digital shopping habits. Each session lasted approximately 30 minutes."
    )

    add_h2("2.1 Participant Demographics & Profiles")
    user_headers = ["ID", "Name & Age", "Occupation", "Skincare Habit & Frequency", "Digital Shopping Device"]
    user_rows = [
        ["U1", "Genevieve (34)", "Senior Brand Director", "Uses multi-step anti-aging serums; purchases monthly.", "Mobile Safari (iPhone 15 Pro) — 90% of shopping."],
        ["U2", "Claire (26)", "UI/UX Designer", "Skin-conscious; researches INCI ingredients and active percentages.", "Desktop Chrome (MacBook) & Mobile (Pixel 8)."],
        ["U3", "Ananya (29)", "Product Manager", "Sensitive combination skin; seeks quick, trustworthy recommendations.", "Mobile Chrome — shops during commutes and evenings."],
        ["U4", "Marcus (38)", "Architect", "Minimalist skincare routine (cleanser + moisturizer); values clean aesthetic.", "Tablet (iPad) and Mobile; abandons long forms."],
        ["U5", "Pooja (23)", "Graduate Student", "Budget-aware; looks for visible discount coupons and free delivery progress.", "Mobile Instagram browser & Safari; high social commerce use."]
    ]
    create_table(user_headers, user_rows, widths=[0.5, 1.4, 1.5, 1.8, 1.3])

    add_h2("2.2 Tasks Assigned to Participants During Usability Testing")
    add_bullet("Task 1 (Discovery): ", "Browse the homepage and locate a formulation designed for cellular firmness or barrier repair.")
    add_bullet("Task 2 (AI Consultation): ", "Open the AI Skin Concierge (`/ai-skin-concierge`), complete the 4-step diagnostic questionnaire, and review the prescribed Dermal Vitality Matrix scores.")
    add_bullet("Task 3 (Ingredient Verification): ", "Check the formulation details (`/product/[id]`) to locate the key botanical actives and clinical trial proof.")
    add_bullet("Task 4 (Bag & Discounts): ", "Add a formulation to the bag, view the slide-out cart drawer, and apply a promotional coupon code (`HAUTE20` or `CONCIERGE15`).")
    add_bullet("Task 5 (Checkout): ", "Navigate to `/checkout`, review shipping calculations, and complete the simulated order using the sandbox payment selector.")

    add_h2("2.3 Key Findings & Qualitative Feedback Synthesis")
    add_bullet("1. High Praise for the AI Skin Concierge: ", "4 out of 5 participants stated that the 3D model portrait and laser scanning animation felt significantly more innovative and engaging than standard static skincare quizzes. U2 commented: 'It feels like a high-tech dermal consultation, not just a marketing survey.'")
    add_bullet("2. Mobile Thumb-Zone Friction Confirmed: ", "Participants U1 and U3, who tested on mobile phones, instinctively tried to tap bottom icons to access the bag and search. They confirmed that having top-only headers forced awkward hand stretching.")
    add_bullet("3. Transparent Shipping & Coupon Demand: ", "U5 and U3 strongly emphasized that seeing the '$150 Free Shipping' progress bar inside the cart drawer made them want to add an extra formulation to avoid paying shipping charges.")
    add_bullet("4. Need for Plain-Language INCI Summaries: ", "While scientific names (e.g., 'Squalane', 'Palmitoyl Tripeptide-5') created a clinical impression, U3 and U4 requested clear one-line explanations of what each ingredient does in plain language.")

    # ==========================================
    # 3. COMPETITIVE ANALYSIS (AESOP VS KIEHL'S)
    # ==========================================
    add_h1("3. Competitive Analysis: Benchmarking Two Competing Platforms")
    add_body(
        "To establish Élanor's strategic positioning within the digital beauty sector, I conducted a thorough comparative audit against two leading brands with contrasting digital strategies:"
    )
    add_bullet("Competitor 1: Aesop (Maison Minimalist Luxury): ", "World-renowned for quiet luxury, poetic editorial narratives, and understated amber packaging. However, their web experience features complex multi-layered menus, no interactive skin diagnostics, and slow mobile checkout.")
    add_bullet("Competitor 2: Kiehl's Since 1851 (Heritage Apothecary Clinical): ", "Renowned for scientific apothecary heritage, strong ingredient transparency, and routine builders. However, their interface is heavily cluttered with aggressive promotional popups, high visual noise, and lack of refined luxury typography.")

    add_h2("3.1 Feature & UX Comparison Matrix")
    comp_headers = ["Evaluation Dimension", "Aesop (Minimalist Luxury)", "Kiehl's (Clinical Heritage)", "Élanor (Haute Botanique)"]
    comp_rows = [
        ["Brand Aesthetics", "Quiet luxury, earth-tone minimalism, architectural photography.", "Apothecary heritage, clinical badges, high visual density.", "Editorial quiet luxury, alabaster linen palette, Cormorant Garamond typography."],
        ["Navigation Structure", "Unconventional nested editorial drawers; high learning curve.", "Traditional multi-category mega menu; highly cluttered on mobile.", "Hybrid: Minimalist top header + Persistent Mobile Bottom Bar (`MobileBottomNav.tsx`)."],
        ["Skin Diagnostic Tool", "Static text recommendations; no interactive diagnostic scan.", "Standard multi-question skin assessment tool; basic product links.", "Interactive 3D-parallax model portrait, laser scan sweep, Dermal Vitality Matrix."],
        ["Ingredient Transparency", "Lists botanical notes, but lacks clinical concentration data.", "Prominent active percentages, but lacks holistic synergy mapping.", "INCI botanical actives synergy accordion + clinical efficacy percentages."],
        ["Cart & Feedback Flow", "Full-page redirect cart; no instant slide-out confirmation.", "Slide-out drawer with aggressive cross-selling popups.", "Slide-out Cart Drawer (`CartDrawer.tsx`) with Free Shipping progress & 1-click coupon pills."],
        ["Checkout Ergonomics", "Multi-step checkout with multiple page loads.", "3-step checkout with mandatory account creation prompts.", "Single-Page Accordion Checkout with 1-click VIP test profiles and Razorpay sandbox."],
        ["WCAG Accessibility", "Low contrast grey text on beige; suppressed focus outlines.", "Moderate contrast; heavy banner clutter causes screen-reader noise.", "Recalibrated #5E584F tokens (5.8:1 ratio); clean ARIA labeling."]
    ]
    create_table(comp_headers, comp_rows, widths=[1.5, 1.7, 1.7, 1.6])

    add_h2("3.2 Élanor's Strategic 'White Space' Advantage")
    add_body(
        "The competitive analysis highlights a clear market opportunity for Élanor. Aesop possesses superior luxury aesthetics but lacks interactive diagnostic intelligence. Kiehl's possesses clinical credibility but suffers from visual clutter and aggressive commercialism. Élanor captures the sweet spot: the ethereal quiet luxury of Aesop merged with the clinical diagnostic power of Kiehl's, delivered through a modern, frictionless Next.js 15 interface."
    )

    # ==========================================
    # 4. USER PERSONAS
    # ==========================================
    add_h1("4. User Personas")
    add_body(
        "Based on our user research findings and market analysis, two comprehensive personas were formulated to guide all interface design decisions:"
    )

    add_h2("4.1 Primary Persona: Genevieve Moreau (The Conscious Luxury Connoisseur)")
    add_bullet("Demographics: ", "34 years old, Senior Corporate Director, living in an urban metropolis. Annual income: $140,000+.")
    add_bullet("Psychographics: ", "Values cellular longevity, clean rare botanicals, and understated elegance. Believes skincare is a sacred daily self-care ritual.")
    add_bullet("Core Goals: ", "Wants anti-aging serums with proven clinical backing. Demands a clean mobile interface that allows purchasing in under 2 minutes during her busy schedule.")
    add_bullet("Pain Points: ", "Frustrated by cluttered websites with flashing discount popups. Dislikes long checkout forms that require re-entering address details.")
    add_bullet("Preferred Tech: ", "iPhone 15 Pro (Safari), Apple Pay, high-resolution screens. Expects instant page loads and zero layout shifts.")

    add_h2("4.2 Secondary Persona: Claire Delacroix (The Precision Skincare & Tech Enthusiast)")
    add_bullet("Demographics: ", "26 years old, Digital Product Designer & Skincare Blogger. Annual income: $85,000.")
    add_bullet("Psychographics: ", "Loves the intersection of technology and dermatological science. Carefully reads INCI ingredient lists before buying.")
    add_bullet("Core Goals: ", "Wants to take the AI Skin Concierge quiz to receive an objective dermal evaluation (Hydration, Barrier Integrity). Seeks tailored AM/PM routine architecture.")
    add_bullet("Pain Points: ", "Skeptical of marketing buzzwords ('miracle cure'). Frustrated when websites don't explain why specific ingredients are recommended together.")
    add_bullet("Preferred Tech: ", "MacBook Pro and Pixel 8. Regularly tests web micro-interactions, dark modes, and responsive design quality.")

    # ==========================================
    # 5. EMPATHY MAP
    # ==========================================
    doc.add_page_break()
    add_h1("5. Empathy Map")
    add_body(
        "To understand the user's mindset beyond superficial demographic data, an Empathy Map was constructed reflecting our target luxury skincare consumer:"
    )

    emp_headers = ["Empathy Quadrant", "User Mindset & Observed Realities"]
    emp_rows = [
        [
            "SAYS\n(Verbal Quotes & Explicit Statements)",
            "• 'I want to know exactly what active botanicals are in this serum before applying it to my face.'\n"
            "• 'If checkout takes more than 2 minutes on my phone, I will close the tab.'\n"
            "• 'I love personalized skincare, but most online quizzes just feel like generic marketing surveys.'\n"
            "• 'Free shipping thresholds should be obvious right inside the cart drawer.'"
        ],
        [
            "THINKS\n(Beliefs, Values & Unspoken Doubts)",
            "• 'Is this brand genuinely formulated with cellular science, or is it just pretty packaging?'\n"
            "• 'Will these ingredients clash with my sensitive skin barrier?'\n"
            "• 'I work hard; my skincare ritual should feel like a luxurious, calming sanctuary.'\n"
            "• 'I hope there are no hidden customs fees or delayed shipping times.'"
        ],
        [
            "DOES\n(Actions & Digital Behaviors)",
            "• Browses product galleries on mobile Safari during transit, evening relaxation, or coffee breaks.\n"
            "• Zooms in on ingredient lists and clinical percentage statistics.\n"
            "• Interacts with the AI Skin Concierge to see what routine it prescribes.\n"
            "• Looks for quick one-click discount codes and immediately checks shipping fees."
        ],
        [
            "FEELS\n(Emotions, Anxieties & Delights)",
            "• Anxious about accelerated skin aging, environmental pollution, and moisture loss.\n"
            "• Overwhelmed by massive 100-product catalogs with identical marketing claims.\n"
            "• Delighted when discovering an elegant, quiet-luxury website with smooth animations.\n"
            "• Reassured and confident when clinical trial percentages validate product claims."
        ]
    ]
    create_table(emp_headers, emp_rows, widths=[2.2, 4.3])

    # ==========================================
    # 6. CUSTOMER JOURNEY MAP
    # ==========================================
    add_h1("6. Customer Journey Map (End-to-End User Experience)")
    add_body(
        "The customer journey tracks Genevieve and Claire across the five critical stages of interaction with the Élanor platform:"
    )

    cjm_headers = ["Stage", "User Goal & Action", "Touchpoint", "User Feeling", "Identified Pain Point", "UX Design Opportunity"]
    cjm_rows = [
        [
            "1. Discovery",
            "Lands on site from referral; explores brand identity and signature formulations.",
            "Editorial Homepage (`/`), Hero Carousel, Lenis Scroll",
            "Interested & Impressed 😊",
            "Top navigation is hard to reach one-handed on mobile devices.",
            "Implement a persistent Mobile Bottom Navigation bar (`MobileBottomNav.tsx`)."
        ],
        [
            "2. Consultation",
            "Takes the AI Skin Concierge quiz to discover customized routine.",
            "AI Skin Concierge (`/ai-skin-concierge`), Laser Scan",
            "Engaged & Curious 😊",
            "Multi-question forms feel tedious if progress is invisible.",
            "Add a 4-step visual progress stepper with live Cosmetologist monitor."
        ],
        [
            "3. Evaluation",
            "Inspects 'Crème Botanique' details, volume sizes, and botanical ingredients.",
            "Product Detail Page (`/product/[id]`), INCI Accordion",
            "Analytical & Reassured 😐",
            "Tiny italic ingredient text was hard to read on phone screens.",
            "Darken text tokens to #5E584F and enlarge ingredient labels."
        ],
        [
            "4. Purchase",
            "Adds formulation to bag, enters shipping, and completes payment.",
            "Slide-Out Cart Drawer (`CartDrawer.tsx`), Checkout (`/checkout`)",
            "Focused & Decisive 😊",
            "Clicking 'Add to Bag' was silent; multi-page checkout causes abandonment.",
            "Auto-open Cart Drawer with $150 free shipping gauge and 1-click demo checkout."
        ],
        [
            "5. Post-Purchase",
            "Monitors order status and receives luxury botanical dispatch.",
            "Sacred Dispatch Tracker (`/track`), Email Confirmation",
            "Satisfied & Loyal 😊",
            "Lack of package milestones creates delivery anxiety.",
            "Provide interactive 5-stage milestone tracking timeline."
        ]
    ]
    create_table(cjm_headers, cjm_rows, widths=[0.9, 1.4, 1.2, 1.0, 1.0, 1.0])

    # ==========================================
    # 7. PROBLEM STATEMENT & HMW STATEMENTS
    # ==========================================
    add_h1("7. Problem Statement & 'How Might We' (HMW) Framework")

    add_h2("7.1 Formal User-Centered Problem Statement")
    add_body(
        "Modern skincare consumers who seek high-potency, clean botanical formulations struggle to find an e-commerce platform that combines artistic luxury prestige with transparent clinical diagnostics and effortless mobile usability. When websites prioritize high-fashion minimalism over functional ergonomics, users encounter unreadable low-contrast typography, cumbersome navigation, silent feedback, and tedious checkouts, leading to cart abandonment and degraded brand trust."
    )

    add_h2("7.2 'How Might We' (HMW) Statements")
    add_body(
        "To transform the problem statement into actionable design solutions, five core HMW questions were developed:"
    )
    add_bullet("1. HMW 1 (Diagnostic Engagement): ", "How might we make an online skin consultation feel as personal, engaging, and scientifically authoritative as visiting a luxury cosmetic dermatologist?")
    add_bullet("2. HMW 2 (Mobile Ergonomics): ", "How might we structure navigation so that mobile users holding large smartphones with one hand can effortlessly browse, search, and access their shopping bag without thumb strain?")
    add_bullet("3. HMW 3 (Ingredient Transparency): ", "How might we communicate complex cellular biochemistry (peptides, squalane, rare botanicals) so that both novice buyers and skincare experts feel informed and reassured?")
    add_bullet("4. HMW 4 (Instant Sensory Feedback): ", "How might we provide clear, immediate visual confirmation whenever an item is added to the cart, while gently incentivizing higher order values?")
    add_bullet("5. HMW 5 (Frictionless Commerce): ", "How might we compress the multi-step luxury checkout flow into a single, intuitive screen that completes in under 60 seconds without losing the feeling of high-end craftsmanship?")

    # ==========================================
    # 8. AGILE USER STORIES & ACCEPTANCE CRITERIA
    # ==========================================
    add_h1("8. Agile User Stories & Formal Acceptance Criteria")
    add_body(
        "To bridge UX research with front-end engineering, the research findings were translated into six structured Agile User Stories with formal, testable Acceptance Criteria:"
    )

    # Story 1
    add_h2("User Story 1: Interactive AI Skin Concierge Consultation")
    add_body("As a skin-conscious patron (Claire),\nI want to complete an interactive dermatological quiz that scans my skin priorities,\nSo that I can receive an objective, bespoke AM/PM skincare ritual tailored to my cellular needs.")
    add_bullet("Acceptance Criteria 1.1: ", "Given the user lands on `/ai-skin-concierge`, when they view the hero, then an interactive 3D model portrait with mouse parallax and an animated laser sweep must be visible.")
    add_bullet("Acceptance Criteria 1.2: ", "Given the user begins the consultation, when they progress through the 4 questions, then a visible stepper ('Step X of 4') and a live Cosmetologist Side Monitor must update in real time.")
    add_bullet("Acceptance Criteria 1.3: ", "Given the quiz is submitted, when analysis completes, then the system must display 4 animated Dermal Vitality Matrix score bars alongside tailored Morning and Evening product recommendations.")
    add_bullet("Acceptance Criteria 1.4: ", "Given the recommended ritual screen, when the user clicks 'Add Entire Ritual to Bag', then all prescribed products must be added with an automatic 15% concierge benefit applied.")

    # Story 2
    add_h2("User Story 2: Mobile Bottom Ergonomic Navigation Bar")
    add_body("As a busy mobile shopper (Genevieve),\nI want a persistent navigation bar docked at the bottom of my phone screen,\nSo that I can easily switch between Home, Shop, Concierge, and my Bag with one thumb.")
    add_bullet("Acceptance Criteria 2.1: ", "Given a viewport width under 1024px, when browsing any storefront route, then `MobileBottomNav.tsx` must remain fixed at the bottom with frosted glass blur.")
    add_bullet("Acceptance Criteria 2.2: ", "Given the mobile bottom bar, when the user taps 'Concierge', then they must be routed immediately to `/ai-skin-concierge`.")
    add_bullet("Acceptance Criteria 2.3: ", "Given items exist in the cart, when viewing the bottom bar, then a floating bag indicator must display the active item count badge.")

    # Story 3
    add_h2("User Story 3: Slide-Out Cart Drawer with Free Shipping Feedback")
    add_body("As a value-conscious luxury consumer (Pooja),\nI want immediate visual confirmation and a free-shipping progress indicator when I add a product,\nSo that I know my item was saved and understand how much more I need to spend for free shipping.")
    add_bullet("Acceptance Criteria 3.1: ", "Given a user clicks 'Add to Bag' on any product card, when the action registers, then `CartDrawer.tsx` must automatically slide open from the right edge.")
    add_bullet("Acceptance Criteria 3.2: ", "Given the cart drawer is open, when the subtotal is under $150, then a dynamic progress bar must display the exact dollar amount remaining to reach Free Shipping.")
    add_bullet("Acceptance Criteria 3.3: ", "Given available promotional codes (`HAUTE20`, `CONCIERGE15`), when the user clicks a coupon pill, then the discount must immediately deduct from the subtotal.")

    # Story 4
    add_h2("User Story 4: Single-Page Accordion Checkout")
    add_body("As a frequent patron (Genevieve),\nI want to review my shipping address, delivery speed, and payment options on a single accordion screen,\nSo that I can complete my purchase in under 60 seconds without multiple page reloads.")
    add_bullet("Acceptance Criteria 4.1: ", "Given the user navigates to `/checkout`, when the page loads, then shipping address, delivery options, and payment methods must be presented in an accordion flow.")
    add_bullet("Acceptance Criteria 4.2: ", "Given test mode is active, when selecting Razorpay sandbox, then 1-click test card buttons must auto-fill valid credentials (`4111 1111 1111 1111`).")
    add_bullet("Acceptance Criteria 4.3: ", "Given payment authorization succeeds, when the order settles, then the user must see an instant luxury confirmation screen with an order tracking reference.")

    # Story 5
    add_h2("User Story 5: Accessible High-Contrast Botanical INCI Transparency")
    add_body("As an analytical skincare shopper (Claire),\nI want clear, high-contrast descriptions of botanical ingredients and clinical trial results,\nSo that I can verify scientific safety and efficacy without squinting or eye fatigue.")
    add_bullet("Acceptance Criteria 5.1: ", "Given any product or ingredient page, when inspecting body text, then the contrast ratio against `#F8F3EB` must be at least 4.5:1 (WCAG 2.1 AA compliant).")
    add_bullet("Acceptance Criteria 5.2: ", "Given the formulation detail page, when clicking the 'Botanical Actives' accordion, then each active ingredient must display its common name, clinical percentage, and cellular function.")

    # Save Document
    doc.save(filename)
    os.makedirs("docs", exist_ok=True)
    doc.save(os.path.join("docs", filename))
    print(f"Assignment 2 successfully generated at: {filename} and docs/{filename}")

if __name__ == "__main__":
    generate_assignment2_document()
