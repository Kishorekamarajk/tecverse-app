-- ========================================================================================
-- TEC-VERSE 2026: AI CHATBOT & VOICE ASSISTANT POSTGRESQL DATABASE SCRIPT
-- ========================================================================================
-- Target Database: tecverse_db
-- Table: faq_items
-- Description: Contains questions, rich answers, voice navigation triggers, and categories
-- dynamically rendered across the AI Assistant Chatbot and Voice Copilot.
-- ========================================================================================

-- 1. Create table schema if not already created
CREATE TABLE IF NOT EXISTS faq_items (
    id BIGSERIAL PRIMARY KEY,
    page_key VARCHAR(40) NOT NULL DEFAULT 'home',
    question VARCHAR(300) NOT NULL,
    answer TEXT NOT NULL,
    display_order INT DEFAULT 1,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Create performance search index
CREATE INDEX IF NOT EXISTS idx_faq_items_page_key ON faq_items(page_key);
CREATE INDEX IF NOT EXISTS idx_faq_items_question ON faq_items(question);

-- 3. Clear existing default entries to prevent duplication (optional)
-- DELETE FROM faq_items;

-- 4. Populate Complete Knowledge Base & Voice Navigation Intents
INSERT INTO faq_items (page_key, question, answer, display_order) VALUES
-- GENERAL & EVENT OVERVIEW
('home', 'What is Tec-verse 2026?', 
 '🌟 **Tec-verse 2026** is India''s flagship Technology & Engineering Exhibition and Conference organized by the Consortium of Scientific Societies (CSC) under MeitY, Government of India, along with C-DAC, SAMEER, and C-MET.

📍 **Venue:** Chennai Trade Centre, Nandambakkam, Tamil Nadu
🗓️ **Dates:** 26–27 November 2026
🎯 **Key Focus:** Quantum Computing, Semiconductor VLSI, Generative AI, Robotics, 6G Wireless, and Advanced Materials.', 1),

('home', 'Is Tec-verse 2026 only for IT companies?', 
 '🌐 **No, Tec-verse 2026 is open to all engineering & deep-tech domains!**

It unites Electronics, Semiconductors, Hardware Design, Defense Technology, Deep Tech Startups, Academic Researchers, and Government Scientific Labs alongside Enterprise IT.', 2),

('home', 'Will there be networking opportunities?', 
 '🤝 **Yes, extensive B2B & Research Networking!**

• **B2B Matchmaking Lounges** in Hall 2 & 3
• **Executive Dinners & MoU Signing Pavilions**
• **Direct interaction with MeitY, C-DAC, SAMEER, & C-MET leadership**
• **Startup Investor Pitching Arenas**', 3),

('home', 'Why should I exhibit at Tec-verse 2026?', 
 '🏢 **Exhibitor Benefits:**

• Showcase innovations to **10,000+ delegates and international buyers**
• High-visibility standard (3x3m) and premium (6x6m) stalls across 8 Halls at Chennai Trade Centre
• Direct government procurement & industry partnership opportunities
• High-profile brand visibility in national media and conference proceedings.', 4),

-- LEADERSHIP RETREAT & SCIENTIFIC SESSIONS
('retreat', 'Who can attend the leadership retreat?', 
 '🏕️ **Leadership Retreat Eligibility:**

Senior scientists, academic chairs, industry executives, startup founders, and invited delegates can participate in the exclusive technical retreat, round-table policy discussions, and strategic workshops.', 5),

('retreat', 'How are speakers selected?', 
 '🎤 **Speaker Selection Process:**

Speakers are nominated and selected by the **National Technical Program Committee (TPC)** comprising eminent scientists from MeitY, C-DAC, IITs, and industry fellows based on research merit and breakthrough innovations.', 6),

('retreat', 'Can industry organizations participate?', 
 '🏭 **Yes, Industry Participation is Welcomed!**

Industry organizations can participate as **Sponsors, Exhibitors, Panelists, and Technology Partners** to demonstrate cutting-edge products and hire top engineering talent.', 7),

('retreat', 'How can researchers present projects?', 
 '🔬 **Research Project Presentations:**

Researchers can submit extended abstracts and technical papers through the official portal. Selected papers are published in conference proceedings and presented in the Plenary Track.', 8),

-- REGISTRATION, PASSES & VENUE AMENITIES
('ticket', 'Is there any registration fee for the event?', 
 '🎟️ **Registration & Fee Details:**

• **Standard Delegate:** Complimentary passes available for registered academic and scientific delegates through government sponsorship.
• **Corporate / Industry Pass:** Available with full access to exhibition halls, executive dining, and conference kits.', 9),

('ticket', 'Will food be provided at the event?', 
 '🍱 **Dining & Hospitality:**

**Yes!** Complimentary high-tea, snacks, and an executive buffet lunch are provided for all registered delegates at the **Lawn Pavilion behind Hall 3** on both Day 1 (26 Nov) and Day 2 (27 Nov).', 10),

('ticket', 'How can I register for the event?', 
 '📝 **How to Register:**

1. Register online at `tecverse2026.cdac.in` or via the in-app registration screen.
2. Complete your profile and select your technology interest domains.
3. Instantly receive your **Encrypted QR Digital Pass** in the app for fast-track entry.', 11),

('ticket', 'Will I receive a confirmation after registration?', 
 '📧 **Registration Confirmation:**

**Yes!** Immediately upon registration, you will receive an official confirmation email with your **Registration Reference Number** and your **Digital Pass QR badge** will activate in this app.', 12),

-- FLOOR PLAN & WAYFINDING
('floor_plan', 'Where is Chennai Trade Centre located?', 
 '📍 **Venue Location:**

**Chennai Trade Centre (CTC)**
Mount-Poonamallee High Road, Nandambakkam, Chennai, Tamil Nadu 600089.
Conveniently located 10 minutes from Chennai International Airport and Guindy Metro Station.', 13),

('floor_plan', 'Which hall has the Quantum Computing and AI exhibits?', 
 '🤖 **Quantum & AI Exhibits:**

• **Hall 1 & 2:** Quantum Computing, Supercomputing (PARAM Supercomputers by C-DAC) & High Performance Computing (HPC)
• **Hall 3 & 4:** Generative AI, Robotics & Autonomous Systems
• **Hall 5 & 6:** Semiconductor VLSI & Advanced Sensors
• **Hall 7 & 8:** 6G Wireless Communications & Defence Electronics.', 14),

('floor_plan', 'Where is the Help Desk and Registration Counter?', 
 'ℹ️ **Help Desk & Badge Printing:**

The Central Registration Desk, Fast-Track QR Scanner, and Information Booth are located at the **Grand Convention Foyer (Entry Gate 1 & 2)**.', 15),

-- SCHEDULE & TIMINGS
('schedule', 'What are the daily exhibition timings?', 
 '⏰ **Exhibition & Conference Timings:**

• **Day 1 (26 Nov 2026):** 09:00 AM – 06:30 PM (Inauguration at 10:00 AM)
• **Day 2 (27 Nov 2026):** 09:30 AM – 06:00 PM (Valedictory & Awards at 04:30 PM)', 16),

('schedule', 'When is the Keynote Address on Quantum Computing?', 
 '⚡ **Quantum Keynote:**

**Day 1 (26 Nov 2026) at 11:30 AM** in **Main Auditorium (Hall 1)**.
Delivered by chief scientists from MeitY and C-DAC on National Quantum Mission (NQM) milestones.', 17),

-- VOICE NAVIGATION SPECIFIC INTENTS
('navigation', 'Go to sessions', 
 '🚀 Opening **Thematic Sessions & Tracks** where you can explore Quantum, VLSI, AI, and Robotics research topics.', 18),

('navigation', 'Go to schedule', 
 '📅 Opening **Event Schedule & Agenda** for 26–27 November 2026.', 19),

('navigation', 'Go to digital pass', 
 '🎫 Opening your **Encrypted QR Digital Pass** for frictionless conference entry.', 20),

('navigation', 'Go to floor plan', 
 '🗺️ Opening **Chennai Trade Centre Interactive Floor Plan** with Halls 1 through 8.', 21),

('navigation', 'Go to profile', 
 '👤 Opening your **Delegate Profile** and Technology Interest Preferences.', 22);

-- 5. Verification Query
SELECT id, page_key, question, LEFT(answer, 60) AS answer_snippet, display_order 
FROM faq_items 
ORDER BY display_order ASC;
