import os
import docx
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml import parse_xml
from docx.oxml.ns import nsdecls

def generate_assignment3_document(filename="Assignment_3_Information_Architecture_and_Wireframing.docx"):
    doc = docx.Document()

    # Colors
    C_PRIMARY = RGBColor(27, 26, 23)      # #1B1A17 - Dark Charcoal
    C_SECONDARY = RGBColor(70, 85, 65)    # #465541 - Deep Sage
    C_ACCENT = RGBColor(160, 110, 40)     # #A06E28 - Warm Bronze
    C_BODY = RGBColor(40, 40, 40)         # Clean dark readable text

    # Margins: 1 inch
    for section in doc.sections:
        section.top_margin = Inches(1.0)
        section.bottom_margin = Inches(1.0)
        section.left_margin = Inches(1.0)
        section.right_margin = Inches(1.0)

    # Shading and borders helper
    def set_cell_background(cell, fill_hex):
        shd = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{fill_hex}"/>')
        cell._tc.get_or_add_tcPr().append(shd)

    def set_cell_padding(cell, top=70, bottom=70, left=90, right=90):
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

    # Headings
    def add_h1(text):
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(14)
        p.paragraph_format.space_after = Pt(4)
        p.paragraph_format.keep_with_next = True
        run = p.add_run(text)
        run.bold = True
        run.font.name = 'Arial'
        run.font.size = Pt(13)
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
        run.font.size = Pt(11)
        run.font.color.rgb = C_SECONDARY
        return p

    def add_body(text, space_after=4):
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(0)
        p.paragraph_format.space_after = Pt(space_after)
        p.paragraph_format.line_spacing = 1.15
        run = p.add_run(text)
        run.font.name = 'Calibri'
        run.font.size = Pt(10.5)
        run.font.color.rgb = C_BODY
        return p

    def add_bullet(bold_prefix, text):
        p = doc.add_paragraph(style='List Bullet')
        p.paragraph_format.space_before = Pt(1)
        p.paragraph_format.space_after = Pt(2.5)
        p.paragraph_format.line_spacing = 1.15
        r_bold = p.add_run(bold_prefix)
        r_bold.bold = True
        r_bold.font.name = 'Calibri'
        r_bold.font.size = Pt(10)
        r_bold.font.color.rgb = C_PRIMARY

        r_text = p.add_run(text)
        r_text.font.name = 'Calibri'
        r_text.font.size = Pt(10)
        r_text.font.color.rgb = C_BODY
        return p

    def create_table(headers, rows_data, widths=None):
        tbl = doc.add_table(rows=len(rows_data) + 1, cols=len(headers))
        tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
        tbl.autofit = False

        # Header Row
        hdr = tbl.rows[0].cells
        for i, h in enumerate(headers):
            hdr[i].text = h
            set_cell_background(hdr[i], "1B1A17")
            set_cell_padding(hdr[i], top=65, bottom=65, left=80, right=80)
            p = hdr[i].paragraphs[0]
            p.alignment = WD_ALIGN_PARAGRAPH.LEFT
            for r in p.runs:
                r.bold = True
                r.font.name = 'Arial'
                r.font.size = Pt(9)
                r.font.color.rgb = RGBColor(255, 255, 255)

        # Data Rows
        for r_i, row in enumerate(rows_data):
            cells = tbl.rows[r_i + 1].cells
            bg = "FAF7F2" if r_i % 2 == 1 else "FFFFFF"
            for c_i, val in enumerate(row):
                cells[c_i].text = str(val)
                set_cell_background(cells[c_i], bg)
                set_cell_padding(cells[c_i], top=55, bottom=55, left=80, right=80)
                p = cells[c_i].paragraphs[0]
                for r in p.runs:
                    r.font.name = 'Calibri'
                    r.font.size = Pt(9)
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

    def add_wireframe_box(ascii_art):
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(2)
        p.paragraph_format.space_after = Pt(4)
        p.paragraph_format.line_spacing = 1.0
        run = p.add_run(ascii_art)
        run.font.name = 'Consolas'
        run.font.size = Pt(8.5)
        run.font.color.rgb = C_PRIMARY

    # ==========================================
    # ACADEMIC HEADER
    # ==========================================
    p_meta = doc.add_paragraph()
    p_meta.paragraph_format.space_before = Pt(0)
    p_meta.paragraph_format.space_after = Pt(2)
    p_meta.alignment = WD_ALIGN_PARAGRAPH.RIGHT
    r_sub = p_meta.add_run("MCA DEGREE PROGRAM | UI/UX DESIGN")
    r_sub.font.name = 'Arial'
    r_sub.font.size = Pt(8.5)
    r_sub.bold = True
    r_sub.font.color.rgb = C_ACCENT

    p_title = doc.add_paragraph()
    p_title.paragraph_format.space_before = Pt(4)
    p_title.paragraph_format.space_after = Pt(2)
    r_t1 = p_title.add_run("ASSIGNMENT 3: INFORMATION ARCHITECTURE & WIREFRAMING\n")
    r_t1.bold = True
    r_t1.font.name = 'Arial'
    r_t1.font.size = Pt(15)
    r_t1.font.color.rgb = C_PRIMARY

    r_t2 = p_title.add_run("Sitemap, Navigation Structure, Card Sorting, User Flows & Low-Fidelity Wireframes for Élanor Skincare")
    r_t2.italic = True
    r_t2.font.name = 'Georgia'
    r_t2.font.size = Pt(10.5)
    r_t2.font.color.rgb = C_SECONDARY

    # Metadata Block
    info_headers = ["Field", "Submission Details"]
    info_rows = [
        ["Course", "Master of Computer Applications (MCA)"],
        ["Subject", "UI/UX Design"],
        ["Student Name", "Rasika Kudale"],
        ["Roll Number / PRN", "[Your Roll Number / PRN]"],
        ["Assignment Scope", "Sitemap, Navigation Structure, Card Sorting Results, 3 User Flows, and Low-Fidelity Wireframes for 7 Screens (Login, Registration, Dashboard, List, Details, Profile, Settings)"]
    ]
    create_table(info_headers, info_rows, widths=[2.0, 4.5])

    # ==========================================
    # 1. SITEMAP
    # ==========================================
    add_h1("1. Sitemap")
    add_body(
        "The sitemap outlines the hierarchical architecture of the Élanor web platform, organizing content logically from the global homepage down to product dispensary pages, diagnostic tools, and customer account settings."
    )

    sitemap_headers = ["Level 1 (Root)", "Level 2 (Primary Navigation)", "Level 3 (Sub-Pages & Views)"]
    sitemap_rows = [
        ["Homepage (`/`)", "1. Curated Shop (`/shop`)", "• Cellular Elixirs\n• Haute Botanique\n• Dermal Resurfacing\n• Filter by Concern (Aging, Hydration, Barrier)"],
        ["Homepage (`/`)", "2. AI Skin Concierge (`/ai-skin-concierge`)", "• 3D Facial Model Diagnostic\n• 4-Step Consultation Flow\n• Dermal Vitality Matrix Scores\n• 7-Day Bespoke Routine Result"],
        ["Homepage (`/`)", "3. Formulation Detail (`/product/[id]`)", "• Volume Selector (30ml / 50ml)\n• Botanical Actives Synergy Accordion\n• Clinical Efficacy Percentages\n• Verified Patron Reviews"],
        ["Homepage (`/`)", "4. Cart & Checkout (`/checkout`)", "• Slide-Out Bag Drawer (`CartDrawer.tsx`)\n• Coupon Engine (`HAUTE20`, `CONCIERGE15`)\n• Single-Page Accordion Checkout\n• Order Tracking (`/track`)"],
        ["Homepage (`/`)", "5. Customer Sanctuary (`/account`)", "• Login & Register Modal (`AuthModal.tsx`)\n• VIP Patron Dashboard\n• Profile & Saved Delivery Addresses\n• Account Settings & Preferences"]
    ]
    create_table(sitemap_headers, sitemap_rows, widths=[1.5, 2.3, 2.7])

    # ==========================================
    # 2. NAVIGATION STRUCTURE
    # ==========================================
    add_h1("2. Navigation Structure")
    add_body(
        "Élanor implements a dual-layer navigation model tailored to deliver seamless usability across both desktop and mobile viewports:"
    )

    nav_headers = ["Navigation Component", "Position & Viewport", "Key Elements & User Purpose"]
    nav_rows = [
        ["Primary Header Navbar", "Top fixed (Desktop & Tablet)", "• Left: Brand Monogram & Name ('Élanor')\n• Center: Shop, AI Concierge, Ingredients, About\n• Right: Search Trigger, Wishlist Heart, Bag Counter, Sanctuary Account"],
        ["Mobile Bottom Navigation (`MobileBottomNav.tsx`)", "Bottom fixed (Mobile <1024px)", "• Docked in the ergonomic 'Thumb Zone'\n• 4 Touch Targets: Home (Atelier), Shop (Dispensary), Concierge (AI Diagnostic), Sanctuary (Account)\n• Floating Bag Action with real-time item counter"],
        ["Breadcrumb Navigation", "Sub-header on internal pages", "• Example: Home > Dispensary > Cellular Elixirs > Crème Botanique\n• Enables immediate 1-click backtracking without browser back button"],
        ["Utility Footer Navigation", "Bottom of all pages", "• Columns: Atelier Collections, Clinical Science, Patron Services, Legal/Sustainability\n• High-Contrast mode toggle and live tracking shortcut"]
    ]
    create_table(nav_headers, nav_rows, widths=[1.8, 1.7, 3.0])

    # ==========================================
    # 3. CARD SORTING RESULTS
    # ==========================================
    add_h1("3. Card Sorting Results")
    add_body(
        "I conducted a hybrid card sorting exercise with 5 participants using 20 core e-commerce content cards to evaluate how users instinctively categorize skincare formulations, clinical diagnostics, and account features."
    )

    cs_headers = ["Final Category Name", "Assigned Content Cards", "Agreement %", "Key IA Decision Derived"]
    cs_rows = [
        ["Shop by Formulation", "Serums, Creams, Balms, Eye Nectar, Cleanser", "95%", "Keep direct product types easily accessible on the main shop filter."],
        ["Shop by Concern", "Cellular Aging, Dehydration, Barrier Repair, Radiance", "88%", "Provide dedicated concern filter tags so users can filter by skin issue."],
        ["Diagnostic Tools", "AI Skin Concierge, Facial Scan, Dermal Matrix", "92%", "Give the AI Skin Concierge its own prominent top-level navigation anchor."],
        ["Cart & Checkout", "Shopping Bag, Coupon Code, Free Shipping Bar, Payment", "100%", "Consolidate coupons and free shipping progress directly inside the Cart Drawer."],
        ["Patron Account", "VIP Status, Order History, Saved Addresses, Settings", "94%", "Group personal profile and delivery preferences under the unified Sanctuary modal."]
    ]
    create_table(cs_headers, cs_rows, widths=[1.6, 2.3, 0.9, 1.7])

    # ==========================================
    # 4. USER FLOWS
    # ==========================================
    add_h1("4. User Flows")
    add_body(
        "Below are three core user task flows illustrating the step-by-step logic, decision points, and screen transitions within the platform:"
    )

    add_h2("4.1 User Flow 1: AI Skin Consultation & Routine Purchase Flow")
    add_body(
        "Goal: A user completes the AI skin quiz and purchases the prescribed 7-day skincare ritual with a 15% discount."
    )
    uf1_headers = ["Step", "Action / Screen", "System Response / Next Screen", "Decision / Outcome"]
    uf1_rows = [
        ["1", "User clicks 'AI Concierge' in bottom/top nav.", "Navigates to `/ai-skin-concierge`; displays 3D model with scanning laser.", "User clicks 'Begin Consultation'."],
        ["2", "Step 1: Selects Skin Type (e.g. Combination).", "Records selection in state; advances stepper to Step 2.", "Side Cosmetologist monitor updates live."],
        ["3", "Step 2: Selects Concerns (e.g. Barrier Restoration).", "Highlights selection; advances stepper to Step 3.", "User chooses 1-3 priorities."],
        ["4", "Step 3 & 4: Selects Climate & Texture Preference.", "Submits responses to diagnostic engine.", "Displays animated 4-stage analysis sequence."],
        ["5", "Results Screen displays Dermal Matrix & AM/PM Ritual.", "User clicks 'Add Entire Ritual to Bag'.", "Cart opens with all items added + 15% concierge discount."]
    ]
    create_table(uf1_headers, uf1_rows, widths=[0.6, 2.1, 2.3, 1.5])

    add_h2("4.2 User Flow 2: Product Discovery & Single-Page Checkout Flow")
    add_body(
        "Goal: A user searches for a product, views formulation details, and completes checkout."
    )
    uf2_headers = ["Step", "Action / Screen", "System Response / Next Screen", "Decision / Outcome"]
    uf2_rows = [
        ["1", "User filters `/shop` by 'Cellular Aging'.", "Displays filtered formulation cards with prices and ratings.", "User selects 'Crème Botanique'."],
        ["2", "Opens Formulation Details (`/product/creme-botanique`).", "Renders volume selector (50ml) and INCI actives.", "User clicks 'Add to Bag'."],
        ["3", "Slide-Out Cart Drawer opens automatically.", "Shows $150 Free Shipping progress gauge.", "User taps coupon pill `HAUTE20` (20% off)."],
        ["4", "User clicks 'Proceed to Checkout'.", "Renders Single-Page Accordion Checkout (`/checkout`).", "Address, delivery, and payment on one screen."],
        ["5", "User selects Sandbox/COD and clicks 'Confirm Order'.", "Transitions order to CONFIRMED.", "Displays luxury order confirmation & tracking ID."]
    ]
    create_table(uf2_headers, uf2_rows, widths=[0.6, 2.1, 2.3, 1.5])

    add_h2("4.3 User Flow 3: User Authentication & Profile Settings Flow")
    add_body(
        "Goal: A patron logs into their account, edits a delivery address, and toggles preferences."
    )
    uf3_headers = ["Step", "Action / Screen", "System Response / Next Screen", "Decision / Outcome"]
    uf3_rows = [
        ["1", "User clicks 'Sanctuary' icon.", "Opens `AuthModal.tsx` popup.", "User selects 1-Click VIP Demo Login ('Genevieve')."],
        ["2", "System authenticates credentials.", "Updates navbar with VIP initials avatar.", "Displays Patron Dashboard with tier status."],
        ["3", "User navigates to 'Profile & Addresses'.", "Lists default shipping address and phone number.", "User updates apartment number and saves."],
        ["4", "User opens 'Settings'.", "Displays notification switches and high-contrast toggle.", "User enables high-contrast text mode."]
    ]
    create_table(uf3_headers, uf3_rows, widths=[0.6, 2.1, 2.3, 1.5])

    # ==========================================
    # 5. LOW-FIDELITY WIREFRAMES FOR 7 SCREENS
    # ==========================================
    doc.add_page_break()
    add_h1("5. Low-Fidelity Wireframes for Seven Screens")
    add_body(
        "The following section presents structured low-fidelity wireframe blueprints and component specifications for the seven requested screens: Login, Registration, Dashboard, List, Details, Profile, and Settings."
    )

    # ------------------------------------------
    # Screen 1: Login Screen
    # ------------------------------------------
    add_h2("5.1 Screen 1: Login Screen (AuthModal)")
    add_body("Purpose: Allows customers to sign into their account via Email/Password, Phone OTP, or 1-Click Demo Profiles.")
    add_wireframe_box(
"""+--------------------------------------------------------------+
| [X Close]                                                    |
|                                                              |
|                     M A I S O N  É L A N O R                 |
|                      Patron Sanctuary Access                 |
|                                                              |
|   [ Login Tab ]                   [ Register Tab ]           |
|  --------------------------------------------------          |
|                                                              |
|   Email Address                                              |
|   [ patron@example.com                                     ] |
|                                                              |
|   Password                                  [Forgot?]        |
|   [ ****************                                       ] |
|                                                              |
|   [================= ENTER SANCTUARY =====================]  |
|                                                              |
|   - - - - - - - - - - OR SIGN IN WITH OTP - - - - - - - - -  |
|                                                              |
|   [ Use 6-Digit Phone OTP Verification                     ] |
|                                                              |
|   - - - - - - - - - QUICK VIP DEMO ACCESS - - - - - - - - -  |
|                                                              |
|   [ Demo: Genevieve (Haute) ]     [ Demo: Claire (Prestige) ]|
+--------------------------------------------------------------+"""
    )
    w1_headers = ["UI Component", "Type", "Behavior & Data"]
    w1_rows = [
        ["Email & Password Inputs", "Text Input Fields", "Validates proper email format; hides password characters."],
        ["Enter Sanctuary Button", "Primary Action CTA", "Submits credentials, verifies JWT, and updates user state."],
        ["VIP Demo Profiles", "Secondary Fast-Buttons", "1-click pre-fills and logs in as sample patrons (`Genevieve` or `Claire`)."],
        ["Register Tab Switcher", "Tab Toggle", "Switches modal view to the Registration screen instantly."]
    ]
    create_table(w1_headers, w1_rows, widths=[1.8, 1.6, 3.1])

    # ------------------------------------------
    # Screen 2: Registration Screen
    # ------------------------------------------
    add_h2("5.2 Screen 2: Registration Screen (New Patron Join)")
    add_body("Purpose: Captures new user credentials and creates an authenticated patron profile.")
    add_wireframe_box(
"""+--------------------------------------------------------------+
| [X Close]                                                    |
|                                                              |
|                     M A I S O N  É L A N O R                 |
|                   Begin Your Cellular Journey                |
|                                                              |
|   [ Login Tab ]                   [ Register Tab (Active) ]  |
|  --------------------------------------------------          |
|                                                              |
|   Full Name                                                  |
|   [ e.g. Genevieve Moreau                                  ] |
|                                                              |
|   Email Address                                              |
|   [ e.g. genevieve@luxury.com                              ] |
|                                                              |
|   Create Security Passcode                                   |
|   [ ****************                                       ] |
|                                                              |
|   [X] I agree to the Élanor Privacy Charter & Clinical Terms |
|                                                              |
|   [================= CREATE PATRON ACCOUNT ===============]  |
|                                                              |
|   Already a Patron? [ Switch to Sign In ]                    |
+--------------------------------------------------------------+"""
    )
    w2_headers = ["UI Component", "Type", "Behavior & Data"]
    w2_rows = [
        ["Full Name Input", "Text Field", "Captures user's first and last name for personalized greetings."],
        ["Email & Password", "Text/Password", "Validates password strength (min 8 chars) and email uniqueness."],
        ["Terms Checkbox", "Checkbox", "Mandatory consent before account creation."],
        ["Create Account Button", "Primary Action CTA", "Registers user, logs them in automatically, and opens Dashboard."]
    ]
    create_table(w2_headers, w2_rows, widths=[1.8, 1.6, 3.1])

    # ------------------------------------------
    # Screen 3: Dashboard Screen
    # ------------------------------------------
    add_h2("5.3 Screen 3: Dashboard Screen (Patron Sanctuary)")
    add_body("Purpose: Central personalized hub showing membership tier, active bag preview, order shortcuts, and AI routine status.")
    add_wireframe_box(
"""+--------------------------------------------------------------+
| [Logo: Élanor]       Shop   Concierge   Search      [GM] Bag(2)|
+--------------------------------------------------------------+
|                                                              |
|  WELCOME BACK, GENEVIEVE                                      |
|  Tier: Haute Patron Member | Points: 1,450 | Privileges: Active|
|                                                              |
|  +------------------------+  +-----------------------------+ |
|  | ACTIVE BAG OVERVIEW    |  | MY DERMAL PRESCRIPTION      | |
|  | - Crème Botanique (50ml|  | Status: 7-Day AM/PM Active  | |
|  | - Sérum Éclat (30ml)   |  | Primary: Barrier Repair     | |
|  | Subtotal: $360.00      |  | Vitality Score: 88%         | |
|  | [ View Bag Drawer ]    |  | [ View Full AI Ritual ]     | |
|  +------------------------+  +-----------------------------+ |
|                                                              |
|  +---------------------------------------------------------+ |
|  | RECENT ORDERS & SHIPMENTS                               | |
|  | Order #ELN-2026-8921 | $360.00 | Status: In Transit      | |
|  | Estimated Delivery: Oct 28 | Carrier: BlueDart / SR     | |
|  | [ Track Sacred Dispatch ]                               | |
|  +---------------------------------------------------------+ |
|                                                              |
|  Account Shortcuts: [ Saved Addresses ] [ Settings ] [ Log Out]
+--------------------------------------------------------------+"""
    )
    w3_headers = ["UI Component", "Type", "Behavior & Data"]
    w3_rows = [
        ["Member Tier Badge", "Status Banner", "Displays patron tier (Haute/Prestige) and reward balance."],
        ["Active Bag Overview", "Card Widget", "Displays real-time items in bag with direct link to open drawer."],
        ["Dermal Prescription Card", "Widget", "Links directly to user's personalized AI routine results."],
        ["Recent Order Tracking", "Card Action", "Provides 1-click access to `/track` with active AWB shipment status."]
    ]
    create_table(w3_headers, w3_rows, widths=[1.8, 1.6, 3.1])

    # ------------------------------------------
    # Screen 4: List Screen (Curated Dispensary Shop)
    # ------------------------------------------
    add_h2("5.4 Screen 4: List Screen (Curated Shop Catalog - `/shop`)")
    add_body("Purpose: Browsing, searching, and multi-facet filtering of all skincare formulations.")
    add_wireframe_box(
"""+--------------------------------------------------------------+
| [Logo: Élanor]       Shop   Concierge   Search      [User] Bag |
+--------------------------------------------------------------+
|                                                              |
|                    T H E  D I S P E N S A R Y                |
|               Curated Formulations & Bio-Actives             |
|                                                              |
|   Category Filters: [ ALL ] [ ELIXIRS ] [ BOTANIQUE ] [ BALMS]
|   Concern Filters:  (o) All  ( ) Anti-Aging  ( ) Hydration   |
|   Sort By: [ Price: High to Low v ]     Price Range: [$50-$400]
|                                                              |
|   +-------------------+  +-------------------+  +----------+ |
|   | [Img Placeholder] |  | [Img Placeholder] |  | [Img]    | |
|   | Crème Botanique   |  | Sérum Éclat       |  | Baume    | |
|   | 50ml | 98% Firmness  50ml | 95% Radiance |  | Apaisant | |
|   | $195.00           |  | $165.00           |  | $145.00  | |
|   | [ + Add to Bag ]  |  | [ + Add to Bag ]  |  | [ + Add ]| |
|   +-------------------+  +-------------------+  +----------+ |
|                                                              |
|   Showing 8 of 8 Formulations                                |
+--------------------------------------------------------------+"""
    )
    w4_headers = ["UI Component", "Type", "Behavior & Data"]
    w4_rows = [
        ["Category Pills", "Filter Buttons", "Filters products by type ('Elixirs', 'Botanique', 'Balms')."],
        ["Concern Radio Filters", "Filter Controls", "Filters formulations by target skin concern."],
        ["Product Card Grid", "Card Container", "Displays product photo, French subtitle, clinical proof, and price."],
        ["+ Add to Bag Button", "Quick CTA", "Instantly adds product to bag and triggers `CartDrawer.tsx`."]
    ]
    create_table(w4_headers, w4_rows, widths=[1.8, 1.6, 3.1])

    # ------------------------------------------
    # Screen 5: Details Screen (Formulation Page)
    # ------------------------------------------
    add_h2("5.5 Screen 5: Details Screen (Product Page - `/product/[id]`)")
    add_body("Purpose: Deep-dive into a single formulation, volume sizes, clinical proof, and active INCI ingredients.")
    add_wireframe_box(
"""+--------------------------------------------------------------+
| [Logo: Élanor]       Shop   Concierge   Search      [User] Bag |
+--------------------------------------------------------------+
| Breadcrumbs: Home > Dispensary > Cellular Elixirs > Crème    |
|                                                              |
|  +------------------------+  CRÈME BOTANIQUE RESTRUCTURANTE   |
|  |                        |  Haute Cellular Firming Treatment|
|  |                        |                                  |
|  |   [ High-Resolution    |  $195.00                         |
|  |     Product Imagery    |                                  |
|  |     Placeholder ]      |  Volume Size:                    |
|  |                        |  [ (o) 50ml ]   [ ( ) 100ml ]    |
|  |                        |                                  |
|  +------------------------+  [======== ADD TO BAG ==========]|
|                                                              |
|  [v] Clinical Trial Results: 98% observed cellular firmness  |
|  [v] Botanical Active Synergy: Niacinamide 10% + Marine Col. |
|  [>] Full INCI Ingredients List                              |
|  [>] Sustainable Glass Packaging & Refill Guidelines         |
+--------------------------------------------------------------+"""
    )
    w5_headers = ["UI Component", "Type", "Behavior & Data"]
    w5_rows = [
        ["Volume Selector Pills", "Option Toggle", "Switches between 50ml ($195) and 100ml ($320) with live price update."],
        ["Add to Bag CTA", "Primary Button", "Adds selected size to cart and slides open the Cart Drawer."],
        ["Clinical Results Box", "Proof Banner", "Displays third-party clinical efficacy percentages."],
        ["Ingredient Accordion", "Expandable List", "Reveals exact botanical actives and cellular mechanisms of action."]
    ]
    create_table(w5_headers, w5_rows, widths=[1.8, 1.6, 3.1])

    # ------------------------------------------
    # Screen 6: Profile Screen (Customer Account)
    # ------------------------------------------
    add_h2("5.6 Screen 6: Profile Screen (Saved Addresses & Identity)")
    add_body("Purpose: View and manage customer personal details, saved delivery addresses, and VIP tier.")
    add_wireframe_box(
"""+--------------------------------------------------------------+
| [Logo: Élanor]       Shop   Concierge   Search      [User] Bag |
+--------------------------------------------------------------+
|                                                              |
|  PATRON PROFILE & ADDRESSES                                  |
|                                                              |
|  Personal Details:                                           |
|  Name:  Genevieve Moreau           Email: genevieve@luxury.com|
|  Phone: +1 (555) 019-2831          Member Since: Jan 2026    |
|  [ Edit Personal Info ]                                      |
|                                                              |
|  Saved Delivery Addresses:                                   |
|  +---------------------------------------------------------+ |
|  | [DEFAULT] PRIMARY RESIDENCE                             | |
|  | 742 Evergreen Terrace, Suite 400                        | |
|  | New York, NY 10021 | United States                      | |
|  | [ Edit Address ]   [ Remove ]                           | |
|  +---------------------------------------------------------+ |
|                                                              |
|  [ + Add New Delivery Address ]                              |
|                                                              |
|  Navigation: [ Back to Dashboard ]   [ Account Settings ]    |
+--------------------------------------------------------------+"""
    )
    w6_headers = ["UI Component", "Type", "Behavior & Data"]
    w6_rows = [
        ["Personal Info Block", "Data Summary", "Displays registered name, email, phone, and membership seniority."],
        ["Saved Address Card", "Address Container", "Shows primary delivery location with 'DEFAULT' badge."],
        ["+ Add New Address", "Action Modal Trigger", "Opens address entry form with city/state auto-complete."],
        ["Edit / Remove Triggers", "Management Buttons", "Allows updating apartment number or deleting old addresses."]
    ]
    create_table(w6_headers, w6_rows, widths=[1.8, 1.6, 3.1])

    # ------------------------------------------
    # Screen 7: Settings Screen (Preferences & Security)
    # ------------------------------------------
    add_h2("5.7 Screen 7: Settings Screen (Preferences & Security)")
    add_body("Purpose: Configure notification preferences, accessibility contrast mode, security passcode, and session logout.")
    add_wireframe_box(
"""+--------------------------------------------------------------+
| [Logo: Élanor]       Shop   Concierge   Search      [User] Bag |
+--------------------------------------------------------------+
|                                                              |
|  ACCOUNT SETTINGS & PREFERENCES                              |
|                                                              |
|  Accessibility & Display:                                    |
|  [X] High-Contrast Typography Mode (WCAG 2.1 AA Enhanced)    |
|  [ ] Reduce Motion & Animated Inertial Scrolling             |
|                                                              |
|  Notification Preferences:                                   |
|  [X] Email Notifications for Order Dispatch & Delivery Tracking
|  [X] Botanical Atelier Seasonal Private Drops & VIP Invites  |
|                                                              |
|  Security & Credentials:                                     |
|  Current Passcode: [ ****************                      ] |
|  New Passcode:     [ ****************                      ] |
|  [ Update Passcode ]                                         |
|                                                              |
|  Session Management:                                         |
|  [================== SECURE SIGN OUT ======================] |
+--------------------------------------------------------------+"""
    )
    w7_headers = ["UI Component", "Type", "Behavior & Data"]
    w7_rows = [
        ["High-Contrast Mode", "Toggle Switch", "Enforces darker text tokens and underlines for maximum accessibility."],
        ["Reduce Motion Toggle", "Toggle Switch", "Disables GSAP parallax animations for motion-sensitive users."],
        ["Passcode Update", "Password Form", "Enables updating user password with confirmation."],
        ["Secure Sign Out CTA", "Danger Button", "Clears authentication JWT token, resets session, and redirects home."]
    ]
    create_table(w7_headers, w7_rows, widths=[1.8, 1.6, 3.1])

    # Save Document
    doc.save(filename)
    os.makedirs("docs", exist_ok=True)
    doc.save(os.path.join("docs", filename))
    print(f"Assignment 3 document successfully written to: {filename} and docs/{filename}")

if __name__ == "__main__":
    generate_assignment3_document()
